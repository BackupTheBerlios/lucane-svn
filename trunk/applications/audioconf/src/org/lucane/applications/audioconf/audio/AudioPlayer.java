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
import org.xiph.speex.spi.*;

/**
 * Audio player that plays speex streams
 */
public class AudioPlayer implements Runnable
{
	//-- audio formats
	private AudioFormat sourceFormat;
	private AudioFormat targetFormat;
	private AudioFileFormat fileFormat;

	//-- sound system
	private AudioInputStream audioInputStream;
	private SourceDataLine dataLine;

	/**
	 * Constructor.
	 * 
	 * @param config the stream configuration
	 * @param source any input stream containing speex data
	 */
	public AudioPlayer(AudioConfig config, InputStream source)
	{
		initAudioFormats(config);
		initAudioInputStream(new BufferedInputStream(source));
		initDataLine();
	}

	/**
	 * Initialize audio formats
	 *  
	 * @param config the audio configuration
	 */	
	private void initAudioFormats(AudioConfig config)
	{
		this.sourceFormat = config.createAudioFormat(SpeexEncoding.SPEEX);
		this.targetFormat = config.createAudioFormat(AudioFormat.Encoding.PCM_SIGNED);

		this.fileFormat = new AudioFileFormat(SpeexFileFormatType.SPEEX, sourceFormat, AudioSystem.NOT_SPECIFIED);
	}

	/**
	 * Initialize audio input stream
	 * 
	 * @param source the stream to play
	 */
	private void initAudioInputStream(BufferedInputStream source)
	{
		audioInputStream = new AudioInputStream(source, sourceFormat, fileFormat.getFrameLength());
		audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
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

		try	{
			audioInputStream.close();
		} catch(IOException ioe) {
			//TODO handle this error
			ioe.printStackTrace();
		}
	}

	/**
	 * Run the player (used in a thread)
	 */
	public void run()
	{
		dataLine.start();

		int read = 1;
		byte[] buffer = new byte[1024];

		while (read != -1)
		{
			try	{
				read = audioInputStream.read(buffer, 0, buffer.length);
			} catch (Exception e) {
				//TODO differentiate STOP from other errors
				e.printStackTrace();
				break;
			}
			
			if(read >= 0)
				dataLine.write(buffer, 0, read);
		}
	}

	/**
	 * Simple test method
	 */
	public static void main(String[] args) throws Exception
	{
		File src = new File("test.spx");
		AudioConfig config = new AudioConfig(AudioConfig.NARROWBAND);
		AudioPlayer ap = new AudioPlayer(config, new FileInputStream(src));
		new Thread(ap).start();
		Thread.sleep(5000);
		ap.stop();
	}
}
