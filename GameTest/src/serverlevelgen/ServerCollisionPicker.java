package serverlevelgen;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import ClientDataManager.Client;
import Configs.ServerSettings;
import Main.Main;
import RigidBodies.RigidBody;
import ServerRigidBodies.RigidBodyCollisionResult;
import ServerRigidBodies.RigidBodyCollisionResults;
import ServerRigidBodies.ServerRigidBody;
import ServerRigidBodies.ServerRigidBodyManager;
import Util.MathUtil;

public class ServerCollisionPicker {

	Main main;
	Node rigidBodyNode = new Node();
	ServerRigidBodyManager rigidBodyManager;
	
	WorldSettings worldSettings;
	
	public ServerCollisionPicker(WorldSettings WorldSettings, Main Main)
	{
		main = Main;
		worldSettings = WorldSettings;
	}
	
	public void setRigidBodyNode(Node Node, ServerRigidBodyManager RigidBodyManager)
	{
		rigidBodyNode = Node;
		rigidBodyManager = RigidBodyManager;
	}
	
	public RigidBodyCollisionResults getRigidBodyCollisions(Vector3f Position, Quaternion Rotation, Float Min, Float Magnitude)
	{
		RigidBodyCollisionResults rigidBodyResults = new RigidBodyCollisionResults();
		
		Queue<ServerRigidBody> rigidBodies = rigidBodyManager.getRigidBodies();
		Vector3f direction =  Rotation.getRotationColumn(2);
		
		List<Vector3f> rayPositions = new ArrayList<Vector3f>();
		for(float i = 0; i <= Magnitude; i+= 0.0125f)
		{
			rayPositions.add(Position.add(direction.mult(i)));
		}
		
		for(ServerRigidBody rigidBody : rigidBodies)
		{	
			for(int j = 0; j < rayPositions.size(); j++)
			{
				Vector3f collisionPosition = rayPositions.get(j);
				
				Vector3f rotDist = rigidBody.getRotation().mult( rigidBody.getCenterBoundingDistance());
				
				Vector3f min = rigidBody.getPosition().subtract(rotDist);
				Vector3f max = rigidBody.getPosition().add(rotDist);
				
				boolean dx = MathUtil.between(min.x, max.x, collisionPosition.x);
				boolean dy = MathUtil.between(min.y, max.y, collisionPosition.y);
				boolean dz = MathUtil.between(min.z, max.z, collisionPosition.z);
				
				if(dx && dy && dz)
				{
					int px = Math.round(MathUtil.distance(collisionPosition.x, min.x)/rigidBody.getVoxelScale());
					int py = Math.round(MathUtil.distance(collisionPosition.y, min.y)/rigidBody.getVoxelScale());
					int pz = Math.round(MathUtil.distance(collisionPosition.z, min.z)/rigidBody.getVoxelScale());
					
					boolean hasVoxel = rigidBody.hasVoxel(px, py, pz);
					float distance = Position.distance(rayPositions.get(j));
					
					if(hasVoxel || distance >= Min)
					{
						Vector3f voxelPosition = new Vector3f(px,py,pz);
						RigidBodyCollisionResult result = new RigidBodyCollisionResult(
								rigidBody,
								collisionPosition, 
								voxelPosition,
								distance,
								rigidBody.getID(),
								hasVoxel);
						rigidBodyResults.add(result);
					}
				}	
			}
		}
		return rigidBodyResults;
	}
	
	public ServerRigidBody getRigidBodyCollision(Vector3f Position, Quaternion Rotation)
	{
		CollisionResults results = new CollisionResults();
		Ray viewRay = new Ray(Position, Rotation.getRotationColumn(2));
	
		rigidBodyNode.collideWith(viewRay, results);
		CollisionResult closest = null;
		if(results.size() > 0) 
		{
			closest  = results.getClosestCollision();
			ServerRigidBody rigidBody = rigidBodyManager.getRigidBodyByID( Integer.parseInt(closest.getGeometry().getName()));
			return rigidBody;
		}
		return null;
	}
	
	public CollisionResult getChunkCollision(Vector3f Position, Quaternion Rotation)
	{
		CollisionResults results = new CollisionResults();
		Ray viewRay = new Ray(Position, Rotation.getRotationColumn(2));
		
		Node ChunkNode = (Node) main.getRootNode().getChild("ServerChunkNode");
		
		if(ChunkNode == null)
		{
			return null;
		}
		
		List<Spatial> chunkGeometryList = new ArrayList<Spatial>();
		for(int i = 0; i < ChunkNode.getChildren().size(); i++)
		{
			//"Chunk: " + ChunkIDX + "-" + ChunkIDZ
			String chunkName = ChunkNode.getChild(i).getName().replace("Chunk: ", "");
			int chunkIDX = Integer.parseInt(chunkName.split("-")[0]);
			int chunkIDZ = Integer.parseInt(chunkName.split("-")[1]);
			
			int playerIDX = (int) (Position.getX()/worldSettings.chunkSize);
			int playerIDZ = (int) (Position.getZ()/worldSettings.chunkSize);
			
			if(Math.abs(chunkIDX - playerIDX) <= ServerSettings.playerChunkCheckDistance)
			{
				if((Math.abs(chunkIDZ - playerIDZ) <= ServerSettings.playerChunkCheckDistance))
				{
					chunkGeometryList.add(ChunkNode.getChild(i));
				}
			}
		}
		
		for(int i = 0; i < chunkGeometryList.size(); i++)
		{
			chunkGeometryList.get(i).collideWith(viewRay, results);
		}
		
		CollisionResult closest = null;
		// Use the results
		if(results.size() > 0) 
		{
			// how to react when a collision was detected
			closest  = results.getClosestCollision();
		}
		return closest;
	}
	
}
