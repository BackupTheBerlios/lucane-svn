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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.applications.calendarprefs;

import java.awt.*;

import javax.swing.*;

import org.lucane.client.Client;
import org.lucane.client.widgets.ManagedWindow;

public class UIFactory
{
	private Color workedColor; 
	private Color unworkedColor; 
	private int initialWorkStart;
	private int initialWorkEnd;
	private int initalFirstDayOfWeek;
		
	private JButton worked;
	private JButton unworked;
	private JSlider workStart;
	private JSlider workEnd;
	private JComboBox firstDayOfWeek;
	
	
	public ManagedWindow createMainFrame(CalendarPrefs plugin)
	{
		ManagedWindow window = new ManagedWindow(plugin, plugin.getTitle());
		window.setIconImage(plugin.getImageIcon().getImage());			
		window.getContentPane().setLayout(new BorderLayout());

		JPanel content = new JPanel(new GridLayout(5, 2));
		
		// hours
		workStart = new JSlider(0, 24, initialWorkStart);
		workStart.setPaintLabels(true);
		workStart.setPaintTicks(true);
		workStart.setMajorTickSpacing(8);
		workStart.setMinorTickSpacing(1);
		workStart.setSnapToTicks(true);
		workEnd = new JSlider(0, 24, initialWorkEnd);
		workEnd.setPaintLabels(true);
		workEnd.setPaintTicks(true);
		workEnd.setMajorTickSpacing(8);
		workEnd.setMinorTickSpacing(1);
		workEnd.setSnapToTicks(true);
		
		content.add(new JLabel(plugin.tr("lbl.workStart")));
		content.add(workStart);
		content.add(new JLabel(plugin.tr("lbl.workEnd")));
		content.add(workEnd);
				
		
		// colors
		worked = new JButton("");
		worked.setBackground(workedColor);
		worked.setName("worked");
		worked.addActionListener(plugin);
		unworked = new JButton("");
		unworked.setBackground(unworkedColor);
		unworked.setName("unworked");
		unworked.addActionListener(plugin);
				
		content.add(new JLabel(plugin.tr("lbl.workedColor")));
		content.add(worked);
		content.add(new JLabel(plugin.tr("lbl.unworkedColor")));
		content.add(unworked);		
		
		// first day of week
		firstDayOfWeek = new JComboBox(new Object[]{
				plugin.tr("monday"),
				plugin.tr("sunday"),
		});
		firstDayOfWeek.setSelectedIndex(initalFirstDayOfWeek);
		content.add(new JLabel(plugin.tr("lbl.firstDayOfWeek")));
		content.add(firstDayOfWeek);
		
		// buttons
		JButton ok = new JButton(plugin.tr("btn.ok"), Client.getIcon("ok.png"));
		JButton cancel = new JButton(plugin.tr("btn.cancel"), Client.getIcon("cancel.png"));
		ok.setName("ok");
		cancel.setName("cancel");
		ok.addActionListener(plugin);
		cancel.addActionListener(plugin);
				
		JPanel buttonContainer = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(ok);
		buttons.add(cancel);
		buttonContainer.add(buttons, BorderLayout.EAST);
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		// frame
		window.getContentPane().add(content, BorderLayout.CENTER);
		window.getContentPane().add(buttonContainer, BorderLayout.SOUTH);
		
		return window;
	}
	
	public void setFirstDayOfWeek(int day)
	{
		this.initalFirstDayOfWeek = day;
	}
	
	public int getFirstDayOfWeek()
	{
		return this.firstDayOfWeek.getSelectedIndex();
	}
	
	public void setWorkedColor(Color color)
	{
		if(color == null)
			return;
			
		workedColor = color;
		if(worked != null)
			worked.setBackground(color);
	}
	
	public void setUnworkedColor(Color color)
	{
		if(color == null)
			return;
			
		unworkedColor = color;
		if(unworked != null)
			unworked.setBackground(color);
	}
	
	public Color getWorkedColor()
	{
		return workedColor;
	}
	
	public Color getUnworkedColor()
	{
		return unworkedColor;
	}
	
	public void setWorkStart(int i)
	{
		this.initialWorkStart = i;
	}
	
	public void setWorkEnd(int i)
	{
		this.initialWorkEnd = i;
	}
	
	public int getWorkStart()
	{
		return workStart.getValue();
	}
	
	public int getWorkEnd()
	{
		return workEnd.getValue();
	}
}