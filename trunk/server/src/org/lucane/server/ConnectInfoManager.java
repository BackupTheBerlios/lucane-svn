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
	private ConnectInfo myConnectInfo;

	
	private ConnectInfoManager()
	{
		this.connections = new ArrayList();        
	}
	
	public void setMyInfos(ConnectInfo my)
	{
		this.myConnectInfo = my;
	}

	public ConnectInfo getMyInfos()
	{
		return this.myConnectInfo;
	}
	
	public void addConnectInfo(ConnectInfo info)
	{
		this.connections.add(info);
	}

	/**
	 * Remove a ConnectionInfo
	 * 
	 * @param ci the connect info
	 */
	public void removeConnectInfo(ConnectInfo ci)
	{
		removeConnectInfo(ci.getName());
	}
	
	/**
	 * Remove a ConnectionInfo
	 * 
	 * @param user the user name
	 */
	public void removeConnectInfo(String user)
	{
		//TODO replace with an iterator
		for (int i = 0; i < this.connections.size(); i++)
		{
			ConnectInfo ci = (ConnectInfo)this.connections.get(i);
			if (ci.getName().equals(user))
			{
				this.connections.remove(ci);
				break;
			}
		}
		
		Server.getInstance().sendUserList();
	}
	
	public Iterator getAllConnectInfos()
	{
		return this.connections.iterator();
	}
	
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
		for (int i=0;  i<this.connections.size(); i++)
		{
			ConnectInfo ci = (ConnectInfo)this.connections.get(i);
			if(ci.getName().equals(userName))
				return ci;
		}
		
		return null;
	}
	
	/**
	 * Get the complete ConnectInfo
	 * 
	 * @param ConnectInfo the user
	 * @return the complete ConnectInfo
	 */
	public ConnectInfo getCompleteConnectInfo(ConnectInfo ci)
	{
		Iterator i = this.connections.iterator();
		
		while (i.hasNext())
		{
			ConnectInfo tmp = (ConnectInfo)i.next();
			if (ci.getName().equals(tmp.getName()))
				return tmp;
		}
		
		return ci;
	}
}