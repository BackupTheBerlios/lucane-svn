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

import org.lucane.common.concepts.PluginConcept;
import org.lucane.server.store.PluginStore;

import javax.naming.*;
import javax.naming.directory.*;

public class LdapPluginStore extends PluginStore
{
	private DirContext context;
	private HashMap mapping;
	private HashMap attributes;
	
	public LdapPluginStore(LdapConfig config)
	throws Exception
	{
		Hashtable ht = new Hashtable();
		ht.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ht.put(DirContext.SECURITY_AUTHENTICATION, config.getAuthType());
		ht.put(DirContext.SECURITY_PRINCIPAL, config.getAuthBindDn());
		ht.put(DirContext.SECURITY_CREDENTIALS, config.getAuthPassword());		
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + config.getPluginsDn());
		this.context = new InitialDirContext(ht);
			
		this.attributes = config.getPluginsAttributes();		
		this.mapping = config.getPluginsMapping();		
	}


	public void storePlugin(PluginConcept plugin) 
	throws Exception
	{		
		BasicAttributes attrs = new BasicAttributes();
		
		//mapped plugin attributes
		attrs.put(new BasicAttribute((String)mapping.get("name"), plugin.getName()));
		attrs.put(new BasicAttribute((String)mapping.get("version"), plugin.getVersion()));

		if(plugin.getDescription() != null && plugin.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), plugin.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 

		context.createSubcontext((String)mapping.get("name") + '=' + plugin.getName(), attrs);
	}

	public void updatePlugin(PluginConcept plugin) 
	throws Exception
	{
		BasicAttributes attrs = new BasicAttributes();
		
		//mapped plugin attributes
		attrs.put(new BasicAttribute((String)mapping.get("name"), plugin.getName()));
		attrs.put(new BasicAttribute((String)mapping.get("version"), plugin.getVersion()));

		if(plugin.getDescription() != null && plugin.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), plugin.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 

		context.modifyAttributes((String)mapping.get("name") + '=' + plugin.getName(), DirContext.REPLACE_ATTRIBUTE, attrs);		
	}

	public void removePlugin(PluginConcept plugin) 
	throws Exception
	{
		context.destroySubcontext((String)mapping.get("name") + '=' + plugin.getName());
	}

	public PluginConcept getPlugin(String pluginName) 
	throws Exception
	{
		String pluginKey = (String)mapping.get("name") + '=' + pluginName;
		
		Attributes attrs = context.getAttributes(pluginKey);
		
		String name = (String)attrs.get((String)mapping.get("name")).get();
		String version = (String)attrs.get((String)mapping.get("version")).get();
		PluginConcept plugin = new PluginConcept(name, version);
				
		try {
			String description = (String)attrs.get((String)mapping.get("description")).get();
			plugin.setDescription(description);
		} catch(Exception e) {
			//no description
		}

		return plugin;
	}

	public Iterator getAllPlugins() throws Exception
	{
		ArrayList plugins = new ArrayList();
		
		NamingEnumeration list = context.list("");		
		while(list.hasMore())
		{
			NameClassPair pair = (NameClassPair)list.next();
			String name = pair.getName();
			name = name.substring(name.indexOf('=')+1);
			
			plugins.add(getPlugin(name));
		}
		
		return plugins.iterator();
	}
}