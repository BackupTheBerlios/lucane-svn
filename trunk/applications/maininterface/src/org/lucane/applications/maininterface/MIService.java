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
package org.lucane.applications.maininterface;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.*;
import java.sql.*;
import java.util.StringTokenizer;


public class MIService
  extends Service
{
  private DatabaseAbstractionLayer layer = null;
  private Connection conn = null;
  private Statement st = null;
  private ResultSet res = null;

  /**
   * Creates a new MIService object.
   */
  public MIService()
  {
  }


  public void process(ObjectConnection oc, Message message)
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
      catch(Exception ex)
      {
        data = "";
      }

      if(command.equals("GET_USE"))
        this.getUse(from, data, oc);
      else if(command.equals("INC_USE"))
        this.incUse(from, data);
    }
  }

  public void init(Server parent)
  {
    try {
      layer = parent.getDBLayer();
      conn = layer.openConnection();
    } catch(SQLException e) {
		Logging.getLogger().warning("Unable to open database connection: " + e);
    }
  }

  public void install()
  {
  	try {
  		String dbDescription = getDirectory()	+ "db-maininterface.xml";
  		layer.getTableCreator().createFromXml(dbDescription);
  	} catch (Exception e) {
  		Logging.getLogger().severe("Unable to install MIService !");
  		e.printStackTrace();
  	}    	
  }

  private void getUse(String who, String data, ObjectConnection oc)
  {
    String resu = "0";
    data = data.substring(1);

    try
    {
      st = conn.createStatement();
      res = st.executeQuery(
                  "SELECT nbUse FROM MIUse WHERE login='" + who + 
                  "' AND plugin='" + data + "'");

      if(res.next())
        resu = res.getString(1);
    }
    catch(SQLException ex)
    {
      //normal if first use
    }

    try {
        oc.write(resu);
    } catch(Exception e) {}
  }

  private void incUse(String who, String data)
  {
    data = data.substring(1);

    try
    {
      st = conn.createStatement();
      res = st.executeQuery(
                  "SELECT nbUse FROM MIUse WHERE login='" + who + "' AND plugin='" + data + "'");

      if(! res.next())
      {
        st.executeUpdate(
              "INSERT INTO MIUse VALUES ('" + who + "', '" + data + "', 0)");
      }
    }
    catch(SQLException e)
    {
		Logging.getLogger().warning("Unable to set the use to 0 : " + e);
    }

    try
    {
      st.executeUpdate(
            "UPDATE MIUse SET nbUse=nbUse+1 WHERE login='" + who + 
            "' AND plugin='" + data + "'");
    }
    catch(SQLException e)
    {
		Logging.getLogger().warning("Unable to update use : " + e);
    }
  }
}
