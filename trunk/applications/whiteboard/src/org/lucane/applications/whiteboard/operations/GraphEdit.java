package org.lucane.applications.whiteboard.operations;

import org.lucane.applications.whiteboard.operations.changers.*;

import java.util.*;


import org.jgraph.event.GraphModelEvent;
import org.jgraph.graph.*;

public class GraphEdit implements GraphOperation
{	
	private Object[] changed;
	private Map oldAttributes;
	private Map newAttributes;
	
	private ConnectionSet connectionSet;
	private ParentMap parentMap;
		
	public void init(GraphModelEvent gme)
	{
		GraphModel model = (GraphModel)gme.getSource();
		DefaultGraphModel.GraphModelEdit change = (DefaultGraphModel.GraphModelEdit)gme.getChange();
		this.oldAttributes = change.getAttributes();
		this.newAttributes = change.getPreviousAttributes();
		this.changed = change.getChanged();

		this.connectionSet = change.getPreviousConnectionSet();
		this.parentMap = change.getPreviousParentMap();
	}

	public void apply(GraphModel model)
	{
		HashMap attributes = new HashMap();
		if(this.oldAttributes == null)
			return;
			
		for(int i=0;i<changed.length;i++)
		{
			DefaultGraphCell cell = (DefaultGraphCell)changed[i];
		
			AttributeMap allCellAttributes = cell.getAttributes();
			Map oldCellAttributes = (Map)oldAttributes.get(cell);
			if(oldCellAttributes != null)
				allCellAttributes.putAll(oldCellAttributes);
			cell.setAttributes(allCellAttributes);
			cell.setUserObject(allCellAttributes.get("value"));
			
			DefaultGraphCell correspondingCell = GraphUtils.findCorrespondingCell(model, cell);	
			if(correspondingCell == null)
				continue;
			
			Changer changer = ChangerFactory.getChanger(cell);
			changer.change(model, correspondingCell, cell);
								
			attributes.put(correspondingCell, newAttributes.get(cell));
		}
		
		model.edit(attributes, connectionSet, parentMap, null);
	}
}