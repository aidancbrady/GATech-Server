package aidancbrady.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketConnection extends Thread
{
	public Socket socket;
	public int userID;
	
	public SocketConnection(int id, Socket accept)
	{
		userID = id;
		socket = accept;
	}
	
	@Override
	public void run()
	{
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			
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
						
						if(getServerConnection().isAuthenticated())
						{
							ServerCore.distributeMessageIgnore(userID, "<" + getServerConnection().user.username + " has quit>");
						}
						
						doneReading = true;
						break;
					}
					
					CommandHandler handler = new CommandHandler(printWriter, readerLine.trim().toLowerCase().replace("/", ""));
					handler.interpret().handle(this, handler);
					continue;
				}
				
				try {
					if(getServerConnection() != null && readerLine != null && readerLine.trim() != "" && !readerLine.isEmpty())
					{
						if(getServerConnection().isAuthenticated())
						{
							getServerConnection().user.addMessage(readerLine.trim());
							System.out.println(getServerConnection().user.username + ": " + readerLine.trim());
							ServerCore.distributeMessageIgnore(userID, getServerConnection().user.username + ": " + readerLine.trim());
						}
						else {
							System.out.println("Guest: " + readerLine.trim());
							getServerConnection().tempMessages.add(readerLine.trim());
							ServerCore.distributeMessageIgnore(userID, "Guest: " + readerLine.trim());
						}
					}
					continue;
				} catch(Exception e) {
					printWriter.println("Invalid message.");
					e.printStackTrace();
				}
			}
			
			ServerCore.removeConnection(userID);

			printWriter.println("Successfully closed connection!");
			System.out.println("Closing connection with user '" + userID + ".'");
			
			bufferedReader.close();
			printWriter.close();
			socket.close();
			try {
				finalize();
			} catch (Throwable e) {
				System.err.println("Unable to close connection thread! Error: " + e.getMessage());
			}
		} catch(Throwable e) {
			if(!e.getMessage().trim().toLowerCase().equals("socket closed"))
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
			
			kick();
		}
	}
	
	public void kick()
	{
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			
			printWriter.println("You have been kicked!");
			System.out.println("Kicked user '" + userID + ".'");
			
			ServerCore.removeConnection(userID);
			bufferedReader.close();
			printWriter.close();
			socket.close();
			
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
				socket.close();
				
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
	
	public ServerConnection getServerConnection()
	{
		return ServerCore.connections.get(userID);
	}
}
