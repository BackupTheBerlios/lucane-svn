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

import java.util.StringTokenizer;

import org.lucane.common.*;
import org.lucane.common.concepts.UserConcept;
import org.lucane.server.store.Store;

public class Authenticator
{
	/**
	 * Authenticate users
	 * 
	 * @param oc the connection
	 * @param message the message
	 * @param data the authentication credentials
	 */
	public void authenticate(ObjectConnection oc, Message message, String data)
	{
		Store store = Server.getInstance().getStore();
		String passwd = null;
		String hostname = null;
		int port = 0;
		
		//TODO create an AuthenticationAction
		//parse the credendials
		StringTokenizer stk = new StringTokenizer(data, " ");
		try
		{
			passwd = stk.nextToken();
			hostname = stk.nextToken();
			port = Integer.parseInt(stk.nextToken());
		}
		catch (Exception ex)
		{
			Logging.getLogger().warning("#Err > Incorrect authentication message.");
			try	{
				oc.write("BAD_MESSAGE");
			} catch (Exception e) {}
			
			return;
		}
		
		//get the user
		String name = message.getSender().getName();				
		UserConcept user = null;		
		try {
			user = store.getUserStore().getUser(message.getSender().getName());
		} catch(Exception e) {
			e.printStackTrace();
		}

		
		//check the password
		if(user != null && store.getUserStore().checkUserPassword(user,  passwd))
		{
			//disconnect already connected user
			if(ConnectInfoManager.getInstance().isConnected(message.getSender()))
			{
				ConnectInfo oldUser = ConnectInfoManager.getInstance().getCompleteConnectInfo(message.getSender());
				try {
					ObjectConnection myoc = Server.getInstance().sendMessageTo(oldUser, "Client", "DISCONNECT");
					myoc.close();
				} catch (Exception e) {
					//we can't do much here, the client might have crashed
				}
				ConnectInfoManager.getInstance().removeConnectInfo(oldUser);
			}
			
			//add the connect info
			String authenticationServer = message.getSender().getAuthenticationServer();
			ConnectInfoManager.getInstance().addConnectInfo(new ConnectInfo(name, authenticationServer, hostname,
					port, user.getPublicKey(), "Client"));
			try	{
				oc.write("AUTH_ACCEPTED " + user.getPrivateKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//send the user list to everyone
			Server.getInstance().sendUserList();
		}
		
		//wrong password or user
		else
		{
			try {
				oc.write("NOT_VALID_USER");
			} catch (Exception e) {}
		}
	}
}