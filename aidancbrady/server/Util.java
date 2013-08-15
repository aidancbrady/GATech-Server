package aidancbrady.server;

import java.util.ArrayList;

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
}
