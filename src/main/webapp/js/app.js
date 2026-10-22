const projectsContainer = document.getElementById("projects");
const projectMessage = document.getElementById("projectMessage");
const projectSection = document.getElementById("projectSection");
const taskSection = document.getElementById("taskSection");
const selectedProjectTitle = document.getElementById("selectedProjectTitle");
const taskMessage = document.getElementById("taskMessage");
const tasksContainer = document.getElementById("tasks");
const backButton = document.getElementById("backButton");
const taskForm = document.getElementById("taskForm");
const formMessage = document.getElementById("formMessage");


let currentProject = null;


async function readResponse(response) {
    const text = await response.text();
    let result = {};

    if (text.trim()) {
        try {
            result = JSON.parse(text);
        } catch {
            result = {
                error: text.startsWith("<")
                    ? `Server returned an unexpected response (HTTP ${response.status}).`
                    : text
            };
        }
    }

    if (!response.ok) {
        throw new Error(
            result.error || `Request failed: HTTP ${response.status}`
        );
    }

    return result;
}



async function loadProjects() {
    projectMessage.textContent = "Loading projects...";

    try {
        const response = await fetch("api/projects");
        const projects = await readResponse(response);

        projectsContainer.replaceChildren();

        if (!Array.isArray(projects) || projects.length === 0) {
            projectMessage.textContent = "No projects found.";
            return;
        }

        projectMessage.textContent = "";

        projects.forEach(project => {
            const card = document.createElement("article");
            card.className = "project-card";
            card.tabIndex = 0;

            const title = document.createElement("h3");
            title.textContent = project.name;

            const description = document.createElement("p");
            description.textContent =
                project.description || "No description provided.";


				const progress = document.createElement("p");
				progress.className = "project-progress";

				const totalTasks = Number(project.totalTasks ?? 0);
				const completedTasks = Number(project.completedTasks ?? 0);
				const completionPercentage = Number(
				    project.completionPercentage ?? 0
				);

				progress.textContent =
				    `${completedTasks}/${totalTasks} tasks completed ` +
				    `(${completionPercentage}%)`;


            const actions = document.createElement("div");
            actions.className = "project-actions";

            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.textContent = "Edit Project";
            editButton.className = "project-edit-button";

            const deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.textContent = "Delete Project";
            deleteButton.className = "project-delete-button";

            editButton.addEventListener("click", event => {
                event.stopPropagation();
                showProjectEditForm(project, card);
            });

            deleteButton.addEventListener("click", event => {
                event.stopPropagation();
                deleteProject(project);
            });

            actions.append(editButton, deleteButton);
            card.append(title, description, actions,progress);

            card.addEventListener("click", () => {
                loadTasks(project);
            });

            card.addEventListener("keydown", event => {
                if (
                    event.target === card &&
                    (event.key === "Enter" || event.key === " ")
                ) {
                    event.preventDefault();
                    loadTasks(project);
                }
            });

            projectsContainer.appendChild(card);
        });

    } catch (error) {
        projectMessage.textContent =
            `Unable to load projects: ${error.message}`;
        console.error("Project loading error:", error);
    }
}

