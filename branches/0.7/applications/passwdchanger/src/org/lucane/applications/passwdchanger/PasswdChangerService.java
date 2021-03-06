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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.passwdchanger;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.common.concepts.UserConcept;
import org.lucane.server.store.UserStore;

import java.util.StringTokenizer;

public class PasswdChangerService
  extends Service
{
  Server parent;

  public PasswdChangerService()
  {
  }

  public void process(ObjectConnection sc, Message message)
  {
	String line = (String)message.getData();
	String from = message.getSender().getName();
	
	if(line != null)
    {
      StringTokenizer stk = new StringTokenizer(line);
      String command = stk.nextToken();
      String data = null;

      try
      {
        data = stk.nextToken("\0");
      }
      catch(Exception e)
      {
        data = "";
      }

      if(command.equals("CHANGE_PASSWD"))
        this.changePasswd(from, data, sc);
      else if(command.equals("GET_KEY"))
        this.getKey(from, sc);
    }
  }

  public void init(Server parent)
  {
    this.parent = parent;
  }

  private void getKey(String who, ObjectConnection sc)
  {
    UserStore um = parent.getStore().getUserStore();
    try {
    	UserConcept user = um.getUser(who);
    	String key = user.getPrivateKey();
        sc.write(key);
    } catch(Exception e) {
    	e.printStackTrace();
    }
  }

  private void changePasswd(String who, String data, ObjectConnection sc)
  {
    UserStore um = parent.getStore().getUserStore();
    UserConcept user = null;
    try {
    	user = um.getUser(who);
    } catch(Exception e) {}
    
    StringTokenizer stk = new StringTokenizer(data);
    String pold = stk.nextToken();
    String pnew = stk.nextToken();
    String key = stk.nextToken();
    String pass = user.getPassword();

    if(! pass.equals(pold))
    {
    	try {
    	      sc.write("BAD_PASSWD");
    	} catch(Exception e) {}
      return;
    }
    
    user.setPassword(pnew);
    user.setKeys(user.getPublicKey(),key);
    try {
    	um.updateUser(user);
        sc.write("PASSWD_CHANGED");
    } catch(Exception e) {
    	Logging.getLogger().warning("error : " + e);
    }
  }
}