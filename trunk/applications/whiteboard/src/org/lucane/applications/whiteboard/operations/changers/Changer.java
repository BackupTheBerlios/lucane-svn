package org.lucane.applications.whiteboard.operations.changers;

import org.jgraph.graph.*;

public interface Changer
{
	public void change(GraphModel model, GraphCell toChange, GraphCell source);
}