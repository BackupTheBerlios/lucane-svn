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

import org.lucane.client.widgets.htmleditor.HTMLEditor;
import org.lucane.applications.notes.NotesPlugin;

class ContentPanel extends JPanel
{
	private JLabel label;
	private HTMLEditor content;
	
	public ContentPanel(NotesPlugin plugin)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout());
		
		this.label = new JLabel(plugin.tr("note.content"));
		this.content = new HTMLEditor();
		this.add(label, BorderLayout.NORTH);
		this.add(content, BorderLayout.CENTER);
	}
	
	public void setContent(String txt)
	{
		content.setText(txt);
	}
	
	public String getContent()
	{
		return content.getText();
	}
}