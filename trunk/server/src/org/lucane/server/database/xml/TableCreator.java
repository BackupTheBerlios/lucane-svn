package org.lucane.server.database.xml;

import org.lucane.common.Logging;
import org.lucane.server.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.sql.*;

public abstract class TableCreator
{
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
	   Connection c = Server.getInstance().getDBLayer().openConnection();
	   node = node.getFirstChild();
	   while(node != null)
	   {	
		  if(node.getNodeName().equals("table"))
			  createTableFromXmlNode(c, node);
  	 	
		  node = node.getNextSibling();
	   }
	   c.close();
  	 
	   Logging.getLogger().info("Created tables from '" +xmlfile + "'.");  	 
	}
  
	/**
	 * Create a table from a xml node
	 * 
	 * @param connection an already opened connection
	 * @param node the node describing the table
	 */
	public abstract void createTableFromXmlNode(Connection connection, Node node) throws Exception;
}
