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
import org.lucane.common.signature.SignatureException;
import org.lucane.server.store.Store;

/**
 * Reads messages from the network.
 * A message is either a command for the server
 * or for an internal Service.
 */
public class MessageHandler extends Thread
{
	private Socket socket;
	
	private ObjectConnection connection;
	private Message message;
	private byte[] signature;
	
	boolean isAlreadyConnected;
	boolean isAuthenticationMessage;
	
	/**
	 * Constructor
	 * 
	 * @param socket the client socket
	 */
	public MessageHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	/**
	 * The main method
	 */
	public void run()
	{
		//init handler
		try {
			init();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Logging.getLogger().warning("#Err > Unable to read message.");
			closeConnection();
			return;
		}		
		
		//check signature if needed
		if(isAlreadyConnected && !isAuthenticationMessage)
		{
			if (!hasValidSignature())
			{
				try	{
					connection.write("FAILED bad signature");
				} catch (Exception e) {}
				
				Logging.getLogger().warning("#Err > bad signature: " + message.getSender());
				closeConnection();
				return;
			}
		}
		
		//check if the user is connected 
		if(!isAlreadyConnected && !isAuthenticationMessage)
		{
			Logging.getLogger().info("Access denied to " + message.getSender());
			try	{
				connection.write("FAILED No Connection");
			} catch (Exception e) {}
		}
		
		//the message is for the server
		else if (message.getApplication().equals("Server"))
			handleServerMessage();
		
		//the message is for a service
		else
			handleServiceMessage();
		
		closeConnection();
	}
	
	/**
	 * Init attributes
	 */
	private void init() 
	throws IOException, ClassNotFoundException
	{
		connection = new ObjectConnection(socket);
		message = (Message)connection.read();
		signature = (byte[])connection.read();
		
		isAlreadyConnected = ConnectInfoManager.getInstance().isConnected(message.getSender());
		isAuthenticationMessage = message.getApplication().equals("Server")	
			&& ((String)message.getData()).startsWith("AUTH");		
	}
	
	/**
	 * Check the signature
	 * 
	 * @return true if the signature is valid
	 */
	private boolean hasValidSignature()
	{
		ConnectInfo info = message.getSender();
		if (info.verifier == null)
			info = ConnectInfoManager.getInstance().getCompleteConnectInfo(info);
		
		try {
			return info.verifier.verify(message, signature);
		} catch (SignatureException e) {
			Logging.getLogger().warning("Error while verifying signature : " + e);
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Handle a server message
	 */
	private void handleServerMessage()
	{
		String command = "";
		String parameters = "";
		
		//separate command and parameters
		try
		{
			StringTokenizer stk = new StringTokenizer((String)message.getData());
			command = stk.nextToken();
			parameters = stk.nextToken("\0").substring(1);
		}
		catch (Exception ex)
		{
			//unable to parse
		}
		
		// if the user asks for authentication, we try to do it and exits this method
		if(isAuthenticationMessage)
		{
			sendAck();
			Server.getInstance().getAuthenticator().authenticate(connection, message, parameters);
			return;
		}
		
		//all checks are ok, execute the command
		executeCommand(command, parameters);
	}
	
	/**
	 * Handle a service message
	 */
	private void handleServiceMessage()
	{
		String userName = message.getSender().getName();
		String serviceName = message.getApplication();
		
		//get the service
		Store store = Server.getInstance().getStore();
		Service s = ServiceManager.getInstance().getService(serviceName);
		
		//ensure that the service is running
		if(s == null)
		{
			try	{
				connection.write("FAILED not running");
			} catch (Exception e) {}
			Logging.getLogger().warning("Service "+ serviceName + " not running");
			return;
		}
		
		
		//check for permission
		boolean isAuthorizedService = false;
		try {
			UserConcept user =	store.getUserStore().getUser(userName);
			ServiceConcept service = store.getServiceStore().getService(serviceName);
			isAuthorizedService = store.getServiceStore().isAuthorizedService(user, service);
		} catch(Exception e) {
			Logging.getLogger().warning("Error while checking service permission");
			e.printStackTrace();
		}
		
		if(!isAuthorizedService)
		{
			Logging.getLogger().warning(serviceName + " : Service denied to " + userName);
			try	{
				connection.write("FAILED You don't have acces to this service");
			} catch (Exception e) {}
			return;
		}
		
		//process the message
		sendAck();
		s.process(connection, message);
	}
	
	/**
	 * Execute server commands
	 */
	private void executeCommand(String command,	String parameters)
	{
		if (command.equals("CONNECT_DEL"))
		{
			sendAck();
			ConnectInfoManager.getInstance().removeConnectInfo(message.getSender());
		}
		
		else if (command.equals("CONNECT_GET"))
		{
			sendAck();
			Server.getInstance().sendConnectInfo(parameters, connection);
		}
		
		else if (command.equals("CONNECT_LIST"))
		{
			sendAck();
			Server.getInstance().sendUserList(connection);
		}
		
		else if (command.equals("PLUGIN_LIST"))
		{
			sendAck();
			Server.getInstance().sendPluginList(connection, message.getSender().getName());
		}
		
		else if (command.equals("PLUGIN_GET"))
		{
			sendAck();
			Server.getInstance().sendPluginFile(connection, parameters);
		}
		
		else if (command.equals("STARTUP_PLUGINS"))
		{
			sendAck();
			Server.getInstance().sendStartupPlugin(connection, message.getSender().getName());
		}
		
		else
		{
			try	{
				connection.write("FAILED Unknown command");
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Send "OK" to the client
	 */
	private void sendAck()
	{
		try	{
			connection.write("OK");
		} catch (Exception e) {}
	}

	/**
	 * Close the connection & socket
	 */
	private void closeConnection()
	{
		try {
			this.connection.close();
			this.socket.close();
		} catch (IOException e) {}
	}
}