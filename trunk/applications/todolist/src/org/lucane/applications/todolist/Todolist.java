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

public class Todolist implements Serializable {
	private int id;
	private String userName;
	private String name;
	private String description;
	
	public Todolist(String userName, String name, String description) {
		this(-1, userName, name, description);
	}
	public Todolist(int id, String userName, String name, String description) {
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
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

	public void setId(int i) {
		id = i;
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
	public String toString() {
		return getName();
	}
}
