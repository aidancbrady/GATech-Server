package aidancbrady.server;

import java.util.ArrayList;

public class User
{
	public String username;
	public ArrayList<String> messages;
	
	public User(String name, ArrayList<String> message)
	{
		username = name;
		messages = message;
		ServerCore.users.put(username, this);
	}
	
	public void addMessage(String message)
	{
		if(message != null)
		{
			messages.add(message);
		}
	}
	
	public boolean isOnline()
	{
		for(ServerConnection connection : ServerCore.connections.values())
		{
			if(connection.user != null)
			{
				if(connection.user.username == username)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public ServerConnection getConnection()
	{
		if(!isOnline())
		{
			return null;
		}
		
		for(ServerConnection connection : ServerCore.connections.values())
		{
			if(connection.user != null)
			{
				if(connection.user.username == username)
				{
					return connection;
				}
			}
		}
		
		return null;
	}
}
