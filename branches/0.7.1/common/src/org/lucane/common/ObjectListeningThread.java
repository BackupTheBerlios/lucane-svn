package org.lucane.common;

import java.util.*;

/**
 * A thread that can listen on an ObjectConnection and notify listeners
 */
class ObjectListeningThread extends Thread
{
  //-- attributes
  private ObjectConnection connection;
  private ArrayList listeners;
  private boolean end;

  /**
   * Constructor
   * 
   * @param oc the ObjectConnection to listen
   */
  public ObjectListeningThread(ObjectConnection oc)
  {
    this.connection = oc;
    this.listeners = new ArrayList();
    this.end = false;
  }

  /**
   * Add a listener
   * 
   * @param ol the listener
   */
  public void addObjectListener(ObjectListener ol)
  {
    this.listeners.add(ol);
  }

  /**
   * Stop the thread
   */
  public void close()
  {
    this.end = true;
  }

  /**
   * Thread excecution
   * Read messages and notify listeners
   */
  public void run()
  {
    while(!end)
    {
      if(this.connection.readyToRead())
      {
        try {
          Object o = this.connection.read();
          Iterator i = listeners.iterator();
          while(i.hasNext())
          {
            ObjectListener ol = (ObjectListener)i.next();
            ol.objectRead(o);
          }
        } catch(Exception e) {
          Logging.getLogger().info(e.toString());
        }
      }
      else
      {
        try {
          Thread.sleep(50);
        } catch(Exception e) {}
      }
    }
  }
}
