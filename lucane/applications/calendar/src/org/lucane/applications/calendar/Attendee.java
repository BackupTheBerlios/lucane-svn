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

import java.io.*;

public class Attendee implements Serializable
{
	//-- status
	public static final int STATUS_UNKNOWN = 0;
	public static final int STATUS_ACCEPTED = 1;
	public static final int STATUS_REFUSED = 2;
	
	//-- attributes
	private String user;
	private boolean mandatory;
	private int status;
	
	public Attendee(String user, boolean mandatory, int status)
	{
		this.user = user;
		this.mandatory = mandatory;
		this.status = status;
	}

	public Attendee(String user, boolean mandatory)
	{
		this(user, mandatory, Attendee.STATUS_UNKNOWN);
	}
	
	//-- setters
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	//-- getters
	public String getUser()
	{
		return this.user;
	}
	
	public boolean isMandatory()
	{
		return this.mandatory;
	}
	
	public int getStatus()
	{
		return this.status;
	}
}