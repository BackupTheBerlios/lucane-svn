/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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
 
package org.lucane.common;

import java.io.Serializable;

import org.lucane.common.signature.Signable;

/**
 * A message that can go accross the network
 */
public class Message implements Serializable, Signable
{
	private ConnectInfo sender;
	private ConnectInfo receiver;
	private String application;
	private Object data;
	
	/**
	 * Constructor
	 * 
	 * @param sender the sender connect info
	 * @param receiver the receiver connect info
	 * @param application the application that should get this message
	 * @param data some parameters
	 */
	public Message(ConnectInfo sender, ConnectInfo receiver, String application, Object data)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.application = application;
		this.data = data;	
	}
	
	//-- getters
	
	/**
	 * Get the message sender
	 * 
	 * @return the sender
	 */
	public ConnectInfo getSender()
	{
		return sender;
	}
	
	/**
	 * Get the message receiver
	 * 
	 * @return the receiver
	 */
	public ConnectInfo getReceiver()
	{
		return receiver;
	}
	
	/**
	 * Get the application name
	 * 
	 * @return the application name
	 */
	public String getApplication()
	{
		return application;
	}
	
	/**
	 * Get message data
	 * 
	 * @return the data
	 */
	public Object getData()
	{
		return data;
	}	
	
	/**
	 * Shows the message as a string
	 * 
	 * @return a description
	 */
	public String toString()
	{
		return "[" + sender.getName() + ">" + receiver.getName()+ 
			 ":" + application + ":" + data + "]";
	}
	
	/**
	 * Get a signable string
	 */
	public String toSignableString()
	{
		String msg = null;
		if(data instanceof Signable)
			msg = ((Signable)data).toSignableString();
		else 
			msg = data.toString();
			
		return sender.getName() + receiver.getName() + 
			 application + msg;
	}
}
