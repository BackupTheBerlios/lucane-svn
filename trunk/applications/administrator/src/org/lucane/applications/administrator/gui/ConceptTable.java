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
package org.lucane.applications.administrator.gui;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

import org.lucane.client.Plugin;
import org.lucane.common.concepts.*;

public class ConceptTable extends JTable
{
	public ConceptTable(Plugin plugin, String name, ArrayList concepts)
	{
		super(new ConceptTableModel(plugin, concepts));
		getColumnModel().getColumn(0).setMinWidth(16);
		getColumnModel().getColumn(0).setMaxWidth(20);
		getTableHeader().getColumnModel().getColumn(0).setHeaderValue("");
		getTableHeader().getColumnModel().getColumn(1).setHeaderValue(name);
		setRowSelectionAllowed(true);
		setShowVerticalLines(false);
	}

	public ConceptTable(Plugin plugin, String name)
	{
		this(plugin, name, new ArrayList());	
	}
	
	public void setConcepts(ArrayList list)
	{
		ConceptTableModel model = (ConceptTableModel)this.getModel();
		model.setConcepts(list);
	}
	
	public void addConcept(Concept concept)
	{
		ConceptTableModel model = (ConceptTableModel)this.getModel();
		model.addConcept(concept);
	}
	
	public Concept getConceptAt(int row)
	{
		return ((ConceptTableModel)this.getModel()).getConceptAt(row);
	}
}

class ConceptTableModel extends AbstractTableModel
{
	private ArrayList concepts;

	private ImageIcon groupIcon;
	private ImageIcon userIcon;
	private ImageIcon pluginIcon;
	private ImageIcon serviceIcon;
	
	public ConceptTableModel(Plugin plugin, ArrayList concepts)
	{		
		this.concepts = concepts;
		
		try {
			this.groupIcon = new ImageIcon(new URL(plugin.getDirectory() + "group.jpg"));
			this.userIcon = new ImageIcon(new URL(plugin.getDirectory() + "user.jpg"));
			this.pluginIcon = new ImageIcon(new URL(plugin.getDirectory() + "plugin.jpg"));
			this.serviceIcon = new ImageIcon(new URL(plugin.getDirectory() + "service.jpg"));
		} catch(Exception e) {
			//should never happen, we know where our icons are
		}
	}

	public void setConcepts(ArrayList concepts)
	{
		this.concepts = concepts;
		this.fireTableDataChanged();
	}

	public void addConcept(Concept concept)
	{
		this.concepts.add(concept);
		this.fireTableDataChanged();
	}
	
	public Concept getConceptAt(int row)
	{
		return (Concept)concepts.get(row);
	}
	
	public void removeAt(int row)
	{
		if(row < 0)
			return;
		
		concepts.remove(row);
		this.fireTableDataChanged();
	}
	
	public ArrayList getConceptList()
	{
		return concepts;
	}
	
	public Object getValueAt(int x, int y)
	{
		Concept c = (Concept)concepts.get(x);
		
		if(y == 1)
			return c.getName();
		
		if(c instanceof PluginConcept)
			return pluginIcon;
		if(c instanceof GroupConcept)
			return groupIcon;
		if(c instanceof UserConcept)
			return userIcon;
		if(c instanceof ServiceConcept)
			return serviceIcon;
		
		return "";
	}
	
	public Class getColumnClass(int c) 
	{
		return (c==0 ? ImageIcon.class : String.class);
	}

	public int getColumnCount()
	{
		return 2;
	}

	public int getRowCount()
	{
		return concepts.size();
	}
}
