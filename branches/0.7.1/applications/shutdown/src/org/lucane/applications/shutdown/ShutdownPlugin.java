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
package org.lucane.applications.shutdown;

import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.*;

public class ShutdownPlugin
extends StandalonePlugin
{
  public ShutdownPlugin()
  {
  	this.starter = true;
  }
  
  public Plugin newInstance(ConnectInfo[] friends, boolean starter)
  {
      return new ShutdownPlugin();
  }


  public void start()
  {
  	boolean isStartupPlugin = Client.getInstance().getStartupPlugin().equals(getName());
  	
	//ask for confirmation if used from a normal user
  	if(!isStartupPlugin)
  	{
	    String msg = tr("msg.reallyShutdown");
	    if(!DialogBox.question(getTitle(), msg))
	    {
	    	exit();
	    	return;
	    }
	}
  	   
    //really shutdown
	try {
		ConnectInfo service = Communicator.getInstance().getConnectInfo(getName());
		ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), "");
		oc.close();
	} catch (Exception e) {
		Logging.getLogger().warning("Unable to send shutdown");
		e.printStackTrace();
	}

	exit();
  }
}
