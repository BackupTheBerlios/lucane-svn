/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.applications.calendar.gui;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

import org.lucane.client.Plugin;
import org.lucane.applications.calendar.Attendee;

public class AttendeeTable extends JTable
{
	public AttendeeTable(Plugin plugin, String name, ArrayList attendees)
	{
		super(new AttendeeTableModel(plugin, attendees));
		getColumnModel().getColumn(0).setMinWidth(16);
		getColumnModel().getColumn(0).setMaxWidth(20);
		getTableHeader().getColumnModel().getColumn(0).setHeaderValue("");
		getTableHeader().getColumnModel().getColumn(1).setHeaderValue(name);
		setRowSelectionAllowed(true);
		setShowVerticalLines(false);
	}

	public AttendeeTable(Plugin plugin, String name)
	{
		this(plugin, name, new ArrayList());	
	}
	
	public void setAttendees(ArrayList list)
	{
		AttendeeTableModel model = (AttendeeTableModel)this.getModel();
		model.setAttendees(list);
	}
	
	public void addAttendee(Attendee a)
	{
		AttendeeTableModel model = (AttendeeTableModel)this.getModel();
		model.addAttendee(a);
	}
	
	public Attendee getAttendeeAt(int row)
	{
		return ((AttendeeTableModel)this.getModel()).getAttendeeAt(row);
	}
	
	public ArrayList getAttendeeList()
	{
		return ((AttendeeTableModel)this.getModel()).geAttendeeList();
	}
	
	public void removeSelected()
	{
		int row = this.getSelectedRow();
		if(row >= 0)
			((AttendeeTableModel)this.getModel()).removeAt(row);
	}
}

class AttendeeTableModel extends AbstractTableModel
{
	private ArrayList attendees;

	private ImageIcon acceptedIcon;
	private ImageIcon refusedIcon;
	private ImageIcon unknownIcon;
	
	public AttendeeTableModel(Plugin plugin, ArrayList concepts)
	{		
		this.attendees = concepts;
		
		try {
			this.acceptedIcon = new ImageIcon(new URL(plugin.getDirectory() + "accepted.png"));
			this.refusedIcon = new ImageIcon(new URL(plugin.getDirectory() + "refused.png"));
			this.unknownIcon = new ImageIcon(new URL(plugin.getDirectory() + "unknown.png"));
		} catch(Exception e) {
			//should never happen, we know where our icons are
		}
	}

	public void setAttendees(ArrayList attendees)
	{
		this.attendees = attendees;
		this.fireTableDataChanged();
	}

	public void addAttendee(Attendee attendee)
	{
		this.attendees.add(attendee);
		this.fireTableDataChanged();
	}
	
	public Attendee getAttendeeAt(int row)
	{
		return (Attendee)attendees.get(row);
	}
	
	public void removeAt(int row)
	{
		if(row < 0)
			return;
		
		attendees.remove(row);
		this.fireTableDataChanged();
	}
	
	public ArrayList geAttendeeList()
	{
		return attendees;
	}
	
	public Object getValueAt(int x, int y)
	{
		Attendee a = (Attendee)attendees.get(x);
		
		if(y == 1)
			return a.getUser();
		
		if(a.getStatus() == Attendee.STATUS_ACCEPTED)
			return acceptedIcon;
		if(a.getStatus() == Attendee.STATUS_REFUSED)
			return refusedIcon;
		if(a.getStatus() == Attendee.STATUS_UNKNOWN)
			return unknownIcon;
		
		return "";
	}
	
	public Class getColumnClass(int c) 
	{
		return (c==0 ? ImageIcon.class : String.class);
	}

	public int getColumnCount()
	{
		return 2;
	}

	public int getRowCount()
	{
		return attendees.size();
	}
}
