/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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
 
package org.lucane.applications.maininterface;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;


public class MainInterface
  extends Plugin
  implements ActionListener,
             UserListListener
{

  private final static int NB_BUTTONS = 8;

  /* plugins... */
  private Vector buttons;
  private Vector names;

  /* client components */
  private Client parent;
  private Communicator commu;
  private PluginLoader ploader;

  /* dialog components */
  private JFrame frame;
  private JPanel pnlUsers;
  private JPanel pnlPlugins;
  private JComboBox cmbGroups;
  private JList lstUsers;
  private JMenuBar mnuBar;
  private JMenu mnuMain;
  private JMenu mnuExtends;
  private JMenuItem mnuExit;

  /**
   * Void contructor. Used by PluginLoader
   */
  public MainInterface()
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
    return new MainInterface();
  }


  /**
   * Show a dialog asking for the friend name
   */
  public void start()
  {
    this.names = new Vector();
    this.buttons = new Vector();
    this.commu = Communicator.getInstance();
    this.parent = Client.getInstance();
    this.ploader = PluginLoader.getInstance();

    frame = new JFrame("Lucane - " + parent.getMyInfos().getName());
    frame.getContentPane().setLayout(new BorderLayout());
    frame.addWindowListener(this);
    mnuBar = new JMenuBar();
    mnuMain = new JMenu("Lucane");
    mnuExtends = new JMenu("Applications");
    mnuBar.add(mnuMain);
    mnuBar.add(mnuExtends);
    frame.setJMenuBar(mnuBar);
    mnuExit = new JMenuItem(tr("quit"));
    mnuExit.addActionListener(this);
    mnuMain.add(mnuExit);

    JPanel pnlButtons = new JPanel();
    pnlUsers = new JPanel();
    pnlPlugins = new JPanel();
    pnlButtons.setLayout(new BorderLayout());
    pnlPlugins.setLayout(new GridLayout(0, 1));
    pnlUsers.setLayout(new BorderLayout());
    frame.getContentPane().add(pnlUsers, BorderLayout.CENTER);
    frame.getContentPane().add(pnlButtons, BorderLayout.EAST);
    pnlButtons.add(pnlPlugins, BorderLayout.NORTH);
    cmbGroups = new JComboBox();
    lstUsers = new JList();
    pnlUsers.add(cmbGroups, BorderLayout.NORTH);
    pnlUsers.add(new JScrollPane(lstUsers), BorderLayout.CENTER);
    cmbGroups.addItem(tr("everyone"));

    Vector list = parent.getUserList();
    lstUsers.setListData(list);

    sortAndDisplayApps();

    parent.addUserListListener(this);
    
    frame.setIconImage(this.getImageIcon().getImage());
    frame.setSize(350, 400);
    frame.setVisible(true);
  }
  
  private void sortAndDisplayApps()
  {
  	int[] uses = new int[ploader.getNumberOfPlugins()];

  	for(int i = 0; i < ploader.getNumberOfPlugins(); i++)
  	{
  		Plugin p = ploader.getPluginAt(i);
  		
  		// how many times was this app launched ?
  		try {
  			uses[i] = Integer.parseInt(this.getLocalConfig().get(p.getName()));
  		} catch(Exception e) {
  			uses[i] = 0;
  		}

  		// add to the Applications menu
  		if(! p.getCategory().equals("Invisible"))
  		{
  			int j;
  			ImageIcon icon = null;

  			//resize icon
  			try {
  				icon = new ImageIcon(new URL(p.getDirectory() + p.getIcon()));
  				Logging.getLogger().finer("ICON: " + p.getDirectory() + p.getIcon());
  				icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
  			} catch(Exception e) {
  				DialogBox.error(tr("error") + e);
  			}

  			//add to the correct category
  			for(j = 0; j < mnuExtends.getMenuComponentCount(); j++)
  			{
  				JMenu menu = (JMenu)mnuExtends.getMenuComponent(j);

  				if(menu.getText().equals(p.getCategory()))
  				{
  					JMenuItem menuItem = new JMenuItem(p.getTitle(), icon);
  					menuItem.setToolTipText(p.getToolTip());
  					menuItem.addActionListener(this);
  					menu.add(menuItem);
  					buttons.addElement(menuItem);
  					names.addElement(p.getName());
  					break;
  				}
  			}

  			//this cat doesn't exist yet, let's create it
  			if(j == mnuExtends.getMenuComponentCount())
  			{
  				JMenu jm = new JMenu(p.getCategory());
  				JMenuItem jmi = new JMenuItem(p.getTitle(), icon);
  				jmi.addActionListener(this);
  				jm.add(jmi);
  				buttons.addElement(jmi);
  				names.addElement(p.getName());
  				mnuExtends.add(jm);
  			}
  		}
  	}

  	//-- sort and display apps in buttons
  	int[] positions = new int[NB_BUTTONS];
  	int max;
  	int cur;
  	int i = 0;

  	while(i < NB_BUTTONS && i < uses.length)
  	{
  		cur = -1;
  		max = -1;

  		for(int j = 0; j < uses.length; j++)
  		{
  			if(uses[j] > max)
  			{
  				boolean already = false;

  				for(int k = 0; k < i && ! already; k++)
  				{
  					if(positions[k] == j)
  						already = true;
  				}

  				Plugin p = ploader.getPluginAt(j);

  				if(! p.getCategory().equals("Invisible") && ! already)
  				{
  					max = uses[j];
  					cur = j;
  				}
  			}
  		}

  		if(cur >= 0)
  		{
  			positions[i] = cur;
  			
  			Plugin p = ploader.getPluginAt(cur);
  			ImageIcon iic = null;

  			try {
  				iic = new ImageIcon(new URL(p.getDirectory() + p.getIcon()));
  			} catch(Exception e) {
  				DialogBox.error(tr("error") + e);
  			}

  			JButton mybutton = new JButton(iic);
  			mybutton.addActionListener(this);
  			buttons.addElement(mybutton);
  			names.addElement(p.getName());
  			mybutton.setHorizontalAlignment(SwingConstants.LEFT);
  			mybutton.setText(p.getTitle());
  			mybutton.setToolTipText(p.getToolTip());
  			pnlPlugins.add(mybutton);
  		}

  		i++;
  	}  	
  }

  public void userListChanged(Vector logins)
  {
    lstUsers.setListData(logins);
  }


  public void actionPerformed(ActionEvent ae)
  {
    if(ae.getSource() == mnuExit)
    {
      cleanExit();
    }

    String name = "";
    for(int i = 0; i < buttons.size(); i++)
    {
      if(ae.getSource() == buttons.elementAt(i))
      {
        name = (String)names.elementAt(i);
        break;
      }
    }

    /* fetch users */
    Object[] lstv = lstUsers.getSelectedValues();
    ConnectInfo[] cis = new ConnectInfo[lstv.length];

    for(int i = 0; i < lstv.length; i++)
    {
		Logging.getLogger().finer("Selected: " + (String)lstv[i]);
      cis[i] = commu.getConnectInfo((String)lstv[i]);
    }

    //-- increment use counter
    int currentUse = 0;
    try {
    	currentUse = Integer.parseInt(this.getLocalConfig().get(name));
    } catch(Exception e) {
    	//nothing, not yet used
    }
    this.getLocalConfig().set(name, String.valueOf(currentUse+1));       

	Logging.getLogger().finer("PluginToLoad: " + name);
    ploader.run(name, cis);
  }

  public void windowClosing(WindowEvent we)
  {
    cleanExit();
  }

  private void cleanExit()
  {
	Logging.getLogger().finer("MainInterface::cleanExit()");
    parent.removeUserListListener(this);
    exit();
    
    //if we are the main plugin, let's exit everyone
    if(Client.getInstance().getStartupPlugin().equals(this.getName()))
    {	
    	while(true)
    		exit();
    }
  }
}
