package aidancbrady.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class GuiConnectionInfo extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private int userID;
	
	private Timer timer;
	
	public JList messageList;
	
	public JLabel idLabel;
	
	public GuiConnectionInfo(int user)
	{
		super("Connection Information");
		
		userID = user;
		setBackground(Color.LIGHT_GRAY);
		setResizable(false);
		setSize(300, 400);
		setPreferredSize(new Dimension(300, 400));
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		writeLabel(new JLabel("Connection Information"), new Font("Arial", Font.BOLD, 14));
		idLabel = writeLabel(new JLabel("User ID: " + getConnection().getUserID()), new Font("Arial", Font.PLAIN, 14));
		
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
		
		JButton clearButton = new JButton("Clear");
		clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		clearButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				getConnection().tempMessages.clear();
				
				if(getConnection().isAuthenticated())
				{
					getConnection().user.messages.clear();
				}
				
				FileHandler.write();
			}
		});
		getContentPane().add(clearButton);
		
		pack();
		
		timer = new Timer(100, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(getConnection().isAuthenticated())
				{
					GuiCacheInfo cacheGui = new GuiCacheInfo(getConnection().user.username);
					cacheGui.setLocation(getLocationOnScreen());
					
					timer.stop();
					
					try {
						dispose();
						setVisible(false);
					} catch(Throwable t) {}
					
					return;
				}
				
				int index = messageList.getSelectedIndex();
				Vector<String> messages = new Vector<String>();
				
				for(String s : getConnection().tempMessages)
				{
					messages.add(s);
				}
				
				messageList.setListData(messages);
				messageList.setSelectedIndex(index);
				
				idLabel.setText("User ID: " + getConnection().getUserID());
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
	
	public ServerConnection getConnection()
	{
		return ServerCore.instance().connections.get(userID);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		getConnection().socketConnection.kick();
		timer.stop();
		
		try {
			dispose();
			setVisible(false);
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
