/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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
package org.lucane.applications.jmail;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

public class SplashFrame extends JFrame
{
	public SplashFrame(JMailPlugin plugin)
	{
		ImageIcon ii = null;
		try {
			ii = new ImageIcon(new URL(plugin.getDirectory() + plugin.getIcon()));
		} catch(Exception e) {
			ii = new ImageIcon();
		}
		
		JLabel message = new JLabel(plugin.tr("splash.waitMessage"));
		message.setOpaque(true);
		message.setBackground(Color.WHITE);
		message.setHorizontalAlignment(JLabel.CENTER);
		message.setIcon(ii);
		message.setIconTextGap(20);
		
		
				
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(message, BorderLayout.CENTER);
		getContentPane().add(bar, BorderLayout.SOUTH);
		
		setSize(400, 150);
		this.rootPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width-getWidth())/2, (d.height-getHeight())/2);
		this.setUndecorated(true);
	}
}