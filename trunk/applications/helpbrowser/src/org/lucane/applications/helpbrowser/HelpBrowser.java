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
package org.lucane.applications.helpbrowser;

import org.lucane.client.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;


public class HelpBrowser
  extends StandalonePlugin
  implements TreeSelectionListener,
             ActionListener
{

  JFrame frame;
  JTree sections;
  JEditorPane bighelp;
  JTextArea minihelp;
  HelpParser parser;
  PluginLoader ploader;
  JComboBox plugins;

  public HelpBrowser()
  {
    this.starter = true;
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new HelpBrowser();
  }

  public void start()
  {
    ploader = PluginLoader.getInstance();
    frame = new JFrame(getTitle());
    frame.getContentPane().setLayout(new BorderLayout());
    bighelp = new JEditorPane();
    bighelp.setEditable(false);
    bighelp.setContentType("text/html");
    minihelp = new JTextArea("");
    minihelp.setEditable(false);
    bighelp.addHyperlinkListener(new LinkListener(this, bighelp, minihelp));

    JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                    new JScrollPane(bighelp), 
                                    new JScrollPane(minihelp));
    jsp.setOneTouchExpandable(true);
    frame.getContentPane().add(jsp, BorderLayout.CENTER);
    sections = new JTree(new DefaultMutableTreeNode());
    sections.setRootVisible(false);
    sections.setEditable(false);
    sections.addTreeSelectionListener(this);
    frame.getContentPane().add(sections, BorderLayout.WEST);
    plugins = new JComboBox();
    plugins.addActionListener(this);

    Iterator i = PluginLoader.getInstance().getPluginIterator();
    while(i.hasNext())
    {
    	Plugin p = (Plugin)i.next();    	
        plugins.addItem(p.getName());
    }

    frame.getContentPane().add(plugins, BorderLayout.NORTH);
    frame.setSize(600, 400);
	frame.setIconImage(this.getImageIcon().getImage());
    frame.show();
    jsp.setDividerLocation(0.80);
  }

  public void actionPerformed(ActionEvent ae)
  {
    int idx = plugins.getSelectedIndex();

    if(idx >= 0)
    {
      try
      {
        Plugin p = ploader.getPlugin((String)plugins.getSelectedItem());
        parser = new HelpParser(p.getDirectory(), tr("file"));
        sections.setModel(new DefaultTreeModel(parser.getSections()));
        bighelp.setText("");

        //select first element
        TreeNode root = (TreeNode)sections.getModel().getRoot();
        TreePath path = new TreePath(root);
        path = path.pathByAddingChild(((DefaultMutableTreeNode)root).getFirstChild());
        sections.setSelectionPath(path);
        valueChanged(null);
      }
      catch(Exception e)
      {
        sections.setModel(new DefaultTreeModel(null));
        bighelp.setText("<html><head></head><body>" + tr("nohelp") + "</body></html>");
      }

      minihelp.setText("");
    }
  }

  public void valueChanged(TreeSelectionEvent tse)
  {
    TreePath tp = sections.getSelectionPath();

    if(tp != null)
      bighelp.setText(parser.htmlForSection(tp));
  }

  public void gotoSection(String section)
  {
    TreeNode root = (TreeNode)sections.getModel().getRoot();
    TreePath path = new TreePath(root);
    path = findSection(path, section);

    if(path != null)
    {
      sections.setSelectionPath(path);
      valueChanged(null);
    }
  }

  private TreePath findSection(TreePath path, String section)
  {
    TreePath result = null;
    DefaultMutableTreeNode n = (DefaultMutableTreeNode)path.getLastPathComponent();

    if(section.equals(n.getUserObject()))
      return path;

    TreePath newpath;

    for(int i = 0; i < n.getChildCount(); i++)
    {
      newpath = path.pathByAddingChild(n.getChildAt(i));
      result = findSection(newpath, section);

      if(result != null)
        break;
    }

    return result;
  }
}