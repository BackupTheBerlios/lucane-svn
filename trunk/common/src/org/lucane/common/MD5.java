/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Gautier Ringeisen <gautier_ringeisen@hotmail.com>
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


/**
 * Password hashing
 */
public class MD5
{
    private final static int MD5_S11 = 7;
    private final static int MD5_S12 = 12;
    private final static int MD5_S13 = 17;
    private final static int MD5_S14 = 22;
    private final static int MD5_S21 = 5;
    private final static int MD5_S22 = 9;
    private final static int MD5_S23 = 14;
    private final static int MD5_S24 = 20;
    private final static int MD5_S31 = 4;
    private final static int MD5_S32 = 11;
    private final static int MD5_S33 = 16;
    private final static int MD5_S34 = 23;
    private final static int MD5_S41 = 6;
    private final static int MD5_S42 = 10;
    private final static int MD5_S43 = 15;
    private final static int MD5_S44 = 21;
    private final static int MD5_T01 = 0xd76aa478;
    private final static int MD5_T02 = 0xe8c7b756;
    private final static int MD5_T03 = 0x242070db;
    private final static int MD5_T04 = 0xc1bdceee;
    private final static int MD5_T05 = 0xf57c0faf;
    private final static int MD5_T06 = 0x4787c62a;
    private final static int MD5_T07 = 0xa8304613;
    private final static int MD5_T08 = 0xfd469501;
    private final static int MD5_T09 = 0x698098d8;
    private final static int MD5_T10 = 0x8b44f7af;
    private final static int MD5_T11 = 0xffff5bb1;
    private final static int MD5_T12 = 0x895cd7be;
    private final static int MD5_T13 = 0x6b901122;
    private final static int MD5_T14 = 0xfd987193;
    private final static int MD5_T15 = 0xa679438e;
    private final static int MD5_T16 = 0x49b40821;
    private final static int MD5_T17 = 0xf61e2562;
    private final static int MD5_T18 = 0xc040b340;
    private final static int MD5_T19 = 0x265e5a51;
    private final static int MD5_T20 = 0xe9b6c7aa;
    private final static int MD5_T21 = 0xd62f105d;
    private final static int MD5_T22 = 0x02441453;
    private final static int MD5_T23 = 0xd8a1e681;
    private final static int MD5_T24 = 0xe7d3fbc8;
    private final static int MD5_T25 = 0x21e1cde6;
    private final static int MD5_T26 = 0xc33707d6;
    private final static int MD5_T27 = 0xf4d50d87;
    private final static int MD5_T28 = 0x455a14ed;
    private final static int MD5_T29 = 0xa9e3e905;
    private final static int MD5_T30 = 0xfcefa3f8;
    private final static int MD5_T31 = 0x676f02d9;
    private final static int MD5_T32 = 0x8d2a4c8a;
    private final static int MD5_T33 = 0xfffa3942;
    private final static int MD5_T34 = 0x8771f681;
    private final static int MD5_T35 = 0x6d9d6122;
    private final static int MD5_T36 = 0xfde5380c;
    private final static int MD5_T37 = 0xa4beea44;
    private final static int MD5_T38 = 0x4bdcefa9;
    private final static int MD5_T39 = 0xf6bb4b60;
    private final static int MD5_T40 = 0xbebfbc70;
    private final static int MD5_T41 = 0x289b7ec6;
    private final static int MD5_T42 = 0xeaa127fa;
    private final static int MD5_T43 = 0xd4ef3085;
    private final static int MD5_T44 = 0x04881d05;
    private final static int MD5_T45 = 0xd9d4d039;
    private final static int MD5_T46 = 0xe6bd99e5;
    private final static int MD5_T47 = 0x1fa27cf8;
    private final static int MD5_T48 = 0xc4ac5665;
    private final static int MD5_T49 = 0xf4292244;
    private final static int MD5_T50 = 0x432aff97;
    private final static int MD5_T51 = 0xab9423a7;
    private final static int MD5_T52 = 0xfc93a039;
    private final static int MD5_T53 = 0x655b59c3;
    private final static int MD5_T54 = 0x8f0ccc92;
    private final static int MD5_T55 = 0xffeff47d;
    private final static int MD5_T56 = 0x85845dd1;
    private final static int MD5_T57 = 0x6fa87e4f;
    private final static int MD5_T58 = 0xfe2ce6e0;
    private final static int MD5_T59 = 0xa3014314;
    private final static int MD5_T60 = 0x4e0811a1;
    private final static int MD5_T61 = 0xf7537e82;
    private final static int MD5_T62 = 0xbd3af235;
    private final static int MD5_T63 = 0x2ad7d2bb;
    private final static int MD5_T64 = 0xeb86d391;
    private int a = 0;
    private int b = 0;
    private int c = 0;
    private int d = 0;
    

