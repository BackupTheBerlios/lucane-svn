package org.lucane.applications.todolist.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.lucane.applications.todolist.TodolistItem;

public class TodolistItemTableModel extends AbstractTableModel {

	private static final String[] COLUMNS_NAMES = {"Name", "Priority", "Complete"};
	private ArrayList items;

	public TodolistItemTableModel() {
		items = new ArrayList();
	}
	
	public void add(TodolistItem item) {
		items.add(item);
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
