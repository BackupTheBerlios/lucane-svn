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

import org.lucane.applications.todolist.Todolist;

public class TodolistTable extends JTable {
	ActionListener columnHeaderActionListener;
	
	public TodolistTable() {
		super();
		
		setModel(new TodolistTableModel());
		setSelectionModel(new DefaultListSelectionModel());
		
		getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    TableColumnModel columnModel = getColumnModel();   
			    int col = columnModel.getColumnIndexAtX(e.getX()); 
			    if(e.getClickCount()==1 && col>=0) {
					((TodolistTableModel)getModel()).sort(col);
			    }
			}
		});
		
	}
	
	public void addListSelectionListener(ListSelectionListener lsl) {
		getSelectionModel().addListSelectionListener(lsl);
	}
	
	public Todolist getSelectedTodolist() {
		int selectedIndex = getSelectionModel().getMinSelectionIndex();
		if (selectedIndex>=0)
			return (Todolist)((TodolistTableModel)getModel()).getValueAt(selectedIndex);
		return null;
	}
	
	public void add(Todolist tl) {
		((TodolistTableModel)getModel()).add(tl);
	}
	
	public void remove(Todolist tl) {
		((TodolistTableModel)getModel()).remove(tl);
	}
	
	public void clear() {
		((TodolistTableModel)getModel()).clear();
	}
	
}

class TodolistTableModel extends AbstractTableModel {

	private static String[] columnsNames = {"Name"};
	private static final int[] COLUMNS_SORT = {TodolistsSorter.NAME, TodolistsSorter.PRIORITY, TodolistsSorter.COMPLETED};
	private ArrayList lists;
	private Comparator comparator;
	
	private int sortedCol = -1;
	private int sortDirection = TodolistsSorter.ASC;
	
	public TodolistTableModel() {
		lists = new ArrayList();
		comparator = new TodolistsSorter(TodolistsSorter.NAME, TodolistsSorter.ASC);
	}
	
	public static void setColumnsNames(String [] names) {
		columnsNames = names;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	public void sort(int columnIndex) {
		if (columnIndex==sortedCol) {
			sortDirection = sortDirection == TodolistsSorter.ASC
					? TodolistsSorter.DESC
					: TodolistsSorter.ASC;
		} else {
			sortedCol = columnIndex;
			sortDirection = TodolistsSorter.ASC;
		}
		
		comparator = new TodolistsSorter(COLUMNS_SORT[sortedCol], sortDirection);
		Collections.sort(lists, comparator);
		fireTableDataChanged();
	}
	
	public void add(Todolist list) {
		lists.add(list);
		Collections.sort(lists, comparator);
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
		return columnsNames[col];
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
		try {
			return ((Todolist) lists.get(row));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public void clear() {
		lists.clear();
		fireTableRowsDeleted(0, 0);
	}
}

class TodolistsSorter implements Comparator {

	public static final int ASC = 1;
	public static final int DESC = -1;

	public static final int NAME = 0;
	public static final int PRIORITY = 1;
	public static final int COMPLETED = 2;
	
	private int sortBy=0;
	private int direction = 0;
	
	public TodolistsSorter(int sortBy, int direction) {
		this.sortBy = sortBy;
		this.direction = direction;
	}
	
	public int compare(Object o1, Object o2) {
		Todolist tl1 = (Todolist) o1;
		Todolist tl2 = (Todolist) o2;
		switch (sortBy) {
			case NAME :
				return direction*(tl1.getName().compareTo(tl2.getName()));
		}
		return 0;
	}
}
