/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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

package org.lucane.applications.sqlnavigator;

import java.util.*;

import javax.swing.*;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

public class SqlPlugin extends Plugin
{
	private ConnectInfo service;	
	public Navigator navigator;

	public SqlPlugin()
	{
		this.starter = true;
	}
	
	
	public Plugin init(ConnectInfo[] friends, boolean starter)
	{
		return new SqlPlugin();
	}

	public void start()
	{
		this.service =Communicator.getInstance().getConnectInfo("org.lucane.applications.sqlnavigator"); 
		this.navigator = new Navigator(this);
		this.navigator.setIconImage(this.getImageIcon().getImage());
		this.navigator.show();
		this.navigator.write(getDriverInfo());
		this.getTableNames();
	}
	
	public void getTableNames()
	{	
		try {
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(
					service, service.getName(), new SqlAction(SqlAction.GET_TABLE_NAMES));
			Vector v= (Vector) oc.read();
			
			navigator.tables.setListData(v);
			
			oc.close();			
		} catch(Exception e) {
			DialogBox.error(""+e);
		}		
	}
	
	public String getDriverInfo()
	{
		String info = tr("error.driver");
		
		try {
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(
					service, service.getName(), new SqlAction(SqlAction.GET_DRIVER_INFO));
			info = oc.readString();
			oc.close();			
		} catch(Exception e) {
			DialogBox.error(""+e);
		}
		
		return info;
	}
	
	public SqlResult executeQuery(String query)
	{
		SqlResult sr = null;
		
		try {
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(
			service, service.getName(), new SqlAction(SqlAction.EXECUTE_QUERY, query));
			sr = (SqlResult) oc.read();				
			oc.close();			
		} catch(Exception e) {
			DialogBox.error(""+e);
		}		
		
		return sr;
	}
	
	public JScrollPane getScrollPane(SqlResult sr)
	{       
		if(sr == null)
			return null;
        
		JTable jt = new JTable(sr.data, sr.columns);
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		return new JScrollPane(jt);
	}	
}