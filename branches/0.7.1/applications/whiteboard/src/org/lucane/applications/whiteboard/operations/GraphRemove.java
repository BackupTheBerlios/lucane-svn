package org.lucane.applications.whiteboard.operations;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphModel;

public class GraphRemove implements GraphOperation
{	
	private Object[] removed;
	
	public void init(GraphModelEvent gme)
	{
		GraphModelEvent.GraphModelChange change = gme.getChange();
		
		this.removed = change.getRemoved();
	}

	public void apply(GraphModel model)
	{
		for(int i=0;i<removed.length;i++)
		{
			DefaultGraphCell cell = (DefaultGraphCell)removed[i];
			removed[i] = GraphUtils.findCorrespondingCell(model, cell);			
		}
		model.remove(removed);
	}
}