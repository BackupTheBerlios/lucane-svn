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

package org.lucane.client.widgets;

import java.awt.*;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import org.lucane.client.Client;
import org.lucane.client.Plugin;

 
public class ManagedWindow
{
	private String name;
	private ArrayList propertyListeners = new ArrayList();
	private ArrayList windowListeners = new ArrayList();
	private JRootPane rootPane = new JRootPane();
	private String title = null;
	private Image iconImage = null;
	private boolean resizeable = true; 
	private Dimension maximumSize = null;
	private Dimension minimumSize = null;
	private Dimension preferredSize = null;
	private Plugin owner;
	private boolean exitPluginOnClose = false;
	private boolean discardWidgetState = false;
	
	public ManagedWindow(Plugin owner, String title)
	{
		this.owner = owner;
		this.title = title;
	}

	public Plugin getOwner()
	{
		return this.owner;
	}
	
	public void setDiscardWidgetState(boolean widgetState)
	{
		this.discardWidgetState = widgetState;
	}
	
	public boolean discardWidgetState()
	{
		return discardWidgetState;
	}
	
	public void setExitPluginOnClose(boolean exit)
	{
		this.exitPluginOnClose = exit;
	}
	
	public boolean mustExitPluginOnClose()
	{
		return exitPluginOnClose;
	}

	public void show()
	{
		Client.getInstance().getWindowManager().show(this);
	}
	
	public void hide()
	{
		Client.getInstance().getWindowManager().hide(this);
	}
	
	public void dispose()
	{
		Client.getInstance().getWindowManager().dispose(this);
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}

	public void setMaximumSize(Dimension size)
	{
		this.maximumSize = size;
	}
	
	public Dimension getMaximumSize()
	{
		return maximumSize;
	}
	
	public void setMinimumSize(Dimension size)
	{
		this.minimumSize = size;
	}
	
	public Dimension getMinimumSize()
	{
		return minimumSize;
	}
	
	public void setPreferredSize(Dimension size)
	{
		this.preferredSize = size;
	}

	public Dimension getPreferredSize()
	{
		return preferredSize;
	}
	
	public void addWindowListener(WindowListener listener) 
	{
		this.windowListeners.add(listener);
	}
	
	public Iterator getWindowListeners()
	{
		return this.windowListeners.iterator();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) 
	{
		this.propertyListeners.add(listener);
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public void setIconImage(Image image)
	{
		this.iconImage = image;
	}
	
	public Image getIconImage()
	{
		return this.iconImage;
	}
	
	public void setContentPane(Container container)
	{
		rootPane.setContentPane(container);
	}

	public Container getContentPane()
	{
		return rootPane.getContentPane();
	}
	
	public void setJMenuBar(JMenuBar menu)
	{
		rootPane.setJMenuBar(menu);
	}
	
	public JMenuBar getJMenuBar()
	{
		return rootPane.getJMenuBar();
	}	
	
	public void setResizeable(boolean resizeable)
	{
		this.resizeable = resizeable;
	}
	
	public boolean isResizeable()
	{
		return this.resizeable;
	}
}