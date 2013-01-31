package aidancbrady.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class ServerCore
{
	public static final int PORT = 3074;
	public static boolean serverRunning = true;
	public static boolean programRunning = true;
	public static ServerSocket serverSocket;
	public static Scanner scanner = new Scanner(System.in);
	public static int usersConnected = 0;
	public static Map<Integer, ServerConnection> connections = new HashMap<Integer, ServerConnection>();
	public static Map<String, User> users = new HashMap<String, User>();
	
	//Going to try and use port 3073
	public static void main(String[] args)
	{
		try {
			FileHandler.read();
			
			new SocketListener().start();
			
			System.out.println("Server initialized, listening on port " + PORT);
			
			while(programRunning)
			{
				if(scanner.hasNextLine())
				{
					String command = scanner.nextLine().trim().toLowerCase();
					
					if(command.equals("stop"))
					{
						serverRunning = false;
						System.out.println("Stop command received.");
					}
					else if(command.equals("quit"))
					{
						programRunning = false;
						System.out.println("Quit command received.");
						break;
					}
					else if(command.startsWith("user"))
					{
						String[] commandArgs = command.split(" ");
						if(commandArgs.length > 1)
						{
							if(commandArgs[1].equals("info"))
							{
								if(commandArgs.length == 3)
								{
									try {
										int userID = Integer.parseInt(commandArgs[2]);
										if(connections.get(userID) != null)
										{
											System.out.println("Information on user " + userID + ":");
											System.out.println("Username: " + (connections.get(userID).isAuthenticated() ? connections.get(userID).user.username : "unknown"));
											if(connections.get(userID).user != null && !connections.get(userID).user.messages.isEmpty())
											{
												System.out.println("Logged messages:");
												for(String message : connections.get(userID).user.messages)
												{
													System.out.println(message);
												}
											}
											else {
												System.out.println("No messages found for this user.");
											}
										}	
										else {
											System.err.println("Unable to find database for user '" + userID + ".'");
										}
									} catch(NumberFormatException e) {
										System.err.println("Invalid characters.");
									}
								}
								else {
									System.out.println("Usage: 'user info <ID>'");
								}
							}
							else if(commandArgs[1].equals("cache"))
							{
								if(commandArgs.length == 3)
								{
									String name = commandArgs[2];
									if(users.get(name) != null)
									{
										System.out.println("Information on user " + name + ":");
										System.out.println("Online: " + (users.get(name).isOnline() ? "Yes (ID: " + users.get(name).getConnection().userID + ")" : "No"));
										if(!users.get(name).messages.isEmpty())
										{
											System.out.println("Logged messages:");
											for(String message : users.get(name).messages)
											{
												System.out.println(message);
											}
										}
										else {
											System.out.println("No messages found for this user.");
										}
									}
									else {
										System.err.println("Unable to find database for user '" + name + ".'");
									}
								}
								else {
									System.out.println("Usage: 'user cache <username>'");
								}
							}
							else if(commandArgs[1].equals("remove"))
							{
								if(commandArgs.length == 3)
								{
									if(!users.containsKey(commandArgs[2]))
									{
										System.err.println("User '" + commandArgs[2] + "' does not exist.");
									}
									else {
										users.remove(commandArgs[2]);
										System.out.println("Successfully removed user '" + commandArgs[2] + "' from cached map.");
									}
								}
								else {
									System.out.println("Usage: 'user remove <username>'");
								}
							}
							else if(commandArgs[1].equals("kick"))
							{
								if(commandArgs.length == 3)
								{
									try {
										int userID = Integer.parseInt(commandArgs[2]);
										if(connections.get(userID) != null)
										{
											connections.get(userID).connection.kick();
											System.out.println("Kicked user '" + userID + ".'");
										}	
										else {
											System.err.println("Unable to find database for user '" + userID + ".'");
										}
									} catch(NumberFormatException e) {
										System.err.println("Invalid characters.");
									}
								}
								else {
									System.out.println("Usage: 'user kick <ID>'");
								}
							}
							else if(commandArgs[1].equals("list"))
							{
								if(commandArgs.length == 2)
								{
									System.out.println("Currently connected users:");
									for(ServerConnection connection : connections.values())
									{
										StringBuilder string = new StringBuilder();
										string.append("User " + connection.userID + " " + (connection.isAuthenticated() ? "(" + connection.user.username + ")" : "(no username found)"));
										System.out.println(string);
									}
								}
								else {
									System.out.println("Usage: 'user list'");
								}
							}
						}
						else {
							System.out.println("-- User Control Panel --");
							System.out.println("Command help:");
							System.out.println("'user info <ID>' - displays a user's information.");
							System.out.println("'user kick <ID>' - kicks a user from the server.");
							System.out.println("'user list' - lists all currently connected users.");
						}
					}
					else if(command.equals("help"))
					{
						System.out.println("-- Server Command Center --");
						System.out.println("Command help:");
						System.out.println("'stop' - stops the server if it is running.");
						System.out.println("'quit' - stops the server if it is running and terminates the program.");
						System.out.println("'user <params>' - reads information from the user.");
					}
					else {
						System.out.println("Unknown command.");
					}
				}
			}
			System.out.println("Shutting down...");
			
			if(serverSocket != null)
			{
				serverSocket.close();
			}
			
			for(ServerConnection connection : connections.values())
			{
				connection.connection.connection.close();
			}
			
			connections.clear();
			
			FileHandler.write();
			
			System.out.println("Goodbye!");
			System.exit(0);
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static int newConnection()
	{
		usersConnected++;
		return usersConnected;
	}
	
	public static void removeConnection(int userId)
	{
		usersConnected--;
		connections.remove(userId);
	}
	
	public static void handleMessage(User sender, String message)
	{
		for(ServerConnection connection : connections.values())
		{
			if(!connection.isAuthenticated() || connection.user.username != sender.username)
			{
				try {
					PrintWriter printWriter = new PrintWriter(connection.connection.connection.getOutputStream(), true);
					printWriter.println(message);
				} catch(Exception e) {
					System.out.println("An error occured while notifying other users.");
					e.printStackTrace();
				}
			}
		}
	}
}

class ShutdownHook extends Thread
{
	@Override
	public void run()
	{
		try {
			if(ServerCore.serverSocket != null)
			{
				ServerCore.serverSocket.close();
			}
			
			finalize();
		} catch (Throwable t) {
			System.err.println("Error: " + t.getMessage());
			t.printStackTrace();
		}
	}
}