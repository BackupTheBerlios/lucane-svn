package org.lucane.server.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.lucane.common.crypto.MD5;
import org.lucane.common.signature.KeyGenerator;
import org.lucane.common.signature.Signer;
import org.lucane.server.ServerConfig;
import org.lucane.server.database.DatabaseAbstractionLayer;

public class ResetPasswords
{
	private static final String CHARS = "azertyuiopqsdfghjklmwxcvbn1234567890";
	private static final int PASSWORD_LENGTH = 8;
	
	private static String createNewPassword()
	{
		String passwd = "";
		
		while(passwd.length() < PASSWORD_LENGTH)
		{
			char newChar = CHARS.charAt((int)(Math.random() * Integer.MAX_VALUE) 
					% CHARS.length());
			passwd += newChar;
		}
		
		return passwd;
	}
	
	private static void updateUser(String login, String passwd, PreparedStatement update)
	throws Exception
	{
		String[] keys = KeyGenerator.generateKeyPair();
        String privateKey = Signer.cypher(keys[0], passwd);
        String publicKey = keys[1];
        
        update.setString(1, MD5.encode(passwd));
        update.setString(2, publicKey);
        update.setString(3, privateKey);
        update.setString(4, login);
        update.execute();
        
        System.out.println(login + ":" + passwd);
	}

	public static void main(String [] args)
	throws Exception
	{
		ServerConfig config = new ServerConfig("etc/server-config.xml");
		if(!config.getStoreBackend().equals("database"))
		{
			System.err.println("not using database backend, sorry.");
			System.exit(1);
		}
		
		if(args.length > 2)
		{
			System.out.println("usage: ResetPasswords [user [passwd]]");
			System.exit(1);
		}
		
		String login = null;
		String passwd = null;
		if(args.length > 0)
		{
			login = args[0];
			if(args.length == 2)
				passwd = args[1];
			else
				passwd = createNewPassword();
		}
			
		
		
		DatabaseAbstractionLayer dbLayer = DatabaseAbstractionLayer.createLayer(config);

		Connection c = dbLayer.openConnection();
        PreparedStatement update = c.prepareStatement(
        		"UPDATE users SET passwd=?, pubkey=?, privkey=? WHERE login=?");

        //-- update all users
        if(login == null)
        {
			PreparedStatement select = c.prepareStatement("SELECT login FROM users");
			ResultSet rs = select.executeQuery();
			
			while(rs.next())
			{
				login = rs.getString(1);
				passwd = createNewPassword();
				updateUser(login, passwd, update);
			}
			rs.close();
			select.close();
        }
        
        //-- update one user
        else
        	updateUser(login, passwd, update);
        
		update.close();
		c.close();
	}
}