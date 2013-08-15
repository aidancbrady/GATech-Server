package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;

public class CommandWhois implements ICommand
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
				printWriter.println("Logged messages:");
				printWriter.println("----------------");
				
				if(ServerCore.instance().connections.get(id).isAuthenticated())
				{
					if(ServerCore.instance().connections.get(id).user.messages.size() != 0)
					{
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
					if(ServerCore.instance().connections.get(id).tempMessages.size() != 0)
					{
						for(String message : ServerCore.instance().connections.get(id).tempMessages)
						{
							printWriter.println(message);
						}
					}
					else {
						printWriter.println("No messages found for this user.");
					}
				}
				
				printWriter.println("----------------");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
