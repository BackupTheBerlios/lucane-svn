/**
 * @author Xiaozheng Ma
 * database layer for Miscrosoft SQL2000
 */
package org.lucane.server.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

class SQLServerLayer extends DatabaseAbstractionLayer
{
	private DataSource dataSource;
	
	public SQLServerLayer(DataSource dataSource) 
	{
		this.dataSource = dataSource;
	}
	
	public Connection getConnection()
	throws SQLException
	{
		return dataSource.getConnection();
	}
	
	public String resolveType(String type)
	{
		if(type.equalsIgnoreCase("SMALLTEXT"))
			return "VARCHAR(250)";
		else if(type.equalsIgnoreCase("TEXT"))
			return "TEXT";
		else if(type.equalsIgnoreCase("SMALLINT"))
			return "SMALLINT";
		else if(type.equalsIgnoreCase("INT"))
			return "INT";
		else if(type.equalsIgnoreCase("BIGINT"))
			return "BIGINT";
		else if(type.equalsIgnoreCase("REAL"))
			return "REAL";
		else
			return type;
	}
}