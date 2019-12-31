package ClientMob;

import java.util.ArrayList;
import java.util.List;

import com.jme3.scene.Node;

import Configs.ServerSettings;
import Configs.SettingsLibrary;
import Input.PlayerCamera;
import Main.Main;

public class EntityManager{

	Node EntityNode;
	
	PlayerCamera playerCamera;
	
	List<Entity> EntityList = new ArrayList<Entity>();
	
	public EntityManager(PlayerCamera PlayerCamera)
	{
		playerCamera = PlayerCamera;
		EntityNode = new Node("ClientEntityNode");
		playerCamera.getRootNode().attachChild(EntityNode);
	}
	
	private class EntityManagerThread implements Runnable
	{
		boolean isRunning = true;
		private EntityManagerThread()
		{
			
		}

		@Override
		public void run() 
		{
			while(isRunning)
			{
				Thread.currentThread().setName("Util-ClientEntityManager");
				long startTime = System.currentTimeMillis();
				updateEntityList();
				RemoveInactiveEntities();
				long frameTime = System.currentTimeMillis() - startTime;
				if(frameTime < 0)
				{
					frameTime = 0;
				}
				else if(frameTime > ServerSettings.tickTime)
				{
					frameTime = ServerSettings.tickTime;
				}
				try 
				{
					Thread.sleep(ServerSettings.tickTime - frameTime);
				} 
				catch(InterruptedException e) 
				{
				}
			}
		}
	}
	
	public void updateEntityList()
	{
		for(int i = 0; i < EntityList.size(); i++)
		{
			if(EntityList.get(i).needsUpdate)
			{
				EntityList.get(i).update();
			}
		}
	}
	
	public void RemoveInactiveEntities()
	{
		int playerIDX = (int) playerCamera.getPosition().x / SettingsLibrary.chunkSize;
		int playerIDZ = (int) playerCamera.getPosition().x / SettingsLibrary.chunkSize;
		
		for(int i = 0; i < EntityList.size(); i++)
		{
			int idx = (int)EntityList.get(i).position.x / SettingsLibrary.chunkSize;
			int idz = (int)EntityList.get(i).position.z / SettingsLibrary.chunkSize;
			
			if (Math.abs(idx - playerIDX) > SettingsLibrary.renderDistance || Math.abs(idz - playerIDZ) > SettingsLibrary.renderDistance)
            {
				//remove entity
				playerCamera.removeFromNode("Entity: " + EntityList.get(i).name + EntityList.get(i).id, "ClientEntityNode");
				EntityList.remove(i);
			}
		}
		
		
	}
	
	public void addEntity(byte[] Data)
	{
		//if has existing, override data
		//else add new entity
		int byteCount = 0;
		int ID = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
		if(!HasEntityByID(ID))
		{
			Entity someEntity = new Entity(Data, byteCount);
			EntityList.add(someEntity);
		}
	}
	
	public void ModifyEntity(byte[] Data)
	{
		int byteCount = 0;
		int ID = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
		Entity someEntity = GetEntityByID(ID);
		if(someEntity != null)
		{
			someEntity.modifyEntity(Data, byteCount);
		}
	}
	
	public Entity GetEntityByID(int ID)
	{
		for(int i = 0; i < EntityList.size(); i++)
		{
			if(EntityList.get(i).id == ID)
			{
				return EntityList.get(i);
			}
		}
		return null;
	}
	
	public boolean HasEntityByID(int ID)
	{
		for(int i = 0; i < EntityList.size(); i++)
		{
			if(EntityList.get(i).id == ID)
			{
				return true;
			}
		}
		return false;
	}
	
	
}
