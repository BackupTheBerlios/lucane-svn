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
package org.lucane.server.store.sql;

import java.util.*;
import java.sql.*;

import org.lucane.server.*;
import org.lucane.common.concepts.*;
import org.lucane.server.database.*;
import org.lucane.server.store.*;

public class SqlGroupStore extends GroupStore
{
    private static final String TABLENAME = "groups";
    protected static final String GROUPLINKS = "groupLinks";
	protected static final String PLUGINLINKS = "pluginLinks";
	protected static final String SERVICELINKS = "serviceLinks";
	protected static final String USERLINKS = "userLinks";    
    
    private DatabaseAbstractionLayer layer;
    private Store store;
    
    public SqlGroupStore(Store store)
    throws Exception
    {
        this.layer = Server.getInstance().getDBLayer();
        this.store = store;
        
        if(!layer.hasTable(TABLENAME))
        {
        	String dbDescription = "jar:file:///"
    			+ Server.getInstance().getWorkingDirectory()
				+ "lib/lucane-server-" +Server.VERSION+ ".jar!/"
				+ "db-sqlstore.xml";

        	layer.getTableCreator().createFromXml(dbDescription);
        }
    }
    
    //-- interface
    
    /**
     * Does the store has any data ?
     * 
     * @return true if the store is already created
     */	
    public boolean isInitialized()
	throws Exception
	{    	
    	return getAllGroups().hasNext();
    }
    
	public void storeGroup(GroupConcept group)
	throws SQLException
	{       
        Connection c = layer.getConnection();
        PreparedStatement insert;
        Iterator i;
               
        //store group          
        insert = c.prepareStatement("INSERT INTO " + TABLENAME + " VALUES(?, ?)");
		insert.setString(1, group.getName());
		insert.setString(2, group.getDescription());
        insert.execute();
        insert.close();
        
        //store group links
        i = group.getParents();
        while(i.hasNext())
        {
            GroupConcept parent = (GroupConcept)i.next();
			insert = c.prepareStatement("INSERT INTO " + GROUPLINKS + " VALUES(?, ?)");
			insert.setString(1, parent.getName());
			insert.setString(2, group.getName());
			insert.execute();
			insert.close();            
        }
        
        //store user links
        i = group.getUsers();
        while(i.hasNext())
        {
            UserConcept user = (UserConcept)i.next();
			insert = c.prepareStatement("INSERT INTO " + USERLINKS + " VALUES(?, ?)");
			insert.setString(1, group.getName());
			insert.setString(2, user.getName());
			insert.execute();
			insert.close();         
        }
        
        //store services links
        i = group.getServices();
        while(i.hasNext())
        {
            ServiceConcept service = (ServiceConcept)i.next();
			insert = c.prepareStatement("INSERT INTO " + SERVICELINKS + " VALUES(?, ?)");
			insert.setString(1, group.getName());
			insert.setString(2, service.getName());
			insert.execute();
			insert.close();   
        }        
        
        //store plugins links
        i = group.getPlugins();
        while(i.hasNext())
        {
            PluginConcept plugin = (PluginConcept)i.next();
			insert = c.prepareStatement("INSERT INTO " + PLUGINLINKS + " VALUES(?, ?)");
			insert.setString(1, group.getName());
			insert.setString(2, plugin.getName());
			insert.execute();
			insert.close();          
        }        
        
        c.close();
	}
	
	public void updateGroup(GroupConcept group)
	throws SQLException
	{
		removeGroupOnly(group);
		storeGroup(group);
	}

	public void removeGroup(GroupConcept group)
	throws SQLException
	{
		//-- delete basic infos	
		removeGroupOnly(group);
		
        Connection c = layer.getConnection();           
        
        //delete group links
        PreparedStatement delete = c.prepareStatement("DELETE FROM " + GROUPLINKS 
        	+ " WHERE parent=? OR child=?");
		delete.setString(1, group.getName());           
		delete.setString(2, group.getName());           
        delete.execute();      
        
        delete.close();
        c.close();
	}

