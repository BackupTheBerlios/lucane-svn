package org.lucane.server.auth;

public class AuthResponse 
{
	public static final int AUTH_ACCEPTED = 0;
	public static final int BAD_CREDENTIALS = 1;
	public static final int USER_LOCKED = 2;
	public static final int LOGIN_DISABLED = 2;
	
	private int value;
	private String privateKey;
	
	public AuthResponse(int value, String privateKey)
	{
		this.value = value;
		this.privateKey = privateKey;
	}
	
	public AuthResponse(int result)
	{
		this(result, null);
	}
		
	public int getValue()
	{
		return this.value;
	}
	
	public String getPrivateKey()
	{
		return this.privateKey;
	}
}
