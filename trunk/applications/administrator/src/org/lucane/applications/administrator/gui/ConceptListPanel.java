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
 
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import org.lucane.client.Client;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.concepts.*;
import org.lucane.applications.administrator.AdministratorPlugin;
 
class ConceptListPanel extends JPanel
implements ActionListener
{
	private transient AdministratorPlugin plugin;
	private ConceptPanel panel;
	
	private JButton refresh;
	private JButton create;
	private JComboBox conceptsTypes;
	private ConceptTable concepts;
	
	private String[] types;	
	
	public ConceptListPanel(AdministratorPlugin plugin, ConceptPanel panel)
	{	
		super(new BorderLayout());
		this.plugin = plugin;
		this.panel = panel;
		
		types = new String []{tr("groups"), tr("users"), tr("plugins"), tr("services")};
		
		//-- top widgets
		JPanel top = new JPanel(new BorderLayout());
		JPanel temp = new JPanel(new BorderLayout());
		
		try {
			refresh = new JButton(Client.getIcon("refresh.png"));
		} catch(Exception e) {
			refresh = new JButton("R");
		}
		refresh.setToolTipText(tr("btn.refresh"));
		refresh.addActionListener(this);
		create = new JButton(tr("btn.new"));
		create.addActionListener(this);
		conceptsTypes = new JComboBox(types);
		conceptsTypes.addActionListener(this);
		temp.add(conceptsTypes, BorderLayout.CENTER);
		temp.add(refresh, BorderLayout.EAST);
		top.add(temp, BorderLayout.CENTER);
		top.add(create, BorderLayout.EAST);
			
		//-- main list
		concepts = new ConceptTable(plugin, tr("concepts"));
		this.add(top, BorderLayout.NORTH);	
		this.add(new JScrollPane(concepts), BorderLayout.CENTER);
		
		//-- fetch data
		this.refresh();
	}
	
	public void refresh()
	{
		ArrayList data;
		
		switch(conceptsTypes.getSelectedIndex())
		{
			case 0: 
				data = plugin.getAllGroups(true);
				break;
			case 1:
				data = plugin.getAllUsers(true);
				break;
			case 2:
				data = plugin.getAllPlugins(true);
				break;
			case 3:
				data = plugin.getAllServices(true);
				break;
			default:
				data = plugin.getAllGroups(true);
		}
		
		concepts.setConcepts(data);
	}	

	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource().equals(create))
		{
			String name = null;
			Concept concept = null;
			switch(conceptsTypes.getSelectedIndex())
			{
				case 0: //groups
					name = DialogBox.input(plugin.getTitle(), tr("msg.new.group"));
					concept = new GroupConcept(name); 
					break;
				case 1: //users
					name = DialogBox.input(plugin.getTitle(), tr("msg.new.user"));
					concept = new UserConcept(name, "password", false, "org.lucane.applications.quicklaunch");
					break;
				case 2: //plugins
					name = DialogBox.input(plugin.getTitle(), tr("msg.new.plugin"));
					concept = new PluginConcept(name, "");
					break;
				case 3: //services
					name = DialogBox.input(plugin.getTitle(), tr("msg.new.service"));
					concept = new ServiceConcept(name, false);
					break;
			}
			if(concept != null)
			{
				plugin.storeConcept(concept);
				panel.showConcept(concept);
			}
		}
		
		refresh();
	}

	public void addListSelectionListener(ListSelectionListener lsl)
	{
		concepts.getSelectionModel().addListSelectionListener(lsl);
	}

	public int getSelectedRow() 
	{
		return concepts.getSelectedRow();
	}

	public Concept getConceptAt(int row) 
	{
		return concepts.getConceptAt(row);
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}