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
package org.lucane.applications.administrator.gui;

import javax.swing.*;
import java.awt.event.*;
import java.net.URL;

import org.lucane.client.*;
import org.lucane.client.widgets.ListBox;
import org.lucane.common.ConnectInfo;


public class PluginButton extends JButton
implements ActionListener
{	
	private Plugin plugin;

	public PluginButton(Plugin plugin)
	{
		this.plugin = plugin;
		
		try {
			ImageIcon icon = new ImageIcon(new URL(plugin.getDirectory() + plugin.getIcon()));
			this.setIcon(icon);
		} catch(Exception e) {
			//no icon for this plugin
		}
		
		this.addActionListener(this);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setText(plugin.getTitle());
	}
	
	public void actionPerformed(ActionEvent ae)
	{
	  	ConnectInfo[] friends = null;  	
	  	
	  	// get users
	  	if(plugin.isStandalone())
	  		friends = new ConnectInfo[0];
	  	else
	  	{	
	  		ListBox userList = new ListBox(null, plugin.getTitle(), plugin.tr("msg.selectUsers"), 
	  				Client.getInstance().getUserList());
	  		Object[] users = userList.selectItems();
	  		if(users != null)
	  		{	
	  			friends = new ConnectInfo[users.length];
	  			for(int i=0;i<friends.length;i++)
	  				friends[i] = Communicator.getInstance().getConnectInfo((String)users[i]);
	  		}
	  	}
	  	
	  	// run the plugin if the user didn't click on cancel
	  	if(friends != null)
	  		PluginLoader.getInstance().run(plugin.getName(), friends);
	}
}