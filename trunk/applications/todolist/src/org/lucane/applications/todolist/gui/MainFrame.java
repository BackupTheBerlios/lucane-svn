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
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;
import org.lucane.client.widgets.DialogBox;
import org.lucane.client.widgets.htmleditor.HTMLEditor;

public class MainFrame extends JFrame {
	private JToolBar jtbToolBar;

	private JTable jtTodolists;
	private TodolistTableModel todolistsModel;
	private DefaultListSelectionModel todolistsSelectionModel;
	private JSplitPane jspList;
	private HTMLEditor htmledListDescription;
	private JPanel jpListView;

	private JTable jtTodolistItems;
	private TodolistItemTableModel todolistItemsModel;
	private DefaultListSelectionModel todolistItemSelectionModel;
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
	
	public MainFrame() {
		super("Todo List");
		mainFrame=this;
		init();
	}
	
	private void init(){
		setSize(640,480);
		
		todolistsModel = new TodolistTableModel();
		todolistsSelectionModel = new DefaultListSelectionModel();
		jtTodolists = new JTable(todolistsModel);
		jtTodolists.setSelectionModel(todolistsSelectionModel);
		todolistsSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				refreshTodolistItems();
			}
		});
		htmledListDescription = new HTMLEditor();
		htmledListDescription.setEditable(false);
		htmledListDescription.setToolbarVisible(false);
		jpListView = new JPanel(new BorderLayout());
		jpListView.add(new JLabel("Description :"), BorderLayout.NORTH);
		jpListView.add(htmledListDescription, BorderLayout.CENTER);
		jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jtTodolists), jpListView);
		jspList.setResizeWeight(.70);

		todolistItemsModel = new TodolistItemTableModel();
		todolistItemSelectionModel = new DefaultListSelectionModel();
		jtTodolistItems = new JTable(todolistItemsModel);
		jtTodolistItems.setSelectionModel(todolistItemSelectionModel);
		todolistItemSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				int selectedRow = todolistItemSelectionModel.getMinSelectionIndex();
				if (selectedRow>=0 && todolistItemsModel.getRowCount()>0) {
					TodolistItem tli = (TodolistItem)todolistItemsModel.getValueAt(selectedRow);
					htmledListItemDescription.setText(tli.getDescription());
				} else {
					htmledListItemDescription.clear();
				}
			}
		});

		jpItemView = new JPanel();
		jpItemView.setLayout(new BorderLayout());
		jpItemView.add(new JLabel("Description :"), BorderLayout.NORTH);
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

		JButton jbCreateTodolist = new JButton("Create todolist");
		jbCreateTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				new TodolistDialog(mainFrame).show();
			}
		});
		jtbToolBar.add(jbCreateTodolist);
		
		JButton jbEditTodolist = new JButton("Edit todolist");
		jbEditTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				int selectedRow = todolistsSelectionModel.getMinSelectionIndex();
				if (selectedRow>=0 && todolistsModel.getRowCount()>0) {
					Todolist selectedTodolist = (Todolist) todolistsModel.getValueAt(selectedRow);
					new TodolistDialog(mainFrame, selectedTodolist).show();
				}
			}
		});
		jtbToolBar.add(jbEditTodolist);
		
		JButton jbDeleteTodolist = new JButton("Remove todolist");
		jbDeleteTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selectedRow = todolistsSelectionModel.getMinSelectionIndex();
				if (selectedRow>=0 && todolistsModel.getRowCount()>0) {
					Todolist selectedTodolist = (Todolist) todolistsModel.getValueAt(selectedRow);
					if (DialogBox.question("Delete todolist", "Do you realy want to delete the selected todolist ?"))
						mainFrame.deleteTodolist(selectedTodolist);
				}
			}
		});
		jtbToolBar.add(jbDeleteTodolist);
		
		JButton jbCreateItem = new JButton("New item");
		jbCreateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				int selectedRow = todolistsSelectionModel.getMinSelectionIndex();
				if (selectedRow>=0 && todolistsModel.getRowCount()>0) {
					Todolist selectedTodolist = (Todolist) todolistsModel.getValueAt(selectedRow);
					new TodolistItemDialog(mainFrame, selectedTodolist.getId()).show();
				}
			}
		});
		jtbToolBar.add(jbCreateItem);
		
		JButton jbEditItem = new JButton("Edit item");
		jbEditItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				int selectedRow = todolistItemSelectionModel.getMinSelectionIndex();
				if (selectedRow>=0 && todolistItemsModel.getRowCount()>0) {
					TodolistItem tli = (TodolistItem)todolistItemsModel.getValueAt(selectedRow);
					new TodolistItemDialog(mainFrame, (TodolistItem)todolistItemsModel.getValueAt(selectedRow)).show();
				} else {
					htmledListItemDescription.clear();
				}
			}
		});
		jtbToolBar.add(jbEditItem);
		
		JButton jbDeleteItem = new JButton("Remove item");
		jbDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selectedRow = todolistItemSelectionModel.getMinSelectionIndex();
				if (selectedRow>=0 && todolistItemsModel.getRowCount()>0) {
					if (DialogBox.question("Delete item", "Do you realy want to delete the selected item ?"))
						mainFrame.deleteTodolistItem((TodolistItem)todolistItemsModel.getValueAt(selectedRow));
				}
			}
		});
		jtbToolBar.add(jbDeleteItem);
		
		getContentPane().add(jtbToolBar, BorderLayout.NORTH);
		
		refreshTodolists();
	}
	
	private void refreshTodolists() {
		ArrayList todolists = IO.getInstance().getTodolists();
		todolistsModel.clear();
		Iterator it = todolists.iterator();
		while (it.hasNext())
			todolistsModel.add((Todolist)it.next());
	}

	private void refreshTodolistItems() {
		int selectedRow = todolistsSelectionModel.getMinSelectionIndex();
		if (selectedRow>=0 && todolistsModel.getRowCount()>0) {
			Todolist todolist = (Todolist) todolistsModel.getValueAt(selectedRow); 
			htmledListDescription.setText(todolist.getDescription());

			ArrayList todolistItems = IO.getInstance().getTodolistItems(todolist.getId());
			todolistItemsModel.clear();
			Iterator it = todolistItems.iterator();
			while (it.hasNext())
				todolistItemsModel.add((TodolistItem)it.next());
		}
		else {
			htmledListDescription.clear();
			todolistItemsModel.clear();
		}
	}
	
	protected void addTodolist(Todolist newTodolist) {
		int id = IO.getInstance().addTodolist(newTodolist);
		if (id<0)
			return;
		newTodolist.setId(id);
		todolistsModel.add(newTodolist);
	}

	protected void modifyTodolist(Todolist oldTodolist, Todolist newTodolist) {
		if (!IO.getInstance().modifyTodolist(oldTodolist, newTodolist))
			return;
		todolistsModel.remove(oldTodolist);
		todolistsModel.add(newTodolist);
	}

	protected void deleteTodolist(Todolist defunctTodolist) {
		if (!IO.getInstance().deleteTodolist(defunctTodolist))
			return;
		todolistsModel.remove(defunctTodolist);
	}

	protected void addTodolistItem(TodolistItem newTodolistItem) {
		int id = IO.getInstance().addTodolistItem(newTodolistItem);
		if (id<0)
			return;
		newTodolistItem.setId(id);
		todolistItemsModel.add(newTodolistItem);
	}

	protected void modifyTodolistItem(TodolistItem oldTodolistItem, TodolistItem newTodolistItem) {
		if (!IO.getInstance().modifyTodolistItem(oldTodolistItem, newTodolistItem))
			return;
		todolistItemsModel.remove(oldTodolistItem);
		todolistItemsModel.add(newTodolistItem);
	}

	protected void deleteTodolistItem(TodolistItem defunctTodolistItem) {
		if (!IO.getInstance().deleteTodolistItem(defunctTodolistItem))
			return;
		todolistItemsModel.remove(defunctTodolistItem);
	}
}
