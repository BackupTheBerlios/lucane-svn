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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.mail.*;
import javax.swing.*;

import org.lucane.applications.jmail.JMailPlugin;

/** Main window */
public class JMailWindow extends JFrame {
	/** Language resource */
	private ResourceBundle msgBundle;

	private JMenuBar menuBar;

	/** "File" menu */
	private JMenu fileMenu;

	/** "New" sub-menu */
	private JMenu newMenu;

	/** "Mail" menu item */
	private JMenuItem newMsgMenuItem;

	/** "Folder" menu item */
	private JMenuItem newFolderMenuItem;

	/** "Open..." menu item */
	private JMenuItem openMenuItem;

	private JMenuItem saveMenuItem;

	private JMenu folderMenu;
	private JMenuItem folderNewMenuItem;
	private JMenuItem folderDeleteMenuItem;

	private JMenuItem printMenuItem;


	/** "Exit" menu item */
	private JMenuItem exitMenuItem;

	/** "Edit" menu */
	private JMenu editMenu;

	/** "Move in folder..." menu item */
	private JMenuItem moveMenuItem;

	/** "Copy in folder..." menu item */
	private JMenuItem copyMenuItem;

	/** "Delete" menu item */
	private JMenuItem deleteMenuItem;

	/** "Mail" menu */
	private JMenu messageMenu;

	/** "New mail" menu item */
	private JMenuItem newMsgMenuItem2;

	/** "Reply..." menu item */
	private JMenuItem replyMenuItem;

	/** "Reply all..." menu item */
	private JMenuItem replyallMenuItem;

	/** "Forward..." menu item */
	private JMenuItem forwardMenuItem;

	private MainPanel panel;

	/** User profile */
	private Profile profile;

	private JMailWindowListener listener;

	private JMailPlugin plugin;

	/**
	 * Constructor
	 * 
	 * @param locale
	 *                  locale used
	 */
	public JMailWindow(JMailPlugin plugin) {
		super(plugin.getTitle());
		this.plugin = plugin;
		
		this.msgBundle = plugin.getBundle();
		this.profile = plugin.getProfile();
		
		init();

		if (profile.getType().compareTo("pop3") == 0)
			setFolderMenuItemsEnabled(false);

		setPanel();
		addWindowListener(listener);
		setSize(800, 600);
		setVisible(true);

		if (profile.getType().compareTo("pop3") != 0) {
			FolderListener f = new FolderListener(panel);
			f.start();
		}
	}

	/** Initialisation of menus */
	private void init() {
		listener = new JMailWindowListener();

		/** Init menus */
		menuBar = new JMenuBar();

		fileMenu = new JMenu(msgBundle.getString("common.fileMenu"));
		fileMenu.setMnemonic('F');

		newMenu = new JMenu(msgBundle.getString("JMailWindow.newMenuLabel"));
		newMenu.setMnemonic('N');
		fileMenu.add(newMenu);

		newMsgMenuItem =
			newMenu.add(msgBundle.getString("common.mail"));
		newMsgMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
		newMsgMenuItem.addActionListener(listener);

		newMenu.addSeparator();

		newFolderMenuItem =
			newMenu.add(
				msgBundle.getString("common.folder"));
		newFolderMenuItem.setMnemonic('R');
		newFolderMenuItem.addActionListener(listener);

		openMenuItem =
			fileMenu.add(msgBundle.getString("common.open"));
		openMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
		openMenuItem.setMnemonic('O');
		openMenuItem.setEnabled(false);
		openMenuItem.addActionListener(listener);

		saveMenuItem =
			fileMenu.add(msgBundle.getString("common.save"));
		saveMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
		saveMenuItem.setMnemonic('E');
		saveMenuItem.setEnabled(false);
		saveMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		folderMenu =
			new JMenu(msgBundle.getString("common.folder"));
		folderMenu.setMnemonic('D');
		fileMenu.add(folderMenu);

		folderNewMenuItem =
			folderMenu.add(
				msgBundle.getString("common.new"));
		folderNewMenuItem.addActionListener(listener);

		folderDeleteMenuItem =
			folderMenu.add(
				msgBundle.getString("common.delete"));
		folderDeleteMenuItem.setEnabled(false);
		folderDeleteMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		printMenuItem =
			fileMenu.add(msgBundle.getString("common.print"));
		printMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
		printMenuItem.setMnemonic('I');
		printMenuItem.setEnabled(false);
		printMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		exitMenuItem =
			fileMenu.add(msgBundle.getString("JMailWindow.exitMenuItemLabel"));
		exitMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('Q', Event.CTRL_MASK));
		exitMenuItem.setMnemonic('Q');
		exitMenuItem.addActionListener(listener);

		fileMenu = menuBar.add(fileMenu);

		editMenu = new JMenu(msgBundle.getString("JMailWindow.editMenuLabel"));
		editMenu.setMnemonic('E');

