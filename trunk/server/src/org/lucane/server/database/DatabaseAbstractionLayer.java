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

import org.lucane.common.Logging;
import org.lucane.server.ServerConfig;
import org.w3c.dom.*;

import java.lang.reflect.*;
import java.sql.*;

import javax.xml.parsers.*;

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
   * Create tables from a xml file describing a database subset
   * 
   * @param xmlfile the path to the xml description
   */
  public void createFromXml(String xmlfile)
  throws Exception
  {
  	 DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  	 Document document = builder.parse(xmlfile);
  	
  	 //-- root element
  	 Node node = document.getFirstChild();                                                                               
  	 while(node != null && node.getNodeType() != Node.ELEMENT_NODE)
  		node = node.getNextSibling();
  	
  	 if(node == null || !node.getNodeName().equals("database"))
  		throw new Exception("root element is different from 'database'");
  	 
  	 
  	 //-- tables
  	 Connection c = openConnection();
  	 node = node.getFirstChild();
  	 while(node != null)
  	 {	
  	 	if(node.getNodeName().equals("table"))
  	 		createTableFromNode(c, node);
  	 	
  	 	node = node.getNextSibling();
  	 }
  	 c.close();
  	 
  	 Logging.getLogger().info("Created tables from '" +xmlfile + "'.");  	 
  }
  
  /**
   * Create a table from a xml node
   * 
   * @param c an already opened connection
   * @param node the node describing the table
   */
  private void createTableFromNode(Connection c, Node node)
  throws SQLException
  {
  	StringBuffer query =new StringBuffer("CREATE TABLE ");
  	String tableName = node.getAttributes().getNamedItem("name").getNodeValue();
    query.append(tableName);
    query.append("(");
    
    //-- columns
    node = node.getFirstChild();
    while(node != null)
    {
    	if(node.getNodeName().equals("column"))
    	{
    		String columnName = node.getAttributes().getNamedItem("name").getNodeValue();
    		String columnType = node.getAttributes().getNamedItem("type").getNodeValue();
    		query.append(columnName);
    		query.append(" ");
    		query.append(resolveType(columnType));
    		query.append(", ");
    	}
    	node = node.getNextSibling();
    }
    query.delete(query.length()-2, query.length());
    query.append(")");
    
    //-- execution
    Statement s = c.createStatement();
    s.execute(query.toString());
    s.close();
    
    Logging.getLogger().fine("Created table " +tableName + " from xml.");
  }

  /**
   * Resolve a logic type
   * 
   * @param type the logic type (see DBMSDataTypes on the wiki)
   * @return the implementation dependent type
   */
  public abstract String resolveType(String type);
}
