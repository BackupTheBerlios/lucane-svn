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

package org.lucane.applications.reminder;

import org.lucane.client.*;
import org.lucane.common.*;

public class ReminderPlugin extends Plugin
{
	private ReminderInfos infos;
	
	//-- from Plugin
	public ReminderPlugin()
	{
		this.starter = false;
		this.infos = null;
	}
		
	public Plugin init(ConnectInfo[] friends, boolean starter)
	{
		return new ReminderPlugin();
	}
	
	public void load(ObjectConnection oc, ConnectInfo who, String data) 
  	{
		try {
			this.infos = (ReminderInfos)oc.read();
			oc.close();
		} catch(Exception e) {
			Logging.getLogger().severe("Unable to read reminder infos !");
			e.printStackTrace();
		}
  	}

	public void follow()
	{
		ReminderFrame frame = new ReminderFrame(this, infos);
		frame.addWindowListener(this);
		frame.show();
	}
}