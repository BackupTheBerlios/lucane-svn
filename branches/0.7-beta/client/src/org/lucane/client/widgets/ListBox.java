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
package org.lucane.client.widgets;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

import org.lucane.client.Client;


/**
 * A ListBox dialog to select an element in a list
 */
public class ListBox  extends JDialog
  implements ActionListener, MouseListener
{
  private Vector list;
  private JButton btOK;
  private JButton btCancel;
  private JList lstSelection;
  private JPanel pnlButton;
  private JPanel pnlEast;
  private JScrollPane pnlScroll;
  private boolean accept;

  /**
   * Creates a new ListBox object.
   * 
   * @param owner the base JFrame (can be null)
   * @param title the title of the dialog box
   * @param message th emessage of the dialog box
   * @param list a Vector of items
   */
  public ListBox(JFrame owner, String title, String message, Vector list)
  {
    super(owner, title, true);
    this.list = list;
    btOK = new JButton("OK", Client.getIcon("ok.png"));
    btCancel = new JButton("Cancel", Client.getIcon("cancel.png"));
    lstSelection = new JList(list);
    pnlEast = new JPanel(new BorderLayout());
    pnlButton = new JPanel(new GridLayout(2, 0));
    pnlScroll = new JScrollPane(lstSelection);

    pnlButton.add(btOK);
    pnlButton.add(btCancel);
    
    pnlEast.add(pnlButton, BorderLayout.NORTH);
    
    btOK.addActionListener(this);
    btCancel.addActionListener(this);
    lstSelection.addMouseListener(this);
    
    this.setSize(400, 200);
    this.getContentPane().setLayout(new BorderLayout(3, 3));
    this.getContentPane().add(new JLabel(message), BorderLayout.NORTH);
    this.getContentPane().add(pnlScroll, BorderLayout.CENTER);
    this.getContentPane().add(pnlEast, BorderLayout.EAST);
  }

  /**
   * Get the selected item index
   * 
   * @return the selected index
   */
  public int selectItemByIndex()
  {
    if(list.size() == 0)
      return -1;

    lstSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    
    this.show();

    if(accept)
      return lstSelection.getSelectedIndex();
    else
      return -1;
  }

  /**
   * Get the selected items
   * 
   * @return the selected items
   */
  public Object[] selectItems()
  {
  	if(list.size() == 0)
  		return new Object[0];

  	lstSelection.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  	this.show();

  	if(accept)
  		return lstSelection.getSelectedValues();
  	else
  		return null;  	
  }

  /**
   * A button has been clicked (oh my god!)
   * 
   * @param ev yet another event...
   */
  public void actionPerformed(ActionEvent ev)
  {
    if(ev.getSource() == btOK)
      accept = true;
    else
      accept = false;

    this.dispose();
  }
  
  //-- mouse listener

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) 
	{
		if(e.getClickCount() > 1 && lstSelection.getSelectedIndex() >= 0)
		{
			accept = true;
			this.dispose();
		}
	}
}