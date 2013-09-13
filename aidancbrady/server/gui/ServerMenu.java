package aidancbrady.server.gui;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import aidancbrady.server.Util;

public class ServerMenu 
{
	public JMenuBar menuBar = new JMenuBar();
	
	public JMenu serverMenu = new JMenu("Server");
	
	public JMenuItem startItem = new JMenuItem("Start");
	public JMenuItem stopItem = new JMenuItem("Stop");
	
	public JMenu discussionMenu = new JMenu("Discussion");
	
	public JMenuItem saveItem = new JMenuItem("Save");
	public JMenuItem openItem = new JMenuItem("Open");
	
	public ServerMenu()
	{
		startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Util.getActionKey()));
		serverMenu.add(startItem);
		
		stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Util.getActionKey()));
		stopItem.setEnabled(false);
		serverMenu.add(stopItem);
		
		menuBar.add(serverMenu);
		
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Util.getActionKey()));
		discussionMenu.add(saveItem);
		
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Util.getActionKey()));
		discussionMenu.add(openItem);
		
		menuBar.add(discussionMenu);
	}
}
