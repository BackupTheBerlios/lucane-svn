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

import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.lucane.client.Plugin;
import org.lucane.client.widgets.ManagedWindow;

public class DefaultWindowManager implements WindowManager
{
	private HashMap windows = new HashMap();
	
	public void show(ManagedWindow window)
	{
		final JFrame f = new JFrame();
		f.setName(window.getName());
		f.setContentPane(window.getContentPane());
		f.setIconImage(window.getIconImage());
		f.setJMenuBar(window.getJMenuBar());
		f.setSize(window.getPreferredSize());
		f.setTitle(window.getTitle());
		f.setResizable(window.isResizeable());
		if(window.getPreferredSize() == null)
			f.pack();
		
		if(window.mustExitPluginOnClose())
			f.addWindowListener(new PluginExitWindowListener(window.getOwner()));

		WidgetState.restore(window.getOwner().getLocalConfig(), f);
		windows.put(window, f);
		
		Iterator listeners = window.getWindowListeners();
		while(listeners.hasNext())
			f.addWindowListener((WindowListener)listeners.next());
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.show();
			}	
		});
	}
	
	public void hide(ManagedWindow window)
	{
		final JFrame f = (JFrame)windows.get(window);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.hide();
			}	
		});	
	}
	
	public void dispose(ManagedWindow window)
	{
		JFrame f = (JFrame)windows.get(window);
		WidgetState.save(window.getOwner().getLocalConfig(), f);
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