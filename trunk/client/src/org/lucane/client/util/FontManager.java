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

package org.lucane.client.util;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontManager
{
	public static void setDefaultFont(Font font){
		Font bold12 = font.deriveFont(Font.BOLD, 12);
		Font plain12 = font.deriveFont(Font.PLAIN, 12);
		Font plain10 = font.deriveFont(Font.PLAIN, 10);
		
		//bold 12
		String[] properties = new String[]{"Button.font", "CheckBox.font", 
				"CheckBoxMenuItem.font", "ComboBox.font", 
				"DesktopIcon.font", "InternalFrame.font", 
				"Label.font", "Menu.font ", 
				"MenuBar.font", "MenuItem.font", 
				"ProgressBar.font", "RadioButton.font", 
				"RadioButtonMenuItem.font",	"TabbedPane.font",  
				"TitledBorder.font", "ToggleButton.font", "ToolBar.font"}; 
		for(int i=0;i<properties.length;i++)
			UIManager.put(properties[i], new FontUIResource(bold12)); 
		
		//plain 12
		properties = new String[]{"ColorChooser.font", "EditorPane.font",
				"List.font", "OptionPane.font",
				"Panel.font", "PasswordField.font",
				"PopupMenu.font", "ScrollPane.font",
				"Table.font", "TableHeader.font", 
				"TextArea.font", "TextField.font",                               
				"TextPane.font", "ToolTip.font", 
				"Tree.font", "Viewport.font"};
		for(int i=0;i<properties.length;i++)
			UIManager.put(properties[i], new FontUIResource(plain12));
		
		//plain 10
		properties = new String[]{"Menu.acceleratorFont",  
				"MenuItem.acceleratorFont", "RadioButtonMenuItem.acceleratorFont"};
		for(int i=0;i<properties.length;i++)
			UIManager.put(properties[i], new FontUIResource(plain10));
	}    
	
	public static void setDefaultFont(String fileName) 
	throws FileNotFoundException, FontFormatException, IOException
	{
		Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fileName));
		setDefaultFont(font);
	}
}