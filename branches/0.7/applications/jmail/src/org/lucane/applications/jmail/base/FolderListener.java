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

import javax.mail.*;

/** Threaded listener to periodically check the mailbox
    (works only on IMAP) */
final class FolderListener extends Thread
{
    /** JMail's panel */
    private MainPanel panel;

    /** Constructor
     *  @param panel JMail's panel
     */
    protected FolderListener(MainPanel panel)
    {
	this.panel = panel;
    }

    /** Main loop */
    public final void run()
    {
	Folder f = null;

	try
	{
	    while(true)
	    {
		Thread.sleep(200000);

		f = panel.getCurrentFolder();

		if(f != null)
		    if(f.isOpen())
			f.getMessageCount();
	    }
	}

	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
}
