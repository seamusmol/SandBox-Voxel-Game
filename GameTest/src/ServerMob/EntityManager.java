package ServerMob;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

import ClientDataManager.Client;
import ClientDataManager.ClientDataManager;
import ClientServer.NetworkPacket;
import Configs.ServerSettings;
import Input.PlayerCamera;
import Inventory.ItemAbilities;
import Main.Main;
import ServerRigidBodies.ServerRigidBody;
import Util.CompressionUtil;
import Util.MathUtil;
import serverlevelgen.ServerChunkManager;
import serverlevelgen.ServerCollisionPicker;
import serverlevelgen.ServerPhysicsManager;

public class EntityManager {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    EntityManagerThread updateThread;
    PlayerCamera playerCamera;
    ServerChunkManager serverChunkManager;
    ServerCollisionPicker collisionPicker;
    ClientDataManager clientDataManager;
    ServerPhysicsManager physicsManager;
    Main main;

    Stack<NetworkPacket> clientInput = new Stack<NetworkPacket>();
    List<Entity> EntityList = new ArrayList<Entity>();
    
    public EntityManager(ServerPhysicsManager ServerPhysicsManager, ServerChunkManager ServerChunkManager, ServerCollisionPicker CollisionPicker, ClientDataManager ClientDataManager, Main Main) {
        //create test entity
    	physicsManager = ServerPhysicsManager;
        serverChunkManager = ServerChunkManager;
        collisionPicker = CollisionPicker;
        clientDataManager = ClientDataManager;
        main = Main;

        updateThread = new EntityManagerThread();
        executor.execute(updateThread);
    }
    
    public void close()
    {
    	updateThread.isRunning = false;
    	executor.shutdown();
    }

    
    
    private class EntityManagerThread implements Runnable
    {
    	float tpf = ServerSettings.tickTime / 1000.0f;
        boolean isRunning = true;

        @Override
        public void run() {
            Thread.currentThread().setName("Util-ServerMobManager");
            while (isRunning)
            {
                long startTime = System.currentTimeMillis();

                updatePlayers(tpf);
                updateMobs(tpf);
                checkDropOffs();
                
                long frameTime = System.currentTimeMillis() - startTime;
                if (frameTime < 0) 
                {
                    frameTime = 0;
                } 
                else if (frameTime > ServerSettings.tickTime - frameTime) 
                {
                    frameTime = (long) (ServerSettings.tickTime - frameTime);
                }
                try 
                {
                    Thread.sleep((long) (ServerSettings.tickTime - frameTime));
                } 
                catch (InterruptedException e) 
                {
                }
            }
        }
    }
    
    public void updatePlayers(float tpf) 
    {
    	if(!physicsManager.isInitialized())
    	{
    		return;
    	}
    	
    	List<Client> clientList = clientDataManager.getClientList();
        
        for(Client client : clientList)
        {
        	PlayerMob clientEntity = client.getEntity();
        	
    		if(clientEntity.IsEnabled && !clientEntity.hasControlAttached)
        	{
    			clientEntity.hasControlAttached = true;
        		physicsManager.attachControl(clientEntity.getControl());
        	}
        }
 
        while (clientInput.size() > 0) 
        {
        	NetworkPacket packet = clientInput.pop();
        	Client someClient = clientDataManager.getClientByName(packet.getDesignation());
            someClient.getEntity().processPlayerInput(this, packet, someClient, tpf);
        }
        
        for(Client client : clientList)
        {
        	PlayerMob clientEntity = client.getEntity();
        	
        	clientEntity.OnUpdate(this, tpf);
        	
//        	if(clientEntity.IsEnabled && clientEntity.hasControlAttached && clientEntity.gameMode == 0)
//        	{
//        		client.setPosition(clientEntity.getControl().getPhysicsLocation());
//        	}
        	
        	client.getInventory().update();
        }
    }
    
    public void checkDropOffs()
	{
		List<Client> clientList = clientDataManager.getClientList();
		for(Client client : clientList)
        {
			if(client.getEntity().getGameMode() == 0)
			{
				if(client.getPosition().getY() <= -5.0f)
				{
					CharacterControl control = client.getEntity().getControl();
					float offset = 5.0f;
					
					int px = (int)client.getPosition().getX();
					int pz = (int)client.getPosition().getZ();
					Vector3f newPosition = new Vector3f(px, serverChunkManager.getTopHeight(px, pz) + offset, pz);
					control.setPhysicsLocation( newPosition);
					client.getEntity().setPosition(newPosition);
				}	
			}
		}
		
		
		
	}
    
    public void updateMobs(float tpf)
    {
    	for (int i = 0; i < EntityList.size(); i++) 
        {
            if (EntityList.get(i).IsEnabled) 
            {
                EntityList.get(i).OnUpdate(this, tpf);
            }
        }
    }

    public void moveClientCameraSurvival(float Value, Vector3f Direction, Client SomeClient)
    {
    	
//    	CharacterControl control = SomeClient.getEntity().getControl();
//    	control.setWalkDirection(vec);
    	
    }

    public void AddClientInput(NetworkPacket InboundPacket) {
        clientInput.add(InboundPacket);
    }
    
   
}
