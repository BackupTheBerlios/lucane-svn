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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.io.IO;

public class TodolistCreationDialog extends JDialog {
	JTextField jtfName;
	JTextField jtfDescription;
	JButton jbOk;
	JButton jbCancel;
	
	MainFrame mainFrame;
	
	public TodolistCreationDialog (MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		init();
	}
	
	private void init() {
		setTitle("Todolist creation ...");
		setSize(320, 90);
		
		getContentPane().setLayout(new GridLayout(3, 2));
		
		jtfName = new JTextField();
		jtfDescription = new JTextField();
		
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.addTodolist(new Todolist(IO.getInstance().getUserName(), jtfName.getText(), jtfDescription.getText()));
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
		getContentPane().add(jbOk);
		getContentPane().add(jbCancel);
	}
}
