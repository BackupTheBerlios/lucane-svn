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

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import org.lucane.applications.todolist.TodolistItem;

public class TodolistItemTable extends JTable {
	ActionListener columnHeaderActionListener;
	
	public TodolistItemTable() {
		super();
		
		setModel(new TodolistItemTableModel());
		setSelectionModel(new DefaultListSelectionModel());
		
		getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    TableColumnModel columnModel = getColumnModel();   
			    int col = columnModel.getColumnIndexAtX(e.getX()); 
			    if(e.getClickCount()==1 && col>=0) {
					((TodolistItemTableModel)getModel()).sort(col);
			    }
			}
		});
	}
	
	public void addListSelectionListener(ListSelectionListener lsl) {
		getSelectionModel().addListSelectionListener(lsl);
	}
	
	public TodolistItem getSelectedTodolistItem() {
		int selectedIndex = getSelectionModel().getMinSelectionIndex();
		if (selectedIndex>=0)
			return (TodolistItem)((TodolistItemTableModel)getModel()).getValueAt(selectedIndex);
		return null;
	}
	
	public void add(TodolistItem tli) {
		((TodolistItemTableModel)getModel()).add(tli);
	}
	
	public void remove(TodolistItem tli) {
		((TodolistItemTableModel)getModel()).remove(tli);
	}
	
	public void clear() {
		((TodolistItemTableModel)getModel()).clear();
	}
	
}
