package org.lucane.applications.jmail.base;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of JMail                                              *
 * Copyright (C) 2002-2003 Yvan Norsa <norsay@wanadoo.fr>                  *
 *                                                                         *
 * JMail is free software; you can redistribute it and/or modify           *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * any later version.                                                      *
 *                                                                         *
 * JMail is distributed in the hope that it will be useful,                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with JMail; if not, write to the Free Software Foundation, Inc.,        *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.awt.event.*;
import java.util.*;
import javax.mail.*;
import javax.swing.*;
import javax.swing.tree.*;

/** Create a new folder */
final class NewFolderFrame extends JFrame
{
    private JPanel panel;

    private JTree foldersTree;
    private JScrollPane scrollPane;

    private JTextField folderName;

    private JCheckBox folders;
    private JCheckBox messages;

    private JButton ok;
    private JButton cancel;

    private NewFolderListener listener;

    /** User profile */
    private Profile profile;

    private Store store;

    /** Language resource */
    private ResourceBundle msgBundle;

    /** Constructor
     *  @param store the connection
     *  @param profile user profile
     *  @param msgBundle language resource
     */
    protected NewFolderFrame(Store store, Profile profile, ResourceBundle msgBundle)
    {
	super(msgBundle.getString("NewFolder.frameTitle"));

	this.msgBundle = msgBundle;

	this.store = store;

	this.profile = profile;

	init();

	pack();
	setVisible(true);
    }

    /** Alternate constructor
     *  @param store the connection
     *  @param profile user profile
     *  @param row tells the row number
     *  @param msgBundle language resource
     */
    protected NewFolderFrame(Store store, Profile profile, int row, ResourceBundle msgBundle)
    {
	super(msgBundle.getString("NewFolder.frameTitle"));

	this.msgBundle = msgBundle;

	this.store = store;

	this.profile = profile;

	init();

	foldersTree.setSelectionRow(row);

	pack();
	setVisible(true);
    }

    /** Inits all the panel stuff */
    private void init()
    {
	listener = new NewFolderListener();

	panel = new JPanel();

	if(store != null)
	    foldersTree = MailClient.getFolders(store, profile);

	else
	{
	    DefaultMutableTreeNode top = new DefaultMutableTreeNode();
	    top.add(new DefaultMutableTreeNode());
	    foldersTree = new JTree(top);
	}	

	foldersTree.setRootVisible(false);
	foldersTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	foldersTree.putClientProperty("JTree.lineStyle", "Angled");
	
	int nb = foldersTree.getRowCount();

	for(int i = 0; i < nb; i++)
	    foldersTree.expandRow(i);

	scrollPane = new JScrollPane(foldersTree);
	panel.add(scrollPane);

	folderName = new JTextField(msgBundle.getString("NewFolder.defaultName"));
	panel.add(folderName);

	folders = new JCheckBox("HOLDS_FOLDERS");
	panel.add(folders);

	messages = new JCheckBox("HOLDS_MESSAGES", true);
	panel.add(messages);

	ok = new JButton("OK");
	ok.addActionListener(listener);
	panel.add(ok);

	cancel = new JButton(msgBundle.getString("NewFolder.cancelLabel"));
	cancel.addActionListener(listener);
	panel.add(cancel);

	setContentPane(panel);
    }

    /** Listener for this class */
    private final class NewFolderListener implements ActionListener
    {
	/** This method is invoked when an event is triggered
	 *  @param e event
	 */
	public final void actionPerformed(ActionEvent e)
	{
	    JButton b = (JButton)e.getSource();

	    if(b == ok)
	    {
		int type;

		if(folders.isSelected())
		{
		    type = Folder.HOLDS_FOLDERS;

		    if(messages.isSelected())
			type &= Folder.HOLDS_MESSAGES;
		}

		else if(messages.isSelected())
		    type = Folder.HOLDS_MESSAGES;

		else
		    return;

		DefaultMutableTreeNode n = (DefaultMutableTreeNode)foldersTree.getLastSelectedPathComponent();

		if(n != null)
		{
		    String selected = n.toString();

		    String newNod = selected + "." + folderName.getText();

		    MailClient.createFolder(store, profile, newNod, type);

		    dispose();
		}

		else
		    JOptionPane.showMessageDialog(null, msgBundle.getString("NewFolder.noParent"), "NewFolder", JOptionPane.INFORMATION_MESSAGE);
	    }

	    else if(b == cancel)
		dispose();
	}
    }
}

