
package com.taskmanager.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.model.Project;
import com.taskmanager.service.ProjectService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/projects")
public class ProjectServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProjectService projectService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        projectService = new ProjectService();
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
            List<Project> projects =
                projectService.getAllProjects();

            objectMapper.writeValue(
                response.getWriter(), projects
            );

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve projects");
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

            String name = json.path("name").asText("");
            String description =
                json.path("description").asText("");

            // ProjectService validates and normalizes the input.
            Project createdProject =
                projectService.createProject(name, description);

            response.setStatus(
                HttpServletResponse.SC_CREATED
            );

            objectMapper.writeValue(
                response.getWriter(), createdProject
            );

        } catch (JsonProcessingException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON request");

        } catch (IllegalArgumentException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage() == null
                    ? "Invalid project data"
                    : e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to create project");
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
                    "Project ID is required");
                return;
            }

            int id = Integer.parseInt(idValue);

            if (id <= 0) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Project ID must be positive");
                return;
            }

            JsonNode json =
                objectMapper.readTree(request.getInputStream());

            String name = json.path("name").asText("");
            String description =
                json.path("description").asText("");

            boolean updated =
                projectService.updateProject(
                    id, name, description
                );

            if (!updated) {
                sendError(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Project not found");
                return;
            }

            objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                    "message", "Project updated successfully",
                    "id", id,
                    "name", name.trim(),
                    "description", description.trim()
                )
            );

        } catch (NumberFormatException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid project ID");

        } catch (JsonProcessingException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON request");

        } catch (IllegalArgumentException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage() == null
                    ? "Invalid project data"
                    : e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update project");
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
                    "Project ID is required");
                return;
            }

            int id = Integer.parseInt(idValue);

            if (id <= 0) {
                sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Project ID must be positive");
                return;
            }

            boolean deleted = projectService.deleteProject(id);

            if (!deleted) {
                sendError(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Project not found");
                return;
            }

            objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                    "message", "Project deleted successfully",
                    "id", id
                )
            );

        } catch (NumberFormatException e) {
            sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid project ID");

        } catch (Exception e) {
            e.printStackTrace();

            if (!response.isCommitted()) {
                sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to delete project");
            }
        }
    }
}
