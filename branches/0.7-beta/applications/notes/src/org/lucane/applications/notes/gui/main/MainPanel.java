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
 
package org.lucane.applications.notes.gui.main;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import org.lucane.applications.notes.*;

class MainPanel extends JPanel
{
	private NoteAndCommentsPanel note;
	private ButtonPanel buttons;
	
	public MainPanel(NotesPlugin parent,  ActionListener action, MyMouseListener mml)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout(7, 0));
		
		this.note = new NoteAndCommentsPanel(parent, mml);
		this.buttons = new ButtonPanel(parent, action);
		
		this.add(note, BorderLayout.CENTER);
		this.add(buttons, BorderLayout.EAST);
	}
	
	public void setNoteContent(String content)
	{
		note.setNoteContent(content);
	}
	
	public void setComments(Object[] data)
	{
		note.setComments(data);
	}
	
	public Comment getComment()
	{
		return note.getComment();
	}

    public void allowComments(boolean state)
    {
        buttons.allowComments(state);
    }
    
    public void allowEdition(boolean state)
    {
        buttons.allowEdition(state);
    }
}
