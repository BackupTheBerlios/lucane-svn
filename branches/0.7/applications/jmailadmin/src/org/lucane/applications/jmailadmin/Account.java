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
package org.lucane.applications.jmailadmin;

import java.io.Serializable;

public class Account implements Serializable
{
	public String user;
	public String address = "";
	public String type = "imap";
	public String inHost = "";
	public int inPort = 143;
	public String outHost = "";
	public int outPort = 25;
	public String login = "";
	public String password = "";
	
	public Account(String user)
	{
		this.user = user;
	}
	
	public Account(String user, String address, String type, String inHost, int inPort, String outHost, int outPort,
			String login, String password)
	{
		this.user = user;
		this.address = address;
		this.type = type;
		this.inHost = inHost;
		this.inPort = inPort;
		this.outHost = outHost;
		this.outPort = outPort;
		this.login = login;
		this.password = password;
	}
}