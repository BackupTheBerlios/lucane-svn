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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lucane.applications.todolist.TodolistItem;
import org.lucane.client.Plugin;
import org.lucane.client.widgets.ManagedWindow;
import org.lucane.client.widgets.htmleditor.HTMLEditor;

public class TodolistItemDialog extends ManagedWindow {
	private JTextField jtfName;
	private HTMLEditor htmledComment;
	//private JTextField jtfPriority;
	private JComboBox jcmbPriority;
	private JCheckBox jcbComplete;
	private JButton jbOk;
	private JButton jbCancel;
	
	private MainFrame mainFrame;
	private Plugin plugin;
	
	private int parentTodolistId;
	private TodolistItem todolistItem;
	
	private boolean modify = false;
	
	public TodolistItemDialog (Plugin plugin, MainFrame mainFrame, int parentTodolistId) {
		super(plugin, "");
		this.plugin = plugin;
		this.mainFrame = mainFrame;
		this.parentTodolistId = parentTodolistId;
		init();
	}
	
	public TodolistItemDialog (Plugin plugin, MainFrame mainFrame, TodolistItem todolistItem) {
		super(plugin, "");
		this.plugin = plugin;
		this.mainFrame = mainFrame;
		this.todolistItem = todolistItem;
		modify = true;
		init();
	}
	
	private void init() {
		if (modify) {
			setTitle(plugin.tr("TodoListItemDialog.modificationTitle"));
		} else {
			setTitle(plugin.tr("TodoListItemDialog.creationTitle"));
		}
		setPreferredSize(new Dimension(512, 384));
	    setIconImage(plugin.getImageIcon().getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	    this.setName("TodolistItemDialog");
	    
		jtfName = new JTextField();
		htmledComment = new HTMLEditor();
		//jtfPriority = new JTextField();
		jcmbPriority = new JComboBox(TodolistItem.getPriorityLabels());
		
		jbOk = new JButton(plugin.tr("TodoListItemDialog.ok"));
		jbOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (modify)
					mainFrame.modifyTodolistItem(
							todolistItem,
							new TodolistItem(
									todolistItem.getId(),
									todolistItem.getParentTodolistId(),
									jtfName.getText(),
									htmledComment.getText(),
									jcmbPriority.getSelectedIndex(),
									jcbComplete.isSelected()));
				else
					mainFrame.addTodolistItem(new TodolistItem(parentTodolistId, jtfName.getText(), htmledComment.getText(), jcmbPriority.getSelectedIndex()));
				hide();
			}
		});
		jbCancel = new JButton(plugin.tr("TodoListItemDialog.cancel"));
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
		getContentPane().add(new JLabel(plugin.tr("TodoListItemDialog.name")), c);
		c.gridx=1;
		c.weightx=1;
		getContentPane().add(jtfName, c);

		c.gridy=1;
		c.gridx=0;
		c.weightx=0;
		c.weighty=1;
		getContentPane().add(new JLabel(plugin.tr("TodoListItemDialog.comment")), c);
		c.fill=GridBagConstraints.BOTH;
		c.gridx=1;
		c.weightx=1;
		getContentPane().add(htmledComment, c);

		c.gridy=2;
		c.gridx=0;
		c.weightx=0;
		c.weighty=0;
		getContentPane().add(new JLabel(plugin.tr("TodoListItemDialog.priority")), c);
		c.gridx=1;
		c.weightx=1;
		getContentPane().add(jcmbPriority, c);

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
			getContentPane().add(new JLabel(plugin.tr("TodoListItemDialog.complete")), c);
			c.gridx=1;
			c.weightx=1;
			getContentPane().add(jcbComplete, c);

			jtfName.setText(todolistItem.getName());
			htmledComment.setText(todolistItem.getComment());
			jcmbPriority.setSelectedIndex(todolistItem.getPriority());
			jcbComplete.setSelected(todolistItem.isCompleted());
		}
	}
}
