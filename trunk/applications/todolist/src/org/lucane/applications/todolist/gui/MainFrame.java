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

package org.lucane.applications.todolist.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;
import org.lucane.client.Plugin;
import org.lucane.client.widgets.DialogBox;
import org.lucane.client.widgets.htmleditor.HTMLEditor;

public class MainFrame extends JFrame {
	private JToolBar jtbToolBar;

	private TodolistTable jtTodolists;
	private JSplitPane jspList;
	private HTMLEditor htmledListDescription;
	private JPanel jpListView;

	private TodolistItemTable jtTodolistItems;
	private JSplitPane jspItem;
	private HTMLEditor htmledListItemDescription;
	private JPanel jpItemView;
	
	private JSplitPane jspMain;

	private JButton jbCreateTodolist;
	private JButton jbEditTodolist;
	private JButton jbDeleteTodolist;
	private JButton jbCreateItem;
	private JButton jbEditItem;
	private JButton jbDeleteItem;
	
	private MainFrame mainFrame;

	private Plugin plugin;
	
	public MainFrame(Plugin plugin) {
		super();
		this.plugin=plugin;
		setTitle(plugin.tr("MainInterface.title"));
		mainFrame=this;
		TodolistItemTableModel.setColumnsNames(new String[] {plugin.tr("MainInterface.items.name"), plugin.tr("MainInterface.items.priority"), plugin.tr("MainInterface.items.complete")});
		TodolistTableModel.setColumnsNames(new String[] {plugin.tr("MainInterface.lists.name")});
		TodolistItem.setPriorityLabels(new String[] {plugin.tr("TodoListItem.priority.low"), plugin.tr("TodoListItem.priority.medium"), plugin.tr("TodoListItem.priority.high")});
		TodolistItem.setCompleteLabels(new String[] {plugin.tr("TodoListItem.complete.false"), plugin.tr("TodoListItem.complete.true")});
		init();
	}
	
