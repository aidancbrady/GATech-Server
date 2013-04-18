package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;

public class CommandDeauthenticate implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter)
	{
		try {
			if(connection.getServerConnection().isAuthenticated())
			{
				String username = connection.getServerConnection().user.username;
				ServerCore.instance().distributeMessageIgnore(connection.userID, "<" + username + " has left>");
				connection.getServerConnection().deauthenticate();
				ServerCore.instance().theGUI.appendChat("User '" + username + "' has deauthenticated.");
				printWriter.println("Successfully deauthenticated.");
			}
			else {
				printWriter.println("You are not authenticated!");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
