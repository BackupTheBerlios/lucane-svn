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

import org.lucane.common.*;
import org.lucane.common.crypto.*;
import org.lucane.server.*;
import org.lucane.server.database.*;

import java.sql.*;

public class JMailService
extends Service
{
  DatabaseAbstractionLayer layer = null;

  public JMailService()
  {
  }

  public void init(Server parent)
  {
  	layer = parent.getDBLayer();	
  }

  public void install()
  {
  	try {
  		String dbDescription = getDirectory()	+ "db-jmail.xml";
  		layer.getTableCreator().createFromXml(dbDescription);
  	} catch (Exception e) {
  		Logging.getLogger().severe("Unable to install JMailService !");
  		e.printStackTrace();
  	}  
  }

  public void process(ObjectConnection oc, Message message)
  {  	
	JMailAction jma = (JMailAction)message.getData();
	String user = message.getSender().getName();
	Account account;

	try {
		switch(jma.action)
		{
		case JMailAction.GET_ACCOUNT:
			account = this.getAccount(user);
			oc.write("OK");
			oc.write(account);
			break;	
		}
	} catch(Exception e) {
		try {
			oc.write("FAILED " + e);
		} catch(Exception e2) {}			
		e.printStackTrace();
	}	
  }

  
  private Account getAccount(String user)
  throws Exception
  {
  	Account a = null;
  	
  	Connection connex = layer.openConnection();  	
	Statement stmt = connex.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM JMailAccounts WHERE userName='" + user + "'");
	if(rs.next())
	{
		String address = rs.getString(2);
		String type = rs.getString(3);
		String inHost = rs.getString(4);
		int inPort = rs.getInt(5);
		String outHost = rs.getString(6);
		int outPort = rs.getInt(7);
		String login = rs.getString(8);
		String password = rs.getString(9);
		password = BlowFish.decipher(login, password);
		a = new Account(address, type, inHost, inPort, outHost, outPort, login, password);
	}
	
	rs.close();
	stmt.close();
	connex.close();
	
	return a;
  }
  
  
}
