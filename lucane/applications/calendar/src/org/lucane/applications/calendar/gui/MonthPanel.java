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

public class MonthPanel extends JPanel
implements ActionListener
{
	private MonthView view;
	
	private JButton previousYear;
	private JButton previousMonth;
	
	private JComboBox month;
	private JComboBox year;
	
	private JButton nextYear;
	private JButton nextMonth;
	
	private Calendar calendar;
	private transient CalendarPlugin plugin;
	
	public MonthPanel(CalendarPlugin plugin, CalendarListener listener)
	{
		super(new BorderLayout());
		this.plugin = plugin;
		
		calendar = Calendar.getInstance();
		
		try {
			previousYear = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "pprevious.png")));
			previousMonth = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "previous.png")));
			nextYear = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "nnext.png")));
			nextMonth = new JButton(new ImageIcon(new URL(plugin.getDirectory() + "next.png")));
		} catch(Exception e) {
			previousYear = new JButton("<<");
			previousMonth = new JButton("<");
			nextYear = new JButton(">>");
			nextMonth = new JButton(">");			
		}
		
		previousYear.addActionListener(this);
		previousMonth.addActionListener(this);
		nextYear.addActionListener(this);
		nextMonth.addActionListener(this);
		
		month = new JComboBox();
		year = new JComboBox();		
		
		view = new MonthView(plugin);
		view.addCalendarListener(listener);
				
		JPanel topbar = new JPanel(new BorderLayout());
		initTopBar(topbar);
		
		
		add(topbar, BorderLayout.NORTH);
		add(view, BorderLayout.CENTER);
		
		refreshView();
	}
	
	private void initCombos()
	{			
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
		previous.add(previousYear);
		previous.add(previousMonth);

		
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(month, BorderLayout.CENTER);
		middle.add(year, BorderLayout.EAST);
		
		JPanel next = new JPanel(new GridLayout(1, 2));
		next.add(nextMonth);
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
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
		else if(ae.getSource() == previousMonth)
		{
			int prevMonth = calendar.get(Calendar.MONTH) -1;
			if(prevMonth < 0)
			{	
				prevMonth = 11;
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
			}
			calendar.set(Calendar.MONTH, prevMonth);
		}
		else if(ae.getSource() == nextYear)
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
		else if(ae.getSource() == nextMonth)
		{
			int nextMonth = calendar.get(Calendar.MONTH) +1;
			if(nextMonth > 11)
			{	
				nextMonth = 0;
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
			}
			calendar.set(Calendar.MONTH, nextMonth);
		}			
		
		//-- combos
		if(ae.getSource() == month)
			calendar.set(Calendar.MONTH, month.getSelectedIndex());
		else if(ae.getSource() == year)
			calendar.set(Calendar.YEAR, ((Integer)year.getSelectedItem()).intValue());
		
		refreshView();
	}
	
	public void refreshView()
	{
		initCombos();
		
		view.setDisplayedMonth(calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
		
		//-- get month interval (in milliseconds)
		long start, end;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);	
		start = cal.getTimeInMillis();
		cal.add(Calendar.MONTH, 1);
		end = cal.getTimeInMillis();
		
		//-- fetch and display events
		try {
			ArrayList events = plugin.getMyEvents(start, end);	
			Iterator i = events.iterator();
			while(i.hasNext())
			{
				Event e = (Event)i.next();
				cal.setTimeInMillis(e.getStartTime());
				view.addEvent(cal.get(Calendar.DAY_OF_MONTH), e);
			}
		} catch(Exception e) {
			DialogBox.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void showMonth(Calendar c)
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