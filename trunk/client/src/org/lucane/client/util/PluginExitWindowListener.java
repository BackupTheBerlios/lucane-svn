/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.client.util;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

import org.lucane.client.Plugin;

/**
 * Useful to exit() a plugin properly. Must be used as WindowListener
 * for graphical plugins.
 */
public class PluginExitWindowListener extends WindowAdapter
{
	private Plugin plugin;
	
	/**
	 * Constructor
	 * 
	 * @param plugin the plugin to exit on window closing
	 */
	public PluginExitWindowListener(Plugin plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * Call plugin.exit();
	 */
	public void windowClosing(WindowEvent e)
	{
		WidgetState.save(plugin.getLocalConfig(), (JFrame)e.getSource());
		plugin.exit();
	}
}