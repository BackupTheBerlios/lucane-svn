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
 
package org.lucane.applications.calendar.widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.*;

import org.lucane.applications.calendar.CalendarPlugin;


/**
 * Month view of a calendar
 */
public class MonthView extends JPanel
implements MouseListener
{
	//-- display
	public JPanel headerPanel;
	public JPanel contentPanel;
	
	//-- listeners
	private ArrayList listeners;
	
	//-- attributes
	private int displayedMonth;
	private int displayedYear;
	

	/**
	 * Constructor
	 * Create an empty month view and set it to the current month and day
	 */
	public MonthView(CalendarPlugin plugin)
	{
		super(new BorderLayout());
		
		this.listeners = new ArrayList();
		
		this.headerPanel = new JPanel(new GridLayout(1, 7));
		this.contentPanel = new JPanel(new GridLayout(6, 7));
		this.add(headerPanel, BorderLayout.NORTH);
		this.add(contentPanel, BorderLayout.CENTER);
		
		for(int i=1;i<=7;i++)
			headerPanel.add(new DayHeader(plugin.tr("day."+i)));
				
		for(int i=0;i<42;i++)
		{
			DayItem day = new DayItem(""+i);
			day.addMouseListener(this);
			contentPanel.add(day);
		}
		
		Calendar c = Calendar.getInstance();
		setDisplayedMonth(c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
		setCurrentDay(c.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Display a month
	 * 
	 * @param month the month to display
	 * @param year the year to display
	 */
	public void setDisplayedMonth(int month, int year)
	{
		this.displayedMonth = month;
		this.displayedYear = year;
		int curMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		
		int day = this.getFirstDayOfDisplayedMonth();
		int max = this.getDaysOfDisplayedMonth();
		for(int i=0;i<day;i++)
		{
			DayItem item = getDayItem(i);
			item.setBackground(Color.LIGHT_GRAY);
			item.reset("");
		}
		for(int i=0;i<max;i++)
		{
			DayItem item = getDayItem(i+day);
			item.setBackground(Color.WHITE);
			item.reset(""+ (i+1));
		}
		for(int i=max+day;i<42;i++)
		{
			DayItem item = getDayItem(i);
			item.setBackground(Color.LIGHT_GRAY);
			item.reset("");
		}
		
		if(curMonth == month && curYear == curYear)
			setCurrentDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		else
			setCurrentDay(0);
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

	public void addEvent(int dayOfMonth, BasicEvent event)
	{
		getDayItem(dayOfMonth + getFirstDayOfDisplayedMonth()-1).addEvent(event);
	}
	
	/**
	 * Highlight the current day
	 * 
	 * @param dayOfMonth the day to select
	 */
	private void setCurrentDay(int dayOfMonth)
	{		
		for(int i=0;i<42;i++)
			getDayItem(i).setCurrentDay(false);
		
		if(dayOfMonth > 0)
			getDayItem(dayOfMonth + getFirstDayOfDisplayedMonth()-1).setCurrentDay(true);
	}

	/**
	 * Get the first day (of week) for the displayed month
	 * 
	 * @return the first day of week
	 */	
	private int getFirstDayOfDisplayedMonth()
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, this.displayedMonth-1);
		c.set(Calendar.YEAR, this.displayedYear);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		//as day labels don't respect locales, we have to enforce monday
		//instead of getFirstDayOfWeek() here to stay consistent
		int res = c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
		
		return res < 0 ? 6 : res;
	}
	
	/**
	 * Get the number of days for the displayed month
	 * 
	 * @return the number of days
	 */
	private int getDaysOfDisplayedMonth()
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, this.displayedMonth-1);
		c.set(Calendar.YEAR, this.displayedYear);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Get a DayItem associated to its index
	 * 
	 * @param index the index (0 - 42)
	 * @return the DayItem
	 */
	private DayItem getDayItem(int index)
	{
		return (DayItem)contentPanel.getComponent(index);
	}
		
	//-- mouse listener implementation
	
	public void mousePressed(MouseEvent me)	{}
	public void mouseReleased(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseClicked(MouseEvent me)
	{
		DayItem day = (DayItem)me.getSource();
		if(day.getDayOfMonth() < 0)
			return;
		
		Iterator i = listeners.iterator();
		while(i.hasNext())
		{
			CalendarListener listener = (CalendarListener)i.next();
			listener.onDayClick(day);
		}
	}
}


