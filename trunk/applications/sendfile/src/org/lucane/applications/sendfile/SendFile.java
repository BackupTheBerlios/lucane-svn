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
package org.lucane.applications.sendfile;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;


public class SendFile
  extends Plugin
  implements ActionListener
{

  private ConnectInfo friend;
  private String filename;
  private ManagedWindow dialog;
  private JTextField txtWho;
  private JTextField txtWhat;
  private JButton btnDlg;
  private JButton btnChoose;
  private JFileChooser fc;
  private JLabel lblNewFile;
  private File file;
  private ObjectConnection sc;

  public SendFile()
  {
  }


  public Plugin newInstance(ConnectInfo[] friends, boolean starter)
  {
	Logging.getLogger().finer("SendFile:init() starter=" + starter);
    if(friends.length > 0)
      return new SendFile(friends[0], starter);
    else
      return new SendFile(null, starter);
  }

  public SendFile(ConnectInfo friend, boolean starter)
  {
    this.friend = friend;
    this.starter = starter;
  }

  public void load(ObjectConnection friend, ConnectInfo who, String filename)
  {
    this.sc = friend;
    this.friend = who;
    this.filename = filename;
  }

  public void start()
  {

    /* selection check */
    if(this.friend == null)
    {
      DialogBox.info(tr("noselect"));
      exit();
      return;
    }

    /* chose file to send */
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showOpenDialog(null);

    if(returnVal == JFileChooser.APPROVE_OPTION)
    {
      file = fc.getSelectedFile();

      try
      {
        filename = file.getName();
        sc = Communicator.getInstance().sendMessageTo(this.friend, 
                       this.getName(),  filename);

        String line = null;

        try
        {
          line = (String)sc.read();
        }
        catch(Exception e)
        {
			//file refused
        }

        if(line == null || ! line.equals("ACCEPT"))
          DialogBox.error(tr("reject1") + this.friend.getName() + " " + tr("reject2") + " " + 
                this.filename + tr("reject3"));
        else
        {
          sendFile();
          DialogBox.info(tr("sent1") + " " + filename + " " + tr("sent2"));
          exit();
        }

        this.sc.close();
      }
      catch(Exception e)
      {
        DialogBox.error(tr("sendError"));
      }
    }
  }


  public void actionPerformed(ActionEvent ae)
  {
    JButton jb = (JButton)ae.getSource();

    /* reject file */
    if(jb.getText().equals(tr("reject")))
    {
      try
      {
        sc.write("REJECT");
		Logging.getLogger().finer("SendFile::REJECT");
        sc.close();
        dialog.dispose();
        exit();
      }
      catch(Exception e)
      {
        //pheeeesh...
      }
    }

    /* save file */
    else if(jb.getText().equals(tr("save")))
    {
      fc = new JFileChooser();

      int returnVal = fc.showSaveDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION)
      {
        file = fc.getSelectedFile();

		DataOutputStream dos = null;
        try
        {
          sc.write("ACCEPT");
		  Logging.getLogger().finer("SendFile::ACCEPT");
          dialog.dispose();

          dos = new DataOutputStream(new FileOutputStream(new File(file.getPath())));
          byte[] buf = (byte[])sc.read();
          dos.write(buf);

          sc.close();
          DialogBox.info(tr("get1") + " " + filename + " " + tr("get2"));
          exit();
        }
        catch(Exception e)
        {
          DialogBox.error(tr("getError"));
        }
        finally {
        	if(dos != null) {
        		try {
        			dos.close();
        		} catch(IOException ioe) {}
        	}
        }
      }
    }
  }

  public void follow()
  {
    dialog = new ManagedWindow(this, getTitle());
    dialog.setExitPluginOnClose(true);
    dialog.getContentPane().setLayout(new FlowLayout());
    lblNewFile = new JLabel( "[" + friend.getName() + "] " + this.filename);
    btnDlg = new JButton(tr("reject"));
    btnDlg.addActionListener(this);
    btnChoose = new JButton(tr("save"));
    btnChoose.addActionListener(this);
    dialog.getContentPane().add(lblNewFile);
    dialog.getContentPane().add(btnDlg);
    dialog.getContentPane().add(btnChoose);
	dialog.setIconImage(this.getImageIcon().getImage());
    dialog.show();
  }


  private void sendFile() throws Exception
  {
    DataInputStream dis = null;
    try { 
        dis = new DataInputStream(new FileInputStream(this.file.getPath()));
    	byte[] buf = new byte[dis.available()];
    	dis.readFully(buf);
    	sc.write(buf);
    	sc.close();
    } finally {
    	if(dis != null)
    		dis.close();
    }
   }

  public void windowClosing(WindowEvent we)
  {
    try
    {
      if(! starter && sc != null)
        sc.close();
    }
    catch(Exception e)
    {
      //we can't do much here
    }

    exit();
  }
}
