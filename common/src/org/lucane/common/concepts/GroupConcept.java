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
 
package org.lucane.common.concepts;

import java.util.*;

public class GroupConcept extends Concept
{
	//links
	private ArrayList parents;
	private ArrayList services;
	private ArrayList plugins;
	private ArrayList users;
	
	public GroupConcept(String name)
	{
		super(name, "");
		this.parents = new ArrayList();
		this.services = new ArrayList();
		this.plugins = new ArrayList();
		this.users = new ArrayList();
	}
	
	
	//-- parents
	
	public void setParents(ArrayList parents)
	{
		this.parents = parents;
	}
	
	public void addParent(GroupConcept group)
	{
		this.parents.add(group);
	}
	
	public Iterator getParents()
	{
		return this.parents.iterator();
	}
	
	public boolean hasParent(GroupConcept group)
	{
		return this.parents.contains(group);
	}
	
	
	//-- services
	
	public void setServices(ArrayList services)
	{
		this.services = services;
	}
	
	public void addService(ServiceConcept service)
	{
		this.services.add(service);
	}
	
	public Iterator getServices()
	{
		return this.services.iterator();
	}
	
	public boolean hasService(ServiceConcept service)
	{
		return this.services.contains(service);
	}
	
	//-- plugins
	
	public void setPlugins(ArrayList plugins)
	{
		this.plugins = plugins;
	}
	
	public void addPlugin(PluginConcept plugin)
	{
		this.plugins.add(plugin);
	}
	
	public Iterator getPlugins()
	{
		return this.plugins.iterator();
	}
	
	public boolean hasPlugin(PluginConcept plugin)
	{
		return this.plugins.contains(plugin);
	}
	
	//-- users
	
	public void setUsers(ArrayList users)
	{
		this.users = users;
	}
	
	public void addUser(UserConcept user)
	{
		this.users.add(user);
	}
	
	public Iterator getUsers()
	{
		return this.users.iterator();
	}
    
    public boolean hasUser(UserConcept user)
    {
        return this.users.contains(user);
    }
    
    //--
    
	public boolean equals(Object o)
	{
		if(o instanceof GroupConcept)
			return this.name.equals(((GroupConcept)o).name);			

		return false;
	}
}