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
package org.lucane.server;

import javax.xml.parsers.*;

import org.lucane.common.Logging;
import org.w3c.dom.*;


/**
 * Server configuration
 */
public class ServerConfig
{
	//-- attributes
	private int port = 9115;
	private String dbDriver = null;
	private String dbUrl = null;
	private String dbLogin = null;
	private String dbPassword = "";
	private String dbLayer = null;
	private String storeBackend = "database";
	private String authenticatorClass = "org.lucane.server.auth.DefaultAuthenticator";
	
	/**
	 * Constructor
	 * 
	 * @param filename the XML config file
	 */
	public ServerConfig(String filename)
	throws Exception
	{
		  DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		  
		  String fileToParse = Server.lucanePath!=null?Server.lucanePath+filename:filename;
		  Document document = builder.parse(fileToParse);
                                                                  
		  //-- root element
		  Node node = document.getFirstChild();                                                                               
		  while(node != null && node.getNodeType() != Node.ELEMENT_NODE)
			node = node.getNextSibling();
          
		  if(node == null || !node.getNodeName().equals("lucane-server"))
		  	throw new Exception("root element is different from 'lucane-server'");
		  
		  this.port = Integer.parseInt(node.getAttributes().getNamedItem("port").getNodeValue());
		  
  		  node = node.getFirstChild();
	 	  while(node != null)
	 	  {	
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals("database"))
					handleDatabase(node);
				else if(node.getNodeName().equals("store"))
					handleStore(node);
				else if(node.getNodeName().equals("authenticator"))
					handleAuthenticator(node);
				else
				  Logging.getLogger().warning("unexepected node : " + node.getNodeName());
			}
		    node = node.getNextSibling();
	 	  }	 	  
	}
	
	/**
	 * Parse database node
	 * 
	 * @param node the database node
	 */
	private void handleDatabase(Node node)
	{
		node = node.getFirstChild();
		while(node != null)
		{	
		  if(node.getNodeType() == Node.ELEMENT_NODE)
		  {
			  if(node.getNodeName().equals("jdbc"))
			  {	
				this.dbDriver = node.getAttributes().getNamedItem("driver").getNodeValue();
				this.dbUrl = node.getAttributes().getNamedItem("url").getNodeValue();
				this.dbLogin = node.getAttributes().getNamedItem("login").getNodeValue();
				this.dbPassword = node.getAttributes().getNamedItem("password").getNodeValue();				
			  }
			  else if(node.getNodeName().equals("dblayer"))
			  	this.dbLayer = node.getAttributes().getNamedItem("class").getNodeValue();
			  else
			  	Logging.getLogger().warning("unexepected node : " + node.getNodeName());
		  }
		  node = node.getNextSibling();
		}	 	  		
	}

	/**
	 * Parse store node
	 * 
	 * @param node the store node
	 */
	private void handleStore(Node node)
	{
		this.storeBackend = node.getAttributes().getNamedItem("backend").getNodeValue();
	}
	
	/**
	 * Parse authenticator node
	 * 
	 * @param node the authenticator node
	 */
	private void handleAuthenticator(Node node)
	{
		this.authenticatorClass = node.getAttributes().getNamedItem("class").getNodeValue();
	}
	
	//-- getters
	
	/**
	 * Get server port
	 * 
	 * @return the server port 
	 */
	public int getPort()
	{
		return this.port;
	}
	
	/**
	 * Get JDBC driver
	 * 
	 * @return the JDBC driver
	 */
	public String getDbDriver()
	{
		return this.dbDriver;
	}
	
	/**
	 * Get JDBC url
	 * 
	 * @return the JDBC url
	 */	
	public String getDbUrl()
	{
		return this.dbUrl;
	}

	/**
	 * Get JDBC login
	 * 
	 * @return the JDBC login
	 */	
	public String getDbLogin()
	{
		return this.dbLogin;
	}

	/**
	 * Get JDBC password
	 * 
	 * @return the JDBC password
	 */	
	public String getDbPassword()
	{
		return this.dbPassword;
	}
	
	/**
	 * Get the DatabaseAbstractionLayer used
	 * 
	 * @return the concrete DatabaseLayer
	 */
	public String getDbLayer()
	{
		return this.dbLayer;
	}
	
	/**
	 * Get the backend used for Store
	 * (should be database, or ldap later)
	 * 
	 * @return the backend for store
	 */
	public String getStoreBackend()
	{
		return this.storeBackend;
	}
	
	/**
	 * Get the authenticator class to use
	 *
	 * @return the class
	 */
	public String getAuthenticatorClass()
	{
		return this.authenticatorClass;
	}
}