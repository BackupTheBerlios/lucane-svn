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
package org.lucane.applications.filemanager;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class FileManager
  extends StandalonePlugin
  implements ActionListener,
             MouseListener
{

  private ConnectInfo service;
  private JFrame mainFrame;
  private JList filelist;
  private JButton btnMkdir;
  private JButton btnUpload;
  private JButton btnDownload;
  private JButton btnBrowse;
  private JFrame mkdirFrame;
  private JTextField mkdirTxt;
  private JButton mkdirBtn;
  private String currentPath;

  /**
   * Creates a new FileManager object.
   */
  public FileManager()
  {
    this.service = Communicator.getInstance().getConnectInfo("org.lucane.applications.filemanager");
    this.starter = true;
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new FileManager();
  }

  public void start()
  {
    showMainFrame();
    currentPath = "/";
    updateFileList();
  }

  public void actionPerformed(ActionEvent ae)
  {
    String selection = (String)filelist.getSelectedValue();

    if(selection != null && ae.getSource() == btnBrowse && 
       selection.charAt(selection.length() - 1) == '/')
    {

      if(selection.equals("../"))
      {
        currentPath = currentPath.substring(0, 
             currentPath.lastIndexOf("/", currentPath.length() - 2));
        currentPath += "/";
      }
      else
        currentPath += selection;

      updateFileList();
    }
    else if(ae.getSource() == btnMkdir)
      showMkdirDialog();
    else if(ae.getSource() == btnUpload)
      upload();
    else if(ae.getSource() == btnDownload && selection != null && selection.charAt(selection.length() - 1) != '/')
      download(selection);
    else if(ae.getSource() == mkdirBtn)
    {
      mkdir(mkdirTxt.getText());
      updateFileList();
      mkdirFrame.setVisible(false);
    }
  }

  public void mouseEntered(MouseEvent me)
  {
  }

  public void mouseExited(MouseEvent me)
  {
  }

  public void mousePressed(MouseEvent me)
  {
  }

  public void mouseReleased(MouseEvent me)
  {
  }

  public void mouseClicked(MouseEvent me)
  {
    if(me.getClickCount() >= 2)
    {
      int index = filelist.locationToIndex(me.getPoint());
      Object o = filelist.getModel().getElementAt(index);

      if(o == filelist.getSelectedValue())
      {
        String s = (String)o;

        //directory
		Logging.getLogger().fine("FileManager: s='" + s + "'");
        if(s.charAt(s.length() - 1) == '/')
        {
          if(s.equals("../"))
          {
            currentPath = currentPath.substring(0, 
                currentPath.lastIndexOf("/", currentPath.length() - 2));
            currentPath += "/";
          }
          else
            currentPath += s;

          updateFileList();
        }

        //file
        else
        {
          download(s);
        }
      }
    }
  }


  private void showMainFrame()
  {
    mainFrame = new JFrame(getTitle());
    mainFrame.addWindowListener(this);
    mainFrame.setSize(400, 300);
    mainFrame.getContentPane().setLayout(new BorderLayout());
    filelist = new JList();
    filelist.addMouseListener(this);
    mainFrame.getContentPane().add(new JScrollPane(filelist), BorderLayout.CENTER);
    btnMkdir = new JButton(tr("mkdir"));
    btnMkdir.addActionListener(this);
    btnUpload = new JButton(tr("upload"));
    btnUpload.addActionListener(this);
    btnDownload = new JButton(tr("download"));
    btnDownload.addActionListener(this);
    btnBrowse = new JButton(tr("goto"));
    btnBrowse.addActionListener(this);

    JPanel left = new JPanel();
    left.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    left.add(panel, BorderLayout.NORTH);
    panel.setLayout(new GridLayout(0, 1));
    panel.add(btnBrowse);
    panel.add(btnMkdir);
    panel.add(btnUpload);
    panel.add(btnDownload);
    mainFrame.getContentPane().add(left, BorderLayout.EAST);
	mainFrame.setIconImage(this.getImageIcon().getImage());
    mainFrame.setVisible(true);
  }


  private void showMkdirDialog()
  {
    mkdirFrame = new JFrame(tr("mkdir"));
    mkdirFrame.getContentPane().setLayout(new BorderLayout());
    mkdirFrame.getContentPane().add(new JLabel(tr("mkdirName")), BorderLayout.WEST);
    mkdirTxt = new JTextField();
    mkdirFrame.getContentPane().add(mkdirTxt, BorderLayout.CENTER);
    mkdirBtn = new JButton(tr("create"));
    mkdirBtn.addActionListener(this);
    mkdirFrame.getContentPane().add(mkdirBtn, BorderLayout.EAST);
    mkdirFrame.setSize(300, 50);
	mkdirFrame.setIconImage(this.getImageIcon().getImage());
    mkdirFrame.setVisible(true);
  }


  private void mkdir(String name)
  {
    name = name.replace(' ', '_');

    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(service, 
            "org.lucane.applications.filemanager", "MKDIR " + currentPath + name);
      String line = (String)sc.read();
	  Logging.getLogger().fine("FileManager::mkdir() LINE: " + currentPath + line);
      sc.close();
    }
    catch(Exception e)
    {
      DialogBox.error(tr("mkdirError"));
    }
  }


  private void upload()
  {
    JFileChooser fc = new JFileChooser();
    File file = null;
    int returnVal = fc.showOpenDialog(mainFrame);

    if(returnVal == JFileChooser.APPROVE_OPTION)
      file = fc.getSelectedFile();

    if(file == null)
      return;

	DataInputStream dis = null;
    try {    	
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, 
           "org.lucane.applications.filemanager", "SET_FILE " + currentPath + " " + 
           file.getName().replace(' ', '_'));
                 
      dis = new DataInputStream(new FileInputStream(file));
	  byte[] buf = new byte[dis.available()];
	  dis.readFully(buf);
	  oc.write(buf);
      oc.close();
    } catch(Exception e) {
      DialogBox.error(tr("uploadError"));
    } finally {
      try {
      	if(dis != null)
        	dis.close();
      } catch(IOException ioe) {}
    }

    updateFileList();
  }


  private void download(String source)
  {
    File file = null;
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(mainFrame);

    if(returnVal == JFileChooser.APPROVE_OPTION)
      file = fc.getSelectedFile();

    if(file == null)
      return;

	DataOutputStream dos = null;
    try {    	
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, 
          "org.lucane.applications.filemanager", "GET_FILE " + currentPath + " " + source);
      dos = new DataOutputStream(new FileOutputStream(file));
      byte[] buf =(byte[])oc.read();
      dos.write(buf);
      oc.close();
    } catch(Exception e) {
    } finally {
    	try {
    		if(dos != null)
    			dos.close();
    	} catch(IOException ioe) {}
    }

    updateFileList();
  }


  private void updateFileList()
  {
    try
    {
      ObjectConnection sc = Communicator.getInstance().sendMessageTo(service, 
            "org.lucane.applications.filemanager",  "LIST_DIR " + currentPath);
      Vector files = (Vector)sc.read();
      sc.close();

      if(! currentPath.equals("/"))
      {
		Logging.getLogger().fine("FileManager::updateFileList() PATH = '" + currentPath + "'");
        files.insertElementAt("../", 0);
      }

      filelist.setListData(files);
    }
    catch(Exception e)
    {
      DialogBox.error(tr("listError"));
    }
  }
}
