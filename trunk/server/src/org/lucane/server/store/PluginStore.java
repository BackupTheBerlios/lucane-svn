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
package org.lucane.server.store;

import java.util.*;

import org.lucane.server.*;
import org.lucane.common.concepts.*;

/**
 * PluginManager abstraction
 */
public abstract class PluginStore
{
	/**
	 * Store a plugin concept.
	 * 
	 * @param plugin the PluginConcept to store
	 */
	public abstract void storePlugin(PluginConcept plugin) throws Exception;

	/**
	 * Update a plugin concept.
	 * 
	 * @param plugin the PluginConcept to update
	 */
	public abstract void updatePlugin(PluginConcept plugin) throws Exception;

	/**
	 * Remove a plugin from the store.
	 * 
	 * @param plugin the PluginConcept to remove
	 */
	public abstract void removePlugin(PluginConcept plugin) throws Exception;

	/**
	 * Retrieve a plugin by its name
	 * 
	 * @param name the plugin to fetch
	 * @return the corresponding PluginConcept or null
	 */
	public abstract PluginConcept getPlugin(String name) throws Exception;

	/**
	 * Get all plugins
	 * 
	 * @return an iterator listing all plugins
	 */
	public abstract Iterator getAllPlugins() throws Exception;
	
	/**
	 * Get the plugins a user can use
	 * 
	 * @param user the UserConcept
	 * @return the list of authorized plugins
	 */
	public Iterator getAuthorizedPlugins(UserConcept user)
    throws Exception
	{
		ArrayList authorizedPlugins = new ArrayList();
		
        UserStore us = Server.getInstance().getStore().getUserStore();
        Iterator groups = us.getAllUserGroups(user);
        while(groups.hasNext())
        {
        	GroupConcept group = (GroupConcept)groups.next();
        	Iterator plugins = group.getPlugins();
        	while(plugins.hasNext())
        	{
        		PluginConcept plugin = (PluginConcept)plugins.next();
        		if(! authorizedPlugins.contains(plugin))
        			authorizedPlugins.add(plugin);
        	}
        }
                
		return authorizedPlugins.iterator();
	}
	
	/**
	 * Check if a user can use a plugin
	 *  
	 * @param user the UserConcept
	 * @param plugin the PluginConcept
	 * @return true if the user has access to the plugin, false instead.
	 */
	public boolean isAuthorizedPlugin(UserConcept user, PluginConcept plugin)
	throws Exception
	{
		Iterator i = getAuthorizedPlugins(user);
		while(i.hasNext())
		{
			Object o = i.next();
				
			if(plugin.equals(o))
				return true;
		}
        
		return false;
	}
	
	/**
	 * Get all users that can use a plugin
	 *  
	 * @param plugin the PluginConcept
	 * @return the list of users that have access to this plugin
	 */
	public Iterator getUsersFor(PluginConcept plugin)
	throws Exception
	{
		ArrayList users = new ArrayList();

		UserStore us = Server.getInstance().getStore().getUserStore();
		Iterator i = us.getAllUsers();
		while(i.hasNext())
		{
			UserConcept user = (UserConcept)i.next();
			if(isAuthorizedPlugin(user, plugin))
						users.add(user);
		}		
		
		return users.iterator();
	}
}