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
 
package org.lucane.applications.calendar.widget;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.*;

import org.lucane.applications.calendar.CalendarPlugin;


/**
 * Week view of a calendar
 */
public class WeekView extends JPanel
{
	//-- display
	public JPanel headerPanel;
	public JPanel contentPanel;
	
	//-- listeners
	private ArrayList listeners;
	
	private Color unworkedHour = new Color(160, 155, 150);
	private Color workedHour = new Color(255, 255, 222);
	private int workStart = 8;
	private int workEnd = 17;

	/**
	 * Constructor
	 * Create an empty wek view and set it to the current month and day
	 */
	public WeekView(CalendarPlugin plugin)
	{
		super(new BorderLayout());
		
		this.listeners = new ArrayList();
		
		unworkedHour = plugin.getColor("unworked", unworkedHour);
		workedHour = plugin.getColor("worked", workedHour);
		workStart = plugin.getLocalConfig().getInt("workStart", workStart);
		workEnd = plugin.getLocalConfig().getInt("workEnd", workEnd);
		
		this.headerPanel = new JPanel(new GridLayout(1, 7));
		this.contentPanel = new JPanel(new GridLayout(1, 7));
		this.add(headerPanel, BorderLayout.NORTH);
		this.add(contentPanel, BorderLayout.CENTER);
		
		for(int i=1;i<=7;i++)
			headerPanel.add(new DayHeader(plugin.tr("day."+i)));
				
		for(int i=0;i<7;i++)
		{
			DayView day = new DayView(unworkedHour, workedHour, workStart, workEnd);
			day.scrollToHour(workStart-1);
			day.setCalendarListeners(listeners);
			contentPanel.add(day);
		}
		
		Calendar c = Calendar.getInstance();
		setDisplayedWeek(c.get(Calendar.WEEK_OF_YEAR)+1, c.get(Calendar.YEAR));
	}
	
	/**
	 * Display a week
	 * 
	 * @param week the week to display
	 * @param year the year to display
	 */
	public void setDisplayedWeek(int week, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week-1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		contentPanel.removeAll();
		for(int i=0;i<7;i++)
		{			
			DayView day = new DayView(unworkedHour, workedHour, workStart, workEnd);
			day.scrollToHour(workStart-1);
			day.setCalendarListeners(listeners);
			contentPanel.add(day);
		}
	}
	
	/**
	 * Add a listener
	 * 
	 * @param listener the CalendarListener to add
	 */
	public void addCalendarListener(CalendarListener listener)
	{
		listeners.add(listener);
	}

	public void addEvent(int dayOfWeek, BasicEvent event)
	{
		getDayView(dayOfWeek-1).addEvent(event);
	}
	
	/**
	 * Get a DayView associated to its index
	 * 
	 * @param index the index (0 - 7)
	 * @return the DayView
	 */
	private DayView getDayView(int index)
	{
		return (DayView)contentPanel.getComponent(index);
	}
}


