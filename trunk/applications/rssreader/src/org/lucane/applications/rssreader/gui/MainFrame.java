package org.lucane.applications.rssreader.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import org.jperdian.rss2.RssException;
import org.jperdian.rss2.dom.RssChannel;
import org.lucane.applications.rssreader.RssReader;
import org.lucane.applications.rssreader.rss.ChannelInfo;
import org.lucane.client.Client;
import org.lucane.client.util.WidgetState;
import org.lucane.client.widgets.DialogBox;

public class MainFrame extends JFrame
implements ListSelectionListener, ActionListener
{
	private JButton btnRefresh;
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
		super(plugin.getTitle());
		this.setName("mainFrame");
		
		this.plugin = plugin;
		
		this.rssChannels = new DefaultListModel();
		this.rssItems = new DefaultListModel();
		
		btnRefresh = new JButton(Client.getIcon("refresh.png"));
		btnRefresh.setToolTipText(plugin.tr("btn.refresh"));
		btnRefresh.addActionListener(this);
		btnAddChannel = new JButton(Client.getIcon("add.png"));
		btnAddChannel.setToolTipText(plugin.tr("btn.addChannel"));
		btnAddChannel.addActionListener(this);
		btnRemoveChannel = new JButton(Client.getIcon("delete.png"));
		btnRemoveChannel.setToolTipText(plugin.tr("btn.removeChannel"));
		btnRemoveChannel.addActionListener(this);
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(1, 3));
		buttons.add(btnRefresh);
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
		
		
		split = new JSplitPane();
		split.setName("split");
		split.setTopComponent(channelPanel);
		split.setBottomComponent(new JScrollPane(items));
		split.setOneTouchExpandable(true);
		split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(120);
		
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(split, BorderLayout.CENTER);
		
		addWindowListener(plugin);
	}
	
	public void saveState()
	{
		WidgetState.save(plugin.getLocalConfig(), this);
		WidgetState.save(plugin.getLocalConfig(), split);
	}
	
	public void restoreState()
	{
		WidgetState.restore(plugin.getLocalConfig(), this);
		WidgetState.restore(plugin.getLocalConfig(), split);
	}

	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource().equals(btnRefresh))
			valueChanged(null);
		else if(ae.getSource().equals(btnRemoveChannel))
		{
			ChannelInfo channel = (ChannelInfo)channels.getSelectedValue();
			if(channel == null)
				return;
			
			if(DialogBox.question(plugin.getTitle(), "msg.removeChannel"))
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
	
	public void refreshChannelList() 
	{
		rssChannels.clear();
		Iterator i = plugin.getChannels().iterator();
		while(i.hasNext())
			rssChannels.addElement(i.next());		
	}	
}