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

import org.lucane.applications.notes.*;
import org.lucane.client.*;
import org.lucane.client.util.PluginExitWindowListener;
import org.lucane.client.widgets.ManagedWindow;

public class MainFrame extends ManagedWindow
{
	private TopListPanel lists;
	private MainPanel content;
	
	public MainFrame(NotesPlugin parent)
	{
		//panel config
		super(parent, parent.getTitle());
		this.getContentPane().setLayout(new BorderLayout(0, 5));
		setPreferredSize(new Dimension(600, 400));

		MyListSelectionListener mlsl = new MyListSelectionListener(parent, this);
        MyActionListener mal = new MyActionListener(parent, this); 
        MyMouseListener mml = new MyMouseListener(parent, this); 
		
		this.lists = new TopListPanel(parent, mlsl);
		this.content = new MainPanel(parent, mal, mml);
		
		this.getContentPane().add(lists, BorderLayout.NORTH);
		this.getContentPane().add(content, BorderLayout.CENTER);

         this.addWindowListener(new PluginExitWindowListener(parent));
	}
	
	public void setAuthors(Object[] data)
	{
		lists.setAuthors(data);
	}
	
	public void setNotes(Object[] data)
	{
		lists.setNotes(data);
	}
	
	public String getAuthor()
	{
		return lists.getAuthor();
	}
	
	public Note getNote()
	{
		return lists.getNote();
	}
	
	public void setNoteContent(String txt)
	{
        if(txt != null)
         {
             Note n = getNote();
             content.allowComments(n.isCommentable());
             content.allowEdition(n.getAuthor().equals(Client.getInstance().getMyInfos().getName()));
		     content.setNoteContent(txt);
         }
         else
          {
              content.allowComments(false);
              content.allowEdition(false);
              content.setNoteContent("");
          }
	}
	
	public void setComments(Object[] data)
	{
		content.setComments(data);
	}
	
	public Comment getComment()
	{
		return content.getComment();
	}
}
