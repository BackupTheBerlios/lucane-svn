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

package org.lucane.server;

import java.util.*;
import java.util.jar.JarFile;

import org.lucane.common.*;
import org.lucane.common.concepts.ServiceConcept;
import org.lucane.server.store.Store;

public class ServiceManager
{
	//-- instance management
	private static ServiceManager instance = null;
	public static ServiceManager getInstance()
	{
		if(instance == null)
			instance = new ServiceManager();
		
		return instance;
	}
	
	//-- attributes
	private Store store;
	private HashMap services;
	
	/**
	 * Constructor
	 */
	private ServiceManager()
	{
		this.store = Server.getInstance().getStore();
		this.services = new HashMap();
	}	
	
	protected void loadAllServices()
	{
			String baseURL = System.getProperty("user.dir")	+ "/" + Server.APPLICATIONS_DIRECTORY;
						
			//get all services from store
			Iterator services;
			try {
				services = store.getServiceStore().getAllServices();
			} catch (Exception e) {
				Logging.getLogger().severe("Unable get service list : " + e);
				services = new ArrayList().iterator();
			}
			
			LucaneClassLoader loader = LucaneClassLoader.getInstance();
			while (services.hasNext())
			{
				ServiceConcept service = (ServiceConcept)services.next();
				String servicename = service.getName();
				try
				{
					loader.addUrl("jar:file:///" + baseURL + servicename + ".jar!/");
					//TODO add a JarUtils in common to do this and the same for plugins
					String className =
						(new JarFile(baseURL + servicename + ".jar"))
						.getManifest()
						.getMainAttributes()
						.getValue("Service-Class");
					
					if (className == null)
						continue;
					
					Service serv = (Service)Class.forName(className, true, loader).newInstance();
					this.services.put(serv.getName(), serv);
					Logging.getLogger().info("Service '" + servicename + "' loaded.");
				}
				catch (Exception e)
				{
					Logging.getLogger().warning("Unable to load service '" + servicename);
					e.printStackTrace();
				}
			}
	}
	
	protected void startAllServices()
	{
		
		Iterator services = this.services.values().iterator();
		while(services.hasNext())
		{
			Service serv = (Service)services.next();
			try
			{
				ServiceConcept service = store.getServiceStore().getService(serv.getName());
				
				serv.init(Server.getInstance());
				
				if (! service.isInstalled())
				{
					serv.install();
					service.setInstalled();
					store.getServiceStore().updateService(service);
				}
				
				//TODO clean the connectinfo creation
				Server.getInstance().connections.add(
						new ConnectInfo(
								serv.getName(),
								Server.getInstance().serverIp,
								Server.getInstance().serverIp,
								Server.getInstance().port,
								"nokey",
						"service"));
				
				Logging.getLogger().info("Service '" + serv.getName() + "' started.");
			}
			catch (Exception e)
			{
				Logging.getLogger().warning("Unable to start service : " + serv.getName() + " : " + e);
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Get a service by its name
	 * 
	 * @param name the service name
	 * @return the service instance
	 */
	public Service getService(String name)
	{
		return (Service)this.services.get(name);
	}
}