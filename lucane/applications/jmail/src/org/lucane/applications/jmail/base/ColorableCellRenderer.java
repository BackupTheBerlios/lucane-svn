package org.lucane.applications.jmail.base;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of JMail                                              *
 * Copyright (C) 2002-2003 Yvan Norsa <norsay@wanadoo.fr>                  *
 *                                                                         *
 * JMail is free software; you can redistribute it and/or modify           *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * any later version.                                                      *
 *                                                                         *
 * JMail is distributed in the hope that it will be useful,                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with JMail; if not, write to the Free Software Foundation, Inc.,        *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.mail.*;
import javax.swing.*;
import javax.swing.table.*;

/** This class is used to display the messages list. It allows to colorize mails and also to display dates according Locale */
final class ColorableCellRenderer extends DefaultTableCellRenderer
{
    /** DateFormat used to display dates */
    private DateFormat df;
    private MainPanel parent;

    /** Constructor
     *  @param df the <code>DateFormat</code> used
     *  @param parent the calling panel
     */
    ColorableCellRenderer(DateFormat df, MainPanel parent)
    {
	super();
	this.df = df;
	this.parent = parent;
    }

    /** TODO : put the DefaultTableCellRenderer.getTableCellRendererComponent() description here */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	if(parent.isFlagSet(row, Flags.Flag.ANSWERED))
	    renderer.setForeground(Color.green);

	else if(!parent.isFlagSet(row, Flags.Flag.SEEN))
	    renderer.setFont(new Font(null, Font.BOLD, 12));

	else
	    renderer.setForeground(Color.black);

	if(value instanceof Date)
	    setText(df.format(value));
	
	return(renderer);
    }
}
