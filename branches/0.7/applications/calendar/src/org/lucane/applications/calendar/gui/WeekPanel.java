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
import java.util.*;

import org.lucane.client.widgets.DialogBox;
import org.lucane.applications.calendar.CalendarPlugin;
import org.lucane.applications.calendar.widget.*;
import org.lucane.applications.calendar.Event;

public class WeekPanel extends JPanel
{
	private WeekView view;
	
	private Calendar calendar;
	private CalendarListener listener;
	private String userName;
	
	private transient CalendarPlugin plugin;
	
	public WeekPanel(CalendarPlugin plugin, CalendarListener listener, String userName)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		
		this.listener = listener;
		this.userName = userName;
		
		calendar = Calendar.getInstance();
		view = new WeekView(plugin);
		view.addCalendarListener(listener);
		add(view, BorderLayout.CENTER);
		
		refreshView();
	}
	
	public void refreshView()
	{	
		view.setDisplayedWeek(calendar.get(Calendar.WEEK_OF_YEAR)+1, calendar.get(Calendar.YEAR));
		
		//-- get month interval (in milliseconds)
		long start, end;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);	
		start = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_WEEK, 7);
		end = cal.getTimeInMillis();
		
		//-- fetch and display events
		try {			
			//if no user, show my events
			ArrayList events;			
				events = plugin.getMyEvents(start, end);
			
			Iterator i = events.iterator();
			while(i.hasNext())
			{
				Event e = (Event)i.next();
				cal.setTimeInMillis(e.getStartTime());
				view.addEvent(cal.get(Calendar.DAY_OF_WEEK)-1, e);
			}
		} catch(Exception e) {
			DialogBox.error(e.getMessage());
			e.printStackTrace();
		}
		
		validate();
	}
	
	public void showWeek(Calendar c)
	{
		this.calendar = c;
		refreshView();
	}
	
	public Calendar getCalendar()
	{
		return (Calendar)calendar.clone();
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}