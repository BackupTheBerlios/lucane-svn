/**
 * RSS framework and reader
 * Copyright (C) 2004 Christian Robert
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

package org.jperdian.rss2.dom;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of an RSS <tt>item</tt> that contains the actual message
 * text
 * 
 * @author Christian Robert
 */

public class RssItem implements Serializable {

  private String myTitle              = "";
  private URL myLink                  = null;
  private String myDescription        = "";
  private String myStrippedDescription        = "";
  private String myAuthor             = "";
  private List myCategoryList         = new ArrayList();
  private URL myComments              = null;
  private RssEnclosure myEnclosure    = null;
  private RssGuid myGuid              = null;
  private Date myPubDate              = null;
  private RssChannel mySource         = null;
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  public String getAuthor() {
    return this.myAuthor;
  }

  public void setAuthor(String author) {
    this.myAuthor = author;
  }

  public void addCategory(String category) {
    this.getCategoryList().add(category);
  }
  
  public List getCategoryList() {
    return this.myCategoryList;
  }

  public void setCategoryList(List list) {
    this.myCategoryList = list;
  }

  public URL getComments() {
    return this.myComments;
  }

  public void setComments(URL comments) {
    this.myComments = comments;
  }

  public String getDescription() {
    return this.myDescription;
  }

  public String getStrippedDescription() {
  	return this.myStrippedDescription;
  }
  
  public void setDescription(String description) {
    this.myDescription = description;
    this.myStrippedDescription = description.trim();
    
    int start = myStrippedDescription.indexOf('<');
    while(start >= 0)
    {
    	int end = myStrippedDescription.indexOf('>', start);
		if(end < 0)
			break;
		
		myStrippedDescription = myStrippedDescription.substring(0, start)
			+ myStrippedDescription.substring(end+1);
		start = myStrippedDescription.indexOf('<');
    }
  }

  public RssEnclosure getEnclosure() {
    return this.myEnclosure;
  }

  public void setEnclosure(RssEnclosure enclosure) {
    this.myEnclosure = enclosure;
  }

  public RssGuid getGuid() {
    return this.myGuid;
  }

  public void setGuid(RssGuid guid) {
    this.myGuid = guid;
  }

  public URL getLink() {
    return myLink;
  }

  public void setLink(URL link) {
    this.myLink = link;
  }

  public Date getPubDate() {
    return this.myPubDate;
  }

  public void setPubDate(Date pubDate) {
    this.myPubDate = pubDate;
  }

  public RssChannel getSource() {
    return this.mySource;
  }

  public void setSource(RssChannel source) {
    this.mySource = source;
  }

  public String getTitle() {
    return this.myTitle;
  }

  public void setTitle(String title) {
    this.myTitle = title;
  }
}