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

import org.lucane.common.*;
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;


/**
 * A Listener is the client's "server" part
 */
class Listener
  extends Thread
{

  private ServerSocket socket;
  private Client parent;
  private boolean stopThread = false;
  private boolean stop;
  private int port;

  /**
   * Constructor. Initialize the ServerSocket
   */
  public Listener()
  {
    Random rnd = new Random();
    boolean ok = false;
    this.parent = Client.getInstance();

    port = parent.getConfig().getListenerPort();
    if(port == 0)
    	port = rnd.nextInt(62000) + 1025;
    
    while(! ok)
    {
      try
      {
        this.socket = new ServerSocket(port);
		Logging.getLogger().info("Listener port : " + port);
        ok = true;
      }
      catch(IOException ex)
      {
      	port = rnd.nextInt(62000) + 1025;
      }
    }
  }

  /**
   * Accept all connections. Start a new Thread.
   */
  public void listen()
  {
    this.start();
  }

  /**
   * Called by listen(). You don't need to start the thread by yourself.
   */
  public void run()
  {
    Socket client = null;

    while(! stop)
    {
      try
      {
        client = socket.accept();

        InetAddress ia = client.getInetAddress();
		Logging.getLogger().finer("Listener::accept() FROM:" + ia.getHostName() + " (" + 
              ia.getHostAddress() + ") : " + client.getPort());
        this.getMessage(client);
      }
      catch(IOException e)
      {
		Logging.getLogger().warning(Translation.tr("listenerCantAcceptConnection") + e);
      }
    }
  }

  /**
   * Read messages from the network
   * 
   * @param s the Socket to read
   */
  private void getMessage(Socket s)
  {
    ObjectConnection oc = new ObjectConnection(s);
    Message message = null;
    byte [] signature = null;

    try
    {
      message = (Message)oc.read();
      signature = (byte[])oc.read();
	  Logging.getLogger().fine("Listener::getMessage(): " + message);
    }
    catch(Exception e)
    {
		Logging.getLogger().warning(Translation.tr("listenerCantReadMessage") + e);
      e.printStackTrace();
      oc.close();
      return;
    }

    if(message != null)
    {
      //Check the signature
      boolean sigok = false;
      try
      {
        ConnectInfo ci = message.getSender();

        if(ci.verifier == null)
          ci = Communicator.getInstance().getConnectInfo(ci.getName());

        sigok = ci.verifier.verify(message, signature);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }

      if(! sigok)
      {
        try {
			Logging.getLogger().warning("Listener::getMessage() : invalid signature");
          oc.write("FAILED " + Translation.tr("listenerInvalidSignature"));
        } catch(Exception e) {}

        return;
      }

      //client command
      if(message.getApplication().equals("Client"))
      {
      	try {
      	 	oc.write("OK");
      	} catch(Exception e) {}
      	
		Logging.getLogger().finest("Listener::getMessage() COMMAND: client");
        internalCommand(message);
      }

      //plugin command
      else
      {

        if(PluginLoader.getInstance().hasPlugin(message.getApplication()))
        {
		try {
			oc.write("OK");
		} catch(Exception e) {}
			
          	PluginLoader.getInstance().load(oc, message);
        }
        else
        {
		try {
			oc.write("FAILED " + Translation.tr("listenerNoSuchApplication"));
		} catch(Exception e) {}       	

		Logging.getLogger().finer("Listener::getMessage() UNKNOWN: " + message.getApplication());
        }
      }
    }
  }

  /**
   * Handles internal commands
   * 
   */
  private void internalCommand(Message message)
  {
    StringTokenizer stk = new StringTokenizer((String)message.getData());
    String command = stk.nextToken();
    String data = null;

    try
    {
      data = stk.nextToken("\0").substring(1);
    }
    catch(NoSuchElementException e)
    {
      data = "";
    }

    /* internal commands */
	if(command.equals("USER_LIST"))
      setUserList(data);
	else if(command.equals("DISCONNECT"))
	  disconnect();
  }

  /**
   * Update the user list
   * 
   * @param data the server message
   */
  private void setUserList(String data)
  {
	Logging.getLogger().fine("Listener::setUserList");

    String line;
    Vector users = new Vector();
    StringTokenizer stk = new StringTokenizer(data);

    while(stk.hasMoreTokens())
    {
      line = stk.nextToken();
      users.addElement(line);
    }

    parent.setUserList(users);
  }
  
  /**
   * Disconnects and exit
   */
  private void disconnect()
  {
  	Client.getInstance().disconnect();
  }

  /**
   * Stop the listener.
   */
  public void pleaseStop()
  {
    this.stop = true;

    try
    {
      this.socket.close();
    }
    catch(Exception e)
    {
      //oh well
    }
  }

  /**
   * Get the listening port
   * 
   * @return the port
   */
  public int getPort()
  {
    return port;
  }
}
