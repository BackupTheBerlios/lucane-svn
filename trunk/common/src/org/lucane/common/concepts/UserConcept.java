/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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
 
package org.lucane.common.concepts;

import org.lucane.common.signature.*;

//links : user <-> group
public class UserConcept extends Concept
{
	private String passwd; //md5 hash
	private boolean locked;
	private String startupPlugin;
	
	private String publicKey;
	private String privateKey;
	
	public UserConcept(String name, String passwd, boolean locked, String startupPlugin)
	{
		super(name, "");
		this.passwd = passwd;
		this.locked = locked;
		this.startupPlugin = startupPlugin;
		
		this.publicKey = "";
		this.privateKey = "";
	}
	
	public void setKeys(String publicKey, String privateKey)
	{
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public void setPassword(String password)
	{
		this.passwd = password;
	}

	public String getPassword()
	{
		return this.passwd;
	}

	public boolean isLocked()
	{
		return this.locked;
	}

	public String getStartupPlugin()
	{
		return this.startupPlugin;
	}

	public String getPublicKey()
	{
		return this.publicKey;
	}

	public String getPrivateKey()
	{
		return this.privateKey;
	}

    public void generateKeys(String passwd)
    throws Exception
    {
        String[] keys = KeyGenerator.generateKeyPair();
        String privateKey = Signer.cypher(keys[0], passwd);
        String publicKey = keys[1];
        
        setKeys(publicKey, privateKey);
    }
    
	//--
    
	public boolean equals(Object o)
	{
		if(o instanceof UserConcept)
			return this.name.equals(((UserConcept)o).name);			

		return false;
	}
}