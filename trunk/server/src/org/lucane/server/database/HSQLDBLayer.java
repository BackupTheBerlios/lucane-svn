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
package org.lucane.server.database;

import java.sql.*;

import org.lucane.common.Logging;
import org.lucane.server.Server;

class HSQLDBLayer extends DatabaseAbstractionLayer
{
  private Connection connection;
  
  public HSQLDBLayer(String url, String login, String passwd) 
  {
    this.url = getAbsoluteUrl(url);
    this.login = login;
    this.passwd = passwd;
    this.connection = null;
  }
  
  private String getAbsoluteUrl(String url)
  {
  	String standardStart = "jdbc:hsqldb:";

  	if(url.toLowerCase().startsWith(standardStart))
  	{
  		String miniurl = url.substring(standardStart.length()).replace('\\','/');
  		
  		if(! miniurl.startsWith("/") && miniurl.charAt(1)!=':')
  			url = standardStart + Server.getInstance().getWorkingDirectory() + miniurl;
  	}
  	
  	Logging.getLogger().fine("HSQLdb url : " + url);
  	
  	return url;
  }
  
  public Connection openConnection()
  throws SQLException
  {
  	if(this.connection == null)
  		this.connection = new UnclosableConnection(DriverManager.getConnection(url, login, passwd));
  	
  	return this.connection;
  }

  public String resolveType(String type)
  {
    if(type.equalsIgnoreCase("SMALLTEXT"))
      return "VARCHAR(250)";
    else if(type.equalsIgnoreCase("TEXT"))
      return "VARCHAR";
    else if(type.equalsIgnoreCase("SMALLINT"))
      return "SMALLINT";
    else if(type.equalsIgnoreCase("INT"))
      return "INT";
    else if(type.equalsIgnoreCase("BIGINT"))
      return "NUMERIC";
    else if(type.equalsIgnoreCase("REAL"))
      return "REAL";
    else
      return type;
  }
}

