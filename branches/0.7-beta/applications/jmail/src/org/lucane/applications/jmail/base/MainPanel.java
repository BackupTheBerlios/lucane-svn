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
import java.io.*;
import java.text.*;
import java.util.*;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.lucane.applications.jmail.JMailPlugin;

/** The main panel of JMail */
final class MainPanel extends JPanel {
	/** Language resource */
	private ResourceBundle msgBundle;

	/** Horizontal split */
	private JSplitPane splitPane1;

	/** Vertical split */
	private JSplitPane splitPane2;

	private JToolBar toolBar;

	private JButton verify;

	private JButton send;
	private JButton reply;
	private JButton replyall;
	private JButton forward;
	private JButton print;
	private JButton delete;

	private JScrollPane foldersPane;
	private JTree foldersTree;

	private JScrollPane subjectsPane;
	private SortableTable subjectsTable;

	private Object msgList[][];

	/** Columns names */
	private String columns[];

	private ListSelectionModel listSelect;

	private Message mails[];
	private Message current;

	/** Panel used when a mail is selected */
	private JPanel readPanel;

	private JLabel from;
	private JLabel subject;

	private JButton attachment;

	/** Textarea for the mail body */
	private JTextArea area;

	private JScrollPane readPane;

	/** Mail body */
	private StringBuffer content;

	/** Current folder */
	private String folder;

	private JMailWindow parent;

	/** User profile */
	private Profile profile;

	private DateFormat df;
	//private FormattedDateRenderer renderer;

	private MainPanelListener listener;

	private JPopupMenu folderPopup;
	private JMenuItem folderNewItem;
	private JMenuItem folderDeleteItem;

	private JPopupMenu msgPopup;
	private JMenuItem msgOpenItem;
	private JMenuItem msgSourceItem;
	private JMenuItem msgSaveItem;
	private JMenuItem msgPrintItem;
	private JMenuItem msgReplyItem;
	private JMenuItem msgReplyallItem;
	private JMenuItem msgForwardItem;
	private JMenuItem msgMoveItem;
	private JMenuItem msgCopyItem;
	private JMenuItem msgDeleteItem;

	private String currentId;

	/** The connection to the mail server */
	private Store store;

	private Folder currentFolder;

	private JMailPlugin plugin;

	/**
	 * Constructor
	 * 
	 * @param window
	 *                  windows which owns this panel
	 * @param profile
	 *                  user profile
	 * @param msgBundle
	 *                  language resource
	 */
	protected MainPanel(
		JMailPlugin plugin,
		JMailWindow window,
		Profile profile,
		ResourceBundle msgBundle) {
		this.plugin = plugin;
		this.msgBundle = msgBundle;

		listener = new MainPanelListener();

		currentId = new String();

		setLayout(new BorderLayout());

		df = DateFormat.getInstance();
		df.setLenient(true);

		parent = window;
		this.profile = profile;

		/** Build the toolbar */
		toolBar = new JToolBar();
		toolBar.setFloatable(true);

		verify = new JButton(plugin.getIcon("check.png"));
		verify.setToolTipText(msgBundle.getString("MainPanel.verifyLabel"));
		verify.addActionListener(listener);

		toolBar.add(verify);

		send = new JButton(plugin.getIcon("new.png"));
		send.setToolTipText(msgBundle.getString("MainPanel.sendLabel"));
		send.addActionListener(listener);

		toolBar.add(send);

		reply = new JButton(plugin.getIcon("reply.png"));
		reply.setToolTipText(msgBundle.getString("MainPanel.replyLabel"));
		reply.setEnabled(false);
		reply.addActionListener(listener);

		toolBar.add(reply);

		replyall = new JButton(plugin.getIcon("replyall.png"));
		replyall.setToolTipText(msgBundle.getString("common.replyAll"));
		replyall.setEnabled(false);
		replyall.addActionListener(listener);

		toolBar.add(replyall);

		forward = new JButton(plugin.getIcon("forward.png"));
		forward.setToolTipText(msgBundle.getString("MainPanel.forwardLabel"));
		forward.setEnabled(false);
		forward.addActionListener(listener);

		toolBar.add(forward);

		toolBar.addSeparator();

		print = new JButton(plugin.getIcon("print.png"));
		print.setToolTipText(msgBundle.getString("MainPanel.printLabel"));
		print.setEnabled(false);

		toolBar.add(print);

		delete = new JButton(plugin.getIcon("delete.png"));
		delete.setToolTipText(msgBundle.getString("MainPanel.deleteLabel"));
		delete.setEnabled(false);
		delete.addActionListener(listener);

		toolBar.add(delete);

		add("North", toolBar);

		currentFolder = null;
		store = MailClient.connect(profile);

		if (store == null) {
			DefaultMutableTreeNode top = new DefaultMutableTreeNode();
			top.add(new DefaultMutableTreeNode());
			foldersTree = new JTree(top);
		} else
			foldersTree = MailClient.getFolders(store, profile);

		foldersTree.setCellRenderer(new FolderRenderer(plugin));
		int nb = foldersTree.getRowCount();

		for (int i = 0; i < nb; i++)
			foldersTree.expandRow(i);

		foldersTree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);

