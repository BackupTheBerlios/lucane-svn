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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jperdian.rss2.dom.RssChannel;
import org.jperdian.rss2.dom.RssCloud;
import org.jperdian.rss2.dom.RssConstants;
import org.jperdian.rss2.dom.RssEnclosure;
import org.jperdian.rss2.dom.RssGuid;
import org.jperdian.rss2.dom.RssImage;
import org.jperdian.rss2.dom.RssItem;
import org.jperdian.rss2.dom.RssTextInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The parser to process an XML document and transfer it into an
 * <code>RssMessage</code>
 * 
 * @author Christian Robert
 */

public class RssParser {

  private DocumentBuilder myDocumentBuilder   = null;
  
  public RssParser() {
    try {
      DocumentBuilder builder   = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      this.setDocumentBuilder(builder);
    } catch(ParserConfigurationException e) {
      throw new RuntimeException("Cannot create DocumentBuilder", e);
    }
  }
  
  /**
   * Transfers the given XML document into a value <code>RssMessage</code>
   * object
   */
  public RssChannel parse(URL sourceURL, RssChannel targetChannel) throws RssException {
    try {
      InputStream inStream    = new BufferedInputStream(sourceURL.openStream());
      Document document       = this.getDocumentBuilder().parse(inStream);
      inStream.close();
      return this.parse(document, targetChannel);
    } catch(SAXException e) {
      throw new RssParseException("Illegal XML format \n[" + e.getMessage() + "]", e);
    } catch(IOException e) {
      throw new RssException("Cannot connect to source URL: " + sourceURL, e);
    }
  }
  
  /**
   * Transfers the given XML document into a value <code>RssMessage</code>
   * object
   */
  public RssChannel parse(Document xmlDocument, RssChannel targetChannel) throws RssParseException {
    RssChannel resultChannel  = null;
    Element rootElement       = xmlDocument.getDocumentElement();
    NodeList rootSubNodes     = rootElement.getChildNodes();
    for(int i=0; i < rootSubNodes.getLength(); i++) {
      Node subNode            = rootSubNodes.item(i);
      String subNodeName      = subNode.getNodeName();
      if(subNode.getNodeType() == Node.ELEMENT_NODE && subNodeName.equalsIgnoreCase("channel")) {
        resultChannel         = this.parseChannel((Element)rootSubNodes.item(i), targetChannel);
      } else if(subNodeName.equalsIgnoreCase("item")) {
        RssItem item          = this.parseItem((Element)subNode);
        item.setSource(resultChannel);
        resultChannel.addItem(item);
      }
    }
    if(resultChannel != null) {
      return resultChannel;
    } else {
      throw new RssParseException("No channel element found in message");
    }
  }

