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

import org.lucane.common.concepts.*;
import org.lucane.server.store.*;

import javax.naming.*;
import javax.naming.directory.*;

/* ou=groups,dc=lucane,dc=org
 * |
 * ou=groups
 *  |-- cn=admins
 *       | member=allUsers
 *       | member=grantedUsers
 *  |-- cn=othergroups
 * 
 * ou=users
 *  |-- cn=admins
 *       | member=admin
 * 
 * ou=plugins
 *  |-- cn=admins
 *       | member=org.lucane.applications.administrator
 *       | member=...
 * 
 * ou=services
 *  |-- cn=admins
 *       | member=org.lucane.applications.administrator
 */

public class LdapGroupStore extends GroupStore
{
	private Store store;
	
	private DirContext groupContext;
	private DirContext userContext;
	private DirContext pluginContext;
	private DirContext serviceContext;
	private HashMap mapping;
	private HashMap attributes;
	
	public LdapGroupStore(Store store, LdapConfig config)
	throws Exception
	{
		this.store = store;
		
		Hashtable ht = new Hashtable();
		ht.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ht.put(DirContext.SECURITY_AUTHENTICATION, config.getAuthType());
		ht.put(DirContext.SECURITY_PRINCIPAL, config.getAuthBindDn());
		ht.put(DirContext.SECURITY_CREDENTIALS, config.getAuthPassword());
		
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + "ou=groups," +config.getGroupsDn());		
		this.groupContext = new InitialDirContext(ht);
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + "ou=users," +config.getGroupsDn());
		this.userContext = new InitialDirContext(ht);
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + "ou=plugins," +config.getGroupsDn());
		this.pluginContext = new InitialDirContext(ht);
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + "ou=services," +config.getGroupsDn());
		this.serviceContext = new InitialDirContext(ht); 
	
		this.attributes = config.getGroupsAttributes();		
		this.mapping = config.getGroupsMapping();		
	}
	
	/**
	 * Does the store has any data ?
	 * 
	 * @return true if the store is already created
	 */	
	public boolean isInitialized()
	throws Exception
	{
		return getAllGroups().hasNext();
	}

	public void storeGroup(GroupConcept group) 
	throws Exception
	{              
		//store group           
		BasicAttributes attrs = getBasicAttributes(group);
   
		//store group links
		Iterator i = group.getParents();
		BasicAttribute member = new BasicAttribute((String)mapping.get("member"), "cn=null");
		while(i.hasNext())
		{
			GroupConcept parent = (GroupConcept)i.next();
			member.add("cn=" + parent.getName());
		}
		attrs.put(member);
		groupContext.createSubcontext((String)mapping.get("name") + '=' + group.getName(), attrs);
    
		//store user links
		attrs = getBasicAttributes(group);
		i = group.getUsers();
		member = new BasicAttribute((String)mapping.get("member"), "cn=null");
		while(i.hasNext())
		{
			UserConcept user = (UserConcept)i.next();
			member.add("cn=" + user.getName());
		}
		attrs.put(member);	
		userContext.createSubcontext((String)mapping.get("name") + '=' + group.getName(), attrs);
		
		//store services links
		attrs = getBasicAttributes(group);
		i = group.getServices();
		member = new BasicAttribute((String)mapping.get("member"), "cn=null");
		while(i.hasNext())
		{
			ServiceConcept service = (ServiceConcept)i.next();
			member.add("cn=" + service.getName());
		}
		attrs.put(member);	  
		serviceContext.createSubcontext((String)mapping.get("name") + '=' + group.getName(), attrs);
		
		//store plugins links
		attrs = getBasicAttributes(group);
		i = group.getPlugins();
		member = new BasicAttribute((String)mapping.get("member"), "cn=null");
		while(i.hasNext())
		{
			PluginConcept plugin = (PluginConcept)i.next();
			member.add("cn=" + plugin.getName());
		}
		attrs.put(member);	    
		pluginContext.createSubcontext((String)mapping.get("name") + '=' + group.getName(), attrs);
		
	}

	public void updateGroup(GroupConcept group) 
	throws Exception
	{
		removeGroupOnly(group);
		storeGroup(group);
	}

	public void removeGroup(GroupConcept group) 
	throws Exception
	{
		//-- delete basic infos	
		removeGroupOnly(group);
		
		//delete group links
		BasicAttributes attrs = new BasicAttributes();
		attrs.put(new BasicAttribute((String)mapping.get("member"), group.getName()));
		NamingEnumeration enum = groupContext.search("", attrs);
		while(enum.hasMoreElements())
		{
			NameClassPair pair = (NameClassPair)enum.nextElement();
			Attributes myattrs = groupContext.getAttributes(pair.getName());
			groupContext.modifyAttributes(pair.getName(), DirContext.REMOVE_ATTRIBUTE, attrs);
		}
	}

	public GroupConcept getGroup(String name) 
	throws Exception
	{
		GroupConcept group = null;
		
		String groupKey = (String)mapping.get("name") + '=' + name;		
		Attributes attrs = groupContext.getAttributes(groupKey);
		
		name = (String)attrs.get((String)mapping.get("name")).get();
		group = new GroupConcept(name);

		try {
			String description = (String)attrs.get((String)mapping.get("description")).get();
			group.setDescription(description);
		} catch(Exception e) {
			//no description
		}
            
		setGroupLinks(group);
		setUserLinks(group);
		setPluginLinks(group);
		setServiceLinks(group);

		return group;  
	}

	public Iterator getAllGroups() 
	throws Exception
	{
		ArrayList groups = new ArrayList();
		
		NamingEnumeration list = groupContext.list("");		
		while(list.hasMore())
		{
			NameClassPair pair = (NameClassPair)list.next();
			String name = pair.getName();
			name = name.substring(name.indexOf('=')+1);
			
			groups.add(getGroup(name));
		}
		
		return groups.iterator();
	}
	
	//-- private methods
    
	private void setServiceLinks(GroupConcept group)
	throws Exception
	{
		String groupKey = (String)mapping.get("name") + '=' + group.getName();		
		Attributes attrs = serviceContext.getAttributes(groupKey);

		Attribute attr = attrs.get((String)mapping.get("member"));
		NamingEnumeration list = attr.getAll();
				
		while(list.hasMore())
		{
			String name = (String)list.next();
			name = name.substring(name.indexOf('=')+1);
			if(name.equals("null"))
				continue;
			
			ServiceConcept service = store.getServiceStore().getService(name);
			group.addService(service);
		}
	}

	private void setPluginLinks(GroupConcept group)
	throws Exception
	{
		String groupKey = (String)mapping.get("name") + '=' + group.getName();		
		Attributes attrs = pluginContext.getAttributes(groupKey);

		Attribute attr = attrs.get((String)mapping.get("member"));
		NamingEnumeration list = attr.getAll();
				
		while(list.hasMore())
		{
			String name = (String)list.next();
			name = name.substring(name.indexOf('=')+1);
			if(name.equals("null"))
				continue;
			
			PluginConcept plugin = store.getPluginStore().getPlugin(name);
			group.addPlugin(plugin);
		}
	}

	private void setUserLinks(GroupConcept group)
	throws Exception
	{
		String groupKey = (String)mapping.get("name") + '=' + group.getName();		
		Attributes attrs = userContext.getAttributes(groupKey);

		Attribute attr = attrs.get((String)mapping.get("member"));
		NamingEnumeration list = attr.getAll();
				
		while(list.hasMore())
		{
			String name = (String)list.next();
			name = name.substring(name.indexOf('=')+1);
			if(name.equals("null"))
				continue;
			
			UserConcept user = store.getUserStore().getUser(name);
			group.addUser(user);
		}
	}

	private void setGroupLinks(GroupConcept group)
	throws Exception
	{
		String groupKey = (String)mapping.get("name") + '=' + group.getName();		
		Attributes attrs = groupContext.getAttributes(groupKey);

		Attribute attr = attrs.get((String)mapping.get("member"));
		NamingEnumeration list = attr.getAll();
				
		while(list.hasMore())
		{
			String name = (String)list.next();
			name = name.substring(name.indexOf('=')+1);
			if(name.equals("null"))
				continue;
			
			GroupConcept parent = store.getGroupStore().getGroup(name);
			group.addParent(parent);
		}		
	}
    
	private void removeGroupOnly(GroupConcept group)
	throws Exception
	{
		//delete group & group links           
		groupContext.destroySubcontext((String)mapping.get("name") + '=' + group.getName());
		
		//delete user links
		userContext.destroySubcontext((String)mapping.get("name") + '=' + group.getName());
        
		//delete services links
		serviceContext.destroySubcontext((String)mapping.get("name") + '=' + group.getName());
        
		//delete plugins links
		pluginContext.destroySubcontext((String)mapping.get("name") + '=' + group.getName());
	}	
	
	private BasicAttributes getBasicAttributes(GroupConcept group)
	{
		BasicAttributes attrs = new BasicAttributes();
		attrs.put(new BasicAttribute((String)mapping.get("name"), group.getName()));
		if(group.getDescription() != null && group.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), group.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 
		
		return attrs;
	}
}