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
import java.awt.event.*;

import org.lucane.client.widgets.*;
import org.lucane.applications.notes.*;
import org.lucane.applications.notes.gui.comment.CommentFrame;
import org.lucane.applications.notes.gui.note.NoteFrame;

class MyActionListener implements ActionListener
{
  private NotesPlugin plugin;
  private MainFrame parent;

  public MyActionListener(NotesPlugin plugin, MainFrame parent)
  {
    this.plugin = plugin;
    this.parent = parent;
  }

  public void actionPerformed(ActionEvent ae)
  {
    Component source = (Component)ae.getSource();
    String sourceName = source.getName();

    if (sourceName.equals("newNote"))
    {
		  new NoteFrame(plugin).show();
    }
    else if (sourceName.equals("editNote"))
    {
      Note note = parent.getNote();
      new NoteFrame(plugin, note).show();
    }
    else if(sourceName.equals("removeNote"))
    {
      boolean remove = DialogBox.question(plugin.tr("main.removeNoteTitle"), plugin.tr("main.removeNoteMessage"));
      if(remove)
      {
        Note note = parent.getNote();
        plugin.removeNote(note.getId());
      }
    }
    else if(sourceName.equals("addComment"))
    {
      new CommentFrame(plugin, parent.getNote()).show();
    }
    else if(sourceName.equals("close"))
    {
      parent.dispose();
      plugin.exit();
    }
  }
}