package org.lucane.applications.whiteboard.listeners;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

public class GraphKeyListener extends KeyAdapter
{
	private JGraph graph;

	public GraphKeyListener(JGraph graph)
	{
		this.graph = graph;
	}

	public void keyPressed(KeyEvent ke)
	{
		if (ke.getKeyCode() == KeyEvent.VK_DELETE)
		{
			Object[] cells = graph.getSelectionCells();
			graph.getModel().remove(cells);
		}
		else if (ke.getKeyCode() == KeyEvent.VK_SPACE)
		{
			DefaultGraphCell hello = new DefaultGraphCell("new");
			AttributeMap helloAttrib = new AttributeMap();
			Rectangle helloBounds = new Rectangle(10, 10, 40, 40);
			GraphConstants.setBounds(helloAttrib, helloBounds);
			GraphConstants.setBorderColor(helloAttrib, Color.black);
			DefaultPort hp = new DefaultPort();
			hello.add(hp);

			Object[] cells = new Object[] { hello };

			Map attributes = new Hashtable();
			attributes.put(hello, helloAttrib);
			graph.getModel().insert(cells, attributes, null, null, null);
		}
		else if (ke.getKeyCode() == KeyEvent.VK_ENTER)
		{
			DefaultEdge edge = new DefaultEdge();
			Map edgeAttrib = new AttributeMap();
			int arrow = GraphConstants.ARROW_CLASSIC;
			GraphConstants.setLineEnd(edgeAttrib, arrow);
			GraphConstants.setEndFill(edgeAttrib, true);

			Object[] cells = new Object[] { edge };

			Map attributes = new Hashtable();
			attributes.put(edge, edgeAttrib);

			graph.getModel().insert(cells, attributes, null, null, null);
		}
	}
}
