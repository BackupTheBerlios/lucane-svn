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
package org.lucane.applications.forumadmin;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;


public class ForumAdmin
  extends StandalonePlugin
  implements ActionListener
{

  private ManagedWindow mainWindow;
  private JButton btnCreate;
  private JButton btnDelete;
  private JButton btnClose;
  private JList lstForums;
  private ConnectInfo service;

  public ForumAdmin()
  {
    this.starter = true;
  }

  public Plugin newInstance(ConnectInfo[] friends, boolean starter)
  {
    return new ForumAdmin();
  }

  public void start()
  {
	this.service = Communicator.getInstance().getConnectInfo("org.lucane.applications.forumadmin");

    mainWindow = new ManagedWindow(this, getTitle());
    lstForums = new JList();
    btnCreate = new JButton(tr("create"));
    btnDelete = new JButton(tr("remove"));
    btnClose = new JButton(tr("close"));
    btnCreate.addActionListener(this);
    btnDelete.addActionListener(this);
    btnClose.addActionListener(this);
    mainWindow.getContentPane().setLayout(new BorderLayout());
    mainWindow.getContentPane().add(lstForums, BorderLayout.CENTER);

    JPanel pnlBtns = new JPanel(new BorderLayout());
    JPanel pnlTop = new JPanel(new GridLayout(0, 1));
    pnlTop.add(btnCreate);
    pnlTop.add(btnDelete);
    pnlBtns.add(pnlTop, BorderLayout.NORTH);
    pnlBtns.add(btnClose, BorderLayout.SOUTH);
    mainWindow.getContentPane().add(pnlBtns, BorderLayout.EAST);
    mainWindow.setPreferredSize(new Dimension(300, 300));
	mainWindow.setIconImage(this.getImageIcon().getImage());
    mainWindow.show();
    getForumList();
  }
  
  public void actionPerformed(ActionEvent ae)
  {
    if(ae.getSource() == btnCreate)
      createForum();
    else if(ae.getSource() == btnDelete)
      deleteForum();
    else if(ae.getSource() == btnClose)
    {
      mainWindow.dispose();
      exit();
    }
  }

  private void getForumList()
  {
    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(service, 
                              "org.lucane.applications.forumadmin", "LIST");
      Vector v = (Vector)sc.read();
      lstForums.setListData(v);
      sc.close();
    }
    catch(Exception e)
    {
      DialogBox.error(tr("listError"));
    }
  }

  private void createForum()
  {
    String forum = JOptionPane.showInputDialog(tr("new"));

    if(forum == null || forum.equals(""))
      return;

    forum = sqlZap(forum);

    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(service, 
                         "org.lucane.applications.forumadmin", "CREATE " + forum);
  
      String line = (String)sc.read();

      if(! line.equals("OK"))
        DialogBox.error(tr("createError"));
      else
        getForumList();
    }
    catch(Exception e)
    {
      DialogBox.error(tr("createError"));
    }
  }

  private void deleteForum()
  {
    String forum = (String)lstForums.getSelectedValue();

    if(forum == null)
      return;

    forum = sqlZap(forum);

    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(service, 
                   "org.lucane.applications.forumadmin",  "DELETE " + forum);
	  String line = (String)sc.read();
      if(! line.equals("OK"))
        DialogBox.error(tr("deleteError"));
      else
        getForumList();
    }
    catch(Exception e)
    {
      DialogBox.error(tr("deleteError"));
    }
  }

  private String sqlZap(String s)
  {
    String res = "";

    for(int i = 0; i < s.length(); i++)
    {
      if(s.charAt(i) == '\\')
        res += "\\\\";
      else if(s.charAt(i) == '\'')
        res += "\\'";
      else if(s.charAt(i) == ' ')
        res += "_";
      else
        res += s.charAt(i);
    }

    return res;
  }
}
