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

import javax.swing.*;

import org.lucane.applications.jmailadmin.Account;
import org.lucane.applications.jmailadmin.JMailAdminPlugin;

import java.awt.*;

public class AccountPanel extends JPanel
{
	private JMailAdminPlugin plugin;
	
	private Account account;

	private JTextField address;
	private JComboBox type;
	private JTextField inHost;
	private JTextField inPort;
	private JTextField outHost;
	private JTextField outPort;
	private JTextField login;
	private JPasswordField password;

	public AccountPanel(JMailAdminPlugin plugin)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		
		this.address = new JTextField();
		this.type = new JComboBox();
		this.type.addItem("imap");
		
		//-- we don't want pop
		//this.type.addItem("pop3");
		
//		this.type.addActionListener(this);
		this.inHost = new JTextField();
		this.inPort = new JTextField("143");
		this.outHost = new JTextField();
		this.outPort = new JTextField("25");
		this.login = new JTextField();
		this.password = new JPasswordField();
		
		JPanel labels = new JPanel(new GridLayout(8, 1));
		labels.add(new JLabel(tr("address")));
		labels.add(new JLabel(tr("type")));
		labels.add(new JLabel(tr("inHost")));
		labels.add(new JLabel(tr("inPort")));
		labels.add(new JLabel(tr("outHost")));
		labels.add(new JLabel(tr("outPort")));
		labels.add(new JLabel(tr("login")));
		labels.add(new JLabel(tr("password")));

		JPanel fields = new JPanel(new GridLayout(8, 1));
		fields.add(this.address);
		fields.add(this.type);
		fields.add(this.inHost);
		fields.add(this.inPort);
		fields.add(this.outHost);
		fields.add(this.outPort);
		fields.add(this.login);
		fields.add(this.password);
	
		this.add(labels, BorderLayout.WEST);
		this.add(fields, BorderLayout.CENTER);
	}
	
	public void setAccount(Account a)
	{
		if(a == null)
			return;

		this.account = a;
		
		this.address.setText(a.address);
		this.type.setSelectedItem(a.type);
		this.inHost.setText(a.inHost);
		this.inPort.setText(""+a.inPort);
		this.outHost.setText(a.outHost);
		this.outPort.setText(""+a.outPort);
		this.login.setText(a.login);
		this.password.setText(a.password);
	}

	public Account getAccount()
	{
		int inPort = Integer.parseInt(this.inPort.getText());
		int outPort = Integer.parseInt(this.outPort.getText());

		return new Account(
			account.user,
			address.getText(),
			(String)type.getSelectedItem(),
			inHost.getText(),
			inPort,
			outHost.getText(),
			outPort,
			login.getText(),
			new String(password.getPassword()));
	}

	private String tr(String s)
	{
		return plugin.tr(s);
	}
}