/**
 * RSS framework and reader
 * Copyright (C) 2004 Christian Robert
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

package org.jperdian.rss2;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

/**
 * Several small helpers
 * 
 * @author Christian Robert
 */

public class RssHelper {

  private static final NumberFormat DECIMAL_FORMAT          = new DecimalFormat("#,##0");
  private static final NumberFormat FLOAT_FORMAT            = new DecimalFormat("#,##0.0");
  private static final Component DUMMY_COMP                 = new JLabel();
  private static final String TEXT_START                    = "org/jperdian/rss2/res/texts/";
  private static final String RESOURCE_START                = "org/jperdian/rss2/res/images/";
  
  /**
   * Creates an image icon for the given name. The image for this icon 
   * must be located in the resource path 
   * <code>"org/jperdian/rss2/res/images"</code> 
   */
  public static String getResourceText(String imageName) {
    ClassLoader loader      = RssHelper.class.getClassLoader();
    URL resourceURL         = loader.getResource(TEXT_START + imageName);
    if(resourceURL == null) {
      throw new IllegalArgumentException("Cannot find text resource: " + TEXT_START + imageName);
    } else {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream()));
        StringBuffer buffer   = new StringBuffer();
        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
          buffer.append(line).append('\n');
        }
        return buffer.toString();
      } catch(IOException e) {
        return "";
      }
    }
  }
  
  /**
   * Creates a text for the given name. The text must be located in the 
   * resource path <code>"braintags/elacarte/imageassist/res/texts"</code> 
   */
  public static Icon getResourceImageIcon(String imageName) {
    ClassLoader loader  = RssHelper.class.getClassLoader();
    URL resourceURL     = loader.getResource(RESOURCE_START + imageName);
    if(resourceURL == null) {
      throw new IllegalArgumentException("Cannot find image resource: " + RESOURCE_START + imageName);
    } else {
      return new ImageIcon(resourceURL);
    }
  }
  
  /**
   * Creates an image icon for the given name. The image for this icon 
   * must be located in the resource path 
   * <code>"org/jperdian/rss2/res/images"</code> 
   */
  public static Image getResourceImage(String imageName) {
    ClassLoader loader        = RssHelper.class.getClassLoader();
    URL resourceURL           = loader.getResource(RESOURCE_START + imageName);
    if(resourceURL == null) {
      throw new IllegalArgumentException("Cannot find image resource: " + RESOURCE_START + imageName);
    } else {
      Image img               = Toolkit.getDefaultToolkit().createImage(resourceURL);
      MediaTracker imgTracker = new MediaTracker(DUMMY_COMP);
      imgTracker.addImage(img, 0);
      try {
        imgTracker.waitForAll();
      } catch(Exception e) {
        // Ignore here
      }
      return img;
    }
  }
  
  /**
   * Gets the image from the given File
   */
  public static Image getImage(File file) throws IOException {
    Image image           = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
    return RssHelper.waitFor(image);
  }
  
  public static Image scaleImageProportional(Image sourceImage, Dimension maxDimension, boolean highQuality) {
    
    double targetWidth    = maxDimension.width;
    double targetHeight   = maxDimension.height;
    double currentWidth   = sourceImage.getWidth(DUMMY_COMP);
    double currentHeight  = sourceImage.getHeight(DUMMY_COMP);
    if(currentWidth > targetWidth || currentHeight > targetHeight) {
      
      double scaleX       = 1 / currentWidth * targetWidth;
      double scaleY       = 1 / currentHeight * targetHeight;
      double scale        = Math.min(scaleX, scaleY);
      int newWidth        = (int)(currentWidth * scale);
      int newHeight       = (int)(currentHeight * scale);
      Image newImage      = sourceImage.getScaledInstance(newWidth, newHeight, highQuality ? Image.SCALE_SMOOTH : Image.SCALE_FAST);
      return RssHelper.waitFor(newImage);
      
    } else {
      return sourceImage;
    }
  }
  
  /**
   * Wait until the image has been loaded completely
   */
  public static Image waitFor(Image image) {
    try {
      MediaTracker tracker    = new MediaTracker(DUMMY_COMP);
      tracker.addImage(image, 0);
      tracker.waitForAll();
    } catch(InterruptedException e) {
      // Do nothing
    }
    return image;
  }
  
  /**
   * Computes the title for the given file size
   */
  public static String computeFileSizeString(long bytes) {
    if(bytes < 1024) {
      return DECIMAL_FORMAT.format(bytes) + " B"; 
    } else if(bytes < 1024 * 1024) {
      return DECIMAL_FORMAT.format(bytes / 1024) + " KB";
    } else {
      return FLOAT_FORMAT.format(bytes / (1024 * 1024)) + " MB";
    }
  }
  
  /**
   * Formats the given time span
   */
  public static String computeTimeString(long time) {
    if(time < 1000) {
      return "0sec";
    } else if(time < 1000 * 60) {
      return (time/1000) + "sec";
    } else {
      long minutes  = time / (1000 * 60);
      long seconds  = (time / 1000) % 60;
      return minutes + "min" + seconds + "sec"; 
    } 
  }
  
  /**
   * Creates a <code>JMenuItem</code>
   * @param action
   *   the action to be performed
   * @param icon
   *   the icon to be used
   */
  public static JMenuItem createMenuItem(Action action, Icon icon) {
    JMenuItem item    = new JMenuItem(action);
    if(icon != null) {
      item.setIcon(icon);
    }
    return item;
  }
  
  /**
   * Creates a <code>JButton</code>
   * @param action
   *   the action to be performed
   * @param icon
   *   the icon to be used
   */
  public static JButton createButton(char mnemonic, Action action, Icon icon) {
    JButton button    = new JButton(action);
    if(icon != null) {
      button.setIcon(icon);
    }
    button.setMnemonic(mnemonic);
    return button;
  }
  
}