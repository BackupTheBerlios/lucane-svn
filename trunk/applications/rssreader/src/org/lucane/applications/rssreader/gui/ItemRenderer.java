/*
 * Based on HotSheet ItemListCellRenderer.java by John Munsh
 */
package org.lucane.applications.rssreader.gui;

import java.awt.*;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jperdian.rss2.dom.RssItem;

public class ItemRenderer implements ListCellRenderer 
{
	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus) {

		RssItem item = (RssItem) value;
		
		boolean isViewed = false;
		int score = 50;
		
		return (new ItemComponent(item, isSelected, cellHasFocus, isViewed, score));
	}
}
