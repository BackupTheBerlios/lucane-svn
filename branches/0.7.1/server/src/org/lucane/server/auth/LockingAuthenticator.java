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

import java.util.HashMap;

import org.lucane.common.*;
import org.lucane.common.concepts.UserConcept;
import org.lucane.server.ConnectInfoManager;
import org.lucane.server.Server;
import org.lucane.server.store.Store;

public class LockingAuthenticator extends Authenticator
{
	private static final int MAX_FAILURES = 3;
	private HashMap loginFailures;
	
	public LockingAuthenticator()
	{
		this.loginFailures = new HashMap();
	}
	
	public AuthResponse authenticate(AuthRequest request) 
	{
		Store store = Server.getInstance().getStore();
		ConnectInfo userInfo = request.getUserInfo();
		
		//get the user concept	
		UserConcept user = null;		
		try {
			user = store.getUserStore().getUser(userInfo.getName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(user == null)
			return new AuthResponse(AuthResponse.BAD_CREDENTIALS);
		
		if(user.isLocked())
			return new AuthResponse(AuthResponse.USER_LOCKED);
		
		if(!store.getUserStore().checkUserPassword(user, request.getMd5Passwd()))
		{
			logonError(user);
			return new AuthResponse(AuthResponse.BAD_CREDENTIALS);
		}
		
		//reset previous failures
		loginFailures.remove(user);		
		
		//disconnect already connected user
		if(ConnectInfoManager.getInstance().isConnected(userInfo))
		{
			ConnectInfo oldUser = ConnectInfoManager.getInstance().
				getCompleteConnectInfo(request.getUserInfo());
			
			try {
				ObjectConnection oc = Server.getInstance().sendMessageTo(oldUser, "Client", "DISCONNECT");
				oc.close();
			} catch (Exception e) {
				//we can't do much here, the client might have crashed
			}
			ConnectInfoManager.getInstance().removeConnectInfo(oldUser);
		}
		
		//add the connect info
		userInfo.setPublicKey(user.getPublicKey());
		ConnectInfoManager.getInstance().addConnectInfo(userInfo);
		
		return new AuthResponse(AuthResponse.AUTH_ACCEPTED, user.getPrivateKey());
	}

	/**
	 * A login error occured
	 * 
	 * @param user the user concept
	 */
	private void logonError(UserConcept user)
	{
		Integer numberOfFailures = (Integer)loginFailures.get(user.getName());
		int newNumber = 1;
		if(numberOfFailures != null)
			newNumber = numberOfFailures.intValue() + 1;
		loginFailures.put(user.getName(), new Integer(newNumber));
		
		Logging.getLogger().fine("Login failure: " + user.getName() + " (" + newNumber + " failures).");
		
		if(newNumber >= MAX_FAILURES)
		{
			user.setLocked(true);
			try	{
				Server.getInstance().getStore().getUserStore().updateUser(user);
				loginFailures.remove(user.getName());
				Logging.getLogger().info("Locking " + user.getName() + " after " + MAX_FAILURES + " failures.");
			} catch (Exception e) {
				Logging.getLogger().warning("Unable to lock user : " + e);
			}
		}
	}
}