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

package org.lucane.applications.notes.gui.note;

import java.awt.*;
import javax.swing.*;

import org.lucane.client.Client;
import org.lucane.applications.notes.Note;
import org.lucane.applications.notes.NotesPlugin;

public class NoteFrame extends JFrame
{
	private MainPanel content;
	private ButtonPanel buttons;
    private String noteId = null;
	
	public NoteFrame(NotesPlugin plugin)
	{
		super(plugin.tr("note.frame.title.add"));
        init(plugin);
	}
  
    public NoteFrame(NotesPlugin plugin, Note note)
    {
        super(plugin.tr("note.frame.title.edit"));
        init(plugin);
        noteId = note.getId();
        
        setNoteTitle(note.getTitle());
        setCommentable(note.isCommentable());
        setPublic(note.isPublic());
        setCreationDate(note.getCreationDate().toString());
        setEditionDate(note.getEditionDate().toString());
        setContent(note.getContent());
		setIconImage(plugin.getImageIcon().getImage());
    }

	private void init(NotesPlugin plugin)
    {
        this.setSize(600, 400);
        this.getContentPane().setLayout(new BorderLayout(5, 0));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        MyActionListener listener = new MyActionListener(plugin, this);
        
        this.content = new MainPanel(plugin);
        this.buttons = new ButtonPanel(plugin, listener);
        
        this.getContentPane().add(content, BorderLayout.CENTER);
        this.getContentPane().add(buttons, BorderLayout.EAST);
    }
    
	public void setNoteTitle(String txt)
	{
		content.setTitle(txt);
	}
	
	public String getNoteTitle()
	{
		return content.getTitle();
	}
	
	public void setCommentable(boolean state)
	{
		content.setCommentable(state);
	}
	
	public void setPublic(boolean state)
	{
		content.setPublic(state);
	}
	
	public boolean isCommentable()
	{
		return content.isCommentable();
	}
	
	public boolean isPublic()
	{
		return content.isPublic();
	}
	
	public void setCreationDate(String txt)
	{
		content.setCreationDate(txt);
	}
	
	public void setEditionDate(String txt)
	{
		content.setEditionDate(txt);
	}
	
	public void setContent(String txt)
	{
		content.setContent(txt);
	}
	
	public String getContent()
	{
		return content.getContent();
	}


    public Note getNote()
    {
        Note n =  new Note(Client.getInstance().getMyInfos().getName(),
                             getNoteTitle(), getContent(), isPublic(), 
                             isCommentable());       
        if(noteId != null)
          n.setId(noteId);
        
        return n;
    }
}