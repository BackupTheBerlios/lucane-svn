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

class TopPanel extends JPanel
{
	private TitlePanel title;
	private OptionsPanel options;
	private DatesPanel dates;
	
	public TopPanel(NotesPlugin plugin)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout(0, 5));
		
		this.title = new TitlePanel(plugin);
		this.options = new OptionsPanel(plugin);
		this.dates = new DatesPanel(plugin);

		this.add(title, BorderLayout.NORTH);		
		this.add(options, BorderLayout.WEST);
		this.add(dates, BorderLayout.EAST);
	}
	
	public void setTitle(String txt)
	{
		title.setTitle(txt);
	}
	
	public String getTitle()
	{
		return title.getTitle();
	}
	
	public void setCommentable(boolean state)
	{
		options.setCommentable(state);
	}
	
	public void setPublic(boolean state)
	{
		options.setPublic(state);
	}
	
	public boolean isCommentable()
	{
		return options.isCommentable();
	}
	
	public boolean isPublic()
	{
		return options.isPublic();
	}
	
	public void setCreationDate(String txt)
	{
		dates.setCreationDate(txt);
	}
	
	public void setEditionDate(String txt)
	{
		dates.setEditionDate(txt);
	}
}