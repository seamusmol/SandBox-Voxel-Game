package ClientServer;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jme3.app.state.AppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import ClientDataManager.Client;
import ClientDataManager.ClientDataManager;
import Configs.ServerSettings;
import Configs.SettingsLibrary;
import Console.Parser;
import Main.Main;
import ServerMob.EntityManager;
import ServerRigidBodies.ServerRigidBodyManager;
import network.ClientPacketProcessor;
import serverlevelgen.ChunkRegionManager;
import serverlevelgen.ServerChunkManager;
import serverlevelgen.ServerCollisionPicker;
import serverlevelgen.ServerPhysicsManager;
import worldGen.MapDataGenerator;
import worldGen.MapTest;

/*
 * TODO
 * if in singleplayer mode, reroute outboundpackets to PacketProcessor in network package
 * if in lan mode, change IPs to local
 * else in online mode, no clientserver and route to dedicated server
 * 
 * 
 * 
 */
public class ClientServerPacketProcessor{

	public Stack<NetworkPacket> inboundPackages = new Stack<NetworkPacket>();
	
	public Stack<NetworkPacket> outboundUDPPackages = new Stack<NetworkPacket>();
	public Stack<NetworkPacket> outboundTCPPackages = new Stack<NetworkPacket>();
	
	private ExecutorService executor = Executors.newFixedThreadPool(ServerSettings.inBoundThreadCount+1);
	PackageProcessorThread packageProcessorThread;
	
	DatagramAcceptor udpAcceptor;
	DatagramSender udpSender;
	
	TCPAcceptor tcpAcceptor;
	TCPSender tcpSender;
	
	MapDataGenerator mapGen;
	
	ServerMessageHandler messageHandler;
	ServerChunkManager serverChunkManager;
	ChunkRegionManager regionManager;
	ClientDataManager clientDataManager;
	ClientPacketProcessor packetProcessor;
	ServerPhysicsManager physicsManager;
	
	ServerRigidBodyManager serverRigidBodyManager;

	ServerCollisionPicker collisionPicker;
	EntityManager entityManager;

	Main main;
	
	long lastPing;
	int pingTime = 1000;
	
