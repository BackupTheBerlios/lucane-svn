/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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

import java.util.Properties;
import java.io.*;

import org.lucane.common.Logging;

/**
 * A local configuration system.
 * It can be used to store a few string values that are not crucial.
 * You should not store important or mandatory keys here, as the file
 * is store on the local client.
 * 
 * It can be compared to the cookie system in a web environment.
 */
public class LocalConfig
{
	static final String CONFIG_DIRECTORY = "etc/localconfig/"; 

	//-- attributes
	private String app;
	private String path;	
	private Properties properties;
	
	/**
	 * Constructor.
	 * Create the Properties and load the applicative configuration
	 * 
	 * @param app the application name
	 */
	protected LocalConfig(String app)
	{
		this.app = app;
		this.path = System.getProperty("user.dir") + '/' + CONFIG_DIRECTORY +
			Client.getInstance().getMyInfos().getName() + '/' + app + ".properties";
		this.path = this.path.replace('\\', '/');
		
		this.properties = new Properties();
		
		try {
			this.properties.load(new FileInputStream(this.path));
		} catch(FileNotFoundException fnfe) {
			//no file yet
		} catch(IOException ioe) {
			Logging.getLogger().warning("Error when loading LocalConfig for " + app);
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Set a value in the localconfig
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void set(String key, String value)
	{
		this.properties.setProperty(key, value);	
	}
	
	/**
	 * Set a value in the localconfig
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void set(String key, int value)
	{
		set(key, String.valueOf(value));	
	}	
	
	/**
	 * Get a value from the localconfig
	 * 
	 * @param key the key
	 * @return the value
	 */
	public String get(String key)
	{
		return this.properties.getProperty(key);
	}
	
	/**
	 * Get a value from the localconfig
	 * 
	 * @param key the key
	 * @return the value
	 */
	public int getInt(String key)
	{
		return Integer.parseInt(get(key));
	}
	
	/**
	 * Get a value from the localconfig
	 * 
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the value, or the default if value is null
	 */
	public String get(String key, String defaultValue)
	{
		return this.properties.getProperty(key, defaultValue);
	}
	
	/**
	 * Save the localconfig to a file
	 */
	protected void save()	
	throws IOException
	{
		this.properties.store(new FileOutputStream(this.path), this.app);
	}
}