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
package org.lucane.common.signature;

import java.security.*;


/**
 * Sign documents or streams
 */
public class Signer
{
  private PrivateKey mykey;

  /**
   * Constructor.
   * 
   * @param key the Base64 encoded Private key
   */
  public Signer(String key)
  throws SignatureException
  {
    this.mykey = Base64.decodePrivateKey(key);
  }

  /**
   * Constructor.
   * 
   * @param key the ciphered base64 encoded private key
   * @param passwd the clear text password
   */
  public Signer(String key, String passwd)
  throws SignatureException
  {
    this.mykey = Base64.decodePrivateKey(cypher(key, passwd));
  }  
    
  /**
   * Sign an object
   * 
   * @param o the object to sign
   * @return the signature
   */
  public byte[] sign(Object o)
  throws SignatureException
  {
	try
	{
	  String data = null;
	  Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
	  dsa.initSign(this.mykey);
	  
	  if(o instanceof Signable)
	  	data = ((Signable)o).toSignableString();
	  else 
	  	data = o.toString();
	  	
	  dsa.update(data.getBytes());
	  return dsa.sign();
	}
	catch(Exception e)
	{
  	  e.printStackTrace();
	  throw new SignatureException(e);
	}
  }

  /**
   * Encode/Decode a key with a password
   * 
   * @param key the encoded key or the key to encode
   * @param password the password
   * @return the key obtained by xor
   */
  public static String cypher(String key, String password)
  {
    byte[] origin = Base64.decode(key);
    byte[] passwd = password.getBytes();
    byte[] result = new byte[origin.length];

    for(int i = 0; i < origin.length; i++)
      result[i] = (byte)(origin[i] ^ passwd[i % passwd.length]);

    return Base64.encode(result);
  }
}
