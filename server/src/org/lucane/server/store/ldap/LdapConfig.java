/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.server.store.ldap;

import java.util.HashMap;

import javax.xml.parsers.*;

import org.lucane.common.Logging;
import org.w3c.dom.*;


/**
 * Server configuration
 */
public class LdapConfig
{
	public static final String CONFIG_FILE = "etc/ldap-store.xml";
	
	private String ldapUrl;
	
	private String authType;
	private String authBindDn;
	private String authPassword;
	
	private String pluginsDn;
	private HashMap pluginsAttributes = new HashMap();
	private HashMap pluginsMapping = new HashMap();
	
	private String servicesDn;
	private HashMap servicesAttributes = new HashMap();
	private HashMap servicesMapping = new HashMap();
	
	private String usersDn;
	private HashMap usersAttributes = new HashMap();
	private HashMap usersMapping = new HashMap();
	
	private String groupsDn;
	private HashMap groupsAttributes = new HashMap();
	private HashMap groupsMapping = new HashMap();
	
	
	/**
	 * Constructor
	 * 
	 * @param filename the XML config file
	 */
	public LdapConfig(String filename)
	throws Exception
	{
		  DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		  Document document = builder.parse(filename);
                                                                  
		  //-- root element
		  Node node = document.getFirstChild();                                                                               
		  while(node != null && node.getNodeType() != Node.ELEMENT_NODE)
			node = node.getNextSibling();
          
		  if(node == null || !node.getNodeName().equals("ldap"))
		  	throw new Exception("root element is different from 'ldap'");
		  
		  this.ldapUrl = node.getAttributes().getNamedItem("url").getNodeValue();
		  
  		  node = node.getFirstChild();
	 	  while(node != null)
	 	  {	
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals("authentication"))
					handleAuthentication(node);
				else if(node.getNodeName().equals("plugins"))
					handlePlugins(node);
				else if(node.getNodeName().equals("services"))
					handleServices(node);
				else if(node.getNodeName().equals("users"))
					handleUsers(node);
				else if(node.getNodeName().equals("groups"))
					handleGroups(node);
				else
				  Logging.getLogger().warning("unexepected node : " + node.getNodeName());
			}
		    node = node.getNextSibling();
	 	  }	 	  
	}
	
	/**
	 * Parse authentication node
	 * 
	 * @param node the authentication node
	 */
	private void handleAuthentication(Node node)
	{
		this.authType = node.getAttributes().getNamedItem("type").getNodeValue();
		this.authBindDn = node.getAttributes().getNamedItem("bind-dn").getNodeValue();
		this.authPassword = node.getAttributes().getNamedItem("password").getNodeValue();
	}
	
	/**
	 * Parse plugins node
	 * 
	 * @param node the plugins node
	 */
	private void handlePlugins(Node node)
	{
		this.pluginsDn = node.getAttributes().getNamedItem("dn").getNodeValue();
		
		node = node.getFirstChild();
		while(node != null)
		{	
		  if(node.getNodeType() == Node.ELEMENT_NODE)
		  {
			  if(node.getNodeName().equals("attribute"))
			  	handleMapping(this.pluginsAttributes, node);
			  else if(node.getNodeName().equals("mapping"))
			  	handleMapping(this.pluginsMapping, node);
			  else
			  	Logging.getLogger().warning("unexepected node : " + node.getNodeName());
		  }
		  node = node.getNextSibling();
		}	 	  		
	}
	
	/**
	 * Parse services node
	 * 
	 * @param node the services node
	 */
	private void handleServices(Node node)
	{
		this.servicesDn = node.getAttributes().getNamedItem("dn").getNodeValue();
		
		node = node.getFirstChild();
		while(node != null)
		{	
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals("attribute"))
					handleMapping(this.servicesAttributes, node);
				else if(node.getNodeName().equals("mapping"))
					handleMapping(this.servicesMapping, node);
				else
					Logging.getLogger().warning("unexepected node : " + node.getNodeName());
			}
			node = node.getNextSibling();
		}	 	  		
	}
	
	/**
	 * Parse users node
	 * 
	 * @param node the users node
	 */
	private void handleUsers(Node node)
	{
		this.usersDn = node.getAttributes().getNamedItem("dn").getNodeValue();
		
		node = node.getFirstChild();
		while(node != null)
		{	
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals("attribute"))
					handleMapping(this.usersAttributes, node);
				else if(node.getNodeName().equals("mapping"))
					handleMapping(this.usersMapping, node);
				else
					Logging.getLogger().warning("unexepected node : " + node.getNodeName());
			}
			node = node.getNextSibling();
		}	 	  		
	}
	
	/**
	 * Parse groups node
	 * 
	 * @param node the groups node
	 */
	private void handleGroups(Node node)
	{
		this.groupsDn = node.getAttributes().getNamedItem("dn").getNodeValue();
		
		node = node.getFirstChild();
		while(node != null)
		{	
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals("attribute"))
					handleMapping(this.groupsAttributes, node);
				else if(node.getNodeName().equals("mapping"))
					handleMapping(this.groupsMapping, node);
				else
					Logging.getLogger().warning("unexepected node : " + node.getNodeName());
			}
			node = node.getNextSibling();
		}	 	  		
	}
	
	/**
	 * Parse mapping or attribute node
	 * 
	 * @param node the node
	 */
	private void handleMapping(HashMap map, Node node)
	{
		String name = node.getAttributes().getNamedItem("name").getNodeValue();
		String value = node.getAttributes().getNamedItem("value").getNodeValue();
		
		map.put(name, value);
	}

	//-- getters
	
	public String getLdapUrl()
	{
		return this.ldapUrl;
	}
	
	public String getAuthType() 
	{
		return this.authType;
	}	

	public String getAuthBindDn() 
	{
		return this.authBindDn;
	}	
	
	public String getAuthPassword() 
	{
		return this.authPassword;
	}	
	
	public String getPluginsDn()
	{
		return this.pluginsDn;		
	}
	
	public HashMap getPluginsAttributes()
	{
		return this.pluginsAttributes;
	}
	
	public HashMap getPluginsMapping()
	{
		return this.pluginsMapping;		
	}
	
	public String getServicesDn()
	{
		return this.servicesDn;
	}
	
	public HashMap getServicesAttributes()
	{
		return this.servicesAttributes;
	}
	
	public HashMap getServicesMapping()
	{
		return this.servicesMapping;
	}
	
	public String getUsersDn()
	{
		return this.usersDn;
	}
	
	public HashMap getUsersAttributes()
	{
		return this.usersAttributes;
	}
	
	public HashMap getUsersMapping()
	{
		return this.usersMapping;
	}
	
	public String getGroupsDn()
	{
		return this.groupsDn;
	}
	
	public HashMap getGroupsAttributes()
	{
		return this.groupsAttributes;
	}
	
	public HashMap getGroupsMapping()
	{
		return this.groupsMapping;
	}
}