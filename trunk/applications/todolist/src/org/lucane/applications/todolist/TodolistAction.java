/*
 * Lucane - a collaborative platform Copyright (C) 2003 Vincent Fiack
 * <vfiack@mail15.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.lucane.applications.todolist;

import java.io.Serializable;

import org.lucane.common.signature.Signable;

public class TodolistAction implements Serializable, Signable {
	public static final int GET_TODOLISTS = 0;
	public static final int GET_TODOLISTITEMS = 2;

	public static final int ADD_TODOLIST = 4;
	public static final int MOD_TODOLIST = 5;
	public static final int DEL_TODOLIST = 6;

	public static final int ADD_TODOLISTITEM = 7;
	public static final int MOD_TODOLISTITEM = 8;
	public static final int DEL_TODOLISTITEM = 9;

	public int action;
	public Object param;

	public TodolistAction(int action, Object param) {
		this.action = action;
		this.param = param;
	}

	public TodolistAction(int action) {
		this(action, null);
	}

	public String toSignableString() {
		return "" + action;
	}

	public int getAction() {
		return action;
	}

	public Object getParam() {
		return param;
	}

}