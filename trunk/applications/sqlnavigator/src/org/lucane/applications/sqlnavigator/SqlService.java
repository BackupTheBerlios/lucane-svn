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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.sqlnavigator;

import java.sql.*;
import java.util.*;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.*;


public class SqlService
extends Service
{
	private DatabaseAbstractionLayer layer;
	
	public SqlService()
	{
	}
	
	public void init(Server parent)
	{
		this.layer = parent.getDBLayer();
	}
	
	
	public void process(ObjectConnection oc, Message message)
	{
		SqlAction sa = (SqlAction)message.getData();
		switch(sa.action)
		{
			case SqlAction.GET_DRIVER_INFO:
				getDriverInfo(oc);
			break;
			case SqlAction.GET_TABLE_NAMES:
				getTableNames(oc);
			break;
			case SqlAction.EXECUTE_QUERY:
				executeQuery(oc, sa.params);
			break;
		}
	}
	
	
	
	private void executeQuery(ObjectConnection oc, Object object) 
	{
		try
		{
			SqlResult result = new SqlResult();
			
			Connection c = layer.getConnection();
			Statement stm = c.createStatement();
			ResultSet rs = stm.executeQuery((String)object);
			
			//-- column names
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();		
			for(int i=0;i<cols;i++)
				result.addColumn(meta.getColumnLabel(i+1));
			
			//-- data
			while(rs.next())
			{
				Vector line = new Vector();
				
				for(int i=0;i<cols;i++)
					line.add(rs.getString(i+1));
				
				result.addLine(line);
			}
			
			oc.write(result);
			c.close();
		}	
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	private void getTableNames(ObjectConnection oc)
	{
		Vector data = new Vector();
		
		try
		{
	  		Connection connection = layer.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			String[] types = {"TABLE", "VIEW"};
			ResultSet rs = dbmd.getTables(null,null,null,types);
			
			//field n�3 is the table name
			while(rs.next())
				data.addElement(rs.getString(3));
			
			rs.close();
			connection.close();
			oc.write(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void getDriverInfo(ObjectConnection oc) 
	{
		try
		{
			Connection connection = layer.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			String info = dbmd.getDriverName() + " " + dbmd.getDriverVersion();
			connection.close();
			
			oc.write(info);			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