	public GroupConcept getGroup(String name) 
	throws Exception
	{
        GroupConcept group = null;

        Connection c = layer.getConnection();
        PreparedStatement select = c.prepareStatement("SELECT * FROM " + TABLENAME 
            + " WHERE name=?");
        select.setString(1, name);
        ResultSet rs = select.executeQuery();
                    
        if(rs.next())
        {
            name = rs.getString(1);
            String description = rs.getString(2); 
            
            group = new GroupConcept(name);
            group.setDescription(description);
            setGroupLinks(c, group);
            setUserLinks(c, group);
            setPluginLinks(c, group);
            setServiceLinks(c, group);
        }
    
        rs.close();     
        select.close();
        c.close();  
            
        return group;  
	}
	
    public Iterator getAllGroups()
    throws Exception
    {
		ArrayList all = new ArrayList();
		
		Connection c = layer.getConnection();
		PreparedStatement select = c.prepareStatement("SELECT * FROM " + TABLENAME);
		ResultSet rs = select.executeQuery();
					
		while(rs.next())
		{
			String  name = rs.getString(1);
			String description = rs.getString(2); 
            
			GroupConcept group = new GroupConcept(name);
			group.setDescription(description);
			setGroupLinks(c, group);
			setUserLinks(c, group);
			setPluginLinks(c, group);
			setServiceLinks(c, group);
			all.add(group);
		}
	
		rs.close();		
		select.close();
		c.close();		
		
		return all.iterator();    
    }
    
	//-- private methods
    
    private void setServiceLinks(Connection c, GroupConcept group)
    throws Exception
    {
    	PreparedStatement select = c.prepareStatement("SELECT service FROM " + SERVICELINKS 
			+ " WHERE groupName=?");
		select.setString(1, group.getName());
        ResultSet rs = select.executeQuery();        
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            ServiceConcept service = store.getServiceStore().getService(name);
            group.addService(service);
        }
    
        rs.close();     
        select.close();
    }

    private void setPluginLinks(Connection c, GroupConcept group)
    throws Exception
    {
		PreparedStatement select = c.prepareStatement("SELECT plugin FROM " + PLUGINLINKS 
			+ " WHERE groupName=?");
		select.setString(1, group.getName());
		ResultSet rs = select.executeQuery();  
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            PluginConcept plugin = store.getPluginStore().getPlugin(name);
            group.addPlugin(plugin);
        }
    
        rs.close();     
        select.close();
    }

    private void setUserLinks(Connection c, GroupConcept group)
    throws Exception
    {
		PreparedStatement select = c.prepareStatement("SELECT userName FROM " + USERLINKS 
			+ " WHERE groupName=?");
		select.setString(1, group.getName());
		ResultSet rs = select.executeQuery();  
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            UserConcept user = store.getUserStore().getUser(name);
            group.addUser(user);
        }
    
        rs.close();     
        select.close();
    }

    private void setGroupLinks(Connection c, GroupConcept group)
    throws Exception
    {
		PreparedStatement select = c.prepareStatement("SELECT parent FROM " + GROUPLINKS 
			+ " WHERE child=?");
		select.setString(1, group.getName());
		ResultSet rs = select.executeQuery();  
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            GroupConcept parent = store.getGroupStore().getGroup(name);
            group.addParent(parent);
        }
    
        rs.close();     
        select.close();
    }
    
	private void removeGroupOnly(GroupConcept group)
	throws SQLException
	{
		Connection c = layer.getConnection();
		PreparedStatement delete;
               
		//delete group
		delete = c.prepareStatement("DELETE FROM " + TABLENAME + " WHERE name=?");
		delete.setString(1, group.getName());
		delete.execute();
		delete.close();
        
		//delete group links
		delete = c.prepareStatement("DELETE FROM " + GROUPLINKS + " WHERE child=?");
		delete.setString(1, group.getName());
		delete.execute();
		delete.close();
		
		//delete user links
		delete = c.prepareStatement("DELETE FROM " + USERLINKS + " WHERE groupName=?");
		delete.setString(1, group.getName());
		delete.execute();
		delete.close();
        
		//delete services links
		delete = c.prepareStatement("DELETE FROM " + SERVICELINKS + " WHERE groupName=?");
		delete.setString(1, group.getName());
		delete.execute();
		delete.close();
		
		//delete plugins links
		delete = c.prepareStatement("DELETE FROM " + PLUGINLINKS + " WHERE groupName=?");
		delete.setString(1, group.getName());
		delete.execute();
		delete.close();
               
		c.close();
	}

}
