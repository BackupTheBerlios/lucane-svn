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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.lucane.client.Communicator;
import org.lucane.client.widgets.*;
import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.applications.administrator.AdministratorPlugin;
import org.lucane.common.*;
import org.lucane.common.concepts.*;

public class MessagePanel extends JPanel
implements ActionListener
{
	private static final String QUICKMESSAGE = "org.lucane.applications.quickmessage";
	
	private transient AdministratorPlugin plugin;

	private HTMLEditor message;

	private JButton sendToAll;
	private JButton sendToGroup;
	private JButton sendToPlugin;
	private JButton sendToService;

	private JComboBox comboGroup;
	private JComboBox comboPlugin;
	private JComboBox comboService;

	public MessagePanel(AdministratorPlugin plugin)
	{
		super(new BorderLayout());
		this.plugin = plugin;

		this.message = new HTMLEditor();
		this.sendToAll = new JButton(tr("send"));
		this.sendToAll.addActionListener(this);
		this.sendToGroup = new JButton(tr("send"));
		this.sendToGroup.addActionListener(this);
		this.sendToPlugin = new JButton(tr("send"));
		this.sendToPlugin.addActionListener(this);
		this.sendToService = new JButton(tr("send"));
		this.sendToService.addActionListener(this);
		
		this.comboGroup = new JComboBox();
		this.comboPlugin = new JComboBox();
		this.comboService = new JComboBox();

		comboGroup.setModel(new DefaultComboBoxModel(plugin.getAllGroups(false).toArray()));
		comboPlugin.setModel(new DefaultComboBoxModel(plugin.getAllPlugins(false).toArray()));
		comboService.setModel(new DefaultComboBoxModel(plugin.getAllServices(false).toArray()));
	
		initLayout();	
	}

	private void initLayout()
	{
		JPanel actions = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(1, 1, 1, 1);

		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.weightx = 1;
		constraints.gridwidth = 2;
		actions.add(new JLabel(tr("send.toAll")), constraints);
		constraints.gridwidth = 1;
		constraints.gridx = 2;
		constraints.weightx = 0.2;
		actions.add(sendToAll, constraints);
		constraints.gridy++;
		constraints.gridx = 0;
		constraints.weightx = 1;
		actions.add(new JLabel(tr("send.toGroup")), constraints);
		constraints.gridx = 1;
		actions.add(comboGroup, constraints);
		constraints.gridx = 2;
		constraints.weightx = 0.2;
		actions.add(sendToGroup, constraints);
		constraints.gridy++;
		constraints.gridx = 0;
		constraints.weightx = 1;
		actions.add(new JLabel(tr("send.toPlugin")), constraints);
		constraints.gridx = 1;
		actions.add(comboPlugin, constraints);
		constraints.gridx = 2;
		constraints.weightx = 0.2;
		actions.add(sendToPlugin, constraints);
		constraints.gridy++;
		constraints.gridx = 0;
		constraints.weightx = 1;
		actions.add(new JLabel(tr("send.toService")), constraints);
		constraints.gridx = 1;
		actions.add(comboService, constraints);
		constraints.gridx = 2;
		constraints.weightx = 0.2;
		actions.add(sendToService, constraints);

		add(message, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals(sendToAll))
		{
			ArrayList users = plugin.getAllConnectedUsers();
			sendToUsers(users);
		}
		else if(ae.getSource().equals(sendToGroup))
		{
			GroupConcept concept = (GroupConcept)comboGroup.getSelectedItem();
			ArrayList users = plugin.getConnectedUsersForConcept(concept);
			sendToUsers(users);
		}
		else if(ae.getSource().equals(sendToPlugin))
		{
			PluginConcept concept = (PluginConcept)comboPlugin.getSelectedItem();
			ArrayList users = plugin.getConnectedUsersForConcept(concept);
			sendToUsers(users);
		}
		else if(ae.getSource().equals(sendToService))
		{
			ServiceConcept concept = (ServiceConcept)comboService.getSelectedItem();
			ArrayList users = plugin.getConnectedUsersForConcept(concept);
			sendToUsers(users);
		}
	}

	private void sendToUsers(ArrayList users)
	{
		Logging.getLogger().info("sending message to " + users);
		Iterator i = users.iterator();
		Communicator com = Communicator.getInstance();
		while(i.hasNext())
		{	
			ConnectInfo ci = com.getConnectInfo((String)i.next());
			ObjectConnection sc = com.sendMessageTo(ci, QUICKMESSAGE, message.getText());
		}
		DialogBox.info(tr("msg.sentToUsers", users.size()));
	}

	private String tr(String s, int i)
	{
		return tr(s).replaceAll("%1", ""+ i);
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}