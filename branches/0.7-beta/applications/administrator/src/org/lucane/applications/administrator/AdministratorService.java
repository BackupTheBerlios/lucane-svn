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
package org.lucane.applications.administrator;


import java.util.*;

import org.lucane.common.*;
import org.lucane.common.concepts.*;
import org.lucane.server.*;
import org.lucane.server.store.*;


public class AdministratorService
  extends Service
{
	private Store store;
	
  public AdministratorService()
  {
  }

  public void init(Server parent)
  {
	try {
		this.store = parent.getStore();
	} catch(Exception e) {
		e.printStackTrace();
	}
  }


  public void process(ObjectConnection oc, Message message)
  {
		AdminAction aa = (AdminAction)message.getData();
		try {
			switch(aa.action)
			{
				case AdminAction.GET_ALL_GROUPS:
					getAllGroups(oc);
					break;
				case AdminAction.GET_ALL_USERS:
					getAllUsers(oc);
					break;
				case AdminAction.GET_ALL_PLUGINS:
					getAllPlugins(oc);
					break;
				case AdminAction.GET_ALL_SERVICES:
					getAllServices(oc);
					break;
				case AdminAction.GET_AUTHORIZED_PLUGINS:
					getAuthorizedPlugins(oc, (UserConcept)aa.param);
					break;
					
				case AdminAction.STORE:
					store((Concept)aa.param);
					oc.write("OK");
					break;
				case AdminAction.UPDATE:
					update((Concept)aa.param);
					oc.write("OK");
					break;
				case AdminAction.REMOVE:
					remove((Concept)aa.param);
					oc.write("OK");					
					break;
					
				case AdminAction.GET_USERS_FOR:
					getUsersFor(oc, (Concept)aa.param);
					break;
			}
		} catch(Exception e) {
			try {
				oc.write("FAILED " + e);
			} catch(Exception e2) {}
			
			e.printStackTrace();
		}
  }


  //-- get elements
  

  private void getAllGroups(ObjectConnection oc) 
  {
	ArrayList groups = new ArrayList();

	try {
		Iterator i = store.getGroupStore().getAllGroups();
		while(i.hasNext())
			groups.add(i.next());
	} catch(Exception e) {
		e.printStackTrace();
	}
	
	try {
		oc.write("OK");
		oc.write(groups);
	} catch(Exception e) {
		e.printStackTrace();
	}
  }

  private void getAllUsers(ObjectConnection oc) 
  {
	ArrayList users = new ArrayList();

	try {
		Iterator i = store.getUserStore().getAllUsers();
		while(i.hasNext())
			users.add(i.next());
	} catch(Exception e) {
		e.printStackTrace();
	}
		
	try {
		oc.write("OK");
		oc.write(users);
	} catch(Exception e) {
		e.printStackTrace();
	}
  }

  private void getAllPlugins(ObjectConnection oc) 
  {
	ArrayList plugins = new ArrayList();

	try {
		Iterator i = store.getPluginStore().getAllPlugins();
		while(i.hasNext())
			plugins.add(i.next());
	} catch(Exception e) {
		e.printStackTrace();
	}
	
	try {
		oc.write("OK");
		oc.write(plugins);
	} catch(Exception e) {
		e.printStackTrace();
	}
  }
  
  private void getAllServices(ObjectConnection oc) 
  {
	ArrayList services = new ArrayList();

	try {
		Iterator i = store.getServiceStore().getAllServices();
		while(i.hasNext())
			services.add(i.next());
	} catch(Exception e) {
		e.printStackTrace();
	}
	
	try {
		oc.write("OK");		
		oc.write(services);
	} catch(Exception e) {
		e.printStackTrace();
	}
  }
  
  private void getAuthorizedPlugins(ObjectConnection oc, UserConcept user) 
  {
	ArrayList plugins = new ArrayList();

	try {
		Iterator i = store.getPluginStore().getAuthorizedPlugins(user);
		while(i.hasNext())
			plugins.add(i.next());
	} catch(Exception e) {
		e.printStackTrace();
	}
	
	try {
		oc.write("OK");		
		oc.write(plugins);
	} catch(Exception e) {
		e.printStackTrace();
	}  	
  }


  //-- add, update, remove concepts

  private void store(Concept concept) 
  throws Exception
  {
	if(concept instanceof GroupConcept)
		store.getGroupStore().storeGroup((GroupConcept)concept);
	else if(concept instanceof UserConcept)
		store.getUserStore().storeUser((UserConcept)concept);
	else if(concept instanceof PluginConcept)
		store.getPluginStore().storePlugin((PluginConcept)concept);
	else if(concept instanceof ServiceConcept)
		store.getServiceStore().storeService((ServiceConcept)concept);
  }

  private void update(Concept concept) 
  throws Exception
  {
	if(concept instanceof GroupConcept)
		store.getGroupStore().updateGroup((GroupConcept)concept);
	else if(concept instanceof UserConcept)
		store.getUserStore().updateUser((UserConcept)concept);
	else if(concept instanceof PluginConcept)
		store.getPluginStore().updatePlugin((PluginConcept)concept);
	else if(concept instanceof ServiceConcept)
		store.getServiceStore().updateService((ServiceConcept)concept);
  }

  private void remove(Concept concept) 
  throws Exception
  {
	if(concept instanceof GroupConcept)
		store.getGroupStore().removeGroup((GroupConcept)concept);
	else if(concept instanceof UserConcept)
		store.getUserStore().removeUser((UserConcept)concept);
	else if(concept instanceof PluginConcept)
		store.getPluginStore().removePlugin((PluginConcept)concept);
	else if(concept instanceof ServiceConcept)
		store.getServiceStore().removeService((ServiceConcept)concept);  
  }
  
  //-- get users for a particular concept
  
  private void getUsersFor(ObjectConnection oc, Concept concept)
  throws Exception
  {
	Iterator i;
	ArrayList list = new ArrayList();
	
  	if(concept instanceof GroupConcept)
  		i = store.getGroupStore().getUsersFor((GroupConcept)concept);
	else if(concept instanceof PluginConcept)
		i = store.getPluginStore().getUsersFor((PluginConcept)concept);
	else if(concept instanceof ServiceConcept)
		i = store.getServiceStore().getUsersFor((ServiceConcept)concept);
	else
		i = list.iterator();
  	
  
  	while(i.hasNext())
  		list.add(i.next());
  	
	try {
		oc.write("OK");
		oc.write(list);
	} catch(Exception e) {
		e.printStackTrace();
	}  	
  }
}
