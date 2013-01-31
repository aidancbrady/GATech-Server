package aidancbrady.server;

import java.io.PrintWriter;
import java.util.ArrayList;

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
				connection.getUser().user = ServerCore.users.get(params[1]);
				printWriter.println("Welcome back, " + params[1]);
				System.out.println("User '" + params[1] + "' has joined.");
			}
			else {
				connection.getUser().user = new User(params[1], new ArrayList<String>());
				printWriter.println("Username received. Welcome to the AidanServer!");
				System.out.println("User " + connection.userID + " sent username '" + params[1] + ".'");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}
