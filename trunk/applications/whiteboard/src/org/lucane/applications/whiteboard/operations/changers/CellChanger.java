package org.lucane.applications.whiteboard.operations.changers;

import org.lucane.applications.whiteboard.operations.GraphUtils;
import org.jgraph.graph.*;

public class CellChanger implements Changer
{
	public void change(GraphModel model, GraphCell toChange, GraphCell source)
	{
		DefaultGraphCell to = (DefaultGraphCell)toChange;
		DefaultGraphCell from = (DefaultGraphCell)source;
		
		DefaultGraphCell parent = (DefaultGraphCell)from.getParent();
		parent = GraphUtils.findCorrespondingCell(model, parent);
		to.setParent(parent);	
		to.setUserObject(from.getUserObject());
	}
}