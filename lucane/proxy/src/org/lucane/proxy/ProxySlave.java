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
 
package org.lucane.proxy;

import org.lucane.common.*;
import java.net.Socket;
import java.util.StringTokenizer;


class ProxySlave
  extends Thread
{

  private ObjectConnection oc;
  private Proxy parent;
  private ProxySlave friend;
  private Message init;
  private ConnectInfo user;

  /**
   * Creates a new ProxySlave object.
   * 
   * @param parent the proxy
   * @param s the socket
   */
  public ProxySlave(Proxy parent, Socket s)
  {
    super();
    this.parent = parent;
    this.oc = new ObjectConnection(s);
    this.friend = null;
  }

  /**
   * Used to be run as a new Thread
   */
  public void run()
  {
    this.readMessage();

    if(this.friend != null)
      this.forward();
  }

  /**
   * Read a line on the socket
   */
  private void readMessage()
  {
    try
    {
      init = (Message)this.oc.read();
      String data = (String)init.getData();
      if(data.startsWith("CONNECT_SET"))
      {
        StringTokenizer stk = new StringTokenizer(data);
        String command = stk.nextToken();
        String username = stk.nextToken();
        String userserver = stk.nextToken();
        String hostname = stk.nextToken();
        int port = Integer.parseInt(stk.nextToken());
        String type = stk.nextToken();
        user = new ConnectInfo(username, userserver, hostname, port, "nokey", type);

        oc.write("OK");
		Logging.getLogger().fine("user : " + user);
        parent.register(this, user.getName());
      }
      else
      {
        ConnectInfo userdest = init.getReceiver();
        this.friend = parent.whois(userdest.getName());
      }
    }
    catch(Exception e)
    {
		Logging.getLogger().warning("Exception : " + e);
    }
  }

  /**
   * Forward a socket connection to a client
   */
  private void forward()
  {
	Logging.getLogger().info("BEGIN FORWARD : " + friend.user);

    try
    {
      Socket sock = new Socket(friend.user.hostname, friend.user.port);

      //copy the init
      ObjectConnection myoc = new ObjectConnection(sock);
      myoc.write(init);

      //start the two forwarders
      Thread t1 = new Forwarder(myoc, oc);
      Thread t2 = new Forwarder(myoc, oc);
      t1.start();
      t2.start();

      //wait
      t1.join();
      t2.join();
    }
    catch(Exception e)
    {
		Logging.getLogger().warning("Exception : " + e);
    }

	Logging.getLogger().fine("END FORWARD : " + friend.user);
  }
}
