
package com.taskmanager.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Task {

    private int id;
    private int project_id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Date due_date;
    private Timestamp created_at;

    public Task() {
    }

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public int getproject_id() {
        return project_id;
    }

    public void setproject_id(int project_id) {
        this.project_id = project_id;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getpriority() {
        return priority;
    }

    public void setpriority(String priority) {
        this.priority = priority;
    }

    public Date getdue_date() {
        return due_date;
    }

    public void setdue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Timestamp getcreated_at() {
        return created_at;
    }

    public void setcreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
