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
	}
	
	//-- interface	
	public void storeUser(UserConcept user)
	throws SQLException
	{	
		//store user
		Connection c = layer.getConnection();
		PreparedStatement insert = c.prepareStatement("INSERT INTO " + TABLENAME 
			+ " VALUES(?, ?, ?, ?, ?, ?, ?);");
		
		insert.setString(1, user.getName());
		insert.setString(2, user.getPassword());
		insert.setInt(3, user.isLocked() ? 1 : 0);
		insert.setString(4, user.getStartupPlugin());
		insert.setString(5, user.getPublicKey());
		insert.setString(6, user.getPrivateKey());
		insert.setString(7, user.getDescription());	
		insert.execute();
		insert.close();
		
		c.close();
	}


	public void updateUser(UserConcept user)
	throws SQLException
	{
		Connection c = layer.getConnection();
		Statement s = c.createStatement();			

		//try to delete user
		try {
			PreparedStatement delete = c.prepareStatement("DELETE FROM " + TABLENAME 
				+ " WHERE login=?");
			delete.setString(1, user.getName());
			delete.execute();
		} catch(SQLException e) {
			//no such user
		}
		
		//store user
		PreparedStatement insert = c.prepareStatement("INSERT INTO " + TABLENAME 
			+ " VALUES(?, ?, ?, ?, ?, ?, ?);");
		
		insert.setString(1, user.getName());
		insert.setString(2, user.getPassword());
		insert.setInt(3, user.isLocked() ? 1 : 0);
		insert.setString(4, user.getStartupPlugin());
		insert.setString(5, user.getPublicKey());
		insert.setString(6, user.getPrivateKey());
		insert.setString(7, user.getDescription());	
		insert.execute();
		insert.close();
			
		c.close();
	}
	public void removeUser(UserConcept user)
	throws SQLException
	{
		Connection c = layer.getConnection();
		
		PreparedStatement delete = c.prepareStatement("DELETE FROM " + TABLENAME 
			+ " WHERE login=?");
		delete.setString(1, user.getName());
		delete.execute();
		
		delete = c.prepareStatement("DELETE FROM " + SqlGroupStore.USERLINKS 
			+ " WHERE userName=?");
		delete.setString(1, user.getName());
		delete.execute();
		
		c.close();							
	}
	
	public UserConcept getUser(String login) 
	throws SQLException
	{
		UserConcept user = null;

		Connection c = layer.getConnection();
		
		PreparedStatement select = c.prepareStatement("SELECT * FROM " + TABLENAME 
			+ " WHERE login=?");
		select.setString(1, login);
		ResultSet rs = select.executeQuery();
					
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
		select.close();
		c.close();	
			
		return user;
	}

	public Iterator getAllUsers()
	throws SQLException
	{
		ArrayList all = new ArrayList();
		
		Connection c = layer.getConnection();
		PreparedStatement select = c.prepareStatement("SELECT * FROM " + TABLENAME);
		ResultSet rs = select.executeQuery();		
							
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
		select.close();
		c.close();		
		
		return all.iterator();
	}
}
