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


public class DayPanel extends JPanel
implements ActionListener
{
	private DayView view;
	
	private JButton previousMonth;
	private JButton previousDay;
	
	private JComboBox day;
	private JComboBox month;
	private JComboBox year;
	
	private JButton nextMonth;
	private JButton nextDay;
	
	private Calendar calendar;
	private transient CalendarPlugin plugin;
	private CalendarListener listener;
	
	
	public DayPanel(CalendarPlugin plugin, CalendarListener listener)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		this.listener = listener;
		
		try {
			previousMonth = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "pprevious.png")));
			previousDay = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "previous.png")));
			nextMonth = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "nnext.png")));
			nextDay = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "next.png")));
		} catch(Exception e) {
			previousMonth = new JButton("<<");
			previousDay = new JButton("<");
			nextMonth = new JButton(">>");
			nextDay = new JButton(">");			
		}
		
		previousMonth.addActionListener(this);
		previousDay.addActionListener(this);
		nextMonth.addActionListener(this);
		nextDay.addActionListener(this);
		
		day = new JComboBox();
		month = new JComboBox();
		year = new JComboBox();	
				
		JPanel topbar = new JPanel(new BorderLayout());
		initTopBar(topbar);
		
		
		add(topbar, BorderLayout.NORTH);
		
		calendar = Calendar.getInstance();
		refreshView();
	}
	
	private void initCombos()
	{
		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		day.removeActionListener(this);
		day.removeAllItems();
		for(int i=1;i<=maxDays;i++)
			day.addItem(new Integer(i));
		day.setSelectedIndex(currentDay-1);
		day.addActionListener(this);
		
		int monthIndex = calendar.get(Calendar.MONTH);
		month.removeActionListener(this);
		month.removeAllItems();
		for(int i=1;i<=12;i++)
			month.addItem(tr("month." + i));

		month.setSelectedIndex(monthIndex);		
		month.addActionListener(this);
		
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
		previous.add(previousMonth);
		previous.add(previousDay);

		
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(day, BorderLayout.WEST);
		middle.add(month, BorderLayout.CENTER);
		middle.add(year, BorderLayout.EAST);
		
		JPanel next = new JPanel(new GridLayout(1, 2));
		next.add(nextDay);
		next.add(nextMonth);
		
		bar.add(previous, BorderLayout.WEST);
		bar.add(middle, BorderLayout.CENTER);
		bar.add(next, BorderLayout.EAST);
		bar.setBorder(BorderFactory.createEmptyBorder(2, 1, 3, 1));
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		//-- buttons
		if(ae.getSource() == previousMonth)
			setPreviousMonth();
		else if(ae.getSource() == previousDay)
		{
			int prevDay = calendar.get(Calendar.DAY_OF_MONTH) -1;
			if(prevDay < 0)
			{	
				setPreviousMonth();
				prevDay = calendar.getMaximum(Calendar.DAY_OF_MONTH);
			}
			calendar.set(Calendar.DAY_OF_MONTH, prevDay);
		}
		else if(ae.getSource() == nextMonth)
			setNextMonth();
		else if(ae.getSource() == nextDay)
		{
			int nextDay = calendar.get(Calendar.DAY_OF_MONTH) +1;
			if(nextDay > calendar.getMaximum(Calendar.DAY_OF_MONTH))
			{	
				setNextMonth();
				nextDay = 1;
			}
			calendar.set(Calendar.DAY_OF_MONTH, nextDay);
		}
			
		//-- combos
		else if(ae.getSource() == day)
			calendar.set(Calendar.DAY_OF_MONTH, day.getSelectedIndex()+1);
		else if(ae.getSource() == month)
			calendar.set(Calendar.MONTH, month.getSelectedIndex());
		else if(ae.getSource() == year)
			calendar.set(Calendar.YEAR, ((Integer)year.getSelectedItem()).intValue());
		
		refreshView();
	}
	
	private void setPreviousMonth()
	{
		int prevMonth = calendar.get(Calendar.MONTH) -1;
		if(prevMonth < 0)
		{	
			prevMonth = 11;
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
		}
		calendar.set(Calendar.MONTH, prevMonth);
	}
	
	private void setNextMonth()
	{
		int nextMonth = calendar.get(Calendar.MONTH) +1;
		if(nextMonth > 11)
		{	
			nextMonth = 0;
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
		}
		calendar.set(Calendar.MONTH, nextMonth);
	}
	
	public void refreshView()
	{
		initCombos();
		
		if(view != null)
			remove(view);
		view = new DayView();
		view.addCalendarListener(listener);
		view.scrollToHour(7);

		//-- get day interval (in milliseconds)
		long start, end;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);	
		start = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 24);
		end = cal.getTimeInMillis();
		
		//-- fetch and display events
		try {
			ArrayList events = plugin.getMyEvents(start, end);	
			Iterator i = events.iterator();
			while(i.hasNext())
			{
				Event e = (Event)i.next();
				view.addEvent(e);
			}
		} catch(Exception e) {
			DialogBox.error(e.getMessage());
			e.printStackTrace();
		}
		
		add(view, BorderLayout.CENTER);
	}
	
	public void showDay(Calendar c)
	{
		this.calendar = c;
		refreshView();
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}