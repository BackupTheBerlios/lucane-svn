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
import java.security.spec.*;


/**
 * Handles Base64 enconding.
 * Inspired by the Jakarta Project
 */
class Base64
{
  /**
   * Encodes a byte array
   * 
   * @param raw the bytes to encode
   * @return the encoded string
   */
  public static String encode(byte[] raw)
  {
    int encLen = ((raw.length + 2) / 3) * 4;
    StringBuffer encoded = new StringBuffer(encLen);
    int len3 = (raw.length / 3) * 3;

    for(int i = 0; i < len3; i += 3)
      encoded.append(encodeFullBlock(raw, i));

    if(len3 < raw.length)
      encoded.append(encodeBlock(raw, len3));

    return encoded.toString();
  }

  /**
   * Decode a string
   * 
   * @param base64 the string to decode
   * @return the corresponding byte array
   */
  public static byte[] decode(String base64)
  {
    try
    {
      int pad = 0;

      for(int i = base64.length() - 1;
          (i > 0) && (base64.charAt(i) == '=');
          i--)
        pad++;

      int length = base64.length() / 4 * 3 - pad;
      byte[] raw = new byte[length];

      for(int i = 0, rawIndex = 0; i < base64.length(); i += 4, rawIndex += 3)
      {
        int block = (getValue(base64.charAt(i)) << 18) + 
                    (getValue(base64.charAt(i + 1)) << 12) + 
                    (getValue(base64.charAt(i + 2)) << 6) + 
                    (getValue(base64.charAt(i + 3)));

        for(int j = 2; j >= 0; j--)
        {
          if(rawIndex + j < raw.length)
            raw[rawIndex + j] = (byte)(block & 0xff);

          block >>= 8;
        }
      }

      return raw;
    }
    catch(Exception e)
    {
      return new byte[0];
    }
  }

  /**
   * Encode a Key in base64
   * 
   * @param key the Key to encode
   * @return the encoded String
   */
  public static String encodeKey(Key key)
  {
    return encode(key.getEncoded());
  }

  /**
   * Decode a PrivateKey
   * 
   * @param base64 the encoded string
   * @return the PrivateKey
    */
  public static PrivateKey decodePrivateKey(String base64)
  throws SignatureException
  {
    try
    {
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decode(base64));
      KeyFactory factory = KeyFactory.getInstance("DSA");

      return factory.generatePrivate(spec);
    }
    catch(Exception e)
    {
      throw new SignatureException(e);
    }
  }

  /**
   * Decode a PublicKey
   * 
   * @param base64 the String to decode
   * @return the decoded PublicKey
   */
  public static PublicKey decodePublicKey(String base64)
  throws SignatureException
  {
    try
    {
      X509EncodedKeySpec spec = new X509EncodedKeySpec(decode(base64));
      KeyFactory factory = KeyFactory.getInstance("DSA");

      return factory.generatePublic(spec);
    }
    catch(Exception e)
    {
      throw new SignatureException(e);
    }
  }

  private static char[] encodeBlock(byte[] raw, int offset)
  {
    int block = 0;
    int slack = raw.length - offset - 1;
    int end = (slack >= 2) ? 2 : slack;

    for(int i = 0; i < 3; i++)
    {

      byte b = (offset + i < raw.length) ? raw[offset + i] : 0;
      int neuter = (b < 0) ? b + 256 : b;
      block <<= 8;
      block += neuter;
    }

    char[] base64 = new char[4];

    for(int i = 3; i >= 0; i--)
    {

      int sixBit = block & 0x3f;
      base64[i] = getChar(sixBit);
      block >>= 6;
    }

    if(slack < 1)
      base64[2] = '=';

    if(slack < 2)
      base64[3] = '=';

    return base64;
  }

  private static char[] encodeFullBlock(byte[] raw, int offset)
  {
    int block = 0;

    for(int i = 0; i < 3; i++)
    {
      block <<= 8;
      block += (0xff & raw[offset + i]);
    }

    block = ((raw[offset] & 0xff) << 16) + ((raw[offset + 1] & 0xff) << 8) + 
            (raw[offset + 2] & 0xff);

    char[] base64 = new char[4];

    for(int i = 3; i >= 0; i--)
    {
      int sixBit = block & 0x3f;
      base64[i] = getChar(sixBit);
      block >>= 6;
    }

    return base64;
  }

  private static char getChar(int sixBit)
  {
    if((sixBit >= 0) && (sixBit < 26))
      return (char)('A' + sixBit);

    if((sixBit >= 26) && (sixBit < 52))
      return (char)('a' + (sixBit - 26));

    if((sixBit >= 52) && (sixBit < 62))
      return (char)('0' + (sixBit - 52));

    if(sixBit == 62)
      return '+';

    if(sixBit == 63)
      return '/';

    return '?';
  }

  private static int getValue(char c)
  {
    if((c >= 'A') && (c <= 'Z'))
      return c - 'A';

    if((c >= 'a') && (c <= 'z'))
      return c - 'a' + 26;

    if((c >= '0') && (c <= '9'))
      return c - '0' + 52;

    if(c == '+')
      return 62;

    if(c == '/')
      return 63;

    if(c == '=')
      return 0;

    return -1;
  }
}