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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;
import org.lucane.client.widgets.DialogBox;
import org.lucane.client.widgets.htmleditor.HTMLEditor;

public class MainFrame extends JFrame {
	private JToolBar jtbToolBar;

	private JList jlTodolists;
	private DefaultListModel todolistsModel;
	private JSplitPane jspList;
	private HTMLEditor htmledListDescription;
	private JPanel jpListView;

	private JList jlTodolistItems;
	private DefaultListModel todolistItemsModel;
	private JSplitPane jspItem;
	private JLabel jlListItemName;
	private JLabel jlListItemPriority;
	private JLabel jlListItemComplete;
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
		
		todolistsModel = new DefaultListModel();
		jlTodolists = new JList(todolistsModel);
		jlTodolists.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				refreshTodolistItems();
				if (jlTodolists.getSelectedIndex()>=0)
					htmledListDescription.setText(((Todolist)jlTodolists.getSelectedValue()).getDescription());
			}
		});
		htmledListDescription = new HTMLEditor();
		htmledListDescription.setEditable(false);
		htmledListDescription.setToolbarVisible(false);
		jpListView = new JPanel(new BorderLayout());
		jpListView.add(new JLabel("Description :"), BorderLayout.NORTH);
		jpListView.add(htmledListDescription, BorderLayout.CENTER);
		jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jlTodolists), jpListView);
		jspList.setResizeWeight(.80);

		todolistItemsModel = new DefaultListModel();
		jlTodolistItems = new JList(todolistItemsModel);
		jlTodolistItems.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				if (jlTodolistItems.getSelectedIndex()>=0) {
					TodolistItem tli = (TodolistItem)jlTodolistItems.getSelectedValue();
					jlListItemName.setText(tli.getName());
					htmledListItemDescription.setText(tli.getDescription());
					jlListItemPriority.setText(""+tli.getPriority());
					jlListItemComplete.setText(""+tli.isComplete());
				}
			}
		});
		jpItemView = new JPanel();

		jpItemView.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets=new Insets(2,2,2,2);
		c.anchor=GridBagConstraints.NORTH;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridy=0;
		c.gridx=0;
		c.weightx=0;
		c.weighty=0;
		jpItemView.add(new JLabel("Name :"), c);
		c.gridx=1;
		c.weightx=1;
		jlListItemName = new JLabel();
		jpItemView.add(jlListItemName, c);

		c.gridy=1;
		c.gridx=0;
		c.weightx=0;
		c.weighty=1;
		jpItemView.add(new JLabel("Description :"), c);
		c.fill=GridBagConstraints.BOTH;
		c.gridx=1;
		c.weightx=1;
		htmledListItemDescription = new HTMLEditor();
		htmledListItemDescription.setEditable(false);
		htmledListItemDescription.setToolbarVisible(false);
		htmledListItemDescription.setBorder(BorderFactory.createEmptyBorder());
		jpItemView.add(htmledListItemDescription, c);

		c.gridy=2;
		c.gridx=0;
		c.weightx=0;
		c.weighty=0;
		jpItemView.add(new JLabel("Priority :"), c);
		c.gridx=1;
		c.weightx=1;
		jlListItemPriority = new JLabel();
		jpItemView.add(jlListItemPriority, c);

		c.gridy=3;
		c.gridx=0;
		c.weightx=0;
		c.weighty=0;
		jpItemView.add(new JLabel("Complete :"), c);
		c.gridx=1;
		c.weightx=1;
		jlListItemComplete = new JLabel();
		jpItemView.add(jlListItemComplete, c);

		jspItem = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jlTodolistItems), jpItemView);
		jspItem.setResizeWeight(.60);

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
				Todolist selectedTodolist = (Todolist) jlTodolists.getSelectedValue();
				if (selectedTodolist!=null)
					new TodolistDialog(mainFrame, selectedTodolist).show();
			}
		});
		jtbToolBar.add(jbEditTodolist);
		
		JButton jbDeleteTodolist = new JButton("Remove todolist");
		jbDeleteTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Todolist selectedTodolist = (Todolist) jlTodolists.getSelectedValue();
				if (selectedTodolist!=null)
					if (DialogBox.question("Delete todolist", "Do you realy want to delete the selected todolist ?"))
						mainFrame.deleteTodolist(selectedTodolist);
			}
		});
		jtbToolBar.add(jbDeleteTodolist);
		
		JButton jbCreateItem = new JButton("New item");
		jbCreateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				Todolist selectedTodolist = (Todolist) jlTodolists.getSelectedValue();
				if (selectedTodolist!=null)
					new TodolistItemDialog(mainFrame, selectedTodolist.getId()).show();
			}
		});
		jtbToolBar.add(jbCreateItem);
		
		JButton jbEditItem = new JButton("Edit item");
		jbEditItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// I use the mainFrame object because in that case this is the action listener
				TodolistItem selectedTodolistItem = (TodolistItem) jlTodolistItems.getSelectedValue();
				if (selectedTodolistItem!=null)
					new TodolistItemDialog(mainFrame, selectedTodolistItem).show();
			}
		});
		jtbToolBar.add(jbEditItem);
		
		JButton jbDeleteItem = new JButton("Remove item");
		jbDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				TodolistItem selectedTodolistItem = (TodolistItem) jlTodolistItems.getSelectedValue();
				if (selectedTodolistItem!=null)
					if (DialogBox.question("Delete item", "Do you realy want to delete the selected item ?"))
						mainFrame.deleteTodolistItem(selectedTodolistItem);
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
			todolistsModel.addElement(it.next());
	}

	private void refreshTodolistItems() {
		Todolist todolist = (Todolist)jlTodolists.getSelectedValue();
		if (todolist!=null) {
			ArrayList todolistItems = IO.getInstance().getTodolistItems(todolist.getId());
			todolistItemsModel.clear();
			Iterator it = todolistItems.iterator();
			while (it.hasNext())
				todolistItemsModel.addElement(it.next());
		}
	}
	
	protected void addTodolist(Todolist newTodolist) {
		int id = IO.getInstance().addTodolist(newTodolist);
		if (id<0)
			return;
		newTodolist.setId(id);
		todolistsModel.addElement(newTodolist);
	}

	protected void modifyTodolist(Todolist oldTodolist, Todolist newTodolist) {
		if (!IO.getInstance().modifyTodolist(oldTodolist, newTodolist))
			return;
		todolistsModel.removeElement(oldTodolist);
		todolistsModel.addElement(newTodolist);
	}

	protected void deleteTodolist(Todolist defunctTodolist) {
		if (!IO.getInstance().deleteTodolist(defunctTodolist))
			return;
		todolistsModel.removeElement(defunctTodolist);
	}

	protected void addTodolistItem(TodolistItem newTodolistItem) {
		int id = IO.getInstance().addTodolistItem(newTodolistItem);
		if (id<0)
			return;
		newTodolistItem.setId(id);
		todolistItemsModel.addElement(newTodolistItem);
	}

	protected void modifyTodolistItem(TodolistItem oldTodolistItem, TodolistItem newTodolistItem) {
		if (!IO.getInstance().modifyTodolistItem(oldTodolistItem, newTodolistItem))
			return;
		todolistItemsModel.removeElement(oldTodolistItem);
		todolistItemsModel.addElement(newTodolistItem);
	}

	protected void deleteTodolistItem(TodolistItem defunctTodolistItem) {
		if (!IO.getInstance().deleteTodolistItem(defunctTodolistItem))
			return;
		todolistItemsModel.removeElement(defunctTodolistItem);
	}
}
