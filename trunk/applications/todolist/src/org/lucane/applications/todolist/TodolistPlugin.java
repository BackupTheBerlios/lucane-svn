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
import org.lucane.applications.todolist.io.IO;
import org.lucane.client.Plugin;
import org.lucane.client.StandalonePlugin;
import org.lucane.common.ConnectInfo;

public class TodolistPlugin extends StandalonePlugin {
	
	public TodolistPlugin() {
		this.starter = true;
	}

	public Plugin init(ConnectInfo[] friends, boolean starter) {
		return new TodolistPlugin();
	}

	public void start() {
		int totolistId = IO.getInstance().addTodolist(new Todolist(IO.getInstance().getUserName(), "liste de test", "description bidon"));
		// TODO find why I must do that (bad classloader ???)
		Class c = TodolistItem.class;
		IO.getInstance().addTodolistItem(new TodolistItem(totolistId, "item de test 1", "description bidon", 0));
		IO.getInstance().addTodolistItem(new TodolistItem(totolistId, "item de test 2", "description bidon", 1));
		IO.getInstance().addTodolistItem(new TodolistItem(totolistId, "item de test 3", "description bidon", 2));
		new MainFrame().show();
	}
}
