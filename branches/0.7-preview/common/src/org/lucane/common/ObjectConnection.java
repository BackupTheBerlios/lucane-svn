/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002-2004  Vincent Fiack <vfiack@mail15.com>
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

import java.io.*;
import java.net.*;

/**
 * An object connection
 */
public class ObjectConnection 
{
	//-- attributes
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
    private ObjectListeningThread listener;
	
	/**
	 * Constructor
	 * 
	 * @param socket the underlying socket
	 */
	public ObjectConnection(Socket socket)
	{
        this.listener = new ObjectListeningThread(this);
		this.socket = socket;
		try {
			this.output = new ObjectOutputStream(socket.getOutputStream());
			this.input = new LucaneObjectInputStream(socket.getInputStream());
		} catch(Exception e) {}
	}
	
	/**
	 * Add an ObjectListener to this connection
	 * 
	 * @param ol the listener
	 */
    public void addObjectListener(ObjectListener ol)
    {
      this.listener.addObjectListener(ol);
    }

	/**
	 * Start the listening thread
	 */
    public void listen()
    {
      this.listener.start();
    }
        
    /**
     * Write an object
	 *
     * @param o the object to write
     */
	public void write(Object o)
	throws IOException
	{
		this.output.writeObject(o);
	}
		
	/**
	 * Check if we have something to read
	 * 
	 * @return true if we can read
	 */
	public boolean readyToRead()
	{
		try {
			return this.socket.getInputStream().available() > 0;
		} catch(Exception e) {e.printStackTrace();}
		
		return false;
	}
	
	/**
	 * Read an object
	 * 
	 * @return the object read
	 */
	public Object read()
	throws IOException, ClassNotFoundException
	{
		return this.input.readObject();
	}
	
	/**
	 * Read a string
	 * 
	 * @return the string read
	 */
	public String readString()
	throws IOException, ClassNotFoundException
	{
		return (String)this.read();
	}
	
	/**
	 * Close this connection
	 */
	public void close()
	{
        this.listener.close();

		try {
			output.close();
			input.close();
			socket.close();
		} catch(Exception e) {}
	}
}
