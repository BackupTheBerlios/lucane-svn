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
 * Representation of the RSS <tt>textInput</tt> element located within a
 * <tt>channel</tt> element
 * 
 * @author Christian Robert
 */

public class RssTextInput implements Serializable {

  private String myTitle        = "";
  private String myDescription  = "";
  private String myName         = "";
  private URL myLink            = null;
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  public String getDescription() {
    return this.myDescription;
  }

  public void setDescription(String description) {
    this.myDescription = description;
  }

  public URL getLink() {
    return this.myLink;
  }

  public void setLink(URL link) {
    this.myLink = link;
  }

  public String getName() {
    return this.myName;
  }

  public void setName(String name) {
    this.myName = name;
  }

  public String getTitle() {
    return this.myTitle;
  }

  public void setTitle(String title) {
    this.myTitle = title;
  }

}