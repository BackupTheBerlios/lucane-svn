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
import org.lucane.common.concepts.*;
import org.lucane.server.Server;

/**
 * GroupManager abstraction.
 */
public abstract class GroupStore
{
  /**
   * Does the store has any data ?
   * 
   * @return true if the store is already created
   */	
  public abstract boolean isInitialized() throws Exception;	
	
  /**
   * Store a group.
   * 
   * @param group the GroupConcept to store
   */	
  public abstract void storeGroup(GroupConcept group) throws Exception;
  
  /**
   * Store a group.
   * 
   * @param group the GroupConcept to store
   */	
  public abstract void updateGroup(GroupConcept group) throws Exception;
    
  /**
   * Remove a group
   *
   * @param group the GroupConcept to remove
   */
  public abstract void removeGroup(GroupConcept group) throws Exception;
  
  /**
   * Fetch a group
   * 
   * @param name the name of the group to fetch
   * @return the GroupConcept or null if no group of this name exists.
   */
  public abstract GroupConcept getGroup(String name) throws Exception;
  
  /**
   * Fetch all groups
   * 
   * @return an iterator listing all groups available.
   */
  public abstract Iterator getAllGroups() throws Exception;
  
  /**
   * Get all users that are in a group or in a child group
   *  
   * @param group the GroupConcept
   * @return the list of users that are in this group (or in a child)
   */
  public Iterator getUsersFor(GroupConcept group)
  throws Exception
  {
	  ArrayList list = new ArrayList();

	  UserStore us = Server.getInstance().getStore().getUserStore();
	  Iterator users = us.getAllUsers();
	  while(users.hasNext())
	  {
		  UserConcept user = (UserConcept)users.next();
		  Iterator groups = us.getAllUserGroups(user);
		  while(groups.hasNext())
		  {
		  	if(group.equals(groups.next()))
		  	{
		  		list.add(user);
		  		break;
		  	}
		  }
	  }		
		
	  return list.iterator();
  }
}