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
package org.lucane.server.store.ldap;

import java.util.*;

import org.lucane.common.concepts.ServiceConcept;
import org.lucane.server.store.ServiceStore;

import javax.naming.*;
import javax.naming.directory.*;

public class LdapServiceStore extends ServiceStore
{
	private DirContext context;
	private HashMap mapping;
	private HashMap attributes;
	
	public LdapServiceStore(LdapConfig config)
	throws Exception
	{
		Hashtable ht = new Hashtable();
		ht.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ht.put(DirContext.SECURITY_AUTHENTICATION, config.getAuthType());
		ht.put(DirContext.SECURITY_PRINCIPAL, config.getAuthBindDn());
		ht.put(DirContext.SECURITY_CREDENTIALS, config.getAuthPassword());		
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + config.getServicesDn());
		this.context = new InitialDirContext(ht);
		
		this.attributes = config.getServicesAttributes();		
		this.mapping = config.getServicesMapping();	
	}


	public void storeService(ServiceConcept service) 
	throws Exception
	{		
		BasicAttributes attrs = new BasicAttributes();
		
		//mapped service attributes
		attrs.put(new BasicAttribute((String)mapping.get("name"), service.getName()));
		attrs.put(new BasicAttribute((String)mapping.get("installed"), service.isInstalled() ? "1" : "0"));

		if(service.getDescription() != null && service.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), service.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 

		context.createSubcontext((String)mapping.get("name") + '=' + service.getName(), attrs);
	}

	public void updateService(ServiceConcept service) 
	throws Exception
	{
		BasicAttributes attrs = new BasicAttributes();
		
		//mapped service attributes
		attrs.put(new BasicAttribute((String)mapping.get("name"), service.getName()));
		attrs.put(new BasicAttribute((String)mapping.get("installed"), service.isInstalled() ? "1" : "0"));

		if(service.getDescription() != null && service.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), service.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 

		context.modifyAttributes((String)mapping.get("name") + '=' + service.getName(), DirContext.REPLACE_ATTRIBUTE, attrs);		
	}

	public void removeService(ServiceConcept service) 
	throws Exception
	{
		context.destroySubcontext((String)mapping.get("name") + '=' + service.getName());
	}

	public ServiceConcept getService(String serviceName) 
	throws Exception
	{
		String serviceKey = (String)mapping.get("name") + '=' + serviceName;
		Attributes attrs = context.getAttributes(serviceKey);
		
		String name = (String)attrs.get((String)mapping.get("name")).get();
		boolean installed = attrs.get((String)mapping.get("installed")).get().equals("1");
		ServiceConcept service = new ServiceConcept(name, installed);
				
		try {
			String description = (String)attrs.get((String)mapping.get("description")).get();
			service.setDescription(description);
		} catch(Exception e) {
			//no description
		}

		return service;
	}

	public Iterator getAllServices() throws Exception
	{
		ArrayList services = new ArrayList();
		
		NamingEnumeration list = context.list("");		
		while(list.hasMore())
		{
			NameClassPair pair = (NameClassPair)list.next();
			String name = pair.getName();
			name = name.substring(name.indexOf('=')+1);
			
			services.add(getService(name));
		}
		
		return services.iterator();
	}
}