package org.lucane.applications.rssreader.gui;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import org.jperdian.rss2.RssException;
import org.jperdian.rss2.dom.RssChannel;
import org.lucane.applications.rssreader.RssReader;
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.client.widgets.DialogBox;

public class MainFrame extends JFrame
implements ListSelectionListener
{
	private JList channels;
	private JList items;
	private DefaultListModel rssChannels;
	private DefaultListModel rssItems;
	
	public MainFrame(RssReader plugin)
	{
		super(plugin.getTitle());
		
		this.rssChannels = new DefaultListModel();
		this.rssItems = new DefaultListModel();
		
		channels = new JList(rssChannels);
		channels.addListSelectionListener(this);
		
		items = new JList(rssItems);
		items.setCellRenderer(new ItemRenderer());
		
		JSplitPane split = new JSplitPane();
		split.setTopComponent(new JScrollPane(channels));
		split.setBottomComponent(new JScrollPane(items));
		split.setOneTouchExpandable(true);
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(200);
		
		getContentPane().add(split, BorderLayout.CENTER);
	}

	public void valueChanged(ListSelectionEvent lse)
	{
		ChannelInfo channel = (ChannelInfo)channels.getSelectedValue();
		if(channel == null)
			return;

		try {					
			RssChannel rss = channel.getChannel();
			this.setTitle(rss.getTitle());
	
			rssItems.clear();
			Iterator i = rss.getItemList().iterator();
			while (i.hasNext())
				rssItems.addElement(i.next());
		}	catch (MalformedURLException mue) {
			DialogBox.error("Wrong url : " + mue);
		}	catch (RssException re) {
			DialogBox.error("RSS Error : " + re);
		}			
	}
	
	public void addChannel(ChannelInfo info)
	{
		rssChannels.addElement(info);
	}
}