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

class PostgresLayer extends DatabaseAbstractionLayer
{
  public PostgresLayer(String url, String login, String passwd) 
  {
    this.url = url;
    this.login = login;
    this.passwd = passwd;
  }


  public String resolveType(String type)
  {
    if(type.equalsIgnoreCase("SMALLTEXT"))
      return "VARCHAR(255)";
    else if(type.equalsIgnoreCase("TEXT"))
      return "TEXT";
    else if(type.equalsIgnoreCase("SMALLINT"))
      return "int2";
    else if(type.equalsIgnoreCase("INT"))
      return "int4";
    else if(type.equalsIgnoreCase("BIGINT"))
      return "int4";
    else if(type.equalsIgnoreCase("REAL"))
      return "float8";
    else
      return type;
  }
}
