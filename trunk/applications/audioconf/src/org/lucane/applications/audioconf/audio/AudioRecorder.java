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

import java.util.*;
import java.io.*;

import javax.sound.sampled.*;
import org.xiph.speex.spi.*;

/**
 * Audio recorder that encodes directly in Speex
 */
public class AudioRecorder implements Runnable
{
	//-- attributes
	private AudioInputStream audioStream;
	private TargetDataLine dataLine;
	private AudioFormat audioFormat;
	private AudioConfig audioConfig;
	
	private ArrayList listeners;
	
	/**
	 * Constructor
	 * 
	 * @param config the audio configuration to use to encode
	 */
	public AudioRecorder(AudioConfig config)
	{
		this.listeners = new ArrayList();
		
		this.audioConfig = config;
		this.audioFormat = config.createAudioFormat(AudioFormat.Encoding.PCM_SIGNED);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		try {
			dataLine = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}

		audioStream = new Pcm2SpeexAudioInputStream(new AudioInputStream(dataLine), audioFormat,
				AudioSystem.NOT_SPECIFIED);
	}
	
	/**
	 * Add a listener that will be notified of recording status
	 * 
	 * @param listener the listener to add
	 */
	public void addAudioListener(AudioRecorderListener listener)
	{
		this.listeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeAudioListener(AudioRecorderListener listener)
	{
		this.listeners.remove(listener);
	}

	/**
	 * Stop recording
	 */
	public void stop()
	{		
		try {
			dataLine.stop();
			dataLine.close();
			audioStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//notify listeners
		Iterator listeners = this.listeners.iterator();
		while(listeners.hasNext())
			((AudioRecorderListener)listeners.next()).audioRecordingEnded();
	}
	
	/**
	 * Run recording as a thread
	 */
	public void run()
	{
		try {
			dataLine.open();
			dataLine.start();
		} catch(LineUnavailableException lue) {
			lue.printStackTrace();
		}
		
		//notify listeners
		Iterator listeners = this.listeners.iterator();
		while(listeners.hasNext())
			((AudioRecorderListener)listeners.next()).audioRecordingStarted(audioConfig);
		
		
		//do the real recording
		try	{
			byte[] buffer = new byte[1024];
			while(dataLine.isOpen())
			{	
				int length = audioStream.read(buffer);
				
				listeners = this.listeners.iterator();
				while(listeners.hasNext())
					((AudioRecorderListener)listeners.next()).audioRecorded(buffer, length);				
			}
		} catch (IOException e)	{
			//TODO hande this error
			e.printStackTrace();
		}
	}
}