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

import java.security.cert.*;
import javax.net.ssl.*;

/** This class is done thanks to Java Tip #115 ( http://www.javaworld.com/javatips/jw-javatip115.html ) */
public class JMailTrustManager implements X509TrustManager
{
    public X509Certificate[] getAcceptedIssuers()
    {
	return(new X509Certificate[0]);
    }

    public void checkClientTrusted(X509Certificate[] cert, String a) throws CertificateException
    {
	checkServerTrusted(cert, a);
    }

    public void checkServerTrusted(X509Certificate[] cert, String a) throws CertificateException
    {
    }
}
