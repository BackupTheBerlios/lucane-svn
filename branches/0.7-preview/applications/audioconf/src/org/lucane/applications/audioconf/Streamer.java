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

import org.lucane.applications.audioconf.audio.*;
import org.lucane.common.*;

public class Streamer implements AudioRecorderListener
{
	ObjectConnection connection;
	
	public Streamer(ObjectConnection oc)
	{
		this.connection = oc;
	}
	
	/**
	 * Recording started, init output stream
	 */
	public void audioRecordingStarted(AudioConfig config) 
	{
		System.out.println("Ready to record");	
		try {
			this.connection.write(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Audio recorded, write to file
	 */
	public void audioRecorded(byte[] data, int length) 	
	{
		try {
			byte[] buffer = new byte[length];
			System.arraycopy(data, 0, buffer, 0, length);
			
			this.connection.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recording ended, close everything
	 */
	public void audioRecordingEnded() 
	{
		this.connection.close();
		System.out.println("ended.");
	}
}