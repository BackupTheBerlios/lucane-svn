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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;


public class Proxy
{
  private static final int DEFAULT_PORT = 5119;
  private ServerSocket listener;
  private Hashtable hash;
  private boolean stop;

  /**
   * Creates a new Proxy object.
   */
  public Proxy(int port)
  {
    try
    {
      this.listener = new ServerSocket(port);
      this.hash = new Hashtable();
      this.stop = false;
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Accept new connections
   */
  public void listen()
  {
    Socket client = null;

    while(! this.stop)
    {
      try
      {
        client = this.listener.accept();
        (new ProxySlave(this, client)).start();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * register a user and a proxyslave
   * 
   * @param ps a ProxySlave
   * @param s a user
   */
  public void register(ProxySlave ps, String s)
  {
    hash.put(s, ps);
  }

  /**
   * Get the ProxySlave associated to a user
   * 
   * @param s the user
   * @return the ProxySlave
   */
  public ProxySlave whois(String s)
  {
    return (ProxySlave)hash.get(s);
  }

  /**
   * Runs the proxy
   * 
   * @param args comment line arguments
   */
  public static void main(String[] args)
  {
	try {
		Logging.init("lucane.log", "ALL");
	} catch(IOException ioe) {
		System.err.println("Unable to init logging, exiting.");
		System.exit(1);
	}
	
	Proxy p;
	
	if(args.length == 1)
    	p = new Proxy(Integer.parseInt(args[0]));
    else
    	p = new Proxy(DEFAULT_PORT);
    
    p.listen();
  }
}
