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
import javax.swing.*;


/**
 * A day item for the MonthView
 */
public class DayItem extends JPanel
{
	//-- colors constants
	public static Color EVENT_COLOR = new Color(200, 100, 50);

	private String dayOfMonth;
	
	/**
	 * Constructor
	 * 
	 * @param dayOfMonth the day of month for this day item
	 */
	public DayItem(String dayOfMonth)
	{
		super(new GridLayout(0, 1));		
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		this.dayOfMonth = dayOfMonth;
		
		JLabel label = new JLabel(dayOfMonth);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setVerticalAlignment(SwingConstants.TOP);
		this.add(label);
	}
	
	/**
	 * Reinit the DayItem.
	 * remove all events and reset the day of month
	 * 
	 * @param dayOfMonth the day of month for this day item
	 */
	public void reset(String dayOfMonth)
	{
		this.removeAll();
		this.dayOfMonth = dayOfMonth;
		
		JLabel label = new JLabel(dayOfMonth);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setVerticalAlignment(SwingConstants.TOP);
		this.add(label);
	}
	
	/**
	 * Add an event to this DayItem
	 * 
	 * @param text the event text to be displayed
	 */
	public void addEvent(BasicEvent event)
	{
		this.add(new EventLabel(event));
	}

	/**
	 * Sets the current day
	 * 
	 * @param current true if this item is the current day
	 */
	public void setCurrentDay(boolean current)
	{
		if(current)
			setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		else
			setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
	
	public int getDayOfMonth()
	{
		if(dayOfMonth.length() > 0)
			return Integer.parseInt(dayOfMonth);
		return -1;
	}
}