		moveMenuItem =
			editMenu.add(msgBundle.getString("JMailWindow.moveMenuItemLabel"));
		moveMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('V', Event.CTRL_MASK + Event.SHIFT_MASK));
		moveMenuItem.setMnemonic('D');
		moveMenuItem.setEnabled(false);
		moveMenuItem.addActionListener(listener);

		copyMenuItem =
			editMenu.add(msgBundle.getString("JMailWindow.copyMenuItemLabel"));
		copyMenuItem.setMnemonic('C');
		copyMenuItem.setEnabled(false);
		copyMenuItem.addActionListener(listener);

		editMenu.addSeparator();

		deleteMenuItem =
			editMenu.add(
				msgBundle.getString("common.delete"));
		deleteMenuItem.setMnemonic('E');
		deleteMenuItem.setEnabled(false);
		deleteMenuItem.addActionListener(listener);

		editMenu = menuBar.add(editMenu);

		messageMenu =
			new JMenu(msgBundle.getString("common.mail"));
		messageMenu.setMnemonic('M');

		newMsgMenuItem2 =
			messageMenu.add(
				msgBundle.getString("JMailWindow.newMsgMenuItem2Label"));
		newMsgMenuItem2.setAccelerator(
			KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
		newMsgMenuItem2.setMnemonic('N');
		newMsgMenuItem2.addActionListener(listener);

		messageMenu.addSeparator();

		replyMenuItem =
			messageMenu.add(
				msgBundle.getString("common.reply"));
		replyMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
		replyMenuItem.setMnemonic('R');
		replyMenuItem.setEnabled(false);
		replyMenuItem.addActionListener(listener);

		replyallMenuItem =
			messageMenu.add(
				msgBundle.getString("common.replyAll"));
		replyallMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('R', Event.CTRL_MASK + Event.SHIFT_MASK));
		replyallMenuItem.setMnemonic('t');
		replyallMenuItem.setEnabled(false);
		replyallMenuItem.addActionListener(listener);

		forwardMenuItem =
			messageMenu.add(
				msgBundle.getString("JMailWindow.forwardMenuItemLabel"));
		forwardMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('F', Event.CTRL_MASK));
		forwardMenuItem.setMnemonic('f');
		forwardMenuItem.setEnabled(false);
		forwardMenuItem.addActionListener(listener);

		messageMenu = menuBar.add(messageMenu);

		setJMenuBar(menuBar);
	}

	/**
	 * Enable or disable menu items related to a mail
	 * 
	 * @param b
	 *                  activate or deactivate
	 */
	protected final void setMenuItemsEnabled(boolean b) {
		openMenuItem.setEnabled(b);
		saveMenuItem.setEnabled(b);
		//moveMenuItem.setEnabled(b);
		//copyMenuItem.setEnabled(b);
		deleteMenuItem.setEnabled(b);
		replyMenuItem.setEnabled(b);
		replyallMenuItem.setEnabled(b);
		forwardMenuItem.setEnabled(b);
	}

	/**
	 * Enable or disable menu items related to folder
	 * 
	 * @param b
	 *                  activate or deactivate
	 */
	protected final void setFolderMenuItemsEnabled(boolean b) {
		folderMenu.setEnabled(b);
		newFolderMenuItem.setEnabled(b);
		folderNewMenuItem.setEnabled(b);
		folderDeleteMenuItem.setEnabled(b);
		moveMenuItem.setEnabled(b);
		copyMenuItem.setEnabled(b);
	}

	/** Properly disconnect and exit */
	private void exit() {
		panel.disconnect();

		this.dispose();
		plugin.exit();
	}

	/** Updates everything */
	private void update() {
		panel.disconnect();
		setPanel();
		SwingUtilities.updateComponentTreeUI(this);
	}

	/** Reloads the main panel */
	private void setPanel() {
		panel = new MainPanel(plugin, this, profile, msgBundle);
		setContentPane(panel);
	}

	/** Listener for this class */
	private final class JMailWindowListener
		extends WindowAdapter
		implements ActionListener {
		/**
		 * Method called when the windows is closed
		 * 
		 * @param e
		 *                  triggered event
		 */
		public final void windowClosing(WindowEvent e) {
			exit();
		}

		/**
		 * This method is invoked when an event is triggered
		 * 
		 * @param e
		 *                  event
		 */
		public final void actionPerformed(ActionEvent e) {
			JMenuItem i = (JMenuItem) e.getSource();

			if (i == newMsgMenuItem || i == newMsgMenuItem2) {
				SendMessageDialog sm = new SendMessageDialog(plugin, profile, msgBundle);
				sm.dispose();
			} else if (i == newFolderMenuItem || i == folderNewMenuItem)
				panel.newFolder();

			else if (i == folderDeleteMenuItem)
				panel.deleteFolder();

			else if (i == openMenuItem) {
				Message currentMessage = panel.getCurrent();

				new ReadMessageFrame(
						plugin,
					panel.getStore(),
					panel.getCurrentFolder(),
					panel,
					profile,
					currentMessage,
					panel.getMailContent(currentMessage).toString(),
					msgBundle,
					panel.isAttachmentVisible());
			} else if (i == saveMenuItem)
				panel.save();

			else if (i == printMenuItem) {
				/** TODO : do printing stuff */
			}
			else if (i == exitMenuItem)
				exit();

			else if (i == moveMenuItem)
				panel.moveMsg();

			else if (i == copyMenuItem)
				panel.copyMsg();

			else if (i == deleteMenuItem)
				panel.delete();

			else if (i == replyMenuItem)
				panel.reply();

			else if (i == replyallMenuItem)
				panel.replyall();

			else if (i == forwardMenuItem)
				panel.forward();

		}
	}
}
