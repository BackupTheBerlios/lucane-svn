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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.server;

import org.lucane.common.Message;
import org.lucane.common.ObjectConnection;

/**
 * This interface has to be implemented by a service in order to run 
 * inside the server as an internal service and not requiring opening a new port
 * and running a distinct processus.
 */
public abstract class Service
{
  /**
   * Get a service class name
   * 
   * @return the class name
   */
  public String getName()
  {
    return this.getClass().getPackage().getName();
  }

  /**
   * Return the service base directory
   * 
   * @return the service directory
   */
  public String getDirectory()
  {
  	String pack = this.getClass().getPackage().getName();

  	String url = "jar:file:///" + System.getProperty("user.dir") + "/" + 
		Server.APPLICATIONS_DIRECTORY + pack + ".jar!/";

  	return url.replace('\\', '/');
  }

  /**
   * Called each time a request for this service has to be treated.
   * 
   * @param oc the connection
   * @param message the message
   */
  public abstract void process(ObjectConnection oc, Message message);

  /**
   * Initialize the service.
   * Called every time the server is started.
   * 
   * @param parent the Server
   */
  public void init(Server parent) {}

  /**
   * Install the service.
   * Only called the first time a service is initialized.
   */
  public void install() {}
  
  /**
   * Shutdown the service.
   * Called at server shutdown
   */
  public void shutdown() {}
}
