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
package org.lucane.applications.jmailadmin.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.lucane.applications.jmailadmin.*;
import org.lucane.common.concepts.UserConcept;

public class MainFrame extends JFrame
implements ActionListener, ListSelectionListener
{
	private JMailAdminPlugin plugin;
	private AccountPanel account;
	
	private Account template;
	
	public MainFrame(JMailAdminPlugin plugin)
	{		
		super(plugin.getTitle());
	
		this.plugin = plugin;	
		this.account = new AccountPanel(plugin);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new UserListPanel(plugin, this), BorderLayout.WEST);
		getContentPane().add(this.account, BorderLayout.CENTER);
		getContentPane().add(new ButtonPanel(plugin, this), BorderLayout.SOUTH);
		setSize(500, 250);
	}

	public void actionPerformed(ActionEvent ae)
	{
		JButton src = (JButton)ae.getSource();
		
		//save & close
		if(src.getText().equals(plugin.tr("btn.save")))
			plugin.storeAccount(this.account.getAccount());
		else if(src.getText().equals(plugin.tr("btn.close")))
		{
			plugin.exit();
			this.dispose();
		}		
		
		//copy paste
		else if(src.getText().equals(plugin.tr("btn.copy")))
			template = account.getAccount();			
		else if(src.getText().equals(plugin.tr("btn.paste")))
			account.applyTemplate(template);
	}

	public void valueChanged(ListSelectionEvent lse)
	{
		JList src = (JList)lse.getSource();		
		UserConcept user = (UserConcept)src.getSelectedValue();
		if(user != null)
			this.account.setAccount(plugin.getAccount(user.getName()));
	}	
}