  /**
   * Parses the content of the given <code>channel</code> element, analyze
   * it's content and generate a valid <code>RssChannel</code> object
   */
  protected RssChannel parseChannel(Element channelElement, RssChannel channel) throws RssParseException {
    List itemList           = channel.getItemList();
    if(itemList != null && itemList.size() > 0) {
      itemList.clear();
    }
    NodeList subNodes       = channelElement.getChildNodes();
    for(int i=0; i < subNodes.getLength(); i++) {
      if(subNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element subElement  = (Element)subNodes.item(i);
        String elemName     = subElement.getNodeName();
        if(elemName.equalsIgnoreCase("title")) {
          channel.setTitle(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("link")) {
          channel.setLink(RssParseHelper.parseContentURL(subElement));
        } else if(elemName.equalsIgnoreCase("description")) {
          channel.setDescription(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("copyright")) {
          channel.setCopyright(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("managingEditor")) {
          channel.setManagingEditor(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("webMaster")) {
          channel.setWebmaster(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("pubDate")) {
          channel.setPubDate(RssParseHelper.parseContentDate(subElement));
        } else if(elemName.equalsIgnoreCase("lastBuildDate")) {
          channel.setLastBuildDate(RssParseHelper.parseContentDate(subElement));
        } else if(elemName.equalsIgnoreCase("category")) {
          channel.addCategory(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("generator")) {
          channel.setGenerator(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("docs")) {
          channel.setDocs(RssParseHelper.parseContentURL(subElement));
        } else if(elemName.equalsIgnoreCase("cloud")) {
          channel.setCloud(this.parseCloud(subElement));
        } else if(elemName.equalsIgnoreCase("ttl")) {
          channel.setTtl(RssParseHelper.parseContentInt(subElement));
        } else if(elemName.equalsIgnoreCase("image")) {
          channel.setImage(this.parseImage(subElement));
        } else if(elemName.equalsIgnoreCase("rating")) {
          channel.setRating(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("textInput")) {
          channel.setTextInput(this.parseTextInput(subElement));
        } else if(elemName.equalsIgnoreCase("skipHours")) {
          channel.addSkipHour(RssParseHelper.parseContentInt(subElement));
        } else if(elemName.equalsIgnoreCase("skipDays")) {
          channel.addSkipDay(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("item")) {
          RssItem item    = this.parseItem(subElement);
          item.setSource(channel);
          channel.addItem(item);
        }
      }
    }
    return channel;
  }
  
  /**
   * Parses a <tt>cloud</tt> element
   */
  protected RssCloud parseCloud(Element cloudElement) throws RssParseException {
    RssCloud cloud    = new RssCloud();
    cloud.setDomain(cloudElement.getAttribute("domain"));
    try {
      cloud.setPort(Integer.parseInt(cloudElement.getAttribute("port")));
    } catch(NumberFormatException e) {
      throw new RssParseException("Illegal port entered for cloud: " + cloudElement.getAttribute("port"));
    }
    cloud.setPath(cloudElement.getAttribute("path"));
    cloud.setRegisterProcedure(cloudElement.getAttribute("registerProcedure"));
    return cloud;
  }
  
  /**
   * Parses a <tt>textInput</tt> element
   */
  protected RssTextInput parseTextInput(Element textInputElement) throws RssParseException {
    RssTextInput textInput  = new RssTextInput();
    NodeList subNodes       = textInputElement.getChildNodes();
    for(int i=0; i < subNodes.getLength(); i++) {
      if(subNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element subElement  = (Element)subNodes.item(i);
        String elemName     = subElement.getNodeName();
        if(elemName.equalsIgnoreCase("title")) {
          textInput.setTitle(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("description")) {
          textInput.setDescription(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("name")) {
          textInput.setName(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("link")) {
          textInput.setLink(RssParseHelper.parseContentURL(subElement));
        }
      }
    }
    return textInput;
  }
  
  /**
   * Parses a <tt>image</tt> element
   */
  protected RssImage parseImage(Element textInputElement) throws RssParseException {
    RssImage image  = new RssImage();
    NodeList subNodes       = textInputElement.getChildNodes();
    for(int i=0; i < subNodes.getLength(); i++) {
      if(subNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element subElement  = (Element)subNodes.item(i);
        String elemName     = subElement.getNodeName();
        if(elemName.equalsIgnoreCase("title")) {
          image.setTitle(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("url")) {
          image.setURL(RssParseHelper.parseContentURL(subElement));
        } else if(elemName.equalsIgnoreCase("title")) {
          image.setTitle(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("link")) {
          image.setLink(RssParseHelper.parseContentURL(subElement));
        } else if(elemName.equalsIgnoreCase("description")) {
          image.setDescription(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("width")) {
          image.setWidth(RssParseHelper.parseContentInt(subElement, RssConstants.DEFAULT_IMAGE_WIDTH));
        } else if(elemName.equalsIgnoreCase("height")) {
          image.setHeight(RssParseHelper.parseContentInt(subElement, RssConstants.DEFAULT_IMAGE_HEIGHT));
        }
      }
    }
    return image;
  }

  /**
   * Parses a <tt>item</tt> element
   */
  protected RssItem parseItem(Element itemElement) throws RssParseException {
    RssItem item    = new RssItem();
    NodeList subNodes       = itemElement.getChildNodes();
    for(int i=0; i < subNodes.getLength(); i++) {
      if(subNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element subElement  = (Element)subNodes.item(i);
        String elemName     = subElement.getNodeName();
        if(elemName.equalsIgnoreCase("title")) {
          item.setTitle(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("link")) {
          item.setLink(RssParseHelper.parseContentURL(subElement));
        } else if(elemName.equalsIgnoreCase("description")) {
          item.setDescription(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("author")) {
          item.setAuthor(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("category")) {
          item.addCategory(RssParseHelper.parseContentChildren(subElement));
        } else if(elemName.equalsIgnoreCase("comments")) {
          item.setComments(RssParseHelper.parseContentURL(subElement));
        } else if(elemName.equalsIgnoreCase("enclosure")) {
          item.setEnclosure(this.parseEnclosure(subElement));
        } else if(elemName.equalsIgnoreCase("guid")) {
          item.setGuid(this.parseGuid(subElement));
        } else if(elemName.equalsIgnoreCase("pubDate")) {
          item.setPubDate(RssParseHelper.parseContentDate(subElement));
        }
      }
    }
    return item;
  }

  /**
   * Parses a <tt>enclosure</tt> element
   */
  protected RssEnclosure parseEnclosure(Element enclosureElement) throws RssParseException {
    RssEnclosure enclosure    = new RssEnclosure();
    enclosure.setURL(RssParseHelper.parseURL(enclosureElement.getAttribute("url")));
    try {
      enclosure.setLength(Long.parseLong(enclosureElement.getAttribute("length")));
    } catch(NumberFormatException e) {
      throw new RssParseException("Illegal length entered for enclosure: " + enclosureElement.getAttribute("length"));
    }
    enclosure.setType(enclosureElement.getAttribute("type"));
    return enclosure;
  }
  
  /**
   * Parses a <tt>guid</tt> element
   */
  protected RssGuid parseGuid(Element guidElement) throws RssParseException {
    RssGuid guid      = new RssGuid();
    String permaLink  = guidElement.getAttribute("isPermaLink");
    if(permaLink != null) {
      guid.setIsPermaLink(permaLink.equalsIgnoreCase("true"));
    }
    guid.setGuid(RssParseHelper.parseContentChildren(guidElement));
    return guid;
  }
  
  
  // --------------------------------------------------------------------------
  // --  Property access methods  ---------------------------------------------
  // --------------------------------------------------------------------------
  
  /**
   * Sets the <code>DocumentBuilder</code> used for XML parsing
   */
  protected void setDocumentBuilder(DocumentBuilder builder) {
    this.myDocumentBuilder = builder;
  }
  
  /**
   * Gets the <code>DocumentBuilder</code> used for XML parsing
   */
  protected DocumentBuilder getDocumentBuilder() {
    return this.myDocumentBuilder;
  }
  
}