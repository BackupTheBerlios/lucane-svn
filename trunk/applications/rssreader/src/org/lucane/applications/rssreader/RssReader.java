package org.lucane.applications.rssreader;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import org.jperdian.rss2.*;
import org.jperdian.rss2.dom.*;
import org.lucane.client.*;
import org.lucane.common.ConnectInfo;

public class RssReader extends StandalonePlugin
{
	public RssReader()
	{
		this.starter = true;
	}
	
	public Plugin init(ConnectInfo[] friends, boolean starter) {
		return new RssReader();
	}  
	
  public void start()
  {
/*	System.setProperty("proxySet", "true");
	System.setProperty("proxyHost", "cyclope");
	System.setProperty("proxyPort", "8080");
  */	
	JFrame frame = new JFrame();
	JList list = new JList();
	DefaultListModel model = new DefaultListModel();
	list.setCellRenderer(new Renderer());
	list.setModel(model);
	frame.getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
	frame.setSize(600, 600);
	frame.show();
	
	try {
	URL sourceURL     = new URL("http://sourceforge.net/export/rss2_projnews.php?group_id=95945&rss_fulltext=1");
	RssClient client  = new RssClient(sourceURL);
	RssChannel channel = client.getData();
	List items = channel.getItemList();
	Iterator i = items.iterator();
    while(i.hasNext())
    	model.addElement(i.next());			

	sourceURL     = new URL("http://linuxfr.org/backend.rss");
	client  = new RssClient(sourceURL);
	channel = client.getData();
	items = channel.getItemList();
	i = items.iterator();
	while(i.hasNext())
		model.addElement(i.next());			
	} catch(Exception e) {
		e.printStackTrace();
	}
  }
}
