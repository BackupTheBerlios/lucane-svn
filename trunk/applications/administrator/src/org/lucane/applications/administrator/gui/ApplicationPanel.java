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

import java.awt.*;
import java.util.Iterator;

import javax.swing.*;

import org.lucane.applications.administrator.AdministratorPlugin;
import org.lucane.client.*;

public class ApplicationPanel extends JPanel
{
	public ApplicationPanel(AdministratorPlugin plugin)
	{
		super(new BorderLayout());
		
		JPanel content = new JPanel(new GridLayout(0, 2));
		PluginLoader ploader = PluginLoader.getInstance();
	
		Iterator plugins = PluginLoader.getInstance().getAvailablePlugins();
		while(plugins.hasNext())
		{
			Plugin p = (Plugin)plugins.next();
			if(p.getCategory().equalsIgnoreCase(plugin.getCategory())
					&& !p.getTitle().equals(plugin.getTitle()))
			{
				content.add(new PluginButton(p));
				content.add(new JLabel(p.getToolTip()));
			}			
		}
		
		this.add(content, BorderLayout.NORTH);
	}
}
