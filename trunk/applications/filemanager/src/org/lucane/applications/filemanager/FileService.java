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
package org.lucane.applications.filemanager;

import org.lucane.common.*;
import org.lucane.server.*;
import java.io.*;
import java.util.*;


public class FileService
  extends Service
{

  private static String DIRECTORY = "STORAGE";

  /**
   * Creates a new FileService object.
   */
  public FileService()
  {
  	if (Server.lucanePath!=null)
  		DIRECTORY = Server.lucanePath+DIRECTORY;  
  }

  /**
   * Process a request
   * 
   * @param from who asked the service
   * @param the line read by the server
   * @param is the input stream
   * @param os the output stream
   */
  public void process(ObjectConnection sc, Message message)
  {
	String line = (String)message.getData();
	String from = message.getSender().getRepresentation();

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

      //SET_FILE dir/ector/y file
      //binary data
      //->OK orFAILED
      if(command.equals("SET_FILE"))
        this.setFile(from, data,sc);

      //GET_FILE dir/ector/y file
      //->binary data
      else if(command.equals("GET_FILE"))
        this.getFile(from, data, sc);

      //MKDIR dir/ector/y
      //->OK orFAILED
      else if(command.equals("MKDIR"))
        this.mkdir(from, data, sc);

      //LIST_DIR dir/ector/y
      //->file file2 ...
      else if(command.equals("LIST_DIR"))
        this.listDir(from, data, sc);
    }
  }


  public void init(Server parent)
  {
  }

  public void install()
  {
    (new File(DIRECTORY)).mkdirs();
  }


  private void setFile(String from, String data, ObjectConnection sc)
  {
    StringTokenizer stk = new StringTokenizer(data);
    String path = stk.nextToken();
    String filename = stk.nextToken();
	Logging.getLogger().finer("FileService::setFile()");
    (new File(DIRECTORY + path)).mkdirs();

	DataOutputStream dos = null;
    try {
      dos = new DataOutputStream(new FileOutputStream(
                 DIRECTORY + '/' + path + "/" + filename));
      byte[] buf = (byte[])sc.read();
      dos.write(buf);
	  Logging.getLogger().finer("FileService::setFile() OK");
      sc.write("OK");
    } catch(Exception e) {
      try {
            sc.write("FAILED " + e);
      } catch(Exception e2) {}
	  Logging.getLogger().warning("Unable to write file: " + e);
    } finally {
    	try {
    		if(dos != null)
				dos.close();
    	} catch(IOException ioe) {}
    }
	
  }

  private void getFile(String from, String data, ObjectConnection sc)
  {
    StringTokenizer stk = new StringTokenizer(data);
    String path = stk.nextToken();
    String filename = stk.nextToken();

	DataInputStream dis = null;
    try {
       dis = new DataInputStream(new FileInputStream(
           DIRECTORY + '/' + path + "/" + filename));
		byte[] buf = new byte[dis.available()];
		dis.readFully(buf);
		sc.write(buf);
    } catch(Exception e) {
		Logging.getLogger().warning("Unable to send file: " + e);
    } finally {
    	try {
    		if(dis != null)
				dis.close();
    	} catch(IOException ioe) {} 
    }
	
  }

  private void mkdir(String from, String data, ObjectConnection sc)
  {
    data = data.substring(1);

    try {
        if((new File(DIRECTORY + data)).mkdirs())
    	  sc.write("OK");
    	else
      		sc.write("FAILED");
    } catch(Exception e) {}
  }

  private void listDir(String from, String data, ObjectConnection sc)
  {
    Vector result = new Vector();
    data = data.substring(1);

	Logging.getLogger().fine("FileService: " + DIRECTORY + data);

    String[] list = (new File(DIRECTORY + data)).list();
    File[] files = (new File(DIRECTORY + data)).listFiles();

	Logging.getLogger().fine("FileService: length=" + list.length);

    for(int i=0; i<list.length; i++)
    {
        if(files[i].isDirectory())
 	  result.addElement(list[i] + "/");
      	else
       	  result.addElement(list[i]);
    }

    try {
      sc.write(result);
    } catch(Exception e) {}
  }
}
