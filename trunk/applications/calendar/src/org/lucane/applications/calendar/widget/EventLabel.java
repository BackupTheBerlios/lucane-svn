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

import javax.swing.*;


/**
 * A label displaying an event
 */
public class EventLabel extends JLabel
{
	private BasicEvent event;
	
	/**
	 * Constructor
	 * 
	 * @param event the EventItem to display
	 */
	public EventLabel(BasicEvent event)
	{
		super(event.getTitle());
		this.event = event;
		this.setBackground(event.getColor());
		this.setOpaque(true);
		
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.TOP);
	}

	/**
	 * Get the displayed event
	 * 
	 * @return the displayed event
	 */
	public BasicEvent getEvent()
	{
		return this.event;
	}
}