package org.lucane.applications.whiteboard;

import java.awt.*;

import javax.swing.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.lucane.applications.whiteboard.listeners.*;
import org.lucane.common.ObjectConnection;

public class GraphGui
{
	private DefaultGraphModel model;

	private ObjectConnection connection;
	
	private JGraph graph;
	private GraphChangeObjectListener graphChangeObjectListener;
	private GraphKeyListener graphKeyListener;
	private NetworkGraphListener networkGraphListener;

	public GraphGui(ObjectConnection connection)
	{
		this.connection = connection;
		
		this.model = new DefaultGraphModel();
		this.graph = new JGraph(model);
		
		this.networkGraphListener = new NetworkGraphListener(connection);
		this.graphKeyListener = new GraphKeyListener(graph);
		this.graphChangeObjectListener = new GraphChangeObjectListener(graph);
	}
	
	public void showFrame(String title)
	{
		JFrame frame = new JFrame(title);
		frame.getContentPane().add(new JScrollPane(graph));
		frame.setSize(600, 600);
		frame.setVisible(true);
	}
	
	public void setGraphAsSource()
	{
	  model.addGraphModelListener(networkGraphListener);
	  
	  graph.addKeyListener(graphKeyListener);
	  graph.setBackground(Color.WHITE);
	  graph.setEnabled(true);
	}
	
	public void setGraphAsDest()
	{
	  //model.removeGraphModelListener(networkGraphListener);
	  //graph.removeKeyListener(graphKeyListener);
	  graph.setBackground(new Color(255, 250, 250));
	  graph.setEnabled(false);

	  connection.addObjectListener(graphChangeObjectListener);
	  connection.listen();
	}
}