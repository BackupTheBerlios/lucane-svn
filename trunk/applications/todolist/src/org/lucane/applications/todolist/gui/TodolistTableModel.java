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
