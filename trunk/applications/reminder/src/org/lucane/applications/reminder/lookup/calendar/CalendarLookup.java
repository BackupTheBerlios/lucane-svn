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

package org.lucane.applications.reminder.lookup.calendar;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.lucane.common.ConnectInfo;
import org.lucane.common.Logging;
import org.lucane.common.ObjectConnection;
import org.lucane.applications.reminder.*;
import org.lucane.applications.reminder.lookup.Lookup;
import org.lucane.server.ConnectInfoManager;
import org.lucane.server.Server;

public class CalendarLookup implements Lookup
{
//show reminder for events occuring in the next 15 minutes
	public static final int LOOKUP_TIME = 15; 
					
	//caches events id to avoid sending multiple reminders for an event
	private ArrayList remindedEvents;
	
	public CalendarLookup()
	{
		this.remindedEvents = new ArrayList();
	}
	
	public void lookup() 
	{		
		long start, end;
		
		Calendar cal = Calendar.getInstance();
		start = cal.getTimeInMillis();
		cal.add(Calendar.MINUTE, LOOKUP_TIME);
		end = cal.getTimeInMillis();
				
		ArrayList events = new ArrayList();
		
		try {
			events = getEvents(start, end);			
		} catch(Exception e) {
			Logging.getLogger().severe("Unable to get events !");
			e.printStackTrace();
		}
		
		for(int i=0;i<events.size();i++)
		{
			Event event = (Event)events.get(i);
			Integer id = new Integer(event.getId());
			if(this.remindedEvents.contains(id))
				continue;
			
			this.remindedEvents.add(id);
			
			ReminderInfos infos = new ReminderInfos("org.lucane.applications.calendar", event.getTitle(),
					event.getDescription());
			
			//send reminder to organizer
			try {
				ConnectInfo ci = ConnectInfoManager.getInstance().getConnectInfo(event.getOrganizer());
				if(ci != null)
				{	
					ObjectConnection oc = Server.getInstance().sendMessageTo(ci, "org.lucane.applications.reminder", "");
					oc.write(infos);
					//oc.close();
				}
			} catch(IOException ioe) {
				Logging.getLogger().warning("Unable to send reminder to organizer :  " +event.getOrganizer());
				ioe.printStackTrace();
			}
			
			//send reminder to attendees
			Iterator attendees = event.getAttendees(); 
			while(attendees.hasNext())
			{
				Attendee a = (Attendee)attendees.next();
				try {
					ConnectInfo ci = ConnectInfoManager.getInstance().getConnectInfo(a.getUser());
					if(ci != null)
					{	
						ObjectConnection oc = Server.getInstance().sendMessageTo(ci, "org.lucane.applications.reminder", "");
						oc.write(infos);
						oc.close();
					}
				} catch(IOException ioe) {
					Logging.getLogger().warning("Unable to send reminder to attendee :  " + a.getUser());
					ioe.printStackTrace();
				}				
			}
		}
	}
	
	private ArrayList getEvents(long start, long end)
	throws Exception
	{
		ArrayList events = new ArrayList();
		
		Connection c = Server.getInstance().getDBLayer().getConnection();
		Statement s = c.createStatement();
		
		String query = "SELECT * FROM CalEvents WHERE "
			+ "startTime >= " + start + " AND startTime <= " + end;		
		addEventsFromQuery(c, s, query, events);
				
		s.close();
		c.close();

		return events;
	}
	
	private void addEventsFromQuery(Connection c, Statement s, String query, ArrayList events)
	throws Exception
	{
		ResultSet r = s.executeQuery(query);
		while(r.next())
		{
			int id = r.getInt(1);
			String title = r.getString(2);
			String type = r.getString(3);
			String organizer = r.getString(4);
			boolean isPublic = (r.getInt(5) == 1);
			long mystart = r.getLong(6);
			long myend = r.getLong(7);
			int recurrence = r.getInt(8);
			String description = r.getString(9);

			Event event = new Event(id, type, title, organizer, isPublic, mystart, myend, recurrence, description);
			loadAttendees(c, event);
			events.add(event);
		}		
	}

	private void loadAttendees(Connection c, Event event)
	throws Exception
	{
		ArrayList attendees = new ArrayList();
		Statement s = c.createStatement();
		
		String query = "SELECT * FROM CalAttendees WHERE eventId = "
			+ event.getId();		

		ResultSet r = s.executeQuery(query);
		while(r.next())
		{
			String user = r.getString(2);
			boolean mandatory = (r.getInt(3) == 1);
			int status = r.getInt(4);
			
			Attendee attendee = new Attendee(user, mandatory, status);
			attendees.add(attendee);
		}
		
		event.setAttendees(attendees);
		
		r.close();
		s.close();
	}	
}
