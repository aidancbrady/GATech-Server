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
		return CommandType.getFromName(command.split(" ")[0]);
	}
	
	public String getCommand()
	{
		return command.split(":")[0];
	}
	
	public static enum CommandType
	{
		USERNAME("user", new CommandUsername()),
		AUTHENTICATE("auth", new CommandAuthenticate()),
		DEAUTHENTICATE("deauth", new CommandDeauthenticate()),
		INFO("info", new CommandInfo()),
		DELETE("delete", new CommandDelete()),
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
			
			System.out.println("");
			command.handle(connection, handler.command.split(" "), handler.printWriter);
		}
		
		private CommandType(String s, ICommand icommand)
		{
			name = s;
			command = icommand;
		}
	}
}
