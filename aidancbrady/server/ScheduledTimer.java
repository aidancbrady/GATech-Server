package aidancbrady.server;

public class ScheduledTimer extends Thread
{
	@Override
	public void run()
	{
		while(ServerCore.instance().serverRunning)
		{
			for(ServerConnection connection : ServerCore.instance().connections.values())
			{
				if(++connection.timeout >= ServerCore.instance().TIMEOUT)
				{
					ServerCore.instance().theGui.appendChat("User " + connection.getUserID() + " has timed out.");
					connection.socketConnection.kick();
				}
			}
			
			synchronized(this)
			{
				try {
					wait(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
