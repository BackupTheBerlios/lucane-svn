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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.reunion;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.lucane.client.Plugin;

/**
 * @author Jonathan Riboux
 *
 * Creates and format messages that are used under the meeting plugin. 
 */
public class ReunionMessage implements Serializable {
  /** type of the message */
  private int type;
  /** datas */
  private Object data;

  /**
   * Text message type. Concerns normal text messages.
   */
  public static final int TYPE_TEXT = 1;

  /**
   * Join message type. Concerns join messages to inform users that a new one
   * joined the meeting.
   */
  public static final int TYPE_JOIN = 2;

  /**
   * End message type. Concerns end messages to inform users that the meeting
   * has ended.
   */
  public static final int TYPE_END = 3;

  /**
   * Leave message type. Concerns leave messages to inform users that a user
   * left the meeting.
   */
  public static final int TYPE_LEAVE = 4;

  /**
   * Constructor.
   * @param type
   *         The message type.
   * @param data
   *         The data to transfer.
   */
  protected ReunionMessage(int type, Object data) {
    this.type = type;
    this.data = data;
  }

  /**
   * Creates a text message.
   * @param user
   *         The user login who sent the message.
   * @param text
   *         The text sent by the user
   * @return
   *         A new instance of ReunionMessage.
   */
  public static ReunionMessage createTextInstance(String user, String text) {
    String[] dt = { user, text };
    return new ReunionMessage(TYPE_TEXT, dt);
  }

  /**
   * Creates a join message.
   * @param user
   *         The user login who joint the meeting.
   * @return
   *         A new instance of ReunionMessage.
   */
  public static ReunionMessage createJoinInstance(String user) {
    return new ReunionMessage(TYPE_JOIN, user);
  }

  /**
   * Creates a leave message.
   * @param user
   *         The user login who left the meeting.
   * @return
   *         A new instance of ReunionMessage.
   */
  public static ReunionMessage createLeaveInstance(String user) {
    return new ReunionMessage(TYPE_LEAVE, user);
  }

  /**
   * Creates an end message.
   * @return
   *         A new instance of ReunionMessage.
   */
  public static ReunionMessage createEndInstance() {
    return new ReunionMessage(TYPE_END, null);
  }

  /**
   * Retrieves the data contained in the message.
   * @return
   *         The data contained in the message.
   */
  public Object getData() {
    return data;
  }

  /** 
     * Sets the data contained in the message.
     * @param object
     *         The data to put in the message.
     */
  public void setData(Object object) {
    data = object;
  }

  /**
   * Retrieves the type of the message.
   * @return
   *         The type of the message.
   */
  public int getType() {
    return type;
  }

  /**
   * Sets the type of the message.
   * @param i
   *         The type of the message.
   */
  public void setType(int i) {
    type = i;
  }

  /**
   * Creates the HTML code to show in the reunion plugin.
   * @param plugin
   *         The plugin using this instance of ReunionMessage
   * @return
   *         The HTML code to show in the reunion plugin.
   */
  public String toString(Plugin plugin) {
    if (type == TYPE_TEXT)
      return createHTMLTextMessage(
        plugin,
        ((String[]) data)[0],
        ((String[]) data)[1]);
    else if (type == TYPE_END)
      return createHTMLInfoMessage(plugin, plugin.tr("endMsg"));
    else if (type == TYPE_JOIN)
      return createHTMLInfoMessage(
        plugin,
        plugin.tr("joinMsg") + (String) data);
    else if (type == TYPE_LEAVE)
      return createHTMLInfoMessage(
        plugin,
        plugin.tr("leaveMsg") + (String) data);
    return null;
  }

  /**
   * Creates the HTML code to show in the reunion plugin.
   * @param plugin
   *         The plugin using this instance of ReunionMessage
   * @param user
   *         The user who wrote the message.
   * @param message
   *         The message wroten by the user.
   * @return
   *         The HTML code to show in the reunion plugin.
   */
  public static String createHTMLTextMessage(
    Plugin plugin,
    String user,
    String message) {
    String res = null;
    res =
      "<DIV "
        + "STYLE=\"padding:0px;margin-bottom:2px;"
        + "border-width:1px;border-style:solid;border-color:#eeeedd;"
        + "background-color:#ffffee;width:100%;\">"
        + "<font size=2>"
        + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())
        + "</font>&nbsp;<b><font size=4>"
        + user
        + "&nbsp;&gt; </font></b>"
        + "<font size=4>"
        + message
        + "</font></DIV>";
    return res;
  }

  /**
   * Creates the HTML code to show in the reunion plugin.
   * @param plugin
   *         The plugin using this instance of ReunionMessage
   * @param message
   *         The message to display.
   * @return
   */
  public static String createHTMLInfoMessage(Plugin plugin, String message) {
    String res;
    res = "<DIV><FONT SIZE=4 COLOR=#888888>" + message + "</FONT></DIV>";
    return res;
  }
}
