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

package org.lucane.client.util;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.UIManager;

import org.lucane.client.Client;
import org.lucane.common.Logging;

/**
 * Handles translations of messages
 */
public class Translation
{
    private static ResourceBundle bundle;
    private static ResourceBundle defaultBundle;
    
    /**
     * Places the correct Locale.
     * Load the properties file
     */
    public static void setLocale(String lang)
    {
    	Locale.setDefault(new Locale(lang));
		try {
			InputStream is = new URL(getDirectory() + "messages.properties").openStream();
			Translation.bundle = new PropertyResourceBundle(is);
			Translation.defaultBundle = Translation.bundle;
		} catch(Exception e) {
			Translation.bundle = null;
			Translation.defaultBundle = null;
		}		
		
		try {
			InputStream is = new URL(getDirectory() + "messages_" + lang +  ".properties").openStream();
			Translation.bundle = new PropertyResourceBundle(is);
		} catch(Exception e) {
			if(Translation.bundle == null)
				Logging.getLogger().info("unable to set language");
		}
		
        try {
	        if(Translation.bundle != null)
    	    	Translation.changeUIMessages();            
		} catch(UnsatisfiedLinkError ule) {
			//awt is not available
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
		} catch(Exception e) {    
			try {
				return defaultBundle.getString(origin);
			} catch(Exception e2) {    
				return origin;
			}
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
    
    /**
     * Change the messages for Swing objects
     */
    private static void changeUIMessages()
    throws UnsatisfiedLinkError
	{
    	String[] messages = {
    			"FileChooser.acceptAllFileFilterText",
				"FileChooser.cancelButtonText",
				"FileChooser.cancelButtonToolTipText",
				"FileChooser.detailsViewButtonToolTipText",
				"FileChooser.directoryDescriptionText",
				"FileChooser.fileDescriptionText",
				"FileChooser.fileNameLabelText",
				"FileChooser.filesOfTypeLabelText",
				"FileChooser.helpButtonText",
				"FileChooser.helpButtonToolTipText",
				"FileChooser.homeFolderToolTipText",
				"FileChooser.listViewButtonToolTipText",
				"FileChooser.lookInLabelText",
				"FileChooser.newFolderErrorText",
				"FileChooser.newFolderToolTipText",
				"FileChooser.openButtonText",
				"FileChooser.openButtonToolTipText",
				"FileChooser.saveButtonText",
				"FileChooser.saveButtonToolTipText",
				"FileChooser.updateButtonText",
				"FileChooser.updateButtonToolTipText",
    	"FileChooser.upFolderToolTipText"};
    	
    	for(int i=0;i<messages.length;i++)
    		UIManager.put(messages[i], Translation.tr(messages[i]));
    }    
}
