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
import java.util.*;
import javax.swing.*;

import org.lucane.applications.jmail.JMailPlugin;

/** Display the mail composition window */
final class SendMessageDialog extends JDialog {
	/** Language resource */
	private ResourceBundle msgBundle;

	private JPanel panel;

	private JMenuBar menuBar;

	private JMenu fileMenu;
	private JMenuItem newMenuItem;
	private JMenuItem sendMenuItem;
	private JMenuItem printMenuItem;
	private JMenuItem closeMenuItem;

	private JMenu viewMenu;
	private JCheckBoxMenuItem ccMenuItem;
	private JCheckBoxMenuItem bccMenuItem;

	private JMenu insertMenu;
	private JMenuItem attachMenuItem;
	private JMenuItem textMenuItem;

	private JMenu messageMenu;
	private JMenuItem sendMsgMenuItem;

	private JToolBar toolBar;

	private JButton send;
	private JButton attachment;

	private JPanel fields;

	private JButton dest;
	private JTextField destTextField;

	private JButton cc;
	private JTextField ccTextField;

	private JButton bcc;
	private JTextField bccTextField;

	private JLabel subject;
	private JTextField subjectField;

	private JLabel attach;
	private JList files;
	private Vector fileNames;
	private JScrollPane attachPane;

	private JTextArea content;
	private JScrollPane contentPane;

	private Profile profile;

	private SendMessageListener listener;

	private boolean sentMail;

	private JMailPlugin plugin;

	/**
	 * Default constructor
	 * 
	 * @param profile
	 *                  user profile
	 * @param msgBundle
	 *                  language resource
	 */
	protected SendMessageDialog(JMailPlugin plugin, Profile profile, ResourceBundle msgBundle) {
		//super(msgBundle.getString("SendMessage.frameTitle"));
		super(
			(JFrame) null,
			msgBundle.getString("SendMessage.frameTitle"),
			true);

		this.plugin = plugin;
		this.msgBundle = msgBundle;
		this.profile = profile;

		init();

		if (profile.getSignature() != null
			&& profile.getSignature().compareTo("") != 0)
			insertTextFile(profile.getSignature());

		setSize(455, 655);
		pack();
		setVisible(true);
	}

