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

package org.lucane.applications.rssreader;

import java.io.Serializable;

import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.common.signature.Signable;

public class RssAction implements Serializable, Signable
{
	public static final int GET_CHANNELS = 0;
	public static final int ADD_CHANNEL = 1;
	public static final int REMOVE_CHANNEL = 2;
		
	public int action;
	public ChannelInfo channel;
	
	public RssAction(int action, ChannelInfo channel)
	{
		this.action = action;
		this.channel = channel;
	}
	
	public RssAction(int action)
	{
		this(action, null);
	}
	
	public String toSignableString()
	{
		return "" + action;
	}
}