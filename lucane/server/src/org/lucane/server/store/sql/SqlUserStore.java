/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.server.store.sql;

import java.util.*;
import java.sql.*;

import org.lucane.common.concepts.UserConcept;
import org.lucane.server.store.*;
import org.lucane.server.*;
import org.lucane.server.database.*;

public class SqlUserStore extends UserStore
{
	private static final String TABLENAME = "users";
	private DatabaseAbstractionLayer layer;
	
	public SqlUserStore()
	throws Exception
	{
		this.layer = Server.getInstance().getDBLayer();
		
		if(!layer.hasTable(TABLENAME))
			createTable();
	}
		
	private void createTable()
	throws SQLException
	{
		String query = "CREATE TABLE " + TABLENAME + "("
			+ "login " + layer.resolveType("SMALLTEXT") + ", "
			+ "passwd " + layer.resolveType("SMALLTEXT") + ", "
			+ "locked " + layer.resolveType("SMALLINT") + ", "
			+ "startup " + layer.resolveType("TEXT") + ", "
			+ "pubkey " + layer.resolveType("TEXT") + ", "
			+ "privkey " + layer.resolveType("TEXT") + ", "
			+ "description " + layer.resolveType("TEXT") + ")";
			
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		s.execute(query);
		s.close();
		c.close();
	}
	
	//-- interface	
	public void storeUser(UserConcept user)
	throws SQLException
	{	
		//store user
		Connection c = layer.openConnection();
		Statement s = c.createStatement();			
		String query = "INSERT INTO " + TABLENAME + " VALUES('"
			+ user.getName() + "', '"
			+ user.getPassword() + "', "
			+ (user.isLocked() ? 1 : 0) + ", '"
			+ user.getStartupPlugin() + "', '"
			+ user.getPublicKey() + "', '"
			+ user.getPrivateKey() + "', '"
			+ user.getDescription() + "')";
			
		s.execute(query);
		s.close();
		c.close();
	}


	public void updateUser(UserConcept user)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();			

		//try to delete user
		try {
			s.execute("DELETE FROM " + TABLENAME 
				+ " WHERE login='" + user.getName() + "'");						
		} catch(SQLException e) {
			//no such user
		}
		
		//store user
		String query = "INSERT INTO " + TABLENAME + " VALUES('"
			+ user.getName() + "', '"
			+ user.getPassword() + "', "
			+ (user.isLocked() ? 1 : 0) + ", '"
			+ user.getStartupPlugin() + "', '"
			+ user.getPublicKey() + "', '"
			+ user.getPrivateKey() + "', '"
			+ user.getDescription() + "')";
			
		s.execute(query);
		s.close();
		c.close();
	}
	public void removeUser(UserConcept user)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		
		s.execute("DELETE FROM " + TABLENAME 
			+ " WHERE login='" + user.getName() + "'");

		s.execute("DELETE FROM " + SqlGroupStore.USERLINKS 
			+ " WHERE user='" + user.getName() + "'");
			
		s.close();
		c.close();							
	}
	
	public UserConcept getUser(String login) 
	throws SQLException
	{
		UserConcept user = null;

		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME 
			+ " WHERE login='" + login + "'");
					
		if(rs.next())
		{
			login = rs.getString(1);
			String passwd = rs.getString(2);
			boolean locked = (rs.getInt(3) != 0);
			String startup = rs.getString(4);
			String pubkey = rs.getString(5);
			String privkey = rs.getString(6);
			String description = rs.getString(7); 
			
			user = new UserConcept(login, passwd, locked, startup);
			user.setKeys(pubkey, privkey);
			user.setDescription(description);
		}
	
		rs.close();		
		s.close();
		c.close();	
			
		return user;
	}

	public Iterator getAllUsers()
	throws SQLException
	{
		ArrayList all = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME);
					
		while(rs.next())
		{
			String login = rs.getString(1);
			String passwd = rs.getString(2);
			boolean locked = (rs.getInt(3) != 0);
			String startup = rs.getString(4);
			String pubkey = rs.getString(5);
			String privkey = rs.getString(6);
			String description = rs.getString(7); 
			
			UserConcept user = new UserConcept(login, passwd, locked, startup);
			user.setKeys(pubkey, privkey);
			user.setDescription(description);
			all.add(user);
		}
	
		rs.close();		
		s.close();
		c.close();		
		
		return all.iterator();
	}
}