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

public class Note
  implements Serializable, Signable
{
  //-- attributes
  private String id;

  private String author;
  private String title;
  private String content;

  private Date creationDate;
  private Date editionDate;

  private boolean published;
  private boolean commentable;

  /**
   * Public constructor
   * Used for new notes
   */
  public Note(String author, String title, String content, boolean published, boolean commentable)
  {
    this.id = null;

    this.author = author;
    this.title = title;
    this.content = content;

    this.creationDate = new Date();
    this.editionDate = null;

    this.published = published;
    this.commentable = commentable;
  }

  /**
   * Protected constructor
   * Used when loading notes from the database
   */
  protected Note(String id, String author, String title, String content, String creationDate, String editionDate, String published, String commentable)
  {
    this.id = id;
    
    this.author = author;
    this.title = title;
    this.content = content;

    this.creationDate = new Date(Long.parseLong(creationDate));
    if(editionDate.length() > 0)
      this.editionDate = new Date(Long.parseLong(editionDate));
    else
      this.editionDate = null;

    this.published = Integer.parseInt(published) > 0;
    this.commentable = Integer.parseInt(commentable) > 0;
  }


  /**
   * Edit the note content
   */
  public void editContent(String content)
  {
    this.content = content;
    this.editionDate = new Date();
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
  
  public Date getEditionDate()
  {
    return this.editionDate;
  }

  public boolean isPublic()
  {
    return this.published;
  }
  
  public boolean isCommentable()
  {
    return this.commentable;
  } 

  public String toString()
  {
	Locale locale = new Locale(Client.getInstance().getConfig().getLanguage());
	DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
	return this.title + " - " + df.format(this.creationDate);
  }
  
  public String toSignableString()
  {
    return this.title + "|" + this.author + "|" + this.id;
  }
}
