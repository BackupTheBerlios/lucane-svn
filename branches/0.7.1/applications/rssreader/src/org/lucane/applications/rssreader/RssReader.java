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

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.lucane.applications.rssreader.gui.*;
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;
import org.lucane.common.ObjectConnection;

public class RssReader extends StandalonePlugin 
{
	private MainFrame frame;
	private ConnectInfo service;
	
	public RssReader() 
	{
		this.starter = true;
	}

	public Plugin newInstance(ConnectInfo[] friends, boolean starter) 
	{
		return new RssReader();
	}

	public void start() 
	{
		this.service = Communicator.getInstance().getConnectInfo(getName());

		setProxyFromLocalConfig();
		
		frame = new MainFrame(this);
		frame.refreshChannelList();
		frame.setPreferredSize(new Dimension(600, 600));
		frame.restoreState();
		frame.show();
	}
	
	public void setProxyFromLocalConfig()
	{
		System.setProperty("proxySet", getLocalConfig().get("proxySet", "false"));
		System.setProperty("proxyHost", getLocalConfig().get("proxyHost", ""));
		System.setProperty("proxyPort", getLocalConfig().get("proxyPort", ""));
	}

	public void openUrl(URL url)
	{
		try	{
			BrowserLauncher.openURL(url.toString());
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}
	
	public void addChannel(ChannelInfo channel)
	{
		RssAction action = new RssAction(RssAction.ADD_CHANNEL, channel);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), action);
		
		String ack = tr("err.connection");
		try {
			 ack = oc.readString();
		} catch(Exception e) {}
		
		if(! ack.equals("OK"))
			DialogBox.error(tr("err.addChannel") + ack);
		else
			frame.refreshChannelList();

		oc.close();
	}
	
	public void removeChannel(ChannelInfo channel)
	{
		RssAction action = new RssAction(RssAction.REMOVE_CHANNEL, channel);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), action);
		
		String ack = tr("err.connection");
		try {
			ack = oc.readString();
		} catch(Exception e) {}
		
		if(! ack.equals("OK"))
			DialogBox.error(tr("err.removeChannel") + ack);
		else
			frame.refreshChannelList();

		oc.close();
	}
	
	public ArrayList getChannels()
	{
		ArrayList channels;
		
		RssAction action = new RssAction(RssAction.GET_CHANNELS);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), action);
		
		String ack = tr("err.connection");
		try {
			ack = oc.readString();
			channels = (ArrayList)oc.read();
		} catch(Exception e) {
			channels = new ArrayList();
			e.printStackTrace();
		}
		
		if(! ack.equals("OK"))
			DialogBox.error(tr("err.listChannels") + ack);
		
		oc.close();
		
		return channels;
	}
	
	public void windowClosing(WindowEvent e)
	{
		frame.saveState();
		this.exit();
	}
	
  public ImageIcon getImageIcon(String name)
  {
    try {
        return new ImageIcon(new URL(getDirectory() + name));
    } catch(Exception e) {
        return new ImageIcon();
    }
  }
}
