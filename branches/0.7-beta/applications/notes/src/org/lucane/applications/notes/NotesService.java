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
package org.lucane.applications.notes;

import org.lucane.common.*;
import org.lucane.server.*;
import org.lucane.server.database.*;

import java.util.*;
import java.sql.*;
import java.io.*;

public class NotesService
extends Service
{
  DatabaseAbstractionLayer layer = null;
  Connection connex = null;

  public NotesService()
  {
  }

  public void init(Server parent)
  {
	try {
	  layer = parent.getDBLayer();
	  connex = layer.openConnection();
	} catch(SQLException se) {
		Logging.getLogger().warning("Unable to open connection");
	}
  }

  public void install()
  {
  	try {
  		String dbDescription = getDirectory()	+ "db-notes.xml";
  		layer.getTableCreator().createFromXml(dbDescription);
  	} catch (Exception e) {
  		Logging.getLogger().severe("Unable to install NotesService !");
  		e.printStackTrace();
  	}    
  }

  public void process(ObjectConnection oc, Message message)
  {  	
  	String user = message.getSender().getName();
  	NotesAction action = (NotesAction)message.getData();
  	try {
  	  	Object res = executeAction(action, user);
  	  	oc.write("OK");
  	  	
  	  	if(res != null)
  	  		oc.write(res);
  	} catch(Exception e) {
  		try {
  			oc.write("FAILED " + e.getMessage());
  		} catch(IOException ioe) {
			Logging.getLogger().info("Error : " + ioe);
  		}
  	}
  }

  private Object executeAction(NotesAction action, String user)
  throws Exception
  {
	switch(action.getAction())
	{
		case NotesAction.SAVE_NOTE:			
			return saveNote((Note)action.getParam());
		case NotesAction.DELETE_NOTE:
			deleteNote((String)action.getParam());
			return null;
		case NotesAction.GET_PERSONNAL_NOTES:
			return getPersonnalNotes(user);
		case NotesAction.GET_PUBLISHED_AUTHORS:
			return getPublishedAuthors();
		case NotesAction.GET_RECENT_PUBLISHED_NOTES:
			return getRecentPublishedNotes((Integer)action.getParam());
		case NotesAction.GET_PUBLISHED_NOTES_BY_AUTHOR:
			return getPublishedNotesByAuthor((String)action.getParam());
		case NotesAction.SAVE_COMMENT:
			saveComment((Comment)action.getParam());
			return null;
		case NotesAction.GET_COMMENTS_FOR_NOTE:
			return getCommentsForNote((String)action.getParam());
	}
	
	return null;
  }


  //-- actions (notes)

  private synchronized Note saveNote(Note note)
    throws Exception
  {
    Statement stmt = connex.createStatement();

    long editionDate = 0;
    if(note.getEditionDate() != null)
      editionDate = note.getEditionDate().getTime();

    //new note, fetch a new id
    if(note.getId() == null)
    {
      try {
        ResultSet rs = stmt.executeQuery("SELECT MAX(id)+1 FROM notes");
        rs.next();
        note.setId(rs.getString(1));
        rs.close();
      } catch(Exception e) {
        note.setId("0");
      }
      if(note.getId() == null)
          note.setId("0");
    }
    //edition, remove old note
    else
    {
        stmt.execute("DELETE FROM notes WHERE id='" + note.getId() + "'");       
    }
    
    
    stmt.execute("INSERT INTO notes VALUES(" +
        note.getId() + ", '" + note.getAuthor() + "', '" + note.getTitle() +
        "', '" + note.getContent() + "', " + note.getCreationDate().getTime()
        + ", " + editionDate + ", " + (note.isPublic()?"1":"0") + ", " + (note.isCommentable()?"1":"0") + ")");
    stmt.close();
    
    return note;
  }

  private void deleteNote(String noteId)
    throws Exception
  {
	Statement stmt = connex.createStatement();  
	stmt.execute("DELETE FROM notes WHERE id=" + noteId);
	stmt.execute("DELETE FROM notes_comments WHERE idnote=" + noteId);
	stmt.close();  	
  }

  private Object[] getPersonnalNotes(String author)
    throws Exception
  {
  	ArrayList result = new ArrayList();
	Statement stmt = connex.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM notes WHERE author='" + author 
					+ "' ORDER BY author");

	while(rs.next())
	{
		Note n = new Note(
				rs.getString(1), //id 
				rs.getString(2), //author 
				rs.getString(3), //title 
				rs.getString(4), //content
				rs.getString(5), //creationDate
				rs.getString(6), //editionDate
				rs.getString(7), //isPublic
				rs.getString(8)); //commentable
		
		result.add(n);			
	}	

	rs.close();
	stmt.close();
	
    return result.toArray();
  }

  private Object[] getPublishedAuthors()
    throws Exception
  {
	ArrayList result = new ArrayList();
	Statement stmt = connex.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT distinct author FROM notes WHERE isPublic=1");

	while(rs.next())
	{
		String author = rs.getString(1);		
		result.add(author);			
	}	

	rs.close();
	stmt.close();
	
	return result.toArray();
  }

  private Object[] getRecentPublishedNotes(Integer max)
    throws Exception
  {
	ArrayList result = new ArrayList();
	Statement stmt = connex.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM notes WHERE isPublic=1 ORDER BY creationDate");

	for(int i=0;i<max.intValue() && rs.next();i++)
	{
		Note n = new Note(
				rs.getString(1), //id 
				rs.getString(2), //author 
				rs.getString(3), //title 
				rs.getString(4), //content
				rs.getString(5), //creationDate
				rs.getString(6), //editionDate
				rs.getString(7), //isPublic
				rs.getString(8)); //commentable
		
		result.add(n);			
	}	

	rs.close();
	stmt.close();
	
	return result.toArray();
  }

  private Object[] getPublishedNotesByAuthor(String author)
    throws Exception
  {
	ArrayList result = new ArrayList();
	Statement stmt = connex.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM notes WHERE author='" + author 
					+ "' AND isPublic=1 ORDER BY creationDate");

	while(rs.next())
	{
		Note n = new Note(
				rs.getString(1), //id 
				rs.getString(2), //author 
				rs.getString(3), //title 
				rs.getString(4), //content
				rs.getString(5), //creationDate
				rs.getString(6), //editionDate
				rs.getString(7), //isPublic
				rs.getString(8)); //commentable
		
		result.add(n);			
	}	

	rs.close();
	stmt.close();
	
	return result.toArray();
  }

  
  //-- actions (comments)

  private synchronized void saveComment(Comment comment)
    throws Exception
  {
	Statement stmt = connex.createStatement();

	//fetch a new id
    try {
  	  ResultSet rs = stmt.executeQuery("SELECT MAX(id)+1 FROM notes_comments");
	  rs.next();
	  comment.setId(rs.getString(1));
	  rs.close();
	} catch(Exception e) {
	  comment.setId("0");
	}
    if(comment.getId() == null)
        comment.setId("0");
   
	stmt.execute("INSERT INTO notes_comments VALUES(" +
		comment.getId() + ", " + comment.getNoteId() + ", '" + comment.getAuthor() + "', '" 
		+ comment.getTitle() +	"', '" + comment.getContent() + "', " 
		+ comment.getCreationDate().getTime() + ")");
	stmt.close();  	
  }

  private Object[] getCommentsForNote(String idNote)
    throws Exception
  {
	ArrayList result = new ArrayList();
	Statement stmt = connex.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM notes_comments WHERE idnote='" + idNote 
					+ "' ORDER BY creationDate");

	while(rs.next())
	{
		Comment c = new Comment(
				rs.getString(1), //id 
				rs.getString(2), //idnote 
				rs.getString(3), //author 
				rs.getString(4), //title 
				rs.getString(5), //content
				rs.getString(6)); //creationDate
		
		result.add(c);			
	}	

	rs.close();
	stmt.close();
	
	return result.toArray();
  }
}

