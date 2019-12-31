package network;

import java.net.SocketAddress;
import java.util.Stack;

import com.jme3.app.state.AbstractAppState;

import ClientMob.EntityManager;
import ClientServer.ClientServerPacketProcessor;
import ClientServer.NetworkPacket;
import Configs.ServerSettings;
import Console.Parser;
import Input.PlayerCamera;
import Main.Main;
import Mob.Player;
import Mob.PlayerSettings;
import RigidBodies.RigidBodyManager;
import levelgen.ChunkManager;

public class ClientPacketProcessor extends AbstractAppState{

	Stack<NetworkPacket> inboundPackages = new Stack<NetworkPacket>();
	
	Stack<NetworkPacket> outboundUDPPackets = new Stack<NetworkPacket>();
	Stack<NetworkPacket> outboundTCPPackets = new Stack<NetworkPacket>();
	
	ClientMessageHandler messageHandler;
	
	SocketAddress serverAddress;
	
	final String verificationServerIP = "174.45.42.126";
	final int verificationServerPort = 2000;
	
	String serverIP;
	int serverPort;
	
	int clientUDPPort = 2301;
	int clientTCPPort = 2300;
	
	ClientServerPacketProcessor clientServerPacketProcessor;
	
	Main main;
	
	PlayerCamera playerCamera;
	Player player;
	
	ChunkManager chunkManager;
	RigidBodyManager rigidBodyManager;
	EntityManager entityManager;
	
	long lastPing;
	int pingTime = 1000;
	
	public ClientPacketProcessor(Main Main, String IPAddress, int Port)
	{
		serverIP = IPAddress;
		serverPort = Port;
		messageHandler = new ClientMessageHandler(this);
		main = Main;
		player = new Player(Main, this);
		playerCamera = new PlayerCamera(Main, player);
		chunkManager = new ChunkManager(main.getPlayerSettings(), playerCamera);
		
		rigidBodyManager = new RigidBodyManager(main.getPlayerSettings(), playerCamera);
		player.init(chunkManager, rigidBodyManager, playerCamera);
	}
	
	public void init(ClientServerPacketProcessor ClientServerPacketProcessor)
	{
		if(ClientServerPacketProcessor != null)
		{
			clientServerPacketProcessor = ClientServerPacketProcessor;
		}
	}
	
	@Override
	public void cleanup()
	{
            //clientServerPacketProcessor.cleanup();
            rigidBodyManager.close();
            chunkManager.close();
            playerCamera.cleanup();
            
            if(clientServerPacketProcessor != null)
    		{
            	clientServerPacketProcessor.cleanup();
            }
            
	}
	
	@Override
	public void update(float tpf)
	{
		processIncomingPackages();
		if(ServerSettings.networkMode == 0)
		{
			//reroute packets to client packet processor
			while(outboundUDPPackets.size() > 0)
			{
				clientServerPacketProcessor.addInBoundPacket( outboundUDPPackets.pop());
				
			}
			while(outboundTCPPackets.size() > 0)
			{
				clientServerPacketProcessor.addInBoundPacket( outboundTCPPackets.pop());
			}
		}
		else
		{
//			processPing();
//			
//			listenTCP();
//			listenUDP();
//			
//			sendUDPPackage();
//			sendTCPPackage();
		}
		player.update(tpf);
		playerCamera.update();
	}
	
	//0=playername,1=playerpacketID
	public void processIncomingPackages()
	{
		while(inboundPackages.size() > 0)
		{
			NetworkPacket pack = inboundPackages.pop();
			
			switch(pack.getIndentifierData()[0])
			{
				case 0:
					//ping
					lastPing = System.currentTimeMillis();			
//						addOutBoundTCPPacket( pack);
					break;
				case 1:
					//messages
					messageHandler.processMessage(pack);
					break;
				case 2:
					//disconnection
					Parser.parseString("close ");
					break;
				case 3:
					//terrain packet
					switch(pack.getIndentifierData()[1])
					{
						case 0:
							chunkManager.addChunk(pack.getData());
							break;
						case 1:
							chunkManager.updateBackGround(pack.getData());
							break;
					}
					break;
				case 4:
					//playerdata
					player.unpackSnapShot(pack.getData());
					break;
				case 5:
					switch(pack.getIndentifierData()[1])
					{
						case 0:
							//add entity
							entityManager.addEntity(pack.getData());
							break;
						case 1:
							//modify entity
							entityManager.ModifyEntity(pack.getData());
							break;
					}
					break;
				case 6:
					switch(pack.getIndentifierData()[1])
					{
						case 0:
							rigidBodyManager.addRigidBodies(pack.getData());
							break;
						case 1:
							rigidBodyManager.updateRigidBodyMovement(pack);
							break;
					}
					break;
			}
		}
	}
	
