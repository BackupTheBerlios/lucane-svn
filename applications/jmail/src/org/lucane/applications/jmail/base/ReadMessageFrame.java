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
import javax.mail.internet.*;
import javax.swing.*;

import org.lucane.applications.jmail.JMailPlugin;

/** Window displayed when reading a mail */
final class ReadMessageFrame extends JFrame
{
	/** Language resource */
	private ResourceBundle msgBundle;

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenuItem newMenuItem;
	private JMenuItem moveMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem printMenuItem;
	private JMenuItem closeMenuItem;

	private JMenu messageMenu;
	private JMenuItem newMenuItem2;
	private JMenuItem replyMenuItem;
	private JMenuItem replyAllMenuItem;
	private JMenuItem forwardMenuItem;

	private JPanel panel;

	private JToolBar toolBar;

	private JButton reply;
	private JButton replyall;
	private JButton forward;
	private JButton print;
	private JButton delete;

	private JLabel exp;
	private JLabel dest;
	private JLabel cc;
	private JLabel date;
	private JLabel subject;

	private JButton attachment;

	private JTextArea area;
	private JScrollPane scrollPane;

	private JPanel fields;

	private Profile profile;

	private Message mail;

	private ReadMessageListener listener;

	private MainPanel parent;

	private Store store;
	private Folder currentFolder;

	private String content;

	private JMailPlugin plugin;

