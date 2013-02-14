package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;
import aidancbrady.server.User;

public class CommandDeauthenticate implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter)
	{
		try {
			if(connection.getUser().isAuthenticated())
			{
				String username = connection.getUser().user.username;
				ServerCore.handleMessage(new User(username, null), "<" + username + " has left>");
				connection.getUser().user = null;
				System.out.println("User '" + username + "' has deauthenticated.");
				printWriter.println("Successfully deauthenticated.");
			}
			else {
				printWriter.println("You are not authenticated!");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
			e.printStackTrace();
		}
	}
}