		if (store != null)
			foldersTree.addTreeSelectionListener(listener);

		foldersTree.putClientProperty("JTree.lineStyle", "Angled");
		foldersPane = new JScrollPane(foldersTree);
		foldersPane.setPreferredSize(new Dimension(150, 500));

		foldersTree.addMouseListener(listener);

		folderPopup = new JPopupMenu();

		folderNewItem =
			new JMenuItem(msgBundle.getString("MainPanel.folderNewItemLabel"));
		folderNewItem.addActionListener(listener);
		folderPopup.add(folderNewItem);

		folderDeleteItem =
			new JMenuItem(
				msgBundle.getString("common.delete"));
		folderDeleteItem.addActionListener(listener);
		folderPopup.add(folderDeleteItem);

		if (profile.getType().compareTo("pop3") == 0) {
			folderDeleteItem.setEnabled(false);
			folderNewItem.setEnabled(false);
		}

		TreeModel model = foldersTree.getModel();
		DefaultMutableTreeNode firstChild =
			(DefaultMutableTreeNode) model.getChild(model.getRoot(), 0);
		folder = (String) firstChild.getUserObject();

		/** Build the table */
		columns = new String[3];
		columns[0] = msgBundle.getString("MainPanel.columns[0]Label");
		columns[1] = msgBundle.getString("MainPanel.columns[1]Label");
		columns[2] = msgBundle.getString("MainPanel.columns[2]Label");

		subjectsTable = new SortableTable();
		subjectsTable.setDefaultRenderer(
			Object.class,
			new ColorableCellRenderer(df, this));
		subjectsTable.setData(new Object[0][0], columns);

		subjectsTable.addListSelectionListener(listener);
		subjectsTable.addMouseListener(listener);

		msgPopup = new JPopupMenu();

		msgOpenItem =
			new JMenuItem(msgBundle.getString("common.open"));
		msgOpenItem.addActionListener(listener);
		msgPopup.add(msgOpenItem);

		msgSourceItem =
			new JMenuItem(msgBundle.getString("MainPanel.msgSourceItemLabel"));
		msgSourceItem.addActionListener(listener);
		msgPopup.add(msgSourceItem);

		msgSaveItem =
			new JMenuItem(msgBundle.getString("common.save"));
		msgSaveItem.addActionListener(listener);
		msgPopup.add(msgSaveItem);

		msgPrintItem =
			new JMenuItem(msgBundle.getString("common.print"));
		msgPrintItem.setEnabled(false);
		msgPopup.add(msgPrintItem);

		msgPopup.addSeparator();

		msgReplyItem =
			new JMenuItem(msgBundle.getString("common.reply"));
		msgReplyItem.addActionListener(listener);
		msgPopup.add(msgReplyItem);

		msgReplyallItem =
			new JMenuItem(
				msgBundle.getString("common.replyAll"));
		msgReplyallItem.addActionListener(listener);
		msgPopup.add(msgReplyallItem);

