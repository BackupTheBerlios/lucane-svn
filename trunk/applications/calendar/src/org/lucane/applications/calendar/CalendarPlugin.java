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

package org.lucane.applications.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.util.*;

import org.lucane.client.*;
import org.lucane.client.util.PluginExitWindowListener;
import org.lucane.common.*;
import org.lucane.applications.calendar.gui.CalendarFrame;

//TODO activate event recurence
//TODO activate resources
//TODO activate optional attendees

public class CalendarPlugin extends StandalonePlugin
{
	private ConnectInfo service;
	
	//-- from Plugin
	public CalendarPlugin()
	{
		this.starter = true;
	}
		
	public Plugin newInstance(ConnectInfo[] friends, boolean starter)
	{
		return new CalendarPlugin();
	}

	public void start()
	{
		service = Communicator.getInstance().getConnectInfo("org.lucane.applications.calendar");								
		
		//-- display month view
		CalendarFrame cf = new CalendarFrame(this);
		cf.setExitPluginOnClose(true);
		cf.setPreferredSize(new Dimension(780, 550));
		cf.setIconImage(this.getImageIcon().getImage());
		cf.show();	 
	}
	
	public void storeEvent(Event e)
	throws Exception
	{
		CalendarAction ca = new CalendarAction(CalendarAction.STORE_EVENT);
		ca.set("event", e);
		
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
			service, service.getName(), ca);
		
		oc.readString(); //OK or FAILED
		
		oc.close();
	}
	
	public void removeEvent(Event e)
	throws Exception
	{
		CalendarAction ca = new CalendarAction(CalendarAction.REMOVE_EVENT);
		ca.set("event", e);
		
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
			service, service.getName(), ca);
		
		oc.readString(); //OK or FAILED
		
		oc.close();
	}
		
	public ArrayList getEventTypes()
	throws Exception
	{
		ArrayList types;
		
		CalendarAction ca = new CalendarAction(CalendarAction.GET_EVENT_TYPES);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), ca);
		
		if(oc.readString().equals("OK"))
			types = (ArrayList)oc.read();
		else
			types = null;
		
		oc.close();
		
		return types;
	}
	
	private ArrayList getEventsForUser(String user, boolean showAll, long start, long end)
	throws Exception
	{
		ArrayList events;
		
		CalendarAction ca = new CalendarAction(CalendarAction.GET_EVENTS_FOR_USER);
		ca.set("user", user);
		ca.set("showAll", Boolean.valueOf(showAll));
		ca.set("startTime", new Long(start));
		ca.set("endTime", new Long(end));
		
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
			service, service.getName(), ca);
	
		if(oc.readString().equals("OK"))
			events = (ArrayList)oc.read();
		else
			events = null;
			
		oc.close();
		
		return events;
	}

	public ArrayList getEventsForUser(String user, long start, long end)
	throws Exception
	{
		return getEventsForUser(user, false, start, end);
	}
	
	public ArrayList getMyEvents(long start, long end)
	throws Exception
	{
		return getEventsForUser(Client.getInstance().getMyInfos().getName(),
			true, start, end);
	}
	
	private ArrayList getEventsForResource(String object, long start, long end)
	throws Exception
	{
		ArrayList events;
		
		CalendarAction ca = new CalendarAction(CalendarAction.GET_EVENTS_FOR_RESOURCE);
		ca.set("resource", object);
		ca.set("startTime", new Long(start));
		ca.set("endTime", new Long(end));
	
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
			service, service.getName(), ca);
	
		if(oc.readString().equals("OK"))
			events = (ArrayList)oc.read();
		else
			events = null;
			
		oc.close();
		
		return events;
	}
	
	public ArrayList getResources()
	throws Exception
	{
		ArrayList resources = new ArrayList();
		CalendarAction ca = new CalendarAction(CalendarAction.GET_RESOURCES);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
			service, service.getName(), ca);
		
		if(oc.readString().equals("OK"))
			resources = (ArrayList)oc.read();
		else
			resources = null;
		
		oc.close();
		
		return resources;
	}
	
	public ArrayList getUsers()
	throws Exception
	{
		ArrayList resources = new ArrayList();
		CalendarAction ca = new CalendarAction(CalendarAction.GET_USERS);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), ca);
		
		if(oc.readString().equals("OK"))
			resources = (ArrayList)oc.read();
		else
			resources = null;
		
		oc.close();
		
		return resources;
	}
	
	public Color getColor(String name, Color defaultColor)
	{
		String r = getLocalConfig().get(name + ".r");
		String g = getLocalConfig().get(name + ".g");
		String b = getLocalConfig().get(name + ".b");
		
		if(r != null && g != null && b != null)
			return new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
		
		return defaultColor;	
	}
}