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

package org.lucane.applications.rssreader.rss;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.jperdian.rss2.RssClient;
import org.jperdian.rss2.RssException;
import org.jperdian.rss2.dom.RssChannel;

public class ChannelInfo implements Serializable
{
	private String name;
	private String url;
	
	public ChannelInfo(String name, String url)
	{
		this.name = name;
		this.url = url;
	}
	
	public String toString()
	{
		return name;
	}
	
	public RssChannel getChannel() 
	throws MalformedURLException, RssException
	{
		RssClient client = new RssClient(new URL(url));
		return client.getData();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getUrl()
	{
		return this.url;
	}
}