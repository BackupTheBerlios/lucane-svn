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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.lucane.common.Logging;
import org.lucane.common.Message;
import org.lucane.common.ObjectConnection;
import org.lucane.server.Server;
import org.lucane.server.Service;
import org.lucane.server.database.DatabaseAbstractionLayer;
import org.lucane.server.store.Store;

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
	        			getTodolistItems(oc, ((Integer)tla.getParam()).intValue());
	        		}
	                break;
            	case TodolistAction.ADD_TODOLIST :
            		addTodolist(oc, (Todolist)tla.getParam());
                    break;
            	case TodolistAction.MOD_TODOLIST :
	        		{
	        			Object[] tmpobj = (Object[])tla.getParam();
	        			modifyTodolist(oc, ((Integer)tmpobj[0]).intValue(), (Todolist)tmpobj[1]);
	        		}
	                break;
            	case TodolistAction.DEL_TODOLIST :
	            	{
	            		deleteTodolist(oc, ((Integer)tla.getParam()).intValue());
	            	}
                    break;
            	case TodolistAction.ADD_TODOLISTITEM :
            		addTodolistItem(oc, (TodolistItem)tla.getParam());
                    break;
            	case TodolistAction.MOD_TODOLISTITEM :
	        		{
	        			Object[] tmpobj = (Object[])tla.getParam();
	        			modifyTodolistItem(oc, ((Integer)tmpobj[0]).intValue(), (TodolistItem)tmpobj[1]);
	        		}
                    break;
            	case TodolistAction.DEL_TODOLISTITEM :
	            	{
	            		deleteTodolistItem(oc, ((Integer)tla.getParam()).intValue());
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
					"SELECT id, user_name, name, description FROM todolists WHERE user_name=?");
			st.setString(1, userName);
			res = st.executeQuery();
			
			ArrayList todolists = new ArrayList(); 
			while (res.next()) {
				todolists.add(new Todolist(res.getInt(1), res.getString(2), res.getString(3), res.getString(4)));
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
	
	private void getTodolistItems(ObjectConnection oc, int idList) {
		try {
			st = conn.prepareStatement(
					"SELECT id, id_list, name, description, priority, complete FROM todolistitems WHERE id_list=?");
			st.setInt(1, idList);
			res = st.executeQuery();
			
			ArrayList todolistitems = new ArrayList(); 
			while (res.next()) {
				todolistitems.add(new TodolistItem(res.getInt(1), res.getInt(2), res.getString(3), res.getString(4), res.getInt(5), res.getInt(6)==1));
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
					"SELECT MAX(id) FROM todolists");
			res = st.executeQuery();
			int id=1;
			if (res.next());
				id = res.getInt(1) + 1;
			res.close();
			st.close();
			
			st = conn.prepareStatement(
					"INSERT INTO todolists (id, user_name, name, description) VALUES (?, ?, ?, ?)");
			newTodolist.setId(id);
			st.setInt(1, id);
			st.setString(2, newTodolist.getUserName());
			st.setString(3, newTodolist.getName());
			st.setString(4, newTodolist.getDescription());
			st.executeUpdate();
			st.close();

			oc.write("OK");
			oc.write(new Integer(id));

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::addTodolist : " + e);
		}
	}

	private void modifyTodolist(ObjectConnection oc, int oldTodolistId, Todolist newTodolist) {
		try {
			st = conn.prepareStatement(
					"UPDATE todolists SET user_name=?, name=?, description=? WHERE id=?");
			st.setString(1, newTodolist.getUserName());
			st.setString(2, newTodolist.getName());
			st.setString(3, newTodolist.getDescription());
			st.setInt(4, oldTodolistId);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::modifyTodolist : " + e);
		}
	}

	private void deleteTodolist(ObjectConnection oc, int id) {
		try {
			st = conn.prepareStatement(
					"DELETE FROM todolists WHERE id=?");
			st.setInt(1, id);
			st.executeUpdate();
			st.close();

			st = conn.prepareStatement(
					"DELETE FROM todolistitems WHERE id_list=?");
			st.setInt(1, id);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::deleteTodolist : " + e);
		}
	}

	private void addTodolistItem(ObjectConnection oc, TodolistItem newTodolistItem) {
		try {
			st = conn.prepareStatement(
			"SELECT count(*) FROM todolists WHERE id=?");
			st.setInt(1, newTodolistItem.getParentTodolistId());
			res = st.executeQuery();
			if (!res.next()||res.getInt(1)!=1) {
				oc.write("FAILED");
				res.close();
				st.close();
				return;
			}
			res.close();
			st.close();

			st = conn.prepareStatement(
					"SELECT MAX(id) FROM todolistItems");
			res = st.executeQuery();
			int id=1;
			if (res.next());
				id = res.getInt(1) + 1;
			res.close();
			st.close();
		
			st = conn.prepareStatement(
					"INSERT INTO todolistitems (id, name, description, id_list, priority, complete) VALUES (?, ?, ?, ?, ?, ?)");
			st.setInt(1, id);
			st.setString(2, newTodolistItem.getName());
			st.setString(3, newTodolistItem.getDescription());
			st.setInt(4, newTodolistItem.getParentTodolistId());
			st.setInt(5, newTodolistItem.getPriority());
			st.setInt(6, newTodolistItem.isComplete()?1:0);
			st.executeUpdate();
			st.close();

			oc.write("OK");
			oc.write(new Integer(id));
			
		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::addTodolistItem : " + e);
		}
	}

	private void modifyTodolistItem(ObjectConnection oc, int oldTodolistItemId, TodolistItem newTodolistItem) {
		try {
			st = conn.prepareStatement(
					"UPDATE todolistitems SET name=?, description=?, id_list=?, priority=?, complete=? WHERE id=?");
			st.setString(1, newTodolistItem.getName());
			st.setString(2, newTodolistItem.getDescription());
			st.setInt(3, newTodolistItem.getParentTodolistId());
			st.setInt(4, newTodolistItem.getPriority());
			st.setInt(5, newTodolistItem.isComplete()?1:0);
			st.setInt(6, oldTodolistItemId);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::modifyTodolistItem : " + e);
		}
	}

	private void deleteTodolistItem(ObjectConnection oc, int id) {
		try {
			st = conn.prepareStatement(
					"DELETE FROM todolistitems WHERE id=?");
			st.setInt(1, id);
			st.executeUpdate();
			st.close();

			oc.write("OK");

		} catch (Exception e) {
			Logging.getLogger().warning(
				"Error in TodolistService::deleteTodolistItem : " + e);
		}
	}

}
