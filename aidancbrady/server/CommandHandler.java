package aidancbrady.server;

import java.io.PrintWriter;

import aidancbrady.server.commands.CommandAuthenticate;
import aidancbrady.server.commands.CommandUsername;
import aidancbrady.server.commands.CommandWhois;

public class CommandHandler 
{
	public String command;
	public PrintWriter printWriter;
	
	public CommandHandler(PrintWriter writer, String s)
	{
		printWriter = writer;
		command = s;
	}
	
	public CommandType interpret()
	{
		return CommandType.getFromName(command.split(":")[0]);
	}
	
	public String getCommand()
	{
		return command.split(":")[0];
	}
	
	public static enum CommandType
	{
		USERNAME("user", new CommandUsername()),
		AUTHENTICATE("auth", new CommandAuthenticate()),
		WHOIS("whois", new CommandWhois()),
		UNKNOWN("null", null);
		
		private String name;
		private ICommand command;
		
		public static CommandType getFromName(String name)
		{
			for(CommandType type : values())
			{
				if(type.name.equals(name.replace("/", "")))
				{
					return type;
				}
			}
			
			return UNKNOWN;
		}
		
		public void handle(SocketConnection connection, CommandHandler handler)
		{
			if(equals(UNKNOWN))
			{
				handler.printWriter.println("Unknown command.");
				return;
			}
			
			command.handle(connection, handler.command.split(":"), handler.printWriter);
			FileHandler.saveCaches();
		}
		
		private CommandType(String s, ICommand icommand)
		{
			name = s;
			command = icommand;
		}
	}
}
