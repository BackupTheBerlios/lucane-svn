package org.lucane.applications.whiteboard.operations.changers;

import org.jgraph.graph.*;

public class ChangerFactory
{
	public static Changer getChanger(GraphCell cell)
	{
		if(cell instanceof DefaultEdge)
			return new EdgeChanger();
		
		return new CellChanger();
	}
}