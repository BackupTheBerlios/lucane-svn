package org.lucane.applications.jmail.base;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of JMail                                              *
 * Copyright (C) 2002-2003 Yvan Norsa <norsay@wanadoo.fr>                  *
 *                                                                         *
 * JMail is free software; you can redistribute it and/or modify           *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * any later version.                                                      *
 *                                                                         *
 * JMail is distributed in the hope that it will be useful,                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with JMail; if not, write to the Free Software Foundation, Inc.,        *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/** This class allows to create a sortable table easily, with the minimum
    amount of work. It may be not very efficient, but it allows the *client*
    class to be lighter than if it had to handle itself all the sorting thing..
    It also handles multiple data types simultaneously. 
    This class is mainly inspired from Sun's tutorial about JTable 
*/
final class SortableTable extends JTable
{
    /** Content of the table */
    private Object rowData[][];

    /** Names of the columns */
    private Object columnsNames[];

    /** Indexes of the rows */
    private Object indexes[];

    /** Indexes of the columns */
    private int rowIndexes[];

    /** Columns which we are currently sorting */
    private int[] sortingColumns;

    /** Tells what is the sort order : ascending or descending */
    private boolean ascending;

    /** List of the listeners */
    private ArrayList listeners;

    /** Default constructor */
    protected SortableTable()
    {
	super();

	ascending = true;
	listeners = new ArrayList();

	init();
    }

    /** Constructor
     *  @param rowData content of the table
     *  @param columnsNames names of the columns
     */
    protected SortableTable(Object rowData[][], Object columnsNames[])
    {
	super(rowData, columnsNames);

	this.rowData = rowData;
	this.columnsNames = columnsNames;

	ascending = true;
	listeners = new ArrayList();

	init();
    }

    /** Allows to make cells editable
     *  NOTE : it returns currently false, independantly from row and col, because this class has been made for JMail. I suppose one should correct it, making a call to the <code>JTable</code>'s original method
     *  @param row number of the row
     *  @param col number of the col
     *  @return boolean telling wether this cell is editable or not
     */
    public final boolean isCellEditable(int row, int col)
    {
	return(false);
    }

    /** Performs the init stuff */
    private void init()
    {       
	sortingColumns = null;

	ascending = true;

	listeners = new ArrayList();

	//setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	setShowGrid(false);

	indexes = new Object[0];
	rowIndexes = new int[0];
    }

    /** Allows to set the indexes array
     *  @param indexes new indexes array
     */
    protected final void setIndexes(Object indexes[])
    {
	this.indexes = indexes;
    }

    /** Allows to set the table data
     *  @param rowData content of the table
     *  @param columnsNames names of the columns
     */
    protected final void setData(Object rowData[][], Object columnsNames[])
    {
	this.rowData = rowData;
	this.columnsNames = columnsNames;

	((DefaultTableModel)dataModel).setDataVector(rowData, columnsNames);
	reallocateIndexes();
    }

    /** Returns the index of selected row
     *  @return the index of row currently selected
     */
    protected final Object getSelectedRowIndex()
    {
	return(indexes[getSelectedRow()]);
    }

    protected final Object[] getSelectedRowsIndexes()
    {
	int[] rows = getSelectedRows();
	Object indices[] = new Object[rows.length];

	for(int i = 0; i < rows.length; i++)
	    indices[i] = indexes[rows[i]];

	return(indices);
    }

    /** Allows to add a <code>ListSelectionListener</code> listener to the table
     *  @param listener <code>ListSelectionListener</code> listener to add
     */
    protected final void addListSelectionListener(ListSelectionListener listener)
    {
	getSelectionModel().addListSelectionListener(listener);
    }

    /** Allows to add a <code>TableModelListener</code> listener to the table
     *  @param listener <code>TableModelListener</code> listener to add
     */
    protected final void addTableModelListener(TableModelListener listener)
    {
	dataModel.addTableModelListener(listener);
    }

