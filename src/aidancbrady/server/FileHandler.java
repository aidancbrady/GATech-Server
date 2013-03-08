package aidancbrady.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

public class FileHandler
{
	public static void read()
	{
		try {
			File file = new File(getHomeDirectory() + File.separator + "Server.txt");
			
			if(!file.exists())
			{
				return;
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String readingLine;
			while((readingLine = reader.readLine()) != null)
			{
				String[] lineSplit = readingLine.split(":");
				
				String username = lineSplit[0];
				ArrayList<String> messagesList = new ArrayList<String>();
				
				if(lineSplit.length == 2 && lineSplit[1] != "")
				{
					String[] messagesSplit = lineSplit[1].split("/");
					for(String s : messagesSplit)
					{
						messagesList.add(s);
					}
				}
				
				ServerCore.users.put(username, new User(username, messagesList));
			}
			
			reader.close();
		} catch(Exception e) {
			System.err.println("Error while reading from Server.txt file:");
			e.printStackTrace();
		}
	}
	
	public static void write()
	{
		try {
			File file = new File(getHomeDirectory() + File.separator + "Server.txt");
			
			if(!file.exists())
			{
				file.createNewFile();
			}
			else {
				file.delete();
				file.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for(Map.Entry<String, User> entry : ServerCore.users.entrySet())
			{
				StringBuilder builder = new StringBuilder(entry.getKey() + ":");
				
				for(String message : entry.getValue().messages)
				{
					builder.append(message + "/");
				}
				
				String toWrite = builder.toString().trim();
				if(toWrite.charAt(toWrite.length()-1) == '/')
				{
					toWrite = toWrite.substring(0, toWrite.length()-1);
				}
				
				writer.append(toWrite);
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		} catch(Exception e) {
			System.err.println("Error while writing to Server.txt file:");
			e.printStackTrace();
		}
	}
	
	public static String getHomeDirectory()
	{
		return System.getProperty("user.home");
	}
}
