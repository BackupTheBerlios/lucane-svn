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

package org.lucane.applications.rssreader.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import org.jperdian.rss2.RssException;
import org.jperdian.rss2.dom.RssChannel;
import org.jperdian.rss2.dom.RssItem;
import org.lucane.applications.rssreader.RssReader;
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.applications.rssreader.rss.RssItemComparator;
import org.lucane.client.Client;
import org.lucane.client.util.WidgetState;
import org.lucane.client.widgets.DialogBox;
import org.lucane.client.widgets.ManagedWindow;

public class MainFrame extends ManagedWindow
implements ListSelectionListener, ActionListener, MouseListener
{
	private JButton btnRefresh;
	private JButton btnAggregate;
	private JButton btnAddChannel;
	private JButton btnRemoveChannel;
	
	private JSplitPane split;
	private JList channels;
	private JList items;
	private DefaultListModel rssChannels;
	private DefaultListModel rssItems;
	private JProgressBar status;
	private RssReader plugin;
	
	public MainFrame(RssReader plugin)
	{
		super(plugin, plugin.getTitle());
		this.setName("mainFrame");
		this.setIconImage(plugin.getImageIcon().getImage());
		
		this.plugin = plugin;
		
		this.rssChannels = new DefaultListModel();
		this.rssItems = new DefaultListModel();
		
		btnRefresh = new JButton(Client.getIcon("refresh.png"));
		btnRefresh.setToolTipText(plugin.tr("btn.refresh"));
		btnRefresh.addActionListener(this);
		btnAggregate = new JButton(plugin.getImageIcon("aggregate.png"));
		btnAggregate.setToolTipText(plugin.tr("btn.aggregate"));
		btnAggregate.addActionListener(this);
		btnAddChannel = new JButton(Client.getIcon("add.png"));
		btnAddChannel.setToolTipText(plugin.tr("btn.addChannel"));
		btnAddChannel.addActionListener(this);
		btnRemoveChannel = new JButton(Client.getIcon("delete.png"));
		btnRemoveChannel.setToolTipText(plugin.tr("btn.removeChannel"));
		btnRemoveChannel.addActionListener(this);
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(1, 4));
		buttons.add(btnRefresh);
		buttons.add(btnAggregate);
		buttons.add(btnAddChannel);
		buttons.add(btnRemoveChannel);
		buttonPanel.add(buttons, BorderLayout.WEST);
		
		JPanel channelPanel = new JPanel(new BorderLayout());
		channels = new JList(rssChannels);
		channels.addListSelectionListener(this);
		status = new JProgressBar();
		channelPanel.add(new JScrollPane(channels), BorderLayout.CENTER);
		channelPanel.add(status, BorderLayout.SOUTH);
		
		items = new JList(rssItems);
		items.setCellRenderer(new ItemRenderer());
		items.addMouseListener(this);
		
		
		split = new JSplitPane();
		split.setName("split");
		split.setTopComponent(channelPanel);
		split.setBottomComponent(new JScrollPane(items));
		split.setOneTouchExpandable(true);
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(120);
		
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(split, BorderLayout.CENTER);
		
		setExitPluginOnClose(true);
	}
	
	public void saveState()
	{
		WidgetState.save(plugin.getLocalConfig(), split);
	}
	
	public void restoreState()
	{
		WidgetState.restore(plugin.getLocalConfig(), split);
	}
	
	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource().equals(btnRefresh))
			valueChanged(null);
		else if(ae.getSource().equals(btnAggregate))
			showAllChannels();
		else if(ae.getSource().equals(btnRemoveChannel))
		{
			ChannelInfo channel = (ChannelInfo)channels.getSelectedValue();
			if(channel == null)
				return;
			
			if(DialogBox.question(plugin.getTitle(), plugin.tr("msg.removeChannel")))
			{
				plugin.removeChannel(channel);
				rssItems.clear();
			}
		}
		else if(ae.getSource().equals(btnAddChannel))
		{
			new ChannelDialog(this, plugin).show();
		}
		
	}
	
	public void valueChanged(ListSelectionEvent lse)
	{
		ChannelInfo channel = (ChannelInfo)channels.getSelectedValue();
		if(channel == null)
			return;
		
		showChannel(channel);
	}
	
	public void refreshChannelList() 
	{
		rssChannels.clear();
		Iterator i = plugin.getChannels().iterator();
		while(i.hasNext())
			rssChannels.addElement(i.next());		
	}	
	
	public void showChannel(final ChannelInfo channel)
	{
		Runnable refresh = new Runnable() {
			public void run() {
				try {							
					rssItems.clear();
					status.setIndeterminate(true);
					RssChannel rss = channel.getChannel();
					
					Iterator i = rss.getItemList().iterator();
					while (i.hasNext())
						rssItems.addElement(i.next());								
				} catch (MalformedURLException mue) {
					DialogBox.error(plugin.tr("err.wrongUrl")+ mue);
				} catch (RssException re) {
					//call proxy config if unable to connect
					if(re.getCause() != null && re.getCause() instanceof ConnectException)
					{
						if(DialogBox.question("err.connect", "msg.setupProxy"))
							new ProxyDialog(plugin).show();
					}
					else
						DialogBox.error(plugin.tr("err.rssError") + re);
				} finally {
					status.setIndeterminate(false);
				}
			}
		};
		
		new Thread(refresh).start();
	}
	
	public void showAllChannels() 
	{
		Runnable refresh = new Runnable() {
			public void run() {
				try {							
					channels.setSelectedValue(null, false);
					rssItems.clear();
					status.setIndeterminate(true);
					
					Iterator channels = plugin.getChannels().iterator();
					ArrayList allItems = new ArrayList();
					while(channels.hasNext())
					{
						ChannelInfo channel = (ChannelInfo)channels.next();
						Collection items = channel.getChannel().getItemList();
						allItems.addAll(items);
					}
					
					Collections.sort(allItems, new RssItemComparator());
					
					Iterator i = allItems.iterator();
					while (i.hasNext())
						rssItems.addElement(i.next());	
					
				} catch (MalformedURLException mue) {
					DialogBox.error(plugin.tr("err.wrongUrl")+ mue);
				} catch (RssException re) {
					//call proxy config if unable to connect
					if(re.getCause() != null && re.getCause() instanceof ConnectException)
					{
						if(DialogBox.question("err.connect", "msg.setupProxy"))
							new ProxyDialog(plugin).show();
					}
					else
						DialogBox.error(plugin.tr("err.rssError") + re);
				} finally {
					status.setIndeterminate(false);
				}
			}
		};
		new Thread(refresh).start();
	}
	
	public void mousePressed(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseClicked(MouseEvent me)
	{
		if(me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2)
		{
			RssItem item = (RssItem)items.getSelectedValue();
			if(item == null)
				return;
			
			plugin.openUrl(item.getLink());	
		}		
	}
}