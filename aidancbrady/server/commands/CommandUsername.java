package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;

public class CommandUsername implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter)
	{
		try {
			if(params.length != 2 || params[1] == null || params[1] == "")
			{
				throw new Exception();
			}
			
			if(connection.getServerConnection().isAuthenticated())
			{
				if(ServerCore.instance().users.get(params[1]) != null)
				{
					if(ServerCore.instance().users.get(params[1]).isOnline())
					{
						printWriter.println("That username is already taken!");
					}
					else {
						connection.getServerConnection().user = ServerCore.instance().users.get(params[1]);
						printWriter.println("Successfully switched to user '" + params[1] + ".'");
						ServerCore.instance().distributeMessageIgnore(connection.userID, "<" + connection.getServerConnection().user.username + " has signed in as " + params[1] + ">");
					}
					return;
				}
				
				ServerCore.instance().distributeMessageIgnore(connection.userID, "<" + connection.getServerConnection().user.username + "'s username was changed to " + params[1] + ">");
				connection.getServerConnection().user.username = params[1];
				printWriter.println("Successfully changed username to " + params[1] + ".");
				ServerCore.instance().theGui.appendChat("User " + connection.userID + " changed his username to '" + params[1] + ".'");
			}
			else {
				printWriter.println("Please authenticate before you modify your username.");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
