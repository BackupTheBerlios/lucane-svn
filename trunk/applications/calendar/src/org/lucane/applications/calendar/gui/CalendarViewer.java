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
import org.lucane.applications.calendar.CalendarPlugin;
import org.lucane.applications.calendar.widget.*;
import org.lucane.applications.calendar.Event;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;
import java.text.*;

public class CalendarViewer extends JFrame
implements ActionListener, CalendarListener
{
	private MonthPanel monthPanel;
	private DayPanel dayPanel;
	
	private JButton goToCurrentMonth;
	private JButton goToCurrentDay;
	private JButton close;
	
	private transient CalendarPlugin plugin;
	
	public CalendarViewer(CalendarPlugin plugin, String userName)
	{
		super(plugin.getTitle() + " - " +userName);
		getContentPane().setLayout(new BorderLayout());
		this.plugin = plugin;
		
		monthPanel = new MonthPanel(plugin, this, userName);
		dayPanel = new DayPanel(plugin, this, userName);
	
		goToCurrentMonth = new JButton(tr("btn.thisMonth"));
		goToCurrentDay = new JButton(tr("btn.today"));
		close = new JButton(tr("btn.close"));
		
		try {
			goToCurrentMonth.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "jumpTo.png")));
			goToCurrentDay.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "jumpTo.png")));
			close.setIcon(new ImageIcon(new URL(plugin.getDirectory() + "close.png")));
		} catch(Exception e) {
			//nothing, no icons :-/
		}

		goToCurrentMonth.addActionListener(this);
		goToCurrentDay.addActionListener(this);
		close.addActionListener(this);

		JPanel topbar = new JPanel(new BorderLayout());
		initTopBar(topbar);
		
		getContentPane().add(topbar, BorderLayout.NORTH);
		getContentPane().add(monthPanel, BorderLayout.CENTER);
	}
	
	private void initTopBar(JPanel bar)
	{		
		Locale locale = new Locale(Client.getInstance().getLanguage());
		DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, locale);
		JLabel dayLabel = new JLabel(df.format(new Date()));
		JPanel buttons = new JPanel(new GridLayout(1, 3));
		
		buttons.add(goToCurrentMonth);
		buttons.add(goToCurrentDay);
		buttons.add(close);
		
		bar.add(dayLabel, BorderLayout.WEST);
		bar.add(buttons, BorderLayout.EAST);
		bar.setBorder(BorderFactory.createEmptyBorder(2, 15, 5, 1));
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == goToCurrentMonth)
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
		else if(ae.getSource() == close)
		{
			this.dispose();
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

	public void onEventClick(EventLabel label) 
	{
		Event event = (Event)label.getEvent();
		if(event.isPublic())
			new EventFrame(plugin, event, dayPanel, monthPanel).show();
		else
			DialogBox.info(tr("event.is.private"));
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}