	public ClientServerPacketProcessor(Object[] MapData, ClientPacketProcessor PacketProcessor, Main Main)
	{
		mapGen = new MapDataGenerator(MapData);
		
		MapTest.createHeightMap(mapGen.map);
		
		lastPing = System.currentTimeMillis();
		
		collisionPicker = new ServerCollisionPicker(mapGen.worldSettings,Main);
		
		physicsManager = new ServerPhysicsManager(mapGen.worldSettings,Main);
		
		clientDataManager = new ClientDataManager(mapGen.worldSettings, this, Main, collisionPicker);
		
		clientDataManager.addNewClient(new Vector3f(mapGen.map.h.length/2,120, mapGen.map.h[0].length/2),SettingsLibrary.playerName, "");
		main = Main;
		switch(ServerSettings.networkMode)
		{
			case 0:
				//singleplayer
				//no connections
				//have active reroute method
				break;
			case 1:
				//lan
				udpAcceptor = new DatagramAcceptor(this, clientDataManager);
				udpSender = new DatagramSender(this, clientDataManager);
				tcpAcceptor = new TCPAcceptor(this, clientDataManager);
				tcpSender = new TCPSender(this, clientDataManager);
				break;
		}
		packetProcessor = PacketProcessor; 
		packageProcessorThread = new PackageProcessorThread();
		messageHandler = new ServerMessageHandler(this, clientDataManager);
		
		regionManager = new ChunkRegionManager(clientDataManager, mapGen);
		
		serverChunkManager = new ServerChunkManager(mapGen, regionManager, clientDataManager, physicsManager, main);
		serverRigidBodyManager = new ServerRigidBodyManager(mapGen, serverChunkManager, regionManager, physicsManager, clientDataManager, Main);
		entityManager = new EntityManager(physicsManager, serverChunkManager, collisionPicker, clientDataManager, Main);
		
		collisionPicker.setRigidBodyNode(serverRigidBodyManager.getRigidBodyNode(), serverRigidBodyManager);
		executor.execute(packageProcessorThread);
		
		if(ServerSettings.networkMode == 1)
		{
			executor.execute(udpAcceptor);
			executor.execute(udpSender);
			executor.execute(tcpAcceptor);
			executor.execute(tcpSender);
		}
	}
	
	
	public void cleanup()
	{
//		detachPhysics();
		
		serverChunkManager.close();
        entityManager.close();
        packageProcessorThread.close();
        serverRigidBodyManager.close();
        executor.shutdown();
        if(ServerSettings.networkMode == 1)
        {
            udpAcceptor.close();
            udpSender.close();
            tcpAcceptor.close();
            tcpSender.close();
        }

        try 
        {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } 
        catch (InterruptedException e) 
        {
        }
		
	}
	
	
	public void initPhysics()
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	main.addAppState("physics", physicsManager);
	        	return null;
	        }
	    });
	}
	
	public void detachPhysics()
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	main.removeAppState("physics");
	        	return null;
	        }
	    });
	}
	
	private class PackageProcessorThread implements Runnable
	{
        public PackageProcessorThread()
        {
            
        }
        
        boolean isRunning = true;
        @Override
        public void run() 
        {
                Thread.currentThread().setName("Util-ServerPacketManager");
                while(isRunning)
                {
                    long startTime = System.currentTimeMillis();
                    initPhysics();
                    processIncomingPackets();
                    clientDataManager.submitClientData();
                    processOutboundPackets();
                    if(ServerSettings.networkMode == 0)
                    {
                    }
                    else
                    {
                            if(System.currentTimeMillis() - lastPing >= pingTime)
                            {
                                    ping();
                            }
                    }

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
        }

        public void close()
        {
            isRunning = false;
        }

        public void processIncomingPackets()
        {
                for(int i = 0; i < clientDataManager.getClientList().size(); i++)
                {
                    while(clientDataManager.getClientList().get(i).getClientPackets().size() > 0)
                    {
                    	executor.execute(new InBoundPacketThread(clientDataManager.getClientList().get(i).getClientPackets().pop()));
                    }
                }

//			while(inboundPackages.size() > 0)
//			{
//				executor.execute(new InBoundPacketThread(inboundPackages.pop()));
//			}
            try 
            {
                    executor.awaitTermination(1, TimeUnit.NANOSECONDS);
            } 
            catch (InterruptedException e) 
            {
            }
        }

        private class InBoundPacketThread extends Thread
        {
            NetworkPacket inboundPacket;
            public InBoundPacketThread(NetworkPacket InboundPacket)
            {
                    inboundPacket = InboundPacket;
            }
            //TODO
            //split Chunk Request types using new variable
            @Override
            public void run() 
            {
//				System.out.println("Received Packet from Client: " + inboundPacket.getIntValue(1))
                switch(inboundPacket.getIndentifierData()[0])
                {
                    case 0:
                        //ping
                        clientDataManager.playerPinged(inboundPacket.getDesignation());
                        break;
                    case 1:
                        break;
                    case 2:
                            //messages
                            messageHandler.processMessage(inboundPacket);
                            break;
                    case 3:
                            break;
                    case 4:
                        entityManager.AddClientInput(inboundPacket);
                        break;
                    case 5:
                            //player rotation
                            //movement input vs vector input
                        break;
                }
            }
        }

        public void processOutboundPackets()
        {
            switch(ServerSettings.networkMode)
            {
                case 0:
                    while(clientDataManager.getClientList().get(0).getOutboundTCP().size() > 0)
                    {
                        packetProcessor.addInBoundPacket(clientDataManager.getClientList().get(0).getOutboundTCP().pop());
                    }
                    while(clientDataManager.getClientList().get(0).getOutboundUDP().size() > 0)
                    {
                        packetProcessor.addInBoundPacket(clientDataManager.getClientList().get(0).getOutboundUDP().pop());
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
        }

        //ping all active players
        //timeout period
        public void ping()
        {
            List<Client> clients = clientDataManager.getClientList();

            for(int i = 0; i < clients.size(); i++)
            {
                if(clients.get(i).hasPinged() && !clients.get(i).hasReceivedPing() )
                {
                    clientDataManager.removeClient(clients.get(i).getName());
                }
            }

            for(int i = 0; i < clients.size(); i++)
            {
                if(clients.get(i).hasLoggedIn())
                {
//					addOutBoundTCPPacket( new NetworkPacket(new byte[0], "Server", 0+"", "global", clients.get(i).getName(), ""));
                    }
            }
            lastPing = System.currentTimeMillis();
        }
	}
	
	public void addInBoundPacket(NetworkPacket Pack)
	{
//		inboundPackages.add(Pack);
		clientDataManager.getClientByName(Pack.getDesignation()).addInboundPacket(Pack);
	}
	
	//packets not ending up in list correctly
	public void addOutBoundTCPPacket(NetworkPacket Pack)
	{
		if(ServerSettings.networkMode == 0)
		{
//			packetProcessor.addInBoundPacket( Pack);
			clientDataManager.getClientByName(Pack.getDesignation()).addOutBoundTCP(Pack);
		}
		else
		{
//			outboundTCPPackages.add(Pack);
			clientDataManager.getClientByName(Pack.getDesignation()).addOutBoundTCP(Pack);
		}
	}
	
	public void addOutBoundUDPPacket(NetworkPacket Pack)
	{
		if(ServerSettings.networkMode == 0)
		{
//			packetProcessor.addInBoundPacket( Pack);
			clientDataManager.getClientByName(Pack.getDesignation()).addOutBoundUDP(Pack);
		}
		else
		{
			clientDataManager.getClientByName(Pack.getDesignation()).addOutBoundUDP(Pack);
//			outboundUDPPackages.add(Pack);
		}
	}


	public ServerRigidBodyManager getServerRigidBodyManager() {
		return serverRigidBodyManager;
	}

	
}

