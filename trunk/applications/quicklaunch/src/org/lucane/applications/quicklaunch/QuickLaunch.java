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
 
package org.lucane.applications.quicklaunch;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;


public class QuickLaunch
  extends StandalonePlugin
  implements ActionListener
{
	private static final String MAIN_INTERFACE = "org.lucane.applications.maininterface";
	
  private TrayIcon trayIcon;
  
  /**
   * Void contructor. Used by PluginLoader
   */
  public QuickLaunch()
  {
    this.starter = true;
  }

  /**
   * @param friends the Client the Plugin belongs to
   * @param starter is true if the Clients started the plugin (start() will be
   *        called instead of follow()
   * @return a new instance of the Plugin.
   */
  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new QuickLaunch();
  }


  /**
   * Show a dialog asking for the friend name
   */
  public void start()
  { 	
  	try {  		
	  	this.trayIcon = new TrayIcon(this.getImageIcon(), this.getTitle());
	} catch(Throwable t) {
		//no user message if we aren't on windows
		if(System.getProperty("os.name").startsWith("Win"))
			DialogBox.error(tr("err.noTray"));
		else
			Logging.getLogger().info("Not on windows, running MainInterface instead of QuickLaunch");
		
		PluginLoader.getInstance().run(MAIN_INTERFACE, new ConnectInfo[0]);
		Client.getInstance().setStartupPlugin(MAIN_INTERFACE);
		return;
	}
	
	addMenuToTray();
	
	this.trayIcon.setVisible(true);
	this.trayIcon.showInfo(tr("lucane.is.ready"), "Lucane Groupware");
  }

  private void addMenuToTray()
  {
  	HashMap categories = new HashMap();
  	
  	//create menu list	
  	for(int i=0; i<PluginLoader.getInstance().getNumberOfPlugins(); i++)
  	{
  		Plugin p = PluginLoader.getInstance().getPluginAt(i);

  		JMenu category = (JMenu)categories.get(p.getCategory());
  		if(category == null)
  		{
  			category = new JMenu(p.getCategory());
  			categories.put(p.getCategory(), category);
  		}
  		
  		ImageIcon icon = new ImageIcon();
  		try {
  			icon = new ImageIcon(new URL(p.getDirectory() + p.getIcon()));
  			icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
  		} catch(Exception e) {
  			//error at icon loading
  		}
  		
  		JMenuItem menu = new JMenuItem(p.getTitle(), icon);
  		menu.setName(p.getName());
  		menu.setToolTipText(p.getToolTip());
  		menu.addActionListener(this);
  		category.add(menu);
  	}

  	//copy menu to tray
  	Iterator keys = categories.keySet().iterator();
  	while(keys.hasNext())
  	{
  		Object key = keys.next();
  		if(!key.equals("Invisible"))
  			this.trayIcon.add((JMenuItem)categories.get(key));
  	}
  	
  	this.trayIcon.addSeparator();
  	
  	//open main interface
  	JMenuItem menu = new JMenuItem(tr("open"));
  	menu.setName("org.lucane.applications.maininterface");
  	menu.addActionListener(this);
  	this.trayIcon.add(menu);
  	
  	//exit
  	menu = new JMenuItem(tr("exit"));
  	menu.setName("exit");
  	menu.addActionListener(this);
  	this.trayIcon.add(menu);  	
  }
  
  public void actionPerformed(ActionEvent ae)
  {
  	JMenuItem src = (JMenuItem)ae.getSource();
  	  	
  	if(src.getName().equals("exit"))
  	{
      cleanExit();
      return;
	}
	
	PluginLoader.getInstance().run(src.getName(), new ConnectInfo[0]);
  }

  private void cleanExit()
  {
	Logging.getLogger().finer("QuickLaunch::cleanExit()");
	this.trayIcon.setVisible(false);

    while(true)
      exit();      
  }
}
