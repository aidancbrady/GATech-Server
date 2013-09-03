package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.FileHandler;
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
				if(ServerCore.instance().cachedUsers.get(params[1]) != null)
				{
					printWriter.println("There is already a user with that username!");
					return;
				}
				
				ServerCore.instance().distributeMessageIgnore(connection.userID, "<" + connection.getServerConnection().user.username + "'s username was changed to " + params[1] + ">");
				
				ServerCore.instance().cachedUsers.remove(connection.getServerConnection().user.username);
				connection.getServerConnection().user.username = params[1];
				ServerCore.instance().cachedUsers.put(connection.getServerConnection().user.username, connection.getServerConnection().user);
				
				printWriter.println("Successfully changed username to " + params[1] + ".");
				ServerCore.instance().theGui.appendChat("User " + connection.userID + " changed his username to '" + params[1] + ".'");
				
				printWriter.println("/user:" + connection.getServerConnection().user.username);
				
				FileHandler.write();
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
