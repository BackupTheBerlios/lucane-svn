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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.lucane.client.Client;
import org.lucane.client.widgets.*;
import org.lucane.applications.calendar.*;
import org.lucane.applications.calendar.Event;

public class EventFrame extends JFrame
implements ActionListener
{
	private transient CalendarPlugin plugin;
	private Event event;
	private DayPanel dayPanel;
	private MonthPanel monthPanel;
	
	private JButton btnSave;
	private JButton btnRemove;
	private JButton btnClose;

	private JButton btnAccept;
	private JButton btnReject;
	
	private EventDescriptionPanel description;
	private AttendeePanel attendees;
		
	public EventFrame(CalendarPlugin plugin, Event event, DayPanel day, MonthPanel month)
	{
		super(event.getTitle());
		
		this.plugin = plugin;
		this.event = event;
		this.dayPanel = day;
		this.monthPanel = month;
		
		//-- panels
		description = new EventDescriptionPanel(plugin, event);
		attendees = new AttendeePanel(plugin, event);

		//-- buttons
		this.btnSave = new JButton(tr("btn.save"));
		this.btnRemove = new JButton(tr("btn.remove"));
		this.btnClose = new JButton(tr("btn.close"));
		btnSave.addActionListener(this);
		btnRemove.addActionListener(this);
		btnClose.addActionListener(this);
		
		this.btnAccept = new JButton(tr("btn.accept"));
		this.btnReject = new JButton(tr("btn.reject"));
		btnAccept.setIcon(Client.getIcon("ok.png"));
		btnReject.setIcon(Client.getIcon("cancel.png"));
		btnAccept.addActionListener(this);
		btnReject.addActionListener(this);
				
		getContentPane().setLayout(new BorderLayout());
		
		//-- topbar
		JPanel topbar = new JPanel(new BorderLayout());
		initTopBar(topbar);
		getContentPane().add(topbar, BorderLayout.NORTH);
		
		//-- tabbed pane
		JTabbedPane tabs = new JTabbedPane();
		tabs.add(tr("tab.description"), description);
		tabs.add(tr("tab.attendees"), attendees);
		//TODO uncomment this when ready
		//tabs.add(tr("tab.resources"), new JPanel());
		getContentPane().add(tabs, BorderLayout.CENTER);
		
		//-- save & close
		JPanel buttonsContainer = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(1, 0));
		String userName = Client.getInstance().getMyInfos().getName();
		if(event.getOrganizer().equals(userName))
		{	
			buttons.add(btnSave);
			buttons.add(btnRemove);
		}
		buttons.add(btnClose);
		buttonsContainer.add(buttons, BorderLayout.EAST);
		getContentPane().add(buttonsContainer, BorderLayout.SOUTH);
		
		setSize(650, 400);
		setIconImage(plugin.getImageIcon().getImage());
	}
	
	private void initTopBar(JPanel bar)
	{
		String type = getUserType();
		bar.add(new JLabel(tr(type)), BorderLayout.WEST);

		if(!type.equals("organizer"))
		{
			JPanel buttons = new JPanel(new GridLayout(1, 2));
			buttons.add(btnAccept);
			buttons.add(btnReject);
			bar.add(buttons, BorderLayout.EAST);
			
			String userName = Client.getInstance().getMyInfos().getName();
			Attendee a = event.getAttendee(userName);
			btnAccept.setEnabled(a.getStatus() != Attendee.STATUS_ACCEPTED);
			btnReject.setEnabled(a.getStatus() != Attendee.STATUS_REFUSED);
		}
	}

	private String getUserType()
	{
		String userName = Client.getInstance().getMyInfos().getName();
		if(event.getOrganizer().equals(userName))
			return "organizer";

		Attendee a = event.getAttendee(userName);
		return  (a.isMandatory() ? "attendee.mandatory" : "attendee.optional");
	}

	public void actionPerformed(ActionEvent ae)
	{
		//-- close & save
		if(ae.getSource() == btnClose)
			this.dispose();
		else if(ae.getSource() == btnSave)
		{
			//TODO add resources to event			
			Event event = description.createEvent();
			event.setAttendees(attendees.getAttendees());

			try {
				plugin.storeEvent(event);
				DialogBox.info(tr("msg.eventStored"));	
				this.dispose();
			} catch(Exception e) {
				DialogBox.error(tr("err.unableToStoreEvent"));
				e.printStackTrace();
			}
			dayPanel.refreshView();
			monthPanel.refreshView();
		}
		else if(ae.getSource() == btnRemove)
		{
			try {
				plugin.removeEvent(event);
				DialogBox.info(tr("msg.eventRemoved"));
				this.dispose();
			} catch(Exception e) {
				DialogBox.error(tr("err.unableToRemoveEvent"));
				e.printStackTrace();
			}
			dayPanel.refreshView();
			monthPanel.refreshView();
		}
		
		//-- accept & refuse
		//TODO add refresh to attendee tab
		else if(ae.getSource() == btnAccept)
		{
			Attendee a = event.getAttendee(Client.getInstance().getMyInfos().getName());
			a.setStatus(Attendee.STATUS_ACCEPTED);
			try {
				plugin.storeEvent(event);
				btnAccept.setEnabled(a.getStatus() != Attendee.STATUS_ACCEPTED);
				btnReject.setEnabled(a.getStatus() != Attendee.STATUS_REFUSED);				
				DialogBox.info(tr("msg.statusChanged"));					
			} catch(Exception e) {
				DialogBox.error(tr("err.unableToChangeStatus"));
				e.printStackTrace();
			}
		}
		else if(ae.getSource() == btnReject)
		{
			Attendee a = event.getAttendee(Client.getInstance().getMyInfos().getName());
			a.setStatus(Attendee.STATUS_REFUSED);
			try {
				plugin.storeEvent(event);
				btnAccept.setEnabled(a.getStatus() != Attendee.STATUS_ACCEPTED);
				btnReject.setEnabled(a.getStatus() != Attendee.STATUS_REFUSED);			
				DialogBox.info(tr("msg.statusChanged"));	
			} catch(Exception e) {
				DialogBox.error(tr("err.unableToChangeStatus"));
				e.printStackTrace();
			}
		}	
	}
	
	public String tr(String s)
	{
		return plugin.tr(s);
	}
}