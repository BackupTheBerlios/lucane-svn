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
 
package org.lucane.applications.notes.gui.comment;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.lucane.applications.notes.NotesPlugin;

class ButtonPanel extends JPanel
{
	private JButton save;
	private JButton close;
		
	public ButtonPanel(NotesPlugin plugin, ActionListener listener)
	{
		//panel config
		super();
		this.setLayout(new BorderLayout());
		
		this.save = new JButton(plugin.tr("comment.save"));
		this.close = new JButton(plugin.tr("comment.close"));
		
		save.setName("save");
		save.addActionListener(listener);
		close.setName("close");
		close.addActionListener(listener);
		
		JPanel grid = new JPanel(new GridLayout(2, 1, 0, 2));
		grid.add(save);
		grid.add(close);			
		
		this.add(grid, BorderLayout.SOUTH);
	}
    
    public void hideSave()
    {
        save.setVisible(false);
    }	
}