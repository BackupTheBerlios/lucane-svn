/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.applications.sendmail;

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

public class MailClient extends StandalonePlugin implements ActionListener
{
	/* dialog components */
	private JFrame frame;
	private JTextField from;
	private JTextField to;
	private JTextField cc;
	private JTextField bcc;
	private JTextField subject;
	private JTextField content;
	private JTextField attachName;
	private JTextField attachData;
	private JButton btnSend;

	private ConnectInfo sendmail;

	public MailClient()
	{
		this.starter = true;
	}

	public Plugin init(ConnectInfo[] friends, boolean starter)
	{
		return new MailClient();
	}

	public void start()
	{
		this.sendmail = Communicator.getInstance().getConnectInfo("org.lucane.applications.sendmail");
		
		frame = new JFrame(getTitle());
		frame.addWindowListener(this);
		frame.getContentPane().setLayout(new GridLayout(0, 2));
		
		from = new JTextField();
		to = new JTextField();
		cc = new JTextField();
		bcc = new JTextField();
		subject = new JTextField();
		content = new JTextField();
		attachName = new JTextField();
		attachData = new JTextField();
		btnSend = new JButton(tr("send"));
		btnSend.addActionListener(this);
		
		frame.getContentPane().add(new JLabel(tr("from")));
		frame.getContentPane().add(from);
		frame.getContentPane().add(new JLabel(tr("to")));
		frame.getContentPane().add(to);
		frame.getContentPane().add(new JLabel(tr("cc")));
		frame.getContentPane().add(cc);
		frame.getContentPane().add(new JLabel(tr("bcc")));
		frame.getContentPane().add(bcc);
		frame.getContentPane().add(new JLabel(tr("subject")));
		frame.getContentPane().add(subject);
		frame.getContentPane().add(new JLabel(tr("content")));
		frame.getContentPane().add(content);
		frame.getContentPane().add(new JLabel(tr("attach.name")));
		frame.getContentPane().add(attachName);
		frame.getContentPane().add(new JLabel(tr("attach.data")));
		frame.getContentPane().add(attachData);
		frame.getContentPane().add(new JLabel(""));
		frame.getContentPane().add(btnSend);
		frame.pack();
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae)
	{
		HashMap map = new HashMap();
		map.put("from", from.getText());
		map.put("to", to.getText());
		map.put("cc", cc.getText());
		map.put("bcc", bcc.getText());
		map.put("subject", subject.getText());
		map.put("content", content.getText());
		
        if(attachName.getText().length() > 0 || attachData.getText().length() > 0)
        {
		  HashMap attach = new HashMap();
  		  attach.put(attachName.getText(), attachData.getText());
  		  map.put("attach", attach);
        }
		
		try
		{			
			ObjectConnection oc = Communicator.getInstance().sendMessageTo(sendmail, "org.lucane.applications.sendmail", map);
			String response = oc.readString();
			oc.close();
			
			if (response.startsWith("FAILED"))
				DialogBox.error(tr("failed") + response.substring(7));
                        else
                        {
                          DialogBox.info(tr("success"));
                          frame.dispose();
                          exit();
                        }
		}
		catch (Exception e)
		{
			DialogBox.error(tr("error") + e);
		}
	}
}
