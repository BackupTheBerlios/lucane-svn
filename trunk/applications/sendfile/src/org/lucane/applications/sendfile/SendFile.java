/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.sendfile;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.io.*;

import javax.swing.*;


public class SendFile
extends Plugin
{
	private ObjectConnection connection;
	private ConnectInfo friend;
	private String fileName;
	
	private SendFileDialog dialog;
	
	public SendFile()
	{
	}
	
	
	public Plugin newInstance(ConnectInfo[] friends, boolean starter)
	{
		Logging.getLogger().finer("SendFile:init() starter=" + starter);
		if(friends.length > 0)
			return new SendFile(friends[0], starter);
		else
			return new SendFile(null, starter);
	}
	
	public SendFile(ConnectInfo friend, boolean starter)
	{
		this.friend = friend;
		this.starter = starter;
	}
	
	public void load(ObjectConnection friend, ConnectInfo who, String filename)
	{
		this.connection = friend;
		this.friend = who;
		this.fileName = filename;
	}
	
	public void start()
	{
		
		// selection check
		if(this.friend == null)
		{
			DialogBox.info(tr("noselect"));
			exit();
			return;
		}
		
		dialog = new SendFileDialog(this, getTitle() + "> " + friend.getName(), true);
		dialog.setExitPluginOnClose(true);
		selectFile(dialog);
		dialog.show();		
	}
	
	public void follow()
	{
		dialog = new SendFileDialog(this, getTitle() + " < " + friend.getName(), false);
		 dialog.setPreferredSize(new Dimension(400, 300));		 
		 dialog.setFilePath(fileName);
		 
		 try {
			String comment = connection.readString();
			dialog.setComment(comment); 
		} catch (Exception e) {
			dialog.setComment(tr("err.comment"));
		}
		 
		 dialog.show();		 
	}
		
	public void selectFile(SendFileDialog dialog)
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
			dialog.setFilePath(fc.getSelectedFile().getPath());
	}
	
	public void askForAccept(String filePath, String comment)
	{
		dialog.dispose();
		
		filePath = filePath.replace('\\', '/');
		int index = filePath.lastIndexOf('/');
		if(index > 0)
			this.fileName = filePath.substring(index+1);
		else 
			this.fileName = filePath;
		
		try {
			connection = Communicator.getInstance().sendMessageTo(friend, getName(), fileName);
			connection.write(comment);
			
			Boolean acceptation = Boolean.FALSE;
			
			try	{
				acceptation = (Boolean)connection.read();
			} catch(Exception e) {
				//file refused
			}
			
			if(acceptation.booleanValue())
				sendFile(filePath);
			else
			{
				String reject = tr("msg.rejectFile");
				reject = reject.replaceAll("%1", friend.getName());
				reject = reject.replaceAll("%2", fileName);
				DialogBox.error(reject);
			}
			
			connection.close();
		}
		catch(Exception e)
		{
			DialogBox.error(tr("sendError"));
			e.printStackTrace();			
		}
	}
	
	public void acceptFile()
	{	
		JFileChooser fc = new JFileChooser();
		
		int returnVal = fc.showSaveDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			
			try {
				connection.write(Boolean.TRUE);
			} catch (IOException e) {
				e.printStackTrace();
			}

			File file = fc.getSelectedFile();			
			downloadFile(file.getPath());
		}
		else
			rejectFile();
		
	}
	
	
	
	public void rejectFile()
	{
		try {
			connection.write(Boolean.FALSE);
		} catch (IOException e) {
			//oops
		}
		Logging.getLogger().finer("SendFile::REJECT");
		connection.close();
		dialog.dispose();
		exit();
	}
	
	public void sendFile(String filePath)
	throws Exception
	{
		DataInputStream dis = null;
		try { 
			dis = new DataInputStream(new FileInputStream(filePath));
			byte[] buf = new byte[dis.available()];
			dis.readFully(buf);
			connection.write(buf);
			connection.close();
		} finally {
			if(dis != null)
				dis.close();
		}
		
		String sent = tr("msg.sent").replaceAll("%1", fileName);
		DialogBox.info(sent);
		exit();		
	}	
	
	public void downloadFile(String destination)
	{
		DataOutputStream dos = null;
		try
		{
			connection.write(Boolean.TRUE);
			Logging.getLogger().finer("SendFile::ACCEPT");
			dialog.dispose();
			
			dos = new DataOutputStream(new FileOutputStream(destination));
			byte[] buf = (byte[])connection.read();
			dos.write(buf);	
			
			connection.close();
			
			String get = tr("msg.get").replaceAll("%1", fileName);
			DialogBox.info(get);
			exit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			DialogBox.error(tr("getError"));
		}
		finally {
			if(dos != null) {
				try {
					dos.close();
				} catch(IOException ioe) {}
			}
		}
	}
}
