package aidancbrady.server;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener
{
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		System.out.println(arg0.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		System.out.println(arg0.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		System.out.println(arg0.getKeyChar());
	}
}
