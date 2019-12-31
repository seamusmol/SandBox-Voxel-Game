package ClientServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ClientDataManager.ClientDataManager;
import Configs.ServerSettings;

public class DatagramSender implements Runnable {

	DatagramSocket serverSocket;
	private ExecutorService executor = Executors.newFixedThreadPool(ServerSettings.TCPOutThreadCount);
	boolean isRunning = true;
	
	ClientServerPacketProcessor packageProcessor;
	ClientDataManager clientDataManager;
	
	public DatagramSender(ClientServerPacketProcessor PackageProcessor, ClientDataManager ClientDataManager)
	{
		packageProcessor = PackageProcessor;
		clientDataManager = ClientDataManager;
	}
	
	public void close()
	{
		isRunning = false;
		try 
		{
			executor.awaitTermination(1, TimeUnit.MILLISECONDS);
		} 
		catch (InterruptedException e) 
		{
		}
	}

	@Override
	public void run() 
	{
		try
		{
			serverSocket = new DatagramSocket(new InetSocketAddress(ServerSettings.serverUDPPortOut));

	       	while(isRunning)
	       	{
	       		long startTime = System.currentTimeMillis();
	       		
	       		while(packageProcessor.outboundUDPPackages.size() > 0)
	       		{
	       			executor.execute( new PacketRunnable(packageProcessor.outboundUDPPackages.pop()));
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
			catch (InterruptedException IE) 
			{
		    }
		}
		catch(IOException E)
		{
			System.out.println("UDP Sender Socket Error: " + E);
		}
	}
	
	private class PacketRunnable implements Runnable
	{
		NetworkPacket pack;
		
		public PacketRunnable(NetworkPacket SendPacket)
		{
			pack = SendPacket;
		}

		@Override
		public void run() 
		{
//   			try 
//   			{
//   				InetAddress address = InetAddress.getByName(clientDataManager.getClientIP(pack.getStringValue(3)));
//   				DatagramSocket clientSocket = new DatagramSocket();
//   				byte[] dataArray = pack.toArray();
//   				DatagramPacket data = new DatagramPacket(dataArray, dataArray.length, address, ServerSettings.clientUDPPort);
//				clientSocket.send(data);
//				clientSocket.close();
//			} 
//   			catch (IOException IOE) 
//   			{
//			}
		}
	}
	public void setRunning(boolean IsRunning) 
	{
		isRunning = IsRunning;
	}
}
