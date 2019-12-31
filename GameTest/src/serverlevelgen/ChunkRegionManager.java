package serverlevelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.math.Vector2f;

import ClientDataManager.Client;
import ClientDataManager.ClientDataManager;
import ServerRigidBodies.ServerRigidBody;
import java.util.logging.Level;
import java.util.logging.Logger;
import worldGen.MapDataGenerator;

public class ChunkRegionManager {

    private Queue<ChunkRegion> activeRegions = new ConcurrentLinkedQueue<ChunkRegion>();
    
    ClientDataManager clientData;
    MapDataGenerator mapGen;
    WorldSettings worldSettings;
    
    int regionSize = 16;

    public ChunkRegionManager(ClientDataManager ClientDataManager, MapDataGenerator MapDataGenerator)
    {
        clientData = ClientDataManager;
        mapGen = MapDataGenerator;
        worldSettings = mapGen.worldSettings;
    }

    public void processActiveRegions() 
    {
    	//sorts rigidbodies to correct chunkregion
        Queue<ServerRigidBody> movedRigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
        for(ChunkRegion chunkRegion : activeRegions)
        {
        	movedRigidBodies.addAll(chunkRegion.updateRigidBodies());
    	}
        
        for(ServerRigidBody rigidBody : movedRigidBodies)
        {
        	int regX = (int)rigidBody.getPosition().x/worldSettings.chunkSize/worldSettings.regionSize;
			int regZ = (int)rigidBody.getPosition().z/worldSettings.chunkSize/worldSettings.regionSize;
        	ChunkRegion correctRegion = getChunkRegion(regX,regZ);
        	correctRegion.addRigidBody(rigidBody);
        }
        
        List<Client> clients = clientData.getClientList();
        List<Vector2f> clientRegionPositions = new ArrayList<Vector2f>();
        for (int i = 0; i < clients.size(); i++)
        {
            int chunkIDX = (int) Math.floor((clients.get(i).getPosition().x - (clients.get(i).getRenderDistance() * worldSettings.chunkSize)) / worldSettings.chunkSize);
            int chunkIDZ = (int) Math.floor((clients.get(i).getPosition().z - (clients.get(i).getRenderDistance() * worldSettings.chunkSize)) / worldSettings.chunkSize);

            int regionIDX = (int) Math.floor(chunkIDX / regionSize);
            int regionIDZ = (int) Math.floor(chunkIDZ / regionSize);

            for (int x = 0; x < 2; x++) 
            {
                for (int y = 0; y < 2; y++) 
                {
                    Vector2f clientRegion = new Vector2f(regionIDX + x, regionIDZ + y);

                    if (!clientRegionPositions.contains(clientRegion)) 
                    {
                        clientRegionPositions.add(clientRegion);
                    }
                }
            }
        }

        for (int i = 0; i < clientRegionPositions.size(); i++) 
        {
            int idx = (int) clientRegionPositions.get(i).x;
            int idz = (int) clientRegionPositions.get(i).y;

            if (idx >= 0 && idz >= 0 && idx < 8 && idz < 8) 
            {
                if (!hasActiveRegion(idx, idz)) 
                {
                    if (ChunkRegionIOUtil.hasImport(mapGen.map.mapName, idx, idz)) 
                    {
                    	boolean hasChunk = false;
                    	for(ChunkRegion region : activeRegions)
                    	{
                    		if(region.getIDX() == idx && region.getIDZ() == idz)
                    		{
                    			hasChunk = true;
                    			break;
                    		}
                    	}
                    	if(!hasChunk)
                    	{
                    		ChunkRegion loadedRegion = ChunkRegionIOUtil.importChunkRegion(worldSettings, mapGen.map.mapName,idx, idz);
	                        if (loadedRegion == null) 
	                        {
	                            ChunkRegion region = new ChunkRegion(worldSettings, mapGen.map.mapName,idx, idz);
	                            activeRegions.add(region);
	                        } 
	                        else 
	                        {
	                            activeRegions.add(loadedRegion);
	                        }
                    	}
                    } 
                    else 
                    {
                        ChunkRegion region = new ChunkRegion(worldSettings, mapGen.map.mapName,idx, idz);
                        activeRegions.add(region);
//                        region.export();
                    }
                }
            }
        }

        for(ChunkRegion chunkregion : activeRegions)
        {
            boolean hasRegion = false;
            for(Vector2f regions : clientRegionPositions)
            {
            	if(regions.x == chunkregion.getIDX() && regions.y == chunkregion.getIDZ())
            	{
            		hasRegion = true;
            		break;
            	}
            }
            if(!hasRegion)
            {
            	if(chunkregion.hasFinishedExport)
            	{
            		activeRegions.remove(chunkregion);
            	}
            	else
            	{
            		chunkregion.export();
            	}
            }
        }
    }

