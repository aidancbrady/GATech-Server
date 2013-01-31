package aidancbrady.server;

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
	
	public boolean isAuthenticated()
	{
		return user != null && user.username != "";
	}
}
