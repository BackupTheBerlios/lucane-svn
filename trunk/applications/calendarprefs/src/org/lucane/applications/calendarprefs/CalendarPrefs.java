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

import java.awt.Color;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

import org.lucane.client.*;
import org.lucane.client.widgets.DialogBox;
import org.lucane.common.*;

public class CalendarPrefs
  extends StandalonePlugin
  implements ActionListener
{
	private JFrame frame;
	private UIFactory ui;
	private LocalConfig prefs;
	
  public CalendarPrefs()
  {
    this.starter = true;
  }

  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new CalendarPrefs();
  }

  public void start()
  {
  		prefs = new LocalConfig("org.lucane.applications.calendar");
  		
		Color unworkedColor = getColor("unworked", new Color(160, 155, 150));
		Color workedColor = getColor("worked", new Color(255, 255, 222));
		int workStart = prefs.getInt("workStart", 8);
		int workEnd = prefs.getInt("workEnd", 18);
		
  		ui = new UIFactory();  		
		ui.setUnworkedColor(unworkedColor);
		ui.setWorkedColor(workedColor);
		ui.setWorkStart(workStart);
		ui.setWorkEnd(workEnd);
		
  		frame = ui.createMainFrame(this);
  		frame.show();
  }

  public Color getColor(String name, Color defaultColor)
  {
	  String r = prefs.get(name + ".r");
	  String g = prefs.get(name + ".g");
	  String b = prefs.get(name + ".b");
		
	  if(r != null && g != null && b != null)
		  return new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
		
	  return defaultColor;	
  }

  public void actionPerformed(ActionEvent ae) 
  {  	
	  	JButton button = (JButton)ae.getSource();
	  	if(button.getName().equals("ok"))
	  	{
	  		prefs.set("workStart", String.valueOf(ui.getWorkStart()));
	  		prefs.set("workEnd", String.valueOf(ui.getWorkEnd()));
	  		
	  		prefs.set("worked.r", String.valueOf(ui.getWorkedColor().getRed()));
	  		prefs.set("worked.g", String.valueOf(ui.getWorkedColor().getGreen()));
	  		prefs.set("worked.b", String.valueOf(ui.getWorkedColor().getBlue()));
	  	
	  		prefs.set("unworked.r", String.valueOf(ui.getUnworkedColor().getRed()));
	  		prefs.set("unworked.g", String.valueOf(ui.getUnworkedColor().getGreen()));
	  		prefs.set("unworked.b", String.valueOf(ui.getUnworkedColor().getBlue()));
	  		
	  		try {
				prefs.save();
			} catch (IOException ioe) {
				DialogBox.error(tr("err.savePrefs"));
				ioe.printStackTrace();
			}
			
			frame.dispose();
	  	}
	  	else if(button.getName().equals("cancel"))
	  		frame.dispose();
	  		
	  	else if(button.getName().equals("worked"))
	  	{
	  		Color color = ui.getWorkedColor();
	  		color = JColorChooser.showDialog(frame, tr("selectWorkedColor"), color);
	  		ui.setWorkedColor(color);
	  	}
		else if(button.getName().equals("unworked"))
		{
			Color color = ui.getUnworkedColor();
			color = JColorChooser.showDialog(frame, tr("selectUnworkedColor"), color);
			ui.setUnworkedColor(color);
		}
  }
}
