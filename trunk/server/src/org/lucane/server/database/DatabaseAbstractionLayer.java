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


/**
 * Database abstraction layer.
 * Allow to use different databases, even if the SQL types aren't completely normalized
 */
public abstract class DatabaseAbstractionLayer
{
  /**
   * DatabaseLayer Factory
   * Get the layer corresponding to the driver
   *
   * @param driver the JDBC driver
   * @param url the JDBC connection url
   * @param login the JDBC login
   * @param passwd the JDBC password
   */
  public static DatabaseAbstractionLayer createLayer(ServerConfig config)
    throws Exception
  {
      Class.forName(config.getDbDriver());
      
      //-- dynamic layer loading
      Class klass = Class.forName(config.getDbLayer());
      Class[] types = {String.class, String.class, String.class};
      Object[] values = {config.getDbUrl(), config.getDbLogin(), config.getDbPassword()};
      Constructor constr = klass.getConstructor(types);
      return (DatabaseAbstractionLayer)constr.newInstance(values);
  }

  //-- attributes
  protected String url;
  protected String login;
  protected String passwd;

  /**
   * Open a new connection
   *
   * @return the connection
   */
  public Connection openConnection()
  throws SQLException
  {
    return DriverManager.getConnection(url, login, passwd);
  }
  
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
	Connection c = this.openConnection();
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
