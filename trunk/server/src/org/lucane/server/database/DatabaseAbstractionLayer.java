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
package org.lucane.server.database;

import org.lucane.server.ServerConfig;
import org.lucane.server.database.xml.*;

import java.lang.reflect.*;
import java.sql.*;

import org.apache.commons.dbcp.BasicDataSource;
import javax.sql.DataSource;

/**
 * Database abstraction layer.
 * Allow to use different databases, even if the SQL types aren't completely normalized
 */
public abstract class DatabaseAbstractionLayer
{	
	/**
	 * DatabaseLayer Factory
	 * Get the layer corresponding to the driver
	 */
	public static DatabaseAbstractionLayer createLayer(ServerConfig config)
	throws Exception
	{
		Class.forName(config.getDbDriver());
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(config.getDbDriver());
		ds.setUsername(config.getDbLogin());
		ds.setPassword(config.getDbPassword());
		ds.setUrl(config.getDbUrl());
		
		//TODO put these params in the server.xml config
		ds.setMaxWait(5000);
		ds.setInitialSize(5);
		
		//-- dynamic layer loading
		Class klass = Class.forName(config.getDbLayer());
		Class[] types = {DataSource.class};
		Object[] values = {ds};
		Constructor constr = klass.getConstructor(types);
		return (DatabaseAbstractionLayer)constr.newInstance(values);
	}
		
	/**
	 * Get an opened connection
	 *
	 * @return the connection
	 */
	public abstract Connection getConnection() throws SQLException;
	
	/**
	 * Check if a table is existing in the system
	 * 
	 * @param tableName the name of the table
	 * @return true if the database has this table
	 */
	public boolean hasTable(String tableName)
	throws SQLException
	{
		boolean has = false;
		String[] types = {"TABLE"};
		Connection c = this.getConnection();
		ResultSet rs = c.getMetaData().getTables(null, null, null, types);
		
		while(!has && rs.next())
			has = rs.getString(3).equalsIgnoreCase(tableName);
		
		rs.close();
		c.close();
		
		return has;
	}
	
	/**
	 * Escape a query (replace bad chars with escape codes)
	 * 
	 * @param query the query to escape
	 * @return the escaped query
	 */
	public String escape(String query)
	{
		return query.replaceAll("'", "\\'");
	}
	
	/**
	 * Return an instance of xml table creator
	 * @return the TableCreator
	 */
	public TableCreator getTableCreator()
	{
		return new DefaultTableCreator();
	}
	
	/**
	 * Resolve a logic type
	 * 
	 * @param type the logic type (see DBMSDataTypes on the wiki)
	 * @return the implementation dependent type
	 */
	public abstract String resolveType(String type);
}
