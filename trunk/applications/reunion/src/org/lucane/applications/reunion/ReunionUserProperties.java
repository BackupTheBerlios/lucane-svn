/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Jonathan Riboux <jonathan.riboux@wanadoo.fr>
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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.reunion;

public class ReunionUserProperties {
  private String user;
  private String bgcolor;
  private String fgcolor;

  public ReunionUserProperties(String user, String fgcolor, String bgcolor) {
    this.user = user;
    this.fgcolor = fgcolor;
    this.bgcolor = bgcolor;
  }

  public String getUser() {
    return user;
  }
  public void setUser(String string) {
    user = string;
  }

  public String getBgColor() {
    return bgcolor;
  }
  public void setBgcolor(String string) {
    bgcolor = string;
  }

  public String getFgColor() {
    return fgcolor;
  }
  public void setFgcolor(String string) {
    fgcolor = string;
  }

}
