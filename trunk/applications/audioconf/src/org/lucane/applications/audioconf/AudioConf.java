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

import org.lucane.applications.audioconf.audio.*;
import org.lucane.applications.audioconf.gui.*;
import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.*;

public class AudioConf extends Plugin
{
	private ConnectInfo friend;
	private ObjectConnection connection;
	
	private AudioRecorder recorder;
	private AudioPlayer player;
	
	private Controller controller;
	
	private boolean stopped = false;
	
	public AudioConf()
	{
		//empty constructor for pluginloader
	}
	
	private AudioConf(ConnectInfo friend, boolean starter)
	{
		this.friend = friend;
		this.starter = starter;
	}
	

	public Plugin newInstance(ConnectInfo[] friends, boolean starter) {
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
		Logging.getLogger().info("receiving audio stream from " +friend.getName());
		
		try {
			AudioConfig config = (AudioConfig)this.connection.read();
			
			String msg = tr("msg.acceptFrom");
			msg = msg.replaceAll("%1", getFriendName());
			
			boolean accept = DialogBox.question(getTitle(), msg);
			this.connection.write(Boolean.valueOf(accept));
			
			if(accept)
				startAll(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void waitForAccept(AudioConfig config)
	{
		this.connection = Communicator.getInstance().sendMessageTo(this.friend, this.getName(), "");
		try	{
			this.connection.write(config);
			Boolean accepted = (Boolean)this.connection.read();
			if(accepted.booleanValue())
				startAll(config);
			else
			{
				String msg = tr("msg.rejectedBy");
				msg = msg.replaceAll("%1", getFriendName());				
				DialogBox.info(msg);
			}
		} catch (Exception e) {
			DialogBox.info(tr("err.accept"));
			e.printStackTrace();
		}		
	}
	
	public void startRecorder(AudioConfig config)
	{
		recorder = new AudioRecorder(config);
		recorder.addAudioListener(new Streamer(this, connection));
		Thread thread = new Thread(recorder);
		thread.start();		
	}
	
	public void startPlayer(AudioConfig config)
	{
		player = new AudioPlayer(this, config, new AudioConfInputStream(this, connection));
		Thread thread = new Thread(player);
		thread.start();
	}
	
	//-- error handling
	
	public void reportRecorderError(Exception e)
	{
		stopAndExit();
	}
	
	public void reportPlayerError(Exception e)
	{
		stopAndExit();
	}
	
	//-- for controller
	
	public void startAll(AudioConfig config)
	{
		startPlayer(config);
		startRecorder(config);
		controller = new Controller(this);
		controller.showController();
	}
	
	public String getFriendName()
	{
		return friend.getName();
	}
	
	public void stopAndExit()
	{
		//avoid infinite loop
		if(this.stopped)
			return;		
		this.stopped = true;
			
		Logging.getLogger().info("Stopping AudioConf");
		
		recorder.stop();
		player.stop();
		controller.hideController();
		exit();
	}
}