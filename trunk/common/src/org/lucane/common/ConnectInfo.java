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
package org.lucane.common;

import org.lucane.common.signature.*;
import java.io.Serializable;

/**
 * Contains connection related informations for clients and services
 */
public class ConnectInfo implements Serializable
{
  private String name; //login
  private String authenticationServer; //auth server
  public String hostname; //where to connect
  public int port;
  public String type; //server, service, client or plugin
  
  public Verifier verifier;
  private String key;

  /**
   * Constructor.
   * 
   * @param name the user name
   * @param server the user's server
   * @param hostname the host where the client is listening
   * @param port the port where the client is listening
   * @param key the public key associated with a user to verify its signatures
   * @param type client or service ?
   */
  public ConnectInfo(String name, String server, String hostname, int port, 
                     String key, String type)
  {
    this.name = name;
    this.authenticationServer = server;
    this.hostname = hostname;
    this.port = port;
    this.type = type;
    this.key = key;

    try
    {
      this.verifier = new Verifier(key);
    }
    catch(SignatureException e)
    {
      this.verifier = null;
    }

    Logging.getLogger().fine("** new ConnectInfo: " + this);
  }

  /**
   * Useful for debugging
   * 
   * @return c
   */
  public String toString()
  {
    return this.name + "@" + this.authenticationServer + " = " + this.hostname + ":" + 
           this.port + " (" + this.type + ")";
  }

  /**
   * Useful to transmit ConnectInfos between elements.
   * 
   * @return a String representing the ConnectInfo
   */
  public String getRepresentation()
  {
    return this.name + " " + this.authenticationServer + " " + this.hostname + " " + 
           this.port + " " + this.key + " " + this.type;
  }

  /**
   * Get the username
   * 
   * @return the user name
   */
  public String getName()
  {
    return this.name;
  }
  
  public String getAuthenticationServer()
  {
  	return this.authenticationServer;
  }
  
  /**
   * Change the host name
   * 
   * @param server the new hostname
   */
  public void setHostName(String hostname)
  {
  		this.hostname = hostname;
  }
  
  /**
   * Is the object pointed by this ConnectInfo a service ?
   * 
   * @return true if it is a service
   */
  public boolean isService()
  {
  	return this.type.equals("service");
  }
  
  /**
   * Is the object pointed by this ConnectInfo a Server ?
   * 
   * @return true if it is a service
   */
  public boolean isServer()
  {
	return this.type.equals("Server");
  }
}
