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

package org.lucane.applications.todolist.io;

import java.util.ArrayList;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistAction;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.client.Client;
import org.lucane.client.Communicator;
import org.lucane.client.Plugin;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;
import org.lucane.common.ObjectConnection;

public class IO {
	private ConnectInfo service;
	private static IO singleInstance;
	private static Plugin plugin;

	private IO() {
		service = Communicator.getInstance().getConnectInfo("org.lucane.applications.todolist");
	}
	
	public static IO getInstance(Plugin plugin) {
		// always use the newest plugin instance to prevent an old one to stay in memory
		IO.plugin = plugin;
		
		if (singleInstance==null) {
			singleInstance = new IO();
		}
		
		return singleInstance;
	}
	
	public ConnectInfo getService() {
		return service;
	}
	
	public String getUserName() {
		return Client.getInstance().getMyInfos().getName();
	}
	
	public ArrayList getTodolists() {
		ArrayList todolists = null;
		try {
			TodolistAction action =	new TodolistAction(TodolistAction.GET_TODOLISTS, getUserName());
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantGetLists"));
				return null;
			}
			todolists = (ArrayList) oc.read();
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantGetLists"));
			return null;
		}

		return todolists;
	}

	public ArrayList getTodolistItems(int todolistId) {
		ArrayList todolistitems = null;
		try {
			TodolistAction action =	new TodolistAction(TodolistAction.GET_TODOLISTITEMS, new Integer(todolistId));
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantGetItems"));
				return null;
			}
			todolistitems = (ArrayList) oc.read();
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantGetItems"));
			return null;
		}

		return todolistitems;
	}

	public int addTodolistItem(TodolistItem newTodolistItem) {
		int id=-1;
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.ADD_TODOLISTITEM, newTodolistItem);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantCreateItem"));
				return -1;
			}
			id = ((Integer)oc.read()).intValue();
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantCreateItem"));
			return -1;
		}

		return id;
	}
	
	public boolean modifyTodolistItem(TodolistItem oldTodolistItem, TodolistItem newTodolistItem) {
        try {
        	TodolistAction action =	new TodolistAction(TodolistAction.MOD_TODOLISTITEM, new Object[] {new Integer(oldTodolistItem.getId()), newTodolistItem});
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantModifyItem"));
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantModifyItem"));
			return false;
		}

		return true;
	}

	public boolean deleteTodolistItem(TodolistItem defunctTodolistItem) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.DEL_TODOLISTITEM, new Integer(defunctTodolistItem.getId()));
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantDeleteItem"));
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantDeleteItem"));
			return false;
		}

		return true;
	}

	public int addTodolist(Todolist newTodolist) {
		int id=-1;
		try {
			TodolistAction action =	new TodolistAction(TodolistAction.ADD_TODOLIST, newTodolist);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantCreateList"));
				return -1;
			}
			id = ((Integer)oc.read()).intValue();

			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantCreateList"));
			return -1;
		}

		return id;
	}

	public boolean modifyTodolist(Todolist oldTodolist, Todolist newTodolist) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.MOD_TODOLIST, new Object[] {new Integer(oldTodolist.getId()), newTodolist});
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantModifyList"));
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantModifyList"));
			return false;
		}

		return true;
	}
	
	public boolean deleteTodolist(Todolist defunctTodolist) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.DEL_TODOLIST, new Integer(defunctTodolist.getId()));
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error(plugin.tr("IO.cantDeleteList"));
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error(plugin.tr("IO.cantDeleteList"));
			return false;
		}

		return true;
	}
}
 