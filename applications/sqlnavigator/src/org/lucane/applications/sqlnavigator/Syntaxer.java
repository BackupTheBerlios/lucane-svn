package org.lucane.applications.sqlnavigator;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.Vector;



/**
 * Syntax colorer
 */
class Syntaxer
{
    SimpleAttributeSet asKeyword1, asKeyword2, asNumber, asText, asStandard;
	Vector keywords1, keywords2;
    
    JTextPane jtaQuery;
    boolean force;

    /**
     * Constructor.
     *
     * @param jtp the JTextPane containing the query
     */
    public Syntaxer(JTextPane jtp)
    {
        jtaQuery = jtp;
        force = false;
        
            
        /* color styles */
        asKeyword1 = new SimpleAttributeSet();
        asKeyword1.addAttribute(StyleConstants.ColorConstants.Foreground, Color.blue);
        asKeyword2 = new SimpleAttributeSet();
        asKeyword2.addAttribute(StyleConstants.ColorConstants.Foreground, new Color(120, 0, 255));
        asNumber = new SimpleAttributeSet();
        asNumber.addAttribute(StyleConstants.ColorConstants.Foreground, Color.red);
        asText = new SimpleAttributeSet();
        asText.addAttribute(StyleConstants.ColorConstants.Foreground, Color.gray);
        asStandard = new SimpleAttributeSet();
        asStandard.addAttribute(StyleConstants.ColorConstants.Foreground, Color.black);
        jtaQuery.setCharacterAttributes(asStandard, true);
        
        initKeyWords();

    }
    
    /**
     * Set a flag forcing the complete coloring of all the query
     */
    public void forcing()
    {
        force = true;
    }
    
    /**
     * Initialize keyword list
     * keywords1 : language
     * keywords2 : stantard functions
     */
    private void initKeyWords()
    {
        this.keywords1 = new Vector();
        this.keywords1.addElement("select");
        this.keywords1.addElement("from");
        this.keywords1.addElement("where");
        this.keywords1.addElement("order");
        this.keywords1.addElement("and");
        this.keywords1.addElement("in");
        this.keywords1.addElement("not");
        this.keywords1.addElement("or");
        this.keywords1.addElement("like");
        this.keywords1.addElement("all");
        this.keywords1.addElement("distinct");
        this.keywords1.addElement("group");
        this.keywords1.addElement("by");
        this.keywords1.addElement("having");
        this.keywords1.addElement("union");
        this.keywords1.addElement("intersect");
        this.keywords1.addElement("minus");
        this.keywords1.addElement("asc");
        this.keywords1.addElement("desc");
        this.keywords1.addElement("for");
        this.keywords1.addElement("update");
        this.keywords1.addElement("of");
        this.keywords1.addElement("is");
        this.keywords1.addElement("null");
        this.keywords1.addElement("between");
        this.keywords1.addElement("exists");
        this.keywords1.addElement("insert");
        this.keywords1.addElement("into");
        this.keywords1.addElement("values");
        this.keywords1.addElement("delete");
        this.keywords1.addElement("set");
        this.keywords1.addElement("as");
 

        this.keywords2 = new Vector();
        this.keywords2.addElement("abs");
        this.keywords2.addElement("ceil");
        this.keywords2.addElement("floor");
        this.keywords2.addElement("mod");
        this.keywords2.addElement("power");
        this.keywords2.addElement("round");
        this.keywords2.addElement("trunc");
        this.keywords2.addElement("sqrt");
        this.keywords2.addElement("least");
        this.keywords2.addElement("greatest");
        this.keywords2.addElement("length");
        this.keywords2.addElement("upper");
        this.keywords2.addElement("lower");
        this.keywords2.addElement("substr");
        this.keywords2.addElement("avg");
        this.keywords2.addElement("count");
        this.keywords2.addElement("max");
        this.keywords2.addElement("min");
        this.keywords2.addElement("sum");
        this.keywords2.addElement("stddev");
        this.keywords2.addElement("variance");
        this.keywords2.addElement("max");
    }
    	    
