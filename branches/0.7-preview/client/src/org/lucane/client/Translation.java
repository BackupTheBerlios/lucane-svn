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

package org.lucane.client;

import java.io.*;
import java.net.*;
import java.util.*;

import org.lucane.common.Logging;

/**
 * Handles translations of messages
 */
public class Translation
{
    private static ResourceBundle bundle;
    
    /**
     * Places the correct Locale.
     * Load the properties file
     */
    protected static void setLocale(String lang)
    {
        try
        {
            InputStream is = new URL(getDirectory() + "messages_" + lang + ".properties").openStream();
            Translation.bundle = new PropertyResourceBundle(is);
        }
        catch (Exception e1)
        {            
            try
            {                
                InputStream is = new URL(getDirectory() + "messages.properties").openStream();
                Translation.bundle = new PropertyResourceBundle(is);
            }
            catch (Exception e2)
            {
				Logging.getLogger().info("unable to set language");
                Translation.bundle = null;
            }
        }
    }
    
    /**
     * Message translaion
     *
     * @param origin string to fetch
     * @return the correct string
     */
    public static String tr(String origin)
    {        
        try {            
            return bundle.getString(origin);
        } catch (Exception e) {
            return origin;
        }
    }
    
    /**
     * Get the directory of the content inside the jar file
     *
     * @return the String containing the directory formed like an url
     */
    private static String getDirectory()
    {        
        String url = "jar:file:///"
            + System.getProperty("user.dir")
            + "/lib/lucane-client-" + Client.VERSION +".jar!/";
        
        return url.replace('\\', '/');
    }
}
