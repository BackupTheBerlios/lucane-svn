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

import org.lucane.applications.notes.*;

class CommentListPanel extends JPanel
{
	private JLabel label;
	private JList list;
	
	public CommentListPanel(NotesPlugin parent, MyMouseListener listener)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout());
		
		this.label = new JLabel(parent.tr("main.comments"));
		this.list = new JList();
		this.add(label, BorderLayout.NORTH);
		this.add(list, BorderLayout.CENTER);
		
		list.setName("comments");
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        
		list.addMouseListener(listener);		
	}
	
	public void setListData(Object[] data)
	{
		list.setListData(data);
	}
	
	public Comment getComment()
	{
		return (Comment)list.getSelectedValue();
	}
}
