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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lucane.applications.todolist.Todolist;
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
//		JPanel contentPane = new JPanel(new BorderLayout(3, 3));
//		contentPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
//		setContentPane(contentPane);
		
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

		JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		jpButtons.add(jbOk);
		jpButtons.add(jbCancel);
		
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets=new Insets(2,2,2,2);
		c.anchor=GridBagConstraints.NORTH;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridy=0;
		c.gridx=0;
		c.weightx=0;
		c.weighty=0;
		getContentPane().add(new JLabel("Name :"), c);
		c.gridx=1;
		c.weightx=1;
		getContentPane().add(jtfName, c);

		c.gridy=1;
		c.gridx=0;
		c.weightx=0;
		c.weighty=1;
		getContentPane().add(new JLabel("Description :"), c);
		c.fill=GridBagConstraints.BOTH;
		c.gridx=1;
		c.weightx=1;
		getContentPane().add(new JScrollPane(jtaDescription), c);

		c.fill=GridBagConstraints.BOTH;
		c.gridy=2;
		c.gridx=0;
		c.weightx=1;
		c.weighty=0;
		c.gridwidth=2;
		getContentPane().add(jpButtons, c);
		
		if (modify) {
			jtfName.setText(todolist.getName());
			jtaDescription.setText(todolist.getDescription());
		}
	}
}
