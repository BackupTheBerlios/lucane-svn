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

import org.lucane.applications.notes.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

class TopListPanel extends JPanel
{
	private AuthorListPanel authors;
	private NoteListPanel notes;
	
	public TopListPanel(NotesPlugin parent, ListSelectionListener listener)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout(7, 0));
		
		this.authors = new AuthorListPanel(parent, listener);
		this.notes = new NoteListPanel(parent, listener);
		this.add(authors, BorderLayout.WEST);
		this.add(notes, BorderLayout.CENTER);
	}
	
	public void setAuthors(Object[] data)
	{
		authors.setListData(data);
	}
	
	public void setNotes(Object[] data)
	{
		notes.setListData(data);
	}
	
	public String getAuthor()
	{
		return authors.getAuthor();
	}
	
	public Note getNote()
	{
		return notes.getNote();
	}
}
