/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Gautier Ringeisen <gautier_ringeisen@hotmail.com>
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
package org.lucane.server;

import org.lucane.common.concepts.*;
import org.lucane.server.auth.Authenticator;
import org.lucane.server.database.*;
import org.lucane.server.store.*;
import org.lucane.common.*;
import org.lucane.common.signature.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The server's class
 */
public class Server
{
	
	private String workingDirectory;
	
	private static final String CONFIG_FILE = "etc/server-config.xml";
	public static final String APPLICATIONS_DIRECTORY = "applications/";
	public static final String VERSION = "0.7.1-beta";
	
	//-- instance management
	private static Server instance = null;
	public static Server getInstance()
	{
		return instance;
	}
	
	//-- attributes
	ServerSocket socket;
	Store store;
	Authenticator authenticator;
	DatabaseAbstractionLayer dbLayer;
	String serverIp;
	int port;
	Signer signer;
	
	/**
	 * Creates a new Server object.
	 * 
	 * @param sqlDriver JDBC driver 
	 * @param dbURL JDBC connection url
	 * @param dbLogin database login
	 * @param dbPasswd database password
	 */
	private Server(ServerConfig config, String workingDirectory)
	{
		Server.instance = this;
		this.workingDirectory = workingDirectory;
		this.port = config.getPort();
		this.socket = null;
		this.dbLayer = null;
		
		try {
			dbLayer = DatabaseAbstractionLayer.createLayer(config);
			Logging.getLogger().finer("dbLayer   : " + dbLayer);
			
			this.store = new Store(config);
		} catch (Exception ex) {
			Logging.getLogger().severe(
					"#Err > Unable to connect to the database : "
					+ ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		
		try {
			this.serverIp = InetAddress.getLocalHost().getHostAddress();
			this.socket = new ServerSocket(this.port);
		} catch (IOException e) {
			Logging.getLogger().severe("#Err > Unable to listen on the port " + port + ".");
			e.printStackTrace();
			System.exit(1);
		}
		
		try	{
			this.authenticator = Authenticator.getInstance(config);
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to get Authenticator instance.");
			e.printStackTrace();
			System.exit(1);
		}	
	}
	
	/**
	 * Generates server's keys for signature
	 */
	public void generateKeys()
	{
		Logging.getLogger().info("Generating keypair");
		
		try {
			String[] pair = KeyGenerator.generateKeyPair();
			ConnectInfo myConnectInfo = new ConnectInfo("server",
					this.serverIp, this.serverIp,
					this.port, pair[1], "Server");
			ConnectInfoManager.getInstance().setServerInfo(myConnectInfo);
			ConnectInfoManager.getInstance().addConnectInfo(myConnectInfo);
			this.signer = new Signer(pair[0]);
		} catch (SignatureException e) {
			Logging.getLogger().severe("Unable to generate keypair : " + e);
			System.exit(1);
		}
	}
	
	/**
	 * Accepts connections
	 */
	public void acceptConnections()
	{
		while(!socket.isClosed())
		{
			try {
				MessageHandler handler = new MessageHandler(socket.accept());
				handler.start();
			} catch (IOException ex) {
				if(!socket.isClosed())
				Logging.getLogger().warning("#Err > Unable to accept connections.");
			}	
		}
	}	
	
	/**
	 * Stop the server
	 */
	public void shutdown()
	{
		this.authenticator.disableLogin();
		ConnectInfoManager.getInstance().kickAllUsers();	
		Logging.getLogger().info("Users disconnected.");		
		ServiceManager.getInstance().shutdownAllServices();
		Logging.getLogger().info("Services shutdowned.");		
		
		try	{
			this.socket.close();
			Logging.getLogger().info("Socket closed.");		
		} catch (IOException e)	{
			Logging.getLogger().warning("Unable to close socket : " + e);
		}
		
		System.exit(0);		
	}	
	
	/**
	 * Send the ConnectInfo associated with this name
	 * 
	 * @param name the user wanted
	 */
	public void sendConnectInfo(String name, ObjectConnection oc)
	{
		ConnectInfo ci = ConnectInfoManager.getInstance().getConnectInfo(name);
		if(ci != null)
		{
			try  {
				oc.write(ci);
			} catch (Exception e) {}
		}
		else
		{
			try  {
				oc.write("unknown");
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Send the connected users list
	 */
	public void sendUserList(ObjectConnection oc)
	{
		//get into vector
		Vector v = new Vector();		
		Iterator i = ConnectInfoManager.getInstance().getClientConnectInfos();
		while(i.hasNext())
			v.addElement(i.next());
		
		//send
		try	{
			oc.write(v);
		} catch (Exception e) {}
	}
	
	/**
	 * Send the plugin list to a client.
	 * The list depends of the client's groups
	 * 
	 * @param source the user that asked this command
	 */
	public void sendPluginList(ObjectConnection oc, String source)
	{
		Vector plist = new Vector();
		Iterator plugins;
		String line = "";
		
		try
		{
			UserConcept user = store.getUserStore().getUser(source);
			plugins = store.getPluginStore().getAuthorizedPlugins(user);
			
			while(plugins.hasNext())
			{
				PluginConcept plugin = (PluginConcept)plugins.next();
				line = plugin.getName();
				line += " " + plugin.getVersion();
				plist.add(line);
			}
			oc.write(plist);
		}
		catch (Exception e)
		{
			Logging.getLogger().warning("Unable to send the plugin list.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a plugin JAR file to the client
	 * 
	 * @param data the plugin name
	 */
	public void sendPluginFile(ObjectConnection oc, String data)
	{
		DataInputStream dis = null;
		try
		{
			dis = new DataInputStream(new FileInputStream(getWorkingDirectory()+APPLICATIONS_DIRECTORY + data + ".jar"));
			byte[] buf = new byte[dis.available()];
			dis.readFully(buf);
			oc.write(buf);
		}
		catch (Exception e)
		{
			Logging.getLogger().warning("Unable to send the file: " + data + ".jar");
		}
		finally {
			if(dis != null)
			{
				try {
					dis.close();
				} catch(IOException ioe) {}		
			}
			
		}
	}
	
	/**
	 * Send the user's startup plugin
	 * 
	 * @param source the user
	 */
	public void sendStartupPlugin(ObjectConnection oc, String source)
	{
		try
		{
			UserConcept user = store.getUserStore().getUser(source);
			oc.write(user.getStartupPlugin());
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * Send the user list to all users
	 */
	public void sendUserListToEveryone()
	{
		//create user list as string
		StringBuffer userList = new StringBuffer("USER_LIST");
		Iterator users = ConnectInfoManager.getInstance().getClientConnectInfos();
		while(users.hasNext())
		{
			ConnectInfo user = (ConnectInfo)users.next();
			userList.append(" ");
			userList.append(user.getName());
		}
		
		// send to everyone
		Iterator clients = ConnectInfoManager.getInstance().getClientConnectInfos();
		while(clients.hasNext())
		{
			ConnectInfo client = (ConnectInfo)clients.next();
			try {
				ObjectConnection oc = sendMessageTo(client, "Client", userList.toString());
				oc.close();
			} catch (IOException e) {
				Logging.getLogger().warning("unable to send user list to " + client);
			}
		}
	}
	
	
	/**
	 * Send a message through the network
	 * 
	 * @param dest the receiver
	 * @param app the application that need to process this message
	 * @param data the data to send
	 */
	public ObjectConnection sendMessageTo(ConnectInfo dest, String app, String data)
	throws IOException
	{
		Socket sock = new Socket(dest.hostname, dest.port);
		ObjectConnection oc = new ObjectConnection(sock);
		Message msg = new Message(ConnectInfoManager.getInstance().getServerInfo(), dest, app, data);
		byte[] signature = null;
		
		try {
			signature = this.signer.sign(msg);
		} catch (SignatureException e) {
			Logging.getLogger().warning("Unable to sign: " + e);
		}
		
		oc.write(msg);
		oc.write(signature);
		return oc;
	}
	
	/**
	 * Get the database layer
	 * 
	 * @return the Layer instance
	 */
	public DatabaseAbstractionLayer getDBLayer()
	{
		return this.dbLayer;
	}
	
	/**
	 *  Get the store factory
	 * 
	 * @return the store factory instance
	 */
	public Store getStore() 
	{
		return store;
	}
	
	/**
	 * Get the authenticator
	 * 
	 * @return the authenticator instance
	 */
	public Authenticator getAuthenticator() 
	{
		return this.authenticator;
	}
	
	//-- static methods
	public static void shutdownServer(String [] args)
	{
		Server.getInstance().shutdown();
	}
	
	/**
	 * Main Method
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		if (args.length > 1) {
			System.out.println("USAGE :\nserver.(bat|sh) [server path]");
			System.exit(1);
		}

		// get the workingDirectory
		String workingDirectory;
		if (args.length==1) {
			workingDirectory=args[0];
		} else {
			workingDirectory=System.getProperty("user.dir");
		}
		workingDirectory=workingDirectory.replace('\\','/');
		if (workingDirectory.startsWith("\""))
			workingDirectory=workingDirectory.substring(1, workingDirectory.length()-2);
		if (!workingDirectory.endsWith("/"))
			workingDirectory+="/";

		//init logging
		try {
			Logging.init(workingDirectory+"lucane.log", "ALL");
		} catch(IOException ioe) {
			System.err.println("Unable to init logging, exiting.");
			System.exit(1);
		}
		
		Server server = null;
		ServerConfig config = null;
		
		try {
			config = new ServerConfig(workingDirectory+CONFIG_FILE);
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to read or parse the config file.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Server creation
		server = new Server(config, workingDirectory);
		server.generateKeys();
		ServiceManager.getInstance().loadAllServices();
		ServiceManager.getInstance().startAllServices();		
		Logging.getLogger().info("Server is ready.");
		server.acceptConnections();		
	}
	
	public String getWorkingDirectory() {
		Logging.getLogger().fine(workingDirectory);
		return workingDirectory;
	}
}
