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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jperdian.rss2.RssChannelUpdateListener;
import org.jperdian.rss2.RssClient;
import org.jperdian.rss2.RssException;

/**
 * Implementatino of a <tt>channel</tt> element as described in the RSS
 * specification
 * 
 * @author Christian Robert
 */

public class RssChannel implements Serializable {
  
  private transient RssClient myClient  = null;
  private boolean stateDataLoaded       = false;
  private boolean stateDataLoadFailed   = false;
  private long myLastUpdate             = -1;
  
  private String myTitle                = "";
  private URL myLink                    = null;
  private String myDescription          = "";
  private String myLanguage             = "";
  private String myCopyright            = "";
  private String myManagingEditor       = "";
  private String myWebmaster            = "";
  private Date myPubDate                = null;
  private Date myLastBuildDate          = null;
  private List myCategoryList           = new ArrayList(1);
  private String myGenerator            = "";
  private URL myDocs                    = null;
  private RssCloud myCloud              = null;
  private int myTtl                     = RssConstants.DEFAULT_TTL;
  private RssImage myImage              = null;
  private String myRating               = "";
  private Set mySkipHours               = new HashSet();
  private Set mySkipDays                = new HashSet();
  private List myItemList               = new ArrayList();
  private RssTextInput myTextInput      = null;
  private List myUpdateListenerList     = new ArrayList();

  public RssChannel(RssClient client) {
    this.setClient(client);
  }
  
  public String toString() {
    StringBuffer result     = new StringBuffer();
    result.append(this.getTitle()).append(" @ ").append(this.getLink());
    result.append("\n").append(this.getDescription());
    return result.toString();
  }
  
  /**
   * Updates the data in the current channel
   */
  public void update() throws RssException {
    this.getClient().loadData(this);
  }
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------

  public void addCategory(String category) {
    this.getCategoryList().add(category);
  }

  public List getCategoryList() {
    return this.myCategoryList;
  }

  public void setCategoryList(List categoryList) {
    this.myCategoryList = categoryList;
  }

  public RssCloud getCloud() {
    return this.myCloud;
  }

  public void setCloud(RssCloud cloud) {
    this.myCloud = cloud;
  }

  public String getCopyright() {
    return this.myCopyright;
  }

  public void setCopyright(String copyright) {
    this.myCopyright = copyright;
  }

  public String getDescription() {
    return this.myDescription;
  }

  public void setDescription(String description) {
    this.myDescription = description;
  }

  public URL getDocs() {
    return this.myDocs;
  }

  public void setDocs(URL docs) {
    this.myDocs = docs;
  }

  public String getGenerator() {
    return this.myGenerator;
  }

  public void setGenerator(String generator) {
    this.myGenerator = generator;
  }

  public RssImage getImage() {
    return this.myImage;
  }
  
  public void setImage(RssImage image) {
    this.myImage = image;
  }

  public void addItem(RssItem item) {
    this.getItemList().add(item);
  }

  public List getItemList() {
    return this.myItemList;
  }

  public void setItemList(List itemList) {
    this.myItemList = itemList;
  }

  public String getLanguage() {
    return this.myLanguage;
  }

  public void setLanguage(String language) {
    this.myLanguage = language;
  }

  public Date getLastBuildDate() {
    return this.myLastBuildDate;
  }

  public void setLastBuildDate(Date lastBuildDate) {
    this.myLastBuildDate = lastBuildDate;
  }

  public URL getLink() {
    return this.myLink;
  }

  public void setLink(URL link) {
    this.myLink = link;
  }

  public String getManagingEditor() {
    return this.myManagingEditor;
  }

  public void setManagingEditor(String managingEditor) {
    this.myManagingEditor = managingEditor;
  }

  public Date getPubDate() {
    return this.myPubDate;
  }

  public void setPubDate(Date pubDate) {
    this.myPubDate = pubDate;
  }

  public String getRating() {
    return this.myRating;
  }

  public void setRating(String rating) {
    this.myRating = rating;
  }

  public void addSkipDay(String day) {
    this.getSkipDays().add(day);
  }

  public Set getSkipDays() {
    return this.mySkipDays;
  }

  public void setSkipDays(Set skipDays) {
    this.mySkipDays = skipDays;
  }

  public void addSkipHour(int hour) {
    this.getSkipHours().add(new Integer(hour));
  }
  
  public Set getSkipHours() {
    return this.mySkipHours;
  }

  public void setSkipHours(Set skipHours) {
    this.mySkipHours = skipHours;
  }

  public String getTitle() {
    return this.myTitle;
  }

  public void setTitle(String title) {
    this.myTitle = title;
  }

  public int getTtl() {
    return this.myTtl;
  }

  public void setTtl(int ttl) {
    this.myTtl = ttl;
  }

  public String getWebmaster() {
    return this.myWebmaster;
  }

  public void setWebmaster(String webmaster) {
    this.myWebmaster = webmaster;
  }
  
  public RssTextInput getTextInput() {
    return this.myTextInput;
  }

  public void setTextInput(RssTextInput textInput) {
    this.myTextInput = textInput;
  }
  
  /**
   * Sets whether the data in the current channel has already been loaded 
   */
  public void setDataLoaded(boolean state) {
    this.stateDataLoaded = state;
  }
  
  /**
   * Checks whether the data in the current channel has already been loaded 
   */
  public boolean isDataLoaded() {
    return this.stateDataLoaded;
  }
  
  /**
   * Sets whether the loading process failed for the current channel
   */
  public void setDataLoadFailed(boolean state) {
    this.stateDataLoadFailed = state;
  }
  
  /**
   * Checks whether the loading process failed for the current channel
   */
  public boolean isDataLoadFailed() {
    return this.stateDataLoadFailed;
  }
  
  /**
   * Sets the receiver from which this channel has been received
   */
  public void setClient(RssClient client) {
    this.myClient = client;
  }
  
  /**
   * Gets the receiver from which this channel has been received
   */
  public RssClient getClient() {
    return this.myClient;
  }

  /**
   * Sets the time when the channel was updated at last
   */
  public void setLastUpdate(long time) {
    this.myLastUpdate = time;
  }
  
  /**
   * Gets the time when the channel was updated at last
   */
  public long getLastUpdate() {
    return this.myLastUpdate;
  }

  /**
   * Gets the <code>List</code> in which all the registred
   * <code>RssChannelUpdateListener</code> objects are stored
   */
  protected List getUpdateListenerList() {
    return this.myUpdateListenerList;
  }
  
  /**
   * Notifies all listeners, that the current channel has been update
   */
  protected void fireChannelUpdate() {
    for(int i=0; i < this.getUpdateListenerList().size(); i++) {
      ((RssChannelUpdateListener)this.getUpdateListenerList().get(i)).channelUpdated(this);
    }
  }
  
  /**
   * Adds the listener
   */
  public void addUpdateListener(RssChannelUpdateListener listener) {
    this.getUpdateListenerList().add(listener);
  }
  
  /**
   * Removes the listener
   */
  public void removeUpdateListener(RssChannelUpdateListener listener) {
    this.getUpdateListenerList().remove(listener);
  }
  
}