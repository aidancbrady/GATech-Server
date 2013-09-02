package aidancbrady.server;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;

public final class Util
{
	public static <T> ArrayList<T> genericClone(ArrayList<T> list)
	{
		ArrayList<T> toReturn = new ArrayList<T>();
		
		for(T obj : list)
		{
			toReturn.add(obj);
		}
		
		return toReturn;
	}
	
	public static JLabel getWithFont(JLabel label, Font font)
	{
		label.setFont(font);
		return label;
	}
}
