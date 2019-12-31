package levelgen;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.BatchHint;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.util.SkyFactory;

import Configs.ServerSettings;
import Configs.SettingsLibrary;
import Input.PlayerCamera;
import Main.AssetLoaderManager;
import Mob.PlayerSettings;

public class ChunkManager{

	ExecutorService executor = Executors.newSingleThreadExecutor();
	ChunkUpdateThread updateThread;
	
	PlayerCamera playerCamera;
	PlayerSettings playerSettings;
	BackgroundTerrainGenerator backgroundGen;
	CloudGen cloudGen;
	
	boolean hasChunksLoaded = false;
	
	private Stack<Chunk> chunkList = new Stack<Chunk>();
	
	boolean hasUpload = false;
	int idx;
	int idz;
	
	public ChunkManager(PlayerSettings PlayerSettings, PlayerCamera PlayerCamera)
	{
		playerSettings = PlayerSettings;
		playerCamera = PlayerCamera;
		backgroundGen = new BackgroundTerrainGenerator(this);
		cloudGen = new CloudGen(this, playerCamera);
		
		Node n1 = new Node("BackgroundNode");
		Node n2 = new Node("ClientChunkNode");
		Node n3 = new Node("TransparentNode");
		Node n4 = new Node("LiquidNode");
		
		n1.setCullHint(CullHint.Never);
		n3.setCullHint(CullHint.Never);
		n4.setCullHint(CullHint.Never);
		
		playerCamera.attachNode(n1);
		playerCamera.attachNode(n2);
		playerCamera.attachNode(n3);
		playerCamera.attachNode(n4);

		Geometry skydome = (Geometry)SkyFactory.createSky(AssetLoaderManager.getAssetManager(), AssetLoaderManager.getTexture("skydome"), SkyFactory.EnvMapType.EquirectMap);
//		skydome.setQueueBucket(Bucket.Sky);
		skydome.setCullHint(CullHint.Never);
		
		playerCamera.attachChild( skydome, "BackgroundNode");
		
		updateThread = new ChunkUpdateThread();
		executor.execute(updateThread);
	}
	
	public void close()
	{
		updateThread.isRunning = false;
		executor.shutdown();
	}
	
	private class ChunkUpdateThread implements Runnable
	{
		boolean isRunning = true;
		
		public ChunkUpdateThread()
		{
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("Util-ClientChunkManager");
			
			while(isRunning)
			{
				long startTime = System.currentTimeMillis();
				hasUpload = false;
				if(hasChunksLoaded)
				{
					if(SettingsLibrary.hasClouds)
					{
						generateClouds();
					}
					if(SettingsLibrary.hasBackGround)
					{
						generateBackground();
					}
				}
				generateChunkMeshes();
				removeChunks();
				
				long frameTime = System.currentTimeMillis() - startTime;
				if(frameTime < 0)
				{
					frameTime = 0;
				}
				else if(frameTime > SettingsLibrary.updateTime - frameTime)
				{
					frameTime = (long) (SettingsLibrary.updateTime - frameTime);
				}
				
				try 
				{
					Thread.sleep((long) (SettingsLibrary.updateTime - frameTime));
				} 
				catch(InterruptedException e) 
				{
				}
			}
		}
	}
	
	public void generateClouds()
	{
//		if(hasUpload)
//		{
//			return;
//		}
		
		cloudGen.update(playerCamera.getPosition());
	}

	public void generateBackground()
	{
		if(hasUpload)
		{
			return;
		}
		
		if(backgroundGen.needsGeomUpload)
		{
			boolean hasGaps = false;
			for(int i = 0; i < chunkList.size(); i++)
			{
				if(chunkList.get(i).needsUpdate)
				{
					hasGaps = true;
				}
			}
			
			if(!hasGaps && backgroundGen.hasGeometry())
			{	
				playerCamera.attachChild(backgroundGen.geom, "BackgroundNode");
				
				//TODO
				//attach control to physics space
				
				
				hasUpload = true;
				backgroundGen.needsGeomUpload = false;
				return;
			}
		}
		if(backgroundGen.needsUpdate)
		{
			boolean hasGaps = false;
			for(int i = 0; i < chunkList.size(); i++)
			{
				if(chunkList.get(i).needsUpdate)
				{
					hasGaps = true;
				}
			}
			if(!hasGaps)
			{
				backgroundGen.update(idx, idz, SettingsLibrary.renderDistance);
			}
		}
	}
	
