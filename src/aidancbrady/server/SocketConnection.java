package aidancbrady.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import aidancbrady.server.commands.CommandHandler;

public class SocketConnection extends Thread
{
	public Socket connection;
	public int userID;
	
	public SocketConnection(int id, Socket accept)
	{
		userID = id;
		connection = accept;
	}
	
	@Override
	public void run()
	{
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			PrintWriter printWriter = new PrintWriter(connection.getOutputStream(), true);
			
			printWriter.println("Please identify yourself with a username.");
			
			String readerLine = "";
			boolean doneReading = false;
			
			while((readerLine = bufferedReader.readLine()) != null && !doneReading)
			{
				if(readerLine.trim().startsWith("/"))
				{
					if(readerLine.trim().contains("/quit"))
					{
						printWriter.println("Received 'done' notification -- closing connection...");
						System.out.println("User " + userID + " has ended the connection.");
						
						if(getUser().isAuthenticated())
						{
							ServerCore.handleMessage(getUser().user, "<" + getUser().user.username + " has quit>");
						}
						
						doneReading = true;
						break;
					}
					
					CommandHandler handler = new CommandHandler(printWriter, readerLine.trim().toLowerCase().replace("/", ""));
					handler.interpret().handle(this, handler);
					continue;
				}
				
				try {
					if(readerLine != null && readerLine.trim() != "")
					{
						if(getUser().isAuthenticated())
						{
							getUser().user.addMessage(readerLine.trim());
							System.out.println(getUser().user.username + ": " + readerLine.trim());
							ServerCore.handleMessage(getUser().user, getUser().user.username + ": " + readerLine.trim());
						}
						else {
							printWriter.println("Please authenticate before you send a message.");
							System.out.println("User " + userID + " attempted to send a message without authentication.");
						}
					}
					continue;
				} catch(Exception e) {
					printWriter.println("In valid message.");
					e.printStackTrace();
				}
			}
			
			ServerCore.removeConnection(userID);

			printWriter.println("Successfully closed connection!");
			System.out.println("Closing connection with user '" + userID + ".'");
			
			bufferedReader.close();
			printWriter.close();
			connection.close();
			try {
				finalize();
			} catch (Throwable e) {
				System.err.println("Unable to close connection thread! Error: " + e.getMessage());
			}
		} catch(IOException e) {
			if(!e.getMessage().trim().toLowerCase().equals("socket closed"))
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
			
			try {
				connection.close();
				
				try {
					finalize();
				} catch (Throwable e1) {
					System.err.println("Unable to close connection thread! Error: " + e1.getMessage());
				}
			} catch(IOException e1) {
				System.err.println("Could not close connection! Error: " + e1.getMessage());
			}
		}
	}
	
	public void kick()
	{
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			PrintWriter printWriter = new PrintWriter(connection.getOutputStream(), true);
			
			printWriter.println("You have been kicked!");
			System.out.println("Kicked user '" + userID + ".'");
			
			ServerCore.removeConnection(userID);
			bufferedReader.close();
			printWriter.close();
			connection.close();
			
			try {
				finalize();
			} catch (Throwable e) {
				System.err.println("Unable to close connection thread! Error: " + e.getMessage());
			}
		} catch(IOException e) {
			if(!e.getMessage().trim().toLowerCase().equals("socket closed"))
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
			
			try {
				connection.close();
				
				try {
					finalize();
				} catch (Throwable e1) {
					System.err.println("Unable to close connection thread! Error: " + e1.getMessage());
				}
			} catch(IOException e1) {
				System.err.println("Could not close connection! Error: " + e1.getMessage());
			}
		}
	}
	
	public ServerConnection getUser()
	{
		return ServerCore.connections.get(userID);
	}
}