    /** Compare the content of two rows on one column
     *  @param row1 first row to be looked to
     *  @param row2 second tow to be looked to
     *  @param column column to look at
     *  @return result of the comparison (-1, 1, 0)
     */
    private int compareRowsByColumn(int row1, int row2, int column)
    {	
	Class type = dataModel.getColumnClass(column);

	DefaultTableModel data = null;

	data = (DefaultTableModel)dataModel;

	Object o1 = data.getValueAt(row1, column);
	Object o2 = data.getValueAt(row2, column); 

	if(o1 == null && o2 == null)
	    return(0); 

	else if(o1 == null)
	    return(-1); 

	else if(o2 == null)
	    return(1);

	if(type == java.util.Date.class)
	{
	    Date d1 = (Date)data.getValueAt(row1, column);
	    long n1 = d1.getTime();
	    Date d2 = (Date)data.getValueAt(row2, column);
	    long n2 = d2.getTime();

	    if(n1 < n2)
		return(-1);

	    else if(n1 > n2)
		return(1);

	    else
		return(0);
	}

	else if(type == String.class)
	{
	    String s1 = (String)data.getValueAt(row1, column);
	    String s2 = (String)data.getValueAt(row2, column);
	    int result = s1.compareTo(s2);

	    if(result < 0)
		return(-1);

	    else if(result > 0)
		return(1);

	    else
		return(0);
	}

	else
	{
	    Object v1 = data.getValueAt(row1, column);
	    String s1 = v1.toString();
	    Object v2 = data.getValueAt(row2, column);
	    String s2 = v2.toString();
	    int result = s1.compareTo(s2);

	    if(result < 0)
		return(-1);

	    else if(result > 0)
		return(1);

	    else
		return(0);
	}
    }

    /** Compares the content of two rows, for each column
     *  @param row1 first row to be looked to
     *  @param row2 second row to be looked to
     *  @return result of the comparison
     */
    private int compare(int row1, int row2)
    {
	for(int level = 0; level < sortingColumns.length; level++)
	{
	    int result = compareRowsByColumn(row1, row2, sortingColumns[level]);

	    if(result != 0)
	    {
		if(ascending)
		    return(result);

		else
		    return(-result);
	    }
	}

	return(0);
    }

    /** Reperforms the calculation of the indexes */
    private void reallocateIndexes()
    {
	int rowCount = dataModel.getRowCount();

	rowIndexes = new int[rowCount];

	for(int row = 0; row < rowCount; row++)
	    rowIndexes[row] = row;
    }

    /** Sorts the content of the table */
    private void sort()
    {
	shuttlesort((int[])rowIndexes.clone(), rowIndexes, 0, indexes.length);
    }

    /** Really performs the shuttlesort
     *
     *  @param from *low* indexes
     *  @param to *high* indexes
     *  @param low start index
     *  @param high end index
     */
    private void shuttlesort(int from[], int to[], int low, int high)
    {
	if((high - low) < 2)
	    return;

	int middle = (low + high) / 2;
	shuttlesort(to, from, low, middle);
	shuttlesort(to, from, middle, high);

	int p = low;
	int q = middle;

	if((high - low) >= 4 && compare(from[middle - 1], from[middle]) <= 0)
	{
	    for(int i = low; i < high; i++)
		to[i] = from[i];

	    return;
	}

	for(int i = low; i < high; i++)
	{
	    if(q >= high || (p < middle && compare(from[p], from[q]) <= 0))
		to[i] = from[p++];

	    else
		to[i] = from[q++];
	}
    }

    /** Requests a sort by column
     *  @param column column number
     */
    private void sortByColumn(int column)
    {
	sortByColumn(column, true);
    }

    /** Performs a sort by column
     *  @param column column number
     *  @param ascending sort order
     */
    private void sortByColumn(int column, boolean ascending)
    {
	this.ascending = ascending;

	sortingColumns = new int[1];
	sortingColumns[0] = column;

	sort();

	((DefaultTableModel)dataModel).fireTableChanged(new TableModelEvent(dataModel));
    }

    /** Allows to catch mouse events */
    protected final void addMouseListenerToHeaderInTable()
    {
	setColumnSelectionAllowed(false); 
	
	MouseAdapter listMouseListener = new SortableTableListener(this);
	listeners.add(listMouseListener);

	JTableHeader th = getTableHeader();
	th.addMouseListener(listMouseListener); 
    }

    /** Remove every MouseListener attached to the table (needed after a change) */
    protected final void removeListeners()
    {
	JTableHeader th = getTableHeader();
	
	int size = listeners.size();

	for(int i = 0; i < size; i++)
	{
	    SortableTableListener l = (SortableTableListener)listeners.get(i);
	    th.removeMouseListener((MouseListener)l);
	}
    }

