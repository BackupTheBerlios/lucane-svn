package org.lucane.applications.whiteboard.listeners;

import java.io.IOException;

import org.lucane.applications.whiteboard.operations.*;
import org.lucane.common.ObjectConnection;

import org.jgraph.event.*;
import org.jgraph.graph.GraphModel;

public class NetworkGraphListener implements GraphModelListener
{	
	private ObjectConnection connection;

	public NetworkGraphListener(ObjectConnection connection)
	{
		this.connection = connection;
	}
	
	public void graphChanged(GraphModelEvent gme)
	{		
		GraphModel model = (GraphModel)gme.getSource();
				
		if(gme.getChange().getInserted() != null && gme.getChange().getInserted().length > 0)
			send(gme, new GraphInsert());

		else if(gme.getChange().getRemoved() != null && gme.getChange().getRemoved().length > 0)
			send(gme, new GraphRemove());	
	
		else if(gme.getChange().getChanged() != null && gme.getChange().getChanged().length > 0)
			send(gme, new GraphEdit());	
	}

	private void send(GraphModelEvent gme, GraphOperation op)
	{
		op.init(gme);
		try	{
			this.connection.write(op);
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}	
}