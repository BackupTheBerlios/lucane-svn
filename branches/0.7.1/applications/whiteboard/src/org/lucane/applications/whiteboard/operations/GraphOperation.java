package org.lucane.applications.whiteboard.operations;

import java.io.Serializable;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.graph.GraphModel;

public interface GraphOperation extends Serializable
{	
	public void init(GraphModelEvent event);
	public void apply(GraphModel model);
}