		msgForwardItem =
			new JMenuItem(msgBundle.getString("MainPanel.msgForwardItemLabel"));
		msgForwardItem.addActionListener(listener);
		msgPopup.add(msgForwardItem);

		msgPopup.addSeparator();

		msgMoveItem =
			new JMenuItem(msgBundle.getString("MainPanel.msgMoveItemLabel"));
		msgMoveItem.addActionListener(listener);
		msgPopup.add(msgMoveItem);

		msgCopyItem =
			new JMenuItem(msgBundle.getString("MainPanel.msgCopyItemLabel"));
		msgCopyItem.addActionListener(listener);
		msgPopup.add(msgCopyItem);

		msgDeleteItem =
			new JMenuItem(msgBundle.getString("common.delete"));
		msgDeleteItem.addActionListener(listener);
		msgPopup.add(msgDeleteItem);

		subjectsPane = new JScrollPane(subjectsTable);
		subjectsPane.setPreferredSize(new Dimension(100, 100));

		readPanel = new JPanel();
		readPanel.setLayout(new BoxLayout(readPanel, BoxLayout.Y_AXIS));
		from = new JLabel(msgBundle.getString("MainPanel.fromLabel"));
		readPanel.add(from);
		subject = new JLabel(msgBundle.getString("MainPanel.subjectLabel"));
		readPanel.add(subject);

		attachment =
			new JButton(msgBundle.getString("MainPanel.attachmentLabel"));
		attachment.setVisible(false);
		attachment.addActionListener(listener);
		readPanel.add(attachment);

		content = new StringBuffer(msgBundle.getString("MainPanel.noMsgLabel"));
		area = new JTextArea(10, 10);
		area.setLineWrap(true);
		area.setText(content.toString());
		area.setEditable(false);
		readPane = new JScrollPane(area);
		readPanel.add(readPane);

