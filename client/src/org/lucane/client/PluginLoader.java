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
  private Client parent;
  private Vector plugins;
  private static PluginLoader instance = null;

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
    this.parent = Client.getInstance();
    this.plugins = new Vector();
  }

  /**
   * Check if a Plugin is available
   * 
   * @param name the Plugin to check
   * @return true if the client has this Plugin
   */
  public boolean hasPlugin(String name)
  {
    return this.hasPlugin(name, "", false);
  }

  /**
   * Check if a Plugin is available
   * 
   * @param name the Plugin to check
   * @param version the version to check
   * @return true if the Client has this version of the Plugin
   */
  public boolean hasPlugin(String name, String version)
  {
    return hasPlugin(name, version, false);
  }

  /**
   * Check if a Plugin is available
   * 
   * @param name the Plugin to check
   * @param version the version to check
   * @param load trus if the PluginLoader has to load the Plugin
   * @return true if the Client has this version of the Plugin
   */
  protected boolean hasPlugin(String name, String version, boolean load)
  {
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
        
        Plugin p = (Plugin)Class.forName(className, true, loader).newInstance();
        p.setLocale(Client.getInstance().getLanguage());
        this.plugins.add(p);
		Logging.getLogger().info(
              "Loaded plugin " + p.getName() + " v. " + 
              p.getVersion());
      }
      catch(Exception e)
      {
        return false;
      }
    }

    boolean has = false;
    for(int i = 0; i < this.plugins.size(); i++)
    {
      if(((Plugin)this.plugins.elementAt(i)).getName().equals(name) && 
         (((Plugin)this.plugins.elementAt(i)).getVersion().equals(version) 
         || version.equals("")))
      {
        has = true;
        break;
      }
    }

    return has;
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
  public void load(ObjectConnection oc, Message message)
  {
    Logging.getLogger().finest("PluginLoader::load() SOURCE:" + message.getSender());
	Logging.getLogger().finest("PluginLoader::load() DEST:" + message.getApplication());
	Logging.getLogger().finest("PluginLoader::load() COMMAND:" + message.getData());

    for(int i = 0; i < this.plugins.size(); i++)
    {
      if(((Plugin)this.plugins.elementAt(i)).getName().equals(message.getApplication()))
      {
		Logging.getLogger().fine("Trying to load plugin " + 
              ((Plugin)this.plugins.elementAt(i)).getName());

        Plugin p = ((Plugin)this.plugins.elementAt(i)).init(new ConnectInfo[0], false);
        p.setLocale(Client.getInstance().getLanguage());
        p.load(oc, message.getSender(), (String)message.getData());
        (new Thread(p)).start();
		Logging.getLogger().info("Plugin " + ((Plugin)this.plugins.elementAt(i)).getName() + " loaded.");

        break;
      }
    }
  }

  /**
   * Runs the requested Plugin in a new Thread
   * 
   * @param plugin_name the Plugin to run
   * @param friends the connexions associated with the plugin
   */
  public void run(String plugin_name, ConnectInfo[] friends)
  {
    for(int i = 0; i < this.plugins.size(); i++)
    {
      if(((Plugin)this.plugins.elementAt(i)).getName().equals(plugin_name))
      {
		Logging.getLogger().fine("Trying to run plugin " + 
              ((Plugin)this.plugins.elementAt(i)).getName());

        Plugin p = ((Plugin)this.plugins.elementAt(i)).init(friends, true);
        p.setLocale(Client.getInstance().getLanguage());
        (new Thread(p)).start();
        Logging.getLogger().fine("Plugin " + ((Plugin)this.plugins.elementAt(i)).getName()
         + " started.");

        break;
      }
    }
  }

  /**
   * Get the number of available plugins
   * 
   * @return the number of plugins
   */
  public int getNumberOfPlugins()
  {
    return this.plugins.size();
  }

  /**
   * Get a plugin in the list. Used with getNumberOfPlugins() to
   * iterate through the plugin list.
   * 
   * @param index the index of the plugin
   * @return the corresponding Plugin
   */
  public Plugin getPluginAt(int index)
  {
    return (Plugin)this.plugins.elementAt(index);
  }
  
  /**
   * Get a plugin by its name
   * 
   * @param name the plugin name
   * @return the corresponding Plugin
   */
  public Plugin getPlugin(String name)
  {
  	for(int i = 0; i < this.plugins.size(); i++)
  	{
  		Plugin p = (Plugin)this.plugins.elementAt(i); 
  		if(p.getName().equals(name))
  			return p;
  	}
  	return null;
  }
}
