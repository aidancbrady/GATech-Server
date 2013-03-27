package aidancbrady.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ServerGUI extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private JTextArea chat;
	
	private JList<JLabel> statistics;
	
	public JList<JLabel> usersList;
	
	private JTextField text;
	
	public boolean isOpen = true;
	
	public Timer timer;
	
	public ServerGUI()
	{
		super("Aidan's and Dhruval's Chatserver");
		
		timer = new Timer(1000, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				Vector<JLabel> vector = new Vector<JLabel>();
				for(ServerConnection connection : ServerCore.connections.values())
				{
					JLabel label = new JLabel(connection.userID + ": " + (connection.isAuthenticated() ? connection.user.username : "Guest"));
					label.setVisible(true);
					vector.add(label);
				}
				
				usersList.setListData(vector);
			}
		});
		
		timer.start();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception localException) {}
		
		JPanel UI = new JPanel(new BorderLayout());
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		usersList = new JList<JLabel>();
		usersList.setBorder(new TitledBorder(new EtchedBorder(), "Online Users"));
		usersList.setVisible(true);
		infoPanel.add(new JScrollPane(usersList), "Center");
		
		statistics = new JList<JLabel>();
		statistics.setBorder(new TitledBorder(new EtchedBorder(), "Statistics"));
		statistics.setVisible(true);
		infoPanel.add(new JScrollPane(statistics), "South");
		
		UI.add(infoPanel, "West");
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		chat = new JTextArea();
		chat.setEditable(false);
		chat.setBorder(new TitledBorder(new EtchedBorder(), "Chatbox"));
		chat.setAutoscrolls(true);
		mainPanel.add(new JScrollPane(chat), "Center");
		
		text = new JTextField();
		text.setText("");
		text.addActionListener(this);
		text.setBorder(new TitledBorder(new EtchedBorder(), "Type here to Chat"));
		mainPanel.add(text, "South");
		
		UI.add(mainPanel, "Center");
		add(UI);
		
		addWindowListener(this);
		setSize(854,480);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}		

	// append message to the two JTextArea
	// position at the end
	public void appendChat(String str) 
	{	
		chat.append(str+"\n");	
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	
	public void removeUser(int userID)
	{
		usersList.remove(userID);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		try {
			text.setText("");
			String command = arg0.getActionCommand().trim().toLowerCase();
			
			if(command == null || command.equals(""))
			{
				return;
			}
			if(command.equals("stop"))
			{
				ServerCore.serverRunning = false;
				
				for(ServerConnection connection : ServerCore.connections.values())
				{
					connection.socketConnection.kick();
				}
				
				appendChat("Halting oncoming connections.");
			}
			else if(command.equals("start"))
			{
				ServerCore.serverRunning = true;
				appendChat("Start command received.");
			}
			else if(command.equals("quit"))
			{
				appendChat("Quit command received.");
				appendChat("Shutting down...");
				
				ServerCore.distributeMessage("Server closing!");
				
				for(ServerConnection connection : ServerCore.connections.values())
				{
					connection.socketConnection.socket.close();
				}
				
				if(ServerCore.serverSocket != null)
				{
					ServerCore.serverSocket.close();
				}
				
				ServerCore.connections.clear();
				FileHandler.write();
				appendChat("Goodbye!");
				System.exit(0);
			}
			else if(command.startsWith("user"))
			{
				String[] commandArgs = command.split(" ");
				if(commandArgs.length > 1)
				{
					if(commandArgs[1].equals("info"))
					{
						if(commandArgs.length == 3)
						{
							try {
								int userID = Integer.parseInt(commandArgs[2]);
								if(ServerCore.connections.get(userID) != null)
								{
									appendChat("Information on user " + userID + ":");
									appendChat("Username: " + (ServerCore.connections.get(userID).isAuthenticated() ? ServerCore.connections.get(userID).user.username : "unknown"));
									if(ServerCore.connections.get(userID).user != null && !ServerCore.connections.get(userID).user.messages.isEmpty())
									{
										appendChat("Logged messages:");
										for(String message : ServerCore.connections.get(userID).user.messages)
										{
											appendChat(message);
										}
									}
									else if(!ServerCore.connections.get(userID).isAuthenticated() && !ServerCore.connections.get(userID).tempMessages.isEmpty())
									{
										appendChat("Logged messages:");
										for(String message : ServerCore.connections.get(userID).tempMessages)
										{
											appendChat(message);
										}
									}
									else {
										appendChat("No messages found for this user.");
									}
								}	
								else {
									System.err.println("Unable to find database for user '" + userID + ".'");
								}
							} catch(NumberFormatException e1) {
								System.err.println("Invalid characters.");
							}
						}
						else {
							appendChat("Usage: 'user info <ID>'");
						}
					}
					else if(commandArgs[1].equals("cache"))
					{
						if(commandArgs.length == 4 && commandArgs[2].equals("info") && !commandArgs[3].equals(""))
						{
							String name = commandArgs[3];
							if(ServerCore.users.get(name) != null)
							{
								appendChat("Information on user " + name + ":");
								appendChat("Online: " + (ServerCore.users.get(name).isOnline() ? "Yes (ID: " + ServerCore.users.get(name).getConnection().userID + ")" : "No"));
								if(!ServerCore.users.get(name).messages.isEmpty())
								{
									appendChat("Logged messages:");
									for(String message : ServerCore.users.get(name).messages)
									{
										appendChat(message);
									}
								}
								else {
									appendChat("No messages found for this user.");
								}
							}
							else {
								System.err.println("No cache found for user '" + name + ".'");
							}
						}
						else if(commandArgs.length == 3 && commandArgs[2].equals("list"))
						{
							appendChat("Cached users:");
							for(User user : ServerCore.users.values())
							{
								StringBuilder string = new StringBuilder();
								string.append("User " + user.username + " " + (user.isOnline() ? "(Online)" : "(Offline)"));
								appendChat(string.toString());
							}
							if(ServerCore.users.isEmpty())
							{
								appendChat("No users found in cache.");
							}
						}
						else if(commandArgs.length == 4 && commandArgs[2].equals("remove") && !commandArgs[3].equals(""))
						{
							if(!ServerCore.users.containsKey(commandArgs[3]))
							{
								System.err.println("User '" + commandArgs[3] + "' does not exist.");
							}
							else {
								ServerCore.users.remove(commandArgs[3]);
								FileHandler.write();
								appendChat("Successfully removed user '" + commandArgs[3] + "' from cached map.");
							}
						}
						else {
							appendChat("-- Cache Control Panel --");
							appendChat("Command help:");
							appendChat("'user cache info <username>' - gets and returns info from a user's cache.");
							appendChat("'user cache remove <username>' - removes user's cache.");
							appendChat("'user cache list' - lists out all the caches.");
						}
					}
					else if(commandArgs[1].equals("kick"))
					{
						if(commandArgs.length == 3)
						{
							try {
								int userID = Integer.parseInt(commandArgs[2]);
								if(ServerCore.connections.get(userID) != null)
								{
									ServerCore.connections.get(userID).socketConnection.kick();
								}	
								else {
									System.err.println("Unable to find database for user '" + userID + ".'");
								}
							} catch(NumberFormatException e1) {
								System.err.println("Invalid characters.");
							}
						}
						else {
							appendChat("Usage: 'user kick <ID>'");
						}
					}
					else if(commandArgs[1].equals("list"))
					{
						if(commandArgs.length == 2)
						{
							appendChat("Currently connected users:");
							for(ServerConnection connection : ServerCore.connections.values())
							{
								StringBuilder string = new StringBuilder();
								string.append("User " + connection.userID + " " + (connection.isAuthenticated() ? "(" + connection.user.username + ")" : "(no username found)"));
								appendChat(string.toString());
							}
							
							if(ServerCore.connections.isEmpty())
							{
								appendChat("No users found on server.");
							}
						}
						else {
							appendChat("Usage: 'user list'");
						}
					}
				}
				else {
					appendChat("-- User Control Panel --");
					appendChat("Command help:");
					appendChat("'user info <ID>' - displays a user's information.");
					appendChat("'user kick <ID>' - kicks a user from the server.");
					appendChat("'user list' - lists all currently connected users.");
					appendChat("'user cache <params>' - cache control panel.");
				}
			}
			else if(command.equals("help"))
			{
				appendChat("-- Server Control Panel --");
				appendChat("Command help:");
				appendChat("'stop' - stops the server if it is running.");
				appendChat("'quit' - stops the server if it is running and terminates the program.");
				appendChat("'user <params>' - user control panel.");
			}
			else if(command.startsWith("/"))
			{
				appendChat(command.substring(1));
				ServerCore.distributeMessage(command.substring(1));
			}
			else {
				appendChat("Unknown command.");
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		isOpen = false;
		timer.stop();
	}
	
	@Override
	public void windowClosed(WindowEvent e) {}
	
	@Override
	public void windowOpened(WindowEvent e) {}
	
	@Override
	public void windowIconified(WindowEvent e) {}
	
	@Override
	public void windowDeiconified(WindowEvent e) {}
	
	@Override
	public void windowActivated(WindowEvent e) {}
	
	@Override
	public void windowDeactivated(WindowEvent e) {}
}
