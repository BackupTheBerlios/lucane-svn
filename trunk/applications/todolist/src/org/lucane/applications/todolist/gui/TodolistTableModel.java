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

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.lucane.applications.todolist.Todolist;

public class TodolistTableModel extends AbstractTableModel {

	private static final String[] COLUMNS_NAMES = {"Todo list"};
	private ArrayList lists;
	
	public TodolistTableModel() {
		lists = new ArrayList();
	}
	
	public void add(Todolist list) {
		lists.add(list);
		fireTableDataChanged();
	}
	
	public void remove(Todolist list) {
		lists.remove(list);
		fireTableDataChanged();
	}
	
	public Class getColumnClass(int col) {
		return String.class;
	}

	public String getColumnName(int col) {
		return COLUMNS_NAMES[col];
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void setValueAt(Object value, int row, int col) {
		super.setValueAt(value, row, col);
	}
	
	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return lists.size();
	}

	public Object getValueAt(int row, int col) {
		Todolist list = (Todolist)lists.get(row);
		return list.getName();
	}

	public Object getValueAt(int row) {
		return ((Todolist)lists.get(row));
	}
	
	public void clear() {
		lists.clear();
		fireTableRowsDeleted(0, 0);
	}
}
