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

/**
 * ServiceManager abstraction
 */
public abstract class ServiceStore
{	
	/**
	 * Store a service
	 * 
	 * @param service the ServiceConcept to store
	 */	
	public abstract void storeService(ServiceConcept service) throws Exception;
	
	/**
	 * Update a service
	 * 
	 * @param service the ServiceConcept to update
	 */	
	public abstract void updateService(ServiceConcept service) throws Exception;
		
	/**
	 * Remove a service
	 * 
	 * @param service the ServiceConcept to remove
	 */
	public abstract void removeService(ServiceConcept service) throws Exception;

	/**
	 * Get a service by its name
	 * 
	 * @param name the service to fetch
	 * @return the corresponding ServiceConcept or null
	 */
	public abstract ServiceConcept getService(String name) throws Exception;

	/**
	 * Get all services
	 * 
	 * @return an iterator listing all services
	 */
	public abstract Iterator getAllServices() throws Exception;
	
	/**
	 * Get the services that a user can use
	 * 
	 * @param user the UserConcept
	 * @return the list of services this user can access to.
	 */
	public Iterator getAuthorizedServices(UserConcept user)
    throws Exception
	{
		ArrayList authorizedServices = new ArrayList();
	
		UserStore um = Server.getInstance().getStore().getUserStore();
		Iterator groups = um.getAllUserGroups(user);
		while(groups.hasNext())
		{
			GroupConcept group = (GroupConcept)groups.next();			
			Iterator services = group.getServices();			
			while(services.hasNext())
			{				
				ServiceConcept service = (ServiceConcept)services.next();
				if(! authorizedServices.contains(service))
					authorizedServices.add(service);				
			}
		}
            		
		return authorizedServices.iterator();		
	}
	
	/**
	 * Check if a user can use a service
	 *  
	 * @param user the UserConcept
	 * @param service the ServiceConcept
	 * @return true if the user has access to the service, false instead.
	 */
	public boolean isAuthorizedService(UserConcept user, ServiceConcept service)
	throws Exception
	{
		Iterator i = getAuthorizedServices(user);
		while(i.hasNext())
		{
			Object o = i.next();
				
			if(service.equals(o))
				return true;
		}
        
		return false;
	}
	
	/**
	 * Get all users that can use a service
	 *  
	 * @param service the ServiceConcept
	 * @return the list of users that have access to this service
	 */
	public Iterator getUsersFor(ServiceConcept service)
	throws Exception
	{
		ArrayList users = new ArrayList();

		UserStore us = Server.getInstance().getStore().getUserStore();
		Iterator i = us.getAllUsers();
		while(i.hasNext())
		{
			UserConcept user = (UserConcept)i.next();
			if(isAuthorizedService(user, service))
					users.add(user);
		}		
		
		return users.iterator();
	}
}
