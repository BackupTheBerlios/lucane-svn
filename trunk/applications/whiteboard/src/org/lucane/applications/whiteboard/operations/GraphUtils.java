package org.lucane.applications.whiteboard.operations;

import org.jgraph.graph.*;

public class GraphUtils
{
	public static DefaultGraphCell findCorrespondingCell(GraphModel model, DefaultGraphCell cell)
	{
		if(cell == null)
			return null;
		
		for(int i=model.getRootCount()-1;i>=0;i--)
		{
			DefaultGraphCell my = (DefaultGraphCell)model.getRootAt(i);
			if(my.getUserObject() == null || my.getUserObject().equals(cell.getUserObject()))
			{
				Object myBounds = my.getAttributes().get("bounds");
				Object cellBounds = cell.getAttributes().get("bounds");
				if(myBounds != null && myBounds.equals(cellBounds))
					return my;							
			}
		}
		
		return null;
	}
}