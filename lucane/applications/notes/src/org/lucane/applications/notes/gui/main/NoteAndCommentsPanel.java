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

import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.applications.notes.*;

class NoteAndCommentsPanel extends JPanel
{
	private HTMLEditor note;
	private CommentListPanel comments;
	
	public NoteAndCommentsPanel(NotesPlugin parent, MyMouseListener listener)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout(0, 7));
		
		this.note = new HTMLEditor();
		this.note.setEditable(false);
		this.note.setToolbarVisible(false);
		this.comments = new CommentListPanel(parent, listener);
		
		this.add(note, BorderLayout.CENTER);
		this.add(comments, BorderLayout.SOUTH);
	}
	
	public void setNoteContent(String content)
	{
		note.setText(content);
	}
	
	public void setComments(Object[] data)
	{
		comments.setListData(data);
	}
	
	public Comment getComment()
	{
		return comments.getComment();
	}
}