	public void processPing()
	{
		if(System.currentTimeMillis() - lastPing > pingTime)
		{
			Parser.parseString("close Lost_Connection");
		}
	}
	
//	public void listenUDP()
//	{
//		DatagramSocket clientSocket = null;
//				
//		byte[] buffer = new byte[2048];
//		DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
//		try 
//		{
//			clientSocket = new DatagramSocket();
//			clientSocket.bind(new InetSocketAddress(clientUDPPort));
//			
//			System.out.println("Listening");
//			clientSocket.receive(packet);
//			
//			NetworkPacket pack = new NetworkPacket(buffer);
//			inboundPackages.add(pack);
//			
//			clientSocket.close();
//        }
//		catch(SocketTimeoutException STE) 
//		{
//		}
//		catch(IOException IOE)
//		{
//		}
//	}

//	public void listenTCP()
//	{
//		ServerSocket clientSocket = null;
//		Socket serverSocket = null;
//		try
//		{
//			clientSocket = new ServerSocket();
//			clientSocket.bind(new InetSocketAddress(clientTCPPort));
//			clientSocket.setSoTimeout(1);
//			serverSocket = clientSocket.accept();
//			
//			InputStream input = serverSocket.getInputStream();
//			List<Byte> dataList = new ArrayList<Byte>();
//			int curByte = 0;
//			while((curByte = input.read()) != -1)
//			{
//				dataList.add((byte)curByte);
//			}
//			byte[] data = new byte[dataList.size()];
//			for(int i = 0; i < data.length; i++)
//			{
//				data[i] = dataList.get(i);
//			}
//			input.close();
//			
//			serverSocket.close();
//			clientSocket.close();
//			
//			addInBoundPacket( new NetworkPacket(data));
//			
//		}
//		catch(SocketTimeoutException STE) 
//		{
//		}
//		catch(IOException IOE)
//		{
//		}
//	}
	
//	public void sendUDPPackage()
//	{
//		while(outboundUDPPackets.size() > 0)
//		{
//			NetworkPacket pack = outboundUDPPackets.pop();
//   			DatagramPacket data = new DatagramPacket(pack.toArray(), pack.toArray().length);
//   			DatagramSocket serverSocket = null;
//   					
//   			try 
//   			{
//   				serverSocket = new DatagramSocket();
//   				serverSocket.bind( new InetSocketAddress(serverIP, serverPort));
//   				
//   				serverSocket.send(data);
//   				serverSocket.close();
//			} 
//   			catch(IOException IOE) 
//   			{
//			}
//		}
//	}
//	
//	public void sendTCPPackage()
//	{
//		while(outboundTCPPackets.size() > 0)
//		{
//			NetworkPacket pack = outboundTCPPackets.pop();
//			OutputStream outStream;
//			
//			Socket serverSocket = null;
//			try 
//			{
//				serverSocket = new Socket();
//				serverSocket.bind(new InetSocketAddress(serverIP,serverPort));
//				outStream = serverSocket.getOutputStream();
//				outStream.write(pack.toArray());
//				outStream.flush();
//				outStream.close();
//				serverSocket.close();
//			} 
//			catch (IOException IOE) 
//			{
//			}
//		}
//	}
	
//	public boolean login()
//	{
//		//send data to verification server
//		//name, password
//		sendVerification();
//		return attemptLogin();
//	}
	
