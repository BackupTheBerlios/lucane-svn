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
package org.lucane.client;

import org.lucane.client.util.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;
import java.net.*;
import java.awt.Font;
import java.io.*;
import java.util.*;

import javax.swing.ImageIcon;


/**
 * The client main class.
 * It's a singleton containing the different client parts : 
 *   Listener, Communicator, PluginLoader, ...
 * It is used as a glue between the different components
 */
public class Client
{   
	private static final String CONFIG_FILE = "etc/client-config.xml";
	public static final String APPLICATIONS_DIRECTORY = "applications/";
	public static final String VERSION = "0.7-preview";
	
    private ConnectInfo myinfos;    //can be masked by the proxy
    private ConnectInfo realinfos;  //has the real infos
	private ConnectInfo serverInfos;

    private PluginLoader pluginloader;
    private Listener listener;
    private Communicator communicator;

    private ConnectBox connectbox;

    private boolean isConnected;

    private ArrayList userListListeners;
    private Vector users;
    private Vector groups;

	private ClientConfig config;
	
	//the current startup plugin
	private String startupPlugin;
	
	//stores running plugins
	private ArrayList registeredPlugins;


    private static Client instance = null;
    
    /**
     * Client is a Singleton
     *
     * @return the unique client instance
     */
    public static Client getInstance()
    {        
        if(instance == null)
            instance = new Client();
        
        return instance;
    }
    
    /**
     * Creates a new client
     */
    private Client()
    {
        this.isConnected = false;
        this.myinfos = null;
        this.realinfos = null;
        this.pluginloader = null;
        this.listener = null;
        this.communicator = null;
        this.users = new Vector();
        this.groups = new Vector();
        this.userListListeners = new ArrayList();
		this.config = null;
		
		this.registeredPlugins = new ArrayList();

		//create application directory structure
		String appPath = System.getProperty("user.dir") + '/' + APPLICATIONS_DIRECTORY;
		new File(appPath).mkdirs();
		new File(appPath + "config").mkdirs();
    }
    
    /**
     * Shows the connection box
     *
     * @param defaultLogin the default login
     * @param passwd the password to use automatically or null
     * @param serverName the server to use for the connection
     * @param serverPort the server to use for the connection
     */
    protected void showConnectBox(String defaultLogin, String passwd, String serverName, int serverPort)
    {
        this.connectbox = new ConnectBox(serverName, serverPort);
        connectbox.showModalDialog(defaultLogin, passwd);
    }
    
    /**
     * Initialize the client and launch the startup plugin
     */
    protected void init()
    {
        this.listener.listen();
        this.pluginloader = PluginLoader.getInstance();
        this.communicator.updatePlugins();
        this.serverInfos = new ConnectInfo("Server", "", myinfos.server,
            config.getServerPort(), "nokey", "Server");
        
        try
        {            
            ObjectConnection oc = this.communicator.sendMessageTo(serverInfos, "Server",
                "STARTUP_PLUGINS");
            
            this.startupPlugin = oc.readString();
            
            if(this.pluginloader.hasPlugin(startupPlugin))
                this.pluginloader.run(startupPlugin, new ConnectInfo[0]);
            else
            {
                DialogBox.error(startupPlugin + " : " + Translation.tr("clientUnknownPlugin"));
                this.cleanExit();
            }
            
            oc.close();
        }
        catch(Exception e)
        {
			Logging.getLogger().warning(e.toString());
        }
        
        this.connectbox.closeDialog();
    }
    
    /**
     * Creates a new Listener object
     *
     * @return the new listener
     */
    protected Listener createNewListener()
    {
        if(this.listener != null)
            this.listener.pleaseStop();
        
        this.listener = new Listener();
        
        return this.listener;
    }
    
    /**
     * Creates a new ConnectInfo object
     *
     * @param userName the user's login
     * @param serverName the authentication server
     * @param listenPort the listening port
     */
    protected void setMyInfos(String userName, String serverName, int listenPort)
    {        
        try
        {            
            if(config.getPublicIp() == null)
            {
                this.realinfos = new ConnectInfo(userName, serverName,
                    InetAddress.getLocalHost().getHostAddress(),
                    listenPort, "nokey", "client");
			}
            else
            {
                this.realinfos = new ConnectInfo(userName, serverName, config.getPublicIp(),
                    listenPort, "nokey", "client");
			}
            
            if(config.getProxyHost() != null)
            {
                this.myinfos = new ConnectInfo(userName, serverName, config.getProxyHost(),
					config.getProxyPort(), "nokey", "client");
			}
            else
                this.myinfos = realinfos;
        }
        catch(UnknownHostException ex)
        {
            this.myinfos = new ConnectInfo("", "", "", 0, "nokey", "");
        }
    }
    
    /**
     * Get the connection informations
     *
     * @return the connection informations
     */
    public ConnectInfo getMyInfos()
    {       
        return getMyInfos(false);
    }
    
    /**
     * Get the connection information
     *
     * @param noproxy allows to get the real infos and bypass the proxy
     * @return the connection informations
     */
    public ConnectInfo getMyInfos(boolean noproxy)
    {        
        return noproxy ? this.realinfos : this.myinfos;
    }
    
    /**
     * Creates a new Communicator object
     *
     * @param serverName the server
     */
    protected void createNewCommunicator(String serverName)
    {
        this.communicator = new Communicator(serverName);
    }
        
    
    /**
     * Tells the client that a plugin has been launched.
     */
    protected void registerPlugin(Plugin p)
    {
    	Logging.getLogger().fine("registering : " + p.getName());
        this.registeredPlugins.add(p);
    }
    
