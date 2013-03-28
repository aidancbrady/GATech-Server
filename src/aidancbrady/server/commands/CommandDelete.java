package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;

public class CommandDelete implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter)
	{
		if(connection.getServerConnection().isAuthenticated())
		{
			String username = connection.getServerConnection().user.username;
			ServerCore.instance().users.remove(username);
			printWriter.println("Successfully removed you from the cached user map. You are no longer authenticated.");
			ServerCore.instance().distributeMessageIgnore(connection.userID, "<" + username + " has left>");
			connection.getServerConnection().deauthenticate();
			System.out.println("Successfully removed user '" + username + "' from cached map.");
		}
		else {
			printWriter.println("Please authenticate before deleting your cache.");
		}
	}
}
