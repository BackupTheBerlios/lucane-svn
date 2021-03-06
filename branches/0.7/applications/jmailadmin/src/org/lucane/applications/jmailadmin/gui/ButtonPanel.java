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
import javax.swing.*;

import org.lucane.applications.jmailadmin.JMailAdminPlugin;
import org.lucane.client.Client;

public class ButtonPanel extends JPanel
{
	private JButton btnCopy;
	private JButton btnPaste;
	private JButton btnSave;
	private JButton btnClose;
	
	public ButtonPanel(JMailAdminPlugin plugin, MainFrame listener)
	{
		super(new BorderLayout());
		
		btnCopy = new JButton(plugin.tr("btn.copy"), Client.getIcon("copy.png"));
		btnPaste = new JButton(plugin.tr("btn.paste"), Client.getIcon("paste.png"));
		btnSave = new JButton(plugin.tr("btn.save"), Client.getIcon("ok.png"));
		btnClose = new JButton(plugin.tr("btn.close"), Client.getIcon("cancel.png"));
		btnCopy.addActionListener(listener);
		btnPaste.addActionListener(listener);
		btnSave.addActionListener(listener);
		btnClose.addActionListener(listener);
		
		JPanel container = new JPanel(new GridLayout(1, 2));
		container.add(btnCopy);
		container.add(btnPaste);
		this.add(container, BorderLayout.WEST);
		
		container = new JPanel(new GridLayout(1, 2));
		container.add(btnSave);
		container.add(btnClose);
		this.add(container, BorderLayout.EAST);
	}
}