		splitPane1 =
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, subjectsPane, readPanel);
		splitPane1.setOneTouchExpandable(true);
		splitPane1.setContinuousLayout(true);

		splitPane2 =
			new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				foldersPane,
				splitPane1);
		splitPane2.setOneTouchExpandable(true);
		splitPane2.setContinuousLayout(true);

		add("Center", splitPane2);


		foldersTree.setSelectionRow(1);
	}

	/** Disconnects from the mail server */
	protected final void disconnect() {
		try {
			if (currentFolder != null)
				if (currentFolder.isOpen())
					currentFolder.close(true);

			if (store != null)
				store.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Display the selected mail */
	private void displayMsg() {
		setButtonsEnabled(true);

		String id = (String) subjectsTable.getSelectedRowIndex();

		if (id == null) {
			content = new StringBuffer("*IDless* message :-(");
			area.setText(content.toString());

			area.setCaretPosition(0);

			current = mails[0];

			return;
		}

		try {
			String currentSubject = null;
			Address a[];

			for (int i = 0; i < mails.length; i++) {
				currentId = ((MimeMessage) mails[i]).getMessageID();

				if (currentId == null)
					continue;

				if (id.compareTo(currentId) == 0) {
					current = mails[i];
					currentSubject = current.getSubject();

					if (currentSubject == null)
						currentSubject = new String();

					subject.setText(
						msgBundle.getString("MainPanel.subjectLabel")
							+ " "
							+ currentSubject);

					a = current.getFrom();

					if (a != null && a[0] != null)
						from.setText(
							msgBundle.getString("MainPanel.fromLabel")
								+ " "
								+ a[0].toString());

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		attachment.setVisible(false);
		content = getMailContent(current);

		area.setText(content.toString());
		area.setCaretPosition(0);

		parent.setMenuItemsEnabled(true);
	}

	boolean isFlagSet(int row, Flags.Flag flag) {
		boolean b = false;

		try {
			b = mails[row].isSet(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (b);
	}

	void checkConnect() {
		try {
			if (current != null)
				current.getContent();
		} catch (Exception e) {
			try {
				if (currentFolder != null)
					if (currentFolder.isOpen())
						currentFolder.close(true);

				if (store != null) {
					currentFolder = store.getFolder(folder);
					currentFolder.open(Folder.READ_WRITE);

					currentFolder.addMessageCountListener(listener);
				}
			} catch (FolderNotFoundException exception) {
				JOptionPane.showMessageDialog(
					null,
					exception.getMessage(),
					"FolderNotFoundException",
					JOptionPane.ERROR_MESSAGE);

				TreePath path =
					new TreePath(new DefaultMutableTreeNode("INBOX"));
				foldersTree.setSelectionPath(path);

				return;
			} catch (ReadOnlyFolderException readException) {
				try {
					currentFolder.open(Folder.READ_ONLY);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (MessagingException me) {
				JOptionPane.showMessageDialog(
					null,
					me.getMessage(),
					"MessagingException",
					JOptionPane.ERROR_MESSAGE);
				return;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Recursive function to get a mail part's content
	 * 
	 * @param current
	 *                  part to be examined
	 * @return content of this part
	 */
	protected final StringBuffer getMailContent(Part current) {
		checkConnect();

		StringBuffer content2 = new StringBuffer();

		try {
			if (current.isMimeType("text/plain"))
				content2.append((String) current.getContent());

			else if (current.isMimeType("text/html")) {
				attachment.setVisible(true);
				content2.append(msgBundle.getString("MainPanel.htmlMsgLabel"));
			} else if (current.isMimeType("multipart/alternative")) {
				attachment.setVisible(true);

				Multipart mp = (Multipart) current.getContent();
				BodyPart bp = null;

				for (int i = 0; i < mp.getCount(); i++) {
					bp = mp.getBodyPart(i);

					if (bp.isMimeType("text/plain")) {
						content2.append((String) bp.getContent());
						break;
					}
				}
			} else if (
				current.isMimeType("multipart/mixed")
					|| current.isMimeType("multipart/related")
					|| current.isMimeType("multipart/signed")) {
				boolean done = false;

				Multipart mp = (Multipart) current.getContent();
				BodyPart bp0 = mp.getBodyPart(0);
				content2.append(getMailContent(bp0));

				BodyPart bp = null;

				for (int i = 1; i < mp.getCount(); i++) {
					bp = mp.getBodyPart(i);

					if (bp.isMimeType("message/rfc822")
						|| bp.isMimeType("multipart/alternative")) {
						if (bp.isMimeType("message/rfc822")) {
							content2.append(
								"\n\n"
									+ msgBundle.getString(
										"MainPanel.forwardedMail")
									+ "\n");
							content2.append(
								getMailContent((Message) bp.getContent()));
						}

						if (bp.isMimeType("multipart/alternative"))
							content2.append(getMailContent(bp));
					} else
						attachment.setVisible(true);
				}
			} else if (current.isMimeType("message/rfc822")) {
				content2.append(
					"\n\n"
						+ msgBundle.getString("MainPanel.forwardedMail")
						+ "\n");
				content2.append(getMailContent((Message) current.getContent()));
			} else if (current.isMimeType("multipart/report")) {
				boolean done = false;

				Multipart mp = (Multipart) current.getContent();

				BodyPart bp0 = mp.getBodyPart(0);

				content2.append(getMailContent(bp0));

				BodyPart bp = null;

				for (int i = 1; i < mp.getCount(); i++) {
					bp = mp.getBodyPart(i);

					if (bp.isMimeType("message/rfc822")
						|| bp.isMimeType("multipart/alternative")) {
						if (bp.isMimeType("message/rfc822")) {
							content2.append(
								"\n\n"
									+ msgBundle.getString(
										"MainPanel.forwardedMail")
									+ "\n");
							content2.append(
								getMailContent((Message) bp.getContent()));
						}

						if (bp.isMimeType("multipart/alternative"))
							content2.append(getMailContent(bp));
					}
				}
			} else
				content2.append(
					msgBundle.getString("MainPanel.unreadableMsgLabel"));
		} catch (UnsupportedEncodingException encodE) {
			JOptionPane.showMessageDialog(
				null,
				encodE.getMessage(),
				"UnsupportedEncodingException",
				JOptionPane.ERROR_MESSAGE);
			content2.append(
				msgBundle.getString("MainPanel.unreadableMsgLabel"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (content2);
	}

	/** Read the selected mail */
	private void read() {
		new ReadMessageFrame(
			plugin,
			store,
			currentFolder,
			this,
			profile,
			current,
			getMailContent(current).toString(),
			msgBundle,
			attachment.isVisible());
	}

	/** Delete the selected mail */
	protected final void delete() {
		try {
			Object[] indexes = subjectsTable.getSelectedRowsIndexes();

			boolean b;

			for (int i = 0; i < indexes.length; i++)
				b =
					MailClient.deleteMessage(
						store,
						currentFolder,
						(String) indexes[i]);

			update();

			content =
				new StringBuffer(msgBundle.getString("MainPanel.noMsgLabel"));
			area.setText(content.toString());

			/*
			 * boolean b = MailClient.deleteMessage(store, currentFolder,
			 * currentId);
			 * 
			 * if(b) { JOptionPane.showMessageDialog(null,
			 * msgBundle.getString("MainPanel.msgDeletedLabel"), "MainPanel",
			 * JOptionPane.INFORMATION_MESSAGE); update();
			 * 
			 * content = new
			 * StringBuffer(msgBundle.getString("MainPanel.noMsgLabel"));
			 * area.setText(content.toString()); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Brings the "New Folder" dialog */
	protected final void newFolder() {
		DefaultMutableTreeNode n =
			(DefaultMutableTreeNode) foldersTree.getLastSelectedPathComponent();

		if (n != null) {
			TreePath t = new TreePath(n);
			int row = foldersTree.getRowForPath(t);

			new NewFolderFrame(store, profile, row, msgBundle);
		} else
			new NewFolderFrame(store, profile, msgBundle);

		foldersTree.treeDidChange();
	}

	/** Update the table */
	protected final void update() {
		DefaultMutableTreeNode n =
			(DefaultMutableTreeNode) foldersTree.getLastSelectedPathComponent();

		if(n != null && n.isRoot())
			return;

		if (n == null) 
		{
			TreePath path = new TreePath(new DefaultMutableTreeNode("INBOX"));
			foldersTree.setSelectionPath(path);

			n =
				(DefaultMutableTreeNode) foldersTree
					.getLastSelectedPathComponent();
		}

		folder = n.toString();

		mails = null;

		try {
			if (currentFolder != null)
			{
				try {
					if (currentFolder.isOpen())
						currentFolder.close(true);
				} catch(Exception e) {
					//probably already closed, race condition...
				}
			}

			if (store != null) {
				currentFolder = store.getFolder(folder);
				if(currentFolder.getType() == Folder.HOLDS_FOLDERS)
					return;

				currentFolder.open(Folder.READ_WRITE);

				currentFolder.addMessageCountListener(listener);
				mails = currentFolder.getMessages();
			}
		} catch (FolderNotFoundException exception) {
			JOptionPane.showMessageDialog(
				null,
				exception.getMessage(),
				"FolderNotFoundException",
				JOptionPane.ERROR_MESSAGE);

			TreePath path = new TreePath(new DefaultMutableTreeNode("INBOX"));
			foldersTree.setSelectionPath(path);

			n =
				(DefaultMutableTreeNode) foldersTree
					.getLastSelectedPathComponent();

			return;
		} catch (ReadOnlyFolderException readException) {
			try {
				currentFolder.open(Folder.READ_ONLY);
				mails = currentFolder.getMessages();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(
				null,
				e.getMessage(),
				"MessagingException",
				JOptionPane.ERROR_MESSAGE);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mails == null)
			mails = new Message[0];

		msgList = null;
		msgList = new Object[mails.length][3];

		String ids[] = new String[mails.length];

		Message m = null;

		try {
			if (folder.compareTo("INBOX") == 0
				|| folder.compareTo("inbox") == 0) {
				for (int i = 0; i < mails.length; i++) {
					m = mails[i];

					if (!m.isSet(Flags.Flag.SEEN)) {
							update();
					}
				}
			}

			InternetAddress[] a;
			String name;
			Date d;

			for (int i = 0; i < mails.length; i++) {
				m = mails[i];

				a = (InternetAddress[]) m.getFrom();

				if (a != null && a.length > 0) {
					name = a[0].getPersonal();

					if (name == null || name.compareTo("") == 0)
						name = a[0].getAddress();

					msgList[i][0] = (Object) name;

					if (msgList[i][0] == null)
						msgList[i][0] = (Object) new String();
				}

				msgList[i][1] = (Object) m.getSubject();

				if (msgList[i][1] == null)
					msgList[i][1] = (Object) new String();

				d = m.getSentDate();

				if (d != null)
					msgList[i][2] = (Object) d;

				else
					msgList[i][2] = (Object) new Date();

				ids[i] = ((MimeMessage) m).getMessageID();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		subjectsTable.setIndexes(ids);
		subjectsTable.setData(msgList, columns);
		subjectsTable.removeListeners();
		subjectsTable.addMouseListenerToHeaderInTable();

		//subjectsTable.setCellRenderer(msgBundle.getString("MainPanel.columns[2]Label"),
		// new FormattedDateRenderer(df, this));

	}

	/** Write a new mail */
	private void send() {
		SendMessageDialog sm =
			new SendMessageDialog(plugin, profile, msgBundle);
		sm.dispose();
	}

	/** Reply to the selected mail */
	protected final void reply() {
		try {
			Address a[] = current.getReplyTo();

			String currentSubject = current.getSubject();

			if (currentSubject == null)
				currentSubject = "";

			currentSubject = "Re: " + currentSubject;

			SendMessageDialog sm =
				new SendMessageDialog(
					plugin,
					profile,
					a[0].toString(),
					currentSubject,
					getMailContent(current).toString(),
					msgBundle);

			if (sm.sentMail())
				current.setFlag(Flags.Flag.ANSWERED, true);

			sm.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Reply to all */
	protected final void replyall() {
		try {
			Address a[] = current.getReplyTo();

			String currentSubject = current.getSubject();

			if (currentSubject == null)
				currentSubject = "";

			currentSubject = "Re: " + currentSubject;

			Address to[] = current.getRecipients(Message.RecipientType.TO);
			Address cc[] = current.getRecipients(Message.RecipientType.CC);

			String copies[] = new String[0];

			if (cc != null) {
				if (to != null) {
					copies = new String[to.length + cc.length];

					int i;

					for (i = 0; i < to.length; i++)
						copies[i] = to[i].toString();

					for (int j = 0; j < cc.length; j++)
						copies[i++] = cc[j].toString();
				} else {
					copies = new String[cc.length];

					for (int i = 0; i < cc.length; i++)
						copies[i] = cc[i].toString();
				}
			} else if (to != null) {
				copies = new String[to.length];

				for (int i = 0; i < to.length; i++)
					copies[i] = to[i].toString();
			}

			SendMessageDialog sm =
				new SendMessageDialog(
					plugin,
					profile,
					a[0].toString(),
					copies,
					currentSubject,
					getMailContent(current).toString(),
					msgBundle);

			if (sm.sentMail())
				current.setFlag(Flags.Flag.ANSWERED, true);

			sm.dispose();
		} catch (AddressException addressEx) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Forward the selected mail */
	protected final void forward() {
		try {
			String currentSubject = current.getSubject();

			if (currentSubject == null)
				currentSubject = "";

			currentSubject = "Fwd : " + currentSubject;

			SendMessageDialog sm =
				new SendMessageDialog(
					plugin,
					profile,
					currentSubject,
					getMailContent(current).toString(),
					msgBundle);
			sm.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Move the selected mail in another folder */
	protected final void moveMsg() {
		new MoveMessageFrame(
			store,
			currentFolder,
			profile,
			folder,
			currentId,
			0,
			msgBundle);
		update();
	}

	/* Copy the selected mail in another folder */
	protected final void copyMsg() {
		new MoveMessageFrame(
			store,
			currentFolder,
			profile,
			folder,
			currentId,
			1,
			msgBundle);
	}

	/**
	 * Activate or deactivate the buttons which are related to a mail
	 * 
	 * @param b
	 *                  activate or deactivate
	 */
	private void setButtonsEnabled(boolean b) {
		reply.setEnabled(b);
		replyall.setEnabled(b);
		forward.setEnabled(b);
		delete.setEnabled(b);
	}

	/** Deletes the selected folder */
	protected final void deleteFolder() {
		DefaultMutableTreeNode n =
			(DefaultMutableTreeNode) foldersTree.getLastSelectedPathComponent();

		if (n != null) {
			if (currentFolder.getFullName().compareTo(n.toString()) == 0) {
				if (!MailClient.deleteFolder(currentFolder))
					JOptionPane.showMessageDialog(
						null,
						msgBundle.getString("MainPanel.msgNotDeletedLabel"),
						"MainPanel.deleteFolder",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					Folder fold = store.getFolder(n.toString());

					if (!MailClient.deleteFolder(fold))
						JOptionPane.showMessageDialog(
							null,
							msgBundle.getString("MainPanel.msgNotDeletedLabel"),
							"MainPanel.deleteFolder",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			MutableTreeNode parent = (MutableTreeNode) n.getParent();

			if (parent != null) {
				DefaultTreeModel model =
					(DefaultTreeModel) foldersTree.getModel();
				model.removeNodeFromParent(n);
			}
		}
	}

	/** Saves the current mail in a text file */
	protected final void save() {
		JFileChooser fc = new JFileChooser();

		int val = fc.showSaveDialog(null);

		if (val == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (file != null) {
				try {
					int choice;

					while (file.exists()) {
						choice =
							JOptionPane.showConfirmDialog(
								null,
								msgBundle.getString(
									"MainPanel.fileExistsLabel"),
								"MainPanel",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						if (choice == JOptionPane.YES_OPTION)
							break;

						val = fc.showSaveDialog(null);

						if (val == JFileChooser.APPROVE_OPTION)
							file = fc.getSelectedFile();

						else
							return;
					}

					FileOutputStream out = new FileOutputStream(file);
					current.writeTo(out);
					out.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/** Displays the current mail's source in a dialog */
	private void displayMsgSource() {
		try {
			new MessageSourceFrame(current.getSubject(), current);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tells if the "Attachment" button is visible or not
	 * 
	 * @return boolean telling wether the button is visible or not
	 */
	protected final boolean isAttachmentVisible() {
		return (attachment.isVisible());
	}

	/**
	 * Returns the current folder
	 * 
	 * @return current folder
	 */
	protected final Folder getCurrentFolder() {
		return (currentFolder);
	}

	/**
	 * Allows to get the connection object
	 * 
	 * @return the connection
	 */
	protected final Store getStore() {
		return (store);
	}

	/**
	 * Gets currently selected mail
	 * 
	 * @return the mail
	 */
	protected final Message getCurrent() {
		return (current);
	}

	/**
	 * Returns the parent <code>JMailWindow</code>
	 * 
	 * @return the parent <code>JMailWindow</code>
	 */
	protected JMailWindow getParentWindow() {
		return (parent);
	}

	/** Listener for this class */
	private final class MainPanelListener
		extends MouseAdapter
		implements
			ActionListener,
			ListSelectionListener,
			TreeSelectionListener,
			MessageCountListener {
		/**
		 * Method called when the user clicks on a mouse button
		 * 
		 * @param e
		 *                  triggered event
		 */
		public final void mousePressed(MouseEvent e) {
			if (e.getSource() == subjectsTable) {
				if (subjectsTable.contains(e.getX(), e.getY())) {
					if (e.isPopupTrigger()) {
						if (subjectsTable.getSelectedRow() != -1)
							msgPopup.show(e.getComponent(), e.getX(), e.getY());
					} else {
						int mods = e.getModifiers();

						/** Right click */
						if (mods == 4) {
							if (subjectsTable.getSelectedRow() != -1)
								msgPopup.show(
									e.getComponent(),
									e.getX(),
									e.getY());
						}

						/** Left double-click */
						else if (mods == 16 && e.getClickCount() == 2) {
							read();
						}
					}
				}
			} else if (e.getSource() == foldersTree) {
				if (e.isPopupTrigger()) {
					DefaultMutableTreeNode n =
						(DefaultMutableTreeNode) foldersTree
							.getLastSelectedPathComponent();

					if (n != null)
						folderPopup.show(e.getComponent(), e.getX(), e.getY());
				} else {
					int mods = e.getModifiers();

					/** Right click */
					if (mods == 4) {
						DefaultMutableTreeNode n =
							(DefaultMutableTreeNode) foldersTree
								.getLastSelectedPathComponent();

						if (n != null)
							folderPopup.show(
								e.getComponent(),
								e.getX(),
								e.getY());
					}
				}
			}
		}

		/**
		 * This method is invoked when an event is triggered
		 * 
		 * @param e
		 *                  event
		 */
		public final void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton) e.getSource();

			if (b == verify)
				update();

			else if (b == send)
				send();

			else if (b == reply || b == msgReplyItem)
				reply();

			else if (b == replyall || b == msgReplyallItem)
				replyall();

			else if (b == forward || b == msgForwardItem)
				forward();

			else if (b == delete || b == msgDeleteItem)
				delete();

			else if (b == folderNewItem)
				newFolder();

			else if (b == folderDeleteItem)
				deleteFolder();

			else if (b == msgOpenItem)
				read();

			else if (b == msgSourceItem)
				displayMsgSource();

			else if (b == msgSaveItem)
				save();

			else if (b == msgMoveItem)
				moveMsg();

			else if (b == msgCopyItem)
				copyMsg();

			else if (b == attachment)
				new AttachmentFrame(current, msgBundle);
		}

		/**
		 * Method invoked when the user clicks on a mail
		 * 
		 * @param e
		 *                  triggered event
		 */
		public final void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			if (lsm.isSelectionEmpty()) {
				setButtonsEnabled(false);

				parent.setMenuItemsEnabled(false);
				from.setText(msgBundle.getString("MainPanel.fromLabel"));
				subject.setText(msgBundle.getString("MainPanel.subjectLabel"));

				content =
					new StringBuffer(
						msgBundle.getString("MainPanel.noMsgLabel"));
				area.setText(content.toString());

				attachment.setVisible(false);
			} else if (subjectsTable.getSelectedRowCount() > 1) {
				setButtonsEnabled(false);
				delete.setEnabled(true);
				from.setText(msgBundle.getString("MainPanel.fromLabel"));
				subject.setText(msgBundle.getString("MainPanel.subjectLabel"));

				content =
					new StringBuffer(
						msgBundle.getString("MainPanel.noMsgLabel"));
				area.setText(content.toString());

				attachment.setVisible(false);
			} else
				displayMsg();
		}

		/**
		 * Method called when the user clicks on a folder
		 * 
		 * @param e
		 *                  triggered event
		 */
		public final void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode n =
				(DefaultMutableTreeNode) foldersTree
					.getLastSelectedPathComponent();

			if (n == null) {
				parent.setFolderMenuItemsEnabled(false);
				return;
			}

			if (profile.getType().compareTo("pop3") != 0)
				parent.setFolderMenuItemsEnabled(true);

			update();
		}

		/**
		 * Method called when a new mail is received
		 * 
		 * @param ev
		 *                  triggered event
		 */
		public final void messagesAdded(MessageCountEvent ev) {
			update();
			getToolkit().beep();
		}

		/***********************************************************************
		 * Method *supposedly Indeed, it is not used and is present only
		 * because of the <code>FolderListener</code> implantation
		 * 
		 * @param ev
		 *                  triggered event
		 */
		public final void messagesRemoved(MessageCountEvent ev) {
		}
	}
}
