package org.lucane.applications.rssreader;

import org.lucane.applications.rssreader.gui.*;
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.client.*;
import org.lucane.common.ConnectInfo;

public class RssReader extends StandalonePlugin 
{
	private static String SF_NET =
		"http://sourceforge.net/export/rss2_projnews.php?group_id=95945&rss_fulltext=1";
	private static String DLFP =
		"http://linuxfr.org/backend.rss";

	public RssReader() 
	{
		this.starter = true;
	}

	public Plugin init(ConnectInfo[] friends, boolean starter) 
	{
		return new RssReader();
	}

	public void start() 
	{
		//String rss = getLocalConfig().get("rss.url", DEFAULT_RSS);

		System.setProperty("proxySet", getLocalConfig().get("proxySet", "false"));
		System.setProperty("proxyHost", getLocalConfig().get("proxyHost", ""));
		System.setProperty("proxyPort", getLocalConfig().get("proxyPort", ""));

		MainFrame frame = new MainFrame(this);
		frame.addChannel(new ChannelInfo("lucane@sf", SF_NET));
		frame.addChannel(new ChannelInfo("linuxfr", DLFP));

		frame.setSize(600, 600);
		frame.show();
	}
}
