package org.lucane.applications.rssreader;

import java.util.ArrayList;
import java.util.Iterator;

import org.lucane.applications.rssreader.gui.*;
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;
import org.lucane.common.ObjectConnection;

public class RssReader extends StandalonePlugin 
{
	private static String SF_NET =
		"http://sourceforge.net/export/rss2_projnews.php?group_id=95945&rss_fulltext=1";
	private static String DLFP =
		"http://linuxfr.org/backend.rss";

	private ConnectInfo service;
	
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
		this.service = Communicator.getInstance().getConnectInfo(getName());

		System.setProperty("proxySet", getLocalConfig().get("proxySet", "false"));
		System.setProperty("proxyHost", getLocalConfig().get("proxyHost", ""));
		System.setProperty("proxyPort", getLocalConfig().get("proxyPort", ""));

		MainFrame frame = new MainFrame(this);
		frame.refreshChannelList();
		frame.setSize(600, 600);
		frame.show();
	}
	
	public void addChannel(ChannelInfo channel)
	{
		RssAction action = new RssAction(RssAction.ADD_CHANNEL, channel);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), action);
		
		String ack = "Connection error";
		try {
			 ack = oc.readString();
		} catch(Exception e) {}
		
		if(! ack.equals("OK"))
			DialogBox.error("Failed to add channel : \n" + ack);

		oc.close();
	}
	
	public void removeChannel(ChannelInfo channel)
	{
		RssAction action = new RssAction(RssAction.REMOVE_CHANNEL, channel);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), action);
		
		String ack = "Connection error";
		try {
			ack = oc.readString();
		} catch(Exception e) {}
		
		if(! ack.equals("OK"))
			DialogBox.error("Failed to remove channel : \n" + ack);

		oc.close();
	}
	
	public ArrayList getChannels()
	{
		ArrayList channels;
		
		RssAction action = new RssAction(RssAction.GET_CHANNELS);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), action);
		
		String ack = "Connection error";
		try {
			ack = oc.readString();
			channels = (ArrayList)oc.read();
		} catch(Exception e) {
			channels = new ArrayList();
			e.printStackTrace();
		}
		
		if(! ack.equals("OK"))
			DialogBox.error("Failed to list channel : \n" + ack);
			 
		oc.close();
		
		return channels;
	}
}