	//send message to verification
//	private void sendVerification()
//	{
//		try 
//		{
//			NetworkPacket pack = new NetworkPacket(new byte[0], SettingsLibrary.playerName, SettingsLibrary.playerPassword, serverIP,serverPort + "");
//			OutputStream outStream;
//			
//			Socket serverSocket = new Socket(verificationServerIP, verificationServerPort);
//			serverSocket.setSoTimeout(1000);
//			outStream = serverSocket.getOutputStream();
//			outStream.write(pack.toArray());
//			outStream.flush();
//			outStream.close();
//			serverSocket.close();
//			System.out.println("Sent packet to verification server");
//		} 
//		catch (IOException IOE) 
//		{
//		}
//	}
//	
//	private boolean attemptLogin()
//	{
//		boolean hasLogin = false;
//		NetworkPacket result = null;
//		try 
//		{
//			//send login packet
//			NetworkPacket pack = new NetworkPacket(new byte[0], SettingsLibrary.playerName, 1 + "", "", "");
//			OutputStream outStream;
//			
//			Socket serverSocket = new Socket(serverIP, serverPort);
//			serverSocket.setSoTimeout(1000);
//			outStream = serverSocket.getOutputStream();
//			outStream.write(pack.toArray());
//			outStream.flush();
//			outStream.close();
//			serverSocket.close();
//			System.out.println("Sent packet to game server");
//		} 
//		catch (IOException IOE) 
//		{
//		}
//		Socket verifySocket;
//		try {
//			ServerSocket clientTCPSocketIn = new ServerSocket();
//			clientTCPSocketIn.bind(new InetSocketAddress(clientTCPPort));
//			clientTCPSocketIn.setSoTimeout(1000);
//			verifySocket = clientTCPSocketIn.accept();
//			System.out.println("received reply connection");
//			InputStream inStream = verifySocket.getInputStream();
//			List<Byte> dataList = new ArrayList<Byte>();
//			int curByte = 0;
//			while((curByte = inStream.read()) != -1)
//			{
//				dataList.add((byte)curByte);
//			}
//			byte[] data = new byte[dataList.size()];
//			for(int i = 0; i < data.length; i++)
//			{
//				data[i] = dataList.get(i);
//			}
//			inStream.close();
//			verifySocket.close();
//			clientTCPSocketIn.close();
//			result = new NetworkPacket(data);
//			hasLogin = Boolean.valueOf(result.getStringValue(2));
//			System.out.println("received result from game server: " + result.getStringValue(2));
//		} 
//		catch(SocketTimeoutException STE) 
//		{
//			System.out.println("socket timeout");
//			return false;
//		}
//		catch (IOException e) 
//		{
//		}
//		
//		return hasLogin;
//	}
//	
//	public boolean containsPacketType(int PacketID)
//	{
//		for(int i = 0; i < outboundTCPPackets.size(); i++)
//		{
//			if(outboundTCPPackets.get(i).getIntValue(1) == PacketID)
//			{
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public boolean canSend(int PacketID, long TimeStamp)
//	{
//		for(int i = 0; i < outboundTCPPackets.size(); i++)
//		{
//			if(outboundTCPPackets.get(i).getIntValue(1) == PacketID)
//			{
////				if(outboundTCPPackets.get(i).getLongValue() == TimeStamp)
////				{
////					return false;
////				}
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	private boolean hasLoaded() 
//	{
//		long startTime = System.nanoTime();
//		while(true)
//		{
//			boolean hasData = true;
//			if(player.getPosition() == null)
//			{
//				hasData = false;
//			}
//			
//			if(hasData)
//			{
//				return true;
//			}
//			else if((System.nanoTime() - startTime)/1000000 > 1000)
//			{
//				return false;
//			}
//		}
//	}
	
	
	public void addInBoundPacket(NetworkPacket Pack)
	{
		if(Pack.getIndentifierData()[0] != 4)
		{
		}
		inboundPackages.add(Pack);
	}
	
	public void addOutBoundTCPPacket(NetworkPacket Pack)
	{
		outboundTCPPackets.add(Pack);
	}
	
	public void addOutBoundUDPPacket(NetworkPacket Pack)
	{
		outboundUDPPackets.add(Pack);
	}
	
}

