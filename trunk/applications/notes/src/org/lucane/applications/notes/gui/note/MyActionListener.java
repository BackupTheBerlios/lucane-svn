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

import java.awt.event.*;
import java.awt.*;

import org.lucane.client.widgets.DialogBox;
import org.lucane.applications.notes.*;

class MyActionListener implements ActionListener
{
    private NoteFrame parent;
    private NotesPlugin plugin;

    public MyActionListener(NotesPlugin plugin, NoteFrame parent)
    {
        this.plugin = plugin;
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent ae)
    {
        Component source = (Component)ae.getSource();
        String sourceName = source.getName();

        if (sourceName.equals("save"))
        {
            //some checks
            if (parent.getNoteTitle().length() < 1)
            {
                DialogBox.error(plugin.tr("error.note.noTitle"));
                return;
            }
            if (parent.getContent().length() < 1)
            {
                DialogBox.error(plugin.tr("error.note.noContent"));
                return;
            }

            try
            {
                plugin.saveNote(parent.getNote());
                parent.dispose();
            }
            catch (Exception e)
            {
                DialogBox.error(e.getMessage());
            }
        }
        else if (sourceName.equals("close"))
        {
            parent.dispose();
        }
    }
}