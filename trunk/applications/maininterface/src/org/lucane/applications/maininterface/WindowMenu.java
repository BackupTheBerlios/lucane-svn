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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.lucane.client.Client;
import org.lucane.client.Plugin;
import org.lucane.client.PluginManager;
import org.lucane.client.widgets.ManagedWindow;

public class WindowMenu extends JMenu
implements MenuListener, ActionListener
{
	private Plugin plugin;
	private HashMap toggleButtons;
	private ArrayList hiddenPlugins;
	
	public WindowMenu(Plugin plugin)
	{
		super(plugin.tr("mnu.windows"));
		this.plugin = plugin;
		
		this.toggleButtons = new HashMap();
		this.hiddenPlugins = new ArrayList();
		
		this.addMenuListener(this);
	}
	
	public void menuCanceled(MenuEvent e) {}
	public void menuDeselected(MenuEvent e) {}
	public void menuSelected(MenuEvent e) 
	{
		//create the menu
		toggleButtons.clear();
		this.removeAll();
		Iterator plugins = PluginManager.getInstance().getRunningPlugins();
		while(plugins.hasNext())
		{
			Plugin plugin = (Plugin)plugins.next();
			
			//zap startup plugin
			if(plugin.getName().equals(Client.getInstance().getStartupPlugin()))
				continue;

			Iterator windows = Client.getInstance().getWindowManager().getWindowsFor(plugin);
			
			//if the plugin has no window, we don't care
			if(!windows.hasNext())
				continue;
			
			JMenu menu = new JMenu(plugin.getTitle());
			
			//showAll/hideAll
			JMenuItem toggle = new JMenuItem(tr("mnu.hide"));
			toggleButtons.put(toggle, plugin);
			if(hiddenPlugins.contains(plugin))
				toggle.setText(tr("mnu.show"));
			toggle.addActionListener(this);						
			menu.add(toggle);
			menu.addSeparator();
			
			while(windows.hasNext())
			{
				ManagedWindow window = (ManagedWindow)windows.next();
				JMenuItem item = new JMenuItem(window.getTitle());
				item.setEnabled(false);
				menu.add(item);	
			}
			
			this.add(menu);
		}
		
		//if no window, show a dumb item
		if(this.getItemCount() == 0)
		{
			JMenuItem dumb = new JMenuItem(tr("mnu.none"));
			dumb.setEnabled(false);
			this.add(dumb);				
		}
	}

	public void actionPerformed(ActionEvent ae) 
	{
		JMenuItem toggle = (JMenuItem)ae.getSource();
		if(toggle.getText().equals(tr("mnu.hide")))
		{
			Plugin plugin = (Plugin)toggleButtons.get(toggle);
			hiddenPlugins.add(plugin);
			hideWindowsFor(plugin);
		}
		else
		{
			Plugin plugin = (Plugin)toggleButtons.get(toggle);
			hiddenPlugins.remove(plugin);
			showWindowsFor(plugin);
		}
	}

	private void hideWindowsFor(Plugin plugin) 
	{
		Iterator windows = Client.getInstance().getWindowManager().getWindowsFor(plugin);
		while(windows.hasNext())
		{
			ManagedWindow window = (ManagedWindow)windows.next();
			window.hide();	
		}
	}
	
	private void showWindowsFor(Plugin plugin) 
	{
		Iterator windows = Client.getInstance().getWindowManager().getWindowsFor(plugin);
		while(windows.hasNext())
		{
			ManagedWindow window = (ManagedWindow)windows.next();
			window.show();	
		}
	}	
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}