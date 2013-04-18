package aidancbrady.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ServerGUI extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private JTextArea chat;
	
	private JList statistics;
	
	public JList usersList;
	
	private JTextField text;
	
	public boolean isOpen = true;
	
	public Timer timer;
	
	public ServerGUI()
	{
		super("Georgia Tech Chatserver");
		
		timer = new Timer(100, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				int index = usersList.getSelectedIndex();
				Vector<String> userVector = new Vector<String>();
				for(ServerConnection connection : ServerCore.instance().connections.values())
				{
					userVector.add(connection.getUserID() + ": " + (connection.isAuthenticated() ? connection.user.username : "Guest"));
				}
				
				if(userVector.isEmpty())
				{
					userVector.add("No users online.");
				}
				
				usersList.setListData(userVector);
				usersList.setSelectedIndex(index);
				
				if(userVector.size() == 1)
				{
					usersList.setSelectedIndex(0);
				}
				
				Vector<String> statsVector = new Vector<String>();
				statsVector.add("Online count: " + ServerCore.instance().connections.size());
				statsVector.add("Cache count: " + ServerCore.instance().users.size());
				statsVector.add("Active threads: " + Thread.activeCount());
				statsVector.add("Active memory: " + (int)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000) + "MB");
				statsVector.add("Total memory: " + (int)(Runtime.getRuntime().totalMemory()/1000000) + "MB");
				
				statistics.setListData(statsVector);
			}
		});
		
		timer.start();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception localException) {}
		
		JPanel completePanel = new JPanel(new BorderLayout());
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		
		usersList = new JList();
		
		usersList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(event.getClickCount() == 2)
				{
					if(!((String)usersList.getSelectedValue()).equals("No users online."))
					{
						int id = Integer.parseInt(((String)usersList.getSelectedValue()).split(":")[0]);
						ServerConnection connection = ServerCore.instance().connections.get(id);
						
						if(connection != null)
						{
							new ClientInfoGUI(connection);
						}
					}
				}
			}
		});
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		usersList.setBorder(new TitledBorder(new EtchedBorder(), "Online Users"));
		usersList.setVisible(true);
		usersList.setFocusable(true);
		usersList.setEnabled(true);
		usersList.setSelectionInterval(1, 1);
		usersList.setBackground(Color.GRAY);
		usersList.setPreferredSize(new Dimension(256-15, 286));
		usersList.setToolTipText("The users currently connected to this server.");
		infoPanel.add(new JScrollPane(usersList), "Center");
		
		statistics = new JList();
		
		for(MouseListener listener : statistics.getMouseListeners())
		{
			statistics.removeMouseListener(listener);
		}
		
		statistics.setBorder(new TitledBorder(new EtchedBorder(), "Statistics"));
		statistics.setVisible(true);
		statistics.setBackground(Color.GRAY);
		statistics.setFocusable(false);
		statistics.setPreferredSize(new Dimension(256-15, 164));
		statistics.setToolTipText("Statistics regarding this server.");
		infoPanel.add(new JScrollPane(statistics), "South");
		
		completePanel.add(infoPanel, "West");
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		chat = new JTextArea();
		chat.setEditable(false);
		chat.setBorder(new TitledBorder(new EtchedBorder(), "Chatbox"));
		chat.setAutoscrolls(true);
		chat.setBackground(Color.LIGHT_GRAY);
		mainPanel.add(new JScrollPane(chat), "Center");
		
		text = new JTextField();
		text.setFocusable(true);
		text.setText("");
		text.addActionListener(this);
		text.setBorder(new TitledBorder(new EtchedBorder(), "Type here to Chat"));
		mainPanel.add(text, "South");
		
		completePanel.add(mainPanel, "Center");
		add(completePanel);
		
		addWindowListener(this);
		setSize(854, 480);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}		

	public void appendChat(String str) 
	{	
		chat.append(str+"\n");	
		chat.setCaretPosition(chat.getText().length() - 1);
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
			
			if(command.startsWith("/"))
			{
				command = command.substring(1);
				String[] commandArgs = command.split(" ");
				if(command.equals("stop"))
				{
					ServerCore.instance().serverRunning = false;
					
					for(ServerConnection connection : ServerCore.instance().connections.values())
					{
						connection.socketConnection.kick();
					}
					
					appendChat("Halting oncoming connections.");
				}
				else if(command.equals("start"))
				{
					ServerCore.instance().serverRunning = true;
					new ScheduledTimer().start();
					new SocketListener().start();
					appendChat("Start command received.");
				}
				else if(command.equals("quit"))
				{
					windowClosing(null);
				}
				else if(command.startsWith("user"))
				{
					if(commandArgs.length > 1)
					{
						if(commandArgs[1].equals("info"))
						{
							if(commandArgs.length == 3)
							{
								try {
									int userID = Integer.parseInt(commandArgs[2]);
									if(ServerCore.instance().connections.get(userID) != null)
									{
										appendChat("Information on user " + userID + ":");
										appendChat("Username: " + (ServerCore.instance().connections.get(userID).isAuthenticated() ? ServerCore.instance().connections.get(userID).user.username : "unknown"));
										if(ServerCore.instance().connections.get(userID).user != null && !ServerCore.instance().connections.get(userID).user.messages.isEmpty())
										{
											appendChat("Logged messages:");
											for(String message : ServerCore.instance().connections.get(userID).user.messages)
											{
												appendChat(message);
											}
										}
										else if(!ServerCore.instance().connections.get(userID).isAuthenticated() && !ServerCore.instance().connections.get(userID).tempMessages.isEmpty())
										{
											appendChat("Logged messages:");
											for(String message : ServerCore.instance().connections.get(userID).tempMessages)
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
								if(ServerCore.instance().users.get(name) != null)
								{
									appendChat("Information on user " + name + ":");
									appendChat("Online: " + (ServerCore.instance().users.get(name).isOnline() ? "Yes (ID: " + ServerCore.instance().users.get(name).getConnection().getUserID() + ")" : "No"));
									if(!ServerCore.instance().users.get(name).messages.isEmpty())
									{
										appendChat("Logged messages:");
										for(String message : ServerCore.instance().users.get(name).messages)
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
								for(User user : ServerCore.instance().users.values())
								{
									StringBuilder string = new StringBuilder();
									string.append("User " + user.username + " " + (user.isOnline() ? "(Online)" : "(Offline)"));
									appendChat(string.toString());
								}
								if(ServerCore.instance().users.isEmpty())
								{
									appendChat("No users found in cache.");
								}
							}
							else if(commandArgs.length == 4 && commandArgs[2].equals("remove") && !commandArgs[3].equals(""))
							{
								if(!ServerCore.instance().users.containsKey(commandArgs[3]))
								{
									System.err.println("User '" + commandArgs[3] + "' does not exist.");
								}
								else {
									ServerCore.instance().users.remove(commandArgs[3]);
									FileHandler.write();
									appendChat("Successfully removed user '" + commandArgs[3] + "' from cached map.");
								}
							}
							else if(commandArgs.length == 3 && commandArgs[2].equals("empty"))
							{
								int toRemove = ServerCore.instance().users.size();
								ServerCore.instance().users.clear();
								FileHandler.write();
								appendChat("Removed " + toRemove + " users from cache.");
							}
							else {
								appendChat("-- Cache Control Panel --");
								appendChat("Command help:");
								appendChat("'user cache info <username>' - gets and returns info from a user's cache.");
								appendChat("'user cache remove <username>' - removes user's cache.");
								appendChat("'user cache list' - lists out all the caches.");
								appendChat("'user cache empty' - empties the server cache list.");
							}
						}
						else if(commandArgs[1].equals("kick"))
						{
							if(commandArgs.length == 3)
							{
								try {
									int userID = Integer.parseInt(commandArgs[2]);
									if(ServerCore.instance().connections.get(userID) != null)
									{
										ServerCore.instance().connections.get(userID).socketConnection.kick();
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
								for(ServerConnection connection : ServerCore.instance().connections.values())
								{
									StringBuilder string = new StringBuilder();
									string.append("User " + connection.getUserID() + " " + (connection.isAuthenticated() ? "(" + connection.user.username + ")" : "(no username found)"));
									appendChat(string.toString());
								}
								
								if(ServerCore.instance().connections.isEmpty())
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
				else if(command.equals("clear"))
				{
					chat.setText("Chat cleared.");
					appendChat("");
				}
				else if(command.equals("help"))
				{
					appendChat("-- Server Control Panel --");
					appendChat("Command help:");
					appendChat("'stop' - stops the server if it is running.");
					appendChat("'quit' - stops the server if it is running and terminates the program.");
					appendChat("'user <params>' - user control panel.");
				}
				else {
					appendChat("Unknown command.");
				}
			}
			else {
				appendChat("Console: " + command);
				ServerCore.instance().distributeMessage("Console: " + command);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		timer.stop();
		isOpen = false;
		
		synchronized(ServerCore.instance())
		{
			ServerCore.instance().notify();
		}
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
