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
import org.lucane.common.signature.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Allows communication with the server and the other clients
 * Used to ask connections informations and to download plugins.
 */
public class Communicator
{

  private ConnectInfo ci_server;
  private ConnectInfo ci_proxy;
  private Vector infos;
  private Client parent;
  private Signer signer;
  private static Communicator instance;

  /**
   * Get the signleton instance
   * 
   * @return the unique Communicator instance
   */
  public static Communicator getInstance()
  {
    return instance;
  }

  /**
   * Constructor.
   * 
   * @param server_name the main server
   */
  protected Communicator(String server_name)
  {
    this.parent = Client.getInstance();
	this.ci_server = new ConnectInfo("Server", server_name, server_name, 
		parent.getConfig().getServerPort(), "nokey", "Server");

    //if a proxy is used
    if(parent.getMyInfos(true) != parent.getMyInfos(false))
    {
      this.ci_proxy = new ConnectInfo("Proxy", "proxy", 
        parent.getMyInfos(false).hostname, 
        parent.getConfig().getProxyPort(), "nokey", "proxy");
    }
    else
      this.ci_proxy = null;

    this.infos = new Vector();
    this.signer = null;
    Communicator.instance = this;
  }

  /**
   * Update the plugins from the server
   * 
   * @return the number of updated plugins
   */
  protected int updatePlugins()
  {
  	int nupdated = 0;
  	Vector list = new Vector();  	
  	
    try
    {
      ObjectConnection oc = this.sendMessageTo(this.ci_server, "Server", "PLUGIN_LIST");
	  
	  list = (Vector)oc.read();
      parent.getConnectBox().setProgressMax(list.size());

      oc.close();
    }
    catch(Exception e)
    {
      DialogBox.error(Translation.tr("communicatorPluginUpdateError"));
    }

    for(int i = 0; i<list.size(); i++)
    {
	  StringTokenizer stk = new StringTokenizer((String)list.elementAt(i), " ");
	  String plugin = stk.nextToken();
	  String version = stk.nextToken();

	  parent.getConnectBox().setProgressValue(i, plugin + " v" + version);
	  
      /* download the plugin if necessary */
      if(! PluginLoader.getInstance().hasPlugin(plugin, version, true))
      {
        this.downloadPlugin(plugin);

        if(! PluginLoader.getInstance().hasPlugin(plugin, version, true))
          DialogBox.error(Translation.tr("communicatorPluginLoadError") + plugin);
        else
          nupdated++;
      }

    }

	Logging.getLogger().info("updated " + nupdated + " plugins.");

    return nupdated;
  }

  /**
   * Download a new Plugin or a new version from the server
   * 
   * @param name the Plugin name
   */
  protected void downloadPlugin(String name)
  {
    String subdirectory = Client.APPLICATIONS_DIRECTORY;    
	Logging.getLogger().info("downloading plugin : " + name);
	
	ObjectConnection oc = null;
	DataOutputStream dos = null;

    try {
      oc = this.sendMessageTo(this.ci_server, "Server", "PLUGIN_GET " + name);
      dos = new DataOutputStream(new FileOutputStream(subdirectory + name + ".jar"));
      byte[] buf = (byte[])oc.read(); 
	  oc.close();
      dos.write(buf);
    } catch(Exception e) {
      DialogBox.error(Translation.tr("communicatorGetFileError") + name);
    } finally {
      try {
		if(dos != null)
			dos.close();
      } catch(IOException ioe) {}
    }
  }

  /**
   * Set the user connection informations to the proxy
   * if necessary
   */
  protected void setProxyInfo() throws IOException
  {
    //proxy
    if(this.ci_proxy != null)
    {
        ObjectConnection oc = this.sendMessageTo(this.ci_proxy, "Proxy", 
            "CONNECT_SET " + parent.getMyInfos(true).getRepresentation());
            
        if(oc == null)
        	throw new IOException(Translation.tr("communicatorProxySendError"));

        oc.close();
		Logging.getLogger().finer("using proxy for : " + 
            parent.getMyInfos(true).getRepresentation());
    }
  }

