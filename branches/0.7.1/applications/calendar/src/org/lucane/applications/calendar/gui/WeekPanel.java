/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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

package org.lucane.applications.calendar.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;

import org.lucane.client.widgets.DialogBox;
import org.lucane.applications.calendar.CalendarPlugin;
import org.lucane.applications.calendar.widget.*;
import org.lucane.applications.calendar.Event;

public class WeekPanel extends JPanel
implements ActionListener
{
	private WeekView view;
	
	private JButton previousYear;
	private JButton previousWeek;
	
	private JComboBox week;
	private JComboBox year;
	
	private JButton nextYear;
	private JButton nextWeek;
	
	private Calendar calendar;
	private CalendarListener listener;
	private String userName;
	
	private transient CalendarPlugin plugin;
	
	public WeekPanel(CalendarPlugin plugin, CalendarListener listener, String userName)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		
		this.listener = listener;
		this.userName = userName;
		
		calendar = Calendar.getInstance();
		
		try {
			previousYear = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "pprevious.png")));
			previousWeek = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "previous.png")));
			nextYear = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "nnext.png")));
			nextWeek = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "next.png")));
		} catch(Exception e) {
			previousYear = new JButton("<<");
			previousWeek = new JButton("<");
			nextYear = new JButton(">>");
			nextWeek = new JButton(">");	
		}
		
		previousYear.addActionListener(this);
		previousWeek.addActionListener(this);
		nextYear.addActionListener(this);
		nextWeek.addActionListener(this);
		
		week = new JComboBox();
		year = new JComboBox();		
		
		view = new WeekView(plugin);
		view.addCalendarListener(listener);
		
		JPanel topbar = new JPanel(new BorderLayout());
		initTopBar(topbar);
		
		
		add(topbar, BorderLayout.NORTH);		
		add(view, BorderLayout.CENTER);
		
		refreshView();
	}
	

	private void initCombos()
	{			
		int weekIndex = calendar.get(Calendar.WEEK_OF_YEAR)-1;
		week.removeActionListener(this);
		week.removeAllItems();
		for(int i=1;i<=52;i++)
			week.addItem(new Integer(i));
		week.setSelectedIndex(weekIndex);
		week.addActionListener(this);
		
		if(weekIndex == 0)
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		else if(weekIndex == 51)
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		int currentYear = calendar.get(Calendar.YEAR);
		year.removeActionListener(this);
		year.removeAllItems();
		for(int i=-3;i<4;i++)
			year.addItem(new Integer(currentYear+i));
		year.setSelectedIndex(3);
		year.addActionListener(this);
	}
	
	private void initTopBar(JPanel bar)
	{
		JPanel previous = new JPanel(new GridLayout(1, 2));
		previous.add(previousYear);
		previous.add(previousWeek);
		
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(week, BorderLayout.CENTER);
		middle.add(year, BorderLayout.EAST);
		
		JPanel next = new JPanel(new GridLayout(1, 2));
		next.add(nextWeek);
		next.add(nextYear);
		
		bar.add(previous, BorderLayout.WEST);
		bar.add(middle, BorderLayout.CENTER);
		bar.add(next, BorderLayout.EAST);
		bar.setBorder(BorderFactory.createEmptyBorder(2, 1, 3, 1));
	}
	
	public void actionPerformed(ActionEvent ae)
	{	
		//-- buttons
		if(ae.getSource() == previousYear)
			calendar.add(Calendar.YEAR, -1);
		else if(ae.getSource() == previousWeek)
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		else if(ae.getSource() == nextYear)
			calendar.add(Calendar.YEAR, 1);
		else if(ae.getSource() == nextWeek)
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		
		//-- combos
		if(ae.getSource() == week)
			calendar.set(Calendar.WEEK_OF_YEAR, ((Integer)week.getSelectedItem()).intValue());
		else if(ae.getSource() == year)
			calendar.set(Calendar.YEAR, ((Integer)year.getSelectedItem()).intValue());
		
		refreshView();
	}
	
	public void refreshView()
	{	
		initCombos();
		view.setDisplayedWeek(calendar.get(Calendar.WEEK_OF_YEAR)+1, calendar.get(Calendar.YEAR));
		
		//-- get month interval (in milliseconds)
		long start, end;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);	
		start = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_WEEK, 7);
		end = cal.getTimeInMillis();
		
		//-- fetch and display events
		try {			
			//if no user, show my events
			ArrayList events;			
				events = plugin.getMyEvents(start, end);
			
			Iterator i = events.iterator();
			while(i.hasNext())
			{
				Event e = (Event)i.next();
				cal.setTimeInMillis(e.getStartTime());
				view.addEvent(cal.get(Calendar.DAY_OF_WEEK)-1, e);
			}
		} catch(Exception e) {
			DialogBox.error(e.getMessage());
			e.printStackTrace();
		}
		
		validate();
	}
	
	public void showWeek(Calendar c)
	{
		this.calendar = c;
		refreshView();
	}
	
	public Calendar getCalendar()
	{
		return (Calendar)calendar.clone();
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}