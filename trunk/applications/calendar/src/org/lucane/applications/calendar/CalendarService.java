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

import java.awt.Color;
import java.sql.*;
import java.util.*;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.DatabaseAbstractionLayer;
import org.lucane.server.store.Store;


public class CalendarService
extends Service
{
	private Store store;
	private DatabaseAbstractionLayer layer;

	/**
	 * Initialize the service.
	 * Called every time the server is started.
	 * 
	 * @param parent the Server
	 */
	public void init(Server parent) 
	{
		this.store = parent.getStore();
		this.layer = parent.getDBLayer();
	}

	/**
	 * Install the service.
	 * Only called the first time a service is initialized.
	 */
	public void install() 
	{		
		try {
			String dbDescription = getDirectory()	+ "db-calendar.xml";
			layer.getTableCreator().createFromXml(dbDescription);
			
			String query = "insert into CalEventTypes values ('default', 170, 140, 220)";
			Connection c = layer.openConnection();
			Statement st = c.createStatement();
			st.execute(query);
			st.close();
			c.close();
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to install CalendarService !");
			e.printStackTrace();
		}    
	}
	
	public void process(ObjectConnection oc, Message message) 
	{
		CalendarAction ca = (CalendarAction)message.getData();
		String user, resource;
		long start, end;
		boolean showAll;
		ArrayList list;

		try {
			switch(ca.action)
			{
			case CalendarAction.STORE_EVENT:
				storeEvent((Event)ca.get("event"));
				oc.write("OK");
				break;

			case CalendarAction.REMOVE_EVENT:
				removeEvent((Event)ca.get("event"));
				oc.write("OK");
				break;

			case CalendarAction.GET_EVENTS_FOR_USER:
				user = (String)ca.get("user");
				showAll = ((Boolean)ca.get("showAll")).booleanValue();
				start = ((Long)ca.get("startTime")).longValue();
				end = ((Long)ca.get("endTime")).longValue();

				list = getEventsForUser(user, showAll, start, end);
				oc.write("OK");
				oc.write(list);
				break;

			case CalendarAction.GET_EVENTS_FOR_RESOURCE:
				resource = (String)ca.get("resource");
				start = ((Long)ca.get("startTime")).longValue();
				end = ((Long)ca.get("endTime")).longValue();

				list = getEventsForResource(resource, start, end);
				oc.write("OK");
				oc.write(list);
				break;

			case CalendarAction.GET_RESOURCES:
				list = getResources();
				oc.write("OK");
				oc.write(list);
				break;
				
			case CalendarAction.GET_USERS:
				list = getUsers();
				oc.write("OK");
				oc.write(list);
				break;
				
			case CalendarAction.GET_EVENT_TYPES:
				list = getEventTypes();
				oc.write("OK");
				oc.write(list);
				break;							
			}
		} catch(Exception e) {
			try {
				oc.write("FAILED " + e);
			} catch(Exception e2) {}
			
			e.printStackTrace();
		}
		
	}

	//-- set

	private synchronized int getNextId()
	throws Exception
	{
		int id = 0;

		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("SELECT max(id)+1 FROM CalEvents");
		if(r.next())
			id = r.getInt(1);	

		r.close();
		s.close();
		c.close();

		return id;
	}
	
	private void storeEvent(Event event)
	throws Exception
	{
		Logging.getLogger().fine("Storing event : " + event.getTitle());
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		
		if(event.getId() < 0)
			event.setId(getNextId());
		else
			removeEvent(event);
			
		
		String query = "INSERT INTO CalEvents VALUES("
			+ "'" + event.getId() +  "', "
			+ "'" + event.getTitle() +  "', "
			+ "'" + event.getType() +  "', "
			+ "'" + event.getOrganizer() +  "', "
			+ "'" + (event.isPublic() ? 1 : 0) +  "', "
			+ "'" + event.getStartTime() +  "', "
			+ "'" + event.getEndTime() +  "', "
			+ "'" + event.getRecurrence() +  "', "
			+ "'" + event.getDescription() +  "')";
		s.execute(query);		

		storeAttendees(event);
		storeResources(event);

		s.close();
		c.close();
	}	

	private void storeAttendees(Event event)
	throws Exception
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		String query;

		Iterator attendees = event.getAttendees();
		while(attendees.hasNext())
		{
			Attendee attendee = (Attendee)attendees.next();
			query = "INSERT INTO CalAttendees VALUES("
				+ "'" + event.getId() + "', "
				+ "'" + attendee.getUser() + "', "
				+ "'" + (attendee.isMandatory() ? 1 : 0) + "', "
				+ "'" + attendee.getStatus() + "')";
			s.execute(query);
		}

		s.close();
		c.close();
	}
	
	private void storeResources(Event event)
	throws Exception
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		String query;

		Iterator resources = event.getResources();
		while(resources.hasNext())
		{
			String resource = (String)resources.next();
			query = "INSERT INTO CalResources VALUES("
				+ "'" + event.getId() + "', "
				+ "'" + resource + "')";
			s.execute(query);
		}

		s.close();
		c.close();
	}

	//-- remove

	private void removeEvent(Event event)
	throws Exception
	{
		if(event.getId() < 0)
			return;

		Logging.getLogger().fine("Removing event : " + event.getTitle());
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
					
		String query = "DELETE FROM CalEvents WHERE id='" + event.getId() + "'";
		s.execute(query);		
		query = "DELETE FROM CalAttendees WHERE eventId='" + event.getId() + "'";
		s.execute(query);		
		query = "DELETE FROM CalResources WHERE eventId='" + event.getId() + "'";
		s.execute(query);		

		s.close();
		c.close();
	}
	
	//-- get
	
	private ArrayList getEventsForUser(String user, boolean showAll, long start, long end)
	throws Exception
	{
		ArrayList events = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
					
		String query = "SELECT * FROM CalEvents WHERE "
			+"organizer = '" + user + "' AND "
			+ "startTime >= " + start + " AND endTime <= " + end;		
		addEventsFromQuery(s, query, events, showAll);
		
		query = "SELECT e.* FROM CalEvents e, CalAttendees a WHERE "
			+ "e.id = a.eventId AND "
			+ "a.userName = '" + user + "' AND "
			+ "e.startTime >= " + start + " AND e.endTime <= " + end;		
		addEventsFromQuery(s, query, events, showAll);
		
		s.close();
		c.close();

		return events;
	}
	
	private void addEventsFromQuery(Statement s, String query, ArrayList events, boolean showAll)
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

			if(showAll || isPublic)
			{
				Event event = new Event(id, type, title, organizer, isPublic, mystart, myend, recurrence, description);
				loadColor(event);
				loadAttendees(event);
				loadResources(event);
				events.add(event);
			}
			else 
			{
				Event event = new Event(id, "private", "private", "", isPublic, mystart, myend, recurrence, "");
				event.setColor(Color.GRAY);
				events.add(event);
			}
		}		
	}
	
	private ArrayList getEventsForResource(String object, long start, long end)
	throws Exception
	{
		ArrayList events = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
					
		String query = "SELECT e.* FROM CalEvents e, CalResources r WHERE "
			+ "e.id = r.eventId AND "
			+ "r.object = '" + object + "'";

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

			if(isPublic)
			{
				Event event = new Event(id, type, title, organizer, isPublic, mystart, myend, recurrence, description);
				loadColor(event);
				loadAttendees(event);
				loadResources(event);
				events.add(event);
			}
			else 
			{
				Event event = new Event(id, "private", "private", "", isPublic, mystart, myend, recurrence, "");
				event.setColor(Color.GRAY);
				events.add(event);
			}
		}
		
		s.close();
		c.close();
		
		return events;
	}

	private ArrayList getResources()
	throws Exception
	{
		ArrayList resources = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
					
		String query = "SELECT * FROM CalObjects";

		ResultSet r = s.executeQuery(query);
		while(r.next())
		{
			String object = r.getString(1);
			resources.add(object);
		}
		
		s.close();
		c.close();
		
		return resources;
	}

	private ArrayList getUsers()
	throws Exception
	{
		ArrayList users = new ArrayList();
		Iterator i = store.getUserStore().getAllUsers();
		while(i.hasNext())
			users.add(i.next());
		
		return users;
	}

	private void loadColor(Event event)
	throws Exception
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
					
		String query = "SELECT * FROM CalEventTypes WHERE type = '"
			+ event.getType() + "'";		

		ResultSet r = s.executeQuery(query);
		if(r.next())
		{
			int red = r.getInt(2);
			int green = r.getInt(3);
			int blue = r.getInt(4);
			
			
			Color color = new Color(red, green, blue);
			event.setColor(color);
		}
		
		r.close();
		s.close();
		c.close();
	}
	
	private ArrayList getEventTypes()
	throws Exception
	{
		ArrayList types = new ArrayList();
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		
		String query = "SELECT * FROM CalEventTypes;";		

		ResultSet r = s.executeQuery(query);
		while(r.next())
			types.add(r.getString(1));
		
		r.close();
		s.close();
		c.close();
		
		return types;
	}
	
	private void loadAttendees(Event event)
	throws Exception
	{
		ArrayList attendees = new ArrayList();
		Connection c = layer.openConnection();
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
		c.close();
	}
	
	private void loadResources(Event event)
	throws Exception
	{
		ArrayList resources = new ArrayList();
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
					
		String query = "SELECT * FROM CalResources WHERE eventId = "
			+ event.getId();		

		ResultSet r = s.executeQuery(query);
		while(r.next())
		{
			String object = r.getString(2);
			resources.add(object);
		}
		
		event.setResources(resources);
		
		r.close();
		s.close();
		c.close();
	}
}

