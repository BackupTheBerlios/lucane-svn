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
 * Implementation of the <tt>enclosure</tt> element with a <tt>item</tt>
 * that contains additional data
 * 
 * @author Christian Robert
 */

public class RssEnclosure implements Serializable {

  private URL myURL       = null;
  private long myLength   = -1;
  private String myType   = "";
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  public long getLength() {
    return this.myLength;
  }

  public void setLength(long length) {
    this.myLength = length;
  }

  public String getType() {
    return this.myType;
  }

  public void setType(String type) {
    this.myType = type;
  }

  public URL getURL() {
    return this.myURL;
  }

  public void setURL(URL url) {
    this.myURL = url;
  }

}