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

import java.io.*;
import java.util.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.lucane.client.widgets.DialogBox;

/** Class allowing to interact with mail servers */
final class MailClient
{
	/** 
	 * Makes the connection to the mail server
	 * @param profile user profile
	 * @return the connection
	 */
	protected final static Store connect(Profile profile)
	{
		try {
			// Get OS's parameters
			Properties props = System.getProperties();

			// This is the trick to get SSL working.. Thanks to Maximilian Schwerin 
			//        ( http://jaymail.sourceforge.net/ )
			if (profile.getUseSSL())
				props.setProperty("mail.imap.socketFactory.class", "JMailSSLSocketFactory");
			else
			{
				// because an IMAP/POP connection without SSL would fail if the prop is set
				//            FIXME : it seems tough to prevent further non-SSL connections to check mails
				//            every three minutes :-\ 
				props.remove("mail.imap.socketFactory.class");
			}

			Session s = Session.getInstance(props);
			//s.setDebug(false);

			/** Connection */
			Store store = s.getStore(profile.getType());
			store.connect(profile.getIncoming(), profile.getIncomingPort(),
				profile.getLogin(),	profile.getDecryptedMailPassword());

			return store;
		} catch(Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return null;
		}
	}

	/** 
	 * Get folders names
	 * @param store the connection
	 * @param profile user profile
	 * @return tree showing folders
	 */
	protected final static JTree getFolders(Store store, Profile profile)
	{
		DefaultMutableTreeNode top;

		top = new DefaultMutableTreeNode("Mailbox");
		DefaultTreeModel model = new DefaultTreeModel(top);

		try {
			if (profile.getType().compareTo("pop3") == 0)
				top.add(new DefaultMutableTreeNode(store.getFolder("INBOX").getFullName()));
			else //if(profile.getType().compareTo("imap") == 0)
			{
				//add default folders first if available
				try {
					top.add(getFolder(store.getFolder("INBOX")));
				} catch(Exception e) {}
				try {
					top.add(getFolder(store.getFolder("Sent")));
				} catch(Exception e) {}
				try {
					top.add(getFolder(store.getFolder("Drafts")));
				} catch(Exception e) {}
				try {
					top.add(getFolder(store.getFolder("Trash")));
				} catch(Exception e) {}

				//add user defined folders
				Folder folder = store.getDefaultFolder();
				if(folder != null)
				{
					TreeNode node = getFolder(folder);
					for(int i=0;i<node.getChildCount();i++)
					{
						DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
						if(!alreadyHasFolder(top, (String)child.getUserObject()))
							top.add((MutableTreeNode)node.getChildAt(i));
					}
				}
			}

			return new JTree(model);
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return null;
		}
	}

	/** 
	 * Recursive function to browse folders
	 * @param f base folder
	 * @return sub-tree
	 */
	private static DefaultMutableTreeNode getFolder(Folder f)
	{
		try	{
			//TODO check if this doesn't break anything to change this
			//DefaultMutableTreeNode node = new DefaultMutableTreeNode(f.getName());
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(f.getFullName());
			
			Folder sons[] = f.listSubscribed();

			if (sons != null)
			{	
				for (int i = 0; i < sons.length; i++)
				{
					if(!alreadyHasFolder(node, sons[i].getFullName()))
						node.add(getFolder(sons[i]));
				}
			}
			return node;
		} catch (Exception e) {
			return null;
		}
	}


