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

import java.util.Comparator;

public class TodolistItemsSorter implements Comparator {

	public static final int ASC = 1;
	public static final int DESC = -1;

	public static final int NAME = 0;
	public static final int PRIORITY = 1;
	public static final int COMPLETED = 2;
	
	private int sortBy=0;
	private int direction = 0;
	
	public TodolistItemsSorter(int sortBy, int direction) {
		this.sortBy = sortBy;
		this.direction = direction;
	}
	
	public int compare(Object o1, Object o2) {
		TodolistItem tli1 = (TodolistItem) o1;
		TodolistItem tli2 = (TodolistItem) o2;
		switch (sortBy) {
			case NAME :
				return direction*(tli1.getName().compareTo(tli2.getName()));
			case PRIORITY :
				return direction*(tli1.getPriority()-tli2.getPriority());
			case COMPLETED :
				return direction*((tli1.isComplete()?1:0)-(tli2.isComplete()?1:0));
		}
		return 0;
	}
}
