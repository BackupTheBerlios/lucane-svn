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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

import javax.swing.ImageIcon;


/**
 * Every plugin has to extend this class and redefine its abstract methods.
 * See the tutorial on writing extensions and the sources of
 * the various plugins for more infiormations.
 */
public abstract class Plugin
  implements Runnable
{
  //do not redeclare them in your plugin !
  protected boolean starter;
  protected ResourceBundle bundle;
  private LocalConfig config;
  
  /**
   * Used by the PluginLoader
   *
   * @return the plugin class name
   */
  public String getName()
  {
    return this.getClass().getPackage().getName();
  }

  /**
   * Used by the PluginLoader to check if new versions needs to be downloaded
   * 
   * @return lthe plugin's version
   */
  public String getVersion()
  {
    return tr("version");
  }

  /**
   * Allow you to know who made these beatiful bugs :-P
   * 
   * @return the author's name
   */
  public String getAuthor()
  {
    return tr("author");
  }

  /**
   * Where to complain or send feedback.
   * 
   * @return the author's mail address
   */
  public String getEmail()
  {
    return tr("email");
  }

  /**
   * Used in the MainInterface.
   * 
   * @return a small description
   */
  public String getToolTip()
  {
    return tr("tooltip");
  }

  /**
   * Associate a picture with a plugin for ever move userfriendliness
   * 
   * @return the plugin's icon name ("plugin.jpg" for example)
   */
  public String getIcon()
  {
    return tr("icon");
  }

  /**
   * Associate a picture with a plugin for ever move userfriendliness
   * 
   * @return the plugin's icon name ("plugin.jpg" for example)
   */
  public String getIcon16()
  {
    return tr("icon16");
  }

  /**
   * Where to put the plugin in the MainInterface
   * 
   * @return the plugin's category
   */
  public String getCategory()
  {
    return tr("category");
  }

  /**
   * The title to show
   * 
   * @return the plugin's title
   */
  public String getTitle()
  {
    return tr("title");
  }
  
  /**
   * Can the plugin be run without any users ?
   * @return true if the plugin is standalone
   */
  public boolean isStandalone()
  {
  	return false;
  }

  /**
   * Used by the PluginLoader to initialize the plugin with adequate parameters
   * 
   * @param friends the connections to use
   * @param starter true if the start() method has to be called
   * @return a new plugin's instance
   */
  public abstract Plugin init(ConnectInfo[] friends, boolean starter);

  /**
   * Used by the PluginLoader to load a plugin previously initialized
   * with a command String and a Socket to communicate with.
   * 
   * @param oc the ObjectConnection
   * @param who the request sender
   * @param data network data for the plugin
   */
  public void load(ObjectConnection oc, ConnectInfo who, String data) 
  {
  }
  
  /**
   * Used when a plugin is started (ie. not loaded afeter a network request).
   * For example, it is called when a user wants to send a mesage to another.
   * It allows the plugin developpeur to ask for other parameters interactively.
   */
  public void start() 
  {
  	exit();
  }

  /**
   * Used by the PluginLoader when a plugin is loaded after a network query.
   */
  public void follow() 
  {
  	exit();
  }
  
  /**
   * Invoke a methode
   * 
   * @param method the method name
   * @param types the params types
   * @param params the params
   * @return the method result
   */
  public Object invoke(String method, Class[] types, Object[] params) 
  throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
  	Method m = this.getClass().getDeclaredMethod(method, types);
	return m.invoke(this, params);
  }


  /**
   * Allows the Client to know when to quit.
   */
  public void exit()
  {
  	Logging.getLogger().fine("Plugin exited");
  	try {
  		this.config.save();
  	} catch(IOException ioe) {
  		Logging.getLogger().warning("Unable to save localconfig");
  		ioe.printStackTrace();
  	}
  	Client.getInstance().unregisterPlugin(this);
  } 

  /**
   * Mandatory to be run as a new Thread
   */
  public void run()
  {
    Client.getInstance().registerPlugin(this);
	Logging.getLogger().fine("attempt to start a plugin thread");
	Logging.getLogger().fine("starter = " + this.starter);
	this.config = new LocalConfig(this.getName());	
	
    if(this.starter)
      start();
    else
      follow();
  }

  /**
   * Internationalization
   * 
   * @param lang the language t use (ie. fr, en, ...)
   */
  public void setLocale(String lang)
  {
    try {
      InputStream is = new URL(getDirectory() + "messages_" + lang +  ".properties").openStream();
      this.bundle = new PropertyResourceBundle(is);
    } catch(Exception e1) {
      try {
        InputStream is = new URL(getDirectory() + "messages.properties").openStream();
        this.bundle = new PropertyResourceBundle(is);
      } catch(Exception e2) {
        this.bundle = null;
      }
    }
  }

  /**
   * Translation
   * 
   * @param origin the String to get
   * @return the corresponding String in the correct language
   */
  public String tr(String origin)
  {
    try {
      return bundle.getString(origin);
    } catch(Exception e) {
      return origin;
    }
  }
  /**
   * Return the plugin's base directory
   * 
   * @return the plugin's directory
   */
  public String getDirectory()
  {
    String pack = this.getClass().getPackage().getName();

    String url = "jar:file:///" + System.getProperty("user.dir") + "/" + 
                 Client.APPLICATIONS_DIRECTORY + pack + ".jar!/";

    return url.replace('\\', '/');
  }
  
  /**
   * Return the plugins icon
   * 
   * @return the plugin icon
   */
  public ImageIcon getImageIcon()
  {
    try {
        return new ImageIcon(new URL(getDirectory() + getIcon()));
    } catch(Exception e) {
        //no image
        return new ImageIcon();
    }
  }
  
  /**
   * Return the plugins icon
   * 
   * @return the plugin icon in 16x16 if available
   */
  public ImageIcon getImageIcon16()
  {
    try {
        return new ImageIcon(new URL(getDirectory() + getIcon16()));
    } catch(Exception e) {
        //no image, try to return the "normal" one
        return getImageIcon();
    }
  }
  
  /**
   * Return the LocalConfig object
   * 
   * @return the local config
   */
  public LocalConfig getLocalConfig()
  {
  	return this.config;
  }
  
  public String toString()
  {
  	return getTitle();
  }
}
