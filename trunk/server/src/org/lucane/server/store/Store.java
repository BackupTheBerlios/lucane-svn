/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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

package org.lucane.server.store;

import org.lucane.server.*;
import org.lucane.common.concepts.*;
import org.lucane.common.crypto.MD5;
import org.lucane.server.store.sql.*;
import org.lucane.server.store.ldap.*;
import org.lucane.common.Logging;

/**
 * The unique way to get Stores for the different concepts.
 */
public class Store
{    
    private GroupStore group;
    private PluginStore plugin;
    private ServiceStore service;
    private UserStore user;
    
    /**
     * Constructor.
     * 
     * Instantiate the managers and set some initial data if necessary
     */
    public Store(ServerConfig config)
    throws Exception
    {
		String backend = config.getStoreBackend();
		
        if(backend.equals("database"))
        {	
        	Logging.getLogger().info("Using database backend");
        	this.group = new SqlGroupStore(this);         
        	this.plugin = new SqlPluginStore();
        	this.service = new SqlServiceStore();
        	this.user = new SqlUserStore();
        }
        else if(backend.equals("ldap"))
        {
        	Logging.getLogger().info("Using ldap backend");
        	LdapConfig ldapConfig = new LdapConfig(LdapConfig.CONFIG_FILE);
			this.group = new LdapGroupStore(this, ldapConfig);         
			this.plugin = new LdapPluginStore(ldapConfig);
			this.service = new LdapServiceStore(ldapConfig);
			this.user = new LdapUserStore(ldapConfig);
        }
        else
        	throw new Exception("no such backend : '" + backend + "'");
        
        if(! this.group.isInitialized())
            setInitialData();
    }
    
    /**
     * Get the GroupStore
     * 
     * @return the GroupStore instance
     */
	public GroupStore getGroupStore()
	{
		return group;
	}
	
	/**
	 * Get the PluginStore
	 * 
	 * @return the PluginStore instance
	 */
	public PluginStore getPluginStore()
	{
		return plugin;
	}
	
	/**
	 * Get the ServiceStore
	 * 
	 * @return the ServiceStore instance
	 */
	public ServiceStore getServiceStore()
	{
		return service;
	}
	
	/**
	 * Get the UserStore
	 * 
	 * @return the UserStore instance
	 */
	public UserStore getUserStore()
	{
		return user;
	}
    
