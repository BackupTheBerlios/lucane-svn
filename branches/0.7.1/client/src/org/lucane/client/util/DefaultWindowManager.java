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

import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.lucane.client.Plugin;
import org.lucane.client.widgets.ManagedWindow;

public class DefaultWindowManager implements WindowManager
{
	private HashMap windows = new HashMap();
	
	private void init(ManagedWindow window)
	{
		JFrame f = new JFrame();
		windows.put(window, f);
		f.addWindowListener(new ManagedWindowListener(window));
		
		f.setName(window.getName());
		f.setContentPane(window.getContentPane());
		f.setIconImage(window.getIconImage());
		f.setJMenuBar(window.getJMenuBar());
		f.setTitle(window.getTitle());
		f.setResizable(window.isResizeable());
		
		if(window.getPreferredSize() == null)
			f.pack();
		else
			f.setSize(window.getPreferredSize());
		
		if(!window.discardWidgetState())
			WidgetState.restore(window.getOwner().getLocalConfig(), f);
		
		//window listener
		Iterator listeners = window.getWindowListeners();
		while(listeners.hasNext())
			f.addWindowListener((WindowListener)listeners.next());		
		
		//key listener
		listeners = window.getKeyListeners();
		while(listeners.hasNext())
			f.addKeyListener((KeyListener)listeners.next());		
	}
	
	public void show(ManagedWindow window)
	{
		if(!windows.containsKey(window))
			init(window);
		
		final JFrame f = (JFrame)windows.get(window); 
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
		
		if(!window.discardWidgetState())
			WidgetState.save(window.getOwner().getLocalConfig(), f);
		
		windows.remove(window);
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
		ArrayList list = new ArrayList();
		Iterator windows = getAllWindows();
		while(windows.hasNext())
		{
			ManagedWindow win = (ManagedWindow)windows.next();
			if(win.getOwner() == plugin)
				list.add(win);
		}
		
		return list.iterator();
	}
	
	public void propertyChange(PropertyChangeEvent pce)
	{
		//TODO handle these events
	}
}
