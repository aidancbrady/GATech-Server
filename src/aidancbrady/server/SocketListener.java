package aidancbrady.server;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketListener extends Thread
{
	public void run()
	{
		try {
			ServerCore.serverSocket = new ServerSocket(ServerCore.PORT);
			ServerCore.serverSocket.setReuseAddress(true);
			
			while(ServerCore.serverRunning)
			{
				Socket connection = ServerCore.serverSocket.accept();
				System.out.println("Connection: " + connection.getInetAddress().toString() + ":" + connection.getPort());
				
				SocketConnection socketConnection = new SocketConnection(ServerCore.newConnection(), connection);
				ServerCore.connections.put(socketConnection.userID, new ServerConnection(socketConnection.userID, socketConnection));
				
				socketConnection.start();
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
