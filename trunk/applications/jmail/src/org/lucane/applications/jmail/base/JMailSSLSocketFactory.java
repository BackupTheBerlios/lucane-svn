package org.lucane.applications.jmail.base;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of JMail                                              *
 * Copyright (C) 2002-2003 Yvan Norsa <norsay@wanadoo.fr>                  *
 *                                                                         *
 * JMail is free software; you can redistribute it and/or modify           *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * any later version.                                                      *
 *                                                                         *
 * JMail is distributed in the hope that it will be useful,                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with JMail; if not, write to the Free Software Foundation, Inc.,        *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.*;
import javax.net.ssl.*;

/** This class is done thanks to Java Tip #115 ( http://www.javaworld.com/javatips/jw-javatip115.html ) */
public class JMailSSLSocketFactory extends SSLSocketFactory 
{
    SSLSocketFactory socketfactory;
    
    public JMailSSLSocketFactory()
    {
	try 
	    {
		SSLContext sslcontext = SSLContext.getInstance("TLS");

		sslcontext.init(null, new TrustManager[]
		                      { 
					  new JMailTrustManager()
				      }, new SecureRandom());

		socketfactory = (SSLSocketFactory)sslcontext.getSocketFactory();
	    }

	catch(Exception ex) 
	{
	    ex.printStackTrace();
	}
    }
    
    public static SocketFactory getDefault()
    {
	return((SocketFactory)new JMailSSLSocketFactory());
    }

    public Socket createSocket(Socket s, String hostaddress, int hostport, boolean autoClose) throws IOException
    {
	return(socketfactory.createSocket(s, hostaddress, hostport, autoClose));
    }
    
    public Socket createSocket(InetAddress hostaddress, int hostport) throws IOException
    {
	return(socketfactory.createSocket(hostaddress, hostport));
    }

    public Socket createSocket(InetAddress hostaddress, int hostport, InetAddress clientaddress, int clientport) throws IOException
    {
	return(socketfactory.createSocket(hostaddress, hostport, clientaddress, clientport));
    }

    public Socket createSocket(String hostaddress, int hostport) throws IOException
    {
	return(socketfactory.createSocket(hostaddress, hostport));
    }

    public Socket createSocket(String hostaddress, int hostport, InetAddress clientaddress, int clientport) throws IOException
    {
	return(socketfactory.createSocket(hostaddress, hostport, clientaddress, clientport));
    }
    
    public String[] getDefaultCipherSuites()
    {
	return(socketfactory.getDefaultCipherSuites());
    }

    public String[] getSupportedCipherSuites()
    {
	return(socketfactory.getSupportedCipherSuites());
    }
}
