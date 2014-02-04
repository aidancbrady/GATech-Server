package com.aidancbrady.chatter.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

public class FileHandler
{
	public static final File discussionsDir = new File(getHomeDirectory() + "/Documents/Chatter/Discussions/Server");
	public static final File dataDir = new File(getHomeDirectory() + "/Documents/Chatter/Data/Server");
	
	public static void loadCaches()
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
				
				ServerCore.instance().cachedUsers.put(username, new User(username, messagesList));
			}
			
			reader.close();
		} catch(Exception e) {
			System.err.println("Error while reading from Server.txt file:");
			e.printStackTrace();
		}
	}
	
	public static void saveCaches()
	{
		try {
			File file = new File(getHomeDirectory() + File.separator + "Server.txt");
			
			if(file.exists())
			{
				file.delete();
			}
			
			file.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for(Map.Entry<String, User> entry : ServerCore.instance().cachedUsers.entrySet())
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
	
	public static void saveDiscussion()
	{
		try {
			if(!discussionsDir.exists())
			{
				discussionsDir.mkdir();
			}
			
			File file = new File(discussionsDir.getAbsolutePath() + "/" + ServerCore.instance().discussion + ".disc");
			
			if(file.exists())
			{
				file.delete();
			}
			
			file.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			writer.append(ServerCore.instance().theGui.chatBox.getText());
			
			writer.flush();
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openDiscussion(File file)
	{
		try {
			if(!file.exists())
			{
				return;
			}
			
			if(!file.getAbsolutePath().endsWith(".disc"))
			{
				JOptionPane.showMessageDialog(ServerCore.instance().theGui, "Invalid discussion file.", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			StringBuilder builder = new StringBuilder();
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String readingLine;
			
			while((readingLine = reader.readLine()) != null)
			{
				builder.append(readingLine);
				builder.append("\n");
			}
			
			ServerCore.instance().updateDiscussion(file.getName().replace(".", ":").split(":")[0]);
			ServerCore.instance().theGui.chatBox.setText(builder.toString());
			ServerCore.instance().syncChat();
			
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadProperties()
	{
		try {
			Properties properties = new Properties();
			
			if(!dataDir.exists())
			{
				dataDir.mkdirs();
			}
			
			File file = new File(dataDir.getAbsolutePath() + "/CachedProps.txt");
			
			if(!file.exists())
			{
				return;
			}
			
			properties.load(new FileInputStream(file));
			
			if(properties.containsKey("port"))
			{
				ServerCore.instance().setPort(Integer.parseInt(properties.getProperty("port")));
			}
			
			if(properties.containsKey("displayName"))
			{
				ServerCore.instance().setDisplayName(properties.getProperty("displayName"));
			}
			
			if(properties.containsKey("discussion"))
			{
				String discName = properties.getProperty("discussion");
				File discFile = new File(discussionsDir.getAbsolutePath() + "/" + discName + ".disc");
				
				if(!discFile.exists())
				{
					return;
				}
				
				openDiscussion(discFile);
			}
		} catch(Exception e) {
			System.out.println("An error ocurred while reading properties.");
		}
	}
	
	public static void saveProperties()
	{
		try {
			Properties properties = new Properties();
			
			if(!dataDir.exists())
			{
				dataDir.mkdirs();
			}
			
			File file = new File(dataDir.getAbsolutePath() + "/CachedProps.txt");
			
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			FileOutputStream outputStream = new FileOutputStream(file);
			
			if(ServerCore.instance().port != -1)
			{
				properties.setProperty("port", Integer.toString(ServerCore.instance().port));
			}
			
			if(ServerCore.instance().displayName != null)
			{
				properties.setProperty("displayName", ServerCore.instance().displayName);
			}
			
			if(ServerCore.instance().discussion != null)
			{
				properties.setProperty("discussion", ServerCore.instance().discussion);
			}
			
			properties.store(outputStream, "Server Cached Properties");
			outputStream.close();
		} catch(Exception e) {
			System.out.println("An error ocurred while saving properties.");
		}
	}
}
