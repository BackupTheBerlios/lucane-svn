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
package org.lucane.applications.notes;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

import org.lucane.client.Client;
import org.lucane.common.signature.Signable;

public class Comment
  implements Serializable, Signable
{
  //-- attributes
  private String id; 
  private String noteId;

  private String author;
  private String title;
  private String content;

  private Date creationDate;

  /**
   * Public constructor
   * Used for new comments
   */
  public Comment(Note note, String author, String title, String content)
  {
    this.id = null;
    this.noteId = note.getId();

    this.author = author;
    this.title = title;
    this.content = content;

    this.creationDate = new Date();
  }

  /**
   * Protected constructor
   * Used when loading comments from the database
   */
  protected Comment(String id, String noteId, String author, String title, String content, String date)
  {
    this.id = id;
    this.noteId = noteId;
    
    this.author = author;
    this.title = title;
    this.content = content;

    this.creationDate = new Date(Long.parseLong(date));
  }

  //-- getters 
  public void setId(String id)
  {
  	this.id = id;  
  }
  
  public String getId()
  {
    return this.id;
  }

  public String getNoteId()
  {
    return this.noteId;
  }

  public String getAuthor()
  {
    return this.author;
  }
  
  public String getTitle()
  {
    return this.title;
  }

  public String getContent()
  {
    return this.content;
  }

  public Date getCreationDate()
  {
    return this.creationDate;
  }

  public String toString()
  {
	Locale locale = new Locale(Client.getInstance().getConfig().getLanguage());
	DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
	return this.author + " - " + this.title + " - " + df.format(this.creationDate);
  }
  
  public String toSignableString()
  {
	return this.title + "|" + this.author + "|" + this.id;
  }
}
