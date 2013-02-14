package aidancbrady.server.commands;

import java.io.PrintWriter;
import java.util.ArrayList;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;
import aidancbrady.server.User;

public class CommandAuthenticate implements ICommand
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
				printWriter.println("You are already authenticated!");
				return;
			}
			
			if(ServerCore.users.containsKey(params[1]))
			{
				if(ServerCore.users.get(params[1]).isOnline())
				{
					printWriter.println("That username is already taken!");
					return;
				}
				
				connection.getUser().user = ServerCore.users.get(params[1]);
				printWriter.println("Welcome back, " + params[1]);
				System.out.println("User '" + params[1] + "' has joined.");
				ServerCore.handleMessage(connection.getUser().user, "<" + connection.getUser().user.username + " has joined>");
			}
			else {
				connection.getUser().user = new User(params[1], new ArrayList<String>());
				printWriter.println("Username received. Welcome to the AidanServer!");
				System.out.println("User " + connection.userID + " sent username '" + params[1] + ".'");
				ServerCore.handleMessage(connection.getUser().user, connection.getUser().user.username + " has joined.");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}