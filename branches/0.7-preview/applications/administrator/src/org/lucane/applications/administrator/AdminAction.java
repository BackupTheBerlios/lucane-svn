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

package org.lucane.applications.administrator;

import java.io.Serializable;

import org.lucane.common.signature.Signable;

public class AdminAction implements Serializable, Signable
{
	public static final int GET_ALL_GROUPS = 0;
	public static final int GET_ALL_USERS = 1;
	public static final int GET_ALL_PLUGINS = 2;
	public static final int GET_ALL_SERVICES = 3;
	public static final int GET_AUTHORIZED_PLUGINS = 4;

	public static final int STORE = 5;
	public static final int UPDATE = 6;
	public static final int REMOVE = 7;

	public static final int GET_USERS_FOR = 8;
	
	
	public int action;
	public Object param;
	
	public AdminAction(int action, Object param)
	{
		this.action = action;
		this.param = param;
	}
	
	public AdminAction(int action)
	{
		this(action, null);
	}
	
	public String toSignableString()
	{
		return "" + action;
	}
}