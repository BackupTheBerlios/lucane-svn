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
import org.lucane.client.widgets.*;
import org.lucane.common.concepts.*;
import org.lucane.common.*;

public class UserPanel extends  JPanel
implements MouseListener, ActionListener
{
	private transient AdministratorPlugin plugin;
	
	private JTextField userLogin;
	private JPasswordField userPasswd;
	private JCheckBox userLocked;
	private JComboBox userStartupPlugin;
	private JTextArea userDescription;

	private UserConcept oldConcept;
	
	private JButton btnUpdate;
	private JButton btnRemove;
	
	private ConceptTable userGroups;

	private ConceptPanel panel;
	
	public UserPanel(AdministratorPlugin plugin, ConceptPanel panel)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		this.panel = panel;
		
		//-- attributes
		userLogin = new JTextField();
		userLogin.setEnabled(false);
		userPasswd = new JPasswordField();
		userLocked = new JCheckBox();
		userStartupPlugin = new JComboBox();
		userDescription = new JTextArea();
		userGroups = new ConceptTable(plugin, tr("groups"));
		userGroups.addMouseListener(this);
		oldConcept = null;
		
		//-- metadata
		JPanel labels = new JPanel(new GridLayout(4,1));
		labels.add(new JLabel(tr("user.login")));
		labels.add(new JLabel(tr("user.passwd")));
		labels.add(new JLabel(tr("user.locked")));
		labels.add(new JLabel(tr("user.startupPlugin")));


		JPanel fields = new JPanel(new GridLayout(4, 1));
		fields.add(userLogin);
		fields.add(userPasswd);
		fields.add(userLocked);
		fields.add(userStartupPlugin);
		
		JPanel metadata = new JPanel(new BorderLayout());
		metadata.add(labels, BorderLayout.WEST);
		metadata.add(fields, BorderLayout.CENTER);
		
		//-- description
		JPanel description = new JPanel(new BorderLayout());
		description.add(new JLabel(tr("description")), BorderLayout.NORTH);
		description.add(new JScrollPane(userDescription), BorderLayout.CENTER);
				
		//-- groups
		JPanel groups = new JPanel(new BorderLayout());
		groups.add(new JLabel(tr("groups")), BorderLayout.NORTH);
		groups.add(new JScrollPane(userGroups));
		
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
		this.add(metadata, BorderLayout.NORTH);
		this.add(descriptionAndGroups, BorderLayout.CENTER);
		this.add(buttonsContainer, BorderLayout.SOUTH);
	}
	
	public void showConcept(UserConcept concept)
	{
		oldConcept = concept;

		userLogin.setText(concept.getName());
		userPasswd.setText(concept.getPassword());
		userLocked.setSelected(concept.isLocked());

		ArrayList authorizedPlugins = plugin.getAuthorizedPlugins(concept);
		
		//if no plugin available, show all plugins
		//useful for new users
		if(authorizedPlugins.size() == 0)
			authorizedPlugins = plugin.getAllPlugins(false);
		
		ArrayList plugins = new ArrayList();
		Iterator i = authorizedPlugins.iterator();
		
		while(i.hasNext())
		{
			PluginConcept plugin = (PluginConcept)i.next();
			plugins.add(plugin.getName());
		}
		
		
		userStartupPlugin.setModel(new DefaultComboBoxModel(plugins.toArray()));
		userStartupPlugin.setSelectedItem(concept.getStartupPlugin());
		
		userDescription.setText(concept.getDescription());
		setGroups(concept);
	}		
	
	public void setGroups(UserConcept service)
	{
		ArrayList groups = new ArrayList();
		Iterator allGroups = plugin.getAllGroups(false).iterator();
		
		while(allGroups.hasNext())
		{
			GroupConcept group = (GroupConcept)allGroups.next();
			if(group.hasUser(service))
				groups.add(group);
		}
		
		userGroups.setConcepts(groups);
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
		
		int row = userGroups.getSelectedRow();
		if(row < 0)
			return;
		
		panel.showConcept(userGroups.getConceptAt(row));
	}
	
	//-- action listener
	
	public void actionPerformed(ActionEvent ae)
	{
		UserConcept concept = new UserConcept(userLogin.getText(),
			new String(userPasswd.getPassword()), userLocked.isSelected(), 
			(String)userStartupPlugin.getSelectedItem());
		
		concept.setDescription(userDescription.getText());
		
		// password changed
		if(! concept.getPassword().equals(oldConcept.getPassword()))
		{
			try {
				concept.generateKeys(concept.getPassword());
			} catch(Exception e) {
				DialogBox.error(tr("err.generateKeys") + e);
				e.printStackTrace();
			}
			concept.setPassword(MD5.encode(concept.getPassword()));
		}
		else
		{
			concept.setKeys(oldConcept.getPublicKey(), oldConcept.getPrivateKey());
		}
			 
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