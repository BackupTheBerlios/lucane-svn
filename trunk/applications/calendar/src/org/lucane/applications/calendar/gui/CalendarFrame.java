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

import org.lucane.client.Client;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.concepts.UserConcept;
import org.lucane.applications.calendar.CalendarPlugin;
import org.lucane.applications.calendar.Event;
import org.lucane.applications.calendar.widget.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;

public class CalendarFrame extends JFrame
implements ActionListener, CalendarListener
{
	private MonthPanel monthPanel;
	private DayPanel dayPanel;
	
	private JButton newEvent;
	private JButton goToCurrentMonth;
	private JButton goToCurrentDay;
	private JButton otherCalendars;
	private JButton close;
	
	private transient CalendarPlugin plugin;
	
	public CalendarFrame(CalendarPlugin plugin)
	{
		super(plugin.getTitle());
		getContentPane().setLayout(new BorderLayout());
		this.plugin = plugin;
		
		monthPanel = new MonthPanel(plugin, this, null);
		dayPanel = new DayPanel(plugin, this, null);
	
		newEvent = new JButton(tr("btn.newEvent"));
		goToCurrentMonth = new JButton(tr("btn.thisMonth"));
		goToCurrentDay = new JButton(tr("btn.today"));
		otherCalendars = new JButton(tr("btn.otherCalendars"));
		close = new JButton(tr("btn.close"));
		
		try {
			newEvent.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "new.png")));
			goToCurrentMonth.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "jumpTo.png")));
			goToCurrentDay.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "jumpTo.png")));
			otherCalendars.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "other.png")));
			close.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "close.png")));
		} catch(Exception e) {
			//nothing, no icons :-/
		}

		newEvent.addActionListener(this);
		goToCurrentMonth.addActionListener(this);
		goToCurrentDay.addActionListener(this);
		otherCalendars.addActionListener(this);
		close.addActionListener(this);

		JPanel topbar = new JPanel(new BorderLayout());
		initTopBar(topbar);
		
		getContentPane().add(topbar, BorderLayout.NORTH);
		getContentPane().add(monthPanel, BorderLayout.CENTER);
	}
	
	private void initTopBar(JPanel bar)
	{		
		JPanel buttons = new JPanel(new GridLayout(1, 5));
		
		buttons.add(newEvent);
		buttons.add(goToCurrentMonth);
		buttons.add(goToCurrentDay);
		buttons.add(otherCalendars);
		buttons.add(close);
		
		bar.add(buttons, BorderLayout.EAST);
		bar.setBorder(BorderFactory.createEmptyBorder(2, 15, 5, 1));
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == newEvent)
		{			
			//TODO create a better default event
			long time = Calendar.getInstance().getTimeInMillis();
			Event e = new Event(-1, "", tr("event.newEvent"), Client.getInstance().getMyInfos().getName(),
					true, time, time+3600*1000, Event.RECUR_NONE, "");
			
			new EventFrame(plugin, e, dayPanel, monthPanel).show();
		}
		else if(ae.getSource() == goToCurrentMonth)
		{
			monthPanel.showMonth(Calendar.getInstance());
			if(getContentPane().getComponent(1) != monthPanel)
			{
				getContentPane().remove(dayPanel);
				getContentPane().add(monthPanel, BorderLayout.CENTER);
				getContentPane().validate();
				this.repaint();
			}
		}
		else if(ae.getSource() == goToCurrentDay)
		{
			dayPanel.showDay(Calendar.getInstance());
			if(getContentPane().getComponent(1) != dayPanel)
			{
				getContentPane().remove(monthPanel);
				getContentPane().add(dayPanel, BorderLayout.CENTER);
				getContentPane().validate();
				this.repaint();
			}
		}
		else if(ae.getSource() == otherCalendars)
		{
			ArrayList users;
			try {
				users = plugin.getUsers();
				int index = DialogBox.list(this, tr("userSelection"), tr("msg.selectUser"), new Vector(users));
				if(index < 0)
					return;
				
				UserConcept user = (UserConcept)users.get(index);
				CalendarViewer viewer = new CalendarViewer(plugin, user.getName());
				viewer.setSize(780, 550);
				viewer.setIconImage(plugin.getImageIcon().getImage());
				viewer.show();					
			} catch (Exception e) {
				DialogBox.error(tr("err.unableToFetchUserList"));
				e.printStackTrace();
			}

		}
		else if(ae.getSource() == close)
		{
			this.dispose();
			plugin.exit();
		}
	} 
	
	public void onDayClick(DayItem day) 
	{
		int dayOfMonth = day.getDayOfMonth();
		Calendar cal = monthPanel.getCalendar();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		dayPanel.showDay(cal);
		getContentPane().remove(monthPanel);
		getContentPane().add(dayPanel, BorderLayout.CENTER);
		getContentPane().validate();
		this.repaint();
	}

	public void onEventClick(EventLabel event) 
	{
		new EventFrame(plugin, (Event)event.getEvent(), dayPanel, monthPanel).show();
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}