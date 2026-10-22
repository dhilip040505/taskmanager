
package com.taskmanager.service;

import java.sql.SQLException;
import java.util.List;

import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.model.Project;

public class ProjectService {

    private final ProjectDAO projectDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
    }

    public List<Project> getAllProjects() throws SQLException {
        return projectDAO.getAllProjects();
    }

    public Project createProject(String name, String description)
            throws SQLException {

        validateProject(name);

        Project project = new Project(
            name.trim(),
            normalizeDescription(description)
        );

        return projectDAO.addProject(project);
    }

    public boolean updateProject(
            int id, String name, String description)
            throws SQLException {

        validateProject(name);

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Project ID must be positive"
            );
        }

        Project project = new Project(
            name.trim(),
            normalizeDescription(description)
        );

        project.setid(id);

        return projectDAO.updateProject(project);
    }

    public boolean deleteProject(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Project ID must be positive"
            );
        }

        return projectDAO.deleteProject(id);
    }

    private void validateProject(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Project name is required"
            );
        }

        if (name.trim().length() > 150) {
            throw new IllegalArgumentException(
                "Project name must not exceed 150 characters"
            );
        }
    }

    private String normalizeDescription(String description) {
        return description == null ? "" : description.trim();
    }
}
