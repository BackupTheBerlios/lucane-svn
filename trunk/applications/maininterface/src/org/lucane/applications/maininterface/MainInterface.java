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
import java.io.IOException;
import java.net.*;
import java.util.*;
import javax.swing.*;


public class MainInterface
  extends StandalonePlugin
  implements ActionListener,
             UserListListener
{
  private final int NB_BUTTONS = 8;

  private ConnectInfo service;
  
  /* plugins... */
  private Vector buttons;
  private Vector names;

  /* client components */
  private Client parent;
  private Communicator commu;
  private PluginLoader ploader;

  /* dialog components */
  private ManagedWindow window;
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

    this.service = commu.getConnectInfo(this.getName());
	
    window = new ManagedWindow(this, "Lucane - " + parent.getMyInfos().getName());
    window.setName("maininterface");
    
    window.getContentPane().setLayout(new BorderLayout());
    //window.addWindowListener(new PluginExitWindowListener(this));
    mnuBar = new JMenuBar();
    mnuMain = new JMenu("Lucane");
    mnuExtends = new JMenu(tr("applications"));
    mnuBar.add(mnuMain);
    mnuBar.add(mnuExtends);
    window.setJMenuBar(mnuBar);
    mnuExit = new JMenuItem(tr("quit"));
    mnuExit.addActionListener(this);
    mnuMain.add(mnuExit);

    JPanel pnlButtons = new JPanel();
    pnlUsers = new JPanel();
    pnlPlugins = new JPanel();
    pnlButtons.setLayout(new BorderLayout());
    pnlPlugins.setLayout(new GridLayout(0, 1));
    pnlUsers.setLayout(new BorderLayout());
    window.getContentPane().add(pnlUsers, BorderLayout.CENTER);
    window.getContentPane().add(pnlButtons, BorderLayout.EAST);
    pnlButtons.add(pnlPlugins, BorderLayout.NORTH);
    cmbGroups = new JComboBox();
    lstUsers = new JList();
    pnlUsers.add(cmbGroups, BorderLayout.NORTH);
    pnlUsers.add(new JScrollPane(lstUsers), BorderLayout.CENTER);
    
    initGroupCombo();
    

    Vector list = parent.getUserList();
    lstUsers.setListData(list);

    displayAppsInMenu();
    displayAppsAsButtons();

    parent.addUserListListener(this);
    
    window.setIconImage(this.getImageIcon16().getImage());
    window.setPreferredSize(new Dimension(350, 400));
    window.setExitPluginOnClose(true);
    window.show();
  }
  
  private void initGroupCombo()
  {
  	cmbGroups.addItem(tr("everyone"));
	try {
		Iterator i = getMyGroups().iterator();
		while(i.hasNext())
			cmbGroups.addItem(i.next());
	} catch (Exception e) {
		DialogBox.error(tr("err.unableToFetchGroups"));
	}
	cmbGroups.addActionListener(this);
  }
  
  private void displayAppsInMenu()
  {
	Iterator plugins = ploader.getPluginIterator();
  	for(int i=0;plugins.hasNext();i++)
  	{
  		Plugin p = (Plugin)plugins.next();
  		
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
  }
  
  private void displayAppsAsButtons()
  {
	//-- sort and display apps in buttons
	Iterator plugins = ploader.getPluginIterator();
	ArrayList pluginUse = new ArrayList();
	while(plugins.hasNext())
	{
		Plugin p = (Plugin)plugins.next();
		pluginUse.add(new PluginUse(p, getLocalConfig().get(p.getName())));
	}
	Collections.sort(pluginUse, Collections.reverseOrder());
	  
	int nbButtons = NB_BUTTONS;
	for(int i=0;i<nbButtons && i<pluginUse.size(); i++)
	{
		Plugin p = ((PluginUse)pluginUse.get(i)).getPlugin();
		if(! p.getCategory().equals("Invisible"))
		{
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
		else
			nbButtons++;
	}
  }  

  public void userListChanged(Vector logins)
  {
    refreshUserList();
  }
  
  private void refreshUserList()
  {
  	if(cmbGroups.getSelectedIndex() <= 0)
  		lstUsers.setListData(parent.getUserList());
  	else
  	{
  		try {
  			ArrayList groups = getUsersForGroup(cmbGroups.getSelectedItem().toString());
  			lstUsers.setListData(groups.toArray());
  		} catch(Exception e) {
  			DialogBox.error(tr("err.unableToFetchUsersForGroup"));
  			lstUsers.setListData(parent.getUserList());
  		}
  	}
  }


  public void actionPerformed(ActionEvent ae)
  {
  	if(ae.getSource() == cmbGroups)
  	{
  		refreshUserList();
  		return;
  	}

  	if(ae.getSource() == mnuExit)
    {
      this.window.dispose();
      cleanExit();
      return;
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
  
  private ArrayList getMyGroups() 
  throws IOException, ClassNotFoundException
  {
  	MainInterfaceAction action = new MainInterfaceAction(MainInterfaceAction.GET_MY_GROUPS);
  	ObjectConnection oc = commu.sendMessageTo(service, service.getName(), action);
  	ArrayList groups = (ArrayList)oc.read();
  	oc.close();
  	
  	return groups;
  }
  
  private ArrayList getUsersForGroup(String group) 
  throws IOException, ClassNotFoundException
  {
  	MainInterfaceAction action = new MainInterfaceAction(
  			MainInterfaceAction.GET_CONNECTED_USERS_FOR_GROUP, group);
  	ObjectConnection oc = commu.sendMessageTo(service, service.getName(), action);
  	ArrayList users = (ArrayList)oc.read();
  	oc.close();
  	
  	return users;
  }
  
  public void windowClosing(WindowEvent we)
  {
    cleanExit();
  }

  private void cleanExit()
  {
	Logging.getLogger().finer("MainInterface::cleanExit()");
	//WidgetState.save(getLocalConfig(), window);
    parent.removeUserListListener(this);
    exit();    
  }
}
