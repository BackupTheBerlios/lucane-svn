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
package org.lucane.applications.reunion;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.*;
import javax.swing.*;

public class Reunion
extends Plugin
implements ActionListener, KeyListener, ObjectListener
{
  private ConnectInfo coordinator;
  private ConnectInfo[] friends;
  private String subject;
 
  protected ObjectConnection ocCoordinator;
  protected ObjectConnection[] ocFriends;

  private JFrame dialog;
  private JLabel lblInfo;
  private JTextField txtSubject;
  private JButton btnDlg;
  private JFrame frame;
  private HTMLEditor txtSend;
  private JList lstUsers;
  private DefaultListModel users;
  HTMLEditor txtRead;
  private boolean exited;

  public Reunion()
  {
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new Reunion(friends, starter);
  }

  public Reunion(ConnectInfo[] friends, boolean starter)
  {
    this.starter = starter;
    this.friends = friends;
    this.exited = false;
  }

  public void load(ObjectConnection oc, ConnectInfo who, String data)
  {
    this.coordinator = who;
    this.subject = data;
    ocCoordinator = oc;
  }


  public void start()
  {
    if(friends.length == 0)
    {
      DialogBox.info(tr("noselect"));
      exit();
      return;
    }

    dialog = new JFrame(getTitle());
    dialog.addWindowListener(this);
    dialog.getContentPane().setLayout(new BorderLayout());
    lblInfo = new JLabel(tr("subject"));
    txtSubject = new JTextField(50);
    txtSubject.addKeyListener(this);
    dialog.addKeyListener(this);
    btnDlg = new JButton(tr("send"));
    btnDlg.addActionListener(this);
    dialog.getContentPane().add(lblInfo, BorderLayout.WEST);
    dialog.getContentPane().add(txtSubject, BorderLayout.CENTER);
    dialog.getContentPane().add(btnDlg, BorderLayout.EAST);
    dialog.pack();
	dialog.setIconImage(this.getImageIcon().getImage());
    dialog.setVisible(true);
  }

  public void follow()
  {
    dialog = new JFrame(getTitle());
    dialog.addWindowListener(this);
    dialog.getContentPane().setLayout(new BorderLayout());
    lblInfo = new JLabel("[" + this.coordinator.getName() + "]");
    txtSubject = new JTextField(50);
    txtSubject.setText(this.subject);
    txtSubject.setEditable(false);
    btnDlg = new JButton(tr("accept"));
    btnDlg.addActionListener(this);
    dialog.getContentPane().add(lblInfo, BorderLayout.WEST);
    dialog.getContentPane().add(txtSubject, BorderLayout.CENTER);
    dialog.getContentPane().add(btnDlg, BorderLayout.EAST);
    dialog.pack();
    dialog.setVisible(true);
  }


  public void actionPerformed(ActionEvent ae)
  {
    if(this.starter)
    {
      this.subject = txtSubject.getText();
      ocFriends = new ObjectConnection[friends.length];

      for(int i = 0; i < friends.length; i++)
      {
        try
        {
          ocFriends[i] = Communicator.getInstance().sendMessageTo(friends[i], 
                                 this.getName(), this.subject);
        }
        catch(Exception e)
        {
          e.printStackTrace();
		  Logging.getLogger().warning(tr("contactError"));
        }
      }
    }

    dialog.removeWindowListener(this);
    dialog.setVisible(false);
    initMainFrame();
    String msg = "#JOIN# " + Client.getInstance().getMyInfos().getName();

    if(starter)
    {
      for(int i = 0; i < ocFriends.length; i++)
      {
        try  {
          ocFriends[i].addObjectListener(this);
          ocFriends[i].listen();
          ocFriends[i].write(msg);
        } catch(Exception e) {}
      }
    }
    else
    {
    	try {
          ocCoordinator.addObjectListener(this);
          ocCoordinator.listen();
          ocCoordinator.write(msg);
    	} catch(Exception e) {}
    }
  }

  public void windowClosing(WindowEvent we)
  {
    if(exited)
      return;
    else
      exited = true;

    //still asking for accept
    if(frame == null)
    {
      exit();
      return;
    }

    String msg;
    if(starter)
    {
      msg = "#END#";
      txtRead.addHTML(createHTMLInfoMessage(tr("endMsg")));

      for(int i = 0; i < ocFriends.length; i++)
      {
        try  {
          ocFriends[i].write(msg);
        } catch(Exception e) { }
      }
      saveReunion();
    }
    else
    {
      msg = "#LEAVE# " + Client.getInstance().getMyInfos().getName();
      try {
        ocCoordinator.write(msg);
      } catch(Exception e) {}
    }

    exit();
  }

  private void initMainFrame()
  {
    frame = new JFrame(this.subject);
    frame.addWindowListener(this);
    frame.setSize(640, 480);
    frame.getContentPane().setLayout(new BorderLayout());
    txtRead = new HTMLEditor();
    txtRead.setEditable(false);
	txtRead.setToolbarVisible(false);
    //txtRead.setLineWrap(true);
    //txtRead.setWrapStyleWord(true);
    lstUsers = new JList();
    users = new DefaultListModel();
    lstUsers.setModel(users);
    frame.getContentPane().add(new JScrollPane(lstUsers), BorderLayout.EAST);

    if(this.starter)
    {
      txtRead.addHTML(createHTMLInfoMessage(tr("subjectMsg") + this.subject));
      addUser(Client.getInstance().getMyInfos().getName());
    }

    frame.getContentPane().add(new JScrollPane(txtRead), BorderLayout.CENTER);
    txtSend = new HTMLEditor();
    txtSend.getEditorPane().addKeyListener(this);
    frame.getContentPane().add(txtSend, BorderLayout.SOUTH);
    frame.addWindowListener(this);
    frame.setVisible(true);
  }


  public void keyPressed(KeyEvent ke)
  {
    if(ke.getKeyCode() == KeyEvent.VK_ENTER)
    {
      /* prevents the event to be catched elsewhere */
      ke.consume();

      /* dialog box */
      if(frame == null)
      {
        actionPerformed(null);

        return;
      }

      /* mainfraime */
	  String msg = createHTMLUserMessage(
                   Client.getInstance().getMyInfos().getName(),
                   txtSend.getText());
      txtSend.clear();

      if(msg.equals(""))

        return;

      //special char
      if(msg.charAt(0) == '#')
        msg = '#' + msg;

      if(starter)
      {
        txtRead.addHTML(msg);

        for(int i = 0; i <ocFriends.length; i++)
        {
          try {
            ocFriends[i].write(msg);
          } catch(Exception e) {}
        }
      }
      else
      {
        try {
          ocCoordinator.write(msg);
        } catch(Exception e) {}
      }
    }
  }

  public void keyReleased(KeyEvent e)
  {
  }

  public void keyTyped(KeyEvent e)
  {
  }

  public void addUser(String user)
  {
    txtRead.addHTML(createHTMLInfoMessage(tr("joinMsg") + user));
    this.users.addElement(user);
    this.lstUsers.setModel(users);
  }

  public void delUser(String user)
  {
    txtRead.addHTML(createHTMLInfoMessage(tr("leaveMsg") + user));
    this.users.removeElement(user);
    this.lstUsers.setModel(users);
  }

  public void end()
  {
    txtRead.addHTML(createHTMLInfoMessage(tr("endMsg")));
    txtSend.getEditorPane().removeKeyListener(this);

    if(starter)
    {
      for(int i = 0; i < ocFriends.length; i++)
           ocFriends[i].close();
    }
    else
      ocCoordinator.close();
  }

  private void saveReunion()
  {
  	try
	{
  		subject = subject.replace(' ', '_');

  		Calendar cal = Calendar.getInstance();
  		String time = "_" + cal.get(Calendar.DAY_OF_MONTH) + "-" +
		(cal.get(Calendar.MONTH) + 1) + "-" +
		cal.get(Calendar.YEAR);
  		ConnectInfo fileService =
  			Communicator.getInstance().getConnectInfo("org.lucane.applications.filemanager");
  		ObjectConnection sc =
  			Communicator.getInstance().sendMessageTo(fileService,
  					"org.lucane.applications.filemanager", "SET_FILE /Reunion/ " +
					subject + time + ".html");

  		sc.write(txtRead.getText().getBytes());
  		sc.close();
  	}
  	catch(Exception e)
	{
  		DialogBox.error(tr("saveError"));
  	}
  }
  
  //-- network object management
 
  public void objectRead(Object o)
  {
    process((String)o);
    if(starter)
      shareMessage((String)o);
  }

  private void shareMessage(String msg)
  {
    //share messages
    for(int j=0; j<ocFriends.length; j++)
    {
      try {
        ocFriends[j].write(msg);
      } catch(Exception e) {}
    }
  }

  private void process(String msg)
  {
    if(msg == null || msg.equals(""))
      return;

    //normal message
    if(msg.charAt(0) != '#')
    {
      txtRead.addHTML(createHTMLInfoMessage(msg));
    }

    //command
    else
    {
      //JOIN
      if(msg.charAt(1) == 'J')
      {
        String user = msg.substring(msg.indexOf(" ") + 1);
        addUser(user);
      }

      //LEAVE
      else if(msg.charAt(1) == 'L')
      {
        String user = msg.substring(msg.indexOf(" ") + 1);
        delUser(user);
      }

      //END
      else if(msg.charAt(1) == 'E')
      {
        end();
      }
      else
        txtRead.addHTML(createHTMLInfoMessage(msg.substring(1)));
    }
  }

  private String createHTMLUserMessage(String user, String message) {
    String res;
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
      /*"<TABLE STYLE=\"padding:1px;margin:1px; border-width:1px; border-style:solid; border-color:#ccccaa; background-color:#ffffee;width:100%;\">\n"
        + "<TR><TD STYLE=\"width:50px;font-size:12px;font-weight:bold;\">\n"
        + user
        + "</TD>\n"
        + "<TD STYLE=\"font-size:10px;border-width:1px; border-style:solid; border-color:#ccccaa; background-color:#ffffff;\">\n"
        + message
        + "\n</TD></TR></TABLE>\n";*/
    return res;
  }

  private String createHTMLInfoMessage(String message) {
    String res;
    res =
      "<DIV><FONT SIZE=4 COLOR=#888888>"
        + message
        + "</FONT></DIV>";
    return res;
  }
}
