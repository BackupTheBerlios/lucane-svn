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

package org.lucane.applications.reminder;

import java.util.*;
import org.lucane.applications.reminder.lookup.*;
import org.lucane.applications.reminder.lookup.calendar.CalendarLookup;

public class ReminderThread extends Thread
{
	private ArrayList lookups;
	
	public ReminderThread()
	{
		this.lookups = new ArrayList();
		this.lookups.add(new CalendarLookup());
		
		this.setDaemon(true);
	}
	
	public void run()
	{
		while(true) 
		{
			try {
				Thread.sleep(5*60*1000); //5 minutes
			} catch(InterruptedException ie) {}
			
			Iterator i = lookups.iterator();
			while(i.hasNext())
			{
				Lookup l = (Lookup)i.next();
				l.lookup();
			}
		}
	}
}