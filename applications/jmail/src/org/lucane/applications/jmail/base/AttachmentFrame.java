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
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.swing.*;

/** A dialog containing list of attached files */
final class AttachmentFrame extends JFrame
{
    /** Language resource */
    private ResourceBundle msgBundle;

    private JPanel panel;

    private JPopupMenu filePopup;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;

    private JList list;
    private JScrollPane listPane;

    private Vector names;

    /** The message which contains the files */
    private Message mail;

    private AttachmentListener listener;

    /** Constructor
     *  @param mail the message
     *  @param msgBundle language resource
     */
    protected AttachmentFrame(Message mail, ResourceBundle msgBundle)
    {
        super(msgBundle.getString("Attachment.frameTitle"));

	this.msgBundle = msgBundle;

        listener = new AttachmentListener();

        this.mail = mail;

        panel = new JPanel();

        names = new Vector();
	names = getFiles(mail, names);

        list = new JList(names);
        list.addMouseListener(listener);
        listPane = new JScrollPane(list);
        panel.add(listPane);

	filePopup = new JPopupMenu();
	
	openMenuItem = new JMenuItem(msgBundle.getString("Attachment.openMenuItemLabel"));
	openMenuItem.addActionListener(listener);
	filePopup.add(openMenuItem);

	saveMenuItem = new JMenuItem(msgBundle.getString("Attachment.saveMenuItemLabel"));
	saveMenuItem.addActionListener(listener);
	filePopup.add(saveMenuItem);

        setContentPane(panel);
        setSize(400, 200);
        setVisible(true);
    }

    /** Recursive method to get the content of a mail part
     *  @param p part to be examined
     *  @param names current names
     *  @return vector containing every files name
     */
    private Vector getFiles(Part p, Vector names)
    {
	try
	{
	    if(p.isMimeType("multipart/mixed") || p.isMimeType("multipart/related") || p.isMimeType("multipart/signed") || p.isMimeType("multipart/report"))
	    {
		Multipart mp = (Multipart)p.getContent();

		for(int i = 0; i < mp.getCount(); i++)
		    names = getFiles(mp.getBodyPart(i), names);
	    }

	    else if(p.isMimeType("multipart/alternative"))
	    {
		Multipart mp = (Multipart)p.getContent();
		BodyPart bp = null;

		for(int i = 0; i < mp.getCount(); i++)
		{
		    bp = mp.getBodyPart(i);

		    if(bp.isMimeType("text/html"))
		    {
			names.add("HTML version");
			break;
		    }
		}
	    }

	    else if(p.isMimeType("message/rfc822"))
	    {
		Message m = (Message)p.getContent();
		names = getFiles(m, names);
	    }

	    else
	    {
		String name = p.getFileName();
		
		if(name != null)
		{
		
		    int size = p.getSize();

		    String sizeString = new String();

		    if(size >= 1000000)
			sizeString = (size / 1000000) + " Mb";

		    else if(size >= 1000)
			sizeString = (size / 1000) + " kb";

		    else
			sizeString = size + " b";
                    
		    names.add(name + " (" + sizeString + ")");
		}

		else if(p.isMimeType("text/html"))
		    names.add("HTML version");

		else if(p.isMimeType("application/pgp-signature"))
		    names.add("PGP");
	    }
	}
	    
	catch(Exception e)
	{
	    e.printStackTrace();
	}

	return(names);
    }