	public void generateChunkMeshes()
	{
		hasChunksLoaded = true;
		for(int i = 0; i < chunkList.size(); i++)
		{
			if(chunkList.get(i).needsUpdate)
			{
				chunkList.get(i).processChunk();
				if(chunkList.get(i).HasGeometry())
				{
					playerCamera.attachChild(chunkList.get(i).GetChunkGeometry(), "ClientChunkNode");
					hasUpload = true;
				}

				if(chunkList.get(i).HasTransparentGeometry())
				{
					playerCamera.attachChild(chunkList.get(i).GetTransparentChunkGeometry(), "TransparentNode");
					hasUpload = true;
				}
				hasChunksLoaded = false;
				return;
			}
		}
		if(hasChunksLoaded)
		{
			if(playerCamera.getServerPosition() != null)
			{
				idx = (int)playerCamera.getServerPosition().getX() / playerSettings.chunkSize;
				idz = (int)playerCamera.getServerPosition().getZ() / playerSettings.chunkSize;
			}
		}
	}
	
	
	public void removeChunks()
	{
		if(playerCamera.getServerPosition() == null)
		{
			return;
		}
		idx = (int)playerCamera.getServerPosition().getX() / playerSettings.chunkSize;
        idz = (int)playerCamera.getServerPosition().getZ() / playerSettings.chunkSize;
		
		for (int i = 0; i < chunkList.size(); i++)
        {
            if (Math.abs(chunkList.get(i).getChunkIDX() - idx) > SettingsLibrary.renderDistance || Math.abs(chunkList.get(i).getChunkIDZ() - idz) > SettingsLibrary.renderDistance)
            {
            	//
            	playerCamera.removeFromNode("Chunk: " + chunkList.get(i).getChunkIDX()+ "-"+ chunkList.get(i).getChunkIDZ(), "ClientChunkNode");
            	playerCamera.removeFromNode("Chunk: " + chunkList.get(i).getChunkIDX()+ "-"+ chunkList.get(i).getChunkIDZ(), "TransparentNode");
            	chunkList.remove(i);
            }
        }
	}
	
	public boolean hasChunksLoaded()
	{
		if(playerCamera == null)
		{
			return false;
		}
		if(playerCamera.getServerPosition() == null)
		{
			return false;
		}
		boolean hasChunkLoaded = true;
		
		idx = (int)playerCamera.getServerPosition().getX() / playerSettings.chunkSize;
		idz = (int)playerCamera.getServerPosition().getZ() / playerSettings.chunkSize;
		
		for(int i = -SettingsLibrary.renderDistance; i < SettingsLibrary.renderDistance; i++)
		{
			for(int j = -SettingsLibrary.renderDistance; j < SettingsLibrary.renderDistance; j++)
			{
				if(!hasChunk(idx + i, idz + j))
				{
					return false;		
				}
			}
		}
		return hasChunkLoaded;
	}
	
	public void addChunk(byte[] Data)
	{
		int ByteCount = 0;
		int IDX = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int IDZ = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		if(hasChunk(IDX,IDZ))
		{
			Chunk someChunk = getChunkByID(IDX,IDZ);
			someChunk.updateData(Data);
		}
		else
		{
			Chunk newChunk = new Chunk(Data);
			chunkList.add( newChunk);
		}
		backgroundGen.needsUpdate = true;
		
		cloudGen.needsUpdate = true;
	}
	
	public void updateBackGround(byte[] Data)
	{
		backgroundGen.updateMap(Data);
	}
	
	public Chunk getChunkByID(int px, int py)
	{
		for(int i = 0; i < chunkList.size(); i++)
		{
			if(chunkList.get(i).getChunkIDX() == px && chunkList.get(i).getChunkIDZ() == py)
			{
				return chunkList.get(i);
			}
		}
		return null;
	}
	
	public boolean hasVoxel(Vector3f Position)
	{
	    int chunkIDX = (int)Position.x / SettingsLibrary.chunkSize - 1;
	    int chunkIDZ = (int)Position.z / SettingsLibrary.chunkSize - 1;
	    Chunk Chunk = getChunkByID(chunkIDX, chunkIDZ);
	    if (Chunk == null)
	    {
	        return false;
	    }
	    else if (Position.y > 127 || Position.y < 0)
	    {
	        return false;
	    }
	    return Chunk.getVoxel((int)Position.x % SettingsLibrary.chunkSize, (int)Position.y, (int)Position.z % SettingsLibrary.chunkSize);
	}
	
	public boolean hasChunk(int px, int py)
	{
		for(int i = 0; i < chunkList.size(); i++)
		{
			if(chunkList.get(i).getChunkIDX() == px && chunkList.get(i).getChunkIDZ() == py)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean[] getVoxelValue(Vector3f Position)
    {
        int chunkIDX = (int)Position.x / playerSettings.chunkSize;
        int chunkIDZ = (int)Position.z / playerSettings.chunkSize;

        Chunk Chunk = getChunkByID(chunkIDX, chunkIDZ);
        if (Chunk == null)
        {
            return new boolean[8];
        }
        return Chunk.getVoxelValue((int)Position.x % playerSettings.chunkSize, (int)Position.y, (int)Position.z % playerSettings.chunkSize);
    }
}
