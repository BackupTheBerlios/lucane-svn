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
	private ManagedWindow window;
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

	public Plugin newInstance(ConnectInfo[] friends, boolean starter)
	{
		return new MailClient();
	}

	public void start()
	{
		this.sendmail = Communicator.getInstance().getConnectInfo("org.lucane.applications.sendmail");
		
		window = new ManagedWindow(this, getTitle());
		window.setExitPluginOnClose(true);
		window.getContentPane().setLayout(new GridLayout(0, 2));
		
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
		
		window.getContentPane().add(new JLabel(tr("from")));
		window.getContentPane().add(from);
		window.getContentPane().add(new JLabel(tr("to")));
		window.getContentPane().add(to);
		window.getContentPane().add(new JLabel(tr("cc")));
		window.getContentPane().add(cc);
		window.getContentPane().add(new JLabel(tr("bcc")));
		window.getContentPane().add(bcc);
		window.getContentPane().add(new JLabel(tr("subject")));
		window.getContentPane().add(subject);
		window.getContentPane().add(new JLabel(tr("content")));
		window.getContentPane().add(content);
		window.getContentPane().add(new JLabel(tr("attach.name")));
		window.getContentPane().add(attachName);
		window.getContentPane().add(new JLabel(tr("attach.data")));
		window.getContentPane().add(attachData);
		window.getContentPane().add(new JLabel(""));
		window.getContentPane().add(btnSend);
		window.show();
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
                          window.dispose();
                          exit();
                        }
		}
		catch (Exception e)
		{
			DialogBox.error(tr("error") + e);
		}
	}
}
