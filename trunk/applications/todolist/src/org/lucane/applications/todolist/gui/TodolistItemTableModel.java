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
import java.util.Collections;
import java.util.Comparator;

import javax.swing.table.AbstractTableModel;

import org.lucane.applications.todolist.TodolistItem;
import org.lucane.applications.todolist.TodolistItemsSorter;

public class TodolistItemTableModel extends AbstractTableModel {

	private static final String[] COLUMNS_NAMES = {"Name", "Priority", "Complete"};
	private static final int[] COLUMNS_SORT = {TodolistItemsSorter.NAME, TodolistItemsSorter.PRIORITY, TodolistItemsSorter.COMPLETED};
	private ArrayList items;
	private Comparator comparator;
	
	private int sortedCol = -1;
	private int sortDirection = TodolistItemsSorter.ASC;
	
	public TodolistItemTableModel() {
		items = new ArrayList();
		comparator = new TodolistItemsSorter(TodolistItemsSorter.NAME, TodolistItemsSorter.ASC);
	}
	
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	public void sort(int columnIndex) {
		if (columnIndex==sortedCol) {
			sortDirection = sortDirection == TodolistItemsSorter.ASC
					? TodolistItemsSorter.DESC
					: TodolistItemsSorter.ASC;
		} else {
			sortedCol = columnIndex;
			sortDirection = TodolistItemsSorter.ASC;
		}
			
		comparator = new TodolistItemsSorter(COLUMNS_SORT[sortedCol], sortDirection);
		Collections.sort(items, comparator);
		fireTableDataChanged();
	}
	
	public void add(TodolistItem item) {
		items.add(item);
		Collections.sort(items, comparator);
		fireTableDataChanged();
	}
	
	public void remove(TodolistItem item) {
		items.remove(item);
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
		return 3;
	}

	public int getRowCount() {
		return items.size();
	}

	public Object getValueAt(int row, int col) {
		TodolistItem item = (TodolistItem)items.get(row);
		switch (col) {
			case 0 :
				return item.getName();
			case 1 :
				return ""+item.getPriority();
			case 2 :
				return item.isComplete()?"true":"false";
		}
		return "";
	}

	public Object getValueAt(int row) {
		return ((TodolistItem)items.get(row));
	}
	
	public void clear() {
		items.clear();
		fireTableRowsDeleted(0, 0);
	}
}
