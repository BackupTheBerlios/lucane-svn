package org.lucane.server.database.xml;

import org.lucane.common.Logging;
import org.lucane.server.*;
import org.lucane.server.database.*;

import org.w3c.dom.*;
import java.sql.*;

public class DefaultTableCreator extends TableCreator
{  
	/**
	 * Create a table from a xml node
	 * 
	 * @param connection an already opened connection
	 * @param node the node describing the table
	 */
	public void createTableFromXmlNode(Connection connection, Node node)
	throws Exception
	{
	  DatabaseAbstractionLayer layer = Server.getInstance().getDBLayer();
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
			  query.append(layer.resolveType(columnType));
			  query.append(", ");
		  }
		  else if(node.getNodeName().equals("primary-key"))
		  {
			  String columns = node.getAttributes().getNamedItem("columns").getNodeValue();
			  query.append(" PRIMARY KEY(");
			  query.append(columns);
			  query.append("), ");
		  }
		  node = node.getNextSibling();
	  }
	  query.delete(query.length()-2, query.length());
	  query.append(")");
    
	  //-- execution
	  Statement s = connection.createStatement();
	  s.execute(query.toString());
	  s.close();
    
	  Logging.getLogger().finest(query.toString());
	}
}
