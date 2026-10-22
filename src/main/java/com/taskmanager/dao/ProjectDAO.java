
package com.taskmanager.dao;

import com.taskmanager.model.Project;
import com.taskmanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public Project addProject(Project project) throws SQLException {

        String sql =
            "INSERT INTO projects (name, description) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, project.getname());

                    if (project.getdescription() != null) {
                        ps.setString(2, project.getdescription());
                    } else {
                        ps.setNull(2, Types.VARCHAR);
                    }

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        throw new SQLException("Project insertion failed.");
                    }

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            project.setid(keys.getInt(1));
                        } else {
                            throw new SQLException(
                                "No generated project ID was returned."
                            );
                        }
                    }
                }

                String timestampSql =
                    "SELECT created_at FROM projects WHERE id = ?";

                try (PreparedStatement timestampPs =
                         con.prepareStatement(timestampSql)) {

                    timestampPs.setInt(1, project.getid());

                    try (ResultSet rs = timestampPs.executeQuery()) {
                        if (rs.next()) {
                            project.setcreated_at(
                                rs.getTimestamp("created_at")
                            );
                        } else {
                            throw new SQLException(
                                "Could not retrieve project creation timestamp."
                            );
                        }
                    }
                }

                con.commit();
                return project;

            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    public List<Project> getAllProjects() throws SQLException {

        List<Project> projects = new ArrayList<>();

        String sql =
            "SELECT p.id, p.name, p.description, p.created_at, " +
            "COUNT(t.id) AS total_tasks, " +
            "COALESCE(SUM(CASE WHEN t.status = 'COMPLETED' " +
            "THEN 1 ELSE 0 END), 0) AS completed_tasks, " +
            "COALESCE(ROUND(100.0 * " +
            "SUM(CASE WHEN t.status = 'COMPLETED' " +
            "THEN 1 ELSE 0 END) / NULLIF(COUNT(t.id), 0)), 0) " +
            "AS completion_percentage " +
            "FROM projects p " +
            "LEFT JOIN tasks t ON t.project_id = p.id " +
            "GROUP BY p.id, p.name, p.description, p.created_at " +
            "ORDER BY p.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Project project = new Project(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at")
                );

                project.setTotalTasks(rs.getInt("total_tasks"));
                project.setCompletedTasks(
                    rs.getInt("completed_tasks")
                );
                project.setCompletionPercentage(
                    rs.getInt("completion_percentage")
                );

                projects.add(project);
            }
        }

        return projects;
    }

    public boolean updateProject(Project project) throws SQLException {

        String sql =
            "UPDATE projects SET name = ?, description = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, project.getname());

            if (project.getdescription() != null) {
                ps.setString(2, project.getdescription());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setInt(3, project.getid());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(int id) throws SQLException {

        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
