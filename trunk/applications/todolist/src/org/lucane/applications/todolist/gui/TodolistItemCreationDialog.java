package org.lucane.applications.todolist.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;

public class TodolistItemCreationDialog extends JDialog {
	JTextField jtfName;
	JTextField jtfDescription;
	JTextField jtfPriority;
	JButton jbOk;
	JButton jbCancel;
	
	MainFrame mainFrame;
	String parentTodolistName;
	
	public TodolistItemCreationDialog (MainFrame mainFrame, String parentTodolistName) {
		this.mainFrame = mainFrame;
		this.parentTodolistName = parentTodolistName;
		init();
	}
	
	private void init() {
		setTitle("Item creation ...");
		setSize(320, 120);
		
		getContentPane().setLayout(new GridLayout(4, 2));
		
		jtfName = new JTextField();
		jtfDescription = new JTextField();
		jtfPriority = new JTextField();
		
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.addTodolistItem(new TodolistItem(IO.getInstance().getUserName(), parentTodolistName, jtfName.getText(), jtfDescription.getText(), new Integer(jtfPriority.getText()).intValue()));
				hide();
			}
		});
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hide();
			}
		});
		
		getContentPane().add(new JLabel("Name :"));
		getContentPane().add(jtfName);
		getContentPane().add(new JLabel("Description :"));
		getContentPane().add(jtfDescription);
		getContentPane().add(new JLabel("Priority :"));
		getContentPane().add(jtfPriority);
		getContentPane().add(jbOk);
		getContentPane().add(jbCancel);
	}
	
}