  /**
   * Clean the cache to force connectInfo loading
   * Usefull when someone disconnected and reconnected 
   */
  protected void flushConnectInfosCache()
  {
  	this.infos = new Vector();
  }

  /**
   * Get the connections informations on all known clients
   * 
   * @return the info list
   */
  public Vector getAllConnectInfos()
  {
    return this.infos;
  }

  /**
   * Get a ConnectInfo from the server or from the local cache
   * if it has already been fetched.
   * 
   * @param username ther user or service name
   * @return the connection informations
   */
  public ConnectInfo getConnectInfo(String username)
  {
    ConnectInfo ci_res = null;
    boolean found = false;

    // search in the local cache
    for(int i = 0; i < this.infos.size() && ! found; i++)
    {
      ci_res = (ConnectInfo)this.infos.elementAt(i);

      if(ci_res.getName().equals(username) || 
         ci_res.getName().equals(username))
        found = true;
    }

    // not found, ask the server
    if(! found)
    {
      try
      {
        ObjectConnection oc = this.sendMessageTo(this.ci_server, "Server", 
            "CONNECT_GET " + username);
        		
        ci_res = (ConnectInfo)oc.read();
        oc.close();
        
        if(ci_res.isService() || ci_res.isServer())
        	ci_res.setHostName(Client.getInstance().getConfig().getServerHost());
        
        this.infos.addElement(ci_res);
      }
      catch(Exception e)
      {
        DialogBox.error(Translation.tr("communicatorUserInfoError"));
        ci_res = null;
      }
    }

    return ci_res;
  }

  /**
   * Set the private key to use to sign the messages
   * 
   * @param key the ciphered key
   * @param passwd the clear password
   */
  protected void setPrivateKey(String key, String passwd)
  {
    try
    {
      this.signer = new Signer(key, passwd);
    }
    catch(SignatureException e)
    {
      DialogBox.error(Translation.tr("communicatorSetKeyError"));
    }
  }

  /**
   * Send a message to the target with the correct parameters
   * and following the protocol correctly.
   * 
   * @param who how to connect to the target
   * @param dest the target component
   * @param data the content
   * @return a Socket connected to the target
   */
  public ObjectConnection sendMessageTo(ConnectInfo who, String dest, Object data)
  {
    try
    {
      Socket sock = new Socket(who.hostname, who.port);
      ObjectConnection oc = new ObjectConnection(sock);
      
      Message message = new Message(parent.getMyInfos(), who, dest, data);
      

      //header signature
      byte[] signature = {};
      if(this.signer != null)
        signature = this.signer.sign(message);


		Logging.getLogger().finer("Communicator::sendMessageTo() MESSAGE:" + message);
      oc.write(message);
      oc.write(signature);
      String ack = oc.readString();
	  Logging.getLogger().finer("Communicator::sendMessageTo() ACK : '" + ack + "'");

      if(ack.equals("OK"))
        return oc;

      if(ack.startsWith("FAILED "))
        DialogBox.error(ack.substring(7));
      else
	  DialogBox.error(Translation.tr("communicatorConnectionError"));

      oc.close();

      return null;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      DialogBox.error(Translation.tr("communicatorSendError") + " : " + who);
      return null;
    }
  }

  /**
   * Fetch the user list from the server
   * 
   * @return the list
   */
  public Vector getUserList()
  {
    try
    {
      ObjectConnection oc = this.sendMessageTo(this.ci_server, "Server", "CONNECT_LIST");
      Vector userList = (Vector)oc.read();
      oc.close();

      return userList;
    }
    catch(Exception e)
    {
      DialogBox.error(Translation.tr("communicatorUserListError"));
    }

    return null;
  }
}
