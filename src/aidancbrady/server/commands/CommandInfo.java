package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;

public class CommandInfo implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter)
	{
		try {
			int id = Integer.parseInt(params[1]);
			if(ServerCore.instance().connections.get(id) != null)
			{
				printWriter.println("Information on user " + id + ":");
				printWriter.println("Username: " + (ServerCore.instance().connections.get(id).isAuthenticated() ? ServerCore.instance().connections.get(id).user.username : "unknown"));
				if(ServerCore.instance().connections.get(id).user != null && !ServerCore.instance().connections.get(id).user.messages.isEmpty())
				{
					printWriter.println("Logged messages:");
					for(String message : ServerCore.instance().connections.get(id).user.messages)
					{
						printWriter.println(message);
					}
				}
				else {
					printWriter.println("No messages found for this user.");
				}
			}	
			else {
				printWriter.println("Unable to find database for user '" + connection.userID + ".'");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
