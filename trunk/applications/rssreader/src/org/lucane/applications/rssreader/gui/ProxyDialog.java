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

package org.lucane.applications.rssreader.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.lucane.applications.rssreader.RssReader;
import org.lucane.client.Client;

public class ProxyDialog extends JDialog
implements ActionListener, KeyListener
{
	private JButton btnOk;
	private JButton btnCancel;
	private JCheckBox proxy;
	private JTextField host;
	private JTextField port;
	private RssReader plugin;
	
	public ProxyDialog(RssReader plugin)
	{
		super((JFrame)null, plugin.tr("setupProxy"));
	
		this.plugin = plugin;
		
		btnOk = new JButton(plugin.tr("btn.apply"), Client.getIcon("ok.png"));
		btnOk.addActionListener(this);
		btnCancel = new JButton(plugin.tr("btn.cancel"), Client.getIcon("cancel.png"));
		btnCancel.addActionListener(this);
		
		proxy = new JCheckBox();
		host = new JTextField();
		port = new JTextField();
		proxy.addKeyListener(this);
		host.addKeyListener(this);
		port.addKeyListener(this);
			
		initValues();
		
		JPanel labels = new JPanel(new GridLayout(3, 1));
		labels.add(new JLabel(plugin.tr("lbl.useProxy")));
		labels.add(new JLabel(plugin.tr("lbl.proxyHost")));
		labels.add(new JLabel(plugin.tr("lbl.proxyPort")));

		JPanel texts = new JPanel(new GridLayout(3, 1));
		texts.add(proxy);
		texts.add(host);
		texts.add(port);
		
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(btnOk);
		buttons.add(btnCancel);

		JPanel config = new JPanel(new BorderLayout());
		config.add(labels, BorderLayout.WEST);
		config.add(texts, BorderLayout.CENTER);
		
		JPanel buttonContainer = new JPanel(new BorderLayout());
		buttonContainer.add(buttons, BorderLayout.EAST);
		
		getContentPane().add(config, BorderLayout.CENTER);
		getContentPane().add(buttonContainer, BorderLayout.SOUTH);
		pack();
	}

	private void initValues() 
	{
		String proxySet = plugin.getLocalConfig().get("proxySet", "false");
		String proxyHost = plugin.getLocalConfig().get("proxyHost", "");
		String proxyPort = plugin.getLocalConfig().get("proxyPort", "");
		
		this.proxy.setSelected(proxySet.equals("true"));
		this.host.setText(proxyHost);
		this.port.setText(proxyPort);
	}
	
	private void setValuesAndApply() 
	{
		plugin.getLocalConfig().set("proxySet", String.valueOf(proxy.isSelected()));
		plugin.getLocalConfig().set("proxyHost", host.getText());
		plugin.getLocalConfig().set("proxyPort", port.getText());
		plugin.setProxyFromLocalConfig();
	}
	
	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource().equals(btnOk))
			setValuesAndApply();
		dispose();
	}

	public void keyReleased(KeyEvent ke) {} 
	public void keyTyped(KeyEvent ke) {}
	public void keyPressed(KeyEvent ke) 
	{
		if(ke.getKeyCode() == KeyEvent.VK_ENTER)
			actionPerformed(new ActionEvent(btnOk, 0, null));
		else if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
			actionPerformed(new ActionEvent(btnCancel, 0, null));
	}
}