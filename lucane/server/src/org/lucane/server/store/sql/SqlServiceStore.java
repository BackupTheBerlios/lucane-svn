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

import org.lucane.common.concepts.ServiceConcept;
import org.lucane.server.store.ServiceStore;
import org.lucane.server.database.*;
import org.lucane.server.*;

public class SqlServiceStore extends ServiceStore
{
	private static final String TABLENAME = "services";
	private DatabaseAbstractionLayer layer;
	
	public SqlServiceStore()
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
			+ "name " + layer.resolveType("SMALLTEXT") + ", "
			+ "installed " + layer.resolveType("SMALLINT") + ", "
			+ "description " + layer.resolveType("TEXT") + ")";
			
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		s.execute(query);
		s.close();
		c.close();
	}
	
	//-- interface
	public void storeService(ServiceConcept service)
	throws SQLException
	{	
		//store service
		Connection c = layer.openConnection();
		Statement s = c.createStatement();			
		String query = "INSERT INTO " + TABLENAME + " VALUES('"
			+ service.getName() + "', "
			+ (service.isInstalled() ? 1 : 0) + ", '"
			+ service.getDescription() + "')";
			
		s.execute(query);
		s.close();
		c.close();
	}
	
	public void updateService(ServiceConcept service)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();		
		
		//try to delete service
		try {
			s.execute("DELETE FROM " + TABLENAME 
			+ " WHERE name='" + service.getName() + "'");							
		} catch(SQLException e) {
			//no such service
		}
		
		//store service
	
		String query = "INSERT INTO " + TABLENAME + " VALUES('"
			+ service.getName() + "', "
			+ (service.isInstalled() ? 1 : 0) + ", '"
			+ service.getDescription() + "')";
			
		s.execute(query);
		s.close();
		c.close();
	}

	public void removeService(ServiceConcept service)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		
		s.execute("DELETE FROM " + TABLENAME 
			+ " WHERE name='" + service.getName() + "'");

		s.execute("DELETE FROM " + SqlGroupStore.SERVICELINKS 
			+ " WHERE service='" + service.getName() + "'");			
		s.close();
		c.close();	
	}

	public ServiceConcept getService(String name) 
	throws SQLException
	{
		ServiceConcept service = null;

		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME 
			+ " WHERE name='" + name + "'");
					
		if(rs.next())
		{
			name = rs.getString(1);
			boolean installed = (rs.getInt(2) != 0);
			String description = rs.getString(3); 
			
			service = new ServiceConcept(name, installed);
			service.setDescription(description);
		}
	
		rs.close();		
		s.close();
		c.close();	
			
		return service;
	}
	
	public Iterator getAllServices()
	throws SQLException
	{
		ArrayList all = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME);
					
		while(rs.next())
		{
			String name = rs.getString(1);
			boolean installed = (rs.getInt(2) != 0);
			String description = rs.getString(3); 
			
			ServiceConcept service = new ServiceConcept(name, installed);
			service.setDescription(description);
			all.add(service);
		}
	
		rs.close();		
		s.close();
		c.close();		
		
		return all.iterator();
	}
}