	/**
	 * Hashes the password
	 *
	 * @param clear the clear password
	 * @return the hashed password
	 */
	public static String encode(String clear)
    {
    	MD5 md5 = new MD5();
    	return md5.encryptToHexString(clear);
    }
    
    /**
     * Creates a new MD5 object.
     */
    private MD5()
    {
    }
    
    /**
     * Hashes the password
     *
     * @param clearWord the clear password
     * @return the hashed password
     */
    private String encryptToHexString(String clearWord)
    {
        encrypt(clearWord);
        
        return (toHex(a) + toHex(b) + toHex(c) + toHex(d));
    }
    
    private void encrypt(String clearWord)
    {        
        int i = 0;
        int j = 4;
        int clearWordLength = clearWord.length();
        int wLen = (((clearWordLength + 8) >> 6) + 1) << 4;
        int[] X = new int[wLen];
        a = 0x67452301;
        b = 0xefcdab89;
        c = 0x98badcfe;
        d = 0x10325476;
        
        for(i = 0; (i * 4) < clearWordLength; i++)
        {
            X[i] = 0;
            
            for(j = 0; (j < 4) && ((j + i * 4) < clearWordLength); j++)
                X[i] += (int)clearWord.charAt(j + i * 4) << (j * 8);
        }
        
        if(j == 4)
            X[i++] = 0x80;
        else
            X[i - 1] += 0x80 << (j * 8);
        
        for(; i < wLen; i++)
            X[i] = 0;
        
        X[wLen - 2] = clearWordLength * 8;
        
        for(i = 0; i < wLen; i += 16)
        {            
            int a0 = 0;
            int b0 = 0;
            int c0 = 0;
            int d0 = 0;
            a = R1(a, b, c, d, X[i + 0], MD5_S11, MD5_T01);
            b = R1(d, a, b, c, X[i + 1], MD5_S12, MD5_T02);
            c = R1(c, d, a, b, X[i + 2], MD5_S13, MD5_T03);
            d = R1(b, c, d, a, X[i + 3], MD5_S14, MD5_T04);
            a = R1(a, b, c, d, X[i + 4], MD5_S11, MD5_T05);
            b = R1(d, a, b, c, X[i + 5], MD5_S12, MD5_T06);
            c = R1(c, d, a, b, X[i + 6], MD5_S13, MD5_T07);
            d = R1(b, c, d, a, X[i + 7], MD5_S14, MD5_T08);
            a = R1(a, b, c, d, X[i + 8], MD5_S11, MD5_T09);
            b = R1(d, a, b, c, X[i + 9], MD5_S12, MD5_T10);
            c = R1(c, d, a, b, X[i + 10], MD5_S13, MD5_T11);
            d = R1(b, c, d, a, X[i + 11], MD5_S14, MD5_T12);
            a = R1(a, b, c, d, X[i + 12], MD5_S11, MD5_T13);
            b = R1(d, a, b, c, X[i + 13], MD5_S12, MD5_T14);
            c = R1(c, d, a, b, X[i + 14], MD5_S13, MD5_T15);
            d = R1(b, c, d, a, X[i + 15], MD5_S14, MD5_T16);
            a = R2(a, b, c, d, X[i + 1], MD5_S21, MD5_T17);
            b = R2(d, a, b, c, X[i + 6], MD5_S22, MD5_T18);
            c = R2(c, d, a, b, X[i + 11], MD5_S23, MD5_T19);
            d = R2(b, c, d, a, X[i + 0], MD5_S24, MD5_T20);
            a = R2(a, b, c, d, X[i + 5], MD5_S21, MD5_T21);
            b = R2(d, a, b, c, X[i + 10], MD5_S22, MD5_T22);
            c = R2(c, d, a, b, X[i + 15], MD5_S23, MD5_T23);
            d = R2(b, c, d, a, X[i + 4], MD5_S24, MD5_T24);
            a = R2(a, b, c, d, X[i + 9], MD5_S21, MD5_T25);
            b = R2(d, a, b, c, X[i + 14], MD5_S22, MD5_T26);
            c = R2(c, d, a, b, X[i + 3], MD5_S23, MD5_T27);
            d = R2(b, c, d, a, X[i + 8], MD5_S24, MD5_T28);
            a = R2(a, b, c, d, X[i + 13], MD5_S21, MD5_T29);
            b = R2(d, a, b, c, X[i + 2], MD5_S22, MD5_T30);
            c = R2(c, d, a, b, X[i + 7], MD5_S23, MD5_T31);
            d = R2(b, c, d, a, X[i + 12], MD5_S24, MD5_T32);
            a = R3(a, b, c, d, X[i + 5], MD5_S31, MD5_T33);
            b = R3(d, a, b, c, X[i + 8], MD5_S32, MD5_T34);
            c = R3(c, d, a, b, X[i + 11], MD5_S33, MD5_T35);
            d = R3(b, c, d, a, X[i + 14], MD5_S34, MD5_T36);
            a = R3(a, b, c, d, X[i + 1], MD5_S31, MD5_T37);
            b = R3(d, a, b, c, X[i + 4], MD5_S32, MD5_T38);
            c = R3(c, d, a, b, X[i + 7], MD5_S33, MD5_T39);
            d = R3(b, c, d, a, X[i + 10], MD5_S34, MD5_T40);
            a = R3(a, b, c, d, X[i + 13], MD5_S31, MD5_T41);
            b = R3(d, a, b, c, X[i + 0], MD5_S32, MD5_T42);
            c = R3(c, d, a, b, X[i + 3], MD5_S33, MD5_T43);
            d = R3(b, c, d, a, X[i + 6], MD5_S34, MD5_T44);
            a = R3(a, b, c, d, X[i + 9], MD5_S31, MD5_T45);
            b = R3(d, a, b, c, X[i + 12], MD5_S32, MD5_T46);
            c = R3(c, d, a, b, X[i + 15], MD5_S33, MD5_T47);
            d = R3(b, c, d, a, X[i + 2], MD5_S34, MD5_T48);
            a = R4(a, b, c, d, X[i + 0], MD5_S41, MD5_T49);
            b = R4(d, a, b, c, X[i + 7], MD5_S42, MD5_T50);
            c = R4(c, d, a, b, X[i + 14], MD5_S43, MD5_T51);
            d = R4(b, c, d, a, X[i + 5], MD5_S44, MD5_T52);
            a = R4(a, b, c, d, X[i + 12], MD5_S41, MD5_T53);
            b = R4(d, a, b, c, X[i + 3], MD5_S42, MD5_T54);
            c = R4(c, d, a, b, X[i + 10], MD5_S43, MD5_T55);
            d = R4(b, c, d, a, X[i + 1], MD5_S44, MD5_T56);
            a = R4(a, b, c, d, X[i + 8], MD5_S41, MD5_T57);
            b = R4(d, a, b, c, X[i + 15], MD5_S42, MD5_T58);
            c = R4(c, d, a, b, X[i + 6], MD5_S43, MD5_T59);
            d = R4(b, c, d, a, X[i + 13], MD5_S44, MD5_T60);
            a = R4(a, b, c, d, X[i + 4], MD5_S41, MD5_T61);
            b = R4(d, a, b, c, X[i + 11], MD5_S42, MD5_T62);
            c = R4(c, d, a, b, X[i + 2], MD5_S43, MD5_T63);
            d = R4(b, c, d, a, X[i + 9], MD5_S44, MD5_T64);
            a = add(a, a0);
            b = add(b, b0);
            c = add(c, c0);
            d = add(d, d0);
        }
    }
    
