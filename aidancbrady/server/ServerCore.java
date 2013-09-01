package aidancbrady.server;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class ServerCore
{
	private static ServerCore instance;
	
	public int port = -1;
	
	public final long TIMEOUT = 300000;
	
	public boolean serverRunning = false;
	public boolean programRunning = true;
	
	public ServerSocket serverSocket;
	
	public int usersConnected = 0;
	
	public Map<Integer, ServerConnection> connections = new HashMap<Integer, ServerConnection>();
	public Map<String, User> users = new HashMap<String, User>();
	
	public ServerGui theGui;
	
	public static void main(String[] args)
	{
		instance = new ServerCore();
		instance.init();
	}
	
	/**
	 * Initiates the server.
	 */
	public void init()
	{
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		try {
			FileHandler.read();
			
			System.out.println("Initializing...");
			
			theGui = new ServerGui();
			
			synchronized(this)
			{
				wait();
			}
			
			System.out.println("Shutting down...");
			
			for(ServerConnection connection : connections.values())
			{
				connection.socketConnection.socket.close();
			}
			
			if(serverSocket != null)
			{
				serverSocket.close();
			}
			
			connections.clear();
			
			FileHandler.write();
			System.out.println("Goodbye!");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the primary ServerCore instance.
	 * @return ServerCore instance
	 */
	public static ServerCore instance()
	{
		return instance;
	}
	
	/**
	 * Increments the usersConnected and returns a new ID for the newly-connected user.
	 * @return new user ID
	 */
	public int newConnection()
	{
		usersConnected++;
		
		int idToUse = 0;
		
		while(true)
		{
			boolean hasThisLoop = false;
			idToUse++;
			
			for(ServerConnection connection : connections.values())
			{
				if(connection.getUserID() == idToUse)
				{
					hasThisLoop = true;
					break;
				}
			}
			
			if(hasThisLoop)
			{
				continue;
			}
			
			return idToUse;
		}
	}
	
	/**
	 * Decrements the usersConnected and removes the connection associated with the defined user ID from the server.
	 * @param userId
	 */
	public void removeConnection(int userId)
	{
		usersConnected--;
		connections.remove(userId);
	}
	
	/**
	 * Distributes a message to all connected users other than the one with the specified ID.
	 * @param id - user ID to ignore
	 * @param message - message to send
	 */
	public void distributeMessageIgnore(int id, String message)
	{
		for(ServerConnection connection : connections.values())
		{
			if(connection.getUserID() != id)
			{
				try {
					connection.socketConnection.printWriter.println(message);
				} catch(Exception e) {
					connection.socketConnection.kick();
					theGui.appendChat("An error occured while notifying other users.");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Distributes a message to all connected users.
	 * @param message - message to send
	 */
	public void distributeMessage(String message)
	{
		for(ServerConnection connection : connections.values())
		{
			try {
				connection.socketConnection.printWriter.println(message);
			} catch(Exception e) {
				connection.socketConnection.kick();
				theGui.appendChat("An error occured while notifying other users.");
				e.printStackTrace();
			}
		}
	}
	
	public void start()
	{
		if(port == -1)
		{
			return;
		}
		
		serverRunning = true;
		
		try {
			serverSocket = new ServerSocket(port);
			
			new ScheduledTimer().start();
			new SocketListener().start();
			
			System.out.println("Server initialized on port " + port);
			
			theGui.setPortButton.setEnabled(false);
			theGui.portEntry.setEnabled(false);
			theGui.startServerButton.setEnabled(false);
			theGui.stopServerButton.setEnabled(true);
		} catch(Exception e) {}
	}
	
	public void stop()
	{
		ServerCore.instance().serverRunning = false;
		
		try {
			for(ServerConnection connection : connections.values())
			{
				connection.socketConnection.socket.close();
			}
			
			if(serverSocket != null)
			{
				serverSocket.close();
			}
			
			System.out.println("Server deinitialized");
			
			theGui.setPortButton.setEnabled(true);
			theGui.portEntry.setEnabled(true);
			theGui.startServerButton.setEnabled(true);
			theGui.stopServerButton.setEnabled(false);
		} catch(Exception e) {}
		
		connections.clear();
	}
}

class ShutdownHook extends Thread
{
	@Override
	public void run()
	{
		try {
			if(ServerCore.instance().serverSocket != null)
			{
				ServerCore.instance().serverSocket.close();
			}
			
			finalize();
		} catch (Throwable t) {
			System.err.println("Error: " + t.getMessage());
			t.printStackTrace();
		}
	}
}