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

import org.lucane.common.concepts.PluginConcept;
import org.lucane.server.store.PluginStore;
import org.lucane.server.database.*;
import org.lucane.server.*;

public class SqlPluginStore extends PluginStore
{
	private static final String TABLENAME = "plugins";
	private DatabaseAbstractionLayer layer;
	
	public SqlPluginStore()
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
			+ "version " + layer.resolveType("SMALLTEXT") + ", "
			+ "description " + layer.resolveType("TEXT") + ")";
			
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		s.execute(query);
		s.close();
		c.close();
	}
	
	
	//-- interface
	public void storePlugin(PluginConcept plugin)
	throws SQLException
	{	
		//store plugin
		Connection c = layer.openConnection();
		Statement s = c.createStatement();			
		String query = "INSERT INTO " + TABLENAME + " VALUES('"
			+ plugin.getName() + "', '"
			+ plugin.getVersion() + "', '"
			+ plugin.getDescription() + "')";
			
		s.execute(query);
		s.close();
		c.close();
	}
	
	public void updatePlugin(PluginConcept plugin)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		
		//try to delete plugin
		try {
			s.execute("DELETE FROM " + TABLENAME 
				+ " WHERE name='" + plugin.getName() + "'");
		} catch(SQLException e) {
			//no such plugin
		}
		
		//store plugin	
		String query = "INSERT INTO " + TABLENAME + " VALUES('"
			+ plugin.getName() + "', '"
			+ plugin.getVersion() + "', '"
			+ plugin.getDescription() + "')";
			
		s.execute(query);
		s.close();
		c.close();
	}

	public void removePlugin(PluginConcept plugin)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		
		s.execute("DELETE FROM " + TABLENAME 
			+ " WHERE name='" + plugin.getName() + "'");
			
		s.execute("DELETE FROM " + SqlGroupStore.PLUGINLINKS 
			+ " WHERE plugin='" + plugin.getName() + "'");
						
		s.close();
		c.close();	
	}

	public PluginConcept getPlugin(String name) 
	throws SQLException
	{
		PluginConcept plugin = null;

		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME 
			+ " WHERE name='" + name + "'");
					
		if(rs.next())
		{
			name = rs.getString(1);
			String version = rs.getString(2);
			String description = rs.getString(3); 
			
			plugin = new PluginConcept(name, version);
			plugin.setDescription(description);
		}
	
		rs.close();		
		s.close();
		c.close();	
			
		return plugin;	
	}

	public Iterator getAllPlugins()
	throws SQLException
	{
		ArrayList all = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME);
					
		while(rs.next())
		{
			String name = rs.getString(1);
			String version = rs.getString(2);
			String description = rs.getString(3); 
			
			PluginConcept plugin = new PluginConcept(name, version);
			plugin.setDescription(description);
			all.add(plugin);
		}
	
		rs.close();		
		s.close();
		c.close();		
		
		return all.iterator();
	}
}