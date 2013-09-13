package aidancbrady.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import aidancbrady.server.FileHandler;
import aidancbrady.server.ServerConnection;
import aidancbrady.server.ServerCore;
import aidancbrady.server.User;
import aidancbrady.server.Util;

public class GuiServer extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;
	
	public JTextArea chatBox;
	
	public JList statistics;
	
	public JList onlineUsersList;
	
	public JList offlineUsersList;
	
	public JButton setPortButton;
	
	public JButton startServerButton;
	
	public JButton stopServerButton;
	
	public JTextField portEntry;
	
	public JLabel portLabel;
	
	public JLabel activeLabel;
	
	public JTextField chatField;
	
	public JTextField discussionEntry;
	
	public JLabel discussionLabel;
	
	public JTextField displayNameEntry = null;
	
	public JLabel displayNameLabel;
	
	public ServerMenu serverMenu = new ServerMenu();
	
	public boolean isOpen = true;
	
	public Timer timer;
	
	public GuiServer()
	{
		super("DynamicServer (Server)");
		
		timer = new Timer(100, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				int onlineIndex = onlineUsersList.getSelectedIndex();
				int offlineIndex = offlineUsersList.getSelectedIndex();
				
				Vector<String> userVector = new Vector<String>();
				Vector<String> offlineVector = new Vector<String>();
				
				if(ServerCore.instance().displayName != null)
				{
					userVector.add("0: [Mod] " + ServerCore.instance().displayName);
				}
				else {
					userVector.add("0: [Mod] Undefined");
				}
				
				for(ServerConnection connection : ServerCore.instance().connections.values())
				{
					if(connection.isAuthenticated())
					{
						userVector.add(connection.getUserID() + ": " + connection.user.username);
					}
				}
				
				onlineUsersList.setListData(userVector);
				onlineUsersList.setSelectedIndex(onlineIndex);
				
				if(userVector.size() == 1)
				{
					onlineUsersList.setSelectedIndex(0);
				}
				
				for(User user : ServerCore.instance().cachedUsers.values())
				{
					if(!user.isOnline())
					{
						offlineVector.add(user.username);
					}
				}
				
				if(offlineVector.isEmpty())
				{
					offlineVector.add("No users cached.");
				}
				
				offlineUsersList.setListData(offlineVector);
				offlineUsersList.setSelectedIndex(offlineIndex);
				
				if(offlineVector.size() == 1)
				{
					offlineUsersList.setSelectedIndex(0);
				}
				
				Vector<String> statsVector = new Vector<String>();
				statsVector.add("Active: " + ServerCore.instance().serverRunning);
				statsVector.add("Online count: " + ServerCore.instance().connections.size());
				statsVector.add("Cache count: " + ServerCore.instance().cachedUsers.size());
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
		
		JPanel leftInfoPanel = new JPanel();
		leftInfoPanel.setLayout(new BoxLayout(leftInfoPanel, BoxLayout.Y_AXIS));
		leftInfoPanel.setPreferredSize(new Dimension(206, 800));
		
		JPanel rightInfoPanel = new JPanel(new BorderLayout());
		rightInfoPanel.setPreferredSize(new Dimension(206, 800));
		
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		
		//Start user list panel
		onlineUsersList = new JList();
		
		onlineUsersList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(event.getClickCount() == 2)
				{
					if(!((String)onlineUsersList.getSelectedValue()).equals("No users online."))
					{
						int id = Integer.parseInt(((String)onlineUsersList.getSelectedValue()).split(":")[0]);
						
						if(id != 0)
						{
							ServerConnection connection = ServerCore.instance().connections.get(id);
							
							if(connection != null)
							{
								if(connection.isAuthenticated())
								{
									new GuiCacheInfo(connection.user.username);
								}
							}
						}
					}
				}
			}
		});
		
		onlineUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		onlineUsersList.setBorder(new TitledBorder(new EtchedBorder(), "Online Users"));
		onlineUsersList.setVisible(true);
		onlineUsersList.setFocusable(true);
		onlineUsersList.setEnabled(true);
		onlineUsersList.setSelectionInterval(1, 1);
		onlineUsersList.setBackground(Color.GRAY);
		onlineUsersList.setToolTipText("The users currently connected to this server.");
		JScrollPane onlinePane = new JScrollPane(onlineUsersList);
		onlinePane.setPreferredSize(new Dimension(256-15, 290));
		rightInfoPanel.add(onlinePane, "North");
		//End user list panel
		
		//Start offline user list panel
		offlineUsersList = new JList();
		
		offlineUsersList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(event.getClickCount() == 2)
				{
					if(!((String)offlineUsersList.getSelectedValue()).equals("No users cached."))
					{
						User user = ServerCore.instance().cachedUsers.get((String)offlineUsersList.getSelectedValue());
						
						if(user != null)
						{
							new GuiCacheInfo(user.username);
						}
					}
				}
			}
		});
		
		offlineUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		offlineUsersList.setBorder(new TitledBorder(new EtchedBorder(), "Cached Users"));
		offlineUsersList.setVisible(true);
		offlineUsersList.setFocusable(true);
		offlineUsersList.setEnabled(true);
		offlineUsersList.setSelectionInterval(1, 1);
		offlineUsersList.setBackground(Color.GRAY);
		offlineUsersList.setToolTipText("The users cached in this server's database.");
		JScrollPane offlinePane = new JScrollPane(offlineUsersList);
		offlinePane.setPreferredSize(new Dimension(256-15, 290));
		rightInfoPanel.add(offlinePane);
		//End offline user list panel
		
		//Start port setter panel
		JPanel serverControlPanel = new JPanel();
		serverControlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Server Control"));
		serverControlPanel.setVisible(true);
		serverControlPanel.setBackground(Color.GRAY);
		serverControlPanel.setFocusable(false);
		serverControlPanel.setPreferredSize(new Dimension(206-15, 70));
		serverControlPanel.setToolTipText("Set this server's active port to a new value.");
		
		activeLabel = Util.getWithFont(new JLabel("Inactive -"), new Font("Arial", Font.BOLD, 14));
		serverControlPanel.add(activeLabel);
		
		portLabel = new JLabel("N/A");
		serverControlPanel.add(portLabel);
		
		portEntry = new JTextField();
		portEntry.setFocusable(true);
		portEntry.setText("");
		portEntry.setPreferredSize(new Dimension(140, 20));
		portEntry.addActionListener(new SetPortListener());
		serverControlPanel.add(portEntry, "North");
		
		setPortButton = new JButton("Confirm");
		setPortButton.setFocusable(true);
		setPortButton.setPreferredSize(new Dimension(120, 25));
		setPortButton.addActionListener(new SetPortListener());
		serverControlPanel.add(setPortButton, "Center");
		
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
		serverControlPanel.add(startServerButton, "South");
		
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
		serverControlPanel.add(stopServerButton, "South");
		
		leftInfoPanel.add(serverControlPanel, "North");
		//End port setter panel
		
		//Start display name panel
		JPanel displayNamePanel = new JPanel();
		displayNamePanel.setBorder(new TitledBorder(new EtchedBorder(), "Display Name"));
		displayNamePanel.setVisible(true);
		displayNamePanel.setBackground(Color.GRAY);
		displayNamePanel.setFocusable(false);
		displayNamePanel.setPreferredSize(new Dimension(206-15, 30));
		displayNamePanel.setToolTipText("Set your display name to a new value.");
		
		displayNameLabel = new JLabel("Display Name: Undefined");
		displayNamePanel.add(displayNameLabel);
		
		displayNameEntry = new JTextField();
		displayNameEntry.setFocusable(true);
		displayNameEntry.setText("");
		displayNameEntry.setPreferredSize(new Dimension(140, 20));
		displayNameEntry.addActionListener(new DisplayNameListener());
		displayNamePanel.add(displayNameEntry);
		
		JButton displayNameButton = new JButton("Confirm");
		displayNameButton.setFocusable(true);
		displayNameButton.setPreferredSize(new Dimension(120, 25));
		displayNameButton.addActionListener(new DisplayNameListener());
		displayNamePanel.add(displayNameButton, "Center");
		
		leftInfoPanel.add(displayNamePanel);
		//End display name panel
		
		//Start discussion panel
		JPanel discussionPanel = new JPanel();
		discussionPanel.setBorder(new TitledBorder(new EtchedBorder(), "Discussion"));
		discussionPanel.setVisible(true);
		discussionPanel.setBackground(Color.GRAY);
		discussionPanel.setFocusable(false);
		discussionPanel.setToolTipText("Save and open discussions.");
		
		discussionLabel = new JLabel("Discussion: Undefined");
		discussionPanel.add(discussionLabel, "North");
		
		discussionEntry = new JTextField();
		discussionEntry.setFocusable(true);
		discussionEntry.setText("");
		discussionEntry.setPreferredSize(new Dimension(140, 20));
		discussionEntry.addActionListener(new DiscussionListener());
		discussionPanel.add(discussionEntry, "Center");
		
		JButton discussionButton = new JButton("Set");
		discussionButton.setFocusable(true);
		discussionButton.setPreferredSize(new Dimension(43, 25));
		discussionButton.setEnabled(true);
		discussionButton.addActionListener(new DiscussionListener());
		discussionPanel.add(discussionButton, "Center");
		
		JButton saveButton = new JButton("Save");
		saveButton.setFocusable(true);
		saveButton.setPreferredSize(new Dimension(80, 25));
		saveButton.setEnabled(true);
		saveButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(ServerCore.instance().discussion == null || ServerCore.instance().discussion.isEmpty())
				{
					return;
				}
				
				FileHandler.saveDiscussion();
			}
		});
		discussionPanel.add(saveButton, "South");
		
		JButton openButton = new JButton("Open");
		openButton.setFocusable(true);
		openButton.setPreferredSize(new Dimension(80, 25));
		openButton.setEnabled(true);
		openButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				File discussionsDir = new File(FileHandler.getHomeDirectory() + "/Documents/Discussions");
				
				if(!discussionsDir.exists())
				{
					discussionsDir.mkdir();
				}
				
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(discussionsDir);
				int returnVal = chooser.showOpenDialog(GuiServer.this);
				
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					FileHandler.openDiscussion(chooser.getSelectedFile());
				}
			}
		});
		discussionPanel.add(openButton, "South");
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.setFocusable(true);
		deleteButton.setPreferredSize(new Dimension(120, 25));
		deleteButton.setEnabled(true);
		deleteButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(ServerCore.instance().discussion == null || ServerCore.instance().discussion.equals(""))
				{
					return;
				}
				
				File file = new File(FileHandler.getHomeDirectory() + "/Documents/Discussions/" + ServerCore.instance().discussion + ".disc");
				
				if(file.exists())
				{
					file.delete();
				}
				
				ServerCore.instance().updateDiscussion(null);
				ServerCore.instance().clearChat();
			}
		});
		discussionPanel.add(deleteButton, "South");
		
		leftInfoPanel.add(discussionPanel);
		//End discussion management panel
		
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
		statistics.setToolTipText("Statistics regarding this server.");
		JScrollPane statScroll = new JScrollPane(statistics);
		statScroll.setPreferredSize(new Dimension(206-15, 55));
		leftInfoPanel.add(statScroll, "South");
		//End statistics panel
		
		completePanel.add(leftInfoPanel, "West");
		completePanel.add(rightInfoPanel, "East");
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		
		//Start chat box panel
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setBorder(new TitledBorder(new EtchedBorder(), "Chatbox"));
		chatBox.setAutoscrolls(true);
		chatBox.setBackground(Color.LIGHT_GRAY);
		centerPanel.add(new JScrollPane(chatBox), "Center");
		//End chat box panel
		
		JPanel chatEntryPanel = new JPanel(new BorderLayout());
		chatEntryPanel.setBackground(Color.WHITE);
		
		//Start chat field panel
		chatField = new JTextField();
		chatField.setFocusable(true);
		chatField.setText("");
		chatField.setPreferredSize(new Dimension(120, 40));
		chatField.addActionListener(new ChatBoxListener());
		chatField.setBorder(new TitledBorder(new EtchedBorder(), "Type here to Chat"));
		chatEntryPanel.add(chatField, "Center");
		//End chat field panel
		
		JButton clearChatButton = new JButton("Clear");
		clearChatButton.setVisible(true);
		clearChatButton.setBackground(Color.WHITE);
		clearChatButton.setFocusable(true);
		clearChatButton.setPreferredSize(new Dimension(60, 40));
		clearChatButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				ServerCore.instance().clearChat();
			}
		});
		chatEntryPanel.add(clearChatButton, "East");
		
		centerPanel.add(chatEntryPanel, "South");
		
		completePanel.add(centerPanel, "Center");
		add(completePanel);
		
		setJMenuBar(serverMenu.menuBar);
		//End menu bar
		
		addWindowListener(this);
		setSize(854, 580);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}		

	public void appendChat(String str) 
	{	
		chatBox.append(str+"\n");	
		chatBox.setCaretPosition(chatBox.getText().length() - 1);
	}
	
	public class DisplayNameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			if(displayNameEntry.getText() == null || displayNameEntry.getText().equals(""))
			{
				return;
			}
			
			for(ServerConnection connection : ServerCore.instance().connections.values())
			{
				if(connection.isAuthenticated())
				{
					if(connection.user.username.equals(displayNameEntry.getText()))
					{
						JOptionPane.showMessageDialog(GuiServer.this, "This display name is already in use!", "Warning", JOptionPane.WARNING_MESSAGE);
						displayNameEntry.setText("");
						return;
					}
				}
			}
			
			if(!Util.isValidDisplayName(displayNameEntry.getText()))
			{
				JOptionPane.showMessageDialog(GuiServer.this, "Invalid display name.\nA username must be at most 16 characters long,\nand only alphabetic characters or digits may be used.", "Warning", JOptionPane.WARNING_MESSAGE);
				displayNameEntry.setText("");
				return;
			}
			
			ServerCore.instance().displayName = displayNameEntry.getText();
			displayNameLabel.setText("Display Name: " + displayNameEntry.getText());
			displayNameEntry.setText("");
		}
	}
	
	public class SetPortListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(!ServerCore.instance().serverRunning)
			{
				String command = portEntry.getText().trim().toLowerCase();
				
				if(command == null || command.equals(""))
				{
					return;
				}
				
				try {
					ServerCore.instance().port = Integer.parseInt(command);
					ServerCore.instance().syncDiscussionName(discussionEntry.getText());
					portLabel.setText("" + ServerCore.instance().port);
					portEntry.setText("");
				} catch(Exception e) {
					JOptionPane.showMessageDialog(GuiServer.this, "Invalid characters.", "Warning", JOptionPane.WARNING_MESSAGE);
					portEntry.setText("");
				}
			}
		}
	}
	
	public class DiscussionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			if(discussionEntry.getText().isEmpty())
			{
				return;
			}
			
			if(!Util.isValidDiscussion(discussionEntry.getText()))
			{
				JOptionPane.showMessageDialog(GuiServer.this, "Please enter a valid discussion name.\nDiscussion names have a maximum of 50 characters and include no special characters.", "Warning", JOptionPane.WARNING_MESSAGE);
				discussionEntry.setText("");
				return;
			}
			
			if(ServerCore.instance().discussion != null && !ServerCore.instance().discussion.equals(""))
			{
				ServerCore.instance().clearChat();
			}
			
			ServerCore.instance().updateDiscussion(discussionEntry.getText());
			discussionEntry.setText("");
		}
	}

	public class ChatBoxListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			try {
				chatField.setText("");
				String command = arg0.getActionCommand().trim();
				
				if(command == null || command.equals(""))
				{
					return;
				}
				
				if(command.startsWith("/"))
				{
					command = command.substring(1);
					String[] commandArgs = command.split(" ");
					
					if(command.startsWith("user"))
					{
						if(commandArgs.length > 1)
						{
							if(commandArgs[1].equals("cache"))
							{
								if(commandArgs.length == 3 && commandArgs[2].equals("empty"))
								{
									int toRemove = ServerCore.instance().cachedUsers.size();
									ServerCore.instance().cachedUsers.clear();
									FileHandler.saveCaches();
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
					else {
						appendChat("Unknown command.");
					}
				}
				else {
					if(ServerCore.instance().displayName != null)
					{
						appendChat(ServerCore.instance().displayName + ": " + command);
						ServerCore.instance().distributeMessage(ServerCore.instance().displayName + ": " + command);
					}
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