    /**
     * Tells a client that a plugin has been closed.
     */
    protected void unregisterPlugin(Plugin p)
    {
		Logging.getLogger().fine("unregistering : " + p.getName());
        this.registeredPlugins.remove(p);
        
		//if no more plugins are running, or if we quit the startup plugin,
		//exits the client cleanly
        if(registeredPlugins.isEmpty() || p.getName().equals(startupPlugin))
        	cleanExit();
    }
    
    /**
     * Disconnected by the server
     */
    protected void disconnect()
    {
    	DialogBox.info(Translation.tr("msg.disconnected"));
    	cleanExit();
    }
    
    /**
     * Quits the client properly
     */
    protected void cleanExit()
    {        
        try
        {            
            ObjectConnection oc = communicator.sendMessageTo(serverInfos, "Server",
                "CONNECT_DEL " + myinfos.getName());
            oc.close();

			Logging.getLogger().info("Exiting cleanly.");
			Runtime.getRuntime().halt(0);
        }
        catch(Exception e)
        {
            Logging.getLogger().warning("Can't send exit message to server");
            e.printStackTrace();
			Runtime.getRuntime().halt(1);
        }        
    }
    
    /**
     * Set the connected user list
     *
     * @param users the user list
     */
    protected void setUserList(Vector users)
    {
        this.users = users;
        this.communicator.flushConnectInfosCache();
        
        Iterator i = this.userListListeners.iterator();
        while(i.hasNext())
            ((UserListListener)i.next()).userListChanged(users);
    }
    
    /**
     * Get the connect box
     *
     * @return the ConnectBox
     */
    protected ConnectBox getConnectBox()
    {       
        return this.connectbox;
    }
    
    /**
     * Add a UserListListener. It will be notified at each
     * change of the connected userlist
     *
     * @param ull the UserListListener
     */
    public void addUserListListener(UserListListener ull)
    {
        this.userListListeners.add(ull);
    }
    
    /**
     * Remove a UserListListener
     *
     * @param ull the UserListListener
     */
    public void removeUserListListener(UserListListener ull)
    {
        this.userListListeners.remove(ull);
    }
    
    /**
     * Get the connected users
     *
     * @return the user list
     */
    public Vector getUserList()
    {       
        return users;
    }
        
    /**
     * Get the startup plugin for the current user
     */
    public String getStartupPlugin()
	{
    	return this.startupPlugin;
    }
    
    /**
     * Change the flag startup plugin.
     * You shouldn't use it. It's used by quicklaunch to set MainInterface
     * if quicklaunch can't be started.
     * 
     * @param pluginName the plugin name
     */
    public void setStartupPlugin(String pluginName)
	{
    	this.startupPlugin = pluginName;
    }
    
	/**
	 * Set configuration
	 * 
	 * @param config the configuration
	 */
	private void setConfig(ClientConfig config)
	{
		this.config = config;
		Translation.setLocale(config.getLanguage());

		if(config.getFont() != null)
		{	
			FontManager.setDefaultFont(new Font(config.getFont(), Font.PLAIN, 12));
			Logging.getLogger().info("Using font : " + config.getFont());   
		}
		
		if(config.getProxyHost() != null)
		{
			Logging.getLogger().info("Using a proxy connection : " + config.getProxyHost() 
								+ ":" + config.getProxyPort());
		}

		if(config.getPublicIp() != null)
			Logging.getLogger().info("Forced use of the IP adress : " + config.getPublicIp());   
	}

	/**
	 * Get the configuration
	 * 
	 * @return the client configuration
	 */
	public ClientConfig getConfig()
	{
		return this.config;
	}
	
	/**
	 * Get an icon from its image name
	 * 
	 * @param icon the image name
	 * @return an ImageIcon instance
	 */
	public static ImageIcon getIcon(String icon)
	{
		URL url=null;
		try {
			/* create the default url */
			url=new URL("jar:file:///"
				+ System.getProperty("user.dir").replace('\\', '/')
				+ "/lib/lucane-client-" + Client.VERSION + ".jar!/icons/"
				+ icon);
			
			return new ImageIcon(url);
		} catch(Exception e) {		
			return new ImageIcon();
		}
	}
    
    /**
     * Main
     *
     * @param args command line argumens
     */
    public static void main(String[] args)
    {
        Translation.setLocale("en");
        
        try {
        	Logging.init("lucane.log", "ALL");
        } catch(IOException ioe) {
        	System.err.println("Unable to init logging, exiting.");
        	System.exit(1);
        }
        
        //configuration
		ClientConfig config = null;
        try  {
        	config = new ClientConfig(CONFIG_FILE);
        } catch(Exception e) {
            DialogBox.error(Translation.tr("clientNoParamFile"));
			e.printStackTrace();
            System.exit(1);
        }
        
		//look and feel
		try {
			javax.swing.UIManager.setLookAndFeel(config.getLooknfeel());
		} catch(Exception e) {}

        //initialization
        Client client = Client.getInstance();
		client.setConfig(config);
        		
		String login = config.getLogin();
		String passwd = null;		
		if(args.length == 2)
		{
			login = args[0];
			passwd = args[1];
		}
        
        client.showConnectBox(login, passwd, config.getServerHost(), config.getServerPort());
    }
}
