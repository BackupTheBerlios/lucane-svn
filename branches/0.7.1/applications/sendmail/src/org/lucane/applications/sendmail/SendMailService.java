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
package org.lucane.applications.sendmail;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.*;

import java.util.*;
import java.sql.*;


public class SendMailService extends Service
{
	protected static String SMTP_HOST = "localhost";
	
	private DatabaseAbstractionLayer layer = null;
	private Connection conn = null;
	private Statement st = null;
	private ResultSet res = null;
	
	public SendMailService()
	{
	}
	
	public void createTable()
	{
		try  {  	
			String dbDescription = getDirectory()	+ "db-sendmail.xml";
			layer.getTableCreator().createFromXml(dbDescription);
			
			conn = layer.getConnection();
			String request = "INSERT INTO smtphost VALUES ('" + SMTP_HOST + "')";
			st = conn.createStatement();
			st.execute(request);
			st.close();
			conn.close();
		} catch (Exception e) {
			Logging.getLogger().severe("Unable to install SendMailService !");
			e.printStackTrace();
		}    
	}
	
	public void initSmtpHost()
	{
		try 
		{
			String request = "SELECT hostname FROM smtphost";
			conn = layer.getConnection();
			st = conn.createStatement();
			res = st.executeQuery(request);
			if(res.next())
				SMTP_HOST = res.getString(1);
			res.close();
			st.close();
		}
		catch(SQLException e)
		{
			Logging.getLogger().warning("Warning: unable to fetch the mailhost, using default: " + SMTP_HOST);
		}
		finally
		{
			try {conn.close();} catch (SQLException e1) {}
		}
	}
	
	public void process(ObjectConnection oc, Message message)
	{
		//fetch parameters
		HashMap map = (HashMap)message.getData();
		String from = (String)map.get("from");
		String subject = (String)map.get("subject");
		String to = (String)map.get("to");
		String cc = (String)map.get("cc");
		String bcc = (String)map.get("bcc");
		String content = (String)map.get("content");
		String contentType = (String)map.get("content-type");
		HashMap attach = (HashMap)map.get("attach");
		
		//default values
		if(from == null)
			from = "SendMailService@" + SMTP_HOST;
		if(content == null)
			content = "";
		if(contentType == null)
			contentType = "text/plain";
		
		//build mail and send it
		try {
			SendableMail sm = new SendableMail();
			sm.setFrom(from);
			sm.setContent(content, contentType);
			if(subject != null)
				sm.setSubject(subject);
			if(to != null)
				sm.addTo(to);
			if(cc != null)
				sm.addCc(cc);
			if(bcc != null)
				sm.addBcc(bcc);
			
			//attachments loop
			if(attach != null)
			{
				Iterator i = attach.keySet().iterator();
				while(i.hasNext())
				{
					String key = (String)i.next();
					String data = (String)attach.get(key);
					if(key != null && data != null)
						sm.attach(key, data);
				}
			}
			
			//send
			sm.send();
			oc.write("OK");
		}
		catch(Exception e) {
			try {
				e.printStackTrace();
				oc.write("FAILED " + e);
			} catch(Exception e2) {}
		}
	}
	
	public void init(Server parent)
	{
		layer = parent.getDBLayer();
		initSmtpHost();
	}
	
	public void install()
	{
		createTable();
	}
}
