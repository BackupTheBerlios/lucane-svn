/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Gautier Ringeisen <gautier_ringeisen@hotmail.com>
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
public class Server implements Runnable
{
	private static final String CONFIG_FILE = "etc/server-config.xml";
	public static final String APPLICATIONS_DIRECTORY = "applications/";
	public static final String VERSION = "0.7";
	
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
	private Server(ServerConfig config)
	{
		Server.instance = this;
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
		
		this.authenticator = new Authenticator();
		
		ServiceManager.getInstance().loadAllServices();
		ServiceManager.getInstance().startAllServices();
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
			ConnectInfoManager.getInstance().setMyInfos(myConnectInfo);
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
	public void run()
	{
		Socket client = null;
		
		try {
			client = socket.accept();
			new Thread(this).start();
			getMessage(client);
		} catch (IOException ex) {
			Logging.getLogger().warning("#Err > Unable to accept connections.");
		}
		
		try {
			client.close();
		} catch (Exception ex) {
			Logging.getLogger().warning("#Err > Socket::close()");
		}
	}
	
	/**
	 * Reads messages from the network.
	 * A message is either a command for the server
	 * or for an internal Service.
	 * 
	 * @param sock the Socket
	 */
	private void getMessage(Socket sock)
	{
		int i;
		boolean alreadyConnected;
		boolean isAuthentication;
		
		Message message;
		byte[] signature;
		
		String cmd;
		String cmdData;
		StringTokenizer stk;
		
		ObjectConnection oc = null;
		
		try
		{
			/* streams initialization */
			oc = new ObjectConnection(sock);
			message = (Message)oc.read();
			signature = (byte[])oc.read();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Logging.getLogger().warning("#Err > Unable to read message.");
			return;
		}
		
		// check wether a user is known or not
		alreadyConnected = ConnectInfoManager.getInstance().isConnected(message.getSender());
		
		//check if command is authentication
		isAuthentication = message.getApplication().equals("Server") 
		&& ((String)message.getData()).startsWith("AUTH");
		
		//signature check
		if (alreadyConnected && !isAuthentication)
		{
			boolean sigok = false;
			try
			{
				ConnectInfo ci = message.getSender();
				if (ci.verifier == null)
					ci = ConnectInfoManager.getInstance().getCompleteConnectInfo(ci);
				sigok = ci.verifier.verify(message, signature);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (!sigok)
			{
				try
				{
					oc.write("FAILED bad signature");
				}
				catch (Exception e)
				{
				}
				Logging.getLogger().warning("#Err > bad signature: " + message.getSender());
				return;
			}
		}
		
		if (message.getApplication().equals("Server"))
		{
			cmd = null;
			
			try
			{
				stk = new StringTokenizer((String)message.getData());
				cmd = stk.nextToken();
				cmdData = stk.nextToken("\0").substring(1);
			}
			catch (Exception ex)
			{
				if (cmd == null)
					cmd = "";
				
				cmdData = "";
			}
			
			/* if the user asks for authentication, we try to do it and exits this method */
			if (cmd.equals("AUTH"))
			{
				try {
					oc.write("OK");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				authenticator.authenticate(oc, message, cmdData);
			}
			else if (!alreadyConnected)
			{
				Logging.getLogger().info("Access denied to " + message.getSender());
				try
				{
					oc.write("FAILED No Connection");
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				internalCommand(oc, message, cmd, cmdData);
			}
		}
		else if (!alreadyConnected)
		{
			Logging.getLogger().info("Access denied to " + message.getSender());
			try
			{
				oc.write("FAILED No Connection");
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			Service s = ServiceManager.getInstance().getService(message.getApplication());
			boolean serviceFound = false;
			if(s != null)
			{
				serviceFound = true;
				UserConcept user = null;
				ServiceConcept service = null;
				try
				{
					user =
						store.getUserStore().getUser(
								message.getSender().getName());
					service =
						store.getServiceStore().getService(
								message.getReceiver().getName());
				}
				catch (Exception e)
				{
				}
				
				
				
				/* tests serviceManager for permissions */
				boolean isAutorizedService = false;
				try {
					isAutorizedService = store.getServiceStore()
					.isAuthorizedService(user, service);
				} catch(Exception e) {}
				
				if (!isAutorizedService)
				{
					Logging.getLogger().info(
							"#Err > "
							+ message.getSender()
							+ " : Service denied to "
							+ message.getReceiver().getName());
					try
					{
						oc.write(
						"FAILED You don't have acces to this service");
					}
					catch (Exception e)
					{
					}
				}
				else
				{
					try
					{
						oc.write("OK");
					}
					catch (Exception e)
					{
					}
					serviceFound = true;
					s.process(oc, message);
				}
			}
			
			if (!serviceFound)
			{
				try
				{
					oc.write("FAILED unknown");
				}
				catch (Exception e)
				{
				}
				Logging.getLogger().warning(
						"#Err > Service "
						+ message.getReceiver().getName()
						+ " unknown");
			}
		}
		
		oc.close();
	}
	
	
	
	/**
	 * Handle internal commands
	 */
	private void internalCommand(
			ObjectConnection oc,
			Message message,
			String command,
			String data)
	{
		if (command.equals("CONNECT_DEL"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			ConnectInfoManager.getInstance().removeConnectInfo(message.getSender());
		}
		else if (command.equals("CONNECT_GET"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			this.getConnectInfo(data, oc);
		}
		else if (command.equals("CONNECT_LIST"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			this.getUserList(oc);
		}
		else if (command.equals("PLUGIN_LIST"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			this.sendPluginList(oc, message.getSender().getName());
		}
		else if (command.equals("PLUGIN_GET"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			this.sendPlugin(oc, data);
		}
		else if (command.equals("STARTUP_PLUGINS"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			this.getStartupPlugin(oc, message.getSender().getName());
		}
		else
		{
			try
			{
				oc.write("FAILED Unknown command");
			}
			catch (Exception e)
			{
			}
		}
	}
	
	
	/**
	 * Send the ConnectInfo associated with this name
	 * 
	 * @param name the user wanted
	 */
	private void getConnectInfo(String name, ObjectConnection oc)
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
	private void getUserList(ObjectConnection oc)
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
	private void sendPluginList(ObjectConnection oc, String source)
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
	private void sendPlugin(ObjectConnection oc, String data)
	{
		DataInputStream dis = null;
		try
		{
			dis = new DataInputStream(new FileInputStream(APPLICATIONS_DIRECTORY + data + ".jar"));
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
	private void getStartupPlugin(ObjectConnection oc, String source)
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
	public void sendUserList()
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
		Message msg = new Message(ConnectInfoManager.getInstance().getMyInfos(), dest, app, data);
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
	 * Main Method
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		//init logging
		try {
			Logging.init("lucane.log", "ALL");
		} catch(IOException ioe) {
			System.err.println("Unable to init logging, exiting.");
			System.exit(1);
		}
		
		Server server = null;
		ServerConfig config = null;
		
		try {
			config = new ServerConfig(CONFIG_FILE);
		} catch (Exception e) {            
			Logging.getLogger().severe("Unable to read or parse the config file.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Server creation
		server = new Server(config);
		server.generateKeys();
		Logging.getLogger().info("Server is ready.");
		server.run();		
	}
}
