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
		if(connection.getUser().isAuthenticated())
		{
			String username = connection.getUser().user.username;
			ServerCore.users.remove(username);
			printWriter.println("Successfully removed you from the cached user map. You are no longer authenticated.");
			System.out.println("Successfully removed user '" + username + "' from cached map.");
		}
		else {
			printWriter.println("Please authenticate before deleting your cache.");
		}
	}
}
