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
package org.lucane.applications.audioconf.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.lucane.applications.audioconf.AcceptationThread;
import org.lucane.applications.audioconf.AudioConf;
import org.lucane.client.Client;

public class ConfigDialog extends JDialog
implements ActionListener
{
	private AudioConf plugin;
	
	private AudioConfigPanel config;
	private JButton btnOk;
	private JButton btnCancel;
	
	public ConfigDialog(AudioConf plugin)
	{
		this.setTitle(plugin.getTitle());
	
		this.plugin = plugin;
		this.config = new AudioConfigPanel(plugin);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.config, BorderLayout.CENTER);
		this.initButtons();
		this.pack();
	}
	
	private void initButtons()
	{
		this.btnOk = new JButton(tr("btn.ok"), Client.getIcon("ok.png"));
		this.btnCancel = new JButton(tr("btn.cancel"), Client.getIcon("cancel.png"));
		
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(btnOk);
		buttons.add(btnCancel);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(buttons, BorderLayout.EAST);
		
		this.getContentPane().add(container, BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource().equals(btnOk))
			new AcceptationThread(plugin, this.config.getAudioConfig()).start();
		else if(e.getSource().equals(btnCancel))
			plugin.exit();

		this.dispose();		
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}