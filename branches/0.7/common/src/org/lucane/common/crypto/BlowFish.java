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
package org.lucane.common.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.*;

public class BlowFish
{
	public static String cipher(String key, String str)
	throws CryptoException
	{
		if(key.length() > 16)
			key = key.substring(0, 16);
		
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "BlowFish");
		Cipher cipher;
		byte[] encrypted;
		
		try	{
			cipher = Cipher.getInstance("BlowFish");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			encrypted = cipher.doFinal(str.getBytes());
		} catch(Exception e) {
			throw new CryptoException(e);
		}

		return new BASE64Encoder().encode(encrypted);
	}

	public static String decipher(String key, String str)
	throws CryptoException
	{
		if(key.length() > 16)
			key = key.substring(0, 16);
		
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "BlowFish");
		Cipher cipher;
		byte[] decrypted;
		
		try	{
			cipher = Cipher.getInstance("BlowFish");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decrypted = cipher.doFinal(new BASE64Decoder().decodeBuffer(str));
		} catch(Exception e) {
			throw new CryptoException(e);
		}
		
		return new String(decrypted);
	}
	
	public static void main(String [] args)
	throws Exception
	{
		System.out.println(cipher("guest", "str"));
	}
}