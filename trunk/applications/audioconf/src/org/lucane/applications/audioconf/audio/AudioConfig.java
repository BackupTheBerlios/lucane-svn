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

import javax.sound.sampled.*;

/**
 * Configuration for audio stream
 */
public class AudioConfig
{
	//-- speex frame rates
	public static final int NARROWBAND = 8000;
	public static final int WIDEBAND = 16000;
	public static final int ULTRA_WIDEBAND = 32000;
	
	//-- attributes
	private int frameRate;
	private int channels;
	
	/**
	 * Constructor
	 * 
	 * @param frameRate the frame rate to use
	 */
	public AudioConfig(int frameRate)
	{
		this.frameRate = frameRate;
		this.channels = 2;
	}
	
	/**
	 * Return the frame rate
	 * 
	 * @return the frame rate
	 */
	public int getFrameRate()
	{
		return this.frameRate;
	}
	
	/**
	 * Return the number of channels
	 * 
	 * @return the number of channels
	 */
	public int getChannels()
	{
		return this.channels;
	}
	
	/**
	 * Factory to create AudioFormat with this configuration
	 * 
	 * @param type the audio encoding (PCM or SPEEX)
	 * @return the audio format
	 */
	public AudioFormat createAudioFormat(AudioFormat.Encoding type)
	{
		return new AudioFormat(
						type,
						this.getFrameRate(), 
						16, 
						this.getChannels(), 
						this.getChannels()*2, 
						this.getFrameRate(), 
						false);
	}
}