package org.lucane.applications.quicklaunch;
import javax.swing.*;
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

}