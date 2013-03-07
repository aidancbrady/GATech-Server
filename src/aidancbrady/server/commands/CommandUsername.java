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
			
			if(connection.getUser().isAuthenticated())
			{
				if(ServerCore.users.get(params[1]) != null)
				{
					printWriter.println("That username is already taken!");
					return;
				}
				
				ServerCore.handleMessageIgnore(connection.userID, "<" + connection.getUser().user.username + "'s username was changed to " + params[1] + ">");
				connection.getUser().user.username = params[1];
				printWriter.println("Successfully changed username to " + params[1] + ".");
				System.out.println("User " + connection.userID + " changed his username to '" + params[1] + ".'");
			}
			else {
				printWriter.println("Please authenticate before you modify your username.");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
