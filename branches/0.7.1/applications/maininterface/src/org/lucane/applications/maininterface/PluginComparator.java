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
package org.lucane.applications.maininterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.lucane.client.Plugin;

public class PluginComparator
implements Comparator
{
	public int compare(Object o1, Object o2) 
	{
		Plugin p1 = (Plugin)o1;
		Plugin p2 = (Plugin)o2;
		
		int category = p1.getCategory().compareTo(p2.getCategory());
		if(category != 0)
			return category;
		
		return p1.getTitle().compareTo(p2.getTitle());
	}
	
	public static Iterator sortPlugins(Iterator i)
	{
		ArrayList list = new ArrayList();
		while(i.hasNext())
			list.add(i.next());
		
		Collections.sort(list, new PluginComparator());
		
		return list.iterator();
	}
}
