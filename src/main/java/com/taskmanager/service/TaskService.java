
package com.taskmanager.service;

import java.sql.SQLException;
import java.util.List;

import com.taskmanager.dao.TaskDAO;
import com.taskmanager.model.Task;

public class TaskService {

    // Allowed values for task status and priority
    private static final String[] VALID_STATUSES = {
        "PENDING", "IN_PROGRESS", "COMPLETED"
    };

    private static final String[] VALID_PRIORITIES = {
        "LOW", "MEDIUM", "HIGH"
    };

    // Object used to access the database through TaskDAO
    private final TaskDAO taskDAO;

    public TaskService() {
        this.taskDAO = new TaskDAO();
    }

    // Retrieve tasks belonging to one project
    public List<Task> getTasksByProjectId(int projectId)
            throws SQLException {

        if (projectId <= 0) {
            throw new IllegalArgumentException(
                "Project ID must be positive"
            );
        }

        return taskDAO.getTasksByProjectId(projectId);
    }

    // Validate and create a task
    public Task createTask(Task task) throws SQLException {
        validateTask(task, true);
        return taskDAO.addTask(task);
    }

    // Validate and update an existing task
    public boolean updateTask(Task task) throws SQLException {

        if (task == null || task.getid() <= 0) {
            throw new IllegalArgumentException(
                "Task ID must be positive"
            );
        }

        validateTask(task, false);
        return taskDAO.updateTask(task);
    }

    // Delete a task using its ID
    public boolean deleteTask(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Task ID must be positive"
            );
        }

        return taskDAO.deleteTask(id);
    }

    // Shared validation rules for creating and updating tasks
    private void validateTask(Task task, boolean creating) {

        if (task == null) {
            throw new IllegalArgumentException(
                "Task data is required"
            );
        }

        if (creating && task.getproject_id() <= 0) {
            throw new IllegalArgumentException(
                "Valid project_id is required"
            );
        }

        String title = task.gettitle();

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Task title is required"
            );
        }

        if (title.trim().length() > 150) {
            throw new IllegalArgumentException(
                "Task title must not exceed 150 characters"
            );
        }

        if (!contains(VALID_STATUSES, task.getstatus())) {
            throw new IllegalArgumentException(
                "Invalid task status"
            );
        }

        if (!contains(VALID_PRIORITIES, task.getpriority())) {
            throw new IllegalArgumentException(
                "Invalid task priority"
            );
        }

        // Clean the data before passing it to the DAO
        task.settitle(title.trim());

        task.setdescription(
            task.getdescription() == null
                ? ""
                : task.getdescription().trim()
        );
    }

    // Check whether a value is present in an allowed list
    private boolean contains(String[] values, String value) {

        if (value == null) {
            return false;
        }

        for (String candidate : values) {
            if (candidate.equals(value)) {
                return true;
            }
        }

        return false;
    }
}
