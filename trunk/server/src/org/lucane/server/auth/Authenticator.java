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

package org.lucane.server.auth;

import java.util.StringTokenizer;

import org.lucane.common.Logging;
import org.lucane.common.Message;
import org.lucane.common.ObjectConnection;
import org.lucane.server.Server;
import org.lucane.server.ServerConfig;

public abstract class Authenticator
{
	public static Authenticator getInstance(ServerConfig config) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Logging.getLogger().info("Authenticator used : " + config.getAuthenticatorClass());
		
		Class authClass = Class.forName(config.getAuthenticatorClass());
		return (Authenticator)authClass.newInstance();
	}
	
	public abstract AuthResponse authenticate(AuthRequest request);
	
	private boolean loginDisabled = false;
	
	public void disableLogin()
	{
		this.loginDisabled = true;
		Logging.getLogger().info("Login disabled");
	}
	
	// for compatibility with the current implementation
	public void authenticate(ObjectConnection oc, Message message, String data)
	{
		// check is login is still allowed
		if(loginDisabled)
		{
			try {
				oc.write("ACCESS_DENIED");
			} catch(Exception e) {}
			return;
		}
		
		String passwd = "";
		StringTokenizer stk = new StringTokenizer(data, " ");
		try	{
			passwd = stk.nextToken();
		} catch (Exception ex) {}
		
		//call the new api
		AuthRequest request = new AuthRequest(message.getSender(), passwd);
		AuthResponse response = authenticate(request);
		
		//accepted
		if(response.getValue() == AuthResponse.AUTH_ACCEPTED)
		{
			try	{
				oc.write("AUTH_ACCEPTED " + response.getPrivateKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//send the user list to everyone
			Server.getInstance().sendUserListToEveryone();
		}
		
		//bad user or password
		else if(response.getValue() == AuthResponse.BAD_CREDENTIALS)
		{
			try {
				oc.write("NOT_VALID_USER");
			} catch(Exception e) {}			
		}
		
		//login disabled
		else
		{
			try {
				oc.write("ACCESS_DENIED");
			} catch(Exception e) {}
		}
		
	}
}