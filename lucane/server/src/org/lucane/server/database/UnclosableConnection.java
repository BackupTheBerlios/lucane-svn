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
package org.lucane.server.database;

import java.sql.*;
import java.util.Map;

class UnclosableConnection implements Connection
{
	private Connection parent;
	
	public UnclosableConnection(Connection parent)
	{
		this.parent = parent;
	}


	public Statement createStatement() throws SQLException
	{
		return parent.createStatement();
	}

	public PreparedStatement prepareStatement(String arg0) throws SQLException
	{
		return parent.prepareStatement(arg0);
	}

	public CallableStatement prepareCall(String arg0) throws SQLException
	{
		return parent.prepareCall(arg0);
	}


	public String nativeSQL(String arg0) throws SQLException
	{
		return parent.nativeSQL(arg0);
	}

	public void setAutoCommit(boolean arg0) throws SQLException
	{
		parent.setAutoCommit(arg0);
	}


	public boolean getAutoCommit() throws SQLException
	{		
		return parent.getAutoCommit();
	}

	public void commit() throws SQLException
	{
		parent.commit();
	}

	public void rollback() throws SQLException
	{
		parent.rollback();
	}

	public void close() throws SQLException
	{
		//no call to parent, we aren't closable !
	}

	public boolean isClosed() throws SQLException
	{
		return parent.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException
	{
		return parent.getMetaData();
	}

	public void setReadOnly(boolean arg0) throws SQLException
	{
		parent.setReadOnly(arg0);		
	}

	public boolean isReadOnly() throws SQLException
	{
		return parent.isReadOnly();
	}

	public void setCatalog(String arg0) throws SQLException
	{
		parent.setCatalog(arg0);
	}


	public String getCatalog() throws SQLException
	{
		return parent.getCatalog();
	}

	public void setTransactionIsolation(int arg0) throws SQLException
	{
		parent.setTransactionIsolation(arg0);
	}

	public int getTransactionIsolation() throws SQLException
	{
		return parent.getTransactionIsolation();
	}

	public SQLWarning getWarnings() throws SQLException
	{		
		return parent.getWarnings();
	}

	public void clearWarnings() throws SQLException
	{
		parent.clearWarnings();
	}

	public Statement createStatement(int arg0, int arg1) throws SQLException
	{
		return parent.createStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException
	{
		return parent.prepareStatement(arg0, arg1, arg2);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2) throws SQLException
	{
		return parent.prepareCall(arg0, arg1, arg2);
	}

	public Map getTypeMap() throws SQLException
	{
		return parent.getTypeMap();
	}

	public void setTypeMap(Map arg0) throws SQLException
	{
		parent.setTypeMap(arg0);
	}

	public void setHoldability(int arg0) throws SQLException
	{
		parent.setHoldability(arg0);
	}

	public int getHoldability() throws SQLException
	{
		return parent.getHoldability();
	}

	public Savepoint setSavepoint() throws SQLException
	{
		return parent.setSavepoint();
	}

	public Savepoint setSavepoint(String arg0) throws SQLException
	{
		return parent.setSavepoint(arg0);
	}

	public void rollback(Savepoint arg0) throws SQLException
	{
		parent.rollback();
	}

	public void releaseSavepoint(Savepoint arg0) throws SQLException
	{
		parent.releaseSavepoint(arg0);
	}

	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException
	{
		return parent.createStatement(arg0, arg1, arg2);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException
	{
		return parent.prepareStatement(arg0, arg1, arg2, arg3);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException
	{
		return parent.prepareCall(arg0, arg1, arg2, arg3);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException
	{
		return parent.prepareStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException
	{
		return this.prepareStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException
	{
		return this.prepareStatement(arg0, arg1);
	}
}
