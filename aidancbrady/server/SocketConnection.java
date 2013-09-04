package aidancbrady.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

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
			
			printWriter.println("Welcome to the AidanServer!");
			
			String readerLine = "";
			boolean doneReading = false;
			
			while((readerLine = bufferedReader.readLine()) != null && !doneReading)
			{
				getServerConnection().timeout = 0;
				
				if(readerLine.trim().startsWith("/"))
				{
					if(readerLine.trim().startsWith("/msg"))
					{
						String message = Util.getMessage(readerLine.trim()).trim();
						
						if(getServerConnection() != null && !message.isEmpty())
						{
							if(getServerConnection().isAuthenticated())
							{
								getServerConnection().user.addMessage(message);
								ServerCore.instance().theGui.appendChat(getServerConnection().user.username + ": " + message);
								ServerCore.instance().distributeMessage(getServerConnection().user.username + ": " + message);
							}
						}
						
						continue;
					}
					
					CommandHandler handler = new CommandHandler(printWriter, readerLine.trim().toLowerCase().replace("/", ""));
					handler.interpret().handle(this, handler);
					
					continue;
				}
			}
			
			if(getServerConnection() != null && getServerConnection().isAuthenticated())
			{
				ServerCore.instance().theGui.appendChat("Closing connection with " + getServerConnection().user.username + ".");
				ServerCore.instance().distributeMessage("<" + getServerConnection().user.username);
			}
			
			ServerCore.instance().removeConnection(userID);

			printWriter.println("Successfully closed connection!");
			
			bufferedReader.close();
			printWriter.close();
			socket.close();
		
			finalize();
		} catch(SocketException e) {
		} catch(Throwable t) {
			ServerCore.instance().theGui.appendChat("Error: " + t.getMessage());
			t.printStackTrace();
			
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
