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

package org.jperdian.rss2;

import java.net.URL;

import org.jperdian.rss2.dom.RssChannel;

/**
 * Implementation of a connector that can be used to read RSS channel data
 * and transform them into a valid RSS DOM object
 * 
 * @author Christian Robert
 */

public class RssClient {

  private RssParser myParser  = new RssParser();
  private URL myURL           = null;
  private String myKeywords   = "";
  private String myTitle      = "";
  
  /**
   * Creates a new client that receives it's data from the given URL
   * @param url
   *   the <code>URL</code> from which to receive the data
   */
  public RssClient(URL url) {
    this(url, "", "");
  }
  
  /**
   * Creates a new client that receives it's data from the given URL
   * @param url
   *   the <code>URL</code> from which to receive the data
   * @param keywords
   *   the keywords that can later be used for sorting
   * @param title
   *   the title to be used for display
   */
  public RssClient(URL url, String keywords, String title) {
    this.setURL(url);
    this.setKeywords(keywords);
    this.setTitle(title);
  }
  
  /**
   * Reads the data from the remote RSS source and create a new 
   * <code>RssChannel</code> object that contains the data that has been read
   * @return
   *   the data in RSS DOM format
   * @throws RssException
   *   thrown if the data is not in valid RSS format or the connection
   *   is unreachable
   */
  public final RssChannel getData() throws RssException {
    RssChannel channel    = new RssChannel(this);
    channel.setClient(this);
    this.loadData(channel);
    return channel;
  }
  
  /**
   * Loads the data into the given <code>RssChannel</code>
   */
  public void loadData(RssChannel channel) throws RssException {
    this.getParser().parse(this.getURL(), channel);
    channel.setDataLoaded(true);
    channel.setLastUpdate(System.currentTimeMillis());
  }
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  /**
   * Gets the <code>RssParser</code> through which the messages are analyzed
   */
  protected RssParser getParser() {
    return this.myParser;
  }
  
  /**
   * Sets the <code>URL</code> from which to read the data
   */
  protected void setURL(URL url) {
    this.myURL = url;
  }

  /**
   * Gets the <code>URL</code> from which to read the data
   */
  public URL getURL() {
    return this.myURL;
  }
  
  /**
   * Sets the keywords to be used for sorting the current client
   */
  protected void setKeywords(String keywords) {
    this.myKeywords = keywords;
  }

  /**
   * Gets the keywords to be used for sorting the current client
   */
  public String getKeywords() {
    return this.myKeywords;
  }
  
  /**
   * Sets the title to be used for sorting the current client
   */
  protected void setTitle(String title) {
    this.myTitle = title;
  }

  /**
   * Gets the title to be used for sorting the current client
   */
  public String getTitle() {
    return this.myTitle;
  }
  
}