    /** Recursive method to get the content of each part
     *  @param mp multipart to be examined
     *  @param name name of the file
     *  @return sub-part
     */
    private Part getBodyPart(Multipart mp, String name)
    {
        try
	{
	    for(int i = 0; i < mp.getCount(); i++)
	    {
		BodyPart bp = mp.getBodyPart(i);

		BodyPart result = null;

		if(bp.isMimeType("multipart/mixed") || bp.isMimeType("multipart/alternative"))
		    result = (BodyPart)getBodyPart((Multipart)bp.getContent(), name);

		if(result != null)
		    return(result);

		if(name.compareTo("HTML version") == 0)
		    if(bp.isMimeType("text/html"))
			return(bp);

		else if(name.compareTo("PGP") == 0)
		    if(bp.isMimeType("application/pgp-signature"))
			return(bp);
			
		if(bp.isMimeType("message/rfc822"))
		{
		    Message newMail = (Message)bp.getContent();

		    if(newMail.isMimeType("text/html"))
			return((Part)bp.getContent());
			
		    else if(newMail.getContent() instanceof Multipart)
		    {
			Multipart mp2 = (Multipart)newMail.getContent();
			    
			BodyPart bp2 = (BodyPart)getBodyPart(mp2, name);
			    
			if(bp2 != null)
			    return(bp2);
		    }
		}


		String n = bp.getFileName();

		if(n != null)
		    if(name.startsWith(n))
			return(bp);
	    }
	}

	catch(UnsupportedEncodingException encodE)
	{
	    JOptionPane.showMessageDialog(null, encodE.getMessage(), "UnsupportedEncodingException", JOptionPane.ERROR_MESSAGE);
	    return(null);
	}

        catch(Exception e)
	{
	    e.printStackTrace();
	}

	return(null);
    }

    /** Method to view the content of an attached file, if possible */
    private void view()
    {
	try
	{
	    Object o = mail.getContent();

	    if(o instanceof Multipart)
	    {
		int i = list.getSelectedIndex();

		if(i != -1)
		{
		    String name = (String)list.getSelectedValue();

		    Multipart mp = (Multipart)mail.getContent();

		    BodyPart bp = (BodyPart)getBodyPart(mp, name);

		    if(bp == null)
			return;

		    String realName = bp.getFileName();

		    if(realName == null)
			realName = "attach.html";

		    if(bp.isMimeType("image/gif") || bp.isMimeType("image/jpeg") || bp.isMimeType("image/jpg") || bp.isMimeType("image/pjpeg") || bp.isMimeType("image/x-png"))
		    {
			File f = getIt(realName.toString(), bp);

			ImageIcon img = new ImageIcon(f.getPath());
			JLabel label = new JLabel(img);

			StringBuffer buf = new StringBuffer(msgBundle.getString("AttachedFile.frameTitle"));
			buf.append(" ").append(realName);
		
			new AttachedFileDialog(buf.toString(), label, msgBundle);

			f.delete();
		    }

		    else if(bp.isMimeType("text/plain"))
		    {
			File f = getIt(realName, bp);

			JTextArea area = new JTextArea(30, 35);
			area.setEditable(false);

			BufferedReader in = new BufferedReader(new FileReader(f));

			String line = new String();

			while((line = in.readLine()) != null)
			    area.append(line + "\n");

			in.close();

			area.setCaretPosition(0);

			StringBuffer buf = new StringBuffer(msgBundle.getString("AttachedFile.frameTitle"));
			buf.append(" ").append(realName);

			new AttachedFileDialog(buf.toString(), area, msgBundle);
					   
			f.delete();
		    }

		    else if(bp.isMimeType("text/html"))
		    {
			JEditorPane editorPane = new JEditorPane("text/html", (String)bp.getContent());
			editorPane.setEditable(false);
					   
			new AttachedFileDialog(realName, editorPane, msgBundle);
		    }

		    else
		    {
			String type[] = bp.getHeader("Content-Type");

			JOptionPane.showMessageDialog(null, msgBundle.getString("Attachment.undisplayableFilePart1Label") + " \"" + type[0] + "\" " + msgBundle.getString("Attachment.undisplayableFilePart2Label"), "Attachment", JOptionPane.ERROR_MESSAGE);				    
		    }
		}
	    }

	    else
	    {
		JEditorPane editorPane = new JEditorPane("text/html", (String)mail.getContent());
		editorPane.setEditable(false);

		StringBuffer buf = new StringBuffer(msgBundle.getString("AttachedFile.frameTitle"));
		buf.append(" attach.html");
		new AttachedFileDialog(buf.toString(), editorPane, msgBundle);
	    }
	}

	catch(Exception ex)
	{
	    ex.printStackTrace();
	}
    }