    /**
     * Colorize the whole query
     */
    private void forceColorize()
    {
        String txt = jtaQuery.getText();
        String markers = " \t\r\n,;=<>+-/*()[]";        

    	int startpos;
	    int endpos;
		int currentpos;
        int caretpos = jtaQuery.getCaretPosition();
        
        for(int usepos = 0;usepos < txt.length();usepos++)
        {
    		startpos=0;
	    	endpos=txt.length();

    		/* search word beginning */
	    	for(int i=0;i<markers.length();i++)
		    {
    			currentpos = txt.lastIndexOf(markers.charAt(i), usepos);
	    		if(currentpos >= startpos)
		    		startpos = currentpos+1;
    		}


	    	/* search word end */
    		for(int i=0;i<markers.length();i++)
	    	{
		    	currentpos = txt.indexOf(markers.charAt(i), usepos);
    			if(currentpos >= 0 && currentpos < endpos)
	    			endpos = currentpos;
    		}
	
	    	if(startpos<endpos)
		    {
			    jtaQuery.select(startpos, endpos);
				
                /* coloration */		   
    			if(jtaQuery.getSelectedText() != null)
	    		{
		    		boolean kw = false;
			    	for(int k=0;k<keywords1.size() && !kw;k++)
				    {
					    if(jtaQuery.getSelectedText().equalsIgnoreCase( (String)keywords1.elementAt(k) ))
    					{
	    					jtaQuery.setCharacterAttributes(asKeyword1, true);
		    				kw = true;
			    		}
				    }
    
	    			for(int k=0;k<keywords2.size() && !kw;k++)
		    		{
			    		if(jtaQuery.getSelectedText().equalsIgnoreCase( (String)keywords2.elementAt(k) ))
				    	{
					    	jtaQuery.setCharacterAttributes(asKeyword2, true);
						    kw = true;
    					}
	    			}
    
	    			if(!kw)
		    		{
			    		try
				    	{
					    	Double d = new Double(jtaQuery.getSelectedText());
						    jtaQuery.setCharacterAttributes(asNumber, true);
    					}
	    				catch(Exception e)
		    			{
			    			jtaQuery.setCharacterAttributes(asStandard, true);
				    	}
    				}   
	    		}
		    }
        }
    
		/* search strings */
        startpos = endpos = 0;
		while(startpos >= 0 && endpos >= 0)
		{
			startpos = txt.indexOf('\'', endpos+1);
			endpos = -1;
			if(startpos >= 1)
			{
				endpos = txt.indexOf('\'', startpos+1);
				if(endpos < 0)
					endpos = txt.length();
				
				if(startpos<endpos)
				{
					jtaQuery.select(startpos-1, endpos+1);
					jtaQuery.setCharacterAttributes(asText, true);  
				}
			}
		}

        /* reset cursor */
		jtaQuery.select(caretpos,caretpos);
        jtaQuery.setCharacterAttributes(asStandard, true);     
    }       
            
    /**
     * Colorize the current word
     */        
    private void colorize()
    {
        String txt = jtaQuery.getText();
        String markers = " \t\r\n,;=<>+-/*()[]";
        
        int caretpos = jtaQuery.getCaretPosition();
		
		int usepos = caretpos -1;
        
		int startpos=0;
		int endpos=txt.length();
		int currentpos;


        /* go back while we are on a separator */
        while(usepos > 0 && markers.indexOf(txt.charAt(usepos))>=0)
            usepos--;

		/* search the end of the word */
		for(int i=0;i<markers.length();i++)
		{
			currentpos = txt.indexOf(markers.charAt(i), usepos);
			if(currentpos >= 0 && currentpos < endpos)
				endpos = currentpos;
		}
        
        
		/* search the beginning of the word */
		for(int i=0;i<markers.length();i++)
		{
			currentpos = txt.lastIndexOf(markers.charAt(i), usepos);
			if(currentpos >= startpos)
				startpos = currentpos+1;
		}
	
		if(startpos<endpos)
		{
            /* text selection */
			jtaQuery.select(startpos, endpos);
						   
            /* coloration */               
			if(jtaQuery.getSelectedText() != null)
			{
				boolean kw = false;
				for(int k=0;k<keywords1.size() && !kw;k++)
				{
					if(jtaQuery.getSelectedText().equalsIgnoreCase( (String)keywords1.elementAt(k) ))
					{
						jtaQuery.setCharacterAttributes(asKeyword1, true);
						kw = true;
					}
				}

				for(int k=0;k<keywords2.size() && !kw;k++)
				{
					if(jtaQuery.getSelectedText().equalsIgnoreCase( (String)keywords2.elementAt(k) ))
					{
						jtaQuery.setCharacterAttributes(asKeyword2, true);
						kw = true;
					}
				}

				if(!kw)
				{
					try
					{
						Double d = new Double(jtaQuery.getSelectedText());
						jtaQuery.setCharacterAttributes(asNumber, true);
					}
					catch(Exception e)
					{
						jtaQuery.setCharacterAttributes(asStandard, true);
					}
				}
			}
		}

     		/* search strings */
            startpos = endpos = 0;
		    while(startpos >= 0 && endpos >= 0)
    		{
		    	startpos = txt.indexOf('\'', endpos+1);
	    		endpos = -1;
			    if(startpos >= 1)
    			{
	    			endpos = txt.indexOf('\'', startpos+1);
		    		if(endpos < 0)
			    		endpos = txt.length();
				
    				if(startpos<endpos)
	    			{
		    			jtaQuery.select(startpos-1, endpos+1);
			    		jtaQuery.setCharacterAttributes(asText, true);  
				    }
    			}

            /* reset cursor */    
	    	jtaQuery.select(caretpos,caretpos);
            jtaQuery.setCharacterAttributes(asStandard, true);     
        }
    }
    
    /**
     * Start colorization
     */
    public void start()
    {
        if(force)
        {
            force = false;
            forceColorize();
        }
        else
            colorize();
    }
}
