package org.lucane.common;

/**
 * The ObjectListener interface
 */
public interface ObjectListener
{
  /**
   * Called when an object is read
   * 
   * @param o the object read from the ObjectConnection
   */
  public void objectRead(Object o);
}
