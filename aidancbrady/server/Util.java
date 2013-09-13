package aidancbrady.server;

import java.awt.Font;
import java.awt.Toolkit;
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
	
	public static String convertForSync(String chat)
	{
		return chat.replace("\n", "#NL#");
	}
	
	public static boolean isValidDiscussion(String discussion)
	{
		if(discussion.length() > 50)
		{
			return false;
		}
		
		for(Character c : discussion.toCharArray())
		{
			if(!Character.isLetter(c) && !Character.isDigit(c))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static String getMessage(String toSplit)
	{
		StringBuilder builder = new StringBuilder();
		boolean foundSplitter = false;
		
		for(Character c : toSplit.toCharArray())
		{
			if(!foundSplitter)
			{
				if(c.equals(':'))
				{
					foundSplitter = true;
					continue;
				}
			}
			else {
				builder.append(c);
			}
		}
		
		return builder.toString();
	}
	
	public static boolean isValidDisplayName(String displayName)
	{
		if(displayName.length() > 16)
		{
			return false;
		}
		
		for(Character c : displayName.toCharArray())
		{
			if(!Character.isLetter(c) && !Character.isDigit(c))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static JLabel getWithFont(JLabel label, Font font)
	{
		label.setFont(font);
		return label;
	}
	
	public static int getActionKey()
	{
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}
}
