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
import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.applications.calendar.*;
import org.lucane.applications.calendar.Event;

public class EventDescriptionPanel extends JPanel
implements ActionListener
{
	private transient CalendarPlugin plugin;
	private Event event;
	
	private Calendar calendar;
	
	private JComboBox type;
	private JTextField title;
	private JTextField organizer;
	private JCheckBox isPublic;
	private JComboBox day;
	private JComboBox month;
	private JComboBox year;
	private JComboBox startHour;
	private JComboBox startMinute;
	private JComboBox endHour;
	private JComboBox endMinute;
	private JComboBox recurence;
	private HTMLEditor description;
	
	public EventDescriptionPanel(CalendarPlugin plugin, Event event)
	{
		super(new BorderLayout());
		
		this.plugin = plugin;
		this.event = event;
		
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeInMillis(event.getStartTime());
		
		try {
			initFields();
		} catch(Exception e) {
			DialogBox.error(tr("err.unableToGetEventData"));
			e.printStackTrace();
		}
		
		JPanel top = new JPanel(new BorderLayout());
		
		//TODO uncomment recurence
		//-- labels
		JPanel labels = new JPanel(new GridLayout(0, 1));
		labels.add(new JLabel(tr("event.title")));
		labels.add(new JLabel(tr("event.type")));
		//labels.add(new JLabel(tr("event.recurence")));
		labels.add(new JLabel(tr("event.organizer")));
		labels.add(new JLabel(tr("event.public")));
		labels.add(new JLabel(tr("event.date")));
		labels.add(new JLabel(tr("event.start")));
		labels.add(new JLabel(tr("event.end")));
		top.add(labels, BorderLayout.WEST);
		
		//-- fields
		JPanel container;
		JPanel fields = new JPanel(new GridLayout(0, 1));
		fields.add(title);
		fields.add(type);
		//fields.add(recurence);
		fields.add(organizer);
		fields.add(isPublic);
		JPanel date = new JPanel(new BorderLayout());
		date.add(day, BorderLayout.WEST);
		date.add(month, BorderLayout.CENTER);
		date.add(year, BorderLayout.EAST);
		container = new JPanel(new BorderLayout());
		container.add(date, BorderLayout.WEST);
		fields.add(container);
		JPanel start = new JPanel(new GridLayout(1, 2));		
		start.add(startHour);
		start.add(startMinute);
		container = new JPanel(new BorderLayout());
		container.add(start, BorderLayout.WEST);
		fields.add(container);
		JPanel end = new JPanel(new GridLayout(1, 2));
		end.add(endHour);
		end.add(endMinute);
		container = new JPanel(new BorderLayout());
		container.add(end, BorderLayout.WEST);
		fields.add(container);
		top.add(fields, BorderLayout.CENTER);
		
		this.add(top, BorderLayout.NORTH);
		this.add(description, BorderLayout.CENTER);
	}
	
	private void initFields() 
	throws Exception
	{
		boolean isOrganizer = 
			event.getOrganizer().equals(Client.getInstance().getMyInfos().getName());
		
		//-- event title
		title = new JTextField(event.getTitle());
		title.setEditable(isOrganizer);

		//-- event type
		type = new JComboBox();
		Iterator i = plugin.getEventTypes().iterator();
		while(i.hasNext())
			type.addItem(i.next());
		type.setEnabled(isOrganizer);
		
		//-- event organizer
		organizer = new JTextField(event.getOrganizer());
		organizer.setEditable(false);
		
		//-- event recurence
		recurence = new JComboBox();
		recurence.addItem(tr("recur.none"));
		recurence.addItem(tr("recur.day"));
		recurence.addItem(tr("recur.week"));
		recurence.addItem(tr("recur.month"));
		recurence.setSelectedIndex(event.getRecurrence());
		recurence.setEnabled(isOrganizer);
		
		//-- event display
		isPublic = new JCheckBox("", event.isPublic());
		isPublic.setEnabled(isOrganizer);
		
		//-- event date
		day = new JComboBox(); 
		month = new JComboBox();
		year = new JComboBox();
		initDateCombos();
		day.setEnabled(isOrganizer);
		month.setEnabled(isOrganizer);
		year.setEnabled(isOrganizer);
		
		//-- event time
		startHour = new JComboBox();
		startMinute = new JComboBox();		
		endHour = new JComboBox();
		endMinute = new JComboBox();
		startHour.setEnabled(isOrganizer);
		startMinute.setEnabled(isOrganizer);
		endHour.setEnabled(isOrganizer);
		endMinute.setEnabled(isOrganizer);
		initHourCombos();
		
		
		//-- event description
		description = new HTMLEditor();		
		description.setText(event.getDescription());
		description.setBorder(BorderFactory.createTitledBorder(tr("event.description")));
		description.setEditable(isOrganizer);
		description.setToolbarVisible(isOrganizer);
	}
	
	private void initDateCombos()
	{
		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		day.removeAllItems();
		for(int i=1;i<=maxDays;i++)
			day.addItem(new Integer(i));
		day.setSelectedIndex(currentDay-1);
		
		int monthIndex = calendar.get(Calendar.MONTH);
		month.removeActionListener(this);
		month.removeAllItems();
		for(int i=1;i<=12;i++)
			month.addItem(tr("month." + i));

		month.setSelectedIndex(monthIndex);		
		month.addActionListener(this);
		
		int currentYear = calendar.get(Calendar.YEAR);
		year.removeActionListener(this);
		year.removeAllItems();
		for(int i=-3;i<4;i++)
			year.addItem(new Integer(currentYear+i));
		year.setSelectedIndex(3);
		year.addActionListener(this);
	}
	
	
	private void initHourCombos()
	{
		for(int i=0;i<10;i++)
		{
			startHour.addItem("0" +i);
			endHour.addItem("0" +i);
		}
		for(int i=10;i<24;i++)
		{
			startHour.addItem("" +i);
			endHour.addItem("" +i);
		}
		startHour.setSelectedIndex(event.getStartHour());
		endHour.setSelectedIndex(event.getEndHour());
		
		startMinute.addItem("00");
		startMinute.addItem("15");
		startMinute.addItem("30");
		startMinute.addItem("45");
		startMinute.setSelectedIndex(event.getStartMinute()/15);

		endMinute.addItem("00");
		endMinute.addItem("15");
		endMinute.addItem("30");
		endMinute.addItem("45");
		endMinute.setSelectedIndex(event.getEndMinute()/15);
	}
	
	public Event createEvent()
	{
		boolean isOrganizer = 
			event.getOrganizer().equals(Client.getInstance().getMyInfos().getName());
		
		//-- only organizer can change event data
		if(!isOrganizer)
			return this.event;
		
		int id = event.getId();
		String type = (String)this.type.getSelectedItem();
		String title = this.title.getText();
		String organizer = event.getOrganizer();
		boolean isPublic = this.isPublic.isSelected();
		int recurence = this.recurence.getSelectedIndex();
		String description = this.description.getText();
		
		calendar.set(Calendar.DAY_OF_MONTH, day.getSelectedIndex()+1);
		calendar.set(Calendar.MONTH, month.getSelectedIndex());
		calendar.set(Calendar.YEAR, ((Integer)year.getSelectedItem()).intValue());
		
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String)startHour.getSelectedItem()));
		calendar.set(Calendar.MINUTE, Integer.parseInt((String)startMinute.getSelectedItem()));
		long start = calendar.getTimeInMillis();
		
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String)endHour.getSelectedItem()));
		calendar.set(Calendar.MINUTE, Integer.parseInt((String)endMinute.getSelectedItem()));
		long end = calendar.getTimeInMillis();
		
		return new Event(id, type, title, organizer, isPublic, start, end, recurence, description);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		//-- combos
		if(ae.getSource() == month)
			calendar.set(Calendar.MONTH, month.getSelectedIndex());
		else if(ae.getSource() == year)
			calendar.set(Calendar.YEAR, ((Integer)year.getSelectedItem()).intValue());
		
		initDateCombos();
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}