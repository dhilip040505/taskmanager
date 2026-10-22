
package com.taskmanager.util;

import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.model.Project;

public class ProjectDAOTest {

    public static void main(String[] args) {

        ProjectDAO dao = new ProjectDAO();

        try {
            Project project = new Project(
                    "Task Manager",
                    "A Java MVC project management system"
            );

            Project savedProject = dao.addProject(project);

            System.out.println(
                    "Project created with ID: "
                    + savedProject.getid()
            );

            System.out.println("\nAll projects:");

            for (Project p : dao.getAllProjects()) {
                System.out.println(
                        p.getid() + " | "
                        + p.getname() + " | "
                        + p.getdescription()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
