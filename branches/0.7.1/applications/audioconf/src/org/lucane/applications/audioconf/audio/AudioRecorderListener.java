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

/**
 * A listener that can be notified of the recording status
 */
public interface AudioRecorderListener
{
	/**
	 * Recording started
	 * 
	 * @param config the configuration of the audio produced
	 */
	public void audioRecordingStarted(AudioConfig config);
	
	/**
	 * A chunk of audio is available for processing.
	 * This method will be called a lot of times.
	 * 
	 * @param data the audio recorder
	 */
	public void audioRecorded(byte[] data, int length);
	
	/**
	 * Recording ended
	 */
	public void audioRecordingEnded();
}