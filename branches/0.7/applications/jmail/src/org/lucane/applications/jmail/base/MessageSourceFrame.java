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
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;

/** Simple frame allowing to view a message's source */
final class MessageSourceFrame extends JFrame
{
    private Message msg;
    
    private JPanel panel;
    private JTextArea area;
    private JScrollPane scrollPane;

    /** Constructor
     *  @param subject subject of the mail
     *  @param msg message
     */
    protected MessageSourceFrame(String subject, Message msg)
    {
	super(subject);

	this.msg = msg;

	panel = new JPanel();

	area = new JTextArea();
	area.setEditable(false);
	fillArea();
	area.setCaretPosition(0);

	area.setRows(30);
	area.setColumns(40);
	
	scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	panel.add(scrollPane);

	setContentPane(panel);

	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	pack();
	setResizable(false);
	setVisible(true);
    }

    /** Copies the mail in a temporary files and display its content */
    private void fillArea()
    {
	try
	{
	    String fileName = ((MimeMessage)msg).getMessageID();
	    fileName = fileName.substring(1, 10);

	    File file = File.createTempFile(fileName, null);

	    FileOutputStream out = new FileOutputStream(file);
	    msg.writeTo(out);
	    out.close();

	    BufferedReader in = new BufferedReader(new FileReader(file));
	    
	    String line = new String();

	    while((line = in.readLine()) != null)
		area.append(line + "\n");

	    in.close();

	    file.delete();
	}

	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
}
