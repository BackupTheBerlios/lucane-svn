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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

		c.gridy=2;
		c.gridx=0;
		c.weightx=0;
		c.weighty=0;
		getContentPane().add(new JLabel("Priority :"), c);
		c.gridx=1;
		c.weightx=1;
		getContentPane().add(jtfPriority, c);

		c.fill=GridBagConstraints.BOTH;
		c.gridy=4;
		c.gridx=0;
		c.weightx=1;
		c.weighty=0;
		c.gridwidth=2;
		getContentPane().add(jpButtons, c);
		
		JPanel jpComplete = null;
		if (modify) {
			jcbComplete = new JCheckBox();
			c.gridy=3;
			c.gridx=0;
			c.weightx=0;
			c.weighty=0;
			getContentPane().add(new JLabel("Complete :"), c);
			c.gridx=1;
			c.weightx=1;
			getContentPane().add(jcbComplete, c);

			jtfName.setText(todolistItem.getName());
			jtaDescription.setText(todolistItem.getDescription());
			jtfPriority.setText("" + todolistItem.getPriority());
			jcbComplete.setSelected(todolistItem.isComplete());
		}
	}
}
