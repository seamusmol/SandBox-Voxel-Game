package ServerRigidBodies;

import java.util.Queue;

import serverlevelgen.ServerChunkManager;
import serverlevelgen.WorldSettings;

public class RigidBodyPhysicsManager {

	ServerChunkManager chunkManager;
	WorldSettings worldSettings;
	
	public RigidBodyPhysicsManager(ServerChunkManager ServerChunkManager, WorldSettings WorldSettings)
	{
		chunkManager = ServerChunkManager;
		worldSettings = WorldSettings;
	}
	
	public void update(float tpf, Queue<ServerRigidBody> ActiveRigidBodies)
	{
		if(!worldSettings.hasRigidBodyPhysics)
		{
			return;
		}
		
		for(ServerRigidBody rigidBody : ActiveRigidBodies)
		{
			if(!rigidBody.isUpright)
			{
				RigidBodyEffects.calculateBuoyancy(tpf, rigidBody, chunkManager);
			}
		}	
		
	}
	
	
}
