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
import javax.sound.sampled.*;

import org.xiph.speex.SpeexDecoder;

/**
 * Audio player that plays speex streams
 */
public class AudioPlayer implements Runnable
{
	//-- sound system
	private SourceDataLine dataLine;
	private AudioFormat targetFormat;
	
	private AudioConfig audioConfig;
	private InputStream source;

	/**
	 * Constructor.
	 * 
	 * @param config the stream configuration
	 * @param source any input stream containing speex data
	 */
	public AudioPlayer(AudioConfig config, InputStream source)
	{
		this.audioConfig = config;
		this.source = source;
		this.targetFormat = config.createAudioFormat(AudioFormat.Encoding.PCM_SIGNED);
		initDataLine();
	}

	/**
	 * Initialize the sound system
	 */
	private void initDataLine()
	{
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat, AudioSystem.NOT_SPECIFIED);
		try	{
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(targetFormat, dataLine.getBufferSize());
		} catch(LineUnavailableException e) {
			//TODO handle this error
			e.printStackTrace();
		}
	}

	/**
	 * Stop the player
	 */
	public void stop()
	{
		dataLine.flush();
		dataLine.stop();
		dataLine.close();
	}

	/**
	 * Run the player (used in a thread)
	 */
	public void run()
	{
		System.out.println("player started");
		dataLine.start();

		int read = 1;

		byte[] speex = new byte[audioConfig.getSpeexBufferSize()];
		byte[] pcm = new byte[audioConfig.getPcmBufferSize()];
		SpeexDecoder decoder = audioConfig.createDecoder();
		
		while (read != -1)
		{
			try	{
				read = source.read(speex, 0, speex.length);
				System.out.println("read buffer : " + speex.length);
				decoder.processData(speex, 0, read);				
				
				int length = decoder.getProcessedDataByteSize();
				decoder.getProcessedData(pcm, 0);
				System.out.println("decoded buffer : " + length);
				
				if(length >= 0)
					dataLine.write(pcm, 0, length);
								
			} catch (Exception e) {
				//TODO differentiate STOP from other errors
				e.printStackTrace();
				break;
			}
			
		}
	}

	/**
	 * Simple test method
	 */
	public static void main(String[] args) throws Exception
	{
		File src = new File("test.spx");
		AudioConfig config = new AudioConfig(AudioConfig.NARROWBAND, 3);
		AudioPlayer ap = new AudioPlayer(config, new FileInputStream(src));
		new Thread(ap).start();
		System.out.println("playing...");
	}
}
