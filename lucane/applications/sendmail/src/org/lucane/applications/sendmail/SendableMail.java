/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.applications.sendmail;

import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * A utility class to send mails
 */
public class SendableMail
{
	private MimeMultipart mime;
	private MimeMessage message;
	private ArrayList tempfiles;

	/**
	 * Constructor.
	 * Creates an empty mail.
	 */
	public SendableMail()
	{
		tempfiles = new ArrayList();

		//session creation
		Properties props = new Properties();
		props.put("mail.smtp.host", SendMailService.SMTP_HOST);
		Session session = Session.getDefaultInstance(props);

		//message creation
		message = new MimeMessage(session);
		mime = new MimeMultipart();
	}

	/**
	 * Set the from field
	 * 
	 * @param from the "from:" email
	 */
	public void setFrom(String from) 
	throws MessagingException
	{
		message.setFrom(new InternetAddress(from));
	}

	/**
	 * Set the subjet field
	 * 
	 * @param subject the subject
	 */
	public void setSubject(String subject) 
	throws MessagingException
	{
		message.setSubject(subject, "iso-8859-15");
	}

	/**
	 * Add To: receivers
	 * 
	 * @param to
	 */
	public void addTo(String to) 
	throws MessagingException
	{
		StringTokenizer str = new StringTokenizer(to, ";,");
		while (str.hasMoreElements())
			message.addRecipient(Message.RecipientType.TO, new InternetAddress((String)str.nextElement()));
	}

	/**
	 * Add Cc: receivers
	 * 
	 * @param cc
	 */
	public void addCc(String cc) 
	throws MessagingException
	{
		StringTokenizer str = new StringTokenizer(cc, ";,");
		while (str.hasMoreElements())
			message.addRecipient(Message.RecipientType.CC, new InternetAddress((String)str.nextElement()));
	}

	/**
	 * Add Bcc: receivers
	 * 
	 * @param bcc
	 */
	public void addBcc(String bcc) 
	throws MessagingException
	{
		StringTokenizer str = new StringTokenizer(bcc, ";,");
		while (str.hasMoreElements())
			message.addRecipient(Message.RecipientType.BCC, new InternetAddress((String)str.nextElement()));
	}

	/**
	 * Set the content of the mail
	 * 
	 * @param content the data
	 * @param type the data type (text/plain, text/html, ...)
	 */
	public void setContent(String content, String type) 
	throws MessagingException
	{
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setContent(content, type);
		mime.addBodyPart(mbp);
	}

	/**
	 * Remove temporary files (used for attachments)
	 */
	protected void finalize() throws Throwable
	{
		for (int i = 0; i < tempfiles.size(); i++)
			 ((File)tempfiles.get(i)).delete();
	}

	/**
	 * Attach a file
	 * 
	 * @param filename the filename to be displayed
	 * @param content the attach content
	 */
	public void attach(String filename, String content) 
	throws IOException, MessagingException
	{
		MimeBodyPart mbp = new MimeBodyPart();

		File file = File.createTempFile("mail", ".tmp");
		tempfiles.add(file);

		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content);
		} finally {
			if(fw != null)
				fw.close();
		}
		
		FileDataSource fds = new FileDataSource(file);
		DataHandler dh = new DataHandler(fds);
		mbp.setFileName(filename);
		mbp.setDisposition(Part.ATTACHMENT);
		mbp.setDataHandler(dh);

		mime.addBodyPart(mbp);
	}

	/**
	 * Send the message
	 */
	public void send() 
	throws MessagingException
	{
		message.setContent(mime);
		message.setSentDate(new Date());
		Transport.send(message);
	}
}
