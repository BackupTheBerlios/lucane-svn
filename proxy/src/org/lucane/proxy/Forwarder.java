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

/**
 * A forwarder simply writes to an OutputStream
 * what it has read in an InputStream
 */
public class Forwarder
  extends Thread
{
  ObjectConnection read;
  ObjectConnection write;

  /**
   * Creates a new Forwarder object.
   */
  public Forwarder(ObjectConnection read, ObjectConnection write)
  {
    this.read = read;
    this.write = write;
  }

  /**
   * Usefull to be run as a new Thread.
   * Copy a stream to another
   */
  public void run()
  {
    try
    {
      while(true)
      {
        write.write(read.read());
      }

    }
    catch(Exception e)
    {
      read.close();
      write.close();
	  Logging.getLogger().warning("Forwarder Error : " + e);
    }
  }
}
