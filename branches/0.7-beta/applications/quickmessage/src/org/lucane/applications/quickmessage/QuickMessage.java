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
package org.lucane.applications.quickmessage;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class QuickMessage
  extends Plugin
  implements ActionListener,
             KeyListener
{
  /* parameters */
  private ConnectInfo[] friends;
  private String message;

  /* widgets */
  private JFrame dialog;
  private JTextField txtWho;
  private HTMLEditor txtMessage;
  private HTMLEditor txtNew;
  private JButton btnMain;
  private boolean ctrl;


  public QuickMessage()
  {
    //nothing
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    Plugin p = new QuickMessage(friends, starter);
    return p;
  }


  public QuickMessage(ConnectInfo[] friends, boolean starter)
  {
    this.starter = starter;
    this.friends = friends;
  }

  public void load(ObjectConnection friend, ConnectInfo who, String data)
  {
    this.friends = new ConnectInfo[1];
    this.friends[0] = who;
    this.message = data;
  }


  public void start()
  {
    this.message = "";

    if(this.friends.length == 0)
    {
      DialogBox.info(tr("nodest"));
      exit();

      return;
    }

    dialog = new JFrame("QuickMessage");
    dialog.addWindowListener(this);
    dialog.getContentPane().setLayout(new BorderLayout());
    txtWho = new JTextField(friends[0].getName());
    txtWho.setEditable(false);

    for(int i = 1; i < friends.length; i++)
      txtWho.setText(txtWho.getText() + ";" + friends[i].getName());

    txtNew = new HTMLEditor();
    dialog.addKeyListener(this);
    txtNew.addKeyListener(this);
    btnMain = new JButton(tr("send"));
    btnMain.addActionListener(this);
    dialog.getContentPane().add(txtWho, BorderLayout.NORTH);
    dialog.getContentPane().add(txtNew, BorderLayout.CENTER);
    dialog.getContentPane().add(btnMain, BorderLayout.SOUTH);
    dialog.setSize(350, 200);
	  dialog.setIconImage(this.getImageIcon().getImage());
    dialog.setVisible(true);
  }

  public void follow()
  {
    dialog = new JFrame("QuickMessage");
    dialog.addWindowListener(this);
    dialog.getContentPane().setLayout(new BorderLayout());
    txtWho = new JTextField(friends[0].getName());
    txtWho.setEditable(false);
    txtMessage = new HTMLEditor();
    txtMessage.setText(this.message);
    txtMessage.setEditable(false);
    txtMessage.setToolbarVisible(false);
    txtNew = new HTMLEditor();
    dialog.addKeyListener(this);
    txtNew.addKeyListener(this);
    btnMain = new JButton(tr("answer"));
    btnMain.addActionListener(this);
    dialog.getContentPane().add(txtWho, BorderLayout.NORTH);

    JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                    txtMessage, txtNew);
    jsp.setOneTouchExpandable(true);
    dialog.getContentPane().add(jsp, BorderLayout.CENTER);
    dialog.getContentPane().add(btnMain, BorderLayout.SOUTH);
    dialog.setSize(350, 350);
	dialog.setIconImage(this.getImageIcon().getImage());
    dialog.setVisible(true);
    jsp.setDividerLocation(0.4);
  }

  public void actionPerformed(ActionEvent ae)
  {
    this.message = this.txtNew.getText();
    for(int i = 0; i < friends.length; i++)
    {

      try
      {
        ObjectConnection sc = Communicator.getInstance().sendMessageTo(friends[i], 
                          this.getName(), this.message);
        sc.close();
      }
      catch(Exception e)
      {
        DialogBox.error(tr("err")+ 
              friends[i].getName() + " : " + e);
      }
    }

    dialog.dispose();
    exit();
  }

  public void keyPressed(KeyEvent e)
  {

    if(e.getKeyCode() == KeyEvent.VK_CONTROL)
      ctrl = true;
    else if(e.getKeyCode() == KeyEvent.VK_ENTER && ctrl)
    {
      ctrl = false;
      actionPerformed(null);
    }
    else
      ctrl = false;
  }

  public void keyReleased(KeyEvent e)
  {
  }

  public void keyTyped(KeyEvent e)
  {
  }
}
