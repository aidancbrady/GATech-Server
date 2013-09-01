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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ServerGui extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private JTextArea chatBox;
	
	private JList statistics;
	
	public JList usersList;
	
	public JPanel portSetter;
	
	public JButton setPortButton;
	
	public JButton startServerButton;
	
	public JButton stopServerButton;
	
	public JTextField portEntry;
	
	public JLabel portLabel;
	
	public JTextField chatField;
	
	public boolean isOpen = true;
	
	public Timer timer;
	
	public ServerGui()
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
				statsVector.add("Active: " + ServerCore.instance().serverRunning);
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
		
		JPanel rightInfoPanel = new JPanel(new BorderLayout());
		JPanel leftInfoPanel = new JPanel(new BorderLayout());
		
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		
		//Start user list panel
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
		leftInfoPanel.add(new JScrollPane(usersList), "Center");
		//End user list panel
		
		//Start port setter panel
		portSetter = new JPanel();
		portSetter.setBorder(new TitledBorder(new EtchedBorder(), "Set Port"));
		portSetter.setVisible(true);
		portSetter.setBackground(Color.GRAY);
		portSetter.setFocusable(false);
		portSetter.setPreferredSize(new Dimension(206-15, 290));
		portSetter.setToolTipText("Set this server's active port to a new value.");
		
		portLabel = new JLabel("N/A");
		portSetter.add(portLabel, "North");
		
		portEntry = new JTextField();
		portEntry.setFocusable(true);
		portEntry.setText("");
		portEntry.setPreferredSize(new Dimension(140, 20));
		portEntry.addActionListener(new PortEntryListener());
		portSetter.add(portEntry, "North");
		
		setPortButton = new JButton("Confirm");
		setPortButton.setFocusable(true);
		setPortButton.setPreferredSize(new Dimension(100, 25));
		setPortButton.addActionListener(new SetPortButtonListener());
		
		portSetter.add(setPortButton, "Center");
		
		startServerButton = new JButton("Start");
		startServerButton.setFocusable(true);
		startServerButton.setPreferredSize(new Dimension(80, 25));
		startServerButton.setEnabled(true);
		startServerButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ServerCore.instance().start();
			}
		});
		
		stopServerButton = new JButton("Stop");
		stopServerButton.setFocusable(true);
		stopServerButton.setPreferredSize(new Dimension(80, 25));
		stopServerButton.setEnabled(false);
		stopServerButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ServerCore.instance().stop();
			}
		});
		
		portSetter.add(startServerButton, "South");
		portSetter.add(stopServerButton, "South");
		
		rightInfoPanel.add(portSetter, "North");
		//End port setter panel
		
		//Start statistics panel
		statistics = new JList();
		
		for(MouseListener listener : statistics.getMouseListeners())
		{
			statistics.removeMouseListener(listener);
		}
		
		statistics.setBorder(new TitledBorder(new EtchedBorder(), "Statistics"));
		statistics.setVisible(true);
		statistics.setBackground(Color.GRAY);
		statistics.setFocusable(false);
		statistics.setPreferredSize(new Dimension(206-15, 164));
		statistics.setToolTipText("Statistics regarding this server.");
		rightInfoPanel.add(new JScrollPane(statistics), "South");
		//End statistics panel
		
		completePanel.add(rightInfoPanel, "West");
		completePanel.add(leftInfoPanel, "East");
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		//Start chat box panel
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setBorder(new TitledBorder(new EtchedBorder(), "Chatbox"));
		chatBox.setAutoscrolls(true);
		chatBox.setBackground(Color.LIGHT_GRAY);
		mainPanel.add(new JScrollPane(chatBox), "Center");
		//End chat box panel
		
		//Start chat field panel
		chatField = new JTextField();
		chatField.setFocusable(true);
		chatField.setText("");
		chatField.addActionListener(new ChatBoxListener());
		chatField.setBorder(new TitledBorder(new EtchedBorder(), "Type here to Chat"));
		mainPanel.add(chatField, "South");
		//End chat field panel
		
		completePanel.add(mainPanel, "Center");
		add(completePanel);
		
		addWindowListener(this);
		setSize(854, 480);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}		

	public void appendChat(String str) 
	{	
		chatBox.append(str+"\n");	
		chatBox.setCaretPosition(chatBox.getText().length() - 1);
	}
	
	public class SetPortButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(!ServerCore.instance().serverRunning)
			{
				String command = portEntry.getText().trim().toLowerCase();
				portEntry.setText("");
				
				ServerCore.instance().port = Integer.parseInt(command);
				portLabel.setText("" + ServerCore.instance().port);
			}
			else {
				
			}
		}
	}
	
	public class PortEntryListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(!ServerCore.instance().serverRunning)
			{
				String command = portEntry.getText().trim().toLowerCase();
				portEntry.setText("");
				
				ServerCore.instance().port = Integer.parseInt(command);
				portLabel.setText("" + ServerCore.instance().port);
			}
			else {
				
			}
		}
	}

	public class ChatBoxListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			try {
				chatField.setText("");
				String command = arg0.getActionCommand().trim().toLowerCase();
				
				if(command == null || command.equals(""))
				{
					return;
				}
				
				if(command.startsWith("/"))
				{
					command = command.substring(1);
					String[] commandArgs = command.split(" ");
					
					if(command.equals("quit"))
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
											appendChat("Unable to find database for user '" + userID + ".'");
										}
									} catch(NumberFormatException e1) {
										appendChat("Invalid characters.");
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
										appendChat("No cache found for user '" + name + ".'");
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
										appendChat("User '" + commandArgs[3] + "' does not exist.");
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
											appendChat("Unable to find database for user '" + userID + ".'");
										}
									} catch(NumberFormatException e1) {
										appendChat("Invalid characters.");
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
						chatBox.setText("Chat cleared.");
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
				appendChat("Error: " + e.getMessage());
				e.printStackTrace();
			}
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
