/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004 Vincent Fiack <vfiack@mail15.com>
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

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * Simple operations on jar files
 */
public class JarUtils
{
	/**
	 * Get an attribute value
	 * 
	 * @param jarUrl the jar file
	 * @param attribute the attribute to fetch
	 * @return the attribute value
	 */
	private static String getManifestAttribute(String jarUrl, String attribute) 
	throws IOException
	{
		JarFile jar = new JarFile(jarUrl);
		Attributes attr = jar.getManifest().getMainAttributes();
		String value = attr.getValue(attribute);
		
		jar.close();
		return value;
	}
	
	/**
	 * Get the service class name specified in a jar
	 * 
	 * @param jarUrl the jar file
	 * @return the class name
	 */
	public static String getServiceClass(String jarUrl)
	throws IOException
	{
		return getManifestAttribute(jarUrl, "Service-Class");
	}

	/**
	 * Get the plugin class name specified in a jar
	 * 
	 * @param jarUrl the jar file
	 * @return the class name
	 */
	public static String getPluginClass(String jarUrl)
	throws IOException
	{
		return getManifestAttribute(jarUrl, "Plugin-Class");
	}
}