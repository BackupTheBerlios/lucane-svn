/**
 * @author Xiaozheng Ma
 * database layer for Miscrosoft SQL2000
 */
package org.lucane.server.database;

class SQLServerLayer extends DatabaseAbstractionLayer
{
	public SQLServerLayer(String url, String login, String passwd)
	{
		this.url = url;
		this.login = login;
		this.passwd = passwd;
	}


	public String resolveType(String type)
	{
		if(type.equalsIgnoreCase("SMALLTEXT"))
			return "VARCHAR(255)";
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