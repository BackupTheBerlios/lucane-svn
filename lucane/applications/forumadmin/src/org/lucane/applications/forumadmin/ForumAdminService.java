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
package org.lucane.applications.forumadmin;

import org.lucane.common.*;
import org.lucane.server.*;
import java.sql.*;
import java.util.*;


public class ForumAdminService
  extends Service
{

  private Connection conn = null;
  private Statement st = null;
  private ResultSet res = null;

  public ForumAdminService()
  {
  }

  public void process(ObjectConnection sc, Message message)
  {
	String line = (String)message.getData();

    if(line != null)
    {
      StringTokenizer stk = new StringTokenizer(line);
      String command = stk.nextToken();
      String data = null;

      try
      {
        data = stk.nextToken("\0").substring(1);
      }
      catch(Exception ex)
      {
        data = "";
      }

      /*
       * known commands :
       * LIST
       * DELETE <forum>
       * CREATE <forum>
       */
      if(command.equals("LIST"))
        this.list(sc);
      else if(command.equals("DELETE"))
        this.delete(data, sc);
      else if(command.equals("CREATE"))
        this.create(data, sc);
    }
  }


  public void init(Server parent)
  {
    try
    {
      conn = parent.getDBLayer().openConnection();
      st = conn.createStatement();
    }
    catch(Exception e)
    {
		Logging.getLogger().warning("ForumAdminService::error during initialization : " + e);
    }
  }

  public void install()
  {
  }

  private void list(ObjectConnection sc)
  {
    try
    {
    	Vector v = new Vector();
      res = st.executeQuery("SELECT name FROM forum");

      while(res.next())
        v.addElement(res.getString(1));

      res.close();
      sc.write(v);
    }
    catch(Exception e)
    {
		Logging.getLogger().warning("Error in ForumService::list() : " + e);
    }
  }

  private void delete(String forum, ObjectConnection sc)
  {
    try
    {
      st.execute("DELETE FROM forum WHERE name='" + forum + "'");
      sc.write("OK");
    }
    catch(Exception e)
    {
      try {
      	sc.write("FAILED");
      } catch(Exception e2) {}
	  Logging.getLogger().warning("Error in ForumService::delete() : " + e);
    }
  }

  private void create(String forum, ObjectConnection sc)
  {

    try
    {
      st.execute("INSERT INTO forum VALUES('" + forum + "')");
      sc.write("OK");
    }
    catch(Exception e)
    {
      try {
          sc.write("FAILED");
      } catch(Exception e2) {}
	  Logging.getLogger().warning("Error in ForumService::create() : " + e);
    }
  }
}
