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
package org.lucane.applications.notes;

import java.io.Serializable;

import org.lucane.common.signature.Signable;

/**
 * This class represents an action transiting between the plugin and the service
 */
class NotesAction
  implements Serializable, Signable
{
	//-- actions
	public static final int SAVE_NOTE = 0;
	public static final int DELETE_NOTE = 1;
	public static final int GET_PERSONNAL_NOTES = 2;
	public static final int GET_PUBLISHED_AUTHORS = 3;
	public static final int GET_RECENT_PUBLISHED_NOTES = 4;
	public static final int GET_PUBLISHED_NOTES_BY_AUTHOR = 5;
	public static final int SAVE_COMMENT = 6;
	public static final int GET_COMMENTS_FOR_NOTE = 7;
	
	//-- attributes
	private int action;
	private Object param;
	
	public NotesAction(int action, Object param)
	{
		this.action = action;
		this.param = param;
	}
	
	public NotesAction(int action)
	{
		this.action = action;
		this.param = null;
	}
	
	public int getAction()
	{
		return this.action;
	}
	
	public Object getParam()
	{
		return this.param;
	}
	
	public String toSignableString()
	{
		if(param instanceof Signable)
			return "" + action + ((Signable)param).toSignableString();
		else
			return "" + action + param;
	}
}
