package aidancbrady.server;

import java.util.ArrayList;
import java.util.List;

public class ServerConnection
{
	public User user;
	public SocketConnection connection;
	public int userID;
	
	public ServerConnection(int id, SocketConnection socket)
	{
		userID = id;
		connection = socket;
	}
	
	public boolean hasUsername()
	{
		return user != null && user.username != "";
	}
}
