package org.lucane.client.util;

import java.awt.*;
import javax.swing.*;

import org.lucane.client.LocalConfig;

/**
 * Used to store a widget (JFrame, JScrollPane, ...) in a LocalConfig
 */
public class WidgetState
{
	/**
	 * Create a key based on the widget name
	 * 
	 * @param component the widget
	 * @param property the property to store
	 * @return the key
	 */
	private static String getKey(Component component, String property)
	{
		return "widget-" + component.getName() + "-" + property;
	}

	/**
	 * Save a window
	 * 
	 * @param config the LocalConfig
	 * @param window the Window
	 */
	public static void save(LocalConfig config, Window window)
	{
		config.set(getKey(window, "saved"), "true");
		config.set(getKey(window, "location.x"), window.getLocation().x);
		config.set(getKey(window, "location.y"), window.getLocation().y);
		config.set(getKey(window, "height"), window.getHeight());
		config.set(getKey(window, "width"), window.getWidth());
	}
	
	/**
	 * Restore a window
	 * 
	 * @param config the LocalConfig
	 * @param frame the Window
	 */	
	public static void restore(LocalConfig config, Window window)
	{
		String saved = config.get(getKey(window, "saved"));
		if(saved == null)
			return;

		int x = config.getInt(getKey(window, "location.x"));
		int y = config.getInt(getKey(window, "location.y"));
		window.setLocation(x, y);
		
		int height = config.getInt(getKey(window, "height"));
		int width = config.getInt(getKey(window, "width")); 
		window.setSize(width, height);		
	}
	
	/**
	 * Save a frame
	 * 
	 * @param config the LocalConfig
	 * @param frame the JFrame
	 */	
	public static void save(LocalConfig config, JFrame frame)
	{
		save(config, (Window)frame);
		config.set(getKey(frame, "extended"), frame.getExtendedState());
	}
	
	/**
	 * Restore a frame
	 * 
	 * @param config the LocalConfig
	 * @param window the JFrame
	 */		
	public static void restore(LocalConfig config, JFrame frame)
	{
		String saved = config.get(getKey(frame, "saved"));
		if(saved == null)
			return;
		
		restore(config, (Window)frame);
		frame.setExtendedState(config.getInt(getKey(frame, "extended")));
	}
	
	/**
	 * Save a slider
	 * 
	 * @param config the LocalConfig
	 * @param window the JFrame
	 */	
	public static void save(LocalConfig config, JSlider slider)
	{
		config.set(getKey(slider, "saved"), "true");		
		config.set(getKey(slider, "value"), slider.getValue());
	}
	
	/**
	 * Restore a slider
	 * 
	 * @param config the LocalConfig
	 * @param slider the JSlider
	 */		
	public static void restore(LocalConfig config, JSlider slider)
	{
		String saved = config.get(getKey(slider, "saved"));
		if(saved == null)
			return;
		
		slider.setValue(config.getInt(getKey(slider, "value")));
	}
	
	/**
	 * Save a JSplitPane
	 * 
	 * @param config the LocalConfig
	 * @param split the JSplitPane
	 */	
	public static void save(LocalConfig config, JSplitPane split)
	{
		config.set(getKey(split, "saved"), "true");		
		config.set(getKey(split, "dividerLocation"), split.getDividerLocation());
	}
	
	/**
	 * Restore a JSplitPane
	 * 
	 * @param config the LocalConfig
	 * @param split the JSplitPane
	 */		
	public static void restore(LocalConfig config, JSplitPane split)
	{
		String saved = config.get(getKey(split, "saved"));
		if(saved == null)
			return;
		
		split.setDividerLocation(config.getInt(getKey(split, "dividerLocation")));
	}	
}