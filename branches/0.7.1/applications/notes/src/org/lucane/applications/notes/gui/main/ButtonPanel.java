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
import java.awt.event.*;
import javax.swing.*;

class ButtonPanel extends JPanel
{
	private JButton newNote;
	private JButton editNote;
	private JButton removeNote;
	private JButton addComment;
	private JButton close;
		
	public ButtonPanel(NotesPlugin parent, ActionListener listener)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout());
		
		this.newNote = new JButton(parent.tr("main.newNote"));
		this.editNote = new JButton(parent.tr("main.editNote"));
		this.removeNote = new JButton(parent.tr("main.removeNote"));
		this.addComment = new JButton(parent.tr("main.addComment"));
		this.close = new JButton(parent.tr("main.close"));
		
		newNote.setName("newNote");
		newNote.addActionListener(listener);
		editNote.setName("editNote");
		editNote.addActionListener(listener);
		removeNote.setName("removeNote");
		removeNote.addActionListener(listener);
		addComment.setName("addComment");
		addComment.addActionListener(listener);
		close.setName("close");
		close.addActionListener(listener);
		
		JPanel grid = new JPanel(new GridLayout(5, 1, 0, 2));
		grid.add(newNote);
		grid.add(editNote);
		grid.add(removeNote);
		grid.add(new JLabel(""));
		grid.add(addComment);				
		
		allowComments(false);
        allowEdition(false);
        
		this.add(grid, BorderLayout.NORTH);
		this.add(close, BorderLayout.SOUTH);
	}	
    
    public void allowComments(boolean state)
    {
        addComment.setEnabled(state);
    }
    
    public void allowEdition(boolean state)
    {
        editNote.setEnabled(state);
        removeNote.setEnabled(state);
    }
}
