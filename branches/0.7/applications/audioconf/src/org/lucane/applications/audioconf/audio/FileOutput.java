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
package org.lucane.applications.audioconf.audio;

import java.io.*;

/**
 * A simple test for AudioRecorder and friends
 */
public class FileOutput implements AudioRecorderListener
{
	//-- attributes
	private String filename;
	private FileOutputStream output;
	
	/**
	 * Constructor
	 * 
	 * @param filename the file to write
	 */
	public FileOutput(String filename)
	{
		this.filename = filename;
	}
	
	/**
	 * Recording started, init output stream
	 */
	public void audioRecordingStarted(AudioConfig config) 
	{
		try {
			this.output = new FileOutputStream(this.filename);
			System.out.println("recording 10 seconds...");			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Audio recorded, write to file
	 */
	public void audioRecorded(byte[] data, int length) 	
	{
		try {
			System.out.println("recorded : " +length);
			this.output.write(data, 0, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recording ended, close everything
	 */
	public void audioRecordingEnded() 
	{
		try {
			this.output.close();
			System.out.println("ended.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main method
	 */
	public static void main(String[] args)
	throws Exception
	{
		AudioConfig config = new AudioConfig(AudioConfig.NARROWBAND, 3);
		FileOutput fo = new FileOutput("test.spx");
		AudioRecorder ar = new AudioRecorder(config);
		ar.addAudioListener(fo);
		
		Thread t = new Thread(ar);
		
		t.start();
		Thread.sleep(10*1000);
		ar.stop();
		
		System.out.println(t.isAlive());
	}
}