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
}