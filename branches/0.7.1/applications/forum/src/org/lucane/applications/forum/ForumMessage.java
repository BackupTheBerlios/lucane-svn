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
package org.lucane.applications.forum;

import java.io.Serializable;
import java.util.StringTokenizer;

class ForumMessage implements Serializable
{
  public String id;
  public String idref;
  public String title;
  public String date;
  public String author;

  public ForumMessage(String digest)
  {
    StringTokenizer stk = new StringTokenizer(digest);
    id = stk.nextToken();
    idref = stk.nextToken();
    author = stk.nextToken();
    date = stk.nextToken();
    title = stk.nextToken("\0").substring(1);
  }

  public String toString()
  {
    return title;
  }
}