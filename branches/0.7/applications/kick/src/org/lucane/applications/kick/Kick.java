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
package org.lucane.applications.kick;

import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.*;

public class Kick
  extends Plugin
{
	private ConnectInfo[] friends;

  public Kick()
  {
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    if(friends.length > 0)
      return new Kick(friends, starter);
    else
      return new Kick(null, starter);
  }

  public Kick(ConnectInfo[] friends, boolean starter)
  {
    this.friends = friends;
    this.starter = starter;
  }

  public void start()
  {
    // selection check
    if(this.friends == null)
    {
      DialogBox.info(tr("msg.selectUsers"));
      exit();
      return;
    }
    
    //ask for confirmation
    String msg = tr("msg.reallyKickUsers");
 	msg = msg.replaceFirst("\\%1", String.valueOf(friends.length));
    if(!DialogBox.question(getTitle(), msg))
    {
    	exit();
    	return;
    }
    
    //send deconnection
    for(int i=0;i<friends.length;i++)
    {
		try {
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(friends[i], "Client", "DISCONNECT");
			oc.close();
		} catch (Exception e) {
			Logging.getLogger().warning("Unable to kick : " + friends[i].getName());
			e.printStackTrace();
		}
	}
	
	exit();
  }
}
