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

import java.io.IOException;

import org.lucane.applications.audioconf.audio.*;
import org.lucane.applications.audioconf.gui.ConfigDialog;
import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.*;

public class AudioConf extends Plugin
{
	private ConnectInfo friend;
	private ObjectConnection connection;
	
	public AudioConf()
	{
		//empty constructor for pluginloader
	}
	
	private AudioConf(ConnectInfo friend, boolean starter)
	{
		this.friend = friend;
		this.starter = starter;
	}
	

	public Plugin init(ConnectInfo[] friends, boolean starter) {
		return new AudioConf(friends.length > 0 ? friends[0] : null, starter);
	}
	
	public void load(ObjectConnection oc, ConnectInfo who, String data)
	{
		this.connection = oc;
		this.friend = who;
	}

	public void start()
	{
		if(friend == null)
		{
			DialogBox.info(tr("err.noUserSelected"));
			exit();
			return;
		}
		
		ConfigDialog cd = new ConfigDialog(this);
		cd.show();
	}

	public void follow()
	{
		System.out.println("DEBUG: receiving audio stream from " +friend.getName());
		
		try {		
			AudioConfig config = (AudioConfig)this.connection.read();
			System.out.println("read config : " +config);
			
			boolean accepted = true;//DialogBox.question(getTitle(), "accept audioconf ?");
			this.connection.write(Boolean.valueOf(accepted));
			
			AudioPlayer player = new AudioPlayer(config, new AudioConfInputStream(this.connection));
			Thread thread = new Thread(player);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void askForAccept(AudioConfig config)
	{
		System.out.println("ask for accept");
		this.connection = Communicator.getInstance().sendMessageTo(this.friend, this.getName(), "");
		try {
			System.out.println("sending " +config);
			this.connection.write(config);
			System.out.println("sent " +config);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		try {
			System.out.println("waiting for accept");
			Boolean accept = (Boolean)this.connection.read();
			if(accept.booleanValue())
			{
				System.out.println("accepted, recording starts");
				this.startRecording(config);
			}
			else
			{
				System.out.println("rejected.");
			}
		} catch (Exception e) {
			System.out.println("error in acceptation");
			e.printStackTrace();
		}		
	}
	
	private void startRecording(AudioConfig config)
	{
		AudioRecorder recorder = new AudioRecorder(config);
		recorder.addAudioListener(new Streamer(this.connection));
		Thread thread = new Thread(recorder);
		thread.start();
	}
}