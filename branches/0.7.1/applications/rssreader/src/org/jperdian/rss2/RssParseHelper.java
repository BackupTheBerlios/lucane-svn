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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some static methods for easy access to parse additions
 * 
 * @author Christian Robert
 */

public class RssParseHelper {
  
  private static final DateFormat DF      = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
  
  /**
   * Parses the given source <code>String</code> into a <code>URL</code>. If
   * no value has been entered <code>null</code> will be returned
   * @exception RssParseException
   *   thrown if the given URL is not valid 
   */
  public static URL parseURL(String sourceString) throws RssParseException {
    if(sourceString == null || sourceString.length() < 1) {
      return null;
    } else {
      try {
        return new URL(sourceString);
      } catch(MalformedURLException e) {
        throw new RssParseException("Illegal URL found: " + sourceString);
      }
    }
  }
  
  /**
   * Parse the given node into the resulting content
   * @param info
   *   the currently available <code>RuntimeInfo</code>
   * @param node
   *   the node to be parsed
   * @return
   *   the content that has been generated during the parse process
   */
  private static String parseContent(Node node)  {

    StringBuffer buffer = new StringBuffer();
    short nodeType      = node.getNodeType();

    switch(nodeType) {

      case Node.CDATA_SECTION_NODE:
      case Node.TEXT_NODE:
        String value = node.getNodeValue();
        if(value.length() > 0) {
          buffer.append(value);
        }
        break;

      case Node.ELEMENT_NODE:
        Element element = (Element)node;

      buffer.append("<").append(element.getNodeName());

      // Add all attributes
      NamedNodeMap attributes = element.getAttributes();
      for(int i=0; i < attributes.getLength(); i++) {
        buffer.append(" ").append(attributes.item(i).getNodeName()).append("=\"");
        buffer.append(attributes.item(i).getNodeValue()).append("\"");
      }

      if(element.hasChildNodes()) {
        buffer.append(">");
        buffer.append(RssParseHelper.parseContentChildren(element));
        buffer.append("</").append(element.getNodeName()).append(">");
      } else {

        /*
         * Several browsers have two problems in interpreting correct XHTML:
         * <br></br> is interpreted as a double <br> and two breaks are inserted
         * where only one should be.
         * On the other hand a <textarea /> is interpretated as only the opening
         * tag <textarea> and everything standing after <textarea /> will be
         * interpreted as if it was inside the textarea.
         * So it has to be checked wheter the tag itself is an empty-tag
         */

        String nodeName = element.getNodeName();
        if(nodeName.equalsIgnoreCase("br") ||
           nodeName.equalsIgnoreCase("hr") ||
           nodeName.equalsIgnoreCase("input") ||
           nodeName.equalsIgnoreCase("meta") ||
           nodeName.equalsIgnoreCase("frame")
          ) {
          buffer.append(">");
        } else {
          buffer.append("></").append(element.getNodeName()).append(">");
        }
      }
    }
    return buffer.toString();
  }

  /**
   * Parse the content of the children from the specified node and return
   * it as String.
   * @param info
   *   the currently available <code>RuntimeInfo</code>
   * @param node
   *   the content which children should be parsed
   * @return
   *   the parse result content
   */
  public static String parseContentChildren(Node node)  {
    StringBuffer result = new StringBuffer();
    if(node.hasChildNodes()) {
      NodeList children = node.getChildNodes();
      for(int i=0; i < children.getLength(); i++) {
        result.append(RssParseHelper.parseContent(children.item(i)));
      }
    }
    return result.toString().trim();
  }

  /**
   * Parses the content of the given element and formats it as date, or
   * returns <code>null</code> if no content could be read
   */
  public static Date parseContentDate(Element node) throws RssParseException {
    String content    = RssParseHelper.parseContentChildren(node);
    if(content.length() < 1) {
      return null;
    } else {
      try {
        return DF.parse(content);
      } catch(ParseException e) {
//        throw new IllegalArgumentException("Illegal date: " + content);        
        return null;
      }
    }
  }
  
  /**
   * Parses the content of the given element and formats it as number, or
   * returns <code>0</code> if no content could be read
   */
  public static int parseContentInt(Element node) throws RssParseException {
    return RssParseHelper.parseContentInt(node, 0);
  }

  /**
   * Parses the content of the given element and formats it as number, or
   * returns the default value if no content could be read
   */
  public static int parseContentInt(Element node, int defaultValue) throws RssParseException {
    String content   = RssParseHelper.parseContentChildren(node);
    if(content.length() < 1) {
      return defaultValue;
    } else {
      try {
        return Integer.parseInt(content);
      } catch(NumberFormatException e) {
        throw new RssParseException("Illegal integer value found: " + content);
      }
    }
  }
  
  /**
   * Parses the content of the given element and formats it as URL
   */
  public static URL parseContentURL(Element node) throws RssParseException {
    String content   = RssParseHelper.parseContentChildren(node);
    return RssParseHelper.parseURL(content);
  }
  
}