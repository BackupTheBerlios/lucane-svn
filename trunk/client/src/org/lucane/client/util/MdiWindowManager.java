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

package org.lucane.client.util;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.lucane.client.Plugin;
import org.lucane.client.widgets.ManagedWindow;

public class MdiWindowManager implements WindowManager
{
	private HashMap windows = new HashMap();
	private JDesktopPane desktop = new JDesktopPane();
	private JFrame container = new JFrame();
	
	public MdiWindowManager()
	{
		container.getContentPane().add(desktop, BorderLayout.CENTER);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setSize(800, 600);
	}

	public void show(ManagedWindow window)
	{
		final JInternalFrame f = new JInternalFrame(window.getTitle(), true, true, true, true);
		f.setName(window.getName());
		f.setContentPane(window.getContentPane());
		//f.setIconImage(window.getIconImage());
		f.setJMenuBar(window.getJMenuBar());
		f.setSize(window.getPreferredSize());
		//f.setTitle(window.getTitle());
		f.setResizable(window.isResizeable());
		if(window.getPreferredSize() == null)
			f.pack();
		
		if(window.mustExitPluginOnClose())
		{	
			final Plugin plugin = window.getOwner();
			f.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosing(InternalFrameEvent e) {
					plugin.exit();
				}
			});						
		}
		
		//WidgetState.restore(window.getOwner().getLocalConfig(), f);
		windows.put(window, f);
		
		Iterator listeners = window.getWindowListeners();
		//while(listeners.hasNext())
		//	f.addWindowListener((WindowListener)listeners.next());
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.show();
			}	
		});
		
		desktop.add(f);
		container.show();
	}
	
	public void hide(ManagedWindow window)
	{
		final JInternalFrame f = (JInternalFrame)windows.get(window);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.setVisible(false);
			}	
		});	
	}
	
	public void dispose(ManagedWindow window)
	{
		JInternalFrame f = (JInternalFrame)windows.get(window);
		//WidgetState.save(window.getOwner().getLocalConfig(), f);
		f.dispose();		
		
		if(window.mustExitPluginOnClose())
			window.getOwner().exit();		
	}
	
	public Iterator getAllWindows()
	{
		return windows.keySet().iterator();
	}
	
	public Iterator getWindowsFor(Plugin plugin)
	{
		//TODO code this
		return getAllWindows();
	}

	public void propertyChange(PropertyChangeEvent pce)
	{
		//TODO handle these events
	}
}