/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Jonathan Riboux <jonathan.riboux@wanadoo.Fr>
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

package org.lucane.applications.todolist;

import org.lucane.applications.todolist.gui.MainFrame;
import org.lucane.client.Plugin;
import org.lucane.client.StandalonePlugin;
import org.lucane.common.ConnectInfo;

public class TodolistPlugin extends StandalonePlugin {
	
	private MainFrame frame;
	
	public TodolistPlugin() {
		this.starter = true;
	}

	public Plugin newInstance(ConnectInfo[] friends, boolean starter) {
		return new TodolistPlugin();
	}

	public void start() {
		frame = new MainFrame(this);
		frame.restoreWidgetState();
		frame.show();
	}
	
	public void exit()
	{
		frame.saveWidgetState();
		super.exit();
	}	
}
