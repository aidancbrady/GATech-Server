package aidancbrady.server;

import java.util.ArrayList;

public class ServerConnection
{
	public User user;
	public SocketConnection connection;
	public int userID;
	public ArrayList<String> tempMessages = new ArrayList<String>();
	
	public ServerConnection(int id, SocketConnection socket)
	{
		userID = id;
		connection = socket;
	}
	
	public boolean isAuthenticated()
	{
		return user != null && user.username != "";
	}
}
