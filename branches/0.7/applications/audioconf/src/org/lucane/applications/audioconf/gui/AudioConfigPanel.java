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
package org.lucane.applications.audioconf.gui;

import java.awt.*;
import javax.swing.*;

import org.lucane.applications.audioconf.*;
import org.lucane.applications.audioconf.audio.AudioConfig;

public class AudioConfigPanel extends JPanel
{
	private AudioConf plugin;
	
	private JComboBox mode;
	private JSlider quality;
	
	public AudioConfigPanel(AudioConf plugin)
	{
		super(new GridBagLayout());		
		
		this.plugin = plugin;
		
		this.initWidgets();
		this.initLayout();		
		this.setBorder(BorderFactory.createTitledBorder(tr("audio.config")));		
	}
	
	private void initWidgets()
	{
		Object[] modes = {tr("mode.narrowband"), tr("mode.wideband"), tr("mode.ultrawideband")};
		this.mode = new JComboBox(modes);
		this.quality = new JSlider(0, 10, 3);
		this.quality.setMajorTickSpacing(10);
		this.quality.setMinorTickSpacing(1);
		this.quality.setPaintTicks(true);
		this.quality.setPaintLabels(true);	
		this.quality.setSnapToTicks(true);
	}
	
	private void initLayout()
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.3;
		gbc.gridy = 0;
		this.add(new JLabel(tr("label.mode")), gbc);
		gbc.gridy = 1;
		this.add(new JLabel(tr("label.quality")), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.7;
		gbc.gridx = 1;
		gbc.gridy = 0;
		this.add(mode, gbc);
		gbc.gridy = 1;
		this.add(quality, gbc);
	}
	
	public AudioConfig getAudioConfig()
	{
		return new AudioConfig(this.mode.getSelectedIndex()+1, this.quality.getValue());
	}
	
	private String tr(String s)
	{
		return plugin.tr(s);
	}
}