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

import javax.swing.*;
import javax.swing.event.*;

class MyListSelectionListener implements ListSelectionListener
{
    private NotesPlugin plugin;
    private MainFrame parent;

    public MyListSelectionListener(NotesPlugin plugin, MainFrame parent)
    {
        this.plugin = plugin;
        this.parent = parent;
    }

    public void valueChanged(ListSelectionEvent lse)
    {
        JList source = (JList)lse.getSource();
        String sourceName = source.getName();

        if (sourceName.equals("authors"))
        {
            Object author = source.getSelectedValue();
            if(author != null)
            {
             	if(author instanceof SpecialElement)
             	{
             		SpecialElement elem = (SpecialElement)author;
             		if(elem.getName().equals("recentNotes"))
						parent.setNotes(plugin.getRecentPublishedNotes());
					else if(elem.getName().equals("myNotes"))
						parent.setNotes(plugin.getPersonnalNotes());
             	}
				else
                	parent.setNotes(plugin.getPublishedNotesByAuthor((String)author));
            }
            else
                parent.setNotes(plugin.getRecentPublishedNotes());
        }
        else if (sourceName.equals("notes"))
        {
            Note note = (Note)source.getSelectedValue();
            if(note != null)
            {
                parent.setNoteContent(note.getContent());
                parent.setComments(plugin.getCommentsForNote(note.getId()));
            }
            else
            {
                parent.setNoteContent(null);
                parent.setComments(new Object[0]);                
            }
        }
    }
}
