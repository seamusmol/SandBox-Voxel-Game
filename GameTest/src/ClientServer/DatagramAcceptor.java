package ClientServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ClientDataManager.ClientDataManager;
import Configs.ServerSettings;

public class DatagramAcceptor implements Runnable {

	DatagramSocket serverSocket;
	private ExecutorService executor = Executors.newFixedThreadPool(ServerSettings.TCPInThreadCount);
	ClientServerPacketProcessor packageProcessor;
	ClientDataManager clientDataManager;
	
	boolean isRunning = true;
	
	public DatagramAcceptor(ClientServerPacketProcessor PackageProcessor, ClientDataManager ClientDataManager)
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
		try 
		{
			serverSocket = new DatagramSocket(new InetSocketAddress(ServerSettings.serverUDPPortIn));
	        byte[] receiveData = new byte[2048];
	        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        
	        while(isRunning)
	        {
	        	long startTime = System.currentTimeMillis();
	        	
	        	serverSocket.receive(receivePacket);
	        	executor.execute( new PacketRunnable(receivePacket));
	        	
	        	long frameTime = System.currentTimeMillis() - startTime;
	        	if(frameTime < 0)
				{
					frameTime = 0;
				}
				else if(frameTime >= ServerSettings.tickTime)
				{
					//server is running below recommended frametime
					frameTime = (long) ServerSettings.tickTime;
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
			System.out.println("UDP Acceptor Socket Error: " + E);
		}
	}
	
	private class PacketRunnable implements Runnable
	{
		DatagramPacket receivePacket;
		
		public PacketRunnable(DatagramPacket ReceivePacket)
		{
			receivePacket = ReceivePacket;
		}
		
		@Override
		public void run() 
		{
			InetAddress IPAddress = receivePacket.getAddress();
//        	DatagramSocket clientSocket = new DatagramSocket(receivePacket.getPort(), receivePacket.getAddress());
			byte[] data = receivePacket.getData();
        	int byteCount = 0;
        	//generate player verification Data
        	//ID(8Bytes) Name(32Bytes) IP(4Bytes)(not in client data)
//        	byte[] playerIDBytes = new byte[8];
//    		for(int i = 0; i < playerIDBytes.length; i++)
//    		{
//    			playerIDBytes[i] = data[byteCount++];
//    		}
//    		String playerID = Arrays.toString(playerIDBytes);
    		
    		byte[] playerNameBytes = new byte[32];
    		for(int i = 0; i < playerNameBytes.length; i++)
    		{
    			playerNameBytes[i] = data[byteCount++];
    		}
    		String playerName = Arrays.toString(playerNameBytes);
    		
        	//data from verification server
        	
    		if(clientDataManager.hasPlayer(playerName))
    		{
    			if(!clientDataManager.isBanned(playerName))
    			{
//    				packageProcessor.addInBoundPacket(new NetworkPacket(data));
//    				clientDataManager.getClientByName(playerName).addInboundPacket(new NetworkPacket(data));
    			}
    		}
      	}
		
	}
	public void setRunning(boolean IsRunning) 
	{
		isRunning = IsRunning;
	}
}