	/** Constructor
	 *  @param store the connection
	 *  @param currentFolder current folder
	 *  @param parent the parent window
	 *  @param profile user profile
	 *  @param msg the mail to be read
	 *  @param content mail body
	 *  @param msgBungle language resource
	 *  @param attachmentVisible tells wether the "Attachment" button has to be visible or not
	 */
	public ReadMessageFrame(
		JMailPlugin plugin,
		Store store,
		Folder currentFolder,
		MainPanel parent,
		Profile profile,
		Message msg,
		String content,
		ResourceBundle msgBundle,
		boolean attachmentVisible)
	{
		super();
		this.plugin = plugin;

		this.msgBundle = msgBundle;

		this.content = content;

		this.store = store;
		this.currentFolder = currentFolder;

		this.parent = parent;

		listener = new ReadMessageListener();

		String currentSubject = new String();

		try
		{
			currentSubject = msg.getSubject();
		}

		catch (Exception e)
		{
			currentSubject = msgBundle.getString("ReadMessage.noSubjectLabel");
		}

		if (currentSubject == null)
			currentSubject = "";

		setTitle(currentSubject);

		this.profile = profile;
		this.mail = msg;

		menuBar = new JMenuBar();

		fileMenu = new JMenu(msgBundle.getString("ReadMessage.fileMenuLabel"));
		fileMenu.setMnemonic('F');

		newMenuItem = fileMenu.add(msgBundle.getString("ReadMessage.newMenuItemLabel"));
		newMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		moveMenuItem = fileMenu.add(msgBundle.getString("ReadMessage.moveMenuItemLabel"));
		moveMenuItem.setMnemonic('D');
		moveMenuItem.addActionListener(listener);

		copyMenuItem = fileMenu.add(msgBundle.getString("ReadMessage.copyMenuItemLabel"));
		copyMenuItem.addActionListener(listener);

		deleteMenuItem = fileMenu.add(msgBundle.getString("ReadMessage.deleteMenuItemLabel"));
		deleteMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		printMenuItem = fileMenu.add(msgBundle.getString("ReadMessage.printMenuItemLabel"));
		printMenuItem.setEnabled(false);
		printMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		closeMenuItem = fileMenu.add(msgBundle.getString("ReadMessage.closeMenuItemLabel"));
		closeMenuItem.setMnemonic('F');
		closeMenuItem.addActionListener(listener);

		fileMenu = menuBar.add(fileMenu);

		messageMenu = new JMenu(msgBundle.getString("ReadMessage.messageMenuLabel"));
		messageMenu.setMnemonic('M');

		newMenuItem2 = messageMenu.add(msgBundle.getString("ReadMessage.newMenuItem2Label"));
		newMenuItem2.addActionListener(listener);

		messageMenu.addSeparator();

		replyMenuItem = messageMenu.add(msgBundle.getString("ReadMessage.replyMenuItemLabel"));
		replyMenuItem.setAccelerator(KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
		replyMenuItem.setMnemonic('R');
		replyMenuItem.addActionListener(listener);

		replyAllMenuItem = messageMenu.add(msgBundle.getString("ReadMessage.replyAllMenuItemLabel"));
		replyAllMenuItem.addActionListener(listener);

		forwardMenuItem = messageMenu.add(msgBundle.getString("ReadMessage.forwardMenuItemLabel"));
		forwardMenuItem.addActionListener(listener);

		messageMenu = menuBar.add(messageMenu);

		setJMenuBar(menuBar);

		panel = new JPanel(new BorderLayout());

		toolBar = new JToolBar();
		toolBar.setFloatable(true);

		reply = new JButton(plugin.getIcon("reply.png"));
		reply.setToolTipText(msgBundle.getString("ReadMessage.replyLabel"));
		reply.addActionListener(listener);

		toolBar.add(reply);

		replyall = new JButton(plugin.getIcon("replyall.png"));
		replyall.setToolTipText(msgBundle.getString("ReadMessage.replyallLabel"));
		replyall.addActionListener(listener);

		toolBar.add(replyall);

		forward = new JButton(plugin.getIcon("forward.png"));
		forward.setToolTipText(msgBundle.getString("ReadMessage.forwardLabel"));
		forward.addActionListener(listener);

		toolBar.add(forward);

		toolBar.addSeparator();

		print = new JButton(plugin.getIcon("print.png"));
		print.setEnabled(false);
		print.setToolTipText(msgBundle.getString("ReadMessage.printLabel"));
		print.addActionListener(listener);

		toolBar.add(print);

		delete = new JButton(plugin.getIcon("delete.png"));
		delete.setToolTipText(msgBundle.getString("ReadMessage.deleteLabel"));
		delete.addActionListener(listener);

		toolBar.add(delete);

		panel.add("North", toolBar);

		fields = new JPanel();
		fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

		try
		{
			Address a[] = mail.getFrom();
			exp = new JLabel(msgBundle.getString("ReadMessage.expLabel") + " " + a[0].toString());
			fields.add(exp);

			a = mail.getRecipients(Message.RecipientType.TO);

			StringBuffer to = new StringBuffer(msgBundle.getString("ReadMessage.toLabel") + " ");

			if (a != null)
			{
				to.append(a[0].toString());

				for (int i = 1; i < a.length; i++)
					to.append(", " + a[i].toString());
			}

			dest = new JLabel(to.toString());

			fields.add(dest);

			a = mail.getRecipients(Message.RecipientType.CC);

			StringBuffer ccString = new StringBuffer("CC : ");

			if (a != null)
			{
				ccString.append(a[0].toString());

				for (int i = 1; i < a.length; i++)
					ccString.append(", " + a[i].toString());

				cc = new JLabel(ccString.toString());
				fields.add(cc);
			}

			Date d = mail.getSentDate();

			if (d != null)
				date = new JLabel(msgBundle.getString("ReadMessage.dateLabel") + " " + d.toString());

			else
				date = new JLabel(msgBundle.getString("ReadMessage.dateLabel"));

			fields.add(date);

			subject = new JLabel(msgBundle.getString("ReadMessage.subjectLabel") + " " + currentSubject);
			fields.add(subject);

			area = new JTextArea(20, 30);
			area.setLineWrap(true);
			area.setEditable(false);

			if (attachmentVisible)
			{
				attachment = new JButton(msgBundle.getString("ReadMessage.attachmentLabel"));
				attachment.addActionListener(listener);
				fields.add(attachment);
			}

			area.setText(content);
			area.setCaretPosition(0);

			scrollPane = new JScrollPane(area);
			fields.add(scrollPane);
		}

		catch (AddressException addressEx)
		{
			addressEx.printStackTrace();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		panel.add("Center", fields);

		setContentPane(panel);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(500, 480);

		setVisible(true);
	}

	/** Listener for this class */
	private final class ReadMessageListener implements ActionListener
	{
		/** This method is invoked when an event is triggered
		 *  @param e event
		 */
		public final void actionPerformed(ActionEvent e)
		{
			AbstractButton b = (AbstractButton) e.getSource();

			if (b == newMenuItem || b == newMenuItem2)
			{
				SendMessageDialog sm = new SendMessageDialog(plugin, profile, msgBundle);
				sm.dispose();
			}

			else if (b == moveMenuItem)
			{
				String folder = new String();

				try
				{
					folder = mail.getFolder().toString();
					new MoveMessageFrame(
						store,
						currentFolder,
						profile,
						folder,
						((MimeMessage) mail).getMessageID(),
						0,
						msgBundle);
					parent.update();
					dispose();
				}

				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			else if (b == copyMenuItem)
			{
				String folder = new String();

				try
				{
					folder = mail.getFolder().toString();

					new MoveMessageFrame(
						store,
						currentFolder,
						profile,
						folder,
						((MimeMessage) mail).getMessageID(),
						1,
						msgBundle);
				}

				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			else if (b == deleteMenuItem || b == delete)
			{
				try
				{
					boolean bool = MailClient.deleteMessage(store, currentFolder, ((MimeMessage) mail).getMessageID());

					if (bool)
					{
						JOptionPane.showMessageDialog(
							null,
							msgBundle.getString("ReadMessage.msgDeletedLabel"),
							"ReadMessage",
							JOptionPane.INFORMATION_MESSAGE);
						parent.update();
						dispose();
					}
				}

				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			else if (b == printMenuItem || b == print)
			{
				/** TODO : print code */
			}

			else if (b == closeMenuItem)
				dispose();

			else if (b == replyMenuItem || b == reply)
			{
				try
				{
					Address a[] = mail.getReplyTo();

					String currentSubject = mail.getSubject();

					if (currentSubject == null)
						currentSubject = "";

					currentSubject = "Re: " + currentSubject;

					String content = new String();

					if (mail.isMimeType("text/plain"))
						content = (String) mail.getContent();

					else
						content = msgBundle.getString("ReadMessage.unreadableMsgLabel");

					SendMessageDialog sm =
						new SendMessageDialog(plugin, profile, a[0].toString(), currentSubject, content, msgBundle);
					sm.dispose();
				}

				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			else if (b == replyAllMenuItem || b == replyall)
			{
				try
				{
					Address a[] = mail.getReplyTo();

					String currentSubject = mail.getSubject();

					if (currentSubject == null)
						currentSubject = "";

					currentSubject = "Re: " + currentSubject;

					String content = new String();

					if (mail.isMimeType("text/plain"))
						content = (String) mail.getContent();

					else
						content = msgBundle.getString("ReadMessage.unreadableMsgLabel");

					Address to[] = mail.getRecipients(Message.RecipientType.TO);

					Address cc[] = mail.getRecipients(Message.RecipientType.CC);

					String copies[] = new String[0];

					if (cc != null)
					{
						if (to != null)
						{
							copies = new String[to.length + cc.length];

							int i;

							for (i = 0; i < to.length; i++)
								copies[i] = to[i].toString();

							for (int j = 0; j < cc.length; j++)
								copies[i++] = cc[j].toString();
						}

						else
						{
							copies = new String[cc.length];

							for (int i = 0; i < cc.length; i++)
								copies[i] = cc[i].toString();
						}
					}

					else if (to != null)
					{
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
							content,
							msgBundle);
					sm.dispose();
				}

				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			else if (b == forwardMenuItem || b == forward)
			{
				try
				{
					String currentSubject = mail.getSubject();

					if (currentSubject == null)
						currentSubject = "";

					currentSubject = "Fwd: " + currentSubject;

					String content = new String();

					if (mail.isMimeType("text/plain"))
						content = (String) mail.getContent();
					else
						content = msgBundle.getString("ReadMessage.unreadableMsgLabel");

					SendMessageDialog sm = new SendMessageDialog(plugin, profile, currentSubject, content, msgBundle);
					sm.dispose();
				}

				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			else if (b == attachment)
				new AttachmentFrame(mail, msgBundle);
		}
	}
}
