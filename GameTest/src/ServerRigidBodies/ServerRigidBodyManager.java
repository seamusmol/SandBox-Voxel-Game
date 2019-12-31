package ServerRigidBodies;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import ClientDataManager.Client;
import ClientDataManager.ClientDataManager;
import ClientServer.NetworkPacket;
import Main.Main;
import RigidBodies.RigidBody;
import serverlevelgen.Chunk;
import serverlevelgen.ChunkRegionManager;
import serverlevelgen.ServerChunkManager;
import serverlevelgen.ServerPhysicsManager;
import serverlevelgen.WorldSettings;
import worldGen.MapDataGenerator;

public class ServerRigidBodyManager {

	MapDataGenerator mapGen;
	WorldSettings worldSettings;
	
	RigidBodyThread rigidBodyThread;
	ClientDataManager clientDataManager;
	ChunkRegionManager regionManager;
	ServerChunkManager chunkManager;
	ServerPhysicsManager physicsManager;
	RigidBodyPhysicsManager rigidBodiesPhysicsManager;
	Main main;
	Node rigidBodyNode;
	
	Queue<ServerRigidBody> activeRigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
	Queue<ServerRigidBody> unloadedRigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
	Queue<Vector2f> activeChunkPositions = new ConcurrentLinkedQueue<Vector2f>();
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public ServerRigidBodyManager(MapDataGenerator MapDataGenerator, ServerChunkManager ChunkManager, ChunkRegionManager ChunkRegionManager, ServerPhysicsManager ServerPhysicsManager, ClientDataManager ClientDataManager, Main Main)
	{
		mapGen = MapDataGenerator;
		worldSettings = mapGen.worldSettings;
		
		regionManager = ChunkRegionManager;
		chunkManager = ChunkManager;
		
		physicsManager = ServerPhysicsManager;
		clientDataManager = ClientDataManager;
		
		main = Main;
		rigidBodyNode = new Node("ServerRigidBody");
		main.getRootNode().attachChild(rigidBodyNode);
		
		rigidBodiesPhysicsManager = new RigidBodyPhysicsManager(chunkManager, worldSettings);
		
		rigidBodyThread = new RigidBodyThread();
		executor.execute(rigidBodyThread);
	}
	
	public void close()
	{
		rigidBodyThread.isRunning = false;
		executor.shutdown();
		main.getRootNode().detachChild(rigidBodyNode);
	}
	
	private class RigidBodyThread implements Runnable
	{
		boolean isRunning = true;
		private RigidBodyThread()
		{
			
		}

		@Override
		public void run() 
		{
			long startTime = System.currentTimeMillis();
			Thread.currentThread().setName("Util-ServerRigidBody");
			
			while(isRunning)
			{
				startTime = System.currentTimeMillis();
				
				activeRigidBodies = regionManager.getActiveRigidBodies();
            	unloadedRigidBodies = regionManager.getUnloadedRigidBodies();
            	
				removeOldRigidBodies();
				
				rigidBodiesPhysicsManager.update(0.016f, activeRigidBodies);
				
				physicsManager.ApplyPhysicsForces(activeRigidBodies);
				
				processClientRigidBodyCreationRequests();
				processModificationRequests();
				processRigidBodiesCreation();
				processClientRigidBodyData();
				
				checkDropOffs();
				
				long frameTime = System.currentTimeMillis() - startTime;
				if(frameTime < 0)
				{
					frameTime = 0;
				}
				else if(frameTime > worldSettings.tickTime)
				{
					frameTime = worldSettings.tickTime;
				}
				
				try 
				{
					Thread.sleep(worldSettings.tickTime - frameTime);
					
				} 
				catch(InterruptedException e) 
				{
				}
			}
			executor.shutdown();
		}
		
	}
	
	public void removeOldRigidBodies() 
    {
		while(!unloadedRigidBodies.isEmpty())
		{
			ServerRigidBody removalRigidBody = unloadedRigidBodies.poll();
			detachChild(removalRigidBody.Model);
		}
    }
	
	public void processRigidBodiesCreation()
	{
		for(ServerRigidBody rigidBody : activeRigidBodies)
		{
			if(!rigidBody.hasProcessedCreation)
			{
				if(rigidBody.modelControl != null)
				{
					physicsManager.detachControl(rigidBody.modelControl);
				}
				
				rigidBody.processCreation();
				attachChild(rigidBody.Model);
				physicsManager.attachControl(rigidBody.modelControl);
			}
		}
	}
	
	public void processClientRigidBodyCreationRequests()
	{
		List<Client> clientList = clientDataManager.getClientList();
		
		for(int i = 0; i < clientList.size(); i++)
		{
			while(clientList.get(i).getRigidbodyCreationRequests().size() > 0)
			{
				ServerRigidBody newRigidBody = clientList.get(i).getRigidbodyCreationRequests().pop();
				regionManager.addRigidBody(newRigidBody);
			}
		}
	}
	
	public void processModificationRequests()
	{
		List<Client> clientList = clientDataManager.getClientList();
		for(int i = 0; i < clientList.size(); i++)
		{
			while(clientList.get(i).getRigidBodyModificationID().size() > 0)
			{
				int ID = clientList.get(i).getRigidBodyModificationID().pop();
				Vector3f position = clientList.get(i).getRigidBodyModificationPositions().pop();
				int modificationType = clientList.get(i).getRigidBodyModificationType().pop();
				int modificationMaterial = clientList.get(i).getRigidBodyMaterialType().pop();
				
				ServerRigidBody rigidBody = getRigidBodyByID(ID);
				
				if(rigidBody != null)
				{
					rigidBody.modify(position, modificationType, modificationMaterial);
					if(rigidBody.hasStaticGeometryUpdate)
					{
						updateChildPosition(rigidBody.Model, position);
						rigidBody.setStaticUpdate(false);
					}
					
					if(rigidBody.hasControlModification)
					{
						physicsManager.detachControl(rigidBody.modelControl);
						rigidBody.processControlModification();
						physicsManager.attachControl(rigidBody.modelControl);
					}
				}
			}
		}
	}
	
