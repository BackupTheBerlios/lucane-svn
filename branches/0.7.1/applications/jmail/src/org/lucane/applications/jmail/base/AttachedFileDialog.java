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

import java.util.*;
import javax.swing.*;

/** This class displays an attached file in a new dialog
 *  FIXME : display it properly in a window (800 * 600 max) with scrollbars !
 */
final class AttachedFileDialog extends JDialog
{
    /** Constructor
     *  @param title name of the file
     *  @param comp the component (= content of the file) to be displayed
     *  @param msgBundle language resource
     */
    protected AttachedFileDialog(String title, JComponent comp, ResourceBundle msgBundle)
    {
	super((JFrame)null, title, true);

	JPanel panel = new JPanel();

	JScrollPane scrollPane = new JScrollPane(comp);
	panel.add(scrollPane);

	setContentPane(panel);
       
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	pack();
	setVisible(true);
    }
}
