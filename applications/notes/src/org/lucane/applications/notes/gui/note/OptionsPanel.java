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

import org.lucane.applications.notes.NotesPlugin;

class OptionsPanel extends JPanel
{
	private JCheckBox published;
	private JCheckBox commentable;
	
	public OptionsPanel(NotesPlugin plugin)
	{
		//panel config
		super();
		this.setLayout(new GridLayout(2, 1));
		
		this.published = new JCheckBox(plugin.tr("note.public"));
		this.commentable = new JCheckBox(plugin.tr("note.commentable"));
		
		this.add(published);
		this.add(commentable);
	}
	
	public void setCommentable(boolean state)
	{
		commentable.setSelected(state);
	}
	
	public void setPublic(boolean state)
	{
		published.setSelected(state);
	}
	
	public boolean isCommentable()
	{
		return commentable.isSelected();
	}
	
	public boolean isPublic()
	{
		return published.isSelected();
	}
}