package com.aidancbrady.chatter.server;

import java.io.PrintWriter;

import com.aidancbrady.chatter.server.commands.CommandAuthenticate;
import com.aidancbrady.chatter.server.commands.CommandResync;
import com.aidancbrady.chatter.server.commands.CommandUsername;
import com.aidancbrady.chatter.server.commands.CommandWhois;

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
		return CommandType.getFromName(getCommand());
	}
	
	public String getCommand()
	{
		return command.split(":")[0].toLowerCase();
	}
	
	public static enum CommandType
	{
		USERNAME("user", new CommandUsername()),
		AUTHENTICATE("auth", new CommandAuthenticate()),
		WHOIS("whois", new CommandWhois()),
		RESYNC("resync", new CommandResync()),
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