    /** Sorts the data contained in the table
     *  TODO : rewrite the code and/or implement a better sort
     *  @param column column number
     *  @param ascending sort order
     */
    private void dataSort(int column, boolean ascending)
    {
	try
	{
	    if(ascending)
	    {
		int j;
		int limit = rowData.length;
		int st = -1;
	    
		while(st < limit)
		{
		    st++;
		    limit--;
		    boolean swapped = false;

		    for(j = st; j < limit; j++) 
		    {
			if(rowData[j][column] instanceof String) 
			{
			    String s1 = (String)rowData[j][column];
			    String s2 = (String)rowData[j + 1][column];

			    if(s1.compareTo(s2) > 0)
			    {
				swap(j, (j+1));
				swapped = true;
				
				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}

			else if(rowData[j][column] instanceof Date)
			{
			    DateFormat df = DateFormat.getInstance();
			    df.setLenient(true);
			    
			    Date d1 = (Date)rowData[j][column];
			    Date d2 = (Date)rowData[j + 1][column];
			    
			    if(d1.after(d2))				
			    {
				swap(j, (j+1));
				swapped = true;
				
				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}
		    }

		    if(!swapped)
			return;

		    else
			swapped = false;
	    
		    for(j = limit; --j >= st;) 
		    {
			if(rowData[j][column] instanceof String)
			{
			    String s1 = (String)rowData[j][column];
			    String s2 = (String)rowData[j + 1][column];

			    if(s1.compareTo(s2) > 0)
			    {
				swap(j, (j+1));
				swapped = true;
				
				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}

			else if(rowData[j][column] instanceof Date)
			{
			    DateFormat df = DateFormat.getInstance();
			    df.setLenient(true);

			    Date d1 = (Date)rowData[j][column];
			    Date d2 = (Date)rowData[j + 1][column];

			    if(d1.after(d2))
			    {
			    	swap(j, (j+1));
				swapped = true;
				
				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}
		    }

		    if(!swapped)
			return;
		}
	    }
       
	    else
	    {
		int j;
		int limit = rowData.length;
		int st = -1;
		    
		while(st < limit)  
		{
		    st++;
		    limit--;
		    boolean swapped = false;

		    for(j = st; j < limit; j++) 
		    {
			if(rowData[j][column] instanceof String)
			{
			    String s1 = (String)rowData[j][column];
			    String s2 = (String)rowData[j + 1][column];

			    if(s1.compareTo(s2) < 0)
			    {
				swap(j, (j+1));
				swapped = true;

				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}

			else if(rowData[j][column] instanceof Date)
			{
			    DateFormat df = DateFormat.getInstance();
			    df.setLenient(true);

			    Date d1 = (Date)rowData[j][column];
			    Date d2 = (Date)rowData[j + 1][column];

			    if(d1.before(d2))
			    {
				swap(j, (j+1));
				swapped = true;

				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}
		    }

		    if(!swapped) 
			return;

		    else
			swapped = false;

		    for(j = limit; --j >= st;) 
		    {
			if(rowData[j][column] instanceof String)
			{
			    String s1 = (String)rowData[j][column];
			    String s2 = (String)rowData[j + 1][column];

			    if(s1.compareTo(s2) < 0)
			    {
				swap(j, (j+1));
				swapped = true;

				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}

			else if(rowData[j][column] instanceof Date)
			{
			    DateFormat df = DateFormat.getInstance();
			    df.setLenient(true);

			    Date d1 = (Date)rowData[j][column];
			    Date d2 = (Date)rowData[j + 1][column];

			    if(d1.before(d2))
			    {
				swap(j, (j+1));
				swapped = true;

				Object tmpId = new Object();
				tmpId = indexes[j];
				indexes[j] = indexes[j + 1];
				indexes[j + 1] = tmpId;
			    }
			}
		    }

		    if(!swapped)
			return;
		}
	    }
	}

	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    /** Swap two rows of the table
     *  @param j first row
     *  @param k second row
     */
    private void swap(int j, int k)
    {
	int size = columnsNames.length;

	Object T[] = new Object[size];

	for(int i = 0; i < size; i++)
	    T[i] = rowData[j][i];
	    
	for(int i = 0; i < size; i++)
	    rowData[j][i] = rowData[k][i];

	for(int i = 0; i < size; i++)
	    rowData[k][i] = T[i];
    }

    /** Listener for this class */
    private final class SortableTableListener extends MouseAdapter
    {
	/** Internal table */
	private JTable tableView;
	
	/** Constructor
	 *  @param table internal table
	 */
	protected SortableTableListener(JTable table)
	{
	    tableView = table;
	}

	/** Method invoked when a mouse event is triggered
	 *  @param e mouse event
	 */
	public final void mouseClicked(MouseEvent e)
	{
	    TableColumnModel columnModel = tableView.getColumnModel();   
	    int viewColumn = columnModel.getColumnIndexAtX(e.getX()); 
	    int column = tableView.convertColumnIndexToModel(viewColumn); 
		    
	    if(e.getClickCount() == 1 && column != -1)
	    {
		int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK; 
			    
		if(shiftPressed == 0)
		    ascending = true;
			    
		else
		    ascending = false;

		sortByColumn(column, ascending);
		dataSort(column, ascending);
		tableView.setModel(dataModel);
		setData(rowData, columnsNames);
	    }
	}
    }
}
