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
	private JProgressBar status;
	
	public MainFrame(RssReader plugin)
	{
		super(plugin.getTitle());
		
		this.rssChannels = new DefaultListModel();
		this.rssItems = new DefaultListModel();
		
		
		JPanel channelPanel = new JPanel(new BorderLayout());
		channels = new JList(rssChannels);
		channels.addListSelectionListener(this);
		status = new JProgressBar();
		channelPanel.add(new JScrollPane(channels), BorderLayout.CENTER);
		channelPanel.add(status, BorderLayout.SOUTH);
		
		items = new JList(rssItems);
		items.setCellRenderer(new ItemRenderer());
		
		
		JSplitPane split = new JSplitPane();
		split.setTopComponent(channelPanel);
		split.setBottomComponent(new JScrollPane(items));
		split.setOneTouchExpandable(true);
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(120);
		
		getContentPane().add(split, BorderLayout.CENTER);
	}

	public void valueChanged(ListSelectionEvent lse)
	{
		final ChannelInfo channel = (ChannelInfo)channels.getSelectedValue();
		if(channel == null)
			return;

		
		Runnable refresh = new Runnable() {
			public void run() {
				try {							
					rssItems.clear();
					status.setIndeterminate(true);
					RssChannel rss = channel.getChannel();

					Iterator i = rss.getItemList().iterator();
					while (i.hasNext())
						rssItems.addElement(i.next());
					status.setIndeterminate(false);			
				}	catch (MalformedURLException mue) {
					DialogBox.error("Wrong url : " + mue);
				}	catch (RssException re) {
					DialogBox.error("RSS Error : " + re);
				}							
			}
		};

		new Thread(refresh).start();
	}
	
	public void addChannel(ChannelInfo info)
	{
		rssChannels.addElement(info);
	}
}