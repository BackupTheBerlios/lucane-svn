package org.lucane.applications.sqlnavigator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.lucane.client.widgets.DialogBox;

import java.util.Vector;


/**
 * Reusable object allowing sql query edition
 * Has history and coloration.
 */
class QueryPanel extends JPanel implements ActionListener, KeyListener
{
    public Vector history;
    
	Navigator parent;
	JTextPane jtaQuery;
	JButton jbtPrevious;
	JButton jbtNext;
	JButton jbtClean;
	JButton jbtExecute;
    
    transient Syntaxer syntaxer;
    
    int histindex;
    
	
    /**
     * Constructor
     *
     * @param parent the parent Navigator
     *       (used for displaying results)
     */
	public QueryPanel(Navigator parent)
	{
		this.parent = parent;
        
        
        jtaQuery = new JTextPane();
        jtaQuery.addKeyListener(this);
        
        syntaxer = null;
        
        
        history = new Vector();       
        histindex = 0;
        
		setLayout(new BorderLayout());
						
		jbtPrevious = new JButton("<<");
		jbtPrevious.addActionListener(this);
		jbtNext = new JButton(">>");
		jbtNext.addActionListener(this);
		jbtClean = new JButton(tr("clear"));
		jbtClean.addActionListener(this);
		jbtExecute = new JButton(tr("execute"));
		jbtExecute.addActionListener(this);
		
		add(new JScrollPane(jtaQuery), BorderLayout.CENTER);
		
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(4, 1));        
        jp.add(jbtPrevious);
        jp.add(jbtNext);
        jp.add(jbtClean);
        jp.add(jbtExecute);
        
        add(jp, BorderLayout.EAST);
	}


    /**
     * Interface KeyListener
     */
    public void keyPressed(KeyEvent e)  {}
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e)
    {           
        if(e == null || 
            (e.getKeyChar() != KeyEvent.VK_BACK_SPACE && e.getKeyChar() != KeyEvent.VK_DELETE) 
            )
        {
            syntaxer = new Syntaxer(jtaQuery);

            if(e == null)
                syntaxer.forcing();
  
            syntaxer.start();   
        }             
    }
    
    /**
     * ActionListener Interface.
     * Handle click on a button
     */
	public void actionPerformed(ActionEvent ae)
	{
		/* history : previous */
        if(ae.getSource() == jbtPrevious && histindex > 0)
        {
            jtaQuery.setText((String)history.elementAt(--histindex));            
            parent.write(tr("history1") + (histindex+1) + " " + tr("history2")  + " " +history.size());
            keyReleased(null); 
        }
        
        /* history : next */
		else if(ae.getSource() == jbtNext && histindex < history.size()-1)
        {
            jtaQuery.setText((String)history.elementAt(++histindex));
			parent.write(tr("history1") + (histindex+1) + " " + tr("history2")  + " " +history.size());
            keyReleased(null); 
        }
        
        /* clear */
		else if(ae.getSource() == jbtClean)
        {
            jtaQuery.setText("");
        }
        
        /* execute query */
		else if(ae.getSource() == jbtExecute)
        {
            try
	    	{
		    	SqlResult sr = parent.plugin.executeQuery(jtaQuery.getText());
    			parent.result.setVisible(false);
                parent.result.removeAll();
		    			
                JScrollPane sp = parent.plugin.getScrollPane(sr);
        
                if(sp == null)
                    sp = new JScrollPane();
        
                parent.result.add(sp, BorderLayout.CENTER);

			    parent.result.setVisible(true);
                history.addElement(jtaQuery.getText());
                histindex = history.size()-1;
 	    	}
		    catch(Exception e)
    		{
    			parent.write(tr("error.result.read"));
                parent.write(e.getMessage());
    			DialogBox.error(tr("error.result.read") + "\n" + e.getMessage());            
    		}
    	}
    }
	
	private String tr(String s)
	{
		return parent.plugin.tr(s);
	}
}
