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
import java.util.jar.*;

/**
 * The server's class
 */
public class Server implements Runnable
{
	private static final String CONFIG_FILE = "etc/server-config.xml";
	public static final String APPLICATIONS_DIRECTORY = "applications/";
	public static final String VERSION = "0.7-beta";
	
	//-- instance management
	private static Server instance = null;
	public static Server getInstance()
	{
		return instance;
	}
	
	//-- attributes
	ArrayList connections;
    ArrayList services;
    ServerSocket socket;
    Store store;
    DatabaseAbstractionLayer dbLayer;
    ConnectInfo myConnectInfo;
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
        this.connections = new ArrayList();
        this.services = new ArrayList();
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

        loadInternalServices();
    }

    /**
     * Generates server's keys for signature
     */
    public void generateKeys()
    {
		Logging.getLogger().info("Generating keypair");

        try {
            String[] pair = KeyGenerator.generateKeyPair();
            myConnectInfo = new ConnectInfo("server",
                    			this.serverIp, this.serverIp,
                    			this.port, pair[1], "Server");
            this.connections.add(myConnectInfo);
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
        alreadyConnected = isAlreadyKnown(message.getSender());

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
                    ci = this.getCompleteConnectInfo(ci);
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

                authentification(oc, message, cmdData);
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
            Service s;

            /* seek the destination service */
            boolean serviceFound = false;
            for (i = 0; i < this.services.size(); i++)
            {
                s = (Service)services.get(i);

                if (s.getName().equalsIgnoreCase(message.getApplication()))
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
                    break;
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
     * Loads internal services
     */
    private void loadInternalServices()
    {
        try
        {
            Iterator services;
            String servicename;
            String baseURL =
                System.getProperty("user.dir")
                    + "/"
                    + APPLICATIONS_DIRECTORY;

            LucaneClassLoader loader = LucaneClassLoader.getInstance();
            services = store.getServiceStore().getAllServices();

            while (services.hasNext())
            {
                ServiceConcept service = (ServiceConcept)services.next();
                servicename = service.getName();
                try
                {
                    loader.addUrl(
                        new URL(
                            "jar:file:///" + baseURL + servicename + ".jar!/"));
                    String className =
                        (new JarFile(baseURL + servicename + ".jar"))
                            .getManifest()
                            .getMainAttributes()
                            .getValue("Service-Class");

                    if (className == null)
                        continue;

                    Service serv =
                        (Service)Class
                            .forName(className, true, loader)
                            .newInstance();
                    this.services.add(serv);
                    serv.init(this);

                    if (! service.isInstalled())
                    {
                        serv.install();
                        service.setInstalled();
                        store.getServiceStore().updateService(service);
                    }

					Logging.getLogger().info("Service '" + servicename + "' loaded.");
                    this.connections.add(
                        new ConnectInfo(
                            servicename,
                            serverIp,
                            serverIp,
                            port,
                            "nokey",
                            "service"));
                }
                catch (Exception e)
                {
					Logging.getLogger().warning("Unable to load service '" + servicename);
                }
            }
        }
        catch (Exception e)
        {
			Logging.getLogger().warning("Unable to load internal services : " + e);
			e.printStackTrace();
        }
    }

    /**
     * Authenticate users
     * 
     * @param oc the streams
     * @param ci the ConnectInfo
     */
    private void authentification(
        ObjectConnection oc,
        Message message,
        String data)
    {
        String passwd = null;
        String name = null;
        String authenticationServer = null;
        String hostname = null;
        int port = 0;
        StringTokenizer stk = new StringTokenizer(data, " ");

        try
        {
            passwd = stk.nextToken();
            hostname = stk.nextToken();
            port = Integer.parseInt(stk.nextToken());
        }
        catch (Exception ex)
        {
			Logging.getLogger().warning("#Err > Incorrect authentication message.");
            try
            {
                oc.write("BAD_MESSAGE");
            }
            catch (Exception e)
            {
            }

            return;
        }

        name = message.getSender().getName();
        authenticationServer = message.getSender().getAuthenticationServer();
        UserConcept user = null;
        
        try {
            user = store.getUserStore().getUser(message.getSender().getName());
        } catch(Exception e) {
        	e.printStackTrace();
        }

		//password ok
        if(user != null && store.getUserStore().checkUserPassword(user,  passwd))
        {
			//disconnect already connected user
			if(isAlreadyKnown(message.getSender()))
			{
				ConnectInfo oldUser = getCompleteConnectInfo(message.getSender());
				try {
					ObjectConnection myoc = this.sendMessageTo(oldUser, "Client", "DISCONNECT");
					myoc.close();
				} catch (Exception e) {
					//we can't do much here, the client might have crashed
				}
				this.removeConnectInfo(oldUser);
			}
			
            connections.add(new ConnectInfo(name, authenticationServer, hostname,
                    port, user.getPublicKey(), "Client"));
            try
            {
                oc.write(
                    "AUTH_ACCEPTED "
                        + user.getPrivateKey());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            this.sendUserList();
        }
        else
        {
            try
            {
                oc.write("NOT_VALID_USER");
            }
            catch (Exception e)
            {
            }
        }
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
            this.removeConnectInfo(message.getSender());
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
     * Remove a ConnectionInfo
     * 
     * @param ci the connect info
     */
    private void removeConnectInfo(ConnectInfo ci)
    {
	    removeConnectInfo(ci.getName());
    }

	/**
	 * Remove a ConnectionInfo
	 * 
	 * @param user the user name
	 */
	private void removeConnectInfo(String user)
	{
		for (int i = 0; i < this.connections.size(); i++)
		{
			ConnectInfo ci = (ConnectInfo)this.connections.get(i);
			if (ci.getName().equals(user))
			{
				this.connections.remove(ci);
				break;
			}
		}
		sendUserList();
	}
    
    /**
     * Send the ConnectInfo associated with this name
     * 
     * @param name the user wanted
     */
    private void getConnectInfo(String name, ObjectConnection oc)
    {
        ConnectInfo ci = getConnectInfo(name);
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
        Vector v = new Vector();
        for (int i = 0; i < this.connections.size(); i++)
        {
            if (((ConnectInfo)this.connections.get(i))
                .type
                .equalsIgnoreCase("Client"))
                v.addElement(this.connections.get(i));
        }

        try
        {
            oc.write(v);
        }
        catch (Exception e)
        {
        }
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
        String data = "";
        Socket sock = null;
        ObjectConnection oc = null;
        byte[] signature = {};

        /* receiver list */
        for (int i = 0; i < this.connections.size(); i++)
        {
            if (((ConnectInfo)this.connections.get(i))
                .type
                .equalsIgnoreCase("Client"))
            {
                data = "";

                /* users list */
                for (int j = 0; j < this.connections.size(); j++)
                {
                    if (((ConnectInfo)this.connections.get(j))
                        .type
                        .equalsIgnoreCase("Client"))
                        data =
                            data
                                + " "
                                + ((ConnectInfo)this.connections.get(j))
                                    .getName();
                }

                try
                {
                    sock =
                        new Socket(
                            (
                                (ConnectInfo)this.connections.get(
                                    i)).hostname,
                            ((ConnectInfo)this.connections.get(i)).port);
                    oc = new ObjectConnection(sock);
                    Message msg =
                        new Message(
                            myConnectInfo,
                            (ConnectInfo)this.connections.get(i),
                            "Client",
                            "USER_LIST " + data);

                    try
                    {
                        signature = this.signer.sign(msg);
                    }
                    catch (SignatureException e)
                    {
						Logging.getLogger().warning("Unable to sign: " + e);
                    }

                    oc.write(msg);
                    oc.write(signature);
                    oc.close();
                }
                catch (IOException ex)
                {
					Logging.getLogger().warning("Unable to connect to host");
                    continue;
                }
            }
        }
    }

    /**
     * Check if a user is already known
     * 
     * @param ConnectInfo the user
     * @return true or false
     */
    private boolean isAlreadyKnown(ConnectInfo ci)
    {
        boolean result = false;
        Iterator i = this.connections.iterator();

        while (i.hasNext() && !result)
        {
            ConnectInfo tmp = (ConnectInfo)i.next();
            result = ci.getName().equals(tmp.getName());
        }

        return result;
    }

    /**
     * Get the complete ConnectInfo
     * 
     * @param ConnectInfo the user
     * @return the complete ConnectInfo
     */
    private ConnectInfo getCompleteConnectInfo(ConnectInfo ci)
    {
        Iterator i = this.connections.iterator();

        while (i.hasNext())
        {
            ConnectInfo tmp = (ConnectInfo)i.next();
            if (ci.getName().equals(tmp.getName()))
                return tmp;
        }

        return ci;
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
    	Message msg = new Message(myConnectInfo, dest, app, data);
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
	 * Get the server connection infos
	 * 
	 * @return the server ConnectInfo
	 */
	public ConnectInfo getMyInfos() 
	{
		return this.myConnectInfo;
	}
	
	/**
	 * Get a user connection infos
	 * 
	 * @param userName the user
	 * @return the ConnectInfo
	 */
	public ConnectInfo getConnectInfo(String userName) 
	{
		for (int i=0;  i<this.connections.size(); i++)
		{
			ConnectInfo ci = (ConnectInfo)this.connections.get(i);
			if(ci.getName().equals(userName))
				return ci;
		}
		
		return null;
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
