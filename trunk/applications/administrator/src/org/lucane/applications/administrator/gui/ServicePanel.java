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
import org.lucane.common.concepts.*;

public class ServicePanel extends  JPanel
implements MouseListener, ActionListener
{
	private transient AdministratorPlugin plugin;
	
	private JTextField serviceName;
	private JCheckBox serviceInstalled;
	private JTextArea serviceDescription;

	private JButton btnUpdate;
	private JButton btnRemove;
	
	private ConceptTable serviceGroups;

	private ConceptPanel panel;
	
	public ServicePanel(AdministratorPlugin plugin, ConceptPanel panel)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		this.panel = panel;
		
		//-- attributes
		serviceName = new JTextField();
		serviceName.setEnabled(false);
		serviceInstalled = new JCheckBox();
		serviceDescription = new JTextArea();
		serviceGroups = new ConceptTable(plugin, tr("groups"));
		serviceGroups.addMouseListener(this);
		
		//-- name and installed
		JPanel labels = new JPanel(new GridLayout(2,1));
		labels.add(new JLabel(tr("name")));
		labels.add(new JLabel(tr("installed")));

		JPanel fields = new JPanel(new GridLayout(2, 1));
		fields.add(serviceName);
		fields.add(serviceInstalled);
		
		JPanel nameAndInstalled = new JPanel(new BorderLayout());
		nameAndInstalled.add(labels, BorderLayout.WEST);
		nameAndInstalled.add(fields, BorderLayout.CENTER);
		
		//-- description
		JPanel description = new JPanel(new BorderLayout());
		description.add(new JLabel(tr("description")), BorderLayout.NORTH);
		description.add(new JScrollPane(serviceDescription), BorderLayout.CENTER);
				
		//-- groups
		JPanel groups = new JPanel(new BorderLayout());
		groups.add(new JLabel(tr("groups")), BorderLayout.NORTH);
		groups.add(new JScrollPane(serviceGroups));	
		
		//-- description and groups
		JPanel descriptionAndGroups = new JPanel(new GridLayout(2, 1));
		descriptionAndGroups.add(description);
		descriptionAndGroups.add(groups);

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
		this.add(nameAndInstalled, BorderLayout.NORTH);
		this.add(descriptionAndGroups, BorderLayout.CENTER);
		this.add(buttonsContainer, BorderLayout.SOUTH);
	}
	
	public void showConcept(ServiceConcept concept)
	{
		serviceName.setText(concept.getName());
		serviceInstalled.setSelected(concept.isInstalled());
		serviceDescription.setText(concept.getDescription());
		setGroups(concept);
	}		
	
	public void setGroups(ServiceConcept service)
	{
		ArrayList groups = new ArrayList();
		Iterator allGroups = plugin.getAllGroups(false).iterator();
		
		while(allGroups.hasNext())
		{
			GroupConcept group = (GroupConcept)allGroups.next();
			if(group.hasService(service))
				groups.add(group);
		}
		
		serviceGroups.setConcepts(groups);
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
		
		 int row = serviceGroups.getSelectedRow();
		 if(row < 0)
		 	return;
		
		  panel.showConcept(serviceGroups.getConceptAt(row));
	  }
	  
	 //-- action listener
	public void actionPerformed(ActionEvent ae)
	{
		ServiceConcept concept = new ServiceConcept(serviceName.getText(), serviceInstalled.isSelected());
		if(ae.getSource() == btnUpdate)
		{
			plugin.updateConcept(concept);
			panel.refresh();			
		}
		else if(ae.getSource() == btnRemove)
		{
			plugin.removeConcept(concept);
			panel.refresh();
			panel.showConcept(null);
		}
	}
}