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

import org.lucane.client.Plugin;

class PluginUse implements Comparable
{
	private Plugin plugin;
	private int use;
	
	public PluginUse(Plugin plugin, String use)
	{
		this.plugin = plugin;
		if(use != null)
			this.use = Integer.parseInt(use);
		else
			this.use = 0;
	}
	
	public int compareTo(Object other)
	{
		if(other instanceof PluginUse)
			return this.use - ((PluginUse)other).use;
		
		throw new IllegalArgumentException("other must be an instance of PluginUse");
	}

	public Plugin getPlugin()
	{
		return this.plugin;
	}
}