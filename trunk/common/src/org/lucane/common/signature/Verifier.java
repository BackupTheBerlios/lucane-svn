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

import java.io.*;
import java.security.*;


/**
 * Verify signed documents or streams
 */
public class Verifier implements Serializable
{
  private PublicKey otherkey;

  /**
   * Constructor.
   * 
   * @param key the public key used to verify the signature
   */
  public Verifier(String key)
  throws SignatureException
  {
    this.otherkey = Base64.decodePublicKey(key);
  }
  
  /**
	* Check an object signature
	* 
	* @param o the signed object
	* @param signature the signature to check
	* @return true if the signature is valid, else otherwise
	*/
   public boolean verify(Object o, byte[] signature)
   throws SignatureException
   {
	boolean isok = false;

	try
	{
	  String data = null;
	  Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
	  sig.initVerify(this.otherkey);
	  
	  if(o instanceof Signable)
		data = ((Signable)o).toSignableString();
	  else 
		data = o.toString();
	  	
	  sig.update(data.getBytes());
	  isok = sig.verify(signature);
	}
	catch(java.security.SignatureException e)
	{
	  isok = false;
	}
	catch(Exception e)
	{
	  throw new SignatureException(e);
	}

	return isok;
   }
}
