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
package org.lucane.applications.pluginsinfos;

import org.lucane.client.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Iterator;

import javax.swing.*;


public class PluginsInfos
  extends StandalonePlugin
  implements ActionListener
{

  private JFrame frame;
  JTextField name; // + version
  JTextField author; // + email
  JTextField tooltip;
  JTextField category;
  JTextField title;
  JPanel infos;
  JLabel lblIcon;
  JComboBox plugins;
  PluginLoader ploader;

  public PluginsInfos()
  {
    this.starter = true;
  }

  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new PluginsInfos();
  }

  public void start()
  {
    ploader = PluginLoader.getInstance();

    JPanel jpmain = new JPanel();
    jpmain.setLayout(new BorderLayout());
    frame = new JFrame(tr("ftitle"));
    frame.addWindowListener(this);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(jpmain, BorderLayout.CENTER);
    plugins = new JComboBox();
    plugins.addActionListener(this);
    jpmain.add(plugins, BorderLayout.NORTH);
    name = new JTextField();
    name.setEditable(false);
    author = new JTextField();
    author.setEditable(false);
    tooltip = new JTextField();
    tooltip.setEditable(false);
    category = new JTextField();
    category.setEditable(false);
    title = new JTextField();
    title.setEditable(false);
    infos = new JPanel();

    JPanel labels = new JPanel();
    JPanel texts = new JPanel();
    infos.setLayout(new BorderLayout());
    labels.setLayout(new GridLayout(0, 1));
    texts.setLayout(new GridLayout(0, 1));
    infos.add(labels, BorderLayout.WEST);
    infos.add(texts, BorderLayout.CENTER);
    labels.add(new JLabel(tr("fname")));
    texts.add(name);
    labels.add(new JLabel(tr("author")));
    texts.add(author);
    labels.add(new JLabel(tr("category")));
    texts.add(category);
    labels.add(new JLabel(tr("title")));
    texts.add(title);
    labels.add(new JLabel(tr("tooltip")));
    texts.add(tooltip);
    lblIcon = new JLabel(tr("noicon"));
    infos.add(lblIcon, BorderLayout.EAST);
    jpmain.add(infos, BorderLayout.CENTER);

    Iterator i = PluginLoader.getInstance().getPluginIterator();
    while(i.hasNext())
    {
    	Plugin p = (Plugin)i.next();   
        plugins.addItem(p.getName());
    }

    frame.setSize(340, 150);
	frame.setIconImage(this.getImageIcon().getImage());
    frame.setVisible(true);
  }

  public void actionPerformed(ActionEvent ae)
  {
    int idx = plugins.getSelectedIndex();

    if(idx >= 0)
    {
    	Plugin p = ploader.getPlugin((String)plugins.getSelectedItem());
      name.setText(p.getName() + " - " + p.getVersion());
      author.setText(p.getAuthor() + " <" + p.getEmail() + ">");
      category.setText(p.getCategory());
      title.setText(p.getTitle());
      tooltip.setText(p.getToolTip());

      ImageIcon iic = null;

      try
      {
        iic = new ImageIcon(new URL(p.getDirectory() + p.getIcon()));
        iic = new ImageIcon(iic.getImage().getScaledInstance(32, 32,  Image.SCALE_SMOOTH));
        lblIcon.setText("");
        lblIcon.setIcon(iic);
      }
      catch(Exception e)
      {
        lblIcon.setText(tr("noicon"));
      }
    }
  }
}