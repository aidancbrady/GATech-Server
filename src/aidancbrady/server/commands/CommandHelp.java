package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.SocketConnection;
import aidancbrady.server.commands.CommandHandler.CommandType;

public class CommandHelp implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter) 
	{
		printWriter.println("-- Help --");
		
		for(CommandType type : CommandType.values())
		{
			if(type.getUsage() != "null")
			{
				printWriter.println(type.getUsage());
			}
		}
		
		printWriter.println("-- End --");
	}
}
