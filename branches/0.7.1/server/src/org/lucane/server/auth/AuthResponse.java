/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004 Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.server.auth;

public class AuthResponse 
{
	public static final int AUTH_ACCEPTED = 0;
	public static final int BAD_CREDENTIALS = 1;
	public static final int USER_LOCKED = 2;
	public static final int LOGIN_DISABLED = 2;
	
	private int value;
	private String privateKey;
	
	public AuthResponse(int value, String privateKey)
	{
		this.value = value;
		this.privateKey = privateKey;
	}
	
	public AuthResponse(int result)
	{
		this(result, null);
	}
		
	public int getValue()
	{
		return this.value;
	}
	
	public String getPrivateKey()
	{
		return this.privateKey;
	}
}
