package org.lucane.applications.rssreader;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import org.jperdian.rss2.*;
import org.jperdian.rss2.dom.*;
import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;

public class RssReader extends StandalonePlugin 
{
	private static String DEFAULT_RSS =
		"http://sourceforge.net/export/rss2_projnews.php?group_id=95945&rss_fulltext=1";

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
		String rss = getLocalConfig().get("rss.url", DEFAULT_RSS);

		System.setProperty("proxySet", getLocalConfig().get("proxySet", "false"));
		System.setProperty("proxyHost", getLocalConfig().get("proxyHost", ""));
		System.setProperty("proxyPort", getLocalConfig().get("proxyPort", ""));

		JFrame frame = new JFrame();
		JList list = new JList();
		DefaultListModel model = new DefaultListModel();
		list.setCellRenderer(new Renderer());
		list.setModel(model);
		frame.getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
		frame.setSize(600, 600);
		frame.show();

		try {
			URL sourceURL = new URL(rss);
			RssClient client = new RssClient(sourceURL);
			RssChannel channel = client.getData();
			frame.setTitle(channel.getTitle());
			
			List items = channel.getItemList();
			Iterator i = items.iterator();
			while (i.hasNext())
				model.addElement(i.next());
		}	catch (MalformedURLException mue) {
			DialogBox.error("Wrong url : " + mue);
		}	catch (RssException re) {
			DialogBox.error("RSS Error : " + re);
		}
	}
}
