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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lucane.applications.todolist.Todolist;
import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.io.IO;

public class TodolistDialog extends JDialog {
	private JTextField jtfName;
	private JTextArea jtaDescription;
	private JButton jbOk;
	private JButton jbCancel;
	
	private MainFrame mainFrame;
	
	private Todolist todolist;
	
	private boolean modify = false;
	
	public TodolistDialog (MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		init();
	}
	
	public TodolistDialog (MainFrame mainFrame, Todolist todolist) {
		this.mainFrame = mainFrame;
		this.todolist = todolist;
		modify=true;
		init();
	}
	
	private void init() {
		if (modify) {
			setTitle("Todolist modification ...");
		} else {
			setTitle("Todolist creation ...");
		}
		JPanel contentPane = new JPanel(new BorderLayout(3, 3));
		contentPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		setContentPane(contentPane);
		
		setSize(320, 200);
		
		jtfName = new JTextField();
		jtaDescription = new JTextArea();
		
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (modify)
					mainFrame.modifyTodolist(
							todolist,
							new Todolist(
									todolist.getUserName(),
									jtfName.getText(),
									jtaDescription.getText()));
				else
					mainFrame.addTodolist(new Todolist(IO.getInstance().getUserName(), jtfName.getText(), jtaDescription.getText()));
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

		JPanel jpDescription = new JPanel(new BorderLayout());
		jpDescription.add(new JLabel("Description :"), BorderLayout.NORTH);
		jpDescription.add(new JScrollPane(jtaDescription), BorderLayout.CENTER);

		JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
		jpButtons.add(jbOk);
		jpButtons.add(jbCancel);

		getContentPane().add(jpName, BorderLayout.NORTH);
		getContentPane().add(jpDescription, BorderLayout.CENTER);
		getContentPane().add(jpButtons, BorderLayout.SOUTH);
		
		if (modify) {
			jtfName.setText(todolist.getName());
			jtaDescription.setText(todolist.getDescription());
		}
	}
}
