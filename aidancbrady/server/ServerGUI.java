package aidancbrady.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
	
	public JList onlineUsersList;
	
	public JList offlineUsersList;
	
	public JPanel serverControlPanel;
	
	public JButton setPortButton;
	
	public JButton startServerButton;
	
	public JButton stopServerButton;
	
	public JTextField portEntry;
	
	public JLabel portLabel;
	
	public JLabel activeLabel;
	
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
				int index = onlineUsersList.getSelectedIndex();
				int offlineIndex = offlineUsersList.getSelectedIndex();
				
				Vector<String> userVector = new Vector<String>();
				Vector<String> offlineVector = new Vector<String>();
				
				for(ServerConnection connection : ServerCore.instance().connections.values())
				{
					userVector.add(connection.getUserID() + ": " + (connection.isAuthenticated() ? connection.user.username : "Guest"));
				}
				
				if(userVector.isEmpty())
				{
					userVector.add("No users online.");
				}
				
				onlineUsersList.setListData(userVector);
				onlineUsersList.setSelectedIndex(index);
				
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
		
		JPanel rightInfoPanel = new JPanel(new BorderLayout());
		rightInfoPanel.setPreferredSize(new Dimension(206, 800));
		
		JPanel leftInfoPanel = new JPanel(new BorderLayout());
		leftInfoPanel.setPreferredSize(new Dimension(206, 800));
		
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
						ServerConnection connection = ServerCore.instance().connections.get(id);
						
						if(connection != null)
						{
							if(!connection.isAuthenticated())
							{
								new ConnectionInfoGUI(id);
							}
							else {
								new CacheInfoGUI(connection.user.username);
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
		leftInfoPanel.add(onlinePane, "North");
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
							new CacheInfoGUI(user.username);
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
		leftInfoPanel.add(offlinePane);
		//End offline user list panel
		
		//Start port setter panel
		serverControlPanel = new JPanel();
		serverControlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Server Control"));
		serverControlPanel.setVisible(true);
		serverControlPanel.setBackground(Color.GRAY);
		serverControlPanel.setFocusable(false);
		serverControlPanel.setPreferredSize(new Dimension(206-15, 290));
		serverControlPanel.setToolTipText("Set this server's active port to a new value.");
		
		activeLabel = Util.getWithFont(new JLabel("Inactive -"), new Font("Arial", Font.BOLD, 14));
		serverControlPanel.add(activeLabel);
		
		portLabel = new JLabel("N/A");
		serverControlPanel.add(portLabel);
		
		portEntry = new JTextField();
		portEntry.setFocusable(true);
		portEntry.setText("");
		portEntry.setPreferredSize(new Dimension(140, 20));
		portEntry.addActionListener(new PortEntryListener());
		serverControlPanel.add(portEntry, "North");
		
		setPortButton = new JButton("Confirm");
		setPortButton.setFocusable(true);
		setPortButton.setPreferredSize(new Dimension(120, 25));
		setPortButton.addActionListener(new SetPortButtonListener());
		
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
		
		rightInfoPanel.add(serverControlPanel, "North");
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
		rightInfoPanel.add(new JScrollPane(statistics));
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
		clearChatButton.setPreferredSize(new Dimension(80, 40));
		chatEntryPanel.add(clearChatButton, "East");
		
		mainPanel.add(chatEntryPanel, "South");
		
		completePanel.add(mainPanel, "Center");
		add(completePanel);
		
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
	
	public class SetPortButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if(!ServerCore.instance().serverRunning)
			{
				String command = portEntry.getText().trim().toLowerCase();
				
				try {
					ServerCore.instance().port = Integer.parseInt(command);
					portLabel.setText("" + ServerCore.instance().port);
					portEntry.setText("");
				} catch(Exception e) {}
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
				
				try {
					ServerCore.instance().port = Integer.parseInt(command);
					portLabel.setText("" + ServerCore.instance().port);
					portEntry.setText("");
				} catch(Exception e) {}
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
							if(commandArgs[1].equals("cache"))
							{
								if(commandArgs.length == 3 && commandArgs[2].equals("empty"))
								{
									int toRemove = ServerCore.instance().cachedUsers.size();
									ServerCore.instance().cachedUsers.clear();
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
