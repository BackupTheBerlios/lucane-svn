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

import java.io.Serializable;
import java.util.HashMap;

import org.lucane.common.signature.Signable;

public class CalendarAction implements Serializable, Signable
{
	public static final int STORE_EVENT = 0;
	public static final int REMOVE_EVENT = 1;
	
	public static final int GET_EVENTS_FOR_USER = 2;
	public static final int GET_EVENTS_FOR_RESOURCE = 3;
	
	public static final int GET_RESOURCES = 4;
	public static final int GET_USERS = 5;
	
	public static final int GET_EVENT_TYPES = 6;
		
	public int action;
	private HashMap param;
	
	public CalendarAction(int action)
	{
		this.action = action;
		this.param = new HashMap();
	}
	
	public void set(String key, Object val)
	{
		param.put(key, val);
	}
	
	public Object get(String key)
	{
		return param.get(key);
	}
	
	public String toSignableString()
	{
		return "" + action;
	}
}