async function showProjectEditForm(project, card) {
    const form = document.createElement("form");
    form.className = "inline-edit-form";

    const title = document.createElement("input");
    title.type = "text";
    title.value = project.name || "";
    title.placeholder = "Project name";
    title.required = true;
    title.maxLength = 150;

    const description = document.createElement("textarea");
    description.value = project.description || "";
    description.placeholder = "Project description";

    const saveButton = document.createElement("button");
    saveButton.type = "submit";
    saveButton.textContent = "Save Project";

    const cancelButton = document.createElement("button");
    cancelButton.type = "button";
    cancelButton.textContent = "Cancel";

    const message = document.createElement("p");

    form.append(title, description, saveButton, cancelButton, message);

    // Keep the original card content so Cancel restores it.
    const originalContent = card.cloneNode(true);
    card.replaceChildren(form);

    cancelButton.addEventListener("click", () => {
        card.replaceWith(originalContent);
        loadProjects();
    });

    form.addEventListener("submit", async event => {
        event.preventDefault();

        if (!title.value.trim()) {
            message.textContent = "Project name is required.";
            return;
        }

        saveButton.disabled = true;
        message.textContent = "Saving project...";

        try {
            const response = await fetch(
                `api/projects?id=${encodeURIComponent(project.id)}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        name: title.value.trim(),
                        description: description.value.trim()
                    })
                }
            );

            await readResponse(response);
            await loadProjects();
        } catch (error) {
            message.textContent = error.message;
            saveButton.disabled = false;
            console.error("Project update error:", error);
        }
    });
}

async function deleteProject(project) {
    const confirmed = window.confirm(
        `Delete project "${project.name}"? This cannot be undone.`
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(
            `api/projects?id=${encodeURIComponent(project.id)}`,
            {
                method: "DELETE"
            }
        );

        await readResponse(response);
        await loadProjects();
    } catch (error) {
        console.error("Project deletion error:", error);
        alert(`Could not delete project: ${error.message}`);
    }
}

async function loadTasks(project) {
    currentProject = project;

    projectSection.hidden = true;
    taskSection.hidden = false;
	taskForm.hidden = true;
	selectedProjectTitle.textContent = `${project.name} - Tasks`;
	    taskMessage.textContent = "Loading tasks...";
	    taskMessage.className = "";
	    tasksContainer.replaceChildren();

    try {
        const response = await fetch(
            `api/tasks?project_id=${encodeURIComponent(project.id)}`
        );

        const tasks = await readResponse(response);

        if (!Array.isArray(tasks) || tasks.length === 0) {
            taskMessage.textContent = "No tasks found for this project.";
            return;
        }

        taskMessage.textContent = "";

        tasks.forEach(task => {
            const card = document.createElement("article");
            card.className = "task-card";

            const title = document.createElement("h3");
            title.textContent = task.title || "Untitled task";

            const description = document.createElement("p");
            description.textContent =
                task.description || "No description provided.";

            const details = document.createElement("p");
            details.className = "task-meta";
            details.textContent =
                `Status: ${(task.status || "PENDING").replace(/_/g, " ")} | ` +
                `Priority: ${task.priority || "MEDIUM"}`;

            const dueDate = document.createElement("p");
            dueDate.className = "task-meta";
            dueDate.textContent = `Due date: ${formatDate(task.due_date)}`;

            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.textContent = "Edit";

            editButton.addEventListener("click", () => {
                showEditForm(card, task);
            });

            const deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.textContent = "Delete";

            deleteButton.addEventListener("click", async () => {
                const confirmed = window.confirm(
                    `Delete "${task.title}"? This cannot be undone.`
                );

                if (!confirmed) {
                    return;
                }

                deleteButton.disabled = true;
                taskMessage.textContent = "Deleting task...";
                taskMessage.className = "";

                try {
                    const response = await fetch(
                        `api/tasks?id=${encodeURIComponent(task.id)}`,
                        { method: "DELETE" }
                    );

                    await readResponse(response);
                    await loadTasks(currentProject);

                    taskMessage.textContent = "Task deleted successfully.";
                    taskMessage.className = "success";
                } catch (error) {
                    taskMessage.textContent = error.message;
                    taskMessage.className = "error";
                    deleteButton.disabled = false;
                }
            });

            card.append(
                title,
                description,
                details,
                dueDate,
                editButton,
                deleteButton
            );

            tasksContainer.appendChild(card);
        });
    } catch (error) {
        taskMessage.textContent =
            `Unable to load tasks: ${error.message}`;
        taskMessage.className = "error";
        console.error("Task loading error:", error);
    }
}

function formatDate(value) {
    if (!value) {
        return "Not set";
    }

    const rawDate = String(value);
    const match = rawDate.match(/^(\d{4})-(\d{2})-(\d{2})/);

    if (match) {
        return `${match[3]}/${match[2]}/${match[1]}`;
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return "Not set";
    }

    return date.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
    });
}

function dateInputValue(value) {
    if (!value) {
        return "";
    }

    const match = String(value).match(/^(\d{4}-\d{2}-\d{2})/);

    if (match) {
        return match[1];
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return (
        `${date.getUTCFullYear()}-` +
        `${String(date.getUTCMonth() + 1).padStart(2, "0")}-` +
        `${String(date.getUTCDate()).padStart(2, "0")}`
    );
}

function showEditForm(card, task) {
    const editForm = document.createElement("form");
    editForm.className = "inline-edit-form";

    const titleInput = document.createElement("input");
    titleInput.type = "text";
    titleInput.value = task.title || "";
    titleInput.maxLength = 150;
    titleInput.required = true;
    titleInput.setAttribute("aria-label", "Task title");

    const descriptionInput = document.createElement("textarea");
    descriptionInput.value = task.description || "";
    descriptionInput.placeholder = "Description";
    descriptionInput.setAttribute("aria-label", "Task description");

    const statusSelect = document.createElement("select");

    ["PENDING", "IN_PROGRESS", "COMPLETED"].forEach(status => {
        const option = document.createElement("option");
        option.value = status;
        option.textContent = status.replace(/_/g, " ");
        statusSelect.appendChild(option);
    });

    statusSelect.value = task.status || "PENDING";

    const prioritySelect = document.createElement("select");

    ["LOW", "MEDIUM", "HIGH"].forEach(priority => {
        const option = document.createElement("option");
        option.value = priority;
        option.textContent = priority;
        prioritySelect.appendChild(option);
    });

    prioritySelect.value = task.priority || "MEDIUM";

    const dateInput = document.createElement("input");
    dateInput.type = "date";
    dateInput.value = dateInputValue(task.due_date);
    dateInput.setAttribute("aria-label", "Due date");

    const saveButton = document.createElement("button");
    saveButton.type = "submit";
    saveButton.textContent = "Save changes";

    const cancelButton = document.createElement("button");
    cancelButton.type = "button";
    cancelButton.textContent = "Cancel";

    const editMessage = document.createElement("p");
    editMessage.className = "edit-message";
    editMessage.setAttribute("role", "status");

    editForm.append(
        titleInput,
        descriptionInput,
        statusSelect,
        prioritySelect,
        dateInput,
        saveButton,
        cancelButton,
        editMessage
    );

    card.replaceChildren(editForm);
    titleInput.focus();

    cancelButton.addEventListener("click", () => {
        loadTasks(currentProject);
    });

    editForm.addEventListener("submit", async event => {
        event.preventDefault();

        if (!titleInput.value.trim()) {
            editMessage.textContent = "Task title is required.";
            titleInput.focus();
            return;
        }

        saveButton.disabled = true;
        editMessage.textContent = "Saving changes...";

        const updatedData = {
            title: titleInput.value.trim(),
            description: descriptionInput.value.trim(),
            status: statusSelect.value,
            priority: prioritySelect.value,
            due_date: dateInput.value || null
        };

        try {
            await updateTask(task, updatedData);
            await loadTasks(currentProject);

            taskMessage.textContent = "Task updated successfully.";
            taskMessage.className = "success";
        } catch (error) {
            editMessage.textContent = error.message;
            saveButton.disabled = false;
        }
    });
}

async function updateTask(task, updatedData) {
    const response = await fetch(
        `api/tasks?id=${encodeURIComponent(task.id)}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedData)
        }
    );

    await readResponse(response);
}

backButton.addEventListener("click", () => {
    taskSection.hidden = true;
    projectSection.hidden = false;
    currentProject = null;
});

taskForm.addEventListener("submit", async event => {
    event.preventDefault();

    if (!currentProject) {
        formMessage.textContent = "Please select a project first.";
        formMessage.className = "error";
        return;
    }

    const taskData = {
        project_id: currentProject.id,
        title: document.getElementById("taskTitle").value.trim(),
        description: document.getElementById("taskDescription").value.trim(),
        status: "PENDING",
        priority: document.getElementById("taskPriority").value,
        due_date: document.getElementById("taskDueDate").value || null
    };

    if (!taskData.title) {
        formMessage.textContent = "Task title is required.";
        formMessage.className = "error";
        return;
    }

    createTaskButton.disabled = true;
    formMessage.textContent = "Creating task...";
    formMessage.className = "";

    try {
        const response = await fetch("api/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(taskData)
        });

        await readResponse(response);

        taskForm.reset();
        document.getElementById("taskPriority").value = "MEDIUM";

        formMessage.textContent = "Task created successfully!";
        formMessage.className = "success";

        await loadTasks(currentProject);
    } catch (error) {
        formMessage.textContent = error.message;
        formMessage.className = "error";
        console.error("Create task error:", error);
    } finally {
        createTaskButton.disabled = false;
    }
});


