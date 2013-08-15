package aidancbrady.server;

import java.io.PrintWriter;

public interface ICommand
{
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter);
}
