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

/** Move/Copy a message in another folder */
final class MoveMessageFrame extends JFrame
{
    /** Language resource */
    private ResourceBundle msgBundle;

    private JPanel panel;

    private JTree foldersTree;
    private JScrollPane scrollPane;

    private JButton ok;
    private JButton cancel;

    private String originalFolder;
    private String id;

    private MoveMessageListener listener;

    /** User profile */
    private Profile profile;

    /** Mode */
    private int mode;

    private Store store;
    private Folder currentFolder;

    protected final static int MOVE_MODE = 0;
    protected final static int COPY_MODE = 1;

    /** Constructor
     *  @param store the connection
     *  @param currentFolder the folder where the mail currently is
     *  @param profile user profile
     *  @param id mail's id
     *  @param mode mode
     *  @param msgBundle language resource
     */
    protected MoveMessageFrame(Store store, Folder currentFolder, Profile profile, String originalFolder, String id, int mode, ResourceBundle msgBundle)
    {
	super(msgBundle.getString("MoveMessage.frameTitle"));

	this.msgBundle = msgBundle;

	this.store = store;
	this.currentFolder = currentFolder;

	this.profile = profile;
	this.id = id;
	this.mode = mode;

	listener = new MoveMessageListener();

	panel = new JPanel();
     
	foldersTree = MailClient.getFolders(store, this.profile);

	foldersTree.setRootVisible(false);
	foldersTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	foldersTree.putClientProperty("JTree.lineStyle", "Angled");

	int nb = foldersTree.getRowCount();

	for(int i = 0; i < nb; i++)
	    foldersTree.expandRow(i);

	scrollPane = new JScrollPane(foldersTree);
	panel.add(scrollPane);

	ok = new JButton("OK");
	ok.addActionListener(listener);
	panel.add(ok);

	cancel = new JButton(msgBundle.getString("MoveMessage.cancelLabel"));
	cancel.addActionListener(listener);
	panel.add(cancel);

	setContentPane(panel);

	setSize(200, 500);
	pack();
	setVisible(true);           
    }

    /** Listener for this class */
    private final class MoveMessageListener implements ActionListener
    {
	/** This method is invoked when an event is triggered
	 *  @param e event
	 */
	public final void actionPerformed(ActionEvent e)
	{
	    JButton b = (JButton)e.getSource();

	    if(b == ok)
	    {
		DefaultMutableTreeNode n = (DefaultMutableTreeNode)foldersTree.getLastSelectedPathComponent();
		String selected = n.toString();

		if(selected.compareTo(currentFolder.getFullName()) == 0)
		{
		    JOptionPane.showMessageDialog(null, msgBundle.getString("MoveMessage.sameFoldersWarningLabel"), "MoveMessage", JOptionPane.INFORMATION_MESSAGE);
		    return;
		}

		if(mode == MOVE_MODE)
		{
		    boolean bool = MailClient.moveMsg(store, currentFolder, id, selected);
		    
		    if(!bool)
			JOptionPane.showMessageDialog(null, msgBundle.getString("MoveMessage.failureLabel"), "MoveMessage", JOptionPane.ERROR_MESSAGE);
		    
		}

		else //if(mode == COPY_MODE)
		{
		    boolean bool = MailClient.copyMsg(store, currentFolder, id, selected);
		    
		    if(!bool)
			JOptionPane.showMessageDialog(null, msgBundle.getString("MoveMessage.failureLabel"), "MoveMessage", JOptionPane.ERROR_MESSAGE);
		    
		}

		dispose();
	    }

	    else if(b == cancel)
		dispose();
	}
    }
}
