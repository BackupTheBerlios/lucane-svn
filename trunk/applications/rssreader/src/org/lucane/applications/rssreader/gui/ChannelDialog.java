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
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.client.Client;

public class ChannelDialog extends JDialog
implements ActionListener
{
	private JButton btnAdd;
	private JButton btnCancel;
	private JTextField name;
	private JTextField url;
	private MainFrame main;
	private RssReader plugin;
	
	public ChannelDialog(MainFrame main, RssReader plugin)
	{
		super(main, plugin.tr("addChannel"));
		
		this.main = main;
		this.plugin = plugin;
		
		btnAdd = new JButton(plugin.tr("btn.add"), Client.getIcon("add.png"));
		btnAdd.addActionListener(this);
		btnCancel = new JButton(plugin.tr("btn.cancel"), Client.getIcon("cancel.png"));
		btnCancel.addActionListener(this);
		name = new JTextField();
		url = new JTextField();
		
		JPanel labels = new JPanel(new GridLayout(2, 1));
		labels.add(new JLabel(plugin.tr("lbl.channelName")));
		labels.add(new JLabel(plugin.tr("lbl.channelUrl")));

		JPanel texts = new JPanel(new GridLayout(2, 1));
		texts.add(name);
		texts.add(url);
		
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(btnAdd);
		buttons.add(btnCancel);

		JPanel channel = new JPanel(new BorderLayout());
		channel.add(labels, BorderLayout.WEST);
		channel.add(texts, BorderLayout.CENTER);
		
		JPanel buttonContainer = new JPanel(new BorderLayout());
		buttonContainer.add(buttons, BorderLayout.EAST);
		
		getContentPane().add(channel, BorderLayout.CENTER);
		getContentPane().add(buttonContainer, BorderLayout.SOUTH);
		setSize(400, 100);
	}

	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource().equals(btnAdd))
		{			
			ChannelInfo ci = new ChannelInfo(name.getText(), url.getText());
			plugin.addChannel(ci);
			main.refreshChannelList();
		}
		dispose();
	}
}