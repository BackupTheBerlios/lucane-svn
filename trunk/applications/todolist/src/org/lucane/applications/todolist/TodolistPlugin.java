package org.lucane.applications.todolist;

import org.lucane.applications.todolist.gui.MainFrame;
import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
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
		IO.getInstance().addTodolist(new Todolist(IO.getInstance().getUserName(), "liste de test", "description bidon"));
		// TODO find why I must do that (bad classloader ???)
		Class c = TodolistItem.class;
		IO.getInstance().addTodolistItem(new TodolistItem(IO.getInstance().getUserName(), "liste de test", "item de test 1", "description bidon", 0));
		IO.getInstance().addTodolistItem(new TodolistItem(IO.getInstance().getUserName(), "liste de test", "item de test 2", "description bidon", 1));
		IO.getInstance().addTodolistItem(new TodolistItem(IO.getInstance().getUserName(), "liste de test", "item de test 3", "description bidon", 2));
		new MainFrame().show();
	}
}
