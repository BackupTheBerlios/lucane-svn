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

public class UIFactory
{
	private JColorChooser workedColor; 
	private JColorChooser unworkedColor; 
	private JSlider workStart;
	private JSlider workEnd;
	
	
	public  JFrame createMainFrame(CalendarPrefs plugin)
	{
		JFrame frame = new JFrame(plugin.getTitle());
		frame.setIconImage(plugin.getImageIcon().getImage());			
		frame.getContentPane().setLayout(new BorderLayout());
		
		// hours
		workStart = new JSlider(0, 24);
		workStart.setPaintLabels(true);
		workStart.setPaintTicks(true);
		workStart.setMajorTickSpacing(8);
		workStart.setMinorTickSpacing(1);
		workStart.setSnapToTicks(true);
		workEnd = new JSlider(0, 24);
		workEnd.setPaintLabels(true);
		workEnd.setPaintTicks(true);
		workEnd.setMajorTickSpacing(8);
		workEnd.setMinorTickSpacing(1);
		workEnd.setSnapToTicks(true);
		
		JPanel hours = new JPanel(new GridLayout(2, 2));
		hours.add(new JLabel(plugin.tr("lbl.workStart")));
		hours.add(workStart);
		hours.add(new JLabel(plugin.tr("lbl.workEnd")));
		hours.add(workEnd);
		JPanel hourContainer = new JPanel(new BorderLayout());
		hourContainer.add(hours, BorderLayout.WEST);
		
		
		// colors
		workedColor = new JColorChooser();
		unworkedColor = new JColorChooser();
		workedColor.setBorder(BorderFactory.createTitledBorder(plugin.tr("lbl.workedColor")));
		unworkedColor.setBorder(BorderFactory.createTitledBorder(plugin.tr("lbl.unworkedColor")));
		JPanel colors = new JPanel(new GridLayout(1, 2));
		colors.add(workedColor);
		colors.add(unworkedColor);
		
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
		
		// frame
		frame.getContentPane().add(hourContainer, BorderLayout.NORTH);
		frame.getContentPane().add(colors, BorderLayout.CENTER);
		frame.getContentPane().add(buttonContainer, BorderLayout.SOUTH);
		frame.pack();
		
		return frame;
	}
	
	public Color getWorkedColor()
	{
		return workedColor.getColor();
	}
	
	public Color getUnworkedColor()
	{
		return unworkedColor.getColor();
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