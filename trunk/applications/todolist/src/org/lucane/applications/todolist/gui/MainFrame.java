package org.lucane.applications.todolist.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
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

public class MainFrame extends JFrame {
	JToolBar jtbToolBar;
	JList jlTodolists;
	//JTable jtTodoListItems;
	JList jlTodolistItems;
	JSplitPane jspMain;
	JSplitPane jspItem;
	JSplitPane jspList;
	JPanel jpItemView;
	JPanel jpListView;

	JButton jbCreateTodolist;
	JButton jbEditTodolist;
	JButton jbDeleteTodolist;
	JButton jbCreateItem;
	JButton jbEditItem;
	JButton jbDeleteItem;
	
	MainFrame mainFrame;
	
	HashMap todolistsByName;
	HashMap todolistItemsByName;
	
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
			}
		});
		jpListView = new JPanel(new BorderLayout());
		jpListView.add(new JLabel("Description :"), BorderLayout.NORTH);
		jpListView.add(new JLabel("<html><body><i>The description</i></body></html>"), BorderLayout.CENTER);
		jspList = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(jlTodolists), jpListView);
		jspList.setResizeWeight(.80);

		//jtTodoListItems = new JTable();
		jlTodolistItems = new JList();
		jpItemView = new JPanel();
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
				new TodolistCreationDialog(mainFrame).show();
			}
		});
		jtbToolBar.add(jbCreateTodolist);
		
		JButton jbEditTodolist = new JButton("Edit todolist");
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
				new TodolistItemCreationDialog(mainFrame, (String)jlTodolists.getSelectedValue()).show();
			}
		});
		jtbToolBar.add(jbCreateItem);
		
		JButton jbEditItem = new JButton("Edit item");
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

	protected void deleteTodolistItem(String defunctTodolistItemName) {
		if (!IO.getInstance().deleteTodolistItem((TodolistItem)todolistItemsByName.get(defunctTodolistItemName)))
			return;
		todolistItemsByName.remove(defunctTodolistItemName);
		jlTodolistItems.setListData(new Vector(todolistItemsByName.keySet()));
	}
}
