package org.lucane.applications.todolist.io;

import java.util.ArrayList;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistAction;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.client.Client;
import org.lucane.client.Communicator;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;
import org.lucane.common.ObjectConnection;

public class IO {
	private ConnectInfo service;
	private static IO singleInstance;

	private IO() {
		service = Communicator.getInstance().getConnectInfo("org.lucane.applications.todolist");
	}
	
	public static IO getInstance() {
		if (singleInstance==null) singleInstance = new IO();
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
				DialogBox.error("Can't add the new item");
				return null;
			}
			todolists = (ArrayList) oc.read();
			oc.close();
		} catch (Exception e) {
			DialogBox.error("Can't add the new item");
			return null;
		}

		return todolists;
	}

	public ArrayList getTodolistItems(String todolistName) {
		ArrayList todolistitems = null;
		try {
			TodolistAction action =	new TodolistAction(TodolistAction.GET_TODOLISTITEMS, new String[] {getUserName(), todolistName});
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error("Can't add the new item");
				return null;
			}
			todolistitems = (ArrayList) oc.read();
			oc.close();
		} catch (Exception e) {
			DialogBox.error("Can't add the new item");
			return null;
		}

		return todolistitems;
	}

	public boolean addTodolistItem(TodolistItem newTodolistItem) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.ADD_TODOLISTITEM, newTodolistItem);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error("Can't add the new item");
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error("Can't add the new item");
			return false;
		}

		return true;
	}
	
	public boolean modifyTodolistItem(String oldItemName/*, ...*/) {
		return false;
	}
	public boolean deleteTodolistItem(TodolistItem defunctTodolistItem) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.DEL_TODOLISTITEM, new String[] {defunctTodolistItem.getUserName(), defunctTodolistItem.getParentTodolistName(), defunctTodolistItem.getName()});
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error("Can't delete the item");
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error("Can't delete the item");
			return false;
		}

		return true;
	}

	public boolean addTodolist(Todolist newTodolist) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.ADD_TODOLIST, newTodolist);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error("Can't create the todolist");
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error("Can't create the todolist");
			return false;
		}

		return true;
	}
	public boolean modifyTodolist(String oldListName, Todolist newTodoList) {
		return false;
	}
	public boolean deleteTodolist(Todolist defunctTodolist) {
        try {
			TodolistAction action =	new TodolistAction(TodolistAction.DEL_TODOLIST, new String[] {defunctTodolist.getUserName(), defunctTodolist.getName()});
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if (ack.startsWith("FAILED")) {
				DialogBox.error("Can't delete the todolist");
				return false;
			}
			oc.close();
		} catch (Exception e) {
			DialogBox.error("Can't delete the todolist");
			return false;
		}

		return true;
	}
}
 