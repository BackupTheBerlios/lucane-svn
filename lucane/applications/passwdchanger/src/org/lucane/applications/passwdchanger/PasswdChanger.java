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
package org.lucane.applications.passwdchanger;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;
import org.lucane.common.signature.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class PasswdChanger
  extends Plugin
  implements ActionListener
{

  /* dialog components */
  private JFrame frame;
  private JTextField txtold;
  private JTextField txtnew1;
  private JTextField txtnew2;
  private JButton btnChange;

  /* network and stream stuff */
  private ConnectInfo cinfo;

  /**
   * Void contructor. Used by PluginLoader
   */
  public PasswdChanger()
  {
    this.starter = true;
  }

  /**
   * @param friends the Client the Plugin belongs to
   * @param starter is true if the Clients started the plugin (start() will be
   *        called instead of follow()
   * @return a new instance of the Plugin.
   */
  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new PasswdChanger();
  }

  /**
   * Show a dialog asking for the friend name
   */
  public void start()
  {
    this.cinfo = Communicator.getInstance().getConnectInfo("org.lucane.applications.passwdchanger");
    frame = new JFrame(getTitle());
    frame.addWindowListener(this);
    frame.getContentPane().setLayout(new GridLayout(0, 2));
    txtold = new JPasswordField();
    txtnew1 = new JPasswordField();
    txtnew2 = new JPasswordField();
    btnChange = new JButton(tr("button"));
    btnChange.addActionListener(this);
    frame.getContentPane().add(new JLabel(tr("old")));
    frame.getContentPane().add(txtold);
    frame.getContentPane().add(new JLabel(tr("new")));
    frame.getContentPane().add(txtnew1);
    frame.getContentPane().add(new JLabel(tr("check")));
    frame.getContentPane().add(txtnew2);
    frame.getContentPane().add(new JLabel(""));
    frame.getContentPane().add(btnChange);
    frame.pack();
	frame.setIconImage(this.getImageIcon().getImage());
    frame.setVisible(true);
  }

  /**
   * Do something on a buttonclick. If it is the dialog button, initailize
   * everything. If it is the main button, send the message.
   * 
   * @param ae the ActionEvent
   */
  public void actionPerformed(ActionEvent ae)
  {
    if(! txtnew1.getText().equals(txtnew2.getText()))
    {
      DialogBox.error(tr("typo"));
      return;
    }

    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(cinfo, 
                         "org.lucane.applications.passwdchanger", "GET_KEY");
      String response = (String)sc.read();
      sc.close();

      String clearkey = Signer.cypher(response, txtold.getText());
      String enckey = Signer.cypher(clearkey, txtnew1.getText());
      sc = Communicator.getInstance().sendMessageTo(cinfo, 
                   "org.lucane.applications.passwdchanger", "CHANGE_PASSWD " + 
                     MD5.encode(txtold.getText()) +  " " + 
                     MD5.encode(txtnew1.getText()) +  " " + enckey);
		response = (String)sc.read();
		sc.close();

      if(response.equals("BAD_PASSWD"))
        DialogBox.error(tr("invalid"));
      else if(response.equals("PASSWD_CHANGED"))
      {
        DialogBox.info(tr("modified"));
        frame.dispose();
        exit();
      }
    }
    catch(Exception e)
    {
      DialogBox.error(tr("error") + e);
    }
  }
}
