/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.applications.jmail.base;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import org.lucane.applications.jmail.JMailPlugin;

public class FolderRenderer extends DefaultTreeCellRenderer 
{
	private JMailPlugin plugin;
	private Icon inboxIcon;
	private Icon sentIcon;
	private Icon draftsIcon;	
	private Icon trashIcon;
	private Icon openedFolderIcon;
	private Icon closedFolderIcon;

	public FolderRenderer(JMailPlugin plugin) 
	{
		this.plugin = plugin;
		this.inboxIcon = plugin.getIcon("folders/inbox.png");
		this.sentIcon = plugin.getIcon("folders/sent.png");
		this.draftsIcon = plugin.getIcon("folders/drafts.png");
		this.trashIcon = plugin.getIcon("folders/trash.png");
		this.openedFolderIcon = plugin.getIcon("folders/openedFolder.png");
		this.closedFolderIcon = plugin.getIcon("folders/closedFolder.png");
	}
		
	public Component getTreeCellRendererComponent(JTree tree,
			Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) 
	{
		JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		//-- tree root
		if(value.toString().equalsIgnoreCase("Mailbox"))
		{
			label.setText("<html><b>" +tr("folder.root") + "</b></html>");
			label.setIcon(null);
		}
		//-- well known folders
		else if(value.toString().equalsIgnoreCase("INBOX"))
		{
			label.setText(tr("folder.inbox"));
			label.setIcon(this.inboxIcon);
		}
		else if(value.toString().equalsIgnoreCase("Sent"))
		{ 
			label.setText(tr("folder.sent"));
			label.setIcon(this.sentIcon);
		}
		else if(value.toString().equalsIgnoreCase("Drafts")) 
		{		
			label.setText(tr("folder.drafts"));
			label.setIcon(this.draftsIcon);
		}
		else if(value.toString().equalsIgnoreCase("Trash"))
		{		
			label.setText(tr("folder.trash"));
			label.setIcon(this.trashIcon);
		}
			
		//-- user defined folders
		else if(expanded)
			label.setIcon(this.openedFolderIcon);
		else
			label.setIcon(this.openedFolderIcon);

		return label;
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);	
	}
}