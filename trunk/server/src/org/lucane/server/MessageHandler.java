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

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import org.lucane.common.ConnectInfo;
import org.lucane.common.Logging;
import org.lucane.common.Message;
import org.lucane.common.ObjectConnection;
import org.lucane.common.concepts.ServiceConcept;
import org.lucane.common.concepts.UserConcept;
import org.lucane.server.store.Store;

public class MessageHandler extends Thread
{
	private Socket socket;
	
	
	/**
	 * Reads messages from the network.
	 * A message is either a command for the server
	 * or for an internal Service.
	 * 
	 * @param sock the Socket
	 */
	public MessageHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	public void run()
	{
		int i;
		boolean alreadyConnected;
		boolean isAuthentication;
		
		Message message;
		byte[] signature;
		
		String cmd;
		String cmdData;
		StringTokenizer stk;
		
		ObjectConnection oc = null;
		
		try
		{
			/* streams initialization */
			oc = new ObjectConnection(socket);
			message = (Message)oc.read();
			signature = (byte[])oc.read();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Logging.getLogger().warning("#Err > Unable to read message.");
			return;
		}
		
		// check wether a user is known or not
		alreadyConnected = ConnectInfoManager.getInstance().isConnected(message.getSender());
		
		//check if command is authentication
		isAuthentication = message.getApplication().equals("Server") 
		&& ((String)message.getData()).startsWith("AUTH");
		
		//signature check
		if (alreadyConnected && !isAuthentication)
		{
			boolean sigok = false;
			try
			{
				ConnectInfo ci = message.getSender();
				if (ci.verifier == null)
					ci = ConnectInfoManager.getInstance().getCompleteConnectInfo(ci);
				sigok = ci.verifier.verify(message, signature);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (!sigok)
			{
				try
				{
					oc.write("FAILED bad signature");
				}
				catch (Exception e)
				{
				}
				Logging.getLogger().warning("#Err > bad signature: " + message.getSender());
				return;
			}
		}
		
		if (message.getApplication().equals("Server"))
		{
			cmd = null;
			
			try
			{
				stk = new StringTokenizer((String)message.getData());
				cmd = stk.nextToken();
				cmdData = stk.nextToken("\0").substring(1);
			}
			catch (Exception ex)
			{
				if (cmd == null)
					cmd = "";
				
				cmdData = "";
			}
			
			/* if the user asks for authentication, we try to do it and exits this method */
			if (cmd.equals("AUTH"))
			{
				try {
					oc.write("OK");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Server.getInstance().getAuthenticator().authenticate(oc, message, cmdData);
			}
			else if (!alreadyConnected)
			{
				Logging.getLogger().info("Access denied to " + message.getSender());
				try
				{
					oc.write("FAILED No Connection");
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				internalCommand(oc, message, cmd, cmdData);
			}
		}
		else if (!alreadyConnected)
		{
			Logging.getLogger().info("Access denied to " + message.getSender());
			try
			{
				oc.write("FAILED No Connection");
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			Store store = Server.getInstance().getStore();
			Service s = ServiceManager.getInstance().getService(message.getApplication());
			boolean serviceFound = false;
			if(s != null)
			{
				serviceFound = true;
				UserConcept user = null;
				ServiceConcept service = null;
				try
				{
					user =
						store.getUserStore().getUser(
								message.getSender().getName());
					service =
						store.getServiceStore().getService(
								message.getReceiver().getName());
				}
				catch (Exception e)
				{
				}
				
				
				
				/* tests serviceManager for permissions */
				boolean isAutorizedService = false;
				try {
					isAutorizedService = store.getServiceStore()
					.isAuthorizedService(user, service);
				} catch(Exception e) {}
				
				if (!isAutorizedService)
				{
					Logging.getLogger().info(
							"#Err > "
							+ message.getSender()
							+ " : Service denied to "
							+ message.getReceiver().getName());
					try
					{
						oc.write(
						"FAILED You don't have acces to this service");
					}
					catch (Exception e)
					{
					}
				}
				else
				{
					try
					{
						oc.write("OK");
					}
					catch (Exception e)
					{
					}
					serviceFound = true;
					s.process(oc, message);
				}
			}
			
			if (!serviceFound)
			{
				try
				{
					oc.write("FAILED unknown");
				}
				catch (Exception e)
				{
				}
				Logging.getLogger().warning(
						"#Err > Service "
						+ message.getReceiver().getName()
						+ " unknown");
			}
		}
		
		oc.close();
		try {
			socket.close();
		} catch (IOException e) {
			Logging.getLogger().warning("#Err > Socket::close()");
		}
	}
	
	/**
	 * Handle internal commands
	 */
	private void internalCommand(
			ObjectConnection oc,
			Message message,
			String command,
			String data)
	{
		if (command.equals("CONNECT_DEL"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			ConnectInfoManager.getInstance().removeConnectInfo(message.getSender());
		}
		else if (command.equals("CONNECT_GET"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			Server.getInstance().sendConnectInfo(data, oc);
		}
		else if (command.equals("CONNECT_LIST"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			Server.getInstance().sendUserList(oc);
		}
		else if (command.equals("PLUGIN_LIST"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			Server.getInstance().sendPluginList(oc, message.getSender().getName());
		}
		else if (command.equals("PLUGIN_GET"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			Server.getInstance().sendPluginFile(oc, data);
		}
		else if (command.equals("STARTUP_PLUGINS"))
		{
			try
			{
				oc.write("OK");
			}
			catch (Exception e)
			{
			}
			Server.getInstance().sendStartupPlugin(oc, message.getSender().getName());
		}
		else
		{
			try
			{
				oc.write("FAILED Unknown command");
			}
			catch (Exception e)
			{
			}
		}
	}
}