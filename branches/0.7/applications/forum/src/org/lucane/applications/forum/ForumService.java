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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.forum;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.*;
import java.sql.*;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;


public class ForumService
  extends Service
{
  private DatabaseAbstractionLayer layer = null;
  private Connection conn = null;
  private PreparedStatement st = null;
  private ResultSet res = null;

  /**
   * Creates a new ForumService object.
   */
  public ForumService()
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
      * known commands
      * LIST
      * GET_FORUM <forum>
      * GET_MESSAGE <id message>
      * POST <id ref> <author> <title>\001<content>
      */
      if(command.equals("LIST"))
        this.list(sc);
      else if(command.equals("GET_FORUM"))
        this.getForum(data, sc);
      else if(command.equals("GET_MESSAGE"))
        this.getMessage(data, sc);
      else if(command.equals("POST"))
        this.post(data);
    }
  }

  public void init(Server parent)
  {
    try {
      layer = parent.getDBLayer();
      conn = layer.openConnection();
    } catch(SQLException e) {
		Logging.getLogger().warning("unable to open connection: " + e);
    }
  }

  public void install()
  {
  		try {
  			String dbDescription = getDirectory()	+ "db-forum.xml";
  			layer.getTableCreator().createFromXml(dbDescription);
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to install ForumService !");
			e.printStackTrace();
		}  
  }

  private void list(ObjectConnection sc)
  {
    try
    {
		Vector v = new Vector();
      st = conn.prepareStatement("SELECT name FROM forum");
      res = st.executeQuery();

      while(res.next())
	     v.addElement(res.getString(1));
	   
      res.close();
      st.close();
      sc.write(v);
    }
    catch(Exception e)
    {
		Logging.getLogger().info("Error : " + e);
    }
  }

  /**
   * GET_FORUM nom 
   * => id idref author date title
   */
  private void getForum(String data, ObjectConnection sc)
  {
    try
    {
    	Vector v = new Vector();
    	st = conn.prepareStatement("SELECT id, idref, author, datum, title " + 
    			"FROM forumMessage WHERE forum=?" +
				" ORDER BY idref desc, id");
    	st.setString(1, data);
      res = st.executeQuery();

      while(res.next())
      {
        v.addElement(res.getString(1) + " " + res.getString(2) + " " + res.getString(3) + 
              " " + res.getString(4) + " " + res.getString(5));
      }
	sc.write(v);
      res.close();
      st.close();
    }
    catch(Exception e)
    {
		Logging.getLogger().warning("Error in ForumService::getForum : " + e);
    }
  }

  /**
   * GET_MESSAGE id 
   * => content with \n replace by \001
   */
  private void getMessage(String data, ObjectConnection sc)
  {
    try
    {
    	st = conn.prepareStatement("SELECT content FROM forumMessage WHERE id=?");
    	st.setString(1, data);
      res = st.executeQuery();
      res.next();

      String tosend = res.getString(1).replace('\n', '\001');
      sc.write(tosend);
      res.close();
      st.close();
    }
    catch(Exception e)
    {
		Logging.getLogger().warning("Error in ForumService::getMessage : " + e);
    }
  }

  /**
   * POST idref author forum\001title\001content
   */
  private void post(String data)
  {
    StringTokenizer stk = new StringTokenizer(data);
    String idref = stk.nextToken();
    String author = stk.nextToken();
    String reste = stk.nextToken("\0").substring(1);
    stk = new StringTokenizer(reste, "\001");

    String forum = stk.nextToken();
    String title = stk.nextToken();
    String content = stk.nextToken("\0").substring(1).replace('\001', '\n');

    //date calculation
    Calendar now = Calendar.getInstance();
    String date = "" + now.get(Calendar.DAY_OF_MONTH);
    date += "/" + now.get(Calendar.MONTH)+1;
    date += "/" + now.get(Calendar.YEAR);

    String id = null;

    //fetch next id
    synchronized(st)
    {
      try
      {
      	st = conn.prepareStatement("SELECT max(id)+1 FROM forumMessage");
        res = st.executeQuery();
        res.next();
        id = res.getString(1);
        res.close();
        st.close();

        if(id == null || id.equals("null"))
          id = "1";

        st = conn.prepareStatement("INSERT INTO forumMessage VALUES (?, ?, ?, ?, ?, ?, ?)");
        st.setString(1, id);
        st.setString(2, idref);
        st.setString(3, forum);
        st.setString(4, author);
        st.setString(5, title);
        st.setString(6, date);
        st.setString(7, content);
        st.execute();
        st.close();
      }
      catch(Exception e)
      {
		Logging.getLogger().warning("Error in ForumService::post : " + e);
      }
    }
  }
}
