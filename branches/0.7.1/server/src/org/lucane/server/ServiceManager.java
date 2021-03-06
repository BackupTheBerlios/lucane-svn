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
	
	/**
	 * Load the services from the jar files
	 */
	protected void loadAllServices()
	{
		String baseURL = Server.getInstance().getWorkingDirectory() + Server.APPLICATIONS_DIRECTORY;
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
				String jarPath = baseURL + servicename + ".jar";
				loader.addUrl("jar:file:///" + jarPath + "!/");
				
				String className = JarUtils.getServiceClass(jarPath);
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
	
	/**
	 * Start all services
	 */
	protected void startAllServices()
	{
		
		Iterator services = this.services.values().iterator();
		while(services.hasNext())
		{
			Service serv = (Service)services.next();
			try
			{
				// start the service
				ServiceConcept service = store.getServiceStore().getService(serv.getName());				
				serv.init(Server.getInstance());
				
				// install if necessary		
				if (! service.isInstalled())
				{
					serv.install();
					service.setInstalled();
					store.getServiceStore().updateService(service);
				}
				
				//add the service connect info
				ConnectInfo serverInfo = ConnectInfoManager.getInstance().getServerInfo();
				ConnectInfo serviceInfo = new ConnectInfo(serv.getName(),
						serverInfo.getAuthenticationServer(), serverInfo.hostname,
						serverInfo.port, "nokey", "service");
				ConnectInfoManager.getInstance().addConnectInfo(serviceInfo);
				
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
	 * Shutdown all services
	 */
	protected void shutdownAllServices()
	{
		
		Iterator services = this.services.values().iterator();
		while(services.hasNext())
		{
			Service serv = (Service)services.next();
			serv.shutdown();
			
			//remove the connectInfo
			ConnectInfoManager.getInstance().removeConnectInfo(serv.getName());
			
			Logging.getLogger().info("Service '" + serv.getName() + "' shutdowned.");
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
