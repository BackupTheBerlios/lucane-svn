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

/**
* The help browser plugin
*/
public class HelpBrowser extends StandalonePlugin
implements TreeSelectionListener, ActionListener
{
	//-- widgets
	private JFrame frame;
	private JTree sections;
	private JEditorPane bighelp;
	private JTextArea minihelp;
	private HelpParser parser;
	private JComboBox plugins;
	
	/**
	 * Constructor
	 */
	public HelpBrowser()
	{
		this.starter = true;
	}
	
	/**
	 * Init for plugin loader
	 */
	public Plugin init(ConnectInfo[] friends, boolean starter)
	{
		return new HelpBrowser();
	}
	
	/**
	 * Plugin started
 	 */
	public void start()
	{
		frame = new JFrame(getTitle());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setName("frame");
		
		bighelp = new JEditorPane();
		bighelp.setEditable(false);
		bighelp.setContentType("text/html");
		minihelp = new JTextArea("");
		minihelp.setEditable(false);
		bighelp.addHyperlinkListener(new LinkListener(this, bighelp, minihelp));
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
				new JScrollPane(bighelp),
				new JScrollPane(minihelp));
		split.setOneTouchExpandable(true);
		split.setName("split");
		frame.getContentPane().add(split, BorderLayout.CENTER);
		sections = new JTree(new DefaultMutableTreeNode());
		sections.setRootVisible(false);
		sections.setEditable(false);
		sections.addTreeSelectionListener(this);
		frame.getContentPane().add(sections, BorderLayout.WEST);
		plugins = new JComboBox();
		plugins.addActionListener(this);
		
		Iterator i = PluginLoader.getInstance().getPluginIterator();
		while(i.hasNext())
			plugins.addItem(i.next());
		
		frame.getContentPane().add(plugins, BorderLayout.NORTH);
		frame.setSize(600, 400);
		frame.setIconImage(this.getImageIcon().getImage());
		frame.show();
		split.setDividerLocation(310);
	}


	//-- listeners
	
	public void actionPerformed(ActionEvent ae)
	{
		Plugin p = (Plugin)plugins.getSelectedItem();
		if(p != null)
			showHelp(p);
	}

	public void valueChanged(TreeSelectionEvent tse)
	{
		TreePath tp = sections.getSelectionPath();
		
		if(tp != null)
			showHelpSection(tp);
	}
	
	//-- useful methods
	
	/**
	 * Show the help for a plugin
     */
	public void showHelp(Plugin p)
	{
		try
		{
			parser = new HelpParser(p.getDirectory() + "help/", tr("file"));
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
	
	public void hidePluginList()
	{
		this.plugins.setVisible(false);
	}
	
	/**
	 * Go to a particular section
     */	
	public void gotoSection(String section)
	{
		TreeNode root = (TreeNode)sections.getModel().getRoot();
		TreePath path = new TreePath(root);
		path = findSection(path, section);
		
		if(path != null)
		{
			sections.setSelectionPath(path);
			showHelpSection(path);
		}
	}
	
	/**
	 * Show a section
     */
	private void showHelpSection(TreePath tp)
	{
		bighelp.setText(parser.htmlForSection(tp));
		bighelp.setCaretPosition(0);
	}	
	
	/**
     * Find a section in the tree
	 */
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