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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.lucane.applications.todolist.TodolistItem;

public class TodolistItemTable extends JTable {
	ActionListener columnHeaderActionListener;
	
	public TodolistItemTable() {
		super();
		
		setModel(new TodolistItemTableModel());
		setSelectionModel(new DefaultListSelectionModel());
		
		getColumnModel().getColumn(1).setMaxWidth(90);
		getColumnModel().getColumn(1).setMinWidth(90);
		getColumnModel().getColumn(2).setMaxWidth(90);
		getColumnModel().getColumn(2).setMinWidth(90);

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
	
	public static void setColumnsNames(String [] names) {
		TodolistItemTableModel.setColumnsNames(names);
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

class TodolistItemTableModel extends AbstractTableModel {

	private static String[] columnsNames = {"Name", "Priority", "Complete"};
	private static final int[] COLUMNS_SORT = {TodolistItemsSorter.NAME, TodolistItemsSorter.PRIORITY, TodolistItemsSorter.COMPLETED};
	private ArrayList items;
	private Comparator comparator;
	
	private int sortedCol = -1;
	private int sortDirection = TodolistItemsSorter.ASC;
	
	public TodolistItemTableModel() {
		items = new ArrayList();
		comparator = new TodolistItemsSorter(TodolistItemsSorter.NAME, TodolistItemsSorter.ASC);
	}

	public static void setColumnsNames(String [] names) {
		columnsNames = names;
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
		return columnsNames[col];
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
				return TodolistItem.getPriorityLabels()[item.getPriority()];
			case 2 :
				return TodolistItem.getCompleteLabels()[item.isComplete()?1:0];
		}
		return "";
	}

	public Object getValueAt(int row) {
		try {
			return ((TodolistItem)items.get(row));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public void clear() {
		items.clear();
		fireTableRowsDeleted(0, 0);
	}
}

class TodolistItemsSorter implements Comparator {

	public static final int ASC = 1;
	public static final int DESC = -1;

	public static final int NAME = 0;
	public static final int PRIORITY = 1;
	public static final int COMPLETED = 2;
	
	private int sortBy=0;
	private int direction = 0;
	
	public TodolistItemsSorter(int sortBy, int direction) {
		this.sortBy = sortBy;
		this.direction = direction;
	}
	
	public int compare(Object o1, Object o2) {
		TodolistItem tli1 = (TodolistItem) o1;
		TodolistItem tli2 = (TodolistItem) o2;
		switch (sortBy) {
			case NAME :
				return direction*(tli1.getName().compareTo(tli2.getName()));
			case PRIORITY :
				return direction*(tli2.getPriority()-tli1.getPriority());
			case COMPLETED :
				return direction*((tli1.isComplete()?1:0)-(tli2.isComplete()?1:0));
		}
		return 0;
	}
}
