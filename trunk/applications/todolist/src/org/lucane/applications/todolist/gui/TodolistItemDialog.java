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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;

public class TodolistItemDialog extends JDialog {
	JTextField jtfName;
	JTextField jtfDescription;
	JTextField jtfPriority;
	JCheckBox jcbComplete;
	JButton jbOk;
	JButton jbCancel;
	
	MainFrame mainFrame;
	String parentTodolistName;
	
	TodolistItem todolistItem;
	
	boolean modify = false;
	
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
			getContentPane().setLayout(new GridLayout(5, 2));
			setSize(320, 150);
		} else {
			setTitle("Item creation ...");
			getContentPane().setLayout(new GridLayout(4, 2));
			setSize(320, 120);
		}
		
		
		jtfName = new JTextField();
		jtfDescription = new JTextField();
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
									jtfDescription.getText(),
									new Integer(jtfPriority.getText()).intValue(),
									jcbComplete.isSelected()));
				else
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
		
		if (modify) {
			jcbComplete = new JCheckBox();
			getContentPane().add(new JLabel("Complete :"));
			getContentPane().add(jcbComplete);

			jtfName.setText(todolistItem.getName());
			jtfDescription.setText(todolistItem.getDescription());
			jtfPriority.setText("" + todolistItem.getPriority());
			jcbComplete.setSelected(todolistItem.isComplete());
		}

		getContentPane().add(jbOk);
		getContentPane().add(jbCancel);
	}
	
}
