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
package org.lucane.applications.helpbrowser;

import java.io.*;
import java.net.URL;
import javax.swing.tree.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


public class HelpParser
{

  Document document;
  String directory;

  public HelpParser(String directory, String filename)
  {
    this.directory = directory;

    try
    {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      InputStream is = new URL(directory + filename).openStream();
      this.document = builder.parse(is);
      is.close();
    }
    catch(Exception e)
    {
      this.document = null;
    }
  }


  public TreeNode getSections()
  {
    if(this.document == null)
      return null;

    DefaultMutableTreeNode result = new DefaultMutableTreeNode();
    Node root = this.document.getFirstChild();

    while(root != null && root.getNodeType() != Node.ELEMENT_NODE)
      root = root.getNextSibling();

    getSections(root, result);
    return result;
  }


  public String htmlForSection(TreePath path)
  {
    if(this.document == null)
      return null;

    String self = (String)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
    String html = "<html>\n<head><title>";
    html += self;
    html += "</title></head>\n<body>\n";

    int nbelems = path.getPathCount();

    for(int i = 0; i < nbelems; i++)
    {
      String section = (String)((DefaultMutableTreeNode)path.getPathComponent( i)).getUserObject();

      if(section != null)
        html += "&gt; <a href=\"#section:" + section + "\">" + section + "</a>";
    }

    html += "<hr>\n";
    html += htmlize(self);
    html += "<p><hr>\n";

    for(int i = 0; i < nbelems; i++)
    {
      String section = (String)((DefaultMutableTreeNode)path.getPathComponent(i)).getUserObject();

      if(section != null)
        html += "&gt; <a href=\"#section:" + section + "\">" + section + "</a>";
    }

    html += "</body>\n</html>";
    return html;
  }


  private void getSections(Node root, DefaultMutableTreeNode result)
  {
    Node section = root.getFirstChild();
    DefaultMutableTreeNode current = null;
    String name = null;

    while(section != null)
    {
      if(section.getNodeType() == Node.ELEMENT_NODE && 
         section.getNodeName().equals("section"))
      {
        NamedNodeMap nnm = section.getAttributes();
        name = nnm.getNamedItem("name").getNodeValue();
        current = new DefaultMutableTreeNode(name);
        result.add(current);
        getSections(section, current);
      }

      section = section.getNextSibling();
    }
  }


  private Node getSectionNode(String wanted, Node root)
  {
    Node result = null;
    Node section = root.getFirstChild();
    String name = null;

    while(section != null && result == null)
    {
      if(section.getNodeType() == Node.ELEMENT_NODE && 
         section.getNodeName().equals("section"))
      {
        NamedNodeMap nnm = section.getAttributes();
        name = nnm.getNamedItem("name").getNodeValue();

        if(name.equals(wanted))
          result = section;
        else
          result = getSectionNode(wanted, section);
      }

      section = section.getNextSibling();
    }

    return result;
  }


  private String htmlize(String section)
  {
    String html = "<h1>" + section + "</h1>\n";
    Node root = getSectionNode(section, document.getFirstChild());
    html += htmlize(root);
    return html;
  }

  private String htmlize(Node root)
  {
    String html = "";
    String text = "";
    Node current = root.getFirstChild();

    while(current != null)
    {
      if(current.getNodeType() == Node.TEXT_NODE)
      {
        text = current.getNodeValue();

        for(int i = 0; i < text.length(); i++)
        {
          if(text.charAt(i) == '\n')
            html += "<br>";
          else
            html += text.charAt(i);
        }
      }
      else if(current.getNodeType() == Node.ELEMENT_NODE)
      {
        try
        {
          if(current.getNodeName().equals("section"))
          {
            NamedNodeMap nnm = current.getAttributes();
            text = nnm.getNamedItem("name").getNodeValue();
            html += "&gt; <a href=\"#section:" + text + "\">" + text + "</a>";
          }
          else if(current.getNodeName().equals("info"))
          {
            NamedNodeMap nnm = current.getAttributes();
            text = nnm.getNamedItem("text").getNodeValue();
            html += "<a href=\"#tooltip:" + text + "\">";
            html += htmlize(current);
            html += "</a>";
          }
          else if(current.getNodeName().equals("colored"))
          {
            NamedNodeMap nnm = current.getAttributes();
            text = nnm.getNamedItem("color").getNodeValue();
            html += "<font color=\"" + text + "\">";
            html += htmlize(current);
            html += "</font>";
          }
          else if(current.getNodeName().equals("b"))
          {
            html += "<b>" + htmlize(current) + "</b>";
          }
          else if(current.getNodeName().equals("tt"))
          {
            html += "<tt>" + htmlize(current) + "</tt>";
          }
          else if(current.getNodeName().equals("i"))
          {
            html += "<i>" + htmlize(current) + "</i>";
          }
          else if(current.getNodeName().equals("table"))
          {
            String border = "1";

            try
            {
              NamedNodeMap nnm = current.getAttributes();
              text = nnm.getNamedItem("border").getNodeValue();

              if(text.equals("no"))
                border = "0";
            }
            catch(Exception e)
            {
              //default border
            }

            html += "<table border=\"" + border + "\">";
            html += htmlize(current);
            html += "</table>";
          }
          else if(current.getNodeName().equals("tr"))
          {
            html += "<tr>" + htmlize(current) + "</tr>";
          }
          else if(current.getNodeName().equals("td"))
          {
            html += "<td>" + htmlize(current) + "</td>";
          }
          else if(current.getNodeName().equals("image"))
          {
            NamedNodeMap nnm = current.getAttributes();
            text = nnm.getNamedItem("file").getNodeValue();
            html += "<img src=\"" + this.directory + text + "\">";
            html += "</img>";
          }
        }
        catch(Exception e)
        {
          //...
        }
      }

      current = current.getNextSibling();
    }

    return html;
  }
}