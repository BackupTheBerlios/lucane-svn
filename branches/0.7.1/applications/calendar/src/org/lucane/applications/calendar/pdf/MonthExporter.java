package org.lucane.applications.calendar.pdf;

import java.util.*;
import java.io.*;
import java.awt.Color;

import org.lucane.applications.calendar.CalendarPlugin;
import org.lucane.applications.calendar.Event;
import org.lucane.applications.calendar.widget.BasicEvent;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class MonthExporter
{
	private CalendarPlugin plugin;
	private Calendar calendar;
	private Document document;
	private boolean sundayFirst = false;
	
	public MonthExporter(CalendarPlugin plugin, Calendar calendar)
	{
		this.plugin = plugin;
		this.calendar = calendar;
		sundayFirst = plugin.getLocalConfig().getInt("sundayFirst", 0) == 1;
	}
	
	public void exportMonth(String filename)
	throws Exception
	{
		Iterator events = this.fetchEvents();
		ArrayList[] list = computeEvents(events);

		this.initDocumentAndWriter(new FileOutputStream(filename));
		
		String monthHeader = plugin.getTitle() + " - " 
			+ plugin.tr("month." + (this.calendar.get(Calendar.MONTH)+1)) 
			+ " " + this.calendar.get(Calendar.YEAR); 
			
		Paragraph header = new Paragraph(monthHeader);
		header.setAlignment(Paragraph.ALIGN_RIGHT);	
		document.add(header);
		
		PdfPTable month = createMonthTable();
		addHeader(month);
		addContent(month, list);
		document.add(month);
		
		this.closeDocument();
	}
	
	private void initDocumentAndWriter(OutputStream out) 
	throws DocumentException
	{
		Rectangle a4_landscape = PageSize.A4.rotate();
		float margin = 10f;
		
		document = new Document(a4_landscape, margin, margin, margin, margin);
		
		PdfWriter.getInstance(document, out);
		document.addCreator("Lucane PDF export");

		document.open();		
	}
	
	private void closeDocument()
	{
		document.close();		
	}
	
	private PdfPTable createMonthTable()
	{
		PdfPTable table = new PdfPTable(7);
		return table;
	}
	
	private void addHeader(PdfPTable table)
	{
		table.getDefaultCell().setFixedHeight(20f);
		table.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
		table.setWidthPercentage(100f);		

		if(sundayFirst)
			table.addCell(plugin.tr("day.7"));
		for(int i=1;i<=6;i++)
			table.addCell(plugin.tr("day." + i));
		if(!sundayFirst)
			table.addCell(plugin.tr("day.7"));
	}
	
	private void addContent(PdfPTable table, ArrayList[] events)
	throws Exception
	{
		BaseFont helvetica = BaseFont.createFont("Helvetica", BaseFont.CP1252, 
			BaseFont.NOT_EMBEDDED);
		Font h10b = new Font(helvetica, 10, Font.BOLD);
		Font h8n = new Font(helvetica, 8, Font.NORMAL);
		Font h8n_gray = new Font(helvetica, 8, Font.NORMAL, Color.GRAY);
		Color unusedDay = new Color(220, 220, 220);
			
		table.getDefaultCell().setFixedHeight(80f);

		int day = this.getFirstDayOfDisplayedMonth();
		int max = this.getDaysOfDisplayedMonth();

		table.getDefaultCell().setBackgroundColor(unusedDay);
		for(int i=0;i<day;i++)
			table.addCell("");

		table.getDefaultCell().setBackgroundColor(Color.WHITE);
		for(int i=0;i<max;i++)
		{
			PdfPCell cell = new PdfPCell(table.getDefaultCell());
			Chunk dayLabel = new Chunk("" + (i+1));			
			dayLabel.setFont(h10b);
			
			Phrase phrase = new Phrase();
			phrase.add(dayLabel);

			Iterator dayEvents = events[day+i].iterator();
			while(dayEvents.hasNext())
			{
				BasicEvent event = (BasicEvent)dayEvents.next();
				
				String start = event.getStartHour() + ":" + event.getStartMinute();
				if(start.length() < 5)
					start += "0";
				String end = event.getEndHour() + ":" + event.getEndMinute();
				if(end.length() < 5)
					end += "0";

				Chunk hour = new Chunk("\n[" + start + "-" + end + "] ");
				hour.setFont(h8n);		
				Chunk title = new Chunk(event.getTitle());
				title.setFont(h8n_gray);
				
				phrase.add(hour);
				phrase.add(title);				
			}
			cell.setPhrase(phrase);
			table.addCell(cell);			
		}

		table.getDefaultCell().setBackgroundColor(unusedDay);
		for(int i=max+day;i<42;i++)
			table.addCell("");
	}
	
	private ArrayList[] computeEvents(Iterator events)
	{
		ArrayList[] list = new ArrayList[42];
		for(int i=0;i<list.length;i++)
			list[i] = new ArrayList();
		
		Calendar cal = Calendar.getInstance();
		
		while(events.hasNext())
		{
			Event event = (Event)events.next();
			cal.setTimeInMillis(event.getStartTime());
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int index = this.getFirstDayOfDisplayedMonth() + day-1;
			
			list[index].add(event);
		}
		
		return list;
	}
	
	private Iterator fetchEvents()
	{
		//-- get month interval (in milliseconds)
		long start, end;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);	
		start = cal.getTimeInMillis();
		cal.add(Calendar.MONTH, 1);
		end = cal.getTimeInMillis();
		
		//-- fetch and display events
		ArrayList events = new ArrayList();			
		try {			
				events = plugin.getMyEvents(start, end);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return events.iterator();
	}
	
	//--
	
	/**
	 * Get the first day (of week) for the displayed month
	 * 
	 * @return the first day of week
	 */	
	private int getFirstDayOfDisplayedMonth()
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		c.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		//as day labels don't respect locales, we have to enforce monday
		//instead of getFirstDayOfWeek() here to stay consistent
		int res = c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
		if(sundayFirst)
			res = c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;		
		
		return res < 0 ? 6 : res;
	}
	
	/**
	 * Get the number of days for the displayed month
	 * 
	 * @return the number of days
	 */
	private int getDaysOfDisplayedMonth()
	{	
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}