package org.lucane.applications.quicklaunch;
import java.awt.event.*;

import javax.swing.*;

import org.lucane.client.widgets.DialogBox;

import com.jeans.trayicon.*;

public class TrayIcon
{	
	private WindowsTrayIcon icon;
	private SwingTrayPopup popup;
	
	public TrayIcon(ImageIcon icon, String tooltip)
	throws Exception
	{
		WindowsTrayIcon.initTrayIcon("lucane-client");
		
		int height = icon.getIconHeight();	
		int width = icon.getIconWidth();
		
		this.icon = new WindowsTrayIcon(icon.getImage(), height, width);
		this.icon.setToolTipText(tooltip);
				
		popup = new SwingTrayPopup();
		popup.setTrayIcon(this.icon);	
	}
	
	public void addMouseListener(MouseListener ml)
	{
		this.icon.addMouseListener(ml);
	}
	
	public void add(JMenuItem item)
	{
		this.popup.add(item);
	}
	
	public void addSeparator()
	{
		this.popup.addSeparator();
	}
	
	public void setVisible(boolean visible)
	{
		this.icon.setVisible(visible);
	}

	private void showBalloon(String message, String title, int timeout, int flags)
	throws TrayIconException
	{
		this.icon.showBalloon(message, title, timeout, flags);
	}
	
	public void showInfo(String message, String title)
	{
		try {
			showBalloon(message, title, 5000, WindowsTrayIcon.BALLOON_INFO);
		} catch (TrayIconException e) {
			DialogBox.info(message);
		}
	}
	
	public void showWarning(String message, String title)
	{
		try {
			showBalloon(message, title, 5000, WindowsTrayIcon.BALLOON_WARNING);
		} catch (TrayIconException e) {
			DialogBox.error(message);
		}
	}
	
	public void showError(String message, String title)
	{
		try {
			showBalloon(message, title, 10000, WindowsTrayIcon.BALLOON_ERROR);
		} catch (TrayIconException e) {
			DialogBox.error(message);
		}
	}
}