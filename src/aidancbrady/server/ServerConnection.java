package aidancbrady.server;

import java.util.ArrayList;

public class ServerConnection
{
	public User user;
	public SocketConnection socketConnection;
	public int userID;
	public ArrayList<String> tempMessages = new ArrayList<String>();
	
	public ServerConnection(int id, SocketConnection socket)
	{
		userID = id;
		socketConnection = socket;
	}
	
	public void deauthenticate()
	{
		user = null;
	}
	
	public boolean isAuthenticated()
	{
		return user != null && user.username != "";
	}
}
