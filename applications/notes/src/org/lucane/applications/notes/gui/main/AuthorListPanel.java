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

class AuthorListPanel extends JPanel
{
	private JLabel label;
	private JList list;
	
	private SpecialElement myNotes;
	private SpecialElement recentNotes;
	
	
	public AuthorListPanel(NotesPlugin parent, ListSelectionListener listener)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout());
	
		this.label = new JLabel(parent.tr("main.authors"));
		this.list = new JList();
		this.add(label, BorderLayout.NORTH);
		this.add(list, BorderLayout.CENTER);
		
		this.myNotes = new SpecialElement("myNotes", parent.tr("main.myNotes"));
		this.recentNotes = new SpecialElement("recentNotes", parent.tr("main.recentNotes"));

		list.setName("authors");
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(listener);
	}
	
	public void setListData(Object[] data)
	{
		Object [] myData = new Object[data.length +2];
		myData[0] = recentNotes;
		myData[1] = myNotes;
		
		for(int i=0;i<data.length;i++)
			myData[i+2] = data[i];
		
		list.setListData(myData);
	}
	
	public String getAuthor()
	{
		return (String)list.getSelectedValue();
	}
}
