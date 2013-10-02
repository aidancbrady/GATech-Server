package aidancbrady.server.commands;

import java.io.PrintWriter;

import aidancbrady.server.ICommand;
import aidancbrady.server.ServerCore;
import aidancbrady.server.SocketConnection;
import aidancbrady.server.Util;

public class CommandResync implements ICommand
{
	@Override
	public void handle(SocketConnection connection, String[] params, PrintWriter printWriter)
	{
		if(ServerCore.instance().discussion == null || ServerCore.instance().discussion.equals(""))
		{
			printWriter.println("/discname:");
		}
		else {
			printWriter.println("/discname:" + ServerCore.instance().discussion);
		}
		
		printWriter.println("/chatlog:" + Util.convertForSync(ServerCore.instance().theGui.chatBox.getText()));
		printWriter.println("/popuser:" + ServerCore.instance().displayName + ":yes");
	}
}
