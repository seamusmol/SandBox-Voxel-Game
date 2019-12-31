package ClientServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ClientDataManager.ClientDataManager;
import Configs.ServerSettings;

public class TCPSender implements Runnable{

	private ExecutorService executor = Executors.newFixedThreadPool(ServerSettings.TCPOutThreadCount);
	ClientServerPacketProcessor packageProcessor;
	ClientDataManager clientDataManager;
	
	boolean isRunning = true;
	
	public TCPSender(ClientServerPacketProcessor PackageProcessor, ClientDataManager ClientDataManager)
	{
		packageProcessor = PackageProcessor;
		clientDataManager = ClientDataManager;
	}
	
	public void close()
	{
		isRunning = false;
	}
	
	@Override
	public void run() 
	{
		while(isRunning)
		{
			long startTime = System.currentTimeMillis();
			
			while(packageProcessor.outboundTCPPackages.size() > 0)
			{
				System.out.println("Sending Package");
				executor.execute( new StreamRunnable(packageProcessor.outboundTCPPackages.pop()));
			}
			long frameTime = System.currentTimeMillis() - startTime;
			if(frameTime < 0)
			{
				frameTime = 0;
			}
			try 
			{
				Thread.sleep((long) (ServerSettings.tickTime - frameTime));
			} 
			catch(InterruptedException e) 
			{
			}
		}
		executor.shutdown();
		try 
		{
			executor.awaitTermination(1, TimeUnit.MILLISECONDS);
	    } 
		catch (InterruptedException e) 
		{
	    }
		
	}

	private class StreamRunnable implements Runnable
	{
		NetworkPacket pack;
		public StreamRunnable(NetworkPacket Pack)
		{
			pack = Pack;
		}
		
		@Override
		public void run() 
		{
//			try 
//			{
////				ServerSocket serverSocket = new ServerSocket(ServerSettings.serverTCPPortOut);
//				System.out.println("Sending to Port: " + ServerSettings.clientTCPPort);
//				
//				String IP = "";
//				switch(pack.getIntValue(1))
//				{
//					case -1:
//						//initial login
//						IP = pack.getStringValue(0);
//						System.out.println(IP + "-" + clientDataManager.getClientByName("user").getIP());
//						clientDataManager.playerLoggedIn(clientDataManager.getPlayerByIP(IP).getName());
//						break;
//					case 2:
//						//failed login
//						IP = pack.getStringValue(3);
//						break;
//					default:
//						//standard communication
//						IP = clientDataManager.getClientByName(pack.getStringValue(3)).getIP();
//						break;
//				}
//				//local IP
//				IP = "192.168.1.66";
//				
//				Socket clientSocket = new Socket(IP, ServerSettings.clientTCPPort);
//				clientSocket.setSoTimeout(10);
//				OutputStream outStream = clientSocket.getOutputStream();
//				outStream.write(pack.toArray());
//				outStream.flush();
//				outStream.close();
//				clientSocket.close();
//				
////				System.out.println("Package Sent: " + pack.getPlayerDestination() + "-" + pack.getPacketID());
//			}
//			catch(IOException E)
//			{
//				System.out.println("TCP Send Socket Error: " + E);
//			}
		}
	}
	public void setRunning(boolean IsRunning) 
	{
		isRunning = IsRunning;
	}
}
