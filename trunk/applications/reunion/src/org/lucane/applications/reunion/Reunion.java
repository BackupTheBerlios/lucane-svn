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

  /* if the user started the reunion */
  public void start()
  {
    /* quit if no friends has been selected */
    if(friends.length == 0)
    {
      DialogBox.info(tr("noselect"));
      exit();
      return;
    }

    /* creation of the frame */
    dialog = new JFrame(getTitle());
	dialog.setIconImage(this.getImageIcon().getImage());
	dialog.addWindowListener(this);
	dialog.addKeyListener(this);
	dialog.getContentPane().setLayout(new BorderLayout());
	/* the subject */
    lblInfo = new JLabel(tr("subject"));
	dialog.getContentPane().add(lblInfo, BorderLayout.WEST);
    txtSubject = new JTextField(50);
    txtSubject.addKeyListener(this);
	dialog.getContentPane().add(txtSubject, BorderLayout.CENTER);
	/* send button */
    btnDlg = new JButton(tr("send"));
    btnDlg.addActionListener(this);
    dialog.getContentPane().add(btnDlg, BorderLayout.EAST);

    dialog.pack();
    dialog.setVisible(true);
  }

  /* if the user joined the reunion */
  public void follow()
  {
	/* creation of the frame */
    dialog = new JFrame(getTitle());
    dialog.addWindowListener(this);
    dialog.getContentPane().setLayout(new BorderLayout());
	/* the subject */
    lblInfo = new JLabel("[" + this.coordinator.getName() + "]");
	dialog.getContentPane().add(lblInfo, BorderLayout.WEST);
    txtSubject = new JTextField(50);
    txtSubject.setText(this.subject);
    txtSubject.setEditable(false);
	dialog.getContentPane().add(txtSubject, BorderLayout.CENTER);
	/* accept button */
    btnDlg = new JButton(tr("accept"));
    btnDlg.addActionListener(this);
    dialog.getContentPane().add(btnDlg, BorderLayout.EAST);

    dialog.pack();
    dialog.setVisible(true);
  }


  public void actionPerformed(ActionEvent ae)
  {
	/* if the user started the reunion, get the friends */
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

    /* hide the subject window */
    dialog.removeWindowListener(this);
    dialog.setVisible(false);
    
    /* show the main frame */
    initMainFrame();
    /* join the reunion */
    ReunionMessage msg = ReunionMessage.createJoinInstance(Client.getInstance().getMyInfos().getName());

	/* if the user started the reunion, send them the join message */
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
    /* else, just send the join message to the reunion creator */
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

    /* still asking for accept */
    if(frame == null)
    {
      exit();
      return;
    }

	ReunionMessage msg;
	/* if the user started the reunion, send all friends the end message */
    if(starter)
    {
      msg = ReunionMessage.createEndInstance();
      txtRead.addHTML(msg.toString(this));

      for(int i = 0; i < ocFriends.length; i++)
      {
        try  {
          ocFriends[i].write(msg);
        } catch(Exception e) { }
      }
      saveReunion();
    }
	/* else, send the reunion creator, the leave message */
    else
    {
      msg = ReunionMessage.createLeaveInstance(Client.getInstance().getMyInfos().getName());
      try {
        ocCoordinator.write(msg);
      } catch(Exception e) {}
    }

    exit();
  }

  private void initMainFrame()
  {
    /* creation of the frame */
    frame = new JFrame(this.subject);
    frame.addWindowListener(this);
    frame.setSize(640, 480);
    frame.getContentPane().setLayout(new BorderLayout());
    /* the send textbox */
    txtRead = new HTMLEditor();
    txtRead.setEditable(false);
	txtRead.setToolbarVisible(false);
	frame.getContentPane().add(new JScrollPane(txtRead), BorderLayout.CENTER);
    /* the friends */
    lstUsers = new JList();
    users = new DefaultListModel();
    lstUsers.setModel(users);
    frame.getContentPane().add(new JScrollPane(lstUsers), BorderLayout.EAST);

	txtRead.addHTML(ReunionMessage.createHTMLInfoMessage(this, tr("subjectMsg") + this.subject));

    if(this.starter)
    {
      addUser(Client.getInstance().getMyInfos().getName());
    }

	/* send button */
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
      /* if there is no message to send, stop */
	  if(txtSend.getText().equals(""))
		return;
      /* create the message to send */
      ReunionMessage msg =
        ReunionMessage.createTextInstance(
          Client.getInstance().getMyInfos().getName(),
          txtSend.getText());
        txtSend.clear();


      /* if the user started the reunion, send the message to all friends */
      if(starter)
      {
        txtRead.addHTML(msg.toString(this));

        for(int i = 0; i <ocFriends.length; i++)
        {
          try {
            ocFriends[i].write(msg);
          } catch(Exception e) {}
        }
      }
      /* else, send just to the reunion creator */
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
    //txtRead.addHTML(ReunionMessage.createHTMLInfoMessage(this, tr("joinMsg") + user));
	this.users.addElement(user);
	this.lstUsers.setModel(users);
  }

  public void delUser(String user)
  {
    //txtRead.addHTML(ReunionMessage.createHTMLInfoMessage(this, tr("leaveMsg") + user));
    this.users.removeElement(user);
    this.lstUsers.setModel(users);
  }

  public void end()
  {
    txtRead.addHTML(ReunionMessage.createHTMLInfoMessage(this, tr("endMsg")));
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
    try {
      process((ReunionMessage) o);
      if (starter)
        shareMessage((ReunionMessage) o);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

  private void shareMessage(ReunionMessage msg)
  {
    //share messages
    for(int j=0; j<ocFriends.length; j++)
    {
      try {
        ocFriends[j].write(msg);
      } catch(Exception e) {}
    }
  }

  private void process(ReunionMessage msg)
  {
    if(msg == null)
      return;

    //normal message
    if(msg.getType()==ReunionMessage.TYPE_TEXT)
    {
      txtRead.addHTML(msg.toString(this));
    }

    //command
    else
    {
      //JOIN
      if(msg.getType()==ReunionMessage.TYPE_JOIN)
      {
		txtRead.addHTML(msg.toString(this));
        String user = (String)(msg.getData());
        addUser(user);
      }

      //LEAVE
	  else if(msg.getType()==ReunionMessage.TYPE_LEAVE)
      {
		txtRead.addHTML(msg.toString(this));
		String user = (String)(msg.getData());
        delUser(user);
      }

      //END
	  else if(msg.getType()==ReunionMessage.TYPE_END)
      {
        end();
      }
      else
        txtRead.addHTML(msg.toString(this));
    }
  }

}

