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

import javax.swing.JFrame;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;
import org.lucane.common.concepts.*;
import org.lucane.applications.administrator.gui.*;

public class AdministratorPlugin extends StandalonePlugin
{
	private ConnectInfo service;
	private ArrayList allGroups = null;
	private ArrayList allUsers = null;
	private ArrayList allPlugins = null;
	private ArrayList allServices = null;
	
	
	//-- from Plugin
	public AdministratorPlugin()
	{
		this.starter = true;
	}
	
	
	public Plugin init(ConnectInfo[] friends, boolean starter)
	{
		return new AdministratorPlugin();
	}

	public void start()
	{
		service = Communicator.getInstance().getConnectInfo("org.lucane.applications.administrator");
		
		JFrame jf = new JFrame("Admin plugin");
		jf.addWindowListener(this);
		jf.getContentPane().add(new MainPanel(this));
		jf.setSize(800, 550);
		jf.setIconImage(this.getImageIcon().getImage());
		jf.show();
	}
	
	//-- get cached concepts
	
	public ArrayList getAllGroups(boolean fetch)
	{
		if(fetch || allGroups == null)
			fetchAllGroups();
		
		return allGroups;
	}
	
	public ArrayList getAllUsers(boolean fetch)
	{
		if(fetch || allUsers == null)
			fetchAllUsers();
		
		return allUsers;
	}
	
	public ArrayList getAllPlugins(boolean fetch)
	{
		if(fetch || allPlugins == null)
			fetchAllPlugins();
		
		return allPlugins;
	}
	public ArrayList getAllServices(boolean fetch)
	{
		if(fetch || allServices == null)
			fetchAllServices();
		
		return allServices;
	}
	
	// -- really fetch concepts
	
	private void fetchAllGroups()
	{
		try {
			AdminAction action = new AdminAction(AdminAction.GET_ALL_GROUPS);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.fetchAllGroups"));

			allGroups = (ArrayList)oc.read();

			oc.close();
		} catch(Exception e) {
			allGroups = new ArrayList();
			DialogBox.error(tr("err") + e);
		}		
	}
	
	private void fetchAllUsers()
	{
		try {
			AdminAction action = new AdminAction(AdminAction.GET_ALL_USERS);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.fetchAllUsers"));

			allUsers = (ArrayList)oc.read();

			oc.close();
		} catch(Exception e) {
			allUsers = new ArrayList();
			DialogBox.error(tr("err") + e);
		}
	}
	
	private void fetchAllPlugins()
	{
		try {
			AdminAction action = new AdminAction(AdminAction.GET_ALL_PLUGINS);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.fetchAllPlugins"));

			allPlugins = (ArrayList)oc.read();

			oc.close();
		} catch(Exception e) {
			allPlugins = new ArrayList();
			DialogBox.error(tr("err") + e);
		}
	}
	
	private void fetchAllServices()
	{
		try {
			AdminAction action = new AdminAction(AdminAction.GET_ALL_SERVICES);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.fetchAllServices"));

			allServices = (ArrayList)oc.read();

			oc.close();
		} catch(Exception e) {
			allServices = new ArrayList();
			DialogBox.error(tr("err") + e);
		}		
	}
	
	//-- 
	
	public ArrayList getAuthorizedPlugins(UserConcept user)
	{
		ArrayList list = null;
		
		try {
			AdminAction action = new AdminAction(AdminAction.GET_AUTHORIZED_PLUGINS, user);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.getAuthorizedPluginsFor") + user);

			list = (ArrayList)oc.read();

			oc.close();
		} catch(Exception e) {
			list = new ArrayList();
			DialogBox.error(tr("err") + e);
		}		
		
		return list;
	}
	
	//-- update concept storage
	
	public void storeConcept(Concept concept)
	{
		try {
			AdminAction action = new AdminAction(AdminAction.STORE, concept);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.storeConcept") + concept);		

			oc.close();
		} catch(Exception e) {
			DialogBox.error(tr("err") + e);
		}		
	}
	
