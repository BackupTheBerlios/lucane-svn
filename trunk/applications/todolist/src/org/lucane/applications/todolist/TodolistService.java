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

import java.sql.PreparedStatement;
import java.util.*;

import org.lucane.common.*;
import org.lucane.common.concepts.*;
import org.lucane.server.*;
import org.lucane.server.database.DatabaseAbstractionLayer;
import org.lucane.server.store.*;
import java.sql.*;

public class TodolistService extends Service {
	private Store store;
    
    private DatabaseAbstractionLayer layer;
    private Connection conn;

    private PreparedStatement st;
    private ResultSet res;

	public TodolistService() {
	}

	public void install() {
		try {
			String dbDescription = getDirectory() + "db-todolist.xml";
			layer.getTableCreator().createFromXml(dbDescription);
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to install TodolistService !");
			e.printStackTrace();
		}
	}

	public void init(Server parent) {
		try {
			layer = parent.getDBLayer();
			conn = layer.openConnection();
		} catch (SQLException e) {
			Logging.getLogger().warning("unable to open connection: " + e);
		}

    	try {
			this.store = parent.getStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void process(ObjectConnection oc, Message message) {
		TodolistAction tla = (TodolistAction) message.getData();
		try {
            switch (tla.action) {
            	case TodolistAction.GET_TODOLISTS :
            		getTodolists(oc, (String)tla.getParam());
                    break;
            	case TodolistAction.GET_TODOLISTITEMS :
	        		{
	        			String[] tmpstr = (String[])tla.getParam();
	        			getTodolistItems(oc, tmpstr[0], tmpstr[1]);
	        		}
	                break;
            	case TodolistAction.ADD_TODOLIST :
            		addTodolist(oc, (Todolist)tla.getParam());
                    break;
            	case TodolistAction.MOD_TODOLIST :
                    break;
            	case TodolistAction.DEL_TODOLIST :
	            	{
	            		String[] tmpstr = (String[])tla.getParam();
	            		deleteTodolist(oc, tmpstr[0], tmpstr[1]);
	            	}
                    break;
            	case TodolistAction.ADD_TODOLISTITEM :
            		addTodolistItem(oc, (TodolistItem)tla.getParam());
                    break;
            	case TodolistAction.MOD_TODOLISTITEM :
	        		{
	        			Object[] tmpobj = (Object[])tla.getParam();
	        			modifyTodolistItem(oc, (String)tmpobj[0], (String)tmpobj[1], (String)tmpobj[2], (TodolistItem)tmpobj[3]);
	        		}
                    break;
            	case TodolistAction.DEL_TODOLISTITEM :
	            	{
	            		String[] tmpstr = (String[])tla.getParam();
	            		deleteTodolistItem(oc, tmpstr[0], tmpstr[1], tmpstr[2]);
	            	}
                    break;
			}
		} catch (Exception e) {
			try {
				oc.write("FAILED " + e);
			} catch (Exception e2) {
			}

			e.printStackTrace();
		}
	}

	private void getTodolists(ObjectConnection oc, String userName) {
		try {
			st = conn.prepareStatement(
					"SELECT user_name, name, description FROM todolists WHERE user_name=?");
			st.setString(1, userName);
			res = st.executeQuery();
			
			ArrayList todolists = new ArrayList(); 
			while (res.next()) {
				todolists.add(new Todolist(res.getString(1), res.getString(2), res.getString(3)));
			}

			oc.write("OK");
			oc.write(todolists);
			
			res.close();
			st.close();
		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::getTodolists : " + e);
		}
	}
	
	private void getTodolistItems(ObjectConnection oc, String userName, String todolistName) {
		try {
			st = conn.prepareStatement(
					"SELECT user_name, list_name, name, description, priority, complete FROM todolistitems WHERE user_name=? AND list_name=?");
			st.setString(1, userName);
			st.setString(2, todolistName);
			res = st.executeQuery();
			
			ArrayList todolistitems = new ArrayList(); 
			while (res.next()) {
				todolistitems.add(new TodolistItem(res.getString(1), res.getString(2), res.getString(3), res.getString(4), res.getInt(5), res.getInt(6)==1));
			}

			oc.write("OK");
			oc.write(todolistitems);
			
			res.close();
			st.close();
		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::getTodolistItems : " + e);
		}
	}
	
	private void addTodolist(ObjectConnection oc, Todolist newTodolist) {
		try {
			st = conn.prepareStatement(
					"INSERT INTO todolists (user_name, name, description) VALUES (?, ?, ?)");
			st.setString(1, newTodolist.getUserName());
			st.setString(2, newTodolist.getName());
			st.setString(3, newTodolist.getDescription());
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::addTodolist : " + e);
		}
	}

	private void deleteTodolist(ObjectConnection oc, String userName, String name) {
		try {
			st = conn.prepareStatement(
					"DELETE FROM todolists WHERE user_name=? AND name=?");
			st.setString(1, userName);
			st.setString(2, name);
			st.executeUpdate();
			st.close();

			st = conn.prepareStatement(
					"DELETE FROM todolistitems WHERE user_name=? AND list_name=?");
			st.setString(1, userName);
			st.setString(2, name);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::deleteTodolist : " + e);
		}
	}

	private void addTodolistItem(ObjectConnection oc, TodolistItem newTodolistItem) {
		// TODO test if the list exists before adding an item
		try {
			st = conn.prepareStatement(
					"INSERT INTO todolistitems (user_name, name, description, list_name, priority, complete) VALUES (?, ?, ?, ?, ?, ?)");
			st.setString(1, newTodolistItem.getUserName());
			st.setString(2, newTodolistItem.getName());
			st.setString(3, newTodolistItem.getDescription());
			st.setString(4, newTodolistItem.getParentTodolistName());
			st.setInt(5, newTodolistItem.getPriority());
			st.setInt(6, newTodolistItem.isComplete()?1:0);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::addTodolistItem : " + e);
		}
	}

	private void modifyTodolistItem(ObjectConnection oc, String oldTodolistItemUser, String oldTodolistItemListName, String oldTodolistItemName, TodolistItem newTodolistItem) {
		try {
			st = conn.prepareStatement(
					"UPDATE todolistitems SET user_name=?, name=?, description=?, list_name=?, priority=?, complete=? WHERE user_name=? AND list_name=? AND name=?");
			st.setString(1, newTodolistItem.getUserName());
			st.setString(2, newTodolistItem.getName());
			st.setString(3, newTodolistItem.getDescription());
			st.setString(4, newTodolistItem.getParentTodolistName());
			st.setInt(5, newTodolistItem.getPriority());
			st.setInt(6, newTodolistItem.isComplete()?1:0);
			st.setString(7, oldTodolistItemUser);
			st.setString(8, oldTodolistItemListName);
			st.setString(9, oldTodolistItemName);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::modifyTodolistItem : " + e);
		}
	}

	private void deleteTodolistItem(ObjectConnection oc, String userName, String listName, String name) {
		try {
			st = conn.prepareStatement(
					"DELETE FROM todolistitems WHERE user_name=? AND list_name=? AND name=?");
			st.setString(1, userName);
			st.setString(2, listName);
			st.setString(3, name);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::deleteTodolistItem : " + e);
		}
	}

}
