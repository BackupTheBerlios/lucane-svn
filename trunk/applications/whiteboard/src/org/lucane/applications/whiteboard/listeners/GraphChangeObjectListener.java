package org.lucane.applications.whiteboard.listeners;

import org.jgraph.JGraph;
import org.lucane.applications.whiteboard.operations.GraphOperation;
import org.lucane.common.*;

public class GraphChangeObjectListener
implements ObjectListener
{
	private JGraph graph;

	public GraphChangeObjectListener(JGraph graph)
	{
		this.graph = graph;
	}

	public void objectRead(Object o)
	{
		GraphOperation op = (GraphOperation)o;
		op.apply(this.graph.getModel());
	}
}