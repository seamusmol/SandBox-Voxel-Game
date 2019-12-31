package Inventory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import ClientDataManager.Client;
import Configs.ServerSettings;
import Console.Parser;
import Main.Main;
import ServerRigidBodies.RigidBodyCollisionResult;
import ServerRigidBodies.RigidBodyCollisionResults;
import ServerRigidBodies.ServerRigidBody;
import Util.MathUtil;
import VoxelModels.VoxelModels;
import serverlevelgen.ServerCollisionPicker;

public class ItemAbilities {

	private static Main main;
	private static ServerCollisionPicker collisionPicker;
	
	static Map<String,List<String>> selectionLists = new HashMap<String,List<String>>();
	
	public ItemAbilities(Main Main, ServerCollisionPicker ServerCollisionPicker)
	{
		main = Main;
		collisionPicker = ServerCollisionPicker;
		loadSelectionList();
	}
	
	public static void loadSelectionList()
	{
		try 
		{
			BufferedReader script = new BufferedReader( new FileReader(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/config/toolselectionlist.cfg"));
			List<String> selectionList = new ArrayList<String>();
			for(String line = script.readLine(); line != null ; line = script.readLine())
			{
                selectionList.add(line);
			}
			script.close();
			selectionLists.put("toolselectionlist", selectionList);
		} 
		catch(FileNotFoundException e) 
		{
			return;
		}
		catch(IOException e) 
		{
			return;
		}
	}
	
	public static void grabRigidBody(final Client Client, final RigidBodyItem Item)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	ServerRigidBody hitBody = collisionPicker.getRigidBodyCollision(Client.GetCameraPosition(), Client.getRotation());
	        	if(hitBody != null && hitBody.isUpright)
	        	{
	        		Item.setGrabbedRigidBody(hitBody);
	        		
	        		Vector3f boundingDistance = hitBody.getBoundingDistance();
	        		float max = MathUtil.getMax(boundingDistance.x,boundingDistance.z);
	        		
	        		Vector3f position = Client.getRotation().getRotationColumn(2).mult(ServerSettings.playerPickDistance + max);
	        		Item.setGrabbedPosition(position);
	        	}
	        	return null;
	        }
		});
	}
	
	public static void moveGrabbedRigidBody(final Client Client, final RigidBodyItem Item)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	Vector3f boundingDistance = Item.getGrabbedRigidBody().getBoundingDistance();
        		float max = MathUtil.getMax(boundingDistance.x,boundingDistance.z);
	        	Vector3f position = Client.GetCameraPosition().add( Client.getRotation().getRotationColumn(2).mult(ServerSettings.playerPickDistance + max));
	        	
	        	Client.getRigidBodyModificationPositions().add(position);
				Client.getRigidBodyModificationID().add(Item.grabbedRigidBody.getID());
				Client.getRigidBodyMaterialType().add(0);
				Client.getRigidBodyModificationType().add(3);
	        	
				Item.setGrabbedPosition(position);
				
	        	return null;
	        }
		});
	}
	
	public static void FreezeRigidBody(final Client Client)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	ServerRigidBody hitBody = collisionPicker.getRigidBodyCollision(Client.GetCameraPosition(), Client.getRotation());
	        	if(hitBody != null)
	        	{
	        		Vector3f boundingDistance = hitBody.getBoundingDistance();
	        		float max = MathUtil.getMax(boundingDistance.x,boundingDistance.z);
	        		
	        		Vector3f position = Client.getRotation().getRotationColumn(2).mult(ServerSettings.playerPickDistance + max);
	        		
	        		Client.getRigidBodyModificationPositions().add(position);
					Client.getRigidBodyModificationID().add(hitBody.getID());
					Client.getRigidBodyMaterialType().add(0);
					Client.getRigidBodyModificationType().add(2);
	        	}
	        	return null;
	        }
		});
	}
	
	public static void ModifyTerrain(final Client Client, final int FillMaterial)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	Vector3f CubePosition = new Vector3f(Client.getCubePosition());
	        	
	        	boolean hasCollision = false;
	        	
	        	RigidBodyCollisionResults rigidBodyResults = collisionPicker.getRigidBodyCollisions(Client.GetCameraPosition(), Client.getRotation(), 3.5f, 8.5f);
	        	RigidBodyCollisionResult result = null;
	        	if(rigidBodyResults.size() > 0)
				{
	        		RigidBodyCollisionResult hitResult = rigidBodyResults.getClosestHitResult();
	        		if(hitResult != null)
	        		{
						Client.setCubeRotation(hitResult.getRigidBody().getRotation());
						result = hitResult;
						hasCollision = true;
						CubePosition = hitResult.getCollisionPosition();
						Client.setCubePosition(CubePosition);
	        		}
	        		else
	        		{
	        			RigidBodyCollisionResult nonHitResult = rigidBodyResults.getClosestNonHitResult(3.5f, 8.5f);
	        			if(nonHitResult != null)
	        			{
	        				CubePosition = nonHitResult.getCollisionPosition();
							Client.setCubePosition(CubePosition);
							Client.setCubeRotation(nonHitResult.getRigidBody().getRotation());
							result = nonHitResult;
							hasCollision = true;
	        			}
	        		}
				}
	        	
	        	if(hasCollision)
	        	{
	        		if(FillMaterial == 0)
					{
						Client.getRigidBodyModificationPositions().add(CubePosition);
						Client.getRigidBodyModificationID().add(result.getRigidBody().getID());
						Client.getRigidBodyMaterialType().add(0);
						Client.getRigidBodyModificationType().add(0);
					}
					else
					{
						Vector3f offset = Client.getRotation().getRotationColumn(2).mult(-1.0f);
						
						Client.getRigidBodyModificationPositions().add( CubePosition.add(offset));
						Client.getRigidBodyModificationID().add( result.getRigidBody().getID());
						Client.getRigidBodyMaterialType().add(FillMaterial);
						Client.getRigidBodyModificationType().add(1);
					}
	        	}
	        	else
				{
	        		boolean hasChunkDetection = false;
	        		CollisionResult chunkResult = collisionPicker.getChunkCollision(Client.GetCameraPosition(), Client.getRotation());
    				if(chunkResult != null)
    				{
    					if(chunkResult.getDistance() <= ServerSettings.playerPickDistance)
    					{
    						Vector3f resultPosition = chunkResult.getContactPoint();
        					CubePosition.x  = (float) (Math.floor(resultPosition.x));
        					CubePosition.y  = (float) (Math.floor(resultPosition.y));
        					CubePosition.z  = (float) (Math.floor(resultPosition.z));
        					Client.setCubePosition(CubePosition);
    						Client.setCubeRotation(new Quaternion());
    						hasChunkDetection = true;
    					}
    				}
    				if(!hasChunkDetection)
    				{
    					Vector3f cameraPosition = Client.GetCameraPosition();
						Vector3f viewDir = Client.getRotation().getRotationColumn(2);
						CubePosition = new Vector3f(cameraPosition.x + viewDir.x * ServerSettings.playerPickDistance, cameraPosition.y + viewDir.y * ServerSettings.playerPickDistance, cameraPosition.z + viewDir.z * ServerSettings.playerPickDistance);
					
						//round cube position to voxel
						CubePosition.x  = (float) (Math.floor(CubePosition.x));
						CubePosition.y  = (float) (Math.floor(CubePosition.y));
						CubePosition.z  = (float) (Math.floor(CubePosition.z));
						
						Client.setCubePosition(CubePosition);
						Client.setCubeRotation(new Quaternion());
    				}
	        		
					if(FillMaterial == 0)
					{
						Client.getCellModificationPositions().add( CubePosition);
						Client.getMaterialType().add(0);
						Client.getCellDestructionType().add(0);
					}
					else
					{
						Vector3f offset = Client.getRotation().getRotationColumn(2).mult(-1.0f);
						Client.getCellModificationPositions().add( CubePosition);
						Client.getMaterialType().add(FillMaterial);
						Client.getCellDestructionType().add(1);
					}
				}
	            return null;
	        }
	    });
	}
	
	
	
	public static void generateClientCubePosition(final List<Client>ClientList)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	for(int i = 0; i < ClientList.size(); i++)
	        	{
	        		Client Client = ClientList.get(i);
	        		Vector3f CubePosition = new Vector3f(Client.getCubePosition());
		        	boolean hasCollision = false;
		        	
		        	RigidBodyCollisionResults rigidBodyResults = collisionPicker.getRigidBodyCollisions(Client.GetCameraPosition(), Client.getRotation(), 3.5f, 8.5f);
		        	if(rigidBodyResults.size() > 0)
					{
		        		RigidBodyCollisionResult hitResult = rigidBodyResults.getClosestHitResult();
		        		if(hitResult != null)
		        		{
		        			Client.setCubeRotation(hitResult.getRigidBody().getRotation());
							hasCollision = true;
							CubePosition = hitResult.getCollisionPosition();
							Client.setCubePosition(CubePosition);
		        		}
		        		else
		        		{
		        			RigidBodyCollisionResult nonHitResult = rigidBodyResults.getClosestNonHitResult(3.5f, 8.5f);
		        			if(nonHitResult != null)
		        			{
		        				CubePosition = nonHitResult.getCollisionPosition();
								Client.setCubePosition(CubePosition);
								Client.setCubeRotation(nonHitResult.getRigidBody().getRotation());
								hasCollision = true;
		        			}
		        		}
					}
		        	
					if(!hasCollision)
					{
						boolean hasChunkDetection = false;
		        		CollisionResult chunkResult = collisionPicker.getChunkCollision(Client.GetCameraPosition(), Client.getRotation());
	    				if(chunkResult != null)
	    				{
	    					if(chunkResult.getDistance() <= ServerSettings.playerPickDistance)
	    					{
	    						Vector3f resultPosition = chunkResult.getContactPoint();
	        					CubePosition.x  = (float) (Math.floor(resultPosition.x));
	        					CubePosition.y  = (float) (Math.floor(resultPosition.y));
	        					CubePosition.z  = (float) (Math.floor(resultPosition.z));
	        					Client.setCubePosition(CubePosition);
	    						Client.setCubeRotation(new Quaternion());
	    						hasChunkDetection = true;
	    					}
	    				}
	    				if(!hasChunkDetection)
	    				{
	    					Vector3f cameraPosition = Client.GetCameraPosition();
							Vector3f viewDir = Client.getRotation().getRotationColumn(2);
							CubePosition = new Vector3f(cameraPosition.x + viewDir.x * ServerSettings.playerPickDistance, cameraPosition.y + viewDir.y * ServerSettings.playerPickDistance, cameraPosition.z + viewDir.z * ServerSettings.playerPickDistance);
						
							//round cube position to voxel
							CubePosition.x  = (float) (Math.floor(CubePosition.x));
							CubePosition.y  = (float) (Math.floor(CubePosition.y));
							CubePosition.z  = (float) (Math.floor(CubePosition.z));
							
							Client.setCubePosition(CubePosition);
							Client.setCubeRotation(new Quaternion());
	    				}
					}
	        	}
	        	
	        	return null;
	        	
	        }
	    });
	}
	
	public static void dropProp(Client Client,int ID)
	{
		if(VoxelModels.hasProp(ID))
		{
			short[][][] materials = VoxelModels.getPropByID(ID);
			Vector3f boundingDistance = new Vector3f(materials.length,materials[0].length,materials[0][0].length);
    		float max = MathUtil.getMax(boundingDistance.x,boundingDistance.z);
        	Vector3f position = Client.GetCameraPosition().add( Client.getRotation().getRotationColumn(2).mult(ServerSettings.playerPickDistance + max));
			
			ServerRigidBody rigidBody = new ServerRigidBody(position, Client.getRotation(), 1.0f, materials, false);
			Client.getRigidbodyCreationRequests().add(rigidBody);
		}
	}
	
	//creates new rigidBody
	public static void DropItem(Client Client, short Material)
	{
		short[][][] materials = new short[6][3][6];
		
		for(int i = 1; i < 5; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				materials[i][1][j] = Material;
			}
		}
		
		Vector3f position = new Vector3f(Client.getPosition()).add(Client.getRotation().getRotationColumn(2).mult(ServerSettings.playerPickDistance));
		ServerRigidBody rigidBody = new ServerRigidBody(position, Client.getRotation(), 1.0f, materials, false);
		Client.getRigidbodyCreationRequests().add(rigidBody);
	}
	
	
	
}
