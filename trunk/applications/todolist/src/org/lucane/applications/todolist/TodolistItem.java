package org.lucane.applications.todolist;

import java.io.Serializable;
import java.util.Date;

public class TodolistItem implements Serializable {
	private String userName;
	private String parentTodolistName;
	private String name;
	private String description;
	private int priority;
	private boolean complete;
	private Date creationDate;
	private Date completionDate;

	public static final int PRIORITY_LOW = 0;
	public static final int PRIORITY_MEDIUM = 1; 
	public static final int PRIORITY_HIGH = 2;

	public TodolistItem(String userName, String parentTodolistName, String name, String description, int priority) {
		this(userName, parentTodolistName, name, description, priority, false);
	}

	public TodolistItem(String userName, String parentTodolistName, String name, String description, int priority, boolean complete) {
		this.userName = userName;
		this.parentTodolistName = parentTodolistName;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.creationDate = new Date();
		this.completionDate = null;
		this.complete = complete;
	}

	public String getUserName() {
		return userName;
	}
	public boolean isComplete() {
		return complete;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}
	public int getPriority() {
		return priority;
	}
	public String getParentTodolistName() {
		return parentTodolistName;
	}
	public Date getCompletionDate() {
		return completionDate;
	}
	public Date getCreationDate() {
		return creationDate;
	}

	public void setUserName(String string) {
		userName = string;
	}
	public void setComplete(boolean b) {
		if (b)
			this.completionDate = new Date();
		else
			this.completionDate = null;
		complete = b;
	}
	public void setDescription(String string) {
		description = string;
	}
	public void setName(String string) {
		name = string;
	}
	public void setPriority(int i) {
		priority = i;
	}
	public void setParentTodolistName(String string) {
		parentTodolistName = string;
	}
}
