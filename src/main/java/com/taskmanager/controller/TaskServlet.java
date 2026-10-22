
package com.taskmanager.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/tasks")
public class TaskServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private TaskService taskService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        taskService = new TaskService();
        objectMapper = new ObjectMapper();
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message) throws IOException {

        response.setStatus(status);
        objectMapper.writeValue(
            response.getWriter(),
            Map.of("error", message)
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String projectIdValue =
                request.getParameter("project_id");

            if (projectIdValue == null ||
                    projectIdValue.isBlank()) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Project ID is required");
                return;
            }

            int projectId = Integer.parseInt(projectIdValue);

            if (projectId <= 0) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Project ID must be positive");
                return;
            }

            List<Task> tasks =
                taskService.getTasksByProjectId(projectId);

            objectMapper.writeValue(
                response.getWriter(), tasks
            );

        } catch (NumberFormatException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Project ID must be a number");

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve tasks");
            }
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JsonNode json =
                objectMapper.readTree(request.getInputStream());

            int projectId =
                json.path("project_id").asInt(0);

            String title =
                json.path("title").asText("");

            String description =
                json.path("description").asText("");

            String status =
                json.path("status").asText("PENDING");

            String priority =
                json.path("priority").asText("MEDIUM");

            String dueDateValue =
                json.path("due_date").asText("").trim();

            Task task = new Task();

            task.setproject_id(projectId);
            task.settitle(title);
            task.setdescription(description);
            task.setstatus(status);
            task.setpriority(priority);

            if (!dueDateValue.isEmpty()) {
                task.setdue_date(Date.valueOf(dueDateValue));
            }

            // TaskService validates before calling TaskDAO.
            Task createdTask = taskService.createTask(task);

            response.setStatus(
                HttpServletResponse.SC_CREATED
            );

            objectMapper.writeValue(
                response.getWriter(), createdTask
            );

        } catch (JsonProcessingException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON request");

        } catch (IllegalArgumentException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage() == null
                    ? "Invalid task data"
                    : e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to create task");
            }
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String idValue = request.getParameter("id");

            if (idValue == null || idValue.isBlank()) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Task ID is required");
                return;
            }

            int id = Integer.parseInt(idValue);

            if (id <= 0) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Task ID must be positive");
                return;
            }

            JsonNode json =
                objectMapper.readTree(request.getInputStream());

            String title =
                json.path("title").asText("");

            String description =
                json.path("description").asText("");

            String status =
                json.path("status").asText("");

            String priority =
                json.path("priority").asText("");

            String dueDateValue =
                json.path("due_date").asText("").trim();

            Task task = new Task();

            task.setid(id);
            task.settitle(title);
            task.setdescription(description);
            task.setstatus(status);
            task.setpriority(priority);

            if (!dueDateValue.isEmpty()) {
                task.setdue_date(Date.valueOf(dueDateValue));
            }

            boolean updated = taskService.updateTask(task);

            if (!updated) {
                sendError(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Task not found");
                return;
            }

            objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                    "message", "Task updated successfully",
                    "id", id
                )
            );

        } catch (NumberFormatException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid task ID");

        } catch (JsonProcessingException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON request");

        } catch (IllegalArgumentException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage() == null
                    ? "Invalid task data"
                    : e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update task");
            }
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String idValue = request.getParameter("id");

            if (idValue == null || idValue.isBlank()) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Task ID is required");
                return;
            }

            int id = Integer.parseInt(idValue);

            if (id <= 0) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Task ID must be positive");
                return;
            }

            boolean deleted = taskService.deleteTask(id);

            if (!deleted) {
                sendError(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Task not found");
                return;
            }

            objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                    "message", "Task deleted successfully",
                    "id", id
                )
            );

        } catch (NumberFormatException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid task ID");

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to delete task");
            }
        }
    }
}
