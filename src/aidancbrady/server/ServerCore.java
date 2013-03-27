package aidancbrady.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JLabel;

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
	public static ServerGUI theGUI;
	
	public static void main(String[] args)
	{
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		try {
			FileHandler.read();
			new SocketListener().start();
			
			System.out.println("Listening on port " + PORT);
			
			theGUI = new ServerGUI();
			
			while(theGUI.isOpen) {}
			
			System.out.println("Shutting down...");
			
			if(serverSocket != null)
			{
				serverSocket.close();
			}
			
			for(ServerConnection connection : connections.values())
			{
				connection.socketConnection.socket.close();
			}
			
			connections.clear();
			
			FileHandler.write();
			System.exit(0);
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static int newConnection()
	{
		return usersConnected++;
	}
	
	public static void removeConnection(int userId)
	{
		usersConnected--;
		connections.remove(userId);
	}
	
	public static void distributeMessageIgnore(int id, String message)
	{
		for(ServerConnection connection : connections.values())
		{
			if(connection.userID != id)
			{
				try {
					PrintWriter printWriter = new PrintWriter(connection.socketConnection.socket.getOutputStream(), true);
					printWriter.println(message);
				} catch(Exception e) {
					connection.socketConnection.kick();
					System.err.println("An error occured while notifying other users.");
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void distributeMessage(String message)
	{
		for(ServerConnection connection : connections.values())
		{
			try {
				PrintWriter printWriter = new PrintWriter(connection.socketConnection.socket.getOutputStream(), true);
				printWriter.println(message);
			} catch(Exception e) {
				connection.socketConnection.kick();
				System.err.println("An error occured while notifying other users.");
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