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
        	//TODO !! make version as a constant !!
        	String dbDescription = "jar:file:///"
        			+ System.getProperty("user.dir").replace('\\', '/')
					+ "/lib/lucane-server-0.6.3.jar!/"
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
        Connection c = layer.openConnection();
        Statement s = c.createStatement();
        Iterator i;
        String query;
               
        //store group           
        query = "INSERT INTO " + TABLENAME + " VALUES('"
            + group.getName() + "', '"
            + group.getDescription() + "')";           
        s.execute(query);
        
        //store group links
        i = group.getParents();
        while(i.hasNext())
        {
            GroupConcept parent = (GroupConcept)i.next();
            query = "INSERT INTO " + GROUPLINKS + " VALUES('"
                + parent.getName() + "', '"
                + group.getName() + "')";           
            s.execute(query);            
        }
        
        //store user links
        i = group.getUsers();
        while(i.hasNext())
        {
            UserConcept user = (UserConcept)i.next();
            query = "INSERT INTO " + USERLINKS + " VALUES('"
                + group.getName() + "', '"
                + user.getName() + "')";           
            s.execute(query);            
        }
        
        //store services links
        i = group.getServices();
        while(i.hasNext())
        {
            ServiceConcept service = (ServiceConcept)i.next();
            query = "INSERT INTO " + SERVICELINKS + " VALUES('"
                + group.getName() + "', '"
                + service.getName() + "')";           
            
            s.execute(query);
        }        
        
        //store plugins links
        i = group.getPlugins();
        while(i.hasNext())
        {
            PluginConcept plugin = (PluginConcept)i.next();
            query = "INSERT INTO " + PLUGINLINKS + " VALUES('"
                + group.getName() + "', '"
                + plugin.getName() + "')";           
            s.execute(query);            
        }        
        
        s.close();
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
		
        Connection c = layer.openConnection();
        Statement s = c.createStatement();
        String query;
               
        
        //delete group links
        query = "DELETE FROM " + GROUPLINKS + " WHERE parent='"
            + group.getName() + "' OR child='" + group.getName() + "'";           
        s.execute(query);      
        
        s.close();
        c.close();
	}

	public GroupConcept getGroup(String name) 
	throws Exception
	{
        GroupConcept group = null;

        Connection c = layer.openConnection();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME 
            + " WHERE name='" + name + "'");
                    
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
        s.close();
        c.close();  
            
        return group;  
	}
	
    public Iterator getAllGroups()
    throws Exception
    {
		ArrayList all = new ArrayList();
		
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM " + TABLENAME);
					
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
		s.close();
		c.close();		
		
		return all.iterator();    
    }
    
	//-- private methods
    
    private void setServiceLinks(Connection c, GroupConcept group)
    throws Exception
    {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT service FROM " + SERVICELINKS 
            + " WHERE groupName='" + group.getName() + "'");
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            ServiceConcept service = store.getServiceStore().getService(name);
            group.addService(service);
        }
    
        rs.close();     
        s.close();
    }

    private void setPluginLinks(Connection c, GroupConcept group)
    throws Exception
    {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT plugin FROM " + PLUGINLINKS 
            + " WHERE groupName='" + group.getName() + "'");
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            PluginConcept plugin = store.getPluginStore().getPlugin(name);
            group.addPlugin(plugin);
        }
    
        rs.close();     
        s.close();
    }

    private void setUserLinks(Connection c, GroupConcept group)
    throws Exception
    {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT userName FROM " + USERLINKS 
            + " WHERE groupName='" + group.getName() + "'");
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            UserConcept user = store.getUserStore().getUser(name);
            group.addUser(user);
        }
    
        rs.close();     
        s.close();
    }

    private void setGroupLinks(Connection c, GroupConcept group)
    throws Exception
    {
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT parent FROM " + GROUPLINKS 
            + " WHERE child='" + group.getName() + "'");
                    
        while(rs.next())
        {
            String name = rs.getString(1);
            GroupConcept parent = store.getGroupStore().getGroup(name);
            group.addParent(parent);
        }
    
        rs.close();     
        s.close();
    }
    
	private void removeGroupOnly(GroupConcept group)
	throws SQLException
	{
		Connection c = layer.openConnection();
		Statement s = c.createStatement();
		String query;
               
		//delete group           
		query = "DELETE FROM " + TABLENAME + " WHERE name='"
			+ group.getName() + "'";           
		s.execute(query);
        
		//delete group links
		query = "DELETE FROM " + GROUPLINKS + " WHERE child='" 
			+ group.getName() + "'";           
		s.execute(query);
		
		//delete user links
		query = "DELETE FROM " + USERLINKS + " WHERE groupName='"
			+ group.getName() + "'";           
		s.execute(query);
        
		//delete services links
		query = "DELETE FROM " +SERVICELINKS + " WHERE groupName='"
			+ group.getName() + "'";           
		s.execute(query);
        
		//delete plugins links
		query = "DELETE FROM " + PLUGINLINKS + " WHERE groupName='"
			+ group.getName() + "'";           
		s.execute(query);
               
		s.close();
		c.close();
	}

}