    /**
     * Set some default initial data.
     * Called by the constructor.
     */
    private void setInitialData()
    throws Exception
    {
        //groups
        GroupConcept allUsers = new GroupConcept("AllUsers");
        GroupConcept grantedUsers = new GroupConcept("GrantedUsers");
		GroupConcept admins = new GroupConcept("Admins");
        admins.addParent(allUsers);
        grantedUsers.addParent(allUsers);
        
        //admin user
        UserConcept admin = new UserConcept("admin", MD5.encode("admin"), false, "org.lucane.applications.quicklaunch");
        admin.generateKeys("admin");
        admins.addUser(admin);
        getUserStore().storeUser(admin);

		//shutdown user
		UserConcept shutdown = new UserConcept("shutdown", MD5.encode("shutdown"), false, "org.lucane.applications.shutdown");
		shutdown.generateKeys("shutdown");
		admins.addUser(shutdown);
		getUserStore().storeUser(shutdown);
		
        //guest user
        UserConcept guest = new UserConcept("guest", MD5.encode("guest"), false, "org.lucane.applications.quicklaunch");
        guest.generateKeys("guest");
        allUsers.addUser(guest);
        getUserStore().storeUser(guest);
        
        //granted user
        UserConcept granted = new UserConcept("granted", MD5.encode("granted"), false, "org.lucane.applications.quicklaunch");
        granted.generateKeys("granted");
        grantedUsers.addUser(granted);
        getUserStore().storeUser(granted);
        
        //administrator application
        PluginConcept administratorPlugin = new PluginConcept("org.lucane.applications.administrator", "0.7");
        ServiceConcept administratorService = new ServiceConcept("org.lucane.applications.administrator", false);
        admins.addPlugin(administratorPlugin);
        admins.addService(administratorService);
        getPluginStore().storePlugin(administratorPlugin);
        getServiceStore().storeService(administratorService);
        
		//shutdown application
		 PluginConcept shutdownPlugin = new PluginConcept("org.lucane.applications.shutdown", "0.7");
		 ServiceConcept shutdownService = new ServiceConcept("org.lucane.applications.shutdown", false);
		 admins.addPlugin(shutdownPlugin);
		 admins.addService(shutdownService);
		 getPluginStore().storePlugin(shutdownPlugin);
		 getServiceStore().storeService(shutdownService);


        //filemanager application
        PluginConcept filemanagerPlugin = new PluginConcept("org.lucane.applications.filemanager", "0.7");
        ServiceConcept filemanagerService = new ServiceConcept("org.lucane.applications.filemanager", false);
        grantedUsers.addPlugin(filemanagerPlugin);
        allUsers.addService(filemanagerService);
        getPluginStore().storePlugin(filemanagerPlugin);
        getServiceStore().storeService(filemanagerService);
          
        //forum application
        PluginConcept forumPlugin = new PluginConcept("org.lucane.applications.forum", "0.7");
        ServiceConcept forumService = new ServiceConcept("org.lucane.applications.forum", false);
        allUsers.addPlugin(forumPlugin);
        allUsers.addService(forumService);
        getPluginStore().storePlugin(forumPlugin);
        getServiceStore().storeService(forumService);
        
        //forumadmin application
        PluginConcept forumadminPlugin = new PluginConcept("org.lucane.applications.forumadmin", "0.7");
        ServiceConcept forumadminService = new ServiceConcept("org.lucane.applications.forumadmin", false);
        admins.addPlugin(forumadminPlugin);
        admins.addService(forumadminService);
        getPluginStore().storePlugin(forumadminPlugin);
        getServiceStore().storeService(forumadminService);
        
        //helpbrowser application
        PluginConcept helpbrowserPlugin = new PluginConcept("org.lucane.applications.helpbrowser", "0.7");
        allUsers.addPlugin(helpbrowserPlugin);
        getPluginStore().storePlugin(helpbrowserPlugin);
                
        //maininterface application
        PluginConcept maininterfacePlugin = new PluginConcept("org.lucane.applications.maininterface", "0.7");
        ServiceConcept maininterfaceService = new ServiceConcept("org.lucane.applications.maininterface", false);
        allUsers.addPlugin(maininterfacePlugin);
        allUsers.addService(maininterfaceService);
        admins.addPlugin(maininterfacePlugin);
        admins.addService(maininterfaceService);
        getPluginStore().storePlugin(maininterfacePlugin);
        getServiceStore().storeService(maininterfaceService);
        
		//quicklaunch application
		PluginConcept quicklaunchPlugin = new PluginConcept("org.lucane.applications.quicklaunch", "0.7");
		allUsers.addPlugin(quicklaunchPlugin);
		admins.addPlugin(quicklaunchPlugin);		
		getPluginStore().storePlugin(quicklaunchPlugin);
        
        //notes application
        PluginConcept notesPlugin = new PluginConcept("org.lucane.applications.notes", "0.7");
        ServiceConcept notesService = new ServiceConcept("org.lucane.applications.notes", false);
        allUsers.addPlugin(notesPlugin);
        allUsers.addService(notesService);
        getPluginStore().storePlugin(notesPlugin);
        getServiceStore().storeService(notesService);
                
        //passwdchanger application
        PluginConcept passwdchangerPlugin = new PluginConcept("org.lucane.applications.passwdchanger", "0.7");
        ServiceConcept passwdchangerService = new ServiceConcept("org.lucane.applications.passwdchanger", false);
        grantedUsers.addPlugin(passwdchangerPlugin);
		grantedUsers.addService(passwdchangerService);
        admins.addPlugin(passwdchangerPlugin);
        admins.addService(passwdchangerService);
        getPluginStore().storePlugin(passwdchangerPlugin);
        getServiceStore().storeService(passwdchangerService);

        //pluginsinfos application
        PluginConcept pluginsinfosPlugin = new PluginConcept("org.lucane.applications.pluginsinfos", "0.7");
        allUsers.addPlugin(pluginsinfosPlugin);
        getPluginStore().storePlugin(pluginsinfosPlugin);
       
        //quickmessage application
        PluginConcept quickmessagePlugin = new PluginConcept("org.lucane.applications.quickmessage", "0.7");
		allUsers.addPlugin(quickmessagePlugin);
		admins.addPlugin(quickmessagePlugin);
        getPluginStore().storePlugin(quickmessagePlugin);
              
        //reunion application
        PluginConcept reunionPlugin = new PluginConcept("org.lucane.applications.reunion", "0.7");
        allUsers.addPlugin(reunionPlugin);
        getPluginStore().storePlugin(reunionPlugin);
        
        //sendfile application
        PluginConcept sendfilePlugin = new PluginConcept("org.lucane.applications.sendfile", "0.7");
        allUsers.addPlugin(sendfilePlugin);
        getPluginStore().storePlugin(sendfilePlugin);

		//audioconf application
		PluginConcept audioconfPlugin = new PluginConcept("org.lucane.applications.audioconf", "0.7");
		allUsers.addPlugin(audioconfPlugin);
		getPluginStore().storePlugin(audioconfPlugin);

		//kick application
		PluginConcept kickPlugin = new PluginConcept("org.lucane.applications.kick", "0.7");
		admins.addPlugin(kickPlugin);
		getPluginStore().storePlugin(kickPlugin);
                
        //sendmail application
        ServiceConcept sendmailService = new ServiceConcept("org.lucane.applications.sendmail", false);
        allUsers.addService(sendmailService);
        getServiceStore().storeService(sendmailService);

		//sqlnavigator application
		PluginConcept sqlnavigatorPlugin = new PluginConcept("org.lucane.applications.sqlnavigator", "0.7");
		ServiceConcept sqlnavigatorService = new ServiceConcept("org.lucane.applications.sqlnavigator", false);
		admins.addPlugin(sqlnavigatorPlugin);
		admins.addService(sqlnavigatorService);
		getPluginStore().storePlugin(sqlnavigatorPlugin);
		getServiceStore().storeService(sqlnavigatorService);

		//calendar application
		PluginConcept calendarPlugin = new PluginConcept("org.lucane.applications.calendar", "0.7");
		ServiceConcept calendarService = new ServiceConcept("org.lucane.applications.calendar", false);
		allUsers.addPlugin(calendarPlugin);
		allUsers.addService(calendarService);
		getPluginStore().storePlugin(calendarPlugin);
		getServiceStore().storeService(calendarService);
		
		//calendarprefs application
		PluginConcept calendarprefsPlugin = new PluginConcept("org.lucane.applications.calendarprefs", "0.7");
		allUsers.addPlugin(calendarprefsPlugin);
		getPluginStore().storePlugin(calendarprefsPlugin);

		//reminder application
		PluginConcept reminderPlugin = new PluginConcept("org.lucane.applications.reminder", "0.7");
		ServiceConcept reminderService = new ServiceConcept("org.lucane.applications.reminder", false);
		allUsers.addPlugin(reminderPlugin);
		allUsers.addService(reminderService);
		getPluginStore().storePlugin(reminderPlugin);
		getServiceStore().storeService(reminderService);
		
		//jmail
		PluginConcept jmailPlugin = new PluginConcept("org.lucane.applications.jmail", "0.7");
		ServiceConcept jmailService = new ServiceConcept("org.lucane.applications.jmail", false);
		allUsers.addPlugin(jmailPlugin);
		allUsers.addService(jmailService);
		getPluginStore().storePlugin(jmailPlugin);
		getServiceStore().storeService(jmailService);

		//jmail account
		PluginConcept jmailaccountPlugin = new PluginConcept("org.lucane.applications.jmailaccount", "0.7");
		ServiceConcept jmailaccountService = new ServiceConcept("org.lucane.applications.jmailaccount", false);
		allUsers.addPlugin(jmailaccountPlugin);
		allUsers.addService(jmailaccountService);
		getPluginStore().storePlugin(jmailaccountPlugin);
		getServiceStore().storeService(jmailaccountService);

		//jmail admin
		PluginConcept jmailadminPlugin = new PluginConcept("org.lucane.applications.jmailadmin", "0.7");
		ServiceConcept jmailadminService = new ServiceConcept("org.lucane.applications.jmailadmin", false);
		admins.addPlugin(jmailadminPlugin);
		admins.addService(jmailadminService);
		getPluginStore().storePlugin(jmailadminPlugin);
		getServiceStore().storeService(jmailadminService);
		
        //todolist application
        PluginConcept todolistPlugin = new PluginConcept("org.lucane.applications.todolist", "0.7");
        ServiceConcept todolistService = new ServiceConcept("org.lucane.applications.todolist", false);
        allUsers.addPlugin(todolistPlugin);
        allUsers.addService(todolistService);
        getPluginStore().storePlugin(todolistPlugin);
        getServiceStore().storeService(todolistService);
        
        //rssreader application
        PluginConcept rssreaderPlugin = new PluginConcept("org.lucane.applications.rssreader", "0.7");
        ServiceConcept rssreaderService = new ServiceConcept("org.lucane.applications.rssreader", false);
        allUsers.addPlugin(rssreaderPlugin);
        allUsers.addService(rssreaderService);
        getPluginStore().storePlugin(rssreaderPlugin);
        getServiceStore().storeService(rssreaderService);
        
        getGroupStore().storeGroup(allUsers);
        getGroupStore().storeGroup(grantedUsers);
        getGroupStore().storeGroup(admins);
    }
}
