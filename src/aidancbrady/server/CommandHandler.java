package aidancbrady.server;

import java.io.PrintWriter;

import aidancbrady.server.commands.CommandAuthenticate;
import aidancbrady.server.commands.CommandDeauthenticate;
import aidancbrady.server.commands.CommandDelete;
import aidancbrady.server.commands.CommandHelp;
import aidancbrady.server.commands.CommandWhois;
import aidancbrady.server.commands.CommandUsername;

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
		USERNAME("username", " /username <username> - change your username", new CommandUsername()),
		AUTHENTICATE("auth", " /auth - authenticate in network", new CommandAuthenticate()),
		DEAUTHENTICATE("deauth", " /deauth - deauthenticate from network", new CommandDeauthenticate()),
		WHOIS("whois", " /whois <id> - get information on a user ID", new CommandWhois()),
		DELETE("delete", " /delete - delete your profile", new CommandDelete()),
		HELP("help", " /help - view this page", new CommandHelp()),
		UNKNOWN("null", "null", null);
		
		private String name;
		private String usage;
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
		
		public String getUsage()
		{
			return usage;
		}
		
		public void handle(SocketConnection connection, CommandHandler handler)
		{
			if(equals(UNKNOWN))
			{
				handler.printWriter.println("Unknown command.");
				return;
			}
			
			command.handle(connection, handler.command.split(" "), handler.printWriter);
			FileHandler.write();
		}
		
		private CommandType(String s, String s1, ICommand icommand)
		{
			name = s;
			usage = s1;
			command = icommand;
		}
	}
}