    /** Allows to save an attached file */
    private void save()
    {
	int i = list.getSelectedIndex();

	if(i != -1)
	{
	    try
	    {
		String name = (String)list.getSelectedValue();

		Part bp = null;
		String realName = new String();

		Object o = mail.getContent();

		if(o instanceof Multipart)
		{
		    Multipart mp = (Multipart)mail.getContent();
		    bp = getBodyPart(mp, name);
		
		    if(bp == null)
			return;

		    realName = bp.getFileName();

		    if(realName == null)
		    {
			if(name.compareTo("HTML version") == 0)
			    realName = "attach.html";

			else if(name.compareTo("PGP") == 0)
			    realName = "sign.pgp";
		    }
		}

		else
		{
		    bp = (Part)mail;
		    realName = "attach.html";
		}

		JFileChooser fc = new JFileChooser();

		String newName = new String();

		fc.setSelectedFile(new File(realName));

		int val = fc.showSaveDialog(null);
                                                        
		if(val == JFileChooser.APPROVE_OPTION)
		{                                            
		    File file = fc.getSelectedFile();
		    
		    if(file != null)
		    {
			try
			{				    
			    while(file.exists())
			    {
				int choice = JOptionPane.showConfirmDialog(null, msgBundle.getString("Attachment.fileExistsLabel"), "Attachment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if(choice == JOptionPane.YES_OPTION)
				    break;

				val = fc.showSaveDialog(null);
					    
				if(val == JFileChooser.APPROVE_OPTION)
				    file = fc.getSelectedFile();

				else
				    return;
			    }

			    InputStream in = bp.getInputStream();
			    FileOutputStream out = new FileOutputStream(file);

			    int nb;
			    byte b[];

			    b = new byte[256];
			    nb = in.read(b);

			    while(nb == 256)
			    {
				out.write(b, 0, nb);
				b = new byte[256];
				nb = in.read(b);
			    }

			    if(nb != -1)
			    {
				out.write(b, 0, nb);
			    }

			    in.close();
			    out.close();
			}
				
			catch(Exception ex)
			{
			    ex.printStackTrace();
			}
		    }
		}
	    }
		    
	    catch(Exception ex)
	    {
		ex.printStackTrace();
	    }					
	}
    }

    /** Creates a temp file, to view it
     *  @param name name of the original file
     *  @param bp part of the mail containing it
     *  @return temporary file
     */
    private File getIt(String name, BodyPart bp)
    {
	try
	{
	    File f = File.createTempFile(name, null);

	    InputStream in = bp.getInputStream();
	    FileOutputStream out = new FileOutputStream(f);

	    int nb;
	    byte b[];

	    do
	    {
		b = new byte[256];
		nb = in.read(b);
		out.write(b, 0, nb);
	    } while(nb != -1 && nb == 256);

	    in.close();
	    out.close();	
	    
	    return(f);
	}

	catch(Exception e)
	{
	    e.printStackTrace();
	}

	return(null);
    }

    /** Listener for this class */
    private final class AttachmentListener extends MouseAdapter implements ActionListener
    {
	/** Method invoked when the user clicks
	 *  @param e event triggered
	 */
	public final void mousePressed(MouseEvent e)
        {
	    if(e.isPopupTrigger())
	    {
		if(list.getSelectedIndex() != -1)
		    filePopup.show(e.getComponent(), e.getX(), e.getY());
	    }

	    else
	    {
		int mods = e.getModifiers();

		if(mods == 4)
		{
		    if(list.getSelectedIndex() != -1)
			filePopup.show(e.getComponent(), e.getX(), e.getY());

		}

		/** Left double-click */
		else if(mods == 16 && e.getClickCount() == 2)
		{
		    if(list.getSelectedIndex() != -1)
			view();
		}
	    }
        }

	/** This method is invoked when an event is triggered
	 *  @param e event
	 */
	public final void actionPerformed(ActionEvent e)
	{
	    JMenuItem m = (JMenuItem)e.getSource();

	    if(m == openMenuItem)
		view();

	    else if(m == saveMenuItem)
		save();
	}
    }
}