	private static boolean alreadyHasFolder(DefaultMutableTreeNode node, String folder) 
	{
		if(node.getChildCount() > 0)
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getFirstChild();
			while(child != null)
			{	
				if(child.getUserObject().equals(folder))
					return true;
			
				child = child.getNextSibling();
			}
		}
		return false;
	}

	/** 
	 * Delete a mail on the server
	 * @param store the connection
	 * @param currentFolder folder holding the mail
	 * @param id mail-to-delete's id
	 * @return boolean saying wether the operation succeded or not
	 */
	protected final static boolean deleteMessage(Store store, Folder currentFolder, String id)
	{
		try	{
			// Get the messages 
			Message msg[] = currentFolder.getMessages();
			String currentId = null;

			for(int i = 0; i < msg.length; i++)
			{
				currentId = ((MimeMessage) msg[i]).getMessageID();

				if(currentId != null)
				{
					// If we have found the mail to delete
					if (currentId.compareTo(id) == 0)
					{
						// Set the flag
						msg[i].setFlag(Flags.Flag.DELETED, true);
						break;
					}
				}
			}

			return true;
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return false;
		}
	}

	/** 
	 * Copy a mail from a folder to another
	 * @param store the connection
	 * @param currentFolder folder holding the mail
	 * @param id mail-to-copy's id
	 * @param dest destination folder
	 * @return boolean saying wether the operation succeded
	 */
	protected final static boolean copyMsg(Store store, Folder currentFolder, String id, String dest)
	{
		try	{
			// Get the messages 
			Message msg[] = currentFolder.getMessages();

			// Array which will get the mail 
			Message moved[] = new Message[1];

			int i;
			String currentId = null;

			for(i=0; i<msg.length; i++)
			{
				currentId = ((MimeMessage) msg[i]).getMessageID();
				if(currentId != null)
				{
					if(currentId.compareTo(id) == 0)
					{
						// We copy the mail
						moved[0] = msg[i];
						break;
					}
				}
			}

			// Open the destinatory folder
			Folder f2 = store.getFolder(dest);
			f2.open(Folder.READ_WRITE);
			currentFolder.copyMessages(moved, f2);
			f2.close(false);

			return true;
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return false;
		}
	}

	/** 
	 * Move a mail from a folder to another
	 * @param store the connection
	 * @param currentFolder folder containing the mail
	 * @param id mail-to-move's id
	 * @param dest destinatory target
	 * @return boolean saying wether the operation has succeded
	 */
	protected final static boolean moveMsg(Store store, Folder currentFolder, String id, String dest)
	{
		try	{
			// Get the messages
			Message msg[] = currentFolder.getMessages();

			// Array for the mail to move
			Message moved[] = new Message[1];

			int i;
			String currentId = null;

			for(i=0;i<msg.length;i++)
			{
				currentId = ((MimeMessage) msg[i]).getMessageID();

				if(currentId != null)
				{
					if(((MimeMessage) msg[i]).getMessageID().compareTo(id) == 0)
					{
						// We copy the mail
						moved[0] = msg[i];
						break;
					}
				}
			}

			Folder f2 = store.getFolder(dest);
			if (!f2.isOpen())
				f2.open(Folder.READ_WRITE);
			f2.appendMessages(moved);
			f2.close(false);

			msg[i].setFlag(Flags.Flag.DELETED, true);

			return true;
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return false;
		}
	}

	/** 
	 * Delete a mail folder
	 * @param currentFolder folder to delete
	 * @return boolean telling wether the operation succeeded or not
	 */
	protected final static boolean deleteFolder(Folder currentFolder)
	{
		try	{
			if (currentFolder.isOpen())
				currentFolder.close(true);
			return currentFolder.delete(true);
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return false;
		}
	}

	/** 
	 * Allows to create a folder on the mail server
	 * @param store the connection
	 * @param profile user profile
	 * @param name name of the folder
	 * @param type type of the folder (see <code>Folder</code>)
	 */
	protected final static void createFolder(Store store, Profile profile, String name, int type)
	{
		try	{
			Folder f = store.getFolder(name);
			f.create(type);
			f.setSubscribed(true);
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
		}
	}

	/** 
	 * Sends a mail
	 * @param dest the TO field
	 * @param cc the CC field
	 * @param bcc the BCC field
	 * @param subject subject of the mail
	 * @param content body of the message
	 * @param filenames attached files
	 * @param profile user profile
	 * @return a boolean telling wether the mail was succesfully sent or not
	 */
	protected final static boolean sendMsg(String dest[], String cc[], String bcc[],
		String subject, String content,	Vector filenames, Profile profile)
	{
		try	{
			// Set the SMTP server
			Properties p = System.getProperties();
			p.put("mail.smtp.host", profile.getOutgoing());

			// Get the OS's parameters
			Session s = Session.getInstance(System.getProperties());

			InternetAddress addr[] = new InternetAddress[dest.length];
			for (int i = 0; i < addr.length; i++)
				addr[i] = new InternetAddress(dest[i]);

			InternetAddress addrCC[] = new InternetAddress[cc.length];
			for (int i = 0; i < cc.length; i++)
				addrCC[i] = new InternetAddress(cc[i]);

			InternetAddress addrBCC[] = new InternetAddress[bcc.length];
			for (int i = 0; i < addrBCC.length; i++)
				addrBCC[i] = new InternetAddress(bcc[i]);

			InternetAddress a = new InternetAddress(profile.getEmail());
			Message m = new MimeMessage(s);
			m.setHeader("X-Mailer", "JMail " + Common.JMAIL_VERSION);

			String replyTo = profile.getReplyTo();
			if (replyTo != null && replyTo.compareTo("") != 0)
				m.setHeader("Reply-To", replyTo);

			m.setFrom(a);
			m.setRecipients(Message.RecipientType.TO, addr);
			m.setRecipients(Message.RecipientType.CC, addrCC);
			m.setRecipients(Message.RecipientType.BCC, addrBCC);
			m.setSubject(subject);

			int size = filenames.size();
			if (size == 0)
				m.setText(content);
			else
			{
				MimeMultipart mp = new MimeMultipart();
				MimeBodyPart bp1 = new MimeBodyPart();
				bp1.setContent(content, "text/plain");
				mp.addBodyPart(bp1);
				
				MimeBodyPart parts[] = new MimeBodyPart[size];
				String name = null;
				File f = null;
				String shortName = null;

				for(int i=0; i<size; i++)
				{
					parts[i] = new MimeBodyPart();
					name = (String) filenames.get(i);
					f = new File(name);
					shortName = f.getName();
					parts[i].setFileName(shortName);
					parts[i].setContent(shortName, "text/plain");
					parts[i].setDataHandler(new DataHandler(new FileDataSource(f)));
					mp.addBodyPart(parts[i]);
				}

				m.setContent(mp);
			}

			Transport t = s.getTransport(new URLName("smtp://" + profile.getOutgoing()));
			t.connect(profile.getOutgoing(), profile.getOutgoingPort(), profile.getLogin(), profile.getPassword());
			Transport.send(m);
			t.close();
		} catch (Exception e) {
			DialogBox.error("" + e);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
