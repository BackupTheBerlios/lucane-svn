/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.common;

import java.util.logging.*;
import java.io.*;

public class Logging
{
	public static void init(String fileName, String level)
	throws IOException
	{
		FileHandler f = new FileHandler(fileName);
		f.setFormatter(new SimpleFormatter());
		f.setLevel(Level.parse(level));

		Logger l = getLogger();
		l.addHandler(f);
		l.setLevel(f.getLevel());
	}
	
	public static Logger getLogger()
	{
		return Logger.getLogger("org.lucane");
	}
}