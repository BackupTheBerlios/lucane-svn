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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.forum;

import java.awt.event.*;

class WinListener
  implements WindowListener
{
  private Forum parent;

  public WinListener(Forum qf)
  {
    parent = qf;
  }

  public void windowClosing(WindowEvent e)
  {
    parent.postWindow = null;
  }

  public void windowActivated(WindowEvent e)
  {
  }

  public void windowClosed(WindowEvent e)
  {
  }

  public void windowDeactivated(WindowEvent e)
  {
  }

  public void windowDeiconified(WindowEvent e)
  {
  }

  public void windowIconified(WindowEvent e)
  {
  }

  public void windowOpened(WindowEvent e)
  {
  }
}