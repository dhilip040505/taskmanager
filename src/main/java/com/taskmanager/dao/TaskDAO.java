
package com.taskmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.taskmanager.model.Task;
import com.taskmanager.util.DBConnection;

public class TaskDAO {

    public List<Task> getTasksByProjectId(int projectId)
            throws SQLException {

        List<Task> tasks = new ArrayList<>();

        String sql =
            "SELECT id, project_id, title, description, " +
            "status, priority, due_date, created_at " +
            "FROM tasks WHERE project_id = ? ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Task task = new Task();

                    task.setid(rs.getInt("id"));
                    task.setproject_id(rs.getInt("project_id"));
                    task.settitle(rs.getString("title"));
                    task.setdescription(rs.getString("description"));
                    task.setstatus(rs.getString("status"));
                    task.setpriority(rs.getString("priority"));
                    task.setdue_date(rs.getDate("due_date"));
                    task.setcreated_at(rs.getTimestamp("created_at"));

                    tasks.add(task);
                }
            }
        }

        return tasks;
    }

    public Task addTask(Task task) throws SQLException {

        String sql =
            "INSERT INTO tasks " +
            "(project_id, title, description, status, priority, due_date) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, task.getproject_id());
                    ps.setString(2, task.gettitle());
                    ps.setString(3, task.getdescription());
                    ps.setString(4, task.getstatus());
                    ps.setString(5, task.getpriority());

                    if (task.getdue_date() != null) {
                        ps.setDate(6, task.getdue_date());
                    } else {
                        ps.setNull(6, Types.DATE);
                    }

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        throw new SQLException("Task insertion failed.");
                    }

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            task.setid(keys.getInt(1));
                        } else {
                            throw new SQLException(
                                "No generated task ID was returned."
                            );
                        }
                    }
                }

                String timestampSql =
                    "SELECT created_at FROM tasks WHERE id = ?";

                try (PreparedStatement timestampPs =
                         con.prepareStatement(timestampSql)) {

                    timestampPs.setInt(1, task.getid());

                    try (ResultSet rs = timestampPs.executeQuery()) {
                        if (rs.next()) {
                            task.setcreated_at(
                                rs.getTimestamp("created_at")
                            );
                        } else {
                            throw new SQLException(
                                "Could not retrieve the task creation timestamp."
                            );
                        }
                    }
                }

                con.commit();
                return task;

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

    public boolean updateTask(Task task) throws SQLException {

        String sql =
            "UPDATE tasks SET title = ?, description = ?, " +
            "status = ?, priority = ?, due_date = ? " +
            "WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, task.gettitle());
            ps.setString(2, task.getdescription());
            ps.setString(3, task.getstatus());
            ps.setString(4, task.getpriority());

            if (task.getdue_date() != null) {
                ps.setDate(5, task.getdue_date());
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, task.getid());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTask(int id) throws SQLException {

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
