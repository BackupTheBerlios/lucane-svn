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
package org.lucane.applications.jmailaccount;

import java.awt.Dimension;

import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.ConnectInfo;
import org.lucane.common.ObjectConnection;


public class JMailAccountPlugin
  extends StandalonePlugin
{
	private ConnectInfo service;

	public JMailAccountPlugin()
	{
		this.starter = true;
	}
	
	public Plugin newInstance(ConnectInfo[] friends, boolean starter) 
	{
		return new JMailAccountPlugin();
	}

	public void start() 
	{
		this.service = Communicator.getInstance().getConnectInfo("org.lucane.applications.jmailaccount");
		AccountFrame frame = new AccountFrame(this);
		frame.setAccount(getAccount());
		frame.setPreferredSize(new Dimension(250, 300));
		frame.setIconImage(this.getImageIcon().getImage());
		frame.show();
	}
	
	public boolean storeAccount(Account a)
	{
		boolean result = true;
		
		JMailAccountAction jma = new JMailAccountAction(JMailAccountAction.STORE_ACCOUNT, a);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), jma);

		try {		
			if(oc.readString().startsWith("OK"))
				DialogBox.info(tr("msg.accountStored"));
			else
			{
				DialogBox.error(tr("err.storeAccount"));
				result = false;
			}
		} catch(Exception e) {
			result = false;
			DialogBox.error(tr("err.storeAccount"));			
			e.printStackTrace();
		}
		oc.close();
		
		return result;
	}
	
	public Account getAccount()
	{
		Account a = null;
		JMailAccountAction jma = new JMailAccountAction(JMailAccountAction.GET_ACCOUNT);
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(
				service, service.getName(), jma);
		
		try {
			if(oc.readString().startsWith("OK"))
				a = (Account)oc.read();
		} catch(Exception e) {
			//probably no account yet
		}
		
		oc.close();
		return a;
	}
}
