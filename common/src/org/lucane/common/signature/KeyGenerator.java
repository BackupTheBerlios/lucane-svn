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
 * Key generator.
 */
public class KeyGenerator
{
  /**
   * Creates a new Key Pair. 
   * The returned array contains 2 elements :
   * - the Private Key
   * - the Public Key
   * Both are encoded in Base64
   * 
   * @return the generated key pair 
   * @throws SignatureException DOCUMENT ME!
   */
  public static String[] generateKeyPair()
  throws SignatureException
  {
    try
    {
      KeyPair kp = KeyManager.generateNewKeyPair();
      String[] pair = new String[2];
      pair[0] = Base64.encodeKey(kp.getPrivate());
      pair[1] = Base64.encodeKey(kp.getPublic());

      return pair;
    }
    catch(Exception e)
    {
      throw new SignatureException(e);
    }
  }
}