	private void init(){
		setSize(640,480);
		
		jtTodolists = new TodolistTable();
		jtTodolists.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				refreshTodolistItems();
			}
		});
		htmledListDescription = new HTMLEditor();
		htmledListDescription.setEditable(false);
		htmledListDescription.setToolbarVisible(false);
		jpListView = new JPanel(new BorderLayout());
		jpListView.add(new JLabel(plugin.tr("MainInterface.decription")), BorderLayout.NORTH);
		jpListView.add(htmledListDescription, BorderLayout.CENTER);
		jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jtTodolists), jpListView);
		jspList.setResizeWeight(.70);

		jtTodolistItems = new TodolistItemTable();

		jtTodolistItems.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				TodolistItem tli = jtTodolistItems.getSelectedTodolistItem();
				if (tli!=null) {
					htmledListItemDescription.setText(tli.getDescription());
				} else {
					htmledListItemDescription.clear();
				}
			}
		});

		jpItemView = new JPanel();
		jpItemView.setLayout(new BorderLayout());
		jpItemView.add(new JLabel(plugin.tr("MainInterface.decription")), BorderLayout.NORTH);
		htmledListItemDescription = new HTMLEditor();
		htmledListItemDescription.setEditable(false);
		htmledListItemDescription.setToolbarVisible(false);
		jpItemView.add(htmledListItemDescription, BorderLayout.CENTER);

		jspItem = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jtTodolistItems), jpItemView);
		jspItem.setResizeWeight(.70);

		jspMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jspList, jspItem);
		jspMain.setResizeWeight(.20);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(jspMain, BorderLayout.CENTER);

		jtbToolBar = new JToolBar();

		JButton jbCreateTodolist = new JButton();
		jbCreateTodolist.setToolTipText(plugin.tr("MainInterface.createList"));
		try {
			jbCreateTodolist.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "create_list.png")));
		} catch (MalformedURLException e) {
		}

		JButton jbDeleteTodolist = new JButton();
		jbDeleteTodolist.setToolTipText(plugin.tr("MainInterface.deleteList"));
		try {
			jbDeleteTodolist.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "delete_list.png")));
		} catch (MalformedURLException e) {
		}

		JButton jbEditTodolist = new JButton();
		jbEditTodolist.setToolTipText(plugin.tr("MainInterface.editList"));
		try {
			jbEditTodolist.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "edit_list.png")));
		} catch (MalformedURLException e) {
		}

		JButton jbCreateItem = new JButton();
		jbCreateItem.setToolTipText(plugin.tr("MainInterface.createItem"));
		try {
			jbCreateItem.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "create_item.png")));
		} catch (MalformedURLException e) {
		}

		JButton jbDeleteItem = new JButton();
		jbDeleteItem.setToolTipText(plugin.tr("MainInterface.deleteItem"));
		try {
			jbDeleteItem.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "delete_item.png")));
		} catch (MalformedURLException e) {
		}

		JButton jbEditItem = new JButton();
		jbEditItem.setToolTipText(plugin.tr("MainInterface.editItem"));
		try {
			jbEditItem.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "edit_item.png")));
		} catch (MalformedURLException e) {
		}

		jbCreateTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				new TodolistDialog(plugin, mainFrame).show();
			}
		});
		jtbToolBar.add(jbCreateTodolist);
		
		jbDeleteTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Todolist tl = jtTodolists.getSelectedTodolist();
				if (tl!=null) {
					if (DialogBox.question(plugin.tr("MainInterface.confirmDeleteListDialog.title"), plugin.tr("MainInterface.confirmDeleteListDialog.question")))
						mainFrame.deleteTodolist(tl);
				}
			}
		});
		jtbToolBar.add(jbDeleteTodolist);

		jbEditTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				Todolist tl = jtTodolists.getSelectedTodolist();
				if (tl!=null) {
					new TodolistDialog(plugin, mainFrame, tl).show();
				}
			}
		});
		jtbToolBar.add(jbEditTodolist);
		
		jtbToolBar.add(new JToolBar.Separator());
		
		jbCreateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				Todolist tl = jtTodolists.getSelectedTodolist();
				if (tl!=null) {
					new TodolistItemDialog(plugin, mainFrame, tl.getId()).show();
				}
			}
		});
		jtbToolBar.add(jbCreateItem);
		
		jbDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				TodolistItem tli = jtTodolistItems.getSelectedTodolistItem(); 
				if (tli!=null) {
					if (DialogBox.question(plugin.tr("MainInterface.confirmDeleteItemDialog.title"), plugin.tr("MainInterface.confirmDeleteItemDialog.question")))
						mainFrame.deleteTodolistItem(tli);
				}
			}
		});
		jtbToolBar.add(jbDeleteItem);
		
		jbEditItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				TodolistItem tli = (TodolistItem)jtTodolistItems.getSelectedTodolistItem();
				if (tli!=null) {
					new TodolistItemDialog(plugin, mainFrame, tli).show();
				} else {
					htmledListItemDescription.clear();
				}
			}
		});
		jtbToolBar.add(jbEditItem);
		
		getContentPane().add(jtbToolBar, BorderLayout.NORTH);
		
		refreshTodolists();
	}
	
	private void refreshTodolists() {
		ArrayList todolists = IO.getInstance(plugin).getTodolists();
		jtTodolists.clear();
		Iterator it = todolists.iterator();
		while (it.hasNext())
			jtTodolists.add((Todolist)it.next());
	}

	private void refreshTodolistItems() {
		Todolist tl = jtTodolists.getSelectedTodolist();
		if (tl!=null) {
			htmledListDescription.setText(tl.getDescription());

			ArrayList todolistItems = IO.getInstance(plugin).getTodolistItems(tl.getId());
			jtTodolistItems.clear();
			Iterator it = todolistItems.iterator();
			while (it.hasNext())
				jtTodolistItems.add((TodolistItem)it.next());
		}
		else {
			htmledListDescription.clear();
			jtTodolistItems.clear();
		}
	}
	
	protected void addTodolist(Todolist newTodolist) {
		int id = IO.getInstance(plugin).addTodolist(newTodolist);
		if (id<0)
			return;
		newTodolist.setId(id);
		jtTodolists.add(newTodolist);
	}

	protected void modifyTodolist(Todolist oldTodolist, Todolist newTodolist) {
		if (!IO.getInstance(plugin).modifyTodolist(oldTodolist, newTodolist))
			return;
		jtTodolists.remove(oldTodolist);
		jtTodolists.add(newTodolist);
	}

	protected void deleteTodolist(Todolist defunctTodolist) {
		if (!IO.getInstance(plugin).deleteTodolist(defunctTodolist))
			return;
		jtTodolists.remove(defunctTodolist);
	}

	protected void addTodolistItem(TodolistItem newTodolistItem) {
		int id = IO.getInstance(plugin).addTodolistItem(newTodolistItem);
		if (id<0)
			return;
		newTodolistItem.setId(id);
		jtTodolistItems.add(newTodolistItem);
	}

	protected void modifyTodolistItem(TodolistItem oldTodolistItem, TodolistItem newTodolistItem) {
		if (!IO.getInstance(plugin).modifyTodolistItem(oldTodolistItem, newTodolistItem))
			return;
		jtTodolistItems.remove(oldTodolistItem);
		jtTodolistItems.add(newTodolistItem);
	}

	protected void deleteTodolistItem(TodolistItem defunctTodolistItem) {
		if (!IO.getInstance(plugin).deleteTodolistItem(defunctTodolistItem))
			return;
		jtTodolistItems.remove(defunctTodolistItem);
	}
}

