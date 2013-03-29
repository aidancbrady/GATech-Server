package aidancbrady.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

public final class ServerCore
{
	private static ServerCore instance;
	public final int PORT = 3074;
	public boolean serverRunning = true;
	public boolean programRunning = true;
	public ServerSocket serverSocket;
	public Scanner scanner = new Scanner(System.in);
	public int usersConnected = 0;
	public Map<Integer, ServerConnection> connections = new HashMap<Integer, ServerConnection>();
	public Map<String, User> users = new HashMap<String, User>();
	public ServerGUI theGUI;
	public Timer timer;
	public long TIMEOUT = 300000;
	
	public static void main(String[] args)
	{
		instance = new ServerCore();
		instance.init();
	}
	
	public void init()
	{
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		try {
			FileHandler.read();
			
			new ScheduledTimer().start();
			new SocketListener().start();
			
			System.out.println("Listening on port " + PORT);
			
			theGUI = new ServerGUI();
			
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
	
	public static ServerCore instance()
	{
		return instance;
	}
	
	public int newConnection()
	{
		return ++usersConnected;
	}
	
	public void removeConnection(int userId)
	{
		usersConnected--;
		connections.remove(userId);
	}
	
	public void distributeMessageIgnore(int id, String message)
	{
		for(ServerConnection connection : connections.values())
		{
			if(connection.getUserID() != id)
			{
				try {
					PrintWriter printWriter = new PrintWriter(connection.socketConnection.socket.getOutputStream(), true);
					printWriter.println(message);
				} catch(Exception e) {
					connection.socketConnection.kick();
					theGUI.appendChat("An error occured while notifying other users.");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void distributeMessage(String message)
	{
		for(ServerConnection connection : connections.values())
		{
			try {
				PrintWriter printWriter = new PrintWriter(connection.socketConnection.socket.getOutputStream(), true);
				printWriter.println(message);
			} catch(Exception e) {
				connection.socketConnection.kick();
				theGUI.appendChat("An error occured while notifying other users.");
				e.printStackTrace();
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