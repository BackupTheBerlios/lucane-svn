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
package org.lucane.client;

import javax.xml.parsers.*;

import org.lucane.common.Logging;
import org.w3c.dom.*;

/**
 * Client configuration
 */
public class ClientConfig
{
	//-- attributes
	private String login = "";
	private String serverHost = null;
	private int listenerPort = 0;
	private int serverPort = 9115;
	private String language = "en";
	private String looknfeel = null;
	private String proxyHost = null;
	private int proxyPort = 5119;
	private String publicIp = null;
	
	/**
	 * Constructor
	 * 
	 * @param filename the xml file 
	 */
	public ClientConfig(String filename)
	throws Exception
	{
		  DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		  Document document = builder.parse(filename);
                                                                  
		  //-- root element
		  Node node = document.getFirstChild();                                                                               
		  while(node != null && node.getNodeType() != Node.ELEMENT_NODE)
			node = node.getNextSibling();
          
		  if(node == null || ! node.getNodeName().equals("lucane-client"))
		  	throw new Exception("root element is different from 'lucane-client'");
		  		  
  		  node = node.getFirstChild();
	 	  while(node != null)
	 	  {	
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals("login"))
					handleLogin(node);
				else if(node.getNodeName().equals("server"))
					handleServer(node);
				else if(node.getNodeName().equals("language"))
					handleLanguage(node);
				else if(node.getNodeName().equals("looknfeel"))
					handleLooknfeel(node);					
				else if(node.getNodeName().equals("proxy"))
					handleProxy(node);
				else if(node.getNodeName().equals("public-ip"))
					handlePublicIp(node);
				else if(node.getNodeName().equals("listener"))
					handleListener(node);
				else
				  Logging.getLogger().warning("unexepected node : " + node.getNodeName());
			}
		    node = node.getNextSibling();
	 	  }	 	  
	}
	
	/**
	 * Parse login node
	 * 
	 * @param node the login node
	 */
	private void handleLogin(Node node)
	{
		this.login = node.getAttributes().getNamedItem("value").getNodeValue();
	}
	
	/**
	 * Parse server node
	 * 
	 * @param node the server node
	 */
	private void handleServer(Node node)
	{
		this.serverHost = node.getAttributes().getNamedItem("host").getNodeValue();
		this.serverPort = Integer.parseInt(node.getAttributes().getNamedItem("port").getNodeValue());
	}	

	/**
	 * Parse language node
	 * 
	 * @param node the language node
	 */
	private void handleLanguage(Node node)
	{
		this.language = node.getAttributes().getNamedItem("value").getNodeValue();
	}
	
	/**
	 * Parse looknfeel node
	 * 
	 * @param node the looknfeel node
	 */
	private void handleLooknfeel(Node node)
	{
		this.looknfeel = node.getAttributes().getNamedItem("class").getNodeValue();
	}	

	/**
	 * Parse proxy node
	 * 
	 * @param node the proxy node
	 */
	private void handleProxy(Node node)
	{
		this.proxyHost = node.getAttributes().getNamedItem("host").getNodeValue();
		this.proxyPort = Integer.parseInt(node.getAttributes().getNamedItem("port").getNodeValue());
	}	

	/**
	 * Parse public-ip node
	 * 
	 * @param node the public-ip node
	 */
	private void handlePublicIp(Node node)
	{
		this.publicIp = node.getAttributes().getNamedItem("address").getNodeValue();
	}
	
	/**
	 * Parse listener node
	 * 
	 * @param node the listener node
	 */
	private void handleListener(Node node)
	{
		this.listenerPort = Integer.parseInt(node.getAttributes().getNamedItem("port").getNodeValue());
	}

	//-- getters
	
	/**
	 * Get the default login
	 * 
	 * @return the default login
	 */
	public String getLogin()
	{
		return login;
	}

	/**
	 * Get the server hostname
	 * 
	 * @return the server hostname
	 */
	public String getServerHost()
	{
		return serverHost;
	}

	/**
	 * Get the server port
	 * 
	 * @return the server port
	 */
	public int getServerPort()
	{
		return serverPort;
	}

	/**
	 * Get the language used
	 * 
	 * @return the language
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * Get the looknfeel used
	 * 
	 * @return the looknfeel
	 */
	public String getLooknfeel()
	{
		return looknfeel;
	}
	
	/**
	 * Get the proxy host
	 * 
	 * @return the proxy host or null if no proxy
	 */
	public String getProxyHost()
	{
		return proxyHost;
	}

	/**
	 * Get the proxy port
	 * 
	 * @return the proxy port
	 */
	public int getProxyPort()
	{
		return proxyPort;
	}

	/**
	 * Get the public ip
	 * 
	 * @return the public ip or none if useless
	 */
	public String getPublicIp()
	{
		return publicIp;
	}
	
	/**
	 * Get the listener port
	 * 
	 * @return the listener port
	 */
	public int getListenerPort()
	{
		return listenerPort;
	}	
}