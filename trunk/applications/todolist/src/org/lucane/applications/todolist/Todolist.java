package org.lucane.applications.todolist;

import java.io.Serializable;

public class Todolist implements Serializable {
	private String userName;
	private String name;
	private String description;
	
	public Todolist(String userName, String name, String description) {
		this.userName = userName;
		this.name = name;
		this.description = description;
	}

	public String getUserName() {
		return userName;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}

	public void setUserName(String string) {
		userName = string;
	}
	public void setDescription(String string) {
		description = string;
	}
	public void setName(String string) {
		name = string;
	}
}
