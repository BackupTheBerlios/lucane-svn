/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.applications.helpbrowser;

import javax.swing.*;
import javax.swing.event.*;


public class LinkListener
  implements HyperlinkListener
{

  JEditorPane big;
  JTextArea mini;
  HelpBrowser parent;

  public LinkListener(HelpBrowser parent, JEditorPane big, JTextArea mini)
  {
    this.big = big;
    this.mini = mini;
    this.parent = parent;
  }


  public void hyperlinkUpdate(HyperlinkEvent he)
  {
    String url = he.getDescription();
    boolean tooltip = false;
    boolean section = false;

    if(url.startsWith("#tooltip:"))
    {
      tooltip = true;
      url = url.substring(9); //#tooltip:
    }
    else if(url.startsWith("#section:"))
    {
      section = true;
      url = url.substring(9); //#section:
    }

    if(he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    {
      if(section)
        this.parent.gotoSection(url);
    }
    else if(he.getEventType() == HyperlinkEvent.EventType.ENTERED)
    {
      if(tooltip)
        this.mini.setText(url);
      else if(section)
        this.mini.setText(parent.tr("gotoSection1") + url + parent.tr("gotoSection2"));
    }
    else if(he.getEventType() == HyperlinkEvent.EventType.EXITED)
    {
      if(tooltip || section)
        this.mini.setText("");
    }
  }
}