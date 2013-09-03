package aidancbrady.server;

import java.util.ArrayList;

public class ServerConnection
{
	public User user;
	public SocketConnection socketConnection;
	public ArrayList<String> tempMessages = new ArrayList<String>();
	public int timeout = 0;
	
	public ServerConnection(SocketConnection socket)
	{
		socketConnection = socket;
	}
	
	public int getUserID()
	{
		return socketConnection.userID;
	}
	
	public boolean isAuthenticated()
	{
		return user != null && user.username != "";
	}
}
