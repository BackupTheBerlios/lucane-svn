/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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

import java.net.*;

/**
 * Used to add urls for services and plugins to the classpath
 */
public class LucaneClassLoader extends URLClassLoader
{
  //-- singleton
  private static LucaneClassLoader instance = null;

  /**
   * Get ClassLoader instance
   * 
   * @return the unique instance
   */
  public static LucaneClassLoader getInstance()
  {
    if(instance == null)
      instance = new LucaneClassLoader();

    return instance;
  }
  
  /**
   * Constructor
   */
  private LucaneClassLoader()
  {
    super(new URL[0]);
  }

  /**
   * Add an url to use in class lookup
   * 
   * @param url the url
   */
  public void addUrl(URL url)
  {
    this.addURL(url);
  }
}
