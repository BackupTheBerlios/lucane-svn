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
 
package org.lucane.applications.notes.gui.comment;

import java.awt.*;
import javax.swing.*;

import org.lucane.client.Client;
import org.lucane.applications.notes.*;

public class CommentFrame extends JFrame
{
    private Note note;
	private TitlePanel title;
	private ContentPanel content;
	private ButtonPanel buttons;
	
	public CommentFrame(NotesPlugin plugin, Note note)
	{
		super(plugin.tr("comment.frame.title.add"));
        init(plugin, note);
	}

    public CommentFrame(NotesPlugin plugin, Note note, Comment comment)
    {
		super(plugin.tr("comment.frame.title.view"));
        init(plugin, note);
        setCommentTitle(comment.getTitle());
        setContent(comment.getContent());
        buttons.hideSave();
        content.setEditable(false);
        title.setEditable(false);
		setIconImage(plugin.getImageIcon().getImage());
    }
    
    private void init(NotesPlugin plugin, Note note)
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(600, 400);
        this.getContentPane().setLayout(new BorderLayout(5, 0));
        
        MyActionListener listener = new MyActionListener(plugin, this);
        this.note = note;
        
        this.title = new TitlePanel(plugin);
        this.content = new ContentPanel(plugin);
        this.buttons = new ButtonPanel(plugin, listener);
        
        JPanel main = new JPanel(new BorderLayout(0, 5));
        main.add(title, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        
        this.getContentPane().add(main, BorderLayout.CENTER);
        this.getContentPane().add(buttons, BorderLayout.EAST);
    }
	
	public void setCommentTitle(String txt)
	{
		title.setTitle(txt);
	}
	
	public String getCommentTitle()
	{
		return title.getTitle();
	}
	
	public void setContent(String txt)
	{
		content.setContent(txt);
	}
	
	public String getContent()
	{
		return content.getContent();
	}

    public Comment getComment()
    {
        return new Comment(note, Client.getInstance().getMyInfos().getName(), 
                getCommentTitle(), getContent());
    }
}