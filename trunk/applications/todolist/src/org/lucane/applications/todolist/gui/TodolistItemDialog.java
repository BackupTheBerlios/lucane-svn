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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;

public class TodolistItemDialog extends JDialog {
	private JTextField jtfName;
	private JTextArea jtaDescription;
	private JTextField jtfPriority;
	private JCheckBox jcbComplete;
	private JButton jbOk;
	private JButton jbCancel;
	
	private MainFrame mainFrame;
	private String parentTodolistName;
	
	private TodolistItem todolistItem;
	
	private boolean modify = false;
	
	public TodolistItemDialog (MainFrame mainFrame, String parentTodolistName) {
		this.mainFrame = mainFrame;
		this.parentTodolistName = parentTodolistName;
		init();
	}
	
	public TodolistItemDialog (MainFrame mainFrame, TodolistItem todolistItem) {
		this.mainFrame = mainFrame;
		this.todolistItem = todolistItem;
		modify = true;
		init();
	}
	
	private void init() {
		if (modify) {
			setTitle("Item modification ...");
		} else {
			setTitle("Item creation ...");
		}
		setSize(320, 240);
		JPanel contentPane = new JPanel(new BorderLayout(3, 3));
		contentPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		setContentPane(contentPane);
		
		
		jtfName = new JTextField();
		jtaDescription = new JTextArea();
		jtfPriority = new JTextField();
		
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (modify)
					mainFrame.modifyTodolistItem(
							todolistItem,
							new TodolistItem(
									todolistItem.getUserName(),
									todolistItem.getParentTodolistName(),
									jtfName.getText(),
									jtaDescription.getText(),
									new Integer(jtfPriority.getText()).intValue(),
									jcbComplete.isSelected()));
				else
					mainFrame.addTodolistItem(new TodolistItem(IO.getInstance().getUserName(), parentTodolistName, jtfName.getText(), jtaDescription.getText(), new Integer(jtfPriority.getText()).intValue()));
				hide();
			}
		});
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hide();
			}
		});
		
		JPanel jpName = new JPanel(new BorderLayout(3, 3));
		jpName.add(new JLabel("Name :"), BorderLayout.WEST);
		jpName.add(jtfName, BorderLayout.CENTER);

		JPanel jpPriority = new JPanel(new BorderLayout(3, 3));
		jpPriority.add(new JLabel("Priority :"), BorderLayout.WEST);
		jpPriority.add(jtfPriority, BorderLayout.CENTER);

		JPanel jpComplete = null;
		if (modify) {
			jpComplete = new JPanel(new BorderLayout(3, 3));
			jpComplete.add(new JLabel("Complete :"), BorderLayout.WEST);
			jcbComplete = new JCheckBox();
			jpComplete.add(jcbComplete, BorderLayout.CENTER);
		}

		JPanel jpDescription = new JPanel(new BorderLayout(3, 3));
		jpDescription.add(new JLabel("Description :"), BorderLayout.NORTH);
		jpDescription.add(new JScrollPane(jtaDescription), BorderLayout.CENTER);

		JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
		jpButtons.add(jbOk);
		jpButtons.add(jbCancel);

		JPanel jpFields;
		if (modify)
			jpFields = new JPanel(new GridLayout(3, 1, 3, 3));
		else
			jpFields = new JPanel(new GridLayout(2, 1, 3, 3));
		jpFields.add(jpName);
		jpFields.add(jpPriority);
		if (modify)
			jpFields.add(jpComplete);
		
		getContentPane().add(jpFields, BorderLayout.NORTH);
		getContentPane().add(jpDescription, BorderLayout.CENTER);
		getContentPane().add(jpButtons, BorderLayout.SOUTH);

		if (modify) {
			jtfName.setText(todolistItem.getName());
			jtaDescription.setText(todolistItem.getDescription());
			jtfPriority.setText("" + todolistItem.getPriority());
			jcbComplete.setSelected(todolistItem.isComplete());
		}
	}
}
