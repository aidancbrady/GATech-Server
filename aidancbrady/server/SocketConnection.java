package aidancbrady.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnection extends Thread
{
	public Socket socket;
	
	public BufferedReader bufferedReader;
	
	public PrintWriter printWriter;
	
	public int userID;
	
	public boolean kicking = false;
	
	public SocketConnection(int id, Socket accept)
	{
		userID = id;
		socket = accept;
	}
	
	@Override
	public void run()
	{
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			
			printWriter.println("Please identify yourself with a username.");
			
			String readerLine = "";
			boolean doneReading = false;
			
			while((readerLine = bufferedReader.readLine()) != null && !doneReading)
			{
				getServerConnection().timeout = 0;
				if(readerLine.trim().startsWith("/"))
				{
					if(readerLine.trim().contains("/quit"))
					{
						printWriter.println(" -- Disconnecting -- ");
						ServerCore.instance().theGui.appendChat("User " + userID + " has ended the connection.");
						
						if(getServerConnection().isAuthenticated())
						{
							ServerCore.instance().distributeMessageIgnore(userID, "<" + getServerConnection().user.username + " has quit>");
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
							ServerCore.instance().theGui.appendChat(getServerConnection().user.username + ": " + readerLine.trim());
							ServerCore.instance().distributeMessageIgnore(userID, getServerConnection().user.username + ": " + readerLine.trim());
						}
						else {
							ServerCore.instance().theGui.appendChat("Guest: " + readerLine.trim());
							getServerConnection().tempMessages.add(readerLine.trim());
							ServerCore.instance().distributeMessageIgnore(userID, "Guest: " + readerLine.trim());
						}
					}
				} catch(Exception e) {
					printWriter.println("Invalid message.");
					e.printStackTrace();
				}
			}
			
			ServerCore.instance().removeConnection(userID);

			printWriter.println("Successfully closed connection!");
			ServerCore.instance().theGui.appendChat("Closing connection with user '" + userID + ".'");
			
			bufferedReader.close();
			printWriter.close();
			socket.close();
			
			try {
				finalize();
			} catch(Throwable t) {
				ServerCore.instance().theGui.appendChat("Unable to close connection thread! Error: " + t.getMessage());
			}
		} catch(Throwable t) {
			if(!t.getMessage().trim().toLowerCase().equals("socket closed") && !t.getMessage().trim().toLowerCase().equals("Socket is closed"))
			{
				ServerCore.instance().theGui.appendChat("Error: " + t.getMessage());
				t.printStackTrace();
			}
			
			if(!kicking)
			{
				kick();
			}
			
			try {
				finalize();
			} catch(Throwable t1) {}
		}
	}
	
	public void kick()
	{
		kicking = true;
		try {
			printWriter.println("You have been kicked!");
			
			ServerCore.instance().theGui.appendChat("Kicked user '" + userID + ".'");
			ServerCore.instance().removeConnection(userID);
			
			printWriter.close();
			socket.close();
			
			try {
				finalize();
			} catch (Throwable e) {
				ServerCore.instance().theGui.appendChat("Unable to close connection thread! Error: " + e.getMessage());
			}
		} catch(Exception e) {
			if(!e.getMessage().trim().toLowerCase().equals("socket closed") && !e.getMessage().trim().toLowerCase().equals("socket is closed"))
			{
				ServerCore.instance().theGui.appendChat("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public ServerConnection getServerConnection()
	{
		return ServerCore.instance().connections.get(userID);
	}
}
