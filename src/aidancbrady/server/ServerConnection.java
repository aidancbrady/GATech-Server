package aidancbrady.server;

import java.util.ArrayList;
import java.util.List;

public class ServerConnection
{
	public String username = "";
	public SocketConnection connection;
	public int userID;
	
	public List<String> messages = new ArrayList<String>();
	
	public ServerConnection(int id, SocketConnection socket)
	{
		userID = id;
		connection = socket;
	}
	
	public boolean hasUsername()
	{
		return username != "";
	}
}
