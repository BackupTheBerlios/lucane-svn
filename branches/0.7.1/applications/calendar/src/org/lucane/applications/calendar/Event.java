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

package org.lucane.applications.calendar;

import java.awt.*;
import java.io.*;
import java.util.*;

import org.lucane.applications.calendar.widget.*;

public class Event implements Serializable, BasicEvent
{
	//-- recurrence
	public static final int RECUR_NONE = 0;
	public static final int RECUR_DAY = 1;
	public static final int RECUR_WEEK = 2;
	public static final int RECUR_MONTH = 3;
	
	//-- attributes
	private int id;
	private String type;
	private int colorRed; 
	private int colorGreen; 
	private int colorBlue; 
	private String title;
	private String organizer;
	private boolean isPublic;
	private long start;
	private long end;
	private int recurrence;
	private String description;
	
	private ArrayList attendees;
	private ArrayList resources;
	
	/**
	 * Constructor
	 */
	public Event(int id, String type, String title, String organizer, boolean isPublic, 
				 long start, long end, int recurence, String description)
	{
		this.id = id;
		this.type = type;
		this.title = title;
		this.organizer = organizer;
		this.isPublic = isPublic;
		this.start = start;
		this.end = end;
		this.recurrence = recurence;
		this.description = description;
		this.attendees = new ArrayList();
		this.resources = new ArrayList();
		this.colorRed = 30;
		this.colorGreen = 30;
		this.colorBlue = 200;
	}
	
	//-- setters
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setAttendees(ArrayList attendees)
	{
		this.attendees = attendees;
	}
	
	public void addAttendee(String user, boolean mandatory, int status)
	{
		this.attendees.add(new Attendee(user, mandatory, status));
	}
	
	public Attendee getAttendee(String user)
	{
		Iterator i = getAttendees();
		while(i.hasNext())
		{
			Attendee a = (Attendee)i.next();
			if(a.getUser().equals(user))
				return a;
		}
		
		return null;
	}

	public void setResources(ArrayList resources)
	{
		this.resources = resources;
	}
	
	public void addResource(String resource)
	{
		this.resources.add(resource);
	}
	
	public void setColor(Color color)
	{
		this.colorRed = color.getRed();
		this.colorGreen = color.getGreen();
		this.colorBlue = color.getBlue();
	}
	
	public void setColor(int r, int g, int b)
	{
		this.colorRed = r;
		this.colorGreen = g;
		this.colorBlue = b;
	}
	
	//-- getters
	public int getId()
	{
		return this.id;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getOrganizer()
	{
		return this.organizer;
	}
	
	public boolean isPublic()
	{
		return this.isPublic;
	}
	
	public long getStartTime()
	{
		return this.start;
	}
	
	public long getEndTime()
	{
		return this.end;
	}
	
	public int getRecurrence()
	{
		return this.recurrence;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public Iterator getAttendees()
	{
		return this.attendees.iterator();
	}
	
	public ArrayList getAttendeeList()
	{
		return this.attendees;
	}
	
	public Iterator getResources()
	{
		return this.resources.iterator();
	}
	
	
	//-- getters for BasicEvent
	
	public String getTitle() 
	{
		return title;
	}
	
	public int getStartHour() 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(start));
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public int getStartMinute() 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(start));
		return c.get(Calendar.MINUTE);	
	}
	
	public int getEndHour() 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(end));
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public int getEndMinute() 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(end));
		return c.get(Calendar.MINUTE);	
	}
	
	public Color getColor() 
	{
		return new Color(colorRed, colorGreen, colorBlue);
	}
	
	public String toString()
	{
		return "[" + id + ": " + title +"]";
	}
}