    private String toHex(int i)
    {
        String h = Integer.toHexString(i);
        
        switch(h.length())
        {
            
            case 1:
                h = "0000000" + h;
                
            case 2:
                h = "000000" + h;
                
            case 3:
                h = "00000" + h;
                
            case 4:
                h = "0000" + h;
                
            case 5:
                h = "000" + h;
                
            case 6:
                h = "00" + h;
                
            case 7:
                h = "0" + h;
        }
        
        return h;
    }
    
    private int add(int x, int y)
    { 
        return ((x & 0x7fffffff) + (y & 0x7fffffff)) ^ (x & 0x80000000) ^
        (y & 0x80000000);
    }
    
    private int R1(int A, int B, int C, int D, int X, int S, int T)
    {        
        int q = add(add(A, (B & C) | ((~ B) & D)), add(X, T));
        
        return add((q << S) | (q >>> (32 - S)), B);
    }
    

    private int R2(int A, int B, int C, int D, int X, int S, int T)
    {        
        int q = add(add(A, (B & D) | (C & (~ D))), add(X, T));
        
        return add((q << S) | (q >>> (32 - S)), B);
    }
    
    private int R3(int A, int B, int C, int D, int X, int S, int T)
    {        
        int q = add(add(A, B ^ C ^ D), add(X, T));
        
        return add((q << S) | (q >>> (32 - S)), B);
    }
    
    private int R4(int A, int B, int C, int D, int X, int S, int T)
	{        
        int q = add(add(A, C ^ (B | (~ D))), add(X, T));
        
        return add((q << S) | (q >>> (32 - S)), B);
    }
}