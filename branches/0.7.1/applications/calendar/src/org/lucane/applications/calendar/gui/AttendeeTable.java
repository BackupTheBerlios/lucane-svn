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

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.table.*;

import org.lucane.client.Plugin;
import org.lucane.applications.calendar.Attendee;
import org.lucane.applications.calendar.CalendarPlugin;

public class AttendeeTable extends JTable
implements MouseListener
{
	private CalendarPlugin plugin;
	
	public AttendeeTable(CalendarPlugin plugin, String name, ArrayList attendees)
	{
		super(new AttendeeTableModel(plugin, attendees));

		getColumnModel().getColumn(0).setMinWidth(16);
		getColumnModel().getColumn(0).setMaxWidth(20);
		getTableHeader().getColumnModel().getColumn(0).setHeaderValue("");
		getTableHeader().getColumnModel().getColumn(1).setHeaderValue(name);
		setRowSelectionAllowed(true);
		setShowVerticalLines(false);
		
		this.plugin = plugin;
		this.addMouseListener(this);
	}

	public AttendeeTable(CalendarPlugin plugin, String name)
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

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) 
	{
		if(e.getClickCount() == 2)
		{	
			Attendee a = this.getAttendeeAt(this.getSelectedRow());
			CalendarViewer viewer = new CalendarViewer(plugin, a.getUser());
			viewer.setPreferredSize(new Dimension(780, 550));
			viewer.setIconImage(plugin.getImageIcon().getImage());
			viewer.show();				
		}
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
		Collections.sort(attendees, new AttendeeComparator());
		
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
		Collections.sort(attendees, new AttendeeComparator());
		this.fireTableDataChanged();
	}

	public void addAttendee(Attendee attendee)
	{
		this.attendees.add(attendee);
		Collections.sort(attendees, new AttendeeComparator());
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
		{
			if(a.isMandatory())
				return "<html><b>" + a.getUser() + "</b></html>";
			
			return a.getUser();
		}
		
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

class AttendeeComparator implements Comparator
{
	public int compare(Object o1, Object o2) 
	{
		Attendee a1 = (Attendee)o1;
		Attendee a2 = (Attendee)o2;
		
		if(a1.isMandatory() && !a2.isMandatory())
			return -1;
		if(!a1.isMandatory() && a2.isMandatory())
			return 1;
		
		return a1.getUser().compareTo(a2.getUser());
	}
}
