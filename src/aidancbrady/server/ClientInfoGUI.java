package aidancbrady.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class ClientInfoGUI extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private ServerConnection connection;
	
	private Timer timer;
	
	public JList messageList;
	
	public JLabel usernameLabel;
	
	public JLabel idLabel;
	
	public JLabel authLabel;
	
	public ClientInfoGUI(ServerConnection conn)
	{
		super(conn.isAuthenticated() ? conn.user.username : "Guest");
		connection = conn;
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		setSize(300, 400);
		setPreferredSize(new Dimension(300, 400));
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		writeLabel(new JLabel("User Information"), new Font("Arial", Font.BOLD, 14));
		idLabel = writeLabel(new JLabel("User ID: " + conn.getUserID()), new Font("Arial", Font.PLAIN, 14));
		authLabel = writeLabel(new JLabel("Authenticated: " + conn.isAuthenticated()), new Font("Arial", Font.PLAIN, 14));
		
		if(conn.isAuthenticated())
		{
			usernameLabel = writeLabel(new JLabel("Username: " + conn.user.username), new Font("Arial", Font.PLAIN, 14));
			usernameLabel.setVisible(true);
		}
		else {
			usernameLabel = writeLabel(new JLabel(""), new Font("Arial", Font.PLAIN, 14));
			usernameLabel.setVisible(false);
		}
		
		messageList = new JList();
		JScrollPane scroll = new JScrollPane(messageList);
		scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
		scroll.setPreferredSize(new Dimension(200, 300));
		scroll.setSize(new Dimension(200, 300));
		getContentPane().add(scroll);
		
		JButton button = new JButton("Kick");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.addActionListener(this);
		getContentPane().add(button);
		
		pack();
		
		timer = new Timer(100, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				int index = messageList.getSelectedIndex();
				Vector<String> messages = new Vector<String>();
				
				if(connection.isAuthenticated())
				{
					for(String s : connection.user.messages)
					{
						messages.add(s);
					}
				}
				else {
					for(String s : connection.tempMessages)
					{
						messages.add(s);
					}
				}
				
				messageList.setListData(messages);
				messageList.setSelectedIndex(index);
				
				idLabel.setText("User ID: " + connection.getUserID());
				authLabel.setText("Authenticated: " + connection.isAuthenticated());
				
				if(connection.isAuthenticated())
				{
					usernameLabel.setText("Username: " + connection.user.username);
					usernameLabel.setVisible(true);
				}
				else {
					usernameLabel.setText("");
					usernameLabel.setVisible(false);
				}
			}
		});
		
		timer.start();
	}
	
	public JLabel writeLabel(JLabel label, Font font)
	{
		label.setFont(font);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		getContentPane().add(label, "North");
		return label;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		connection.socketConnection.kick();
		timer.stop();
		
		try {
			finalize();
			dispose();
			setVisible(false);
			pack();
		} catch(Throwable t) {}
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
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
