/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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
 
package org.lucane.common.concepts;


//links : service <-> group
public class ServiceConcept extends Concept
{
	private boolean installed;
	
	public ServiceConcept(String name, boolean installed)
	{
		super(name, "");
		this.installed = installed;
	}

	public boolean isInstalled()
	{
		return this.installed;
	}

    public void setInstalled()
    {
        this.installed = true;
    }
	
	//--
    
	public boolean equals(Object o)
	{
		if(o instanceof ServiceConcept)
			return this.name.equals(((ServiceConcept)o).name);			

		return false;
	}
}