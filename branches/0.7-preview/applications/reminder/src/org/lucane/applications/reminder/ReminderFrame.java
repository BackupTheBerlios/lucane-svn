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

package org.lucane.applications.reminder;

import javax.swing.*;

import org.lucane.client.*;
import org.lucane.client.widgets.htmleditor.HTMLEditor;

import java.awt.event.*;
import java.awt.*;

public class ReminderFrame extends JFrame
implements ActionListener
{
	private ReminderInfos infos;
	private JButton btnPlugin;
	private JButton btnClose;
	
	public ReminderFrame(ReminderPlugin plugin, ReminderInfos infos)
	{
		super(plugin.tr("reminder") + " " + infos.getTitle());
		this.infos = infos;
		getContentPane().setLayout(new BorderLayout());
		
		HTMLEditor content = new HTMLEditor();
		content.setText(infos.getMessage());
		content.setEditable(false);
		content.setToolbarVisible(false);
		getContentPane().add(content, BorderLayout.CENTER);
		
		Plugin p = PluginLoader.getInstance().getPlugin(infos.getPlugin());		
		btnPlugin = new JButton(p.getTitle(), p.getImageIcon());
		btnClose = new JButton(plugin.tr("close"), Client.getIcon("cancel.png"));
		btnPlugin.addActionListener(this);
		btnClose.addActionListener(this);
		
		JPanel container = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(2, 1));
		buttons.add(btnPlugin);
		buttons.add(btnClose);
		container.add(buttons, BorderLayout.NORTH);
		getContentPane().add(container, BorderLayout.EAST);
		this.setIconImage(p.getImageIcon().getImage());
		setSize(450, 200);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == btnPlugin)
			PluginLoader.getInstance().run(infos.getPlugin(), null);
		dispose();
	}
}