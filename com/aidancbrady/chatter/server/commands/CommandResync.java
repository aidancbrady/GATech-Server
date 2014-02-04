package com.aidancbrady.chatter.server.commands;

import java.io.PrintWriter;

import com.aidancbrady.chatter.server.ICommand;
import com.aidancbrady.chatter.server.ServerCore;
import com.aidancbrady.chatter.server.SocketConnection;
import com.aidancbrady.chatter.server.Util;

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
