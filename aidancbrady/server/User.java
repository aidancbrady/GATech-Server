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
		ServerCore.instance().cachedUsers.put(username, this);
	}
	
	public void addMessage(String message)
	{
		if(message != null)
		{
			messages.add(message);
			FileHandler.write();
		}
	}
	
	public boolean isOnline()
	{
		return getConnection() != null;
	}
	
	public ServerConnection getConnection()
	{
		for(ServerConnection connection : ServerCore.instance().connections.values())
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
