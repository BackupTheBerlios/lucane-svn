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
package org.lucane.applications.jmailadmin;

import org.lucane.common.*;
import org.lucane.common.crypto.*;
import org.lucane.server.*;
import org.lucane.server.database.*;
import org.lucane.server.store.Store;

import java.sql.*;
import java.util.*;

public class JMailAdminService
extends Service
{
  private DatabaseAbstractionLayer layer = null;
  private Store store = null;

  public JMailAdminService()
  {
  }

  public void init(Server parent)
  {
	  layer = parent.getDBLayer();
	  store = parent.getStore();
  }

  public void process(ObjectConnection oc, Message message)
  {  	
	JMailAdminAction jma = (JMailAdminAction)message.getData();
	String user;
	Account account;

	try {
		switch(jma.action)
		{
		case JMailAdminAction.GET_ACCOUNT:
			user = (String)jma.param;
			account = this.getAccount(user);
			oc.write("OK");
			oc.write(account);
			break;

		case JMailAdminAction.STORE_ACCOUNT:
			account = (Account)jma.param;
			this.storeAccount(account);
			oc.write("OK");
			break;
			
		case JMailAdminAction.GET_USERS:
			ArrayList users = this.getAllUsers();
			oc.write("OK");
			oc.write(users);
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
		user = rs.getString(1);
		String address = rs.getString(2);
		String type = rs.getString(3);
		String inHost = rs.getString(4);
		int inPort = rs.getInt(5);
		String outHost = rs.getString(6);
		int outPort = rs.getInt(7);
		String login = rs.getString(8);
		String password = rs.getString(9);
		password = BlowFish.decipher(login, password);
		a = new Account(user, address, type, inHost, inPort, outHost, outPort, login, password);
	}
	
	rs.close();
	stmt.close();
	connex.close();
	
	if(a == null)
		a = new Account(user);
	
	return a;
  }
  
  private void storeAccount(Account a)
  throws Exception
  {  	  	
  	Connection connex = layer.openConnection();  	  	
  	Statement stmt = connex.createStatement();
  	
  	try {
  		stmt.execute("DELETE FROM JMailAccounts WHERE userName = "+ "'" + a.user + "'");
  	} catch(Exception e) {
  		//no such account yet
  	}
  	
	stmt.execute("INSERT INTO JMailAccounts VALUES("
			+ "'" + a.user + "', "
			+ "'" + a.address + "', "
			+ "'" + a.type + "', "
			+ "'" + a.inHost + "', "
			+ "'" + a.inPort + "', "
			+ "'" + a.outHost + "', "
			+ "'" + a.outPort + "', "
			+ "'" + a.login + "', "
			+ "'" + BlowFish.cipher(a.login, a.password) + "')"
		);
	
	stmt.close();
	connex.close();
  }
  
  private ArrayList getAllUsers() 
  {
  	ArrayList users = new ArrayList();

  	try {
  		Iterator i = store.getUserStore().getAllUsers();
  		while(i.hasNext())
  			users.add(i.next());
  	} catch(Exception e) {
  		e.printStackTrace();
  	}
  	
  	return users;
  }  
}

