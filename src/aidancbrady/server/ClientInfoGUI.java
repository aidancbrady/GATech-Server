package aidancbrady.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ClientInfoGUI extends JFrame
{
	private ServerConnection connection;
	
	public ClientInfoGUI(ServerConnection conn)
	{
		super(conn.isAuthenticated() ? conn.user.username : "Guest");
		connection = conn;
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		setSize(300, 400);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JList list = new JList();
		
		for(MouseListener listener : list.getMouseListeners())
		{
			list.removeMouseListener(listener);
		}
		
		list.setVisible(true);
		list.setBackground(Color.GRAY);
		list.setFocusable(false);
		list.setPreferredSize(new Dimension(256-15, 164));
		
		Vector<String> data = new Vector<String>();
		
		data.add("User ID: " + conn.getUserID());
		data.add("Authenticated: " + conn.isAuthenticated());
		
		if(!conn.isAuthenticated())
		{
			data.add("Temporary messages: " + conn.tempMessages.size());
		}
		else {
			data.add("Messages: " + conn.user.messages);
		}
		
		list.setListData(data);
		add(list);
	}
}