	public void processClientRigidBodyData()
	{
		for(ServerRigidBody rigidBody : activeRigidBodies)
		{
			rigidBody.updatePosition();
		}
		
		List<Client> clientList = clientDataManager.getClientList();
		
		for(int i = 0; i < clientList.size(); i++)
		{
			List<ServerRigidBody> clientRigidBodyExportList = new ArrayList<ServerRigidBody>();
			List<ServerRigidBody> clientRigidBodyMovementList = new ArrayList<ServerRigidBody>();
			
			int px = (int)clientList.get(i).GetCameraPosition().getX()/worldSettings.chunkSize;
			int pz = (int)clientList.get(i).GetCameraPosition().getZ()/worldSettings.chunkSize;
			
			for(ServerRigidBody rigidBody : activeRigidBodies)
			{
				int idx = (int)rigidBody.getPosition().getX()/worldSettings.chunkSize;
				int idz = (int)rigidBody.getPosition().getZ()/worldSettings.chunkSize;
				
				if(Math.abs(idx - px) <= clientList.get(i).getRenderDistance() && Math.abs(idz - pz) <= clientList.get(i).getRenderDistance())
				{
					if(!clientList.get(i).getRigidBodyIDs().contains(rigidBody.getID()))
					{
						clientRigidBodyExportList.add(rigidBody);
						clientRigidBodyMovementList.add(rigidBody);
						
						clientList.get(i).getRigidBodyIDs().add(rigidBody.getID());
					}
					if(rigidBody.needsCreationDataSent)
					{
						clientRigidBodyExportList.add(rigidBody);
						clientRigidBodyMovementList.add(rigidBody);
					}
//					else if(rigidBody.needsMovementDataSent)
//					{
						clientRigidBodyMovementList.add(rigidBody);
//					}
				}
			}
			//generate networkPacket
			if(clientRigidBodyExportList.size() > 0)
			{
				List<Byte> rigidBodyData = new ArrayList<Byte>();
				for(int j = 0; j < clientRigidBodyExportList.size(); j++)
				{
					rigidBodyData.addAll(clientRigidBodyExportList.get(j).GetData());
				}
				//export
				byte[] data = new byte[rigidBodyData.size()];
				for(int j = 0; j < data.length; j++)
				{
					data[j] = rigidBodyData.get(j);
				}
				sendRigidBodyDataToClient(clientList.get(i), data);
			}
			
			if(clientRigidBodyMovementList.size() > 0)
			{
				List<Byte> rigidBodyMovementData = new ArrayList<Byte>();
				for(int j = 0; j < clientRigidBodyMovementList.size(); j++)
				{
					rigidBodyMovementData.addAll(clientRigidBodyMovementList.get(j).GetMovementData());
				}
				//export
				byte[] data = new byte[rigidBodyMovementData.size()];
				for(int j = 0; j < data.length; j++)
				{
					data[j] = rigidBodyMovementData.get(j);
				}
				sendRigidBodyMovementData(clientList.get(i), data);
			}
			
		}
		
		for(ServerRigidBody rigidBody : activeRigidBodies)
		{
			rigidBody.needsCreationDataSent = false;
			rigidBody.needsMovementDataSent = false;
		}
	}
	
	public void checkDropOffs()
	{
		List<Control> controls = new ArrayList<Control>();
		List<Vector3f> newPositions = new ArrayList<Vector3f>();
		for(ServerRigidBody rigidBody : activeRigidBodies)
		{
			if(rigidBody.getPosition().getY() <= -5.0f)
			{
				if(rigidBody.isUpright)
				{
					//check static geometries
				}
				else
				{
					controls.add(rigidBody.modelControl);
					int px = (int)rigidBody.getPosition().getX();
					int pz = (int)rigidBody.getPosition().getZ();
					newPositions.add(new Vector3f(px, chunkManager.getTopHeight(px, pz) + rigidBody.getBoundingDistance().getY()*2+1, pz));
				}
			}
		}
		if(controls.size() > 0)
		{
			physicsManager.UpdateControlPositions(controls, newPositions);
		}
	}
	
	public ServerRigidBody getRigidBodyByID(int ID)
	{
		for(ServerRigidBody rigidBody : activeRigidBodies)
		{
			if(rigidBody.getID() == ID)
			{
				return rigidBody;
			}
		}
		return null;
	}
	
	public void attachChild(Geometry Geom)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	rigidBodyNode.attachChild(Geom);
	        	return null;
	        }
		});
	}
	
	public void updateChildPosition(Geometry Geom, Vector3f Position)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	Geom.setLocalTranslation(Position);
	        	return null;
	        }
		});
	}
	
	public void detachChild(Geometry Geom)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	if(Geom != null)
	        	{
	        		rigidBodyNode.detachChild(Geom);
	        	}
	        	return null;
	        }
		});
	}
	
	public void sendRigidBodyDataToClient(final Client Client, byte[] Data)
	{
		Client.addOutBoundTCP(new NetworkPacket(Client.getName(), Data, new int[]{ 6, 0}));
	}
	
	public void sendRigidBodyMovementData(final Client Client, byte[] Data)
	{
		Client.addOutBoundTCP(new NetworkPacket(Client.getName(), Data, new int[]{ 6, 1}));
	}

	public Node getRigidBodyNode() {
		return rigidBodyNode;
	}

	public Queue<ServerRigidBody> getRigidBodies() {
		return activeRigidBodies;
	}
	
	
	
}
