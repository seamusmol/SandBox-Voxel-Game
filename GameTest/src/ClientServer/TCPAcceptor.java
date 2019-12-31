package ClientServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ClientDataManager.ClientDataManager;
import Configs.ServerSettings;

public class TCPAcceptor implements Runnable{

	private ExecutorService executor = Executors.newFixedThreadPool(ServerSettings.TCPInThreadCount);
	private boolean isRunning = true;
	ClientServerPacketProcessor packageProcessor;
	ClientDataManager clientDataManager;
	
	public TCPAcceptor(ClientServerPacketProcessor PackageProcessor,ClientDataManager ClientDataManager)
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
		ServerSocket serverSocket = null;
		try 
		{
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(ServerSettings.serverTCPPortIn));
		} 
		catch (IOException e) 
		{
		}
		
		while(isRunning)
		{
			System.out.println("active");
			long startTime = System.currentTimeMillis();
			Socket clientSocket = null;
			try
			{
				clientSocket = serverSocket.accept();
			}
			catch(IOException e)
			{
				if(!isRunning)
				{
					break;
				}
				throw new RuntimeException("Cannot Accept New Client", e);
			}
			
			executor.execute( new StreamRunnable( clientSocket.getInetAddress(), readData(clientSocket)));
			
			long frameTime = System.currentTimeMillis() - startTime;
			if(frameTime < 0)
			{
				frameTime = 0;
			}
			try 
			{
				Thread.sleep(frameTime - frameTime);
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
	
	public byte[] readData(Socket clientSocket)
	{
		byte[] data = null;
		try{
			InputStream input = clientSocket.getInputStream();
			List<Byte> dataList = new ArrayList<Byte>();
			int curByte = 0;
			while((curByte = input.read()) != -1)
			{
				dataList.add((byte)curByte);
			}
			data = new byte[dataList.size()];
			for(int i = 0; i < data.length; i++)
			{
				data[i] = dataList.get(i);
			}
			input.close();
			clientSocket.close();
		} 
		catch (IOException e) 
		{
			System.out.println("TCP Socket Error");
		}
		return data;
	}
	
	private class StreamRunnable implements Runnable
	{
		InetAddress ipAddress;
		byte[] data;
		public StreamRunnable(InetAddress Address, byte[] Data)
		{
			ipAddress = Address;
			data = Data;
		}
		
		@Override
		public void run() 
		{
//			int byteCount = 0;
//			//generate player verification Data
//			//ID(8Bytes) Name(32Bytes) IP(4Bytes)(not in client data)
//			int userNameByteLength =  data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
//			byte[] userNameBytes = new byte[userNameByteLength];
//			for(int i = 0; i < userNameBytes.length; i++)
//			{
//				userNameBytes[i] = data[byteCount++];
//			}
//			String playerName = new String(userNameBytes);
//			
//			String IP = ipAddress.toString().replace("/", "");
//			System.out.println(IP);
//			//data from verification server
//			if(IP.equals("192.168.1.1") && !clientDataManager.hasPlayer(playerName))
//			{
//				System.out.println("New Player: " + playerName);
//				//get playerIP
//				int ipByteLength =  data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
//				byte[] ipBytes = new byte[ipByteLength];
//				for(int i = 0; i < userNameBytes.length; i++)
//				{
//					ipBytes[i] = data[byteCount++];
//				}
//				
//				clientDataManager.addNewClient(playerName, IP);
////				packageProcessor.addOutBoundTCPPacket( new NetworkPacket(new byte[0],IP, -1+"", "true", "valid"));
//				clientDataManager.getClientByName(playerName).addOutBoundTCP(new NetworkPacket(new byte[0],IP, -1+"", "true", "valid"));
//			}
//			else
//			{
//				if(clientDataManager.hasPlayer(playerName))
//				{
//					if(clientDataManager.isBanned(playerName))
//					{
////						packageProcessor.addInBoundPacket(new NetworkPacket(data));
//						clientDataManager.getClientByName(playerName).addInboundPacket(new NetworkPacket(data));
//					}
//					else
//					{
//						//player banned
//						//send banned message
////						packageProcessor.addOutBoundTCPPacket( new NetworkPacket(new byte[0],IP, 2+"", "false", "banned"));
//						//send tcp packet back here.
//						
//					}
//				}
//				else
//				{
//					//attempted login from invalid ID/IP
////					packageProcessor.addOutBoundTCPPacket( new NetworkPacket(new byte[0],IP, 2+"", "false", "invalid"));
//					//send tcp packet back here.
//				}
//			}
		}
	}

	public void setRunning(boolean IsRunning) 
	{
		isRunning = IsRunning;
	}
	
	
}
