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

import org.lucane.common.concepts.UserConcept;
import org.lucane.server.store.UserStore;

import javax.naming.*;
import javax.naming.directory.*;

public class LdapUserStore extends UserStore
{
	private DirContext context;
	private HashMap mapping;
	private HashMap attributes;
	
	public LdapUserStore(LdapConfig config)
	throws Exception
	{
		Hashtable ht = new Hashtable();
		ht.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ht.put(DirContext.SECURITY_AUTHENTICATION, config.getAuthType());
		ht.put(DirContext.SECURITY_PRINCIPAL, config.getAuthBindDn());
		ht.put(DirContext.SECURITY_CREDENTIALS, config.getAuthPassword());		
		ht.put(DirContext.PROVIDER_URL, config.getLdapUrl() + config.getUsersDn());
		this.context = new InitialDirContext(ht);
		
		this.attributes = config.getUsersAttributes();		
		this.mapping = config.getUsersMapping();	
	}

	public void storeUser(UserConcept user) 
	throws Exception
	{
		BasicAttributes attrs = new BasicAttributes();
		
		//mapped user attributes
		attrs.put(new BasicAttribute((String)mapping.get("name"), user.getName()));
		attrs.put(new BasicAttribute((String)mapping.get("password"), user.getPassword()));
		attrs.put(new BasicAttribute((String)mapping.get("startupPlugin"), user.getStartupPlugin()));
		attrs.put(new BasicAttribute((String)mapping.get("locked"), user.isLocked() ? "1" : "0"));

		if(user.getPrivateKey() != null && user.getPrivateKey().length() > 0)
		{
			attrs.put(new BasicAttribute((String)mapping.get("privateKey"), user.getPrivateKey()));
			attrs.put(new BasicAttribute((String)mapping.get("publicKey"), user.getPublicKey()));
		}

		if(user.getDescription() != null && user.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), user.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 

		context.createSubcontext((String)mapping.get("name") + '=' + user.getName(), attrs);
	}

	public void updateUser(UserConcept user) 
	throws Exception
	{
		BasicAttributes attrs = new BasicAttributes();
		
		//mapped user attributes
		attrs.put(new BasicAttribute((String)mapping.get("name"), user.getName()));
		attrs.put(new BasicAttribute((String)mapping.get("password"), user.getPassword()));
		attrs.put(new BasicAttribute((String)mapping.get("startupPlugin"), user.getStartupPlugin()));
		attrs.put(new BasicAttribute((String)mapping.get("locked"), user.isLocked() ? "1" : "0"));

		if(user.getPrivateKey() != null && user.getPrivateKey().length() > 0)
		{
			attrs.put(new BasicAttribute((String)mapping.get("privateKey"), user.getPrivateKey()));
			attrs.put(new BasicAttribute((String)mapping.get("publicKey"), user.getPublicKey()));
		}

		if(user.getDescription() != null && user.getDescription().length() > 0)
			attrs.put(new BasicAttribute((String)mapping.get("description"), user.getDescription()));

		//other attributes
		Iterator keys = attributes.keySet().iterator();
		while(keys.hasNext())
		{
			String key = (String)keys.next();
			attrs.put(new BasicAttribute(key, attributes.get(key))); 
		} 

		context.modifyAttributes((String)mapping.get("name") + '=' + user.getName(), DirContext.REPLACE_ATTRIBUTE, attrs);		
	}

	public void removeUser(UserConcept user) 
	throws Exception
	{
		context.destroySubcontext((String)mapping.get("name") + '=' + user.getName());
	}

	public UserConcept getUser(String login) 
	throws Exception
	{
		String userKey = (String)mapping.get("name") + '=' + login;
		
		Attributes attrs = context.getAttributes(userKey);
		
		String name = (String)attrs.get((String)mapping.get("name")).get();
		String password = new String((byte[])attrs.get((String)mapping.get("password")).get());
		String startupPlugin = (String)attrs.get((String)mapping.get("startupPlugin")).get();
		String locked = (String)attrs.get((String)mapping.get("locked")).get();
		UserConcept user = new UserConcept(name, password, (locked.equals("1")), startupPlugin);
		
		try {
			String privateKey = (String)attrs.get((String)mapping.get("privateKey")).get();
			String publicKey = (String)attrs.get((String)mapping.get("publicKey")).get();
			user.setKeys(publicKey, privateKey);
		} catch(Exception e) {
			//no key info
		}
		
		try {
			String description = (String)attrs.get((String)mapping.get("description")).get();
			user.setDescription(description);
		} catch(Exception e) {
			//no description
		}

		return user;
	}

	public Iterator getAllUsers() 
	throws Exception
	{
		ArrayList users = new ArrayList();
		
		NamingEnumeration list = context.list("");		
		while(list.hasMore())
		{
			NameClassPair pair = (NameClassPair)list.next();
			String login = pair.getName();
			login = login.substring(login.indexOf('=')+1);
			
			users.add(getUser(login));
		}
		
		return users.iterator();
	}
}