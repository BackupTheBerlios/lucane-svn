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

/**
 * Representation of an RSS <tt>image</tt> element
 * 
 * @author Christian Robert
 */

public class RssImage implements Serializable {

  private String myTitle        = "";
  private String myDescription  = "";
  private URL myURL             = null;
  private URL myLink            = null;
  private int myWidth           = RssConstants.DEFAULT_IMAGE_WIDTH;
  private int myHeight          = RssConstants.DEFAULT_IMAGE_HEIGHT;
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  public int getHeight() {
    return this.myHeight;
  }

  public void setHeight(int height) {
    this.myHeight = height;
  }

  public URL getLink() {
    return this.myLink;
  }

  public void setLink(URL link) {
    this.myLink = link;
  }

  public String getTitle() {
    return this.myTitle;
  }

  public void setTitle(String title) {
    this.myTitle = title;
  }

  public URL getURL() {
    return this.myURL;
  }

  public void setURL(URL url) {
    this.myURL = url;
  }

  public int getWidth() {
    return this.myWidth;
  }

  public void setWidth(int width) {
    this.myWidth = width;
  }

  public String getDescription() {
    return this.myDescription;
  }

  public void setDescription(String description) {
    this.myDescription = description;
  }

}