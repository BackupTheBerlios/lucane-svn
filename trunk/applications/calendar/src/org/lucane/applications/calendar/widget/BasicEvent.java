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

import java.awt.Color;

/**
 * A basic event
 */
public interface BasicEvent
{
	/**
	 * Get the event title
	 * 
	 * @return the event title
	 */
	public String getTitle();

	/**
	 * Get the event description
	 * 
	 * @return the event description
	 */
	public String getDescription();
	
	/**
	 * Get the event start hour
	 * 
	 * @return the event start hour
	 */
	public int getStartHour();
	
	/**
	 * Get the event start minute
	 * 
	 * @return the event start minute
	 */
	public int getStartMinute();
	
	/**
	 * Get the event end hour
	 * 
	 * @return the end hour
	 */
	public int getEndHour();

	/**
	 * Get the event end minute
	 * 
	 * @return the end minute
	 */
	public int getEndMinute();
	
	/**
	 * Get the color associated with this event
	 * 
	 * @return the color
	 */
	 public Color getColor();
}