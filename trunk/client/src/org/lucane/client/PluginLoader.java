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

import java.net.URL;
import java.util.*;
import java.util.jar.*;


/**
 * This is an important part of the Client.
 * It can dynamically load plugins.
 */
public class PluginLoader
{
	private static PluginLoader instance = null;
	
	//stores available plugins in a (name, plugin) map
	private HashMap availablePlugins;
	
	//stores running plugins
	private ArrayList runningPlugins;
	
	/**
	 * PluginLoader is a singleton
	 * 
	 * @return the unique PluginLoader instance
	 */
	public static PluginLoader getInstance()
	{
		if(instance == null)
			instance = new PluginLoader();
		
		return instance;
	}
	
	/**
	 * Creates a PluginLoader and initialize its plugins list
	 */
	private PluginLoader()
	{
		this.availablePlugins = new HashMap();
		this.runningPlugins = new ArrayList();
	}
	
	//-- plugin initialization & run
	
	/**
	 * Get a new plugin instance
	 * 
	 * @param name the plugin name
	 * @param friends the connectinfos
	 * @param starter true if the plugin is starter
	 * @return a new plugin instance
	 */
	public Plugin newPluginInstance(String name, ConnectInfo[] friends, boolean starter)
	{
		Plugin p = ((Plugin)this.availablePlugins.get(name)).newInstance(friends, starter);
		p.setLocale(Client.getInstance().getConfig().getLanguage());
		return p;
	}	
	
	/**
	 * Loads a Plugin.
	 * Initialize it with the correct streams and run it in a new Thread
	 * 
	 * @param friend the Socket
	 * @param source the source of this message
	 * @param dest the component that has to receive the message
	 * @param command the network request
	 */
	public Plugin load(ObjectConnection oc, Message message)
	{
		String name = message.getApplication();
		Logging.getLogger().fine("Trying to load plugin " + name);
		
		Plugin p = newPluginInstance(name, new ConnectInfo[0], false); 
		p.load(oc, message.getSender(), (String)message.getData());
		(new Thread(p, p.getName())).start();
		Logging.getLogger().info("Plugin " + name + " loaded.");
		return p;
	}
	
	/**
	 * Runs the requested Plugin in a new Thread
	 * 
	 * @param name the Plugin to run
	 * @param friends the connexions associated with the plugin
	 */
	public Plugin run(String name, ConnectInfo[] friends)
	{
		Logging.getLogger().fine("Trying to run plugin " + name);
		Plugin p = newPluginInstance(name, friends, true);
		(new Thread(p, p.getName())).start();
		Logging.getLogger().fine("Plugin " + name + " started.");
		
		return p;
	}
	
	//-- available plugins
	
	/**
	 * Check if a Plugin is available
	 * 
	 * @param name the Plugin to check
	 * @return true if the client has this Plugin
	 */
	public boolean isAvailable(String name)
	{
		return this.isAvailable(name, "", false);
	}
	
	/**
	 * Check if a Plugin is available
	 * 
	 * @param name the Plugin to check
	 * @param version the version to check
	 * @return true if the Client has this version of the Plugin
	 */
	public boolean isAvailable(String name, String version)
	{
		return isAvailable(name, version, false);
	}
	
	/**
	 * Check if a Plugin is available
	 * 
	 * @param name the Plugin to check
	 * @param version the version to check
	 * @param load trus if the PluginLoader has to load the Plugin
	 * @return true if the Client has this version of the Plugin
	 */
	protected boolean isAvailable(String name, String version, boolean load)
	{
		Plugin plugin;
		if(load == true)
		{
			try
			{
				LucaneClassLoader loader = LucaneClassLoader.getInstance();
				String baseURL = System.getProperty("user.dir") + "/" + Client.APPLICATIONS_DIRECTORY;
				URL url = new URL("jar:file:///" + baseURL + name + ".jar!/");
				loader.addUrl(url);
				Logging.getLogger().fine("plugin URL: " + url);
				
				//we have to set our ClassLoader to reload the plugin.
				Logging.getLogger().finer("classname: " + baseURL + name + ".jar");
				String className = (new JarFile(baseURL + name + ".jar")).getManifest()
				.getMainAttributes().getValue("Plugin-Class");
				
				plugin = (Plugin)Class.forName(className, true, loader).newInstance();
				plugin.setLocale(Client.getInstance().getConfig().getLanguage());
				this.availablePlugins.put(plugin.getName(), plugin);
				Logging.getLogger().info("Loaded plugin " + plugin.getName() + " v. " + 
						plugin.getVersion());
			}
			catch(Exception e)
			{
				return false;
			}
		}
		
		
		plugin = (Plugin)this.availablePlugins.get(name);
		return plugin != null && (plugin.getVersion().equals(version) || version.length() == 0);
	}	
	
	/**
	 * Get all available plugins
	 * 
	 * @return an iterator
	 */
	public Iterator getAvailablePlugins()
	{
		return this.availablePlugins.values().iterator();
	}
	
	/**
	 * Get a plugin by its name
	 * 
	 * @param name the plugin name
	 * @return the corresponding Plugin
	 */
	public Plugin getPlugin(String name)
	{
		return (Plugin)this.availablePlugins.get(name);
	}
	
	
	//-- running plugins
	
	/**
	 * Tells the client that a plugin has been launched.
	 */
	protected void addRunningPlugin(Plugin p)
	{
		Logging.getLogger().fine("registering : " + p.getName());
		this.runningPlugins.add(p);
	}
	
	/**
	 * Tells a client that a plugin has been closed.
	 */
	protected void removeRunningPlugin(Plugin p)
	{
		Logging.getLogger().fine("unregistering : " + p.getName());
		this.runningPlugins.remove(p);
		
		//if no more plugins are running, or if we quit the startup plugin,
		//exits the client cleanly
		if(runningPlugins.isEmpty() ||	p.getName().equals(Client.getInstance().getStartupPlugin()))
			Client.getInstance().cleanExit();
	}
	
	/**
	 * Get plugins that are currently running
	 * 
	 * @return an iterator containing active plugins
	 */
	public Iterator getRunningPlugins()
	{
		return runningPlugins.iterator();
	}
}