	/**
	 * Reply constructor
	 * 
	 * @param profile
	 *                  user profile
	 * @param desti
	 *                  mail recipient
	 * @param subj
	 *                  mail subject
	 * @param mailContent
	 *                  body of the mail
	 * @param msgBundle
	 *                  language resource
	 */
	protected SendMessageDialog(JMailPlugin plugin,
		Profile profile,
		String desti,
		String subj,
		String mailContent,
		ResourceBundle msgBundle) {
		//super(subj);
		super((JFrame) null, subj, true);
		this.plugin = plugin;

		this.msgBundle = msgBundle;
		this.profile = profile;

		init();

		destTextField.setText(desti);
		subjectField.setText(subj);

		content.setText(
			msgBundle.getString("SendMessage.originalMsgLabel") + "\n");
		content.append(mailContent);

		int nb = content.getLineCount();

		try {
			for (int i = 0; i < nb; i++) {
				int x = content.getLineStartOffset(i);
				content.insert("> ", x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		content.append("\n");

		if (profile.getSignature() != null
			&& profile.getSignature().compareTo("") != 0)
			insertTextFile(profile.getSignature());

		content.setCaretPosition(0);

		setSize(455, 655);
		pack();
		setVisible(true);
	}

	/**
	 * Forward constructor
	 * 
	 * @param profile
	 *                  user profile
	 * @param subj
	 *                  mail subject
	 * @param mailContent
	 *                  mail body
	 * @param msgBundle
	 *                  language resource
	 */
	protected SendMessageDialog(JMailPlugin plugin,
		Profile profile,
		String subj,
		String mailContent,
		ResourceBundle msgBundle) {
		//super(subj);
		super((JFrame) null, subj, true);

		this.plugin = plugin;
		this.msgBundle = msgBundle;
		this.profile = profile;

		init();

		subject.setText(subj);

		content.setText(
			msgBundle.getString("SendMessage.originalMsgLabel") + "\n");
		content.append(mailContent);

		int nb = content.getLineCount();

		try {
			for (int i = 0; i < nb; i++) {
				int x = content.getLineStartOffset(i);
				content.insert("> ", x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		content.append("\n");

		if (profile.getSignature() != null
			&& profile.getSignature().compareTo("") != 0)
			insertTextFile(profile.getSignature());

		content.setCaretPosition(0);

		setSize(455, 655);
		pack();
		setVisible(true);
	}

	/**
	 * Reply-all constructor
	 * 
	 * @param profile
	 *                  user profile
	 * @param desti
	 *                  main recipient
	 * @param copies
	 *                  extra recipients
	 * @param subj
	 *                  mail subject
	 * @param mailContent
	 *                  mail body
	 * @param msgBundle
	 *                  language resource
	 */
	protected SendMessageDialog(JMailPlugin plugin,
		Profile profile,
		String desti,
		String copies[],
		String subj,
		String mailContent,
		ResourceBundle msgBundle) {
		//super(subj);
		super((JFrame) null, subj, true);

		this.plugin = plugin;
		this.msgBundle = msgBundle;
		this.profile = profile;

		init();

		destTextField.setText(desti);

		if (copies.length > 0) {
			StringBuffer cc = new StringBuffer(copies[0]);

			for (int i = 1; i < copies.length; i++)
				cc.append(", " + copies[i]);

			ccTextField.setText(cc.toString());
		}

		subject.setText(subj);

		content.setText(
			msgBundle.getString("SendMessage.originalMsgLabel") + "\n");
		content.append(mailContent);

		int nb = content.getLineCount();

		try {
			for (int i = 0; i < nb; i++) {
				int x = content.getLineStartOffset(i);
				content.insert("> ", x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		content.append("\n");

		if (profile.getSignature() != null
			&& profile.getSignature().compareTo("") != 0)
			insertTextFile(profile.getSignature());

		content.setCaretPosition(0);

		setSize(455, 655);
		pack();
		setVisible(true);
	}

	/** Does the init stuff */
	private void init() {
		listener = new SendMessageListener();

		menuBar = new JMenuBar();

		fileMenu = new JMenu(msgBundle.getString("common.fileMenu"));
		fileMenu.setMnemonic('F');

		newMenuItem =
			fileMenu.add(msgBundle.getString("common.new"));
		newMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
		newMenuItem.setMnemonic('N');
		newMenuItem.addActionListener(listener);

		sendMenuItem =
			fileMenu.add(msgBundle.getString("SendMessage.sendMenuItemLabel"));
		sendMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		printMenuItem =
			fileMenu.add(msgBundle.getString("common.print"));
		printMenuItem.setEnabled(false);
		printMenuItem.addActionListener(listener);

		fileMenu.addSeparator();

		closeMenuItem =
			fileMenu.add(msgBundle.getString("SendMessage.closeMenuItemLabel"));
		closeMenuItem.setMnemonic('F');
		closeMenuItem.addActionListener(listener);

		fileMenu = menuBar.add(fileMenu);

		viewMenu = new JMenu(msgBundle.getString("SendMessage.viewMenuLabel"));
		viewMenu.setMnemonic('V');

		ccMenuItem = new JCheckBoxMenuItem("CC", true);
		ccMenuItem.addActionListener(listener);
		viewMenu.add(ccMenuItem);

		bccMenuItem = new JCheckBoxMenuItem("BCC", true);
		bccMenuItem.addActionListener(listener);
		viewMenu.add(bccMenuItem);

		viewMenu = menuBar.add(viewMenu);

		insertMenu =
			new JMenu(msgBundle.getString("SendMessage.insertMenuLabel"));
		insertMenu.setMnemonic('I');

		attachMenuItem =
			insertMenu.add(
				msgBundle.getString("SendMessage.attachMenuItemLabel"));
		attachMenuItem.addActionListener(listener);

		textMenuItem =
			insertMenu.add(
				msgBundle.getString("SendMessage.textMenuItemLabel"));
		textMenuItem.addActionListener(listener);

		insertMenu = menuBar.add(insertMenu);

		messageMenu =
			new JMenu(msgBundle.getString("common.mail"));
		messageMenu.setMnemonic('M');

		sendMsgMenuItem =
			messageMenu.add(
				msgBundle.getString("SendMessage.sendMenuItemLabel"));
		sendMsgMenuItem.setAccelerator(
			KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
		sendMsgMenuItem.setMnemonic('E');
		sendMsgMenuItem.addActionListener(listener);
		messageMenu = menuBar.add(messageMenu);

		setJMenuBar(menuBar);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		toolBar = new JToolBar();
		toolBar.setFloatable(true);

		send = new JButton(plugin.getIcon("send.png"));
		send.setToolTipText(msgBundle.getString("SendMessage.sendLabel"));
		send.addActionListener(listener);
		toolBar.add(send);

		toolBar.addSeparator();

		attachment =
			new JButton(plugin.getIcon("attach.png"));
		attachment.setToolTipText(
			msgBundle.getString("SendMessage.attachmentLabel"));
		attachment.addActionListener(listener);
		toolBar.add(attachment);

		panel.add("North", toolBar);

		fields = new JPanel();

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		fields.setLayout(gridBag);

		Common.buildConstraints(constraints, 0, 0, 1, 1, 30, 10);
		dest = new JButton("To : ");
		dest.addActionListener(listener);
		gridBag.setConstraints(dest, constraints);
		fields.add(dest);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		Common.buildConstraints(constraints, 1, 0, 1, 1, 70, 0);
		destTextField = new JTextField(30);
		gridBag.setConstraints(destTextField, constraints);
		fields.add(destTextField);

		constraints.fill = GridBagConstraints.NONE;
		Common.buildConstraints(constraints, 0, 1, 1, 1, 0, 10);
		cc = new JButton("Cc : ");
		cc.addActionListener(listener);
		gridBag.setConstraints(cc, constraints);
		fields.add(cc);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		Common.buildConstraints(constraints, 1, 1, 1, 1, 0, 0);
		ccTextField = new JTextField(30);
		gridBag.setConstraints(ccTextField, constraints);
		fields.add(ccTextField);

		constraints.fill = GridBagConstraints.NONE;
		Common.buildConstraints(constraints, 0, 2, 1, 1, 0, 10);
		bcc = new JButton("Bcc : ");
		bcc.addActionListener(listener);
		gridBag.setConstraints(bcc, constraints);
		fields.add(bcc);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		Common.buildConstraints(constraints, 1, 2, 1, 1, 0, 0);
		bccTextField = new JTextField(30);
		gridBag.setConstraints(bccTextField, constraints);
		fields.add(bccTextField);

		constraints.fill = GridBagConstraints.NONE;
		Common.buildConstraints(constraints, 0, 3, 1, 1, 0, 10);
		subject = new JLabel(msgBundle.getString("SendMessage.subjectLabel"));
		gridBag.setConstraints(subject, constraints);
		fields.add(subject);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		Common.buildConstraints(constraints, 1, 3, 1, 1, 0, 0);
		subjectField = new JTextField(30);
		gridBag.setConstraints(subjectField, constraints);
		fields.add(subjectField);

		constraints.fill = GridBagConstraints.NONE;
		Common.buildConstraints(constraints, 0, 4, 1, 1, 0, 10);
		attach = new JLabel(msgBundle.getString("SendMessage.attachLabel"));
		attach.setEnabled(false);
		gridBag.setConstraints(attach, constraints);
		fields.add(attach);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		Common.buildConstraints(constraints, 1, 4, 1, 1, 0, 0);
		fileNames = new Vector();
		files = new JList(fileNames);
		files.setEnabled(false);
		attachPane =
			new JScrollPane(
				files,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		attachPane.setPreferredSize(new Dimension(200, 20));
		gridBag.setConstraints(attachPane, constraints);
		attachPane.setEnabled(false);
		fields.add(attachPane);

		constraints.fill = GridBagConstraints.NONE;
		Common.buildConstraints(constraints, 0, 5, 2, 1, 0, 50);
		content = new JTextArea(20, 30);
		content.setLineWrap(true);
		contentPane = new JScrollPane(content);
		gridBag.setConstraints(contentPane, constraints);
		fields.add(contentPane);

		panel.add("Center", fields);

		setContentPane(panel);

		sentMail = false;
	}

	private void insertTextFile(String name) {
		try {
			int pos = content.getCaretPosition();

			BufferedReader in = new BufferedReader(new FileReader(name));

			StringBuffer text = new StringBuffer();
			String line = new String();

			while ((line = in.readLine()) != null)
				text.append(line + "\n");

			in.close();

			content.insert(text.toString(), pos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected boolean sentMail() {
		return (sentMail);
	}

	/** Listener for this class */
	private final class SendMessageListener
		implements ActionListener {

		/**
		 * This method is invoked when an event is triggered
		 * 
		 * @param e
		 *                  event
		 */
		public final void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton) e.getSource();

			if (b == newMenuItem) {
				SendMessageDialog sm = new SendMessageDialog(plugin, profile, msgBundle);
				sm.dispose();
			} else if (b == closeMenuItem)
				//dispose();
				hide();

			else if (b == ccMenuItem) {
				if (!ccMenuItem.getState() == true) {
					ccMenuItem.setSelected(false);
					cc.setVisible(false);
					ccTextField.setVisible(false);
				} else {
					ccMenuItem.setSelected(true);
					cc.setVisible(true);
					ccTextField.setVisible(true);
				}
			} else if (b == bccMenuItem) {
				if (!bccMenuItem.getState()) {
					bccMenuItem.setSelected(false);
					bcc.setVisible(false);
					bccTextField.setVisible(false);
				} else {
					bccMenuItem.setSelected(true);
					bcc.setVisible(true);
					bccTextField.setVisible(true);
				}
			} else if (b == textMenuItem) {
				JFileChooser fc = new JFileChooser();

				int val = fc.showOpenDialog(null);

				if (val == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					if (file != null) {
						insertTextFile(file.getPath());

						/*
						 * try { int pos = content.getCaretPosition();
						 * 
						 * BufferedReader in = new BufferedReader(new
						 * FileReader(file));
						 * 
						 * StringBuffer text = new StringBuffer(); String line =
						 * new String();
						 * 
						 * while((line = in.readLine()) != null)
						 * text.append(line + "\n");
						 * 
						 * in.close();
						 * 
						 * content.insert(text.toString(), pos); }
						 * 
						 * catch(Exception ex) { ex.printStackTrace(); }
						 */
					}
				}
			} else if (
				b == sendMenuItem || b == send || b == sendMsgMenuItem) {
				StringTokenizer str =
					new StringTokenizer(destTextField.getText(), ",");
				String dest[] = new String[str.countTokens()];
				int index = 0;

				while (str.hasMoreTokens())
					dest[index++] = str.nextToken();

				str = new StringTokenizer(ccTextField.getText(), ",");
				String cc[] = new String[str.countTokens()];

				index = 0;

				while (str.hasMoreTokens())
					cc[index++] = str.nextToken();

				str = new StringTokenizer(bccTextField.getText(), ",");
				String bcc[] = new String[str.countTokens()];

				index = 0;

				while (str.hasMoreTokens())
					bcc[index++] = str.nextToken();

				if (MailClient
					.sendMsg(
						dest,
						cc,
						bcc,
						subjectField.getText(),
						content.getText(),
						fileNames,
						profile)) {
					JOptionPane.showMessageDialog(
						null,
						msgBundle.getString("SendMessage.msgSentLabel"),
						"SendMessage",
						JOptionPane.INFORMATION_MESSAGE);
					sentMail = true;

					//dispose();
					hide();
				}
			} else if (b == attachment || b == attachMenuItem) {
				JFileChooser fc = new JFileChooser();

				int val = fc.showOpenDialog(null);

				if (val == JFileChooser.APPROVE_OPTION) {
					attachPane.setMinimumSize(new Dimension(200, 40));

					File file = fc.getSelectedFile();

					if (file != null) {
						String name = file.getPath();
						fileNames.add(name);
						files.setListData(fileNames);
						attach.setEnabled(true);
						attachPane.setEnabled(true);
						files.setEnabled(true);
					}
				}
			}
		}
	}
}
