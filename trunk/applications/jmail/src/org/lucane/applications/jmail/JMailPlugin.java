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
package org.lucane.applications.jmail;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;
import org.lucane.common.ObjectConnection;
import org.lucane.applications.jmail.base.*;


public class JMailPlugin
  extends Plugin
{
	private ConnectInfo service;
	private Profile profile;
	
	private static final String ACCOUNT_APP = "org.lucane.applications.jmailaccount";

	public JMailPlugin()
	{
		this.starter = true;
	}
	
	public Plugin init(ConnectInfo[] friends, boolean starter) 
	{
		return new JMailPlugin();
	}

	public void start() 
	{
		this.service = Communicator.getInstance().getConnectInfo("org.lucane.applications.jmail");
		try {
			this.profile = new Profile(getAccount());
			SplashFrame splash = new SplashFrame(this);
			splash.show();
			
			JMailWindow jmail = new JMailWindow(this);
			jmail.setExtendedState(JMailWindow.MAXIMIZED_BOTH);

			splash.dispose();
		} catch(Exception e) {
			if(PluginLoader.getInstance().hasPlugin(ACCOUNT_APP))
			{	
				if(DialogBox.question("JMail", tr("msg.accountCreation")))
					PluginLoader.getInstance().run(ACCOUNT_APP, new ConnectInfo[0]);
			}
			else
				DialogBox.error(tr("err.getAccount"));							
		}
		
	}
	
	public Account getAccount()
	throws Exception
	{
		Account a = null;
		JMailAction jma = new JMailAction(JMailAction.GET_ACCOUNT);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), jma);
		
		if(oc.readString().startsWith("OK"))
			a = (Account)oc.read();
		else
			DialogBox.error(tr("err.getAccount"));
		
		oc.close();
		return a;
	}
	
	public ResourceBundle getBundle()
	{
		return this.bundle;
	}
	
	public Profile getProfile()
	{
		return this.profile;
	}

	public ImageIcon getIcon(String icon)
	{
		try {
			return new ImageIcon(new URL(getDirectory() + "pics/" +icon));
		} catch(Exception e) {		
			return new ImageIcon();
		}
	} 
}
