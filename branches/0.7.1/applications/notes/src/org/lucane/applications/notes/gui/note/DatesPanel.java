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

class DatesPanel extends JPanel
{
	private JLabel creation;
	private JLabel edition;
	
	public DatesPanel(NotesPlugin plugin)
	{
		//panel config
		super();
		this.setLayout(new GridLayout(2, 2));
		
		this.creation = new JLabel("");
		this.edition = new JLabel("");
		
		this.add(new JLabel(plugin.tr("note.creation.date")));
		this.add(creation);
		this.add(new JLabel(plugin.tr("note.edition.date")));
		this.add(edition);
	}
	
	public void setCreationDate(String txt)
	{
		creation.setText(txt);
	}
	
	public void setEditionDate(String txt)
	{
		edition.setText(txt);
	}
}