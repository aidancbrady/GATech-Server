package aidancbrady.server.commands;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;
import aidancbrady.server.User;
import aidancbrady.server.Util;

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
			
			if(connection.getServerConnection().isAuthenticated())
			{
				printWriter.println("You are already authenticated!");
				return;
			}
			
			if(ServerCore.instance().users.containsKey(params[1]))
			{
				if(ServerCore.instance().users.get(params[1]).isOnline())
				{
					printWriter.println("That username is already taken!");
					return;
				}
				
				connection.getServerConnection().user = ServerCore.instance().users.get(params[1]);
				
				ArrayList<String> list = Util.genericClone(connection.getServerConnection().tempMessages);
				
				for(String s : list)
				{
					connection.getServerConnection().user.messages.add(s);
				}
				
				connection.getServerConnection().tempMessages.clear();
				
				printWriter.println("Welcome back, " + params[1]);
				ServerCore.instance().theGUI.appendChat("User '" + params[1] + "' has joined.");
				ServerCore.instance().distributeMessageIgnore(connection.userID, "<" + connection.getServerConnection().user.username + " has joined>");
			}
			else {
				ArrayList<String> newList = new ArrayList<String>(Arrays.asList(new String[connection.getServerConnection().tempMessages.size()]));
				Collections.copy(newList, connection.getServerConnection().tempMessages);
				connection.getServerConnection().user = new User(params[1], Util.genericClone(connection.getServerConnection().tempMessages));
				connection.getServerConnection().tempMessages.clear();
				printWriter.println("Username received. Welcome to the AidanServer!");
				ServerCore.instance().theGUI.appendChat("User " + connection.userID + " sent username '" + params[1] + ".'");
				ServerCore.instance().distributeMessageIgnore(connection.userID, connection.getServerConnection().user.username + " has joined.");
			}
		} catch(Exception e) {
			printWriter.println("Invalid command usage.");
		}
	}
}