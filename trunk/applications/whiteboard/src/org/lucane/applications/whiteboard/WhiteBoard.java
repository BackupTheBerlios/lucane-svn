package org.lucane.applications.whiteboard;

import org.lucane.client.Communicator;
import org.lucane.client.Plugin;
import org.lucane.common.*;

public class WhiteBoard extends Plugin
{
  private ObjectConnection connection;
  private ConnectInfo[] friends;
  private String message;

  public WhiteBoard()
  {
    //nothing
  }


  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    Plugin p = new WhiteBoard(friends, starter);
    return p;
  }


  public WhiteBoard(ConnectInfo[] friends, boolean starter)
  {
    this.starter = starter;
    this.friends = friends;
  }

  public void load(ObjectConnection friend, ConnectInfo who, String data)
  {
  	this.connection = friend;
    this.friends = new ConnectInfo[1];
    this.friends[0] = who;
    this.message = data;    	
  }

  public void start()
  {
  	this.connection = Communicator.getInstance().sendMessageTo(friends[0], this.getName(), "");
  
	GraphGui gui = new GraphGui(connection);
	gui.setGraphAsSource();
	gui.showWindow(this, "start");
  }

  public void follow()
  {
	GraphGui gui = new GraphGui(connection);
	gui.setGraphAsDest();
	gui.showWindow(this, "follow");
  }
}
