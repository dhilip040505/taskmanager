package com.taskmanager.model;
import java.sql.Timestamp;
public class Project {
    private int id;
	private String name; 
	private String description;
	private Timestamp created_at;
	private int totalTasks;
	private int completedTasks;
	private int completionPercentage;

	public Project() {
		
	}
	public Project(String name,String description) {
		this.name=name;
		this.description=description;
	}
	public Project(int id,String name,String description,Timestamp created_at) {
		this.id=id;
		this.name=name;
		this.description=description;
		this.created_at=created_at;
	}
	public int getid() {
		return id;
	}
	public void setid(int id) {
		this.id=id;
	}
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name=name;
	}
	public String getdescription() {
		return description;
	}
	public void setdescription(String description) {
		this.description=description;
	}
	public Timestamp getcreated_at() {
		return created_at;
	}
	public void setcreated_at(Timestamp created_at) {
		this.created_at=created_at;
	}
	public int getTotalTasks() {
	    return totalTasks;
	}

	public void setTotalTasks(int totalTasks) {
	    this.totalTasks = totalTasks;
	}

	public int getCompletedTasks() {
	    return completedTasks;
	}

	public void setCompletedTasks(int completedTasks) {
	    this.completedTasks = completedTasks;
	}

	public int getCompletionPercentage() {
	    return completionPercentage;
	}

	public void setCompletionPercentage(int completionPercentage) {
	    this.completionPercentage = completionPercentage;
	}
}
