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

public class PluginPanel extends  JPanel
implements MouseListener, ActionListener
{
	private transient AdministratorPlugin plugin;
	
	private JTextField pluginName;
	private JTextField pluginVersion;
	private JTextArea pluginDescription;
		
	private JButton btnUpdate;
	private JButton btnRemove;
	
	private ConceptTable pluginGroups;
	private ConceptPanel panel;
	
	public PluginPanel(AdministratorPlugin plugin, ConceptPanel panel)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		this.panel = panel;
		
		//-- attributes
		pluginName = new JTextField();
		pluginName.setEnabled(false);
		pluginVersion = new JTextField();
		pluginDescription = new JTextArea();
		pluginGroups = new ConceptTable(plugin, tr("groups"));
		pluginGroups.addMouseListener(this);
		
		//-- name and version
		JPanel labels = new JPanel(new GridLayout(2,1));
		labels.add(new JLabel(tr("name")));
		labels.add(new JLabel(tr("plugin.version")));

		JPanel fields = new JPanel(new GridLayout(2, 1));
		fields.add(pluginName);
		fields.add(pluginVersion);
		
		JPanel nameAndVersion = new JPanel(new BorderLayout());
		nameAndVersion.add(labels, BorderLayout.WEST);
		nameAndVersion.add(fields, BorderLayout.CENTER);
		
		//-- description
		JPanel description = new JPanel(new BorderLayout());
		description.add(new JLabel(tr("description")), BorderLayout.NORTH);
		description.add(new JScrollPane(pluginDescription), BorderLayout.CENTER);
				
		//-- groups
		JPanel groups = new JPanel(new BorderLayout());
		groups.add(new JLabel(tr("groups")), BorderLayout.NORTH);
		groups.add(new JScrollPane(pluginGroups));	
		
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
		this.add(nameAndVersion, BorderLayout.NORTH);
		this.add(descriptionAndGroups, BorderLayout.CENTER);
		this.add(buttonsContainer, BorderLayout.SOUTH);
	}
	
	public void showConcept(PluginConcept concept)
	{
		pluginName.setText(concept.getName());
		pluginVersion.setText(concept.getVersion());
		pluginDescription.setText(concept.getDescription());
		setGroups(concept);
	}		
	
	public void setGroups(PluginConcept plugin)
	{
		ArrayList groups = new ArrayList();
		Iterator allGroups = this.plugin.getAllGroups(false).iterator();
		
		while(allGroups.hasNext())
		{
			GroupConcept group = (GroupConcept)allGroups.next();
			if(group.hasPlugin(plugin))
				groups.add(group);
		}
		
		pluginGroups.setConcepts(groups);
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
		
		int row = pluginGroups.getSelectedRow();
		if(row < 0)
			return;
		
		panel.showConcept(pluginGroups.getConceptAt(row));
	}
	
	//-- action listener
   public void actionPerformed(ActionEvent ae)
   {
	   PluginConcept concept = new PluginConcept(pluginName.getText(), pluginVersion.getText());
	   concept.setDescription(pluginDescription.getText());
	   
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

   public static void main(String [] args)
   {
	   PluginPanel pp = new PluginPanel(null, null);
	   JFrame jf = new JFrame();
	   jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   jf.getContentPane().add(pp);
	   jf.setSize(800, 500);
	   jf.show();
   }
}