	public void updateConcept(Concept concept)
	{
		if(updateIsSafe(concept))
		{
			try {
				AdminAction action = new AdminAction(AdminAction.UPDATE, concept);
				ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
				String ack = oc.readString();
				if(ack.startsWith("FAILED"))
					DialogBox.error(tr("err.updateConcept") + concept);
				else
					DialogBox.info(tr("msg.conceptUpdated") + concept);
	
				oc.close();
			} catch(Exception e) {
				DialogBox.error(tr("err") + e);
			}
		}		
	}
	
	public void removeConcept(Concept concept)
	{
		if(removeIsSafe(concept))
		{
			try {
				AdminAction action = new AdminAction(AdminAction.REMOVE, concept);
				ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
				String ack = oc.readString();
				if(ack.startsWith("FAILED"))
					DialogBox.error(tr("err.removeConcept") + concept);
	
				oc.close();				
			} catch(Exception e) {
				DialogBox.error(tr("err") + e);
			}
		}				
	}

	//-- fetch users
	
	public ArrayList getAllConnectedUsers() 
	{
		Vector v = Client.getInstance().getUserList();
		return new ArrayList(v);
	}

	public ArrayList getConnectedUsersForConcept(Concept concept) 
	{
		ArrayList list = new ArrayList();

		try {
			AdminAction action = new AdminAction(AdminAction.GET_USERS_FOR, concept);
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, service.getName(), action);
			String ack = oc.readString();
			if(ack.startsWith("FAILED"))
				DialogBox.error(tr("err.getUsersForConcept") + concept);
			else
			{
				Vector v = Client.getInstance().getUserList();
				ArrayList users = (ArrayList)oc.read();
				Iterator i = users.iterator();
				while(i.hasNext())
				{
					UserConcept user = (UserConcept)i.next();
					if(isConnected(user))
						list.add(user.getName());
				}
			}

			oc.close();				
		} catch(Exception e) {
			DialogBox.error(tr("err") + e);
			e.printStackTrace();
		}				
		return list;
	}
	
	/**
	 * Check if a user is connected
	 * 
	 * @param user the UserConcept
	 * @return true if connected
	 */
	private boolean isConnected(UserConcept user)
	{
		Vector v = Client.getInstance().getUserList();
		return v.contains(user.getName());
	}
	
	/**
	 * Removing plugins or users from admin concept is dangerous
	 */
	private boolean updateIsSafe(Concept concept)
	{
		//admin group
		if(concept.getName().equals("Admins") && concept instanceof GroupConcept)
		{
			GroupConcept group = (GroupConcept)concept;
			
			//basic applications
			if(!group.hasPlugin(new PluginConcept("org.lucane.application.quicklaunch", "version")))
				return askForConfirmation(tr("dangerous.update"));
			if(!group.hasPlugin(new PluginConcept("org.lucane.application.maininterface", "version")))
				return askForConfirmation(tr("dangerous.update"));
			if(!group.hasPlugin(new PluginConcept("org.lucane.application.administrator", "version")))
				return askForConfirmation(tr("dangerous.update"));
			if(!group.hasService(new ServiceConcept("org.lucane.application.administrator", false)))
				return askForConfirmation(tr("dangerous.update"));
				
			//at least one user
			if(!group.getUsers().hasNext())
				return askForConfirmation(tr("dangerous.update"));
		}

		//admin user
		if(concept.getName().equals("admin") && concept instanceof UserConcept)
			return askForConfirmation(tr("dangerous.update"));
			
		return true;
	}
	
	/**
	 * Removing a plugin might break everything
	 */
	private boolean removeIsSafe(Concept concept)
	{
		String[] dangerous = {
			"Admins", //group
			"admin",  //user
			"org.lucane.applications.maininterface",
			"org.lucane.applications.quicklaunch",
			"org.lucane.applications.administrator",
		};
		
		for(int i=0;i<dangerous.length;i++)
		{
			if(dangerous[i].equals(concept.getName()))
				return askForConfirmation(tr("dangerous.remove"));
		}
		
		return true;
	}
	
	/**
	 * Ask the user before screwing everything
	 */
	private boolean askForConfirmation(String message)
	{
		return DialogBox.question(getTitle(), message);
	}
}