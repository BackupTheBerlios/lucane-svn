package org.lucane.server.auth;

import org.lucane.common.ConnectInfo;

public class AuthRequest 
{
	private ConnectInfo userInfo;
	private String md5Passwd;
	
	public AuthRequest(ConnectInfo userInfo, String md5Passwd)
	{
		this.userInfo = userInfo;
		this.md5Passwd = md5Passwd;
	}
	
	public ConnectInfo getUserInfo()
	{
		return this.userInfo;
	}
	
	public String getMd5Passwd()
	{
		return this.md5Passwd;
	}
}
