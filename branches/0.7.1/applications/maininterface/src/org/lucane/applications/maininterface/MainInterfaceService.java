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

import org.lucane.common.*;
import org.lucane.common.concepts.*;
import org.lucane.server.*;
import org.lucane.server.store.Store;

import java.io.IOException;
import java.util.*;

public class MainInterfaceService
extends Service
{
  private Store store = null;

  public MainInterfaceService()
  {
  }

  public void init(Server parent)
  {
	  store = parent.getStore();
  }

  public void process(ObjectConnection oc, Message message)
  {  	
  	MainInterfaceAction mia = (MainInterfaceAction)message.getData();
  	String user = message.getSender().getName();
  	ArrayList result = null;

	switch(mia.action)
	{
		case MainInterfaceAction.GET_MY_GROUPS:
			result = getMyGroups(user);
			break;
		case MainInterfaceAction.GET_CONNECTED_USERS_FOR_GROUP:
			result = getConnectedUsersForGroup(mia.param);
			break;
	}
  		
	try {
		oc.write(result);
	} catch (IOException e) {
		//nothing can be done here
		Logging.getLogger().warning("unable to send result");
	}			
  }
  
  private ArrayList getMyGroups(String login) 
  {
  	ArrayList groups = new ArrayList();

  	try {
  		UserConcept user = store.getUserStore().getUser(login);
  		Iterator i = store.getUserStore().getAllUserGroups(user);
  		while(i.hasNext())
  		{
  			GroupConcept group = (GroupConcept)i.next();
  			groups.add(group.getName());
  		}
  	} catch(Exception e) {
  		e.printStackTrace();
  	}
  	
  	return groups;
  }
  
  private ArrayList getConnectedUsersForGroup(String group) 
  {
  	ArrayList users = new ArrayList();

  	try {
  		Iterator i = store.getGroupStore().getUsersFor(new GroupConcept(group));
  		while(i.hasNext())
  		{	
  			UserConcept user = (UserConcept)i.next();
  			if(ConnectInfoManager.getInstance().isConnected(user.getName()))
  				users.add(user.getName());
  		}
  	} catch(Exception e) {
  		e.printStackTrace();
  	}
  	
  	return users;
  }
}

