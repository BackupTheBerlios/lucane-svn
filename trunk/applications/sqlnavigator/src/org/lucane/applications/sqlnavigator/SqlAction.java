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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.sqlnavigator;

import java.io.Serializable;

import org.lucane.common.signature.Signable;

public class SqlAction implements Serializable, Signable
{
	public static final int GET_DRIVER_INFO = 1;
	public static final int GET_TABLE_NAMES = 2;
	public static final int EXECUTE_QUERY = 3;
	
	public int action;
	public Object params;

	public SqlAction(int action)
	{
	  this.action = action;
	  this.params = null;
	}
	
    public SqlAction(int action, Object params)
    {
    	this.action = action;
    	this.params = params;
    }
    
    public String toSignableString()
    {
    	return "" + action + params;
    }
}