const addProjectButton = document.getElementById("addProjectButton");
const projectForm = document.getElementById("projectForm");
const projectNameInput = document.getElementById("projectName");
const projectDescriptionInput =
    document.getElementById("projectDescription");
const cancelProjectButton =
    document.getElementById("cancelProjectButton");
const projectFormMessage =
    document.getElementById("projectFormMessage");

addProjectButton.addEventListener("click", () => {
    projectForm.reset();
    projectFormMessage.textContent = "";
    projectFormMessage.className = "";
    projectForm.hidden = false;
    projectNameInput.focus();
});

cancelProjectButton.addEventListener("click", () => {
    projectForm.reset();
    projectForm.hidden = true;
    projectFormMessage.textContent = "";
});

projectForm.addEventListener("submit", async event => {
    event.preventDefault();

    const projectData = {
        name: projectNameInput.value.trim(),
        description: projectDescriptionInput.value.trim()
    };

    if (!projectData.name) {
        projectFormMessage.textContent =
            "Project name is required.";
        projectFormMessage.className = "error";
        return;
    }

    const saveButton = document.getElementById("saveProjectButton");
    saveButton.disabled = true;
    projectFormMessage.textContent = "Creating project...";

    try {
        const response = await fetch("api/projects", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(projectData)
        });

        await readResponse(response);

        projectForm.reset();
        projectForm.hidden = true;

        await loadProjects();

    } catch (error) {
        projectFormMessage.textContent = error.message;
        projectFormMessage.className = "error";
        console.error("Project creation error:", error);
    } finally {
        saveButton.disabled = false;
    }
});
const createTaskButton = document.getElementById("createTaskButton");

taskForm.hidden = true;


createTaskButton.addEventListener("click", () => {
    taskForm.reset();

    document.getElementById("taskPriority").value = "MEDIUM";

    formMessage.textContent = "";
    formMessage.className = "";

    taskForm.hidden = false;

    taskForm.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
});

const cancelTaskButton = document.getElementById("cancelTaskButton");

if (cancelTaskButton) {
    cancelTaskButton.addEventListener("click", () => {
        taskForm.reset();
        taskForm.hidden = true;
    });
}


loadProjects();
