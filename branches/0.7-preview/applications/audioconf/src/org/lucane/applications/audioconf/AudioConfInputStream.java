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
package org.lucane.applications.audioconf;

import java.io.*;

import org.lucane.common.ObjectConnection;

public class AudioConfInputStream extends InputStream
{
	private ObjectConnection connection;
	private byte[] buffer;
	private int index;
	
	public AudioConfInputStream(ObjectConnection connection)
	{
		this.connection = connection;
		this.buffer = new byte[0];
		this.index = 0;
	}
	
	public int read() throws IOException 
	{
		if(index >= buffer.length)
			readNextBuffer();
			
		return buffer[index++];
	}
	
	public int read(byte[] array, int offset, int length)
	throws IOException
	{
		int bytes = 0;
		
		while(offset < array.length && bytes < length)
		{
			if(index >= buffer.length)
				readNextBuffer();
			
			array[offset++] = buffer[index++];
			bytes++;
		}		
		
		return bytes;
	}
	
	public int available()
	{
		return this.buffer.length - index;
	}
	
	public void close()
	throws IOException
	{
		this.connection.close();
		super.close();
	}
	
	
	private synchronized void readNextBuffer()
	throws IOException
	{
		this.index = 0;
		try	{
			this.buffer = (byte[])connection.read();
		} catch (ClassNotFoundException e)	{
			e.printStackTrace();
		}
	}
}