    public Queue<Chunk> getActiveChunks() 
    {
        List<Client> clientList = clientData.getClientList();
        List<Vector2f> activePositions = new ArrayList<Vector2f>();
        for (int i = 0; i < clientList.size(); i++) {
            int chunkIDX = (int) clientList.get(i).getPosition().getX() / worldSettings.chunkSize;
            int chunkIDZ = (int) clientList.get(i).getPosition().getZ() / worldSettings.chunkSize;

            int renderDistance = clientList.get(i).getRenderDistance();

            for (int countX = -renderDistance; countX <= renderDistance; countX++) 
            {
                for (int countY = -renderDistance; countY <= renderDistance; countY++) 
                {
                    int px = chunkIDX + countX;
                    int py = chunkIDZ + countY;

                    if (px > 0 && py > 0 && px < worldSettings.worldSize && py < worldSettings.worldSize)
                    {
                    	boolean hasPos = false;
                    	for(int j = 0; j < activePositions.size(); j++)
                    	{
                    		if( (int)activePositions.get(j).x == px && (int)activePositions.get(j).y == py)
                    		{
                    			hasPos = true;
                    			break;
                    		}
                    	}
                    	if(!hasPos)
                    	{
                    		activePositions.add(new Vector2f(px, py));
                    	}
                    }
                }
            }
        }

        for (int i = 0; i < activePositions.size(); i++) 
        {	
            int chunkIDX = (int) activePositions.get(i).x;
            int chunkIDZ = (int) activePositions.get(i).y;

            int regIDX = chunkIDX / regionSize;
            int regIDZ = chunkIDZ / regionSize;

            if (hasActiveRegion(regIDX, regIDZ)) 
            {
                ChunkRegion chunkRegion = getChunkRegion(regIDX, regIDZ);
                if (!chunkRegion.containsChunk(chunkIDX, chunkIDZ)) 
                {
                    Chunk chunk = chunkRegion.requestChunk(chunkIDX, chunkIDZ);
                    if (chunk != null) 
                    {
                        chunkRegion.addChunk(chunk);
                    }
                    else 
                    {
                    	//generate chunk data
                        Object[] chunkData = mapGen.CreateChunkData(chunkIDX, chunkIDZ);
                        Chunk newChunk = new Chunk(chunkIDX, chunkIDZ, (short[][][]) chunkData[0]);
                        chunkRegion.addChunk(newChunk);
                        
                        List<ServerRigidBody> RigidBodyList = (List<ServerRigidBody>) chunkData[1];
                        for(int j = 0; j < RigidBodyList.size(); j++)
                        {
                        	chunkRegion.addRigidBody(RigidBodyList.get(j));
                        }
                    }
                }
            }
        }
        
        Queue<Chunk> activeChunkList = new ConcurrentLinkedQueue<Chunk>();
        for (ChunkRegion chunkRegion : activeRegions) 
        {
            Queue<Chunk> regionChunks = chunkRegion.getChunkList();
            for (Chunk chunk : regionChunks) 
            {
                Vector2f position = new Vector2f(chunk.chunkIDX, chunk.chunkIDZ);
                if (activePositions.contains(position)) 
                {
                    activeChunkList.add(chunk);
                } 
                else 
                {
                	chunkRegion.unloadChunk(chunk.chunkIDX, chunk.chunkIDZ);
                }
            }
        }
        return activeChunkList;
    }
    
    public Queue<ServerRigidBody> getActiveRigidBodies()
    {
        Queue<ServerRigidBody> activeBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
        for(ChunkRegion chunkRegion : activeRegions)
        {
        	activeBodies.addAll( chunkRegion.getRigidBodyList());
        }
        return activeBodies;
    }

    public void addRigidBody(ServerRigidBody newRigidBody)
    {
    	int regIDX = (int)(newRigidBody.getPosition().x/worldSettings.chunkSize)/worldSettings.regionSize;
    	int regIDZ = (int)(newRigidBody.getPosition().z/worldSettings.chunkSize)/worldSettings.regionSize;
    	
    	if(newRigidBody.getID() == -1)
    	{
    		newRigidBody.setID(getWorldSettings().getRigidBodyIDCount());
    		getWorldSettings().incrementRigidBodyCount();
    	}
    	if(hasActiveRegion(regIDX,regIDZ))
    	{
    		getChunkRegion(regIDX,regIDZ).addRigidBody(newRigidBody);
    	}
    }
    
    public Queue<Chunk> getUnloadedChunks()
    {
    	Queue<Chunk> unloadedChunks = new ConcurrentLinkedQueue<Chunk>();
    	for (ChunkRegion chunkRegion : activeRegions) 
        {
    		unloadedChunks.addAll(chunkRegion.getUnloadedChunks());
    	}
    	return unloadedChunks;
    }
    
    public Queue<ServerRigidBody> getUnloadedRigidBodies()
    {
    	Queue<ServerRigidBody> unloadedRigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
    	for (ChunkRegion chunkRegion : activeRegions) 
        {
    		unloadedRigidBodies.addAll(chunkRegion.getUnloadedRigidBodies());
    	}
    	return unloadedRigidBodies;
    }
    
    public boolean hasActiveRegion(int IDX, int IDZ) 
    {
        for (ChunkRegion chunkRegion : activeRegions) 
        {
            if (chunkRegion.getIDX() == IDX && chunkRegion.getIDZ() == IDZ) 
            {
                return true;
            }
        }
        return false;
    }

    public ChunkRegion getChunkRegion(int IDX, int IDZ) 
    {
    	for (ChunkRegion chunkRegion : activeRegions) 
        {
            if (chunkRegion.getIDX() == IDX && chunkRegion.getIDZ() == IDZ) 
            {
                return chunkRegion;
            }
        }
        return null;
    }

    public ChunkRegion importChunkRegion(int IDX, int IDZ) {
        return null;
    }

    //todo
    //add sleep until finished loading
    public void saveChunkRegions()
    {	
    	for (ChunkRegion chunkRegion : activeRegions) 
        {
    		chunkRegion.saveActiveChunkData();
    		chunkRegion.export();
        }
    	
        mapGen.exportWorldSettings();
        try 
        {
        	boolean hasSaved = false;
            while(!hasSaved)
            {
            	hasSaved = true;
            	for (ChunkRegion chunkRegion : activeRegions) 
                {
            		if(!chunkRegion.hasFinishedExport)
            		{
            			hasSaved = false;
            		}
            	}
            	Thread.sleep(1000);
            }
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(ChunkRegionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public WorldSettings getWorldSettings()
    {
    	return mapGen.worldSettings;
    }
    
}
