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

import java.io.*;

/**
 * Used to fetch classes from our custom ClassLoader when deserializing custom objects
 */
public class LucaneObjectInputStream extends ObjectInputStream
{
  /**
   * Constructor
   * 
   * @param is a stream
   */
  public LucaneObjectInputStream(InputStream is)
  throws IOException
  {
    super(is);
  }
  
  /**
   * Resolve a class from its description
   * Uses LucaneClassLoader.
   * 
   * @param desc the class descriptor
   * @return the class
   */
  protected Class resolveClass(ObjectStreamClass desc)
  throws IOException, ClassNotFoundException
  {
    String className = desc.getName();
    return Class.forName(className, false, LucaneClassLoader.getInstance());
  }
}
