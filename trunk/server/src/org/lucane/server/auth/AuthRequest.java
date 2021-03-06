/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004 Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.server.auth;

import org.lucane.common.ConnectInfo;

public class AuthRequest 
{
	private ConnectInfo userInfo;
	private String md5Passwd;
	
	public AuthRequest(ConnectInfo userInfo, String md5Passwd)
	{
		this.userInfo = userInfo;
		this.md5Passwd = md5Passwd;
	}
	
	public ConnectInfo getUserInfo()
	{
		return this.userInfo;
	}
	
	public String getMd5Passwd()
	{
		return this.md5Passwd;
	}
}
