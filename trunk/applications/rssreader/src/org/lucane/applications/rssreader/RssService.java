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
package org.lucane.applications.rssreader;

import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.*;
import org.lucane.server.store.Store;

import java.sql.*;
import java.util.*;

public class RssService
extends Service
{
	private DatabaseAbstractionLayer layer = null;
	private Store store = null;

	public RssService()
	{
	}

	public void init(Server parent)
	{
		layer = parent.getDBLayer();
	}	

	public void install()
	{
		try {
			String dbDescription = getDirectory()	+ "db-rssreader.xml";
			layer.getTableCreator().createFromXml(dbDescription);
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to install RssService !");
			e.printStackTrace();
		}  
	}

	public void process(ObjectConnection oc, Message message)
	{  	
		RssAction ra = (RssAction)message.getData();
		String user = message.getSender().getName();
		ChannelInfo channel = (ChannelInfo)ra.channel;

		try {
			switch(ra.action)
			{
			case RssAction.GET_CHANNELS:
				ArrayList channels = this.getChannels(user);
				oc.write("OK");
				oc.write(channels);
				break;

			case RssAction.ADD_CHANNEL:
				this.addChannel(user, channel);
				oc.write("OK");
				break;
				
			case RssAction.REMOVE_CHANNEL:
				this.removeChannel(user, channel);
				oc.write("OK");
			}
		} catch(Exception e) {
			try {
				oc.write("FAILED " + e);
			} catch(Exception e2) {}			
			e.printStackTrace();
		}	
	}

	private ArrayList getChannels(String user) 
	throws Exception
	{
		ArrayList channels = new ArrayList();
		
		Connection connection = layer.getConnection();
		PreparedStatement select = connection.prepareStatement(
				"SELECT name, url FROM rssChannels WHERE user=?");
		
		select.setString(1, user);
		
		ResultSet result = select.executeQuery();
		while(result.next())
		{
			String name = result.getString(1);
			String url = result.getString(2);
			channels.add(new ChannelInfo(name, url));
		}
		
		result.close();
		select.close();
		connection.close();
		
		return channels;
	}

	private void addChannel(String user, ChannelInfo channel) 
	throws Exception
	{
		Connection connection = layer.getConnection();
		PreparedStatement insert = connection.prepareStatement(
			"INSERT INTO rssChannels VALUES(?, ?, ?)");
		
		insert.setString(1, user);
		insert.setString(2, channel.getName());
		insert.setString(3, channel.getUrl());
		insert.execute();
		
		insert.close();
		connection.close();
	}

	private void removeChannel(String user, ChannelInfo channel) 
	throws Exception
	{
		Connection connection = layer.getConnection();
		PreparedStatement delete = connection.prepareStatement(
			"DELETE FROM rssChannels WHERE user=? AND name=?");
		
		delete.setString(1, user);
		delete.setString(2, channel.getName());
		delete.execute();
		
		delete.close();
		connection.close();
	}
}

