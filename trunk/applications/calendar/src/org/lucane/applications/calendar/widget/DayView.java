/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package org.lucane.applications.calendar.widget;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

/**
 * A calendar view by day
 */
public class DayView extends JScrollPane
implements MouseListener
{
	//-- colors
	private Color unworkedHour;
	private Color workedHour;
	private int workStart;
	private int workEnd;
	
	//-- attributes
	private JPanel contentPanel;
	private JPanel[] background;
	private JPanel[] border;
	private ArrayList[] occupied;
	
	//-- listeners
	private ArrayList listeners;
	
	/**
	 * Constructor.
	 * Creates an empty day view
	 */
	public DayView(Color unworkedHour, Color workedHour, int workStart, int workEnd)
	{
		//-- init 		
		this.listeners = new ArrayList();	
		
		this.unworkedHour = unworkedHour;
		this.workedHour = workedHour;
		this.workStart = workStart;
		this.workEnd = workEnd;
		
		this.contentPanel = new JPanel(new GridBagLayout());
		this.setViewportView(contentPanel);
		this.background = new JPanel[48];
		this.border = new JPanel[48];
		this.occupied = new ArrayList[48];
		for(int i=0;i<occupied.length;i++)
			occupied[i] = new ArrayList();
		
		//speed up scrolling !
		this.getVerticalScrollBar().setUnitIncrement(15);
		
		
		GridBagConstraints constraints;		
		
		//-- hours
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 2;
		constraints.ipadx = 20;
		constraints.ipady = 20;
		for(int i=0;i<24;i++)
		{
			constraints.gridy = i*2;
			
			JLabel lbl = new JLabel("" + i);
			lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			lbl.setHorizontalAlignment(JLabel.CENTER);
			contentPanel.add(lbl, constraints);
		}
		
		//-- border
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;

		for(int i=0;i<24*2;i++)
		{
			constraints.gridy = i;
			JPanel border = new JPanel();
			border.setBackground(Color.WHITE);
			this.border[i] = border; 

			contentPanel.add(border, constraints); 
		}	
		
		//-- content
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridwidth = 10;
		constraints.weighty = 1;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.BOTH;
		for(int i=0;i<24*2;i++)
		{
			constraints.gridy = i;
			JPanel back = new JPanel();
			this.background[i] = back; 
			back.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			if(i<workStart*2 || i >= (workEnd+1)*2)
				back.setBackground(this.unworkedHour);
			else
				back.setBackground(this.workedHour);

			contentPanel.add(back, constraints); 
		}		
	}
	
	/**
	 * Add a listener
	 * 
	 * @param listener the CalendarListener to add
	 */
	public void addCalendarListener(CalendarListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Scroll to an hour
	 * @param hour the hour that should be on top
	 */
	public void scrollToHour(int hour)
	{
		getViewport().setViewPosition(new Point(0, hour*40-20));
	}
	
	/**
	 * Add an event to the view
	 * 
	 * @param event the Event to add
	 */
	public void addEvent(BasicEvent event)
	{
		//-- create the label
		EventLabel label = new EventLabel(event);
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label.addMouseListener(this);
		
		//-- translate some infos
		int start = event.getStartHour() * 2 + (event.getStartMinute() / 30);
		int end = event.getEndHour() * 2 + (event.getEndMinute() / 30);
		int length = end - start;
		if(length < 1)
			length = 1;
		
		//-- put the label on the panel
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = getFreeIndexFor(start, length);
		constraints.gridy = start;
		constraints.gridheight = length;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.ipadx = 5;
		constraints.ipady = 5;
		this.contentPanel.add(label, constraints);
		
		//-- update attributes
		for(int i=0;i<length;i++)
		{
			this.background[start + i].setVisible(false);
			this.border[start + i].setBackground(event.getColor());
			this.occupied[start + i].add(new Integer(constraints.gridx));
		}		
	}
	
	//-- mouse listener
	
	public void mousePressed(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseClicked(MouseEvent me)
	{
		EventLabel event = (EventLabel)me.getSource(); 
		
		Iterator i = listeners.iterator();
		while(i.hasNext())
		{
			CalendarListener listener = (CalendarListener)i.next();
			listener.onEventClick(event);
		}
	}
	
	/**
	 * Get the next free index for the gridbag.
	 * Used to avoid collisions if two events occurs at the same time
	 * 
	 * @param start the start index
	 * @param length the event length
	 * @return the next free index
	 */
	private int getFreeIndexFor(int start, int length)
	{
		Integer number = new Integer(2);
		
		for(int i=0;i<length;i++)
		{
			if(occupied[start+i].contains(number))
			{
				number = new Integer(number.intValue()+1);
				i =-1;
			}
		}
		
		return number.intValue();
	}
}