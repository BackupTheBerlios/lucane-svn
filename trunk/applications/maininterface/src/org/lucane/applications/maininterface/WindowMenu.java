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

package org.lucane.applications.maininterface;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.lucane.client.Client;
import org.lucane.client.Plugin;
import org.lucane.client.widgets.ManagedWindow;

public class WindowMenu extends JMenu
implements MenuListener
{
	public WindowMenu()
	{
		super("Windows");
		this.addMenuListener(this);
	}
	
	public void menuCanceled(MenuEvent e) {}
	public void menuDeselected(MenuEvent e) {}
	public void menuSelected(MenuEvent e) 
	{
		//get all running plugins
		//could be better if PluginLoader could give them
		ArrayList pluginList = new ArrayList();
		Iterator windows = Client.getInstance().getWindowManager().getAllWindows();		
		while(windows.hasNext())
		{
			ManagedWindow window = (ManagedWindow)windows.next();
			if(!pluginList.contains(window.getOwner()))
				pluginList.add(window.getOwner());
		}
		
		//create the menu
		this.removeAll();
		Iterator plugins = pluginList.iterator();
		while(plugins.hasNext())
		{
			Plugin plugin = (Plugin)plugins.next();
			JMenu menu = new JMenu(plugin.getTitle());
			
			//TODO add showAll/hideAll
			
			windows = Client.getInstance().getWindowManager().getWindowsFor(plugin);
			while(windows.hasNext())
			{
				ManagedWindow window = (ManagedWindow)windows.next();
				JMenuItem item = new JMenuItem(window.getTitle());
				item.setEnabled(false);
				menu.add(item);	
			}
			
			this.add(menu);
		}
	}
}