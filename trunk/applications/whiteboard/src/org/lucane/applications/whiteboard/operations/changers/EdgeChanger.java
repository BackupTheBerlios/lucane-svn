package org.lucane.applications.whiteboard.operations.changers;

import org.lucane.applications.whiteboard.operations.GraphUtils;

import org.jgraph.graph.*;

public class EdgeChanger extends CellChanger
{
	public void change(GraphModel model, GraphCell toChange, GraphCell source)
	{
		super.change(model, toChange, source);
		
		DefaultEdge to = (DefaultEdge)toChange;
		DefaultEdge from = (DefaultEdge)source;
		
		DefaultPort sourcePort = (DefaultPort)from.getSource();
		DefaultPort targetPort = (DefaultPort)from.getTarget();
				
		if(sourcePort == null)
			to.setSource(null);
		else
		{
			DefaultGraphCell parent = GraphUtils.findCorrespondingCell(model, (DefaultGraphCell)sourcePort.getParent());
			sourcePort.setParent(parent);
			to.setSource(sourcePort);
		}
				
		if(targetPort == null)
			to.setTarget(null);
		else
		{
			DefaultGraphCell parent = GraphUtils.findCorrespondingCell(model, (DefaultGraphCell)targetPort.getParent());
			targetPort.setParent(parent);
			to.setTarget(targetPort);
		}
	}
}