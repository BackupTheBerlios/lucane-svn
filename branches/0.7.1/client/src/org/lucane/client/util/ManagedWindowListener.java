/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.lucane.client.widgets.ManagedWindow;

/**
 * Used to call dispose on a managed window when the real frame is closed
 */
public class ManagedWindowListener extends WindowAdapter
{
	private ManagedWindow window;
	
	/**
	 * Constructor
	 * 
	 * @param window the ManagedWindow
	 */
	public ManagedWindowListener(ManagedWindow window)
	{
		this.window = window;
	}
	
	/**
	 * Call window.dispose()
	 */
	public void windowClosing(WindowEvent e)
	{		
		window.dispose();
	}
}