/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004 Vincent Fiack <vfiack@mail15.com>
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

import java.util.*;

import org.lucane.common.*;

public class ConnectInfoManager
{
	//-- instance management
	private static ConnectInfoManager instance = null;
	public static ConnectInfoManager getInstance()
	{
		if(instance == null)
			instance = new ConnectInfoManager();
		
		return instance;
	}
	
	//-- attributes
	private ArrayList connections;
	private ConnectInfo serverInfo;
	
	/**
	 * Constructor
	 */
	private ConnectInfoManager()
	{
		this.connections = new ArrayList();        
	}
	
	/**
	 * Set the server's ConnectInfo
	 * 
	 * @param info the server info
	 */
	public void setServerInfo(ConnectInfo info)
	{
		this.serverInfo = info;
	}
	
	/**
	 * Get the server's ConnectInfo
	 * 
	 * @return the server info
	 */
	public ConnectInfo getServerInfo()
	{
		return this.serverInfo;
	}
	
	/**
	 * Add a connect info to the list
	 * 
	 * @param info the info to add
	 */
	public void addConnectInfo(ConnectInfo info)
	{
		this.connections.add(info);
	}
	
	/**
	 * Remove a ConnectionInfo
	 * 
	 * @param info the info to remove
	 */
	public void removeConnectInfo(ConnectInfo info)
	{
		removeConnectInfo(info.getName());
	}
	
	/**
	 * Remove a ConnectionInfo
	 * 
	 * @param user the user name to remove
	 */
	public void removeConnectInfo(String user)
	{
		ConnectInfo info = getConnectInfo(user);
		if(info == null)
			return;
		
		this.connections.remove(info);
		
		//refresh the user list if needed
		if(info.type.equalsIgnoreCase("Client"))
			Server.getInstance().sendUserListToEveryone();
	}
	
	/**
	 * Get all known connectinfos
	 * @return an iterator of ConnectInfos
	 */
	public Iterator getAllConnectInfos()
	{
		return this.connections.iterator();
	}
	
	/**
	 * Get all client connectinfos
	 * 
	 * @return an iterator of ConnectInfos with "Client" as a type
	 */
	public Iterator getClientConnectInfos()
	{
		ArrayList clients = new ArrayList();
		
		Iterator i = getAllConnectInfos();
		while(i.hasNext())
		{
			ConnectInfo ci = (ConnectInfo)i.next();
			if(ci.type.equalsIgnoreCase("Client"))
				clients.add(ci);
		}
		
		return clients.iterator();
	}
	
	
	/**
	 * Check if a user is already known
	 * 
	 * @param ConnectInfo the user
	 * @return true or false
	 */
	public boolean isConnected(String userName)
	{
		return (getConnectInfo(userName) != null);
	}
	
	
	/**
	 * Check if a user is already known
	 * 
	 * @param ConnectInfo the user
	 * @return true or false
	 */
	public boolean isConnected(ConnectInfo ci)
	{
		return isConnected(ci.getName());
	}
	
	
	/**
	 * Get a user connection infos
	 * 
	 * @param userName the user
	 * @return the ConnectInfo
	 */
	public ConnectInfo getConnectInfo(String userName) 
	{
		Iterator infos = this.connections.iterator();
		while(infos.hasNext())
		{
			ConnectInfo info = (ConnectInfo)infos.next();
			if (info.getName().equals(userName))
				return info;
		}
		
		return null;
	}
	
	/**
	 * Get the complete ConnectInfo
	 * 
	 * @param ConnectInfo the user
	 * @return the complete ConnectInfo
	 */
	public ConnectInfo getCompleteConnectInfo(ConnectInfo info)
	{
		ConnectInfo complete = getConnectInfo(info.getName());		
		return complete == null ? info : complete;
	}
}