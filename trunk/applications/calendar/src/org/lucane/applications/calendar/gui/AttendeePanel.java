/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.lucane.client.Client;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.concepts.UserConcept;
import org.lucane.applications.calendar.*;
import org.lucane.applications.calendar.Event;

public class AttendeePanel extends JPanel
implements ActionListener
{
	private transient CalendarPlugin plugin;
	private Event event;
	
	private AttendeeTable attendees;
	private JButton btnAddMandatory;
	private JButton btnAddOptional;
	private JButton btnRemove;
	
	public AttendeePanel(CalendarPlugin plugin, Event event)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		this.event = event;
		
		this.attendees = new AttendeeTable(plugin, tr("event.attendees"), event.getAttendeeList());
		this.btnAddMandatory = new JButton(tr("btn.addMandatoryAttendee"));
		this.btnAddOptional = new JButton(tr("btn.addOptionalAttendee"));
		this.btnRemove = new JButton(tr("btn.removeAttendee"));
		this.btnAddMandatory.addActionListener(this);
		this.btnAddOptional.addActionListener(this);
		this.btnRemove.addActionListener(this);
		
		JPanel buttons = new JPanel(new GridLayout(3, 1));
		buttons.add(this.btnAddMandatory);
		buttons.add(this.btnAddOptional);
		buttons.add(this.btnRemove);
		JPanel container = new JPanel(new BorderLayout());
		container.add(buttons, BorderLayout.NORTH);
		
		this.add(new JScrollPane(attendees), BorderLayout.CENTER);
		
		boolean isOrganizer = event.getOrganizer().equals(Client.getInstance().getMyInfos().getName());
		if(isOrganizer)			
			this.add(container, BorderLayout.EAST);		
	}
	
	public ArrayList getAttendees()
	{
		return attendees.getAttendeeList();
	}
	
	public void refreshAttendees()
	{
		attendees.setAttendees(event.getAttendeeList());
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == btnAddMandatory || ae.getSource() == btnAddOptional)
		{
			try {
				ArrayList users = plugin.getUsers();
				int index = DialogBox.list(null, tr("userSelection"), tr("msg.selectUser"), new Vector(users));
				if(index < 0)
					return;
				
				UserConcept user = (UserConcept)users.get(index);
				boolean isMandatory = (ae.getSource() == btnAddMandatory);
				attendees.addAttendee(new Attendee(user.getName(), isMandatory));
			} catch(Exception e) {
				DialogBox.error(tr("err.unableToFetchUserList"));
				e.printStackTrace();
			}
		}
		else if(ae.getSource() == btnRemove)
		{
			attendees.removeSelected();
		}
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}