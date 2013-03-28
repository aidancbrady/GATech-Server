package aidancbrady.server;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketListener extends Thread
{
	@Override
	public void run()
	{
		try {
			ServerCore.instance().serverSocket = new ServerSocket(ServerCore.instance().PORT);
			ServerCore.instance().serverSocket.setReuseAddress(true);
			
			while(ServerCore.instance().serverRunning)
			{
				Socket connection = ServerCore.instance().serverSocket.accept();
				
				if(ServerCore.instance().serverRunning)
				{
					ServerCore.instance().theGUI.appendChat("Connection: " + connection.getInetAddress().toString() + ":" + connection.getPort());
					
					SocketConnection socketConnection = new SocketConnection(ServerCore.instance().newConnection(), connection);
					ServerCore.instance().connections.put(socketConnection.userID, new ServerConnection(socketConnection.userID, socketConnection));
					
					socketConnection.start();
				}
			}
		} catch (Exception e) {
			if(!e.getMessage().trim().toLowerCase().equals("socket closed"))
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
