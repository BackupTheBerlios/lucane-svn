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

/**
 * <p>Implementation of the RSS <tt>cloud</tt> element.</p>
 * 
 * <p>Note: The <tt>cloud</tt> processing is not supported yet</p>
 * 
 * @author Christian Robert
 */

public class RssCloud implements Serializable {

  private String myDomain             = "";
  private int myPort                  = 80;
  private String myPath               = "";
  private String myRegisterProcedure  = "";
  private String myProtocol           = "";
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  public String getDomain() {
    return myDomain;
  }

  public void setDomain(String domain) {
    this.myDomain = domain;
  }

  public String getPath() {
    return this.myPath;
  }

  public void setPath(String path) {
    this.myPath = path;
  }

  public int getPort() {
    return this.myPort;
  }

  public void setPort(int port) {
    this.myPort = port;
  }

  public String getProtocol() {
    return this.myProtocol;
  }

  public void setProtocol(String protocol) {
    this.myProtocol = protocol;
  }

  public String getRegisterProcedure() {
    return this.myRegisterProcedure;
  }

  public void setRegisterProcedure(String registerProcedure) {
    this.myRegisterProcedure = registerProcedure;
  }

}