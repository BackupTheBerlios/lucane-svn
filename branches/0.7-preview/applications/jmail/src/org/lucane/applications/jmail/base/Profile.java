package org.lucane.applications.jmail.base;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of JMail                                              *
 * Copyright (C) 2002-2003 Yvan Norsa <norsay@wanadoo.fr>                  *
 *                                                                         *
 * JMail is free software; you can redistribute it and/or modify           *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * any later version.                                                      *
 *                                                                         *
 * JMail is distributed in the hope that it will be useful,                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License along *
 * with JMail; if not, write to the Free Software Foundation, Inc.,        *
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA                 *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import org.lucane.applications.jmail.Account;

/** Class which represents a user profile */
public class Profile
{
    /** Profile name */
    private String profileName;

    private String profilePassword;
    private String unhashedProfilePassword;

    /** User email */
    private String email;

    private String replyTo;

    /** Incoming server's type (protocol) */
    private String type;

    /** Incoming server */
    private String incoming;

    /** Incoming server's port number */
    private int incomingPort;

    private boolean useSSL;

    /** Outgoing server */
    private String outgoing;

    /** Outgoing server's port number */ 
    private int outgoingPort;

    /** User login */
    private String login;

    /** User password */
    private String password;

    private String decryptedMailPassword;

    private String signature;

    public Profile(Account a)
	{
    	this("", "", a.address, a.address, a.type, a.inHost, a.inPort, false, a.outHost,
    			a.outPort, a.login, "", "");
    	this.setDecryptedMailPassword(a.password);    	
    }
    
    /** Constructor
     *  @param profileName profile name
     *  @param profilePassword profile password (hashed)
     *  @param email user email
     *  @param type incoming server type
     *  @param incoming incoming server
     *  @param incomingPort incoming server's port number
     *  @param useSSl tells if the profiles uses a SSL connection
     *  @param outgoing outgoing server
     *  @param outgoingPort outgoing server's port number
     *  @param login user login
     *  @param password user password
     *  @param signature signature file
     *  @param addresses addresses stored in the profile
     */
    protected Profile(String profileName, String profilePassword, String email, String replyTo, 
    		String type, String incoming, int incomingPort, boolean useSSL, String outgoing,
    		int outgoingPort, String login, String password, String signature)
    {
	this.profileName = profileName;
	this.profilePassword = profilePassword;
	this.email = email;
	this.replyTo = replyTo;
	this.type = type;
	this.incoming = incoming;
	this.incomingPort = incomingPort;
	this.useSSL = useSSL;
	this.outgoing = outgoing;
	this.outgoingPort = outgoingPort;
	this.login = login;
	this.password = password;
	this.signature = signature;
    }


    /** Returns the profile name
     *  @return profile name
     */
    protected final String getProfileName()
    {
	return(profileName);
    }

    protected final String getProfilePassword()
    {
	return(profilePassword);
    }

    protected final void setProfilePassword(String profilePassword)
    {
	this.profilePassword = profilePassword;
    }

    protected final String getUnhashedProfilePassword()
    {
	return(unhashedProfilePassword);
    }

    protected final void setUnhashedProfilePassword(String unhashedProfilePassword)
    {
	this.unhashedProfilePassword = unhashedProfilePassword;
    }

    /** Returns the email address
     *  @return the email address
     */
    protected final String getEmail()
    {
	return(email);
    }

    /** Sets the new email address
     *  @param email the new email address
     */
    protected final void setEmail(String email)
    {
	this.email = email;
    }

    protected final String getReplyTo()
    {
	return(replyTo);
    }

    protected final void setReplyTo(String replyTo)
    {
	this.replyTo = replyTo;
    }

    /** Returns the server's type
     *  @return server type
     */
    protected final String getType()
    {
	return(type);
    }

    /** Sets the new server type
     *  @param type the new type
     */
    protected final void setType(String type)
    {
	this.type = type;
    }

    /** Returns the incoming server
     *  @return incoming server
     */
    protected final String getIncoming()
    {
	return(incoming);
    }

    /** Sets the new incoming server
     *  @param incoming new incoming server
     */
    protected final void setIncoming(String incoming)
    {
	this.incoming = incoming;
    }

    /** Returns the incoming server's port number
     *  @return incoming server's port number
     */
    protected final int getIncomingPort()
    {
	return(incomingPort);
    }

    /** Sets the new incoming server's port number
     *  @param incomingPort the new incoming server's port number
     */
    protected final void setIncomingPort(int incomingPort)
    {
	this.incomingPort = incomingPort;
    }

    protected final boolean getUseSSL()
    {
	return(useSSL);
    }    

    protected final void setUseSSL(boolean useSSL)
    {
	this.useSSL = useSSL;
    }

    /** Returns the outgoing server
     *  @return outgoing server
     */
    protected final String getOutgoing()
    {
	return(outgoing);
    }

    /** Sets the new outgoing server
     *  @param outgoing the new outgoing server
     */
    protected final void setOutgoing(String outgoing)
    {
	this.outgoing = outgoing;
    }

    /** Returns the outgoing server's port number
     *  @return outgoing server's port number
     */
    protected final int getOutgoingPort()
    {
	return(outgoingPort);
    }

    /** Sets the new outgoing server's port number
     *  @param outgoingPort the new outgoing server's port number
     */
    protected final void setOutgoingPort(int outgoingPort)
    {
	this.outgoingPort = outgoingPort;
    }

    /** Returns the login
     *  @return login
     */
    protected final String getLogin()
    {
	return(login);
    }

    /** Sets the new login
     *  @param login new login
     */
    protected final void setLogin(String login)
    {
	this.login = login;
    }

    /** Returns the password
     *  @return password
     */
    protected final String getPassword()
    {
	return(password);
    }

    /** Sets the new password
     *  @param password new password
     */
    protected final void setPassword(String password)
    {
	this.password = password;
    }

    protected final String getDecryptedMailPassword()
    {
	return(decryptedMailPassword);
    }

    protected final void setDecryptedMailPassword(String decryptedMailPassword)
    {
	this.decryptedMailPassword = decryptedMailPassword;
    }

    protected final String getSignature()
    {
	return(signature);
    }

    protected final void setSignature(String signature)
    {
	this.signature = signature;
    }

    /** Sets the base infos for the profile
     *  @param email user email
     *  @param type incoming server type
     *  @param incoming incoming server
     *  @param incomingPort incoming server's port number
     *  @param outgoing outgoing server
     *  @param outgoingPort outgoing server's port number
     *  @param login user login
     *  @param password user password
     */
    protected final void setBaseInfos(String profilePassword, String email, String replyTo, String type, String incoming, int incomingPort, boolean useSSL, String outgoing, int outgoingPort, String login, String password, String signature)
    {
	this.profilePassword = profilePassword;
	this.email = email;
	this.replyTo = replyTo;
	this.type = type;
	this.incoming = incoming;
	this.incomingPort = incomingPort;
	this.useSSL = useSSL;
	this.outgoing = outgoing;
	this.outgoingPort = outgoingPort;
	this.login = login;
	this.password = password;
	this.signature = signature;
    }
}
