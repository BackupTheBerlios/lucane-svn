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

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;


import org.lucane.common.concepts.*;
import org.lucane.applications.administrator.AdministratorPlugin;

public class ConceptPanel extends JPanel
implements ListSelectionListener
{
	private ConceptListPanel conceptList;
	private UserPanel userPanel;
	private ServicePanel servicePanel;
	private PluginPanel pluginPanel;
	private GroupPanel groupPanel; 
	
	private JSplitPane splitPane;
	
	public ConceptPanel(AdministratorPlugin plugin)
	{	
		super(new BorderLayout());
		//-- attributes
		conceptList = new ConceptListPanel(plugin, this);
		conceptList.addListSelectionListener(this);
		userPanel = new UserPanel(plugin, this);
		servicePanel = new ServicePanel(plugin, this);
		pluginPanel = new PluginPanel(plugin, this);
		groupPanel = new GroupPanel(plugin, this);
			
		//-- layout
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, conceptList, new JPanel());
		splitPane.setDividerLocation(250);
		splitPane.setDividerSize(5);
		this.add(splitPane, BorderLayout.CENTER);
	}
	
	public void valueChanged(ListSelectionEvent e) 
	{
		int row = conceptList.getSelectedRow();
		if(row < 0)
			return;

		Concept concept = conceptList.getConceptAt(row);
		showConcept(concept);
	}
	
	public void refresh()
	{
		conceptList.refresh();
	}
	
	public void showConcept(Concept concept)
	{
		if(concept == null)
			splitPane.setBottomComponent(new JPanel());
		else if(concept instanceof GroupConcept)
		{	
			groupPanel.showConcept((GroupConcept)concept);
			splitPane.setBottomComponent(groupPanel);
		}
		else if(concept instanceof UserConcept)
		{
			userPanel.showConcept((UserConcept)concept);
			splitPane.setBottomComponent(userPanel);
		}
		else if(concept instanceof PluginConcept)
		{
			pluginPanel.showConcept((PluginConcept)concept);
			splitPane.setBottomComponent(pluginPanel);
		}
		else if(concept instanceof ServiceConcept)
		{
			servicePanel.showConcept((ServiceConcept)concept);
			splitPane.setBottomComponent(servicePanel);
		}
		
		splitPane.setDividerLocation(250);
	}
	
	public static void main(String[] args)
	{
		JFrame jf = new JFrame("test");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new ConceptPanel(null));
		jf.pack();
		jf.show();
	}
}