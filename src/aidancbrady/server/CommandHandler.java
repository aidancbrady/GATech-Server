package aidancbrady.server;

import java.io.PrintWriter;

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
		final String split = command.split(":")[0];
		switch(split)
		{
			case "msg":
				return CommandType.MESSAGE;
			case "user":
				return CommandType.USERNAME;
			case "done":
				return CommandType.DONE;
			case "info":
				return CommandType.INFO;
			default:
				return CommandType.UNKNOWN;
		}
	}
	
	public String getCommand()
	{
		return command.split(":")[0];
	}
	
	public String getText()
	{
		if(interpret().hasSplitter())
		{
			return command.split(":")[1].trim();
		}
		
		return null;
	}
	
	public static enum CommandType
	{
		MESSAGE(true),
		USERNAME(true),
		DONE(false),
		INFO(true),
		UNKNOWN(false);
		
		private boolean hasSplitter;
		
		public boolean hasSplitter()
		{
			return hasSplitter;
		}
		
		private CommandType(boolean splitter)
		{
			hasSplitter = splitter;
		}
	}
}
