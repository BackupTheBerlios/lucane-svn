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

import org.lucane.client.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;

import org.lucane.applications.notes.gui.main.*;


public class NotesPlugin
extends StandalonePlugin
{
  private ConnectInfo service;
  private MainFrame mainFrame;

  public NotesPlugin()
  {
    this.service = Communicator.getInstance().getConnectInfo("org.lucane.applications.notes");
    this.starter = true;
  }

  public Plugin init(ConnectInfo[] friends, boolean starter)
  {
    return new NotesPlugin();
  }

  public void start()
  {
    mainFrame = new MainFrame(this);
    
    mainFrame.setAuthors(getPublishedAuthors());
    mainFrame.setNotes(getRecentPublishedNotes());
    
	mainFrame.setIconImage(this.getImageIcon().getImage());
    mainFrame.show();
  }

  public Object[] getPublishedAuthors()
  {
    Object[] authors = new Object[0];

    try {
      NotesAction action = new NotesAction(NotesAction.GET_PUBLISHED_AUTHORS);
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
      String ack = oc.readString();
      if(ack.equals("OK"))
        authors = (Object[])oc.read();
      else
        throw new Exception(ack);
    } catch(Exception e) {
      DialogBox.error(tr("error.listPublishedAuthors"));
      e.printStackTrace();
    }

    return authors;
  }

  public Object[] getRecentPublishedNotes()
  {
    Object[] notes = new Object[0];

    try {
      NotesAction action = new NotesAction(NotesAction.GET_RECENT_PUBLISHED_NOTES, new Integer(10));
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
      String ack = oc.readString();
      if(ack.equals("OK"))
        notes = (Object[])oc.read();
      else
        throw new Exception(ack);
    } catch(Exception e) {
      DialogBox.error(tr("error.listRecentPublishedNotes"));
      e.printStackTrace();
    }

    return notes;
  }

  public Object[] getPublishedNotesByAuthor(String author)
  {
    Object[] notes = new Object[0];

    try {
      NotesAction action = new NotesAction(NotesAction.GET_PUBLISHED_NOTES_BY_AUTHOR, author);
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
      String ack = oc.readString();
      if(ack.equals("OK"))
        notes = (Object[])oc.read();
      else
        throw new Exception(ack);
    } catch(Exception e) {
      DialogBox.error(tr("error.listPublishedNotesByAuthor"));
      e.printStackTrace();
    }

    return notes; 
  }

  public Object[] getPersonnalNotes()
  {
	Object[] notes = new Object[0];

	try {
	  NotesAction action = new NotesAction(NotesAction.GET_PERSONNAL_NOTES);
	  ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
	  String ack = oc.readString();
	  if(ack.equals("OK"))
		notes = (Object[])oc.read();
	  else
		throw new Exception(ack);
	} catch(Exception e) {
	  DialogBox.error(tr("error.listPersonnalNotes"));
	  e.printStackTrace();
	}

	return notes; 
  }

  public Object[] getCommentsForNote(String idNote)
  {
    Object[] comments = new Object[0];

    try {
      NotesAction action = new NotesAction(NotesAction.GET_COMMENTS_FOR_NOTE, idNote);
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
      String ack = oc.readString();
      if(ack.equals("OK"))
        comments = (Object[])oc.read();
      else
        throw new Exception(ack);
    } catch(Exception e) {
      DialogBox.error(tr("error.listCommentsForNote"));
      e.printStackTrace();
    }

    return comments;
  }
  
  public void removeNote(String idNote)
  {
    try {
      NotesAction action = new NotesAction(NotesAction.DELETE_NOTE, idNote);
      ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
      String ack = oc.readString();
      if(ack.equals("OK"))
        DialogBox.info(tr("noteRemoved"));
      else
        throw new Exception(ack);
    } catch(Exception e) {
      DialogBox.error(tr("error.removeNote"));
      e.printStackTrace();
    }
    
    mainFrame.setAuthors(getPublishedAuthors());
    mainFrame.setNotes(getRecentPublishedNotes());
  }



    public void saveNote(Note note)
    {
        try {
          NotesAction action = new NotesAction(NotesAction.SAVE_NOTE, note);
          ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
          String ack = oc.readString();
          if(ack.equals("OK"))
            DialogBox.info(tr("noteSaved"));
          else
            throw new Exception(ack);
        } catch(Exception e) {
          DialogBox.error(tr("error.saveNote"));
          e.printStackTrace();
        }
        
        mainFrame.setAuthors(getPublishedAuthors());
        mainFrame.setNotes(getRecentPublishedNotes());
    }


    public void saveComment(Comment comment)
    {
        try {
          NotesAction action = new NotesAction(NotesAction.SAVE_COMMENT, comment);
          ObjectConnection oc = Communicator.getInstance().sendMessageTo(service, getName(), action);
          String ack = oc.readString();
          if(ack.equals("OK"))
            DialogBox.info(tr("commentSaved"));
          else
            throw new Exception(ack);
        } catch(Exception e) {
          DialogBox.error(tr("error.saveComment"));
          e.printStackTrace();
        }
        
        mainFrame.setComments(getCommentsForNote(comment.getNoteId()));
    }
}
