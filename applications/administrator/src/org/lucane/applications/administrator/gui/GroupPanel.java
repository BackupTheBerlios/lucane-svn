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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.lucane.applications.administrator.AdministratorPlugin;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.concepts.*;

public class GroupPanel extends  JPanel
implements MouseListener, ActionListener
{
	private transient AdministratorPlugin plugin;
	
	private JTextField groupName;
	private JTextArea groupDescription;
	
	private ConceptTable groupParents;
	private ConceptTable groupChildren;
	private ConceptTable groupUsers;
	private ConceptTable groupPlugins;	
	private ConceptTable groupServices;

	private JButton btnUpdate;
	private JButton btnRemove;
	
	private JButton addParent;
	private JButton removeParent;
	private JButton addUser;
	private JButton removeUser;
	private JButton addService;
	private JButton removeService;
	private JButton addPlugin;
	private JButton removePlugin;

	private ConceptPanel panel;
	
	public GroupPanel(AdministratorPlugin plugin, ConceptPanel panel)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		this.panel = panel;
		
		//-- temp
		JPanel subbuttonsContainer;
		JPanel subbuttons;
		
		//-- attributes
		groupName = new JTextField();
		groupName.setEnabled(false);
		groupDescription = new JTextArea();
		groupParents = new ConceptTable(plugin, tr("parents"));
		groupParents.addMouseListener(this);
		groupChildren = new ConceptTable(plugin, tr("children"));
		groupChildren.addMouseListener(this);
		groupUsers = new ConceptTable(plugin, tr("users"));
		groupUsers.addMouseListener(this);
		groupPlugins = new ConceptTable(plugin, tr("plugins"));
		groupPlugins.addMouseListener(this);
		groupServices = new ConceptTable(plugin, tr("services"));
		groupServices.addMouseListener(this);
		
		//-- name and version	
		JPanel nameAndVersion = new JPanel(new BorderLayout());
		nameAndVersion.add(new JLabel(tr("name")), BorderLayout.WEST);
		nameAndVersion.add(groupName, BorderLayout.CENTER);
		
		//-- description
		JPanel description = new JPanel(new BorderLayout());
		description.add(new JLabel(tr("description")), BorderLayout.NORTH);
		description.add(new JScrollPane(groupDescription), BorderLayout.CENTER);
				
		//-- parents
		JPanel parents = new JPanel(new BorderLayout());
		parents.add(new JLabel(tr("group.parents")), BorderLayout.NORTH);
		parents.add(new JScrollPane(groupParents));	
		subbuttonsContainer = new JPanel(new BorderLayout());
		subbuttons = new JPanel(new GridLayout(2, 1));
		addParent = new JButton(tr("btn.add"));
		addParent.addActionListener(this);
		subbuttons.add(addParent);
		removeParent = new JButton(tr("btn.remove"));
		removeParent.addActionListener(this);
		subbuttons.add(removeParent);
		subbuttonsContainer.add(subbuttons, BorderLayout.NORTH);
		parents.add(subbuttonsContainer, BorderLayout.EAST);
		
		//-- children
		JPanel children = new JPanel(new BorderLayout());
		children.add(new JLabel(tr("group.children")), BorderLayout.NORTH);
		children.add(new JScrollPane(groupChildren));	
		
		//-- users
		JPanel users = new JPanel(new BorderLayout());
		users.add(new JLabel(tr("users")), BorderLayout.NORTH);
		users.add(new JScrollPane(groupUsers));	
		subbuttonsContainer = new JPanel(new BorderLayout());
		subbuttons = new JPanel(new GridLayout(2, 1));
		addUser = new JButton(tr("btn.add"));
		addUser.addActionListener(this);
		subbuttons.add(addUser);
		removeUser = new JButton(tr("btn.remove"));
		removeUser.addActionListener(this);
		subbuttons.add(removeUser);
		subbuttonsContainer.add(subbuttons, BorderLayout.NORTH);
		users.add(subbuttonsContainer, BorderLayout.EAST);
				
		//-- plugins
		JPanel plugins = new JPanel(new BorderLayout());
		plugins.add(new JLabel(tr("plugins")), BorderLayout.NORTH);
		plugins.add(new JScrollPane(groupPlugins));	
		subbuttonsContainer = new JPanel(new BorderLayout());
		subbuttons = new JPanel(new GridLayout(2, 1));
		addPlugin = new JButton(tr("btn.add"));
		addPlugin.addActionListener(this);
		subbuttons.add(addPlugin);
		removePlugin = new JButton(tr("btn.remove"));
		removePlugin.addActionListener(this);
		subbuttons.add(removePlugin);
		subbuttonsContainer.add(subbuttons, BorderLayout.NORTH);
		plugins.add(subbuttonsContainer, BorderLayout.EAST);
				
		//-- services
		JPanel services = new JPanel(new BorderLayout());
		services.add(new JLabel(tr("services")), BorderLayout.NORTH);
		services.add(new JScrollPane(groupServices));
		subbuttonsContainer = new JPanel(new BorderLayout());
		subbuttons = new JPanel(new GridLayout(2, 1));
		addService = new JButton(tr("btn.add"));
		addService.addActionListener(this);
		subbuttons.add(addService);
		removeService = new JButton(tr("btn.remove"));
		removeService.addActionListener(this);
		subbuttons.add(removeService);
		subbuttonsContainer.add(subbuttons, BorderLayout.NORTH);
		services.add(subbuttonsContainer, BorderLayout.EAST);
				
		//-- blocks
		JPanel blocks = new JPanel(new GridLayout(6, 1));
		blocks.add(description);
		blocks.add(parents);
		blocks.add(children);
		blocks.add(users);
		blocks.add(plugins);
		blocks.add(services);

		//buttons
		JPanel buttonsContainer = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		btnRemove = new JButton(tr("btn.remove"));
		btnRemove.addActionListener(this);
		buttons.add(btnRemove);
		btnUpdate = new JButton(tr("btn.save"));
		btnUpdate.addActionListener(this);
		buttons.add(btnUpdate);
		buttonsContainer.add(buttons, BorderLayout.EAST);
		
		//-- complete
		this.add(nameAndVersion, BorderLayout.NORTH);
		this.add(blocks, BorderLayout.CENTER);
		this.add(buttonsContainer, BorderLayout.SOUTH);
	}
	
	public void showConcept(GroupConcept concept)
	{
		groupName.setText(concept.getName());
		groupDescription.setText(concept.getDescription());
		setParents(concept);
		setChildren(concept);
		setUsers(concept);
		setPlugins(concept);
		setServices(concept);
	}		
	
	private void setParents(GroupConcept group)
	{
		ArrayList groups = new ArrayList();
		Iterator parents = group.getParents();
		
		while(parents.hasNext())
		{
			GroupConcept g = (GroupConcept)parents.next();
			groups.add(g);
		}
		
		groupParents.setConcepts(groups);
	}
	
	private void setChildren(GroupConcept group)
	{
		ArrayList groups = new ArrayList();
		Iterator allGroups = plugin.getAllGroups(false).iterator(); 
		
		while(allGroups.hasNext())
		{
			GroupConcept g = (GroupConcept)allGroups.next();
			if(g.hasParent(group))
				groups.add(g);
		}
		
		groupChildren.setConcepts(groups);
	}
	
	private void setUsers(GroupConcept group)
	{
		ArrayList users = new ArrayList();
		Iterator i = group.getUsers();
		
		while(i.hasNext())
			users.add(i.next());
		
		groupUsers.setConcepts(users);
	}
	
	private void setPlugins(GroupConcept group)
	{
		ArrayList plugins = new ArrayList();
		Iterator i = group.getPlugins();
		
		while(i.hasNext())
			plugins.add(i.next());
		
		groupPlugins.setConcepts(plugins);
	}
	
	private void setServices(GroupConcept group)
	{
		ArrayList services = new ArrayList();
		Iterator i = group.getServices();
		
		while(i.hasNext())
			services.add(i.next());
		
		groupServices.setConcepts(services);
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
	
	//-- mouse listener
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) 
	{	
		if(e.getClickCount() < 2)
			return;
		
		ConceptTable table = (ConceptTable)e.getSource();
		int row = table.getSelectedRow();
		if(row < 0)
			return;
		
		panel.showConcept(table.getConceptAt(row));
	}		
	
	//-- action listener
   public void actionPerformed(ActionEvent ae)
   {
      GroupConcept concept = new GroupConcept(groupName.getText());
	   
	   // general buttons
	   if(ae.getSource() == btnUpdate)
	   {
			concept.setDescription(groupDescription.getText());
			concept.setParents(((ConceptTableModel)groupParents.getModel()).getConceptList());
			concept.setUsers(((ConceptTableModel)groupUsers.getModel()).getConceptList());
			concept.setPlugins(((ConceptTableModel)groupPlugins.getModel()).getConceptList());
			concept.setServices(((ConceptTableModel)groupServices.getModel()).getConceptList());
			plugin.updateConcept(concept);
			panel.refresh();			
		}
		else if(ae.getSource() == btnRemove)
		{
			plugin.removeConcept(concept);
			panel.refresh();
			panel.showConcept(null);
		}
		
		// add parent
		else if(ae.getSource() == addParent)
		{		
			Vector list = new Vector();
			ArrayList parents = ((ConceptTableModel)groupParents.getModel()).getConceptList();
			Iterator allGroups = plugin.getAllGroups(false).iterator();
			while(allGroups.hasNext())
			{
				GroupConcept group = (GroupConcept)allGroups.next();
				if(group.getName().equals(groupName.getText()))
					;//zap, same group
				else if(parents.contains(group))
					;//zap, already parent
				else
					list.add(group);
				
			}
			
			if(list.size() > 0)
			{
				int index = DialogBox.list(null, tr("msg.addParent"), tr("msg.selectParentToAdd"), list);
				if(index >= 0)
				{
					GroupConcept group = (GroupConcept)list.elementAt(index);
					((ConceptTableModel)groupParents.getModel()).addConcept(group);
				}
			}
			else
				DialogBox.info(tr("msg.allGroupsAreParents"));
		}
		// remove parent
		else if(ae.getSource() == removeParent)
		{
			int row = groupParents.getSelectedRow();
			((ConceptTableModel)groupParents.getModel()).removeAt(row);
		}
	
		// add user
		else if(ae.getSource() == addUser)
		{		
			Vector list = new Vector();
			ArrayList users = ((ConceptTableModel)groupUsers.getModel()).getConceptList();
			Iterator allUsers = plugin.getAllUsers(false).iterator();
			while(allUsers.hasNext())
			{
				UserConcept user = (UserConcept)allUsers.next();
				if(users.contains(user))
					;//zap, already contained
				else
					list.add(user);
				
			}
			
			if(list.size() > 0)
			{
				int index = DialogBox.list(null, tr("msg.addUser"), tr("msg.selectUserToAdd"), list);
				if(index >= 0)
				{
					UserConcept user = (UserConcept)list.elementAt(index);
					((ConceptTableModel)groupUsers.getModel()).addConcept(user);
				}
			}
			else
				DialogBox.info(tr("msg.allUsersAreContained"));
		}
		// remove user
		else if(ae.getSource() == removeUser)
		{
			int row = groupUsers.getSelectedRow();
			((ConceptTableModel)groupUsers.getModel()).removeAt(row);
		}
	
		// add plugin
		else if(ae.getSource() == addPlugin)
		{		
			Vector list = new Vector();
			ArrayList plugins = ((ConceptTableModel)groupPlugins.getModel()).getConceptList();
			Iterator allPlugins = plugin.getAllPlugins(false).iterator();
			while(allPlugins.hasNext())
			{
				PluginConcept pc = (PluginConcept)allPlugins.next();
				if(plugins.contains(pc))
					;//zap, already contained
				else
					list.add(pc);				
			}
			
			if(list.size() > 0)
			{
				int index = DialogBox.list(null, tr("msg.addPlugin"), tr("selectPluginToAdd"), list);
				if(index >= 0)
				{
					PluginConcept pc = (PluginConcept)list.elementAt(index);
					((ConceptTableModel)groupPlugins.getModel()).addConcept(pc);
				}
			}
			else
				DialogBox.info(tr("msg.allPluginsAreContained"));
		}
		// remove plugin
		else if(ae.getSource() == removePlugin)
		{
			int row = groupPlugins.getSelectedRow();
			((ConceptTableModel)groupPlugins.getModel()).removeAt(row);
		}

		// add service
		else if(ae.getSource() == addService)
		{		
			Vector list = new Vector();
			ArrayList services = ((ConceptTableModel)groupServices.getModel()).getConceptList();
			Iterator allServices = plugin.getAllServices(false).iterator();
			while(allServices.hasNext())
			{
				ServiceConcept service = (ServiceConcept)allServices.next();
				if(services.contains(service))
					;//zap, already contained
				else
					list.add(service);				
			}
			
			if(list.size() > 0)
			{
				int index = DialogBox.list(null, tr("addService"), tr("selectServiceToAdd"), list);
				if(index >= 0)
				{
					ServiceConcept service = (ServiceConcept)list.elementAt(index);
					((ConceptTableModel)groupServices.getModel()).addConcept(service);
				}
			}
			else
				DialogBox.info(tr("msg.allServicesAreContained"));
		}
		// remove service
		else if(ae.getSource() == removeService)
		{
			int row = groupServices.getSelectedRow();
			((ConceptTableModel)groupServices.getModel()).removeAt(row);
		}
   }

   public static void main(String [] args)
   {
	   GroupPanel gp = new GroupPanel(null, null);
	   JFrame jf = new JFrame();
	   jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   jf.getContentPane().add(gp);
	   jf.setSize(800, 500);
	   jf.show();
   }
}