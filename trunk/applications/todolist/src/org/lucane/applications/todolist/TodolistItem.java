/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Jonathan Riboux <jonathan.riboux@wanadoo.Fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.lucane.applications.todolist;

import java.io.Serializable;
import java.util.Date;

public class TodolistItem implements Serializable {
	private int id;
	private int parentTodolistId;
	private String name;
	private String description;
	private int priority;
	private boolean complete;
	private Date creationDate;
	private Date completionDate;

	public static final int PRIORITY_LOW = 0;
	public static final int PRIORITY_MEDIUM = 1; 
	public static final int PRIORITY_HIGH = 2;

	public TodolistItem(int parentTodolistId, String name, String description, int priority) {
		this(-1, parentTodolistId, name, description, priority, false);
	}

	public TodolistItem(int parentTodolistId, String name, String description, int priority, boolean complete) {
		this(-1, parentTodolistId, name, description, priority, complete);
	}

	public TodolistItem(int id, int parentTodolistId, String name, String description, int priority, boolean complete) {
		this.id = id;
		this.parentTodolistId = parentTodolistId;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.creationDate = new Date();
		this.completionDate = null;
		this.complete = complete;
	}

	public int getId() {
		return id;
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
	public int getParentTodolistId() {
		return parentTodolistId;
	}
	public Date getCompletionDate() {
		return completionDate;
	}
	public Date getCreationDate() {
		return creationDate;
	}

	public void setId(int i) {
		id = i;
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
	public void setParentTodolistId(int i) {
		parentTodolistId = i;
	}
	public String toString() {
		return getName();
	}
}
