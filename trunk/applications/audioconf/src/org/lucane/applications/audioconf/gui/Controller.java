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

import java.awt.event.*;

import javax.swing.*;

import org.lucane.applications.audioconf.AudioConf;

public class Controller
implements ActionListener, WindowListener
{
	private AudioConf plugin;
	private JFrame controller;
	
	public Controller(AudioConf plugin)
	{
		this.plugin = plugin;
	}
	
	public void showController()
	{
		controller = new JFrame(plugin.getTitle() + " >> "
			+ plugin.getFriendName());
			
		JButton stop = new JButton("stop");
		stop.addActionListener(this);
		
		controller.getContentPane().add(stop);
		controller.addWindowListener(this);
		controller.pack();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				controller.show();
			}
		});
	}
	
	public void hideController()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				controller.dispose();
			}
		});
	}

	public void actionPerformed(ActionEvent ae)
	{
		plugin.stopAndExit();
	}

	public void windowIconified(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowActivated(WindowEvent we) {}
	public void windowDeactivated(WindowEvent we) {}
	public void windowOpened(WindowEvent we) {}
	public void windowClosing(WindowEvent we) {}
	public void windowClosed(WindowEvent we)
	{
		plugin.stopAndExit();
	}
}