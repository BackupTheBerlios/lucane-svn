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
package org.lucane.applications.forum;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;


public class Forum
  extends StandalonePlugin
  implements ActionListener,
             ListSelectionListener,
             TreeSelectionListener
{

  /* widgets : main window */
  private JFrame mainFrame;
  private JTextField txtTitle;
  private JTextField txtDate;
  private JTextField txtAuthor;
  private HTMLEditor txtMessage;
  private JButton btnRefresh;
  private JButton btnNew;
  private JButton btnAnswer;
  private JList lstForums;
  private JTree treeTopics;
  private TreePath lastSelected;

  /* widgets : new post */
  public JFrame postFrame;
  private JTextField txtPostTitle;
  private HTMLEditor txtPostMessage;
  private JButton btnPost;
  private ConnectInfo ci;
  private Vector messages;
  private String idref;

  public Forum()
  {
    this.starter = true;
    this.ci = Communicator.getInstance().getConnectInfo("org.lucane.applications.forum");
    this.messages = new Vector();
    lastSelected = null;
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new Forum();
  }

  public void start()
  {
    mainFrame = new JFrame(tr("title"));
    lstForums = new JList();
    treeTopics = new JTree(new Vector());
    btnRefresh = new JButton(tr("refresh"));
    btnNew = new JButton(tr("new"));
    btnAnswer = new JButton(tr("answer"));
    txtAuthor = new JTextField();
    txtDate = new JTextField();
    txtTitle = new JTextField();
    txtMessage = new HTMLEditor();
    txtAuthor.setEditable(false);
    txtDate.setEditable(false);
    txtTitle.setEditable(false);
    txtMessage.setEditable(false);
    txtMessage.setToolbarVisible(false);
    btnRefresh.addActionListener(this);
    btnNew.addActionListener(this);
    btnAnswer.addActionListener(this);
    lstForums.addListSelectionListener(this);
    treeTopics.addTreeSelectionListener(this);
    treeTopics.setRootVisible(false);

    JSplitPane haut = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JPanel bas = new JPanel();
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, haut, bas);
    split.setOneTouchExpandable(true);
    mainFrame.getContentPane().setLayout(new BorderLayout());
    mainFrame.getContentPane().add(split);
    haut.setLeftComponent(new JScrollPane(lstForums));
    haut.setRightComponent(new JScrollPane(treeTopics));
    haut.setOneTouchExpandable(true);
    bas.setLayout(new BorderLayout());

    JPanel left = new JPanel();
    JPanel right = new JPanel();
    JPanel infos = new JPanel();
    JPanel buttons = new JPanel();
    JPanel labels = new JPanel();
    JPanel texts = new JPanel();
    bas.add(left, BorderLayout.CENTER);
    bas.add(right, BorderLayout.EAST);
    infos.setLayout(new BorderLayout());
    infos.add(labels, BorderLayout.WEST);
    infos.add(texts, BorderLayout.CENTER);
    labels.setLayout(new GridLayout(0, 1));
    texts.setLayout(new GridLayout(0, 1));
    labels.add(new JLabel(tr("mauthor")));
    texts.add(txtAuthor);
    labels.add(new JLabel(tr("mtitle")));
    texts.add(txtTitle);
    labels.add(new JLabel(tr("mdate")));
    texts.add(txtDate);
    left.setLayout(new BorderLayout());
    left.add(infos, BorderLayout.NORTH);
    left.add(txtMessage);
    buttons.setLayout(new GridLayout(0, 1));
    buttons.add(btnRefresh);
    buttons.add(btnNew);
    buttons.add(btnAnswer);
    right.setLayout(new BorderLayout());
    right.add(buttons, BorderLayout.NORTH);
    mainFrame.setSize(600, 400);
    mainFrame.addWindowListener(this);
	mainFrame.setIconImage(this.getImageIcon().getImage());
    mainFrame.setVisible(true);
    split.setDividerLocation(0.5);
    haut.setDividerLocation(0.25);
    getForumList();
  }

  public void actionPerformed(ActionEvent ae)
  {
    if(ae.getSource() == btnRefresh)
    {
       if(lstForums.getSelectedValue() == null)
         DialogBox.error(tr("noforumselected"));
       else
         getForum((String)lstForums.getSelectedValue());
    }
    else if(ae.getSource() == btnNew)
    {
      if(lstForums.getSelectedValue() == null)
        DialogBox.error(tr("noforumselected"));
      else
        showPostFrame(null);
    }
    else if(ae.getSource() == btnAnswer)
    {
      try
      {
        TreePath tp = treeTopics.getSelectionPath();

        if(tp != null)
        {
          ForumMessage msg = (ForumMessage)((DefaultMutableTreeNode)tp.getLastPathComponent()).getUserObject();
          showPostFrame(msg);
        }
        else
          DialogBox.error(tr("nomessageselected"));
      }
      catch(Exception e)
      {
		Logging.getLogger().warning(tr("errselect"));
      }
    }
    else if(ae.getSource() == btnPost)
    {
      try
      {
        //<idref> <auteur> <forum>\001<titre>\001<contenu>
        ObjectConnection sc = Communicator.getInstance().sendMessageTo(ci, "org.lucane.applications.forum", 
                 "POST " + idref +  " " + Client.getInstance() .getMyInfos().getName() + " " + 
                  sqlZap((String)lstForums .getSelectedValue()) + "\001" + sqlZap(txtPostTitle.getText()) + 
                   "\001" + sqlZap(txtPostMessage.getText()).replace('\n', '\001'));
        sc.close();
        postFrame.setVisible(false);
        postFrame = null;
        getForum((String)lstForums.getSelectedValue());
      }
      catch(Exception e)
      {
        DialogBox.error(tr("errpost") + e);
      }
    }
  }


  public void valueChanged(ListSelectionEvent lse)
  {
    if(lstForums.getSelectedValue() != null)
    {
      getForum((String)lstForums.getSelectedValue());

      if(lastSelected != null)
        treeTopics.setSelectionPath(lastSelected);
    }
  }


  public void valueChanged(TreeSelectionEvent tse)
  {
    try
    {
      lastSelected = treeTopics.getSelectionPath();

      if(lastSelected != null)
      {

        ForumMessage msg = (ForumMessage)((DefaultMutableTreeNode)lastSelected.getLastPathComponent()).getUserObject();
        getMessage(msg.id);
        txtTitle.setText(msg.title);
        txtDate.setText(msg.date);
        txtAuthor.setText(msg.author);
      }
    }
    catch(Exception e)
    {
		Logging.getLogger().warning(tr("errselect"));
    }
  }

  private void getForumList()
  {
    try
    {
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(ci, "org.lucane.applications.forum",  "LIST");
      Vector v = (Vector)oc.read();
		oc.close();

      lstForums.setListData(v);
    }
    catch(Exception e)
    {
      //no forum
    }
  }

  private void getForum(String forum)
  {
	Logging.getLogger().finer("Forum::getForum '" + forum + "'");

    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(ci, "org.lucane.applications.forum", 
                          "GET_FORUM " + forum);
      Vector v = (Vector)sc.read();
      messages = new Vector(v.size());
      for(int i=0;i<v.size();i++)
      	messages.addElement(new ForumMessage((String)v.elementAt(i)));
      	
      DefaultMutableTreeNode root = new DefaultMutableTreeNode();
      updateTree("0", root, messages);
      treeTopics.setModel(new DefaultTreeModel(root));
    }
    catch(Exception e)
    {
    	e.printStackTrace();
      //pas de forum...
    }
  }


  private void updateTree(String idmsg, DefaultMutableTreeNode node, 
                              Vector messages)
  {
    for(int i = 0; i < messages.size(); i++)
    {
      ForumMessage msg = (ForumMessage)messages.elementAt(i);

      if(msg.idref.equals(idmsg))
      {

        DefaultMutableTreeNode n = new DefaultMutableTreeNode(msg);
        node.add(n);
        updateTree(msg.id, n, messages);
      }
    }
  }


  private void getMessage(String id)
  {
	Logging.getLogger().finer("Forum::getMessage '" + id + "'");

    try
    {

      ObjectConnection sc = Communicator.getInstance().sendMessageTo(ci, "org.lucane.applications.forum", "GET_MESSAGE " + id);
      String line = (String)sc.read();
      sc.close();
      line = line.replace('\001', '\n');
      txtMessage.setText(line);
    }
    catch(Exception e)
    {
      //no message
    }
  }


  private void showPostFrame(ForumMessage msg)
  {
    if(postFrame != null)
    {
      DialogBox.error(tr("frameopened"));
      return;
    }

    postFrame = new JFrame(tr("newmsg"));
    txtPostTitle = new JTextField();
    btnPost = new JButton(tr("send"));
    txtPostMessage = new HTMLEditor();

    JPanel haut = new JPanel();
    haut.setLayout(new BorderLayout());
    haut.add(txtPostTitle, BorderLayout.CENTER);
    haut.add(btnPost, BorderLayout.EAST);
    btnPost.addActionListener(this);
    postFrame.setSize(300, 400);
    postFrame.addWindowListener(new WinListener(this));
    postFrame.getContentPane().setLayout(new BorderLayout());
    postFrame.getContentPane().add(haut, BorderLayout.NORTH);
    postFrame.getContentPane().add(txtPostMessage,  BorderLayout.CENTER);
    idref = "0";

    if(msg != null)
    {
      txtPostTitle.setText(msg.title);
      idref = msg.id;
    }
	
	postFrame.setIconImage(this.getImageIcon().getImage());
    postFrame.setVisible(true);
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
      else
        res += s.charAt(i);
    }

    return res;
  }
}
