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

import org.lucane.server.*;
import org.lucane.common.concepts.*;

import java.util.*;

public abstract class UserStore
{
	/**
	 * Store a user
	 * 
	 * @param user the UserConcept to store
	 */
	public abstract void storeUser(UserConcept user) throws Exception;
	
	/**
	 * Update a user
	 * 
	 * @param user the UserConcept to update
	 */
	public abstract void updateUser(UserConcept user) throws Exception;

	/**
	 * Remove a user
	 * 
	 * @param user the UserConcept to remove from the store
	 */
	public abstract void removeUser(UserConcept user) throws Exception;

	/**
	 * Fetch a user by its login
	 * 
	 * @param login the user to fetch
	 * @return the corresponding user or null
	 */
	public abstract UserConcept getUser(String login) throws Exception;

	/**
	 * Fetch all users
	 * 
	 * @return an iterator listing all users
	 */
	public abstract Iterator getAllUsers() throws Exception;
	
	/**
	 * Check wether a password is correct
	 * 
	 * @param user the UserConcept
	 * @param passwd the password to check
	 * @return true if the password is correct.
	 */
	public boolean checkUserPassword(UserConcept user, String passwd)
	{
		try  {
			return user.getPassword().equals(passwd);
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * Get all groups where a user is contained
	 * 
	 * @param user the user
	 * @return an iterator listing all groups a user is in
	 */
	public Iterator getAllUserGroups(UserConcept user)
	throws Exception
	{
		ArrayList userGroups = new ArrayList();
		GroupStore gm = Server.getInstance().getStore().getGroupStore();
		
		Iterator groups = gm.getAllGroups();
		while(groups.hasNext())
		{
			GroupConcept group = (GroupConcept)groups.next();
			if(group.hasUser(user))
				recurseGroups(group, userGroups);
		}
		
		return userGroups.iterator();
	}
	
	/**
	 * Recurse parents of a group to add them to the list
	 *  
	 * @param group the root group
	 * @param groups the list of groups
	 */
	private void recurseGroups(GroupConcept group, ArrayList groups)
	{
		if(group == null || groups.contains(group))
			return;
		
		groups.add(group);
		Iterator i = group.getParents();

		while(i.hasNext())
			recurseGroups((GroupConcept)i.next(), groups);
	}
}
