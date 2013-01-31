package aidancbrady.server;

import java.io.PrintWriter;

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
				if(ServerCore.users.get(params[1]) != null && ServerCore.users.get(params[1]).isOnline())
				{
					printWriter.println("That username is already taken!");
				}
				
				ServerCore.handleMessage(connection.getUser().user, "<" + connection.getUser().user.username + "'s username was changed to " + params[1] + ">");
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
