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

import java.util.Hashtable;

public class ReunionUsersProperties {
  private Hashtable users;
  private ColorsManager colorsManager;

  public ReunionUsersProperties() {
    users = new Hashtable();
    colorsManager = new ReunionUsersProperties.ColorsManager();
  }

  public ReunionUserProperties addUser(String user) {
    ReunionUserProperties rup = new ReunionUserProperties(user, colorsManager.getFgColor(), colorsManager.getBgColor());
    colorsManager.moveToNextColor();
    users.put(user, rup);
    return rup;
  }

  public ReunionUserProperties removeUser(String user) {
    return (ReunionUserProperties) users.remove(user);
  }

  public ReunionUserProperties getUserProperties(String user) {
    return (ReunionUserProperties) users.get(user);
  }


  class ColorsManager {
    private final String [] FG_COLORS = {
      "#444444",
      "#880000",
      "#008800",
      "#000088",
      "#884400",
      "#880044",
      "#008844",
      "#448800",
      "#440088",
      "#004488"
    };
    private final String [] BG_COLORS = {
      "#eeeeee",
      "#ffdddd",
      "#ddffdd",
      "#ddddff",
      "#ffeedd",
      "#ffddee",
      "#ddffee",
      "#eeddff",
      "#ddeeff"
    };
    private int currentColorIndex=0;
    private final int NUMBER_OF_COLORS=BG_COLORS.length;
    
    public ColorsManager(){
    }
    
    public int moveToNextColor() {
      currentColorIndex++;
      if (currentColorIndex>=NUMBER_OF_COLORS) {
        currentColorIndex=0;
      }
      return currentColorIndex;
    }
    
    public void moveToFirstColor() {
      currentColorIndex=0;
    }
    
    public String getBgColor() {
      return BG_COLORS[currentColorIndex];
    }
    
    public String getFgColor() {
      return FG_COLORS[currentColorIndex];
    }
  }

}
