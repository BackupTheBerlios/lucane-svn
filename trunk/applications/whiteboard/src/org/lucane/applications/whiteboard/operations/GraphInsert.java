package org.lucane.applications.whiteboard.operations;

import org.lucane.applications.whiteboard.operations.changers.*;

import java.util.*;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.graph.*;

public class GraphInsert implements GraphOperation
{	
	private Object[] inserted;
	private Map attributes;
	private ConnectionSet connectionSet;
	private ParentMap parentMap;
	
	public void init(GraphModelEvent gme)
	{
		GraphModelEvent.GraphModelChange change = gme.getChange();
		
		this.inserted = change.getInserted();
		this.attributes = change.getPreviousAttributes();
		this.connectionSet = change.getPreviousConnectionSet();
		this.parentMap = change.getPreviousParentMap();
	}

	public void apply(GraphModel model)
	{
		for(int i=0;i<inserted.length;i++)
		{
			GraphCell gc = (GraphCell)inserted[i];
			Changer changer = ChangerFactory.getChanger(gc);	
			changer.change(model, gc, gc);
		}
		
		model.insert(inserted, attributes, connectionSet, parentMap, null);		
	}
}