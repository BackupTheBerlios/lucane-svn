/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002-2004  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.client.widgets;

import java.util.Vector;
import javax.swing.*;
import javax.swing.JOptionPane;

import org.lucane.common.Logging;


public class DialogBox
{
  /**
   * Outputs and logs a graphical error message
   * 
   * @param message the error message
   */
  public static void error(String message)
  {
    JOptionPane.showMessageDialog(null, message, "Lucane", JOptionPane.ERROR_MESSAGE);
	Logging.getLogger().info(message);
  }

  /**
   * Outputs and logs a graphical informative message
   * 
   * @param message the informative message
   */
  public static void info(String message)
  {
    JOptionPane.showMessageDialog(null, message, "Lucane", JOptionPane.INFORMATION_MESSAGE);
    Logging.getLogger().info(message);
  }

  /**
   * Display a question box (yes/no)
   * 
   * @param title the dialog box title
   * @param message the message
   * @return true if yes is cliqued, false otherwise
   */
  public static boolean question(String title, String message)
  {

    if(JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      return true;
    else
      return false;
  }
  
  /**
   * Display a text box
   * 
   * @param title the dialog box title
   * @param message the message
   * @return the user answer
   */
  public static String input(String title, String message)
  {
	return JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
  }

  /**
   * Display a ListBox
   * 
   * @param owner the owner JFrame or null
   * @param title the dialog title
   * @param message the dialog message
   * @param list the selectables items
   * @return the selected index
   */
  public static int list(JFrame owner, String title, String message, Vector list)
  {
    return (new ListBox(owner, title, message, list)).selectItemByIndex();
  }
}
