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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;
import org.lucane.client.widgets.DialogBox;

public class MainFrame extends JFrame {
	private JToolBar jtbToolBar;

	private JList jlTodolists;
	private JSplitPane jspList;
	private JTextArea jtaListDescription;
	private JPanel jpListView;

	private JList jlTodolistItems;
	private JSplitPane jspItem;
	private JLabel jlListItemName;
	private JLabel jlListItemPriority;
	private JLabel jlListItemComplete;
	private JTextArea jtaListItemDescription;
	private JPanel jpItemView;
	
	private JSplitPane jspMain;

	private JButton jbCreateTodolist;
	private JButton jbEditTodolist;
	private JButton jbDeleteTodolist;
	private JButton jbCreateItem;
	private JButton jbEditItem;
	private JButton jbDeleteItem;
	
	private MainFrame mainFrame;
	
	private HashMap todolistsByName;
	private HashMap todolistItemsByName;

	public MainFrame() {
		super("Todo List");

		// TODO find a better idea for that
		mainFrame=this;
		
		init();
	}
	
	private void init(){
		setSize(640,480);
		
		jlTodolists = new JList();
		jlTodolists.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				refreshTodolistItems();
				if (jlTodolists.getSelectedIndex()>=0)
					jtaListDescription.setText(((Todolist)todolistsByName.get((String)jlTodolists.getSelectedValue())).getDescription());
			}
		});
		jtaListDescription = new JTextArea();
		//jtaListDescription.setBackground(SystemColor.control);
		jtaListDescription.setEditable(false);
		jtaListDescription.setLineWrap(true);
		jtaListDescription.setWrapStyleWord(true);
		jpListView = new JPanel(new BorderLayout());
		jpListView.add(new JLabel("Description :"), BorderLayout.NORTH);
		jpListView.add(new JScrollPane(jtaListDescription), BorderLayout.CENTER);
		jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jlTodolists), jpListView);
		jspList.setResizeWeight(.80);

		//jtTodoListItems = new JTable();
		jlTodolistItems = new JList();
		jlTodolistItems.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				if (jlTodolistItems.getSelectedIndex()>=0) {
					TodolistItem tli = (TodolistItem)todolistItemsByName.get((String)jlTodolistItems.getSelectedValue());
					jlListItemName.setText(tli.getName());
					jtaListItemDescription.setText(tli.getDescription());
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
		jtaListItemDescription = new JTextArea();
		jtaListItemDescription.setEditable(false);
		jtaListItemDescription.setOpaque(false);
		jtaListItemDescription.setBorder(BorderFactory.createEmptyBorder());
		jpItemView.add(new JScrollPane(jtaListItemDescription), c);

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

		//jspItem = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jtTodoListItems), jpItemView);
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
				// TODO check if a selection is available
				// I use the mainFrame object because in that case this is the action listener
				new TodolistDialog(mainFrame, (Todolist) todolistsByName.get((String) jlTodolists.getSelectedValue())).show();
			}
		});
		jtbToolBar.add(jbEditTodolist);
		
		JButton jbDeleteTodolist = new JButton("Remove todolist");
		jbDeleteTodolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DialogBox.question("Delete todolist", "Do you realy want to delete the selected todolist ?"))
					mainFrame.deleteTodolist((String)jlTodolists.getSelectedValue());
			}
		});
		jtbToolBar.add(jbDeleteTodolist);
		
		JButton jbCreateItem = new JButton("New item");
		jbCreateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// TODO check if a selection is available
				// I use the mainFrame object because in that case this is the action listener
				new TodolistItemDialog(mainFrame, (String)jlTodolists.getSelectedValue()).show();
			}
		});
		jtbToolBar.add(jbCreateItem);
		
		JButton jbEditItem = new JButton("Edit item");
		jbEditItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// TODO check if a selection is available
				// I use the mainFrame object because in that case this is the action listener
				new TodolistItemDialog(mainFrame, (TodolistItem) todolistItemsByName.get((String) jlTodolistItems.getSelectedValue())).show();
			}
		});
		jtbToolBar.add(jbEditItem);
		
		JButton jbDeleteItem = new JButton("Remove item");
		jbDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DialogBox.question("Delete item", "Do you realy want to delete the selected item ?"))
					mainFrame.deleteTodolistItem((String)jlTodolistItems.getSelectedValue());
			}
		});
		jtbToolBar.add(jbDeleteItem);
		
		getContentPane().add(jtbToolBar, BorderLayout.NORTH);
		
		refreshTodolists();
	}
	
	private void refreshTodolists() {
		Iterator it;
		
		ArrayList todolists = IO.getInstance().getTodolists();
		todolistsByName = new HashMap();
		it = todolists.iterator();
		while (it.hasNext()) {
			Todolist tl = (Todolist)it.next();
			todolistsByName.put(tl.getName(), tl);
		}
		
		jlTodolists.setListData(new Vector(todolistsByName.keySet()));
	}

	private void refreshTodolistItems() {
		Iterator it;
		
		String todolistName = (String)jlTodolists.getSelectedValue();
		
		ArrayList todolistItems = IO.getInstance().getTodolistItems(todolistName);
		todolistItemsByName = new HashMap();
		it = todolistItems.iterator();
		while (it.hasNext()) {
			TodolistItem tli = (TodolistItem)it.next();
			todolistItemsByName.put(tli.getName(), tli);
		}
		
		jlTodolistItems.setListData(new Vector(todolistItemsByName.keySet()));
	}
	
	protected void addTodolist(Todolist newTodolist) {
		if (!IO.getInstance().addTodolist(newTodolist))
			return;
		todolistsByName.put(newTodolist.getName(), newTodolist);
		jlTodolists.setListData(new Vector(todolistsByName.keySet()));
	}

	protected void modifyTodolist(Todolist oldTodolist, Todolist newTodolist) {
		if (!IO.getInstance().modifyTodolist(oldTodolist, newTodolist))
			return;
		todolistsByName.remove(oldTodolist.getName());
		todolistsByName.put(newTodolist.getName(), newTodolist);
		jlTodolists.setListData(new Vector(todolistsByName.keySet()));
	}

	protected void deleteTodolist(String defunctTodolistName) {
		if (!IO.getInstance().deleteTodolist((Todolist)todolistsByName.get(defunctTodolistName)))
			return;
		todolistsByName.remove(defunctTodolistName);
		jlTodolists.setListData(new Vector(todolistsByName.keySet()));
	}

	protected void addTodolistItem(TodolistItem newTodolistItem) {
		if (!IO.getInstance().addTodolistItem(newTodolistItem))
			return;
		todolistItemsByName.put(newTodolistItem.getName(), newTodolistItem);
		jlTodolistItems.setListData(new Vector(todolistItemsByName.keySet()));
	}

	protected void modifyTodolistItem(TodolistItem oldTodolistItem, TodolistItem newTodolistItem) {
		if (!IO.getInstance().modifyTodolistItem(oldTodolistItem, newTodolistItem))
			return;
		todolistItemsByName.remove(oldTodolistItem.getName());
		todolistItemsByName.put(newTodolistItem.getName(), newTodolistItem);
		jlTodolistItems.setListData(new Vector(todolistItemsByName.keySet()));
	}

	protected void deleteTodolistItem(String defunctTodolistItemName) {
		if (!IO.getInstance().deleteTodolistItem((TodolistItem)todolistItemsByName.get(defunctTodolistItemName)))
			return;
		todolistItemsByName.remove(defunctTodolistItemName);
		jlTodolistItems.setListData(new Vector(todolistItemsByName.keySet()));
	}
}
