package org.lucane.applications.sqlnavigator;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import org.lucane.client.widgets.ManagedWindow;

/**
 * SQLNavigator
 */
class Navigator extends ManagedWindow 
implements ListSelectionListener
{
	transient SqlPlugin plugin;
	JPanel result;
	QueryPanel query;
    JList tables;
    JTextArea messages;
    JScrollPane jspMessages;
	
	/**
     * Constructor.
     */
    public Navigator(SqlPlugin plugin)
	{
    	super(plugin, plugin.getTitle());
        this.plugin = plugin;
        setExitPluginOnClose(true);
           
        result = new JPanel();
        result.setLayout(new BorderLayout());       
        query = new QueryPanel(this);
        tables = new JList();
        messages = new JTextArea(3, 10);
        
		JPanel jpm = new JPanel();
		jpm.setBorder(BorderFactory.createTitledBorder(plugin.tr("messages") + " "));
		jpm.setLayout(new BorderLayout());
        
        jspMessages = new JScrollPane(messages);
        jpm.add(jspMessages, BorderLayout.CENTER);
		
        JPanel jpq = new JPanel();
		jpq.setBorder(BorderFactory.createTitledBorder(plugin.tr("queries") + " "));
		jpq.setLayout(new BorderLayout());
		jpq.add(query, BorderLayout.CENTER);

        tables.setFixedCellWidth(175);
        tables.addListSelectionListener(this);
        
		getContentPane().setLayout(new BorderLayout());
        
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true
            ,new JScrollPane(tables), result);        
        jsp.setDividerSize(10);
        jsp.setOneTouchExpandable(true);
        
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(jpq, BorderLayout.CENTER);
        jp.add(jpm, BorderLayout.SOUTH);
        
        getContentPane().add(jsp, BorderLayout.CENTER);
        getContentPane().add(jp, BorderLayout.SOUTH);
       
        setTitle(plugin.getTitle());
		setPreferredSize(new Dimension(800, 600));
		show();
	}
    
    
	/**
     * ListSelectionListener Interface.
     * Display selected table content
     */
    public void valueChanged(ListSelectionEvent lse)
    {
        String query = "select * from " + (String)tables.getSelectedValue();
        SqlResult sr = plugin.executeQuery(query);
        result.setVisible(false);
        result.removeAll();
        
		JScrollPane sp = plugin.getScrollPane(sr);
        
        if(sp == null)
            sp = new JScrollPane();
        
        result.add(sp, BorderLayout.CENTER);
            
		result.setVisible(true);
    }
    
    /**
     * Display some text in the bottom area
     *
     * @param s the text to write
     */
    public void write(String s)
    {
        messages.append(s+"\n");
        JScrollBar jsb = jspMessages.getVerticalScrollBar();
        jsb.setValue(jsb.getMaximum());
    }    
}
