package serverlevelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import ClientDataManager.Client;
import ClientDataManager.ClientDataManager;
import ClientServer.NetworkPacket;
import Main.Main;
import worldGen.MapDataGenerator;

public class ServerChunkManager {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    ChunkManagerThread serverChunkManagerThread;
    ClientDataManager clientDataManager;
    ServerPhysicsManager physicsManager;

    MapDataGenerator mapGen;
    ChunkRegionManager regionManager;
    WorldSettings worldSettings;
    
    Queue<Chunk> activeChunks = new ConcurrentLinkedQueue<Chunk>(); 
    Queue<Chunk> unloadedChunks = new ConcurrentLinkedQueue<Chunk>(); 
    
    Main main;
    Node chunkNode;

    public ServerChunkManager(MapDataGenerator MapDataGenerator, ChunkRegionManager ChunkRegionManager, ClientDataManager ClientDataManager, ServerPhysicsManager ServerPhysicsManager, Main Main) 
    {
        mapGen = MapDataGenerator;
        worldSettings = mapGen.worldSettings;
        
        main = Main;
        clientDataManager = ClientDataManager;
        physicsManager = ServerPhysicsManager;

        regionManager = ChunkRegionManager;
        chunkNode = new Node("ServerChunkNode");
        main.getRootNode().attachChild(chunkNode);
        serverChunkManagerThread = new ChunkManagerThread();
       
        executor.execute(serverChunkManagerThread);
    }

    
    public void close()
    {
        regionManager.saveChunkRegions();
        
        main.getRootNode().detachChild(chunkNode);
        chunkNode.detachAllChildren();
        chunkNode = null;
        serverChunkManagerThread.isRunning = false;
        executor.shutdown();
        
    }
    
    
    private class ChunkManagerThread implements Runnable {

        boolean isRunning = true;
        private ChunkManagerThread() 
        {
        	
        }

        @Override
        public void run() 
        {
            long startTime = System.currentTimeMillis();
            long frameTime = System.currentTimeMillis();
            		
            Thread.currentThread().setName("Util-ServerChunkManager");
            while (isRunning) 
            {
                startTime = System.currentTimeMillis();
            	
                regionManager.processActiveRegions();
                activeChunks = regionManager.getActiveChunks();
                unloadedChunks = regionManager.getUnloadedChunks();
                
                removeOldChunks();

                processTerrainBackgroundRequests();
                processModificationRequests();
                updateChunk();
                processChunkData();
                checkChunks();

                frameTime = System.currentTimeMillis() - startTime;
                if (frameTime < 0) 
                {
                    frameTime = 0;
                } else if (frameTime > worldSettings.tickTime) {
                    frameTime = worldSettings.tickTime;
                }
                try 
                {
                    Thread.sleep(worldSettings.tickTime - frameTime);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }

    public void removeOldChunks() 
    {
    	while(!unloadedChunks.isEmpty())
    	{
    		Chunk unloadedChunk = unloadedChunks.poll();
    		if(unloadedChunk.Model != null)
    		{
    			DetachChunkFromNode(unloadedChunk.Model);
    		}
    	}
    }

    public void updateChunk() 
    {
    	for(Chunk chunk : activeChunks)
    	{
    		if (!chunk.hasProcessed) 
            {
                chunk.processChunk(worldSettings.chunkSize);
                
                if(chunk.Model != null)
                {
                	physicsManager.attachControl(chunk.control);
	                AttachChunkToNode(chunk.Model);
                }
            }
    	}
    }
	
    public void processTerrainBackgroundRequests() 
    {
        if (mapGen.map.bh == null) {
            return;
        }

        List<Client> clientList = clientDataManager.getClientList();
        for (int i = 0; i < clientList.size(); i++) 
        {
            if (clientList.get(i).needsTerrainBackground())
            {
                clientList.get(i).setNeedsTerrainBackground(false);
                sendBackGroundDataToClient(clientList.get(i));
            }
        }
    }

    public void processChunkData() 
    {
        List<Client> clientList = clientDataManager.getClientList();
        for (int i = 0; i < clientList.size(); i++) 
        {
            for (int j = 0; j < clientList.get(i).getNeededChunks().size(); j++) 
            {
                Chunk someChunk = getChunkByID(clientList.get(i).getNeededChunks().get(j));
                if (someChunk != null) {
                    sendChunkDataToClient(clientList.get(i), someChunk);
                }
            }
            clientList.get(i).getNeededChunks().clear();
        }
    }

    public void checkChunks() 
    {
        List<Client> clientList = clientDataManager.getClientList();
        Queue<Vector2f> activeChunkPositions = new ConcurrentLinkedQueue<Vector2f>();
        for (int i = 0; i < clientList.size(); i++) 
        {
            int chunkIDX = (int) clientList.get(i).getPosition().getX() / worldSettings.chunkSize;
            int chunkIDZ = (int) clientList.get(i).getPosition().getZ() / worldSettings.chunkSize;

            int renderDistance = clientList.get(i).getRenderDistance();

            List<Vector2f> ActivePositions = new ArrayList<Vector2f>();

            for (int countX = -renderDistance; countX <= renderDistance; countX++) 
            {
                for (int countY = -renderDistance; countY <= renderDistance; countY++) 
                {
                    int px = chunkIDX + countX;
                    int py = chunkIDZ + countY;

                    if (px > 0 && py > 0 && px < worldSettings.worldSize && py < worldSettings.worldSize) 
                    {
                        ActivePositions.add(new Vector2f(px, py));
                        
                    	activeChunkPositions.add(new Vector2f(px, py));
                    }
                }
            }

            for (int j = 0; j < ActivePositions.size(); j++) 
            {
                if (!clientList.get(i).getActiveChunks().contains(ActivePositions.get(j))) 
                {
                    clientList.get(i).getNeededChunks().add(ActivePositions.get(j));
                }
            }

            //remove inactive player chunks
            for (int j = 0; j < clientList.get(i).getActiveChunks().size(); j++) 
            {
                if (!ActivePositions.contains((clientList.get(i).getActiveChunks().get(j)))) 
                {
                    clientList.get(i).getActiveChunks().remove(j);
                    break;
                }
            }
            clientList.get(i).setActiveChunks(ActivePositions);
        }
        physicsManager.checkControlPositions(activeChunkPositions);
    }

    public void processModificationRequests() {
        List<Vector2f> affectedChunks = new ArrayList<Vector2f>();
        List<Client> clientList = clientDataManager.getClientList();

        for (int i = 0; i < clientList.size(); i++) {
            while (clientList.get(i).getCellModificationPositions().size() > 0) 
            {
                if (clientList.get(i) == null) 
                {
                    continue;
                }
                if (clientList.get(i).getCellModificationPositions().peek() == null) 
                {
                    clientList.get(i).getCellModificationPositions().pop();
                    continue;
                }

                Vector3f worldPosition = clientList.get(i).getCellModificationPositions().pop();
                worldPosition = new Vector3f(Math.round(worldPosition.getX()), Math.round(worldPosition.getY()), Math.round(worldPosition.getZ()));

                Vector2f chunkPosition = new Vector2f((int) (worldPosition.getX() / worldSettings.chunkSize), (int) (worldPosition.getZ() / worldSettings.chunkSize));
                Vector3f cellPosition = new Vector3f((int) worldPosition.getX() % worldSettings.chunkSize, (int) worldPosition.getY(), (int) worldPosition.getZ() % worldSettings.chunkSize);

                int destructionType = clientList.get(i).getCellDestructionType().pop();
                int MaterialType = clientList.get(i).getMaterialType().pop();

                if (cellPosition.getX() % worldSettings.chunkSize == 0 && cellPosition.getZ() % worldSettings.chunkSize == 0)
                {
                    Vector2f otherChunkPositionOne = new Vector2f(chunkPosition.getX() - 1, chunkPosition.getY());
                    Vector2f otherChunkPositionTwo = new Vector2f(chunkPosition.getX(), chunkPosition.getY() - 1);
                    Vector2f otherChunkPositionThree = new Vector2f(chunkPosition.getX() - 1, chunkPosition.getY() - 1);

                    Vector3f otherCellPositionOne = new Vector3f(worldSettings.chunkSize, cellPosition.getY(), cellPosition.getZ());
                    Vector3f otherCellPositionTwo = new Vector3f(cellPosition.getX(), cellPosition.getY(), worldSettings.chunkSize);
                    Vector3f otherCellPositionThree = new Vector3f(worldSettings.chunkSize, cellPosition.getY(), worldSettings.chunkSize);

                    Chunk chunkTwo = getChunkByID(otherChunkPositionOne);
                    Chunk chunkThree = getChunkByID(otherChunkPositionTwo);
                    Chunk chunkFour = getChunkByID(otherChunkPositionThree);

                    if (chunkTwo != null) 
                    {
                        boolean hasChange = chunkTwo.modifyChunk(otherCellPositionOne, destructionType, MaterialType);
                        if (hasChange) {
                            affectedChunks.add(otherChunkPositionOne);
                        }
                    }
                    if (chunkThree != null) 
                    {

                        boolean hasChange = chunkThree.modifyChunk(otherCellPositionTwo, destructionType, MaterialType);
                        if (hasChange) {
                            affectedChunks.add(otherChunkPositionTwo);
                        }
                    }
                    if (chunkFour != null) 
                    {
                        boolean hasChange = chunkFour.modifyChunk(otherCellPositionThree, destructionType, MaterialType);
                        if (hasChange) {
                            affectedChunks.add(otherChunkPositionThree);
                        }
                    }
                } else if (cellPosition.getX() % worldSettings.chunkSize == 0) 
                {
                    Vector2f otherChunkPosition = new Vector2f(chunkPosition.getX() - 1, chunkPosition.getY());
                    Vector3f otherCellPosition = new Vector3f(worldSettings.chunkSize, cellPosition.getY(), cellPosition.getZ());

                    Chunk otherChunk = getChunkByID(otherChunkPosition);

                    if (otherChunk != null) 
                    {
                        boolean hasChange = getChunkByID(otherChunkPosition).modifyChunk(otherCellPosition, destructionType, MaterialType);
                        if (hasChange) {
                            affectedChunks.add(otherChunkPosition);
                        }
                    }
                } 
                else if (cellPosition.getZ() % worldSettings.chunkSize == 0) 
                {
                    Vector2f otherChunkPosition = new Vector2f(chunkPosition.getX(), chunkPosition.getY() - 1);
                    Vector3f otherCellPosition = new Vector3f(cellPosition.getX(), cellPosition.getY(), worldSettings.chunkSize);

                    Chunk otherChunk = getChunkByID(otherChunkPosition);

                    if (otherChunk != null) 
                    {
                        boolean hasChange = otherChunk.modifyChunk(otherCellPosition, destructionType, MaterialType);
                        if (hasChange) 
                        {
                            affectedChunks.add(otherChunkPosition);
                        }
                    }
                }

                Chunk someChunk = getChunkByID(chunkPosition);
                if (someChunk != null) {
                    boolean hasChange = someChunk.modifyChunk(cellPosition, destructionType, MaterialType);
                    if (hasChange) {
                        affectedChunks.add(chunkPosition);
                    }
                }
            }
        }

        for (int i = 0; i < affectedChunks.size(); i++) 
        {
            for (int j = 0; j < clientList.size(); j++)
            {
                if (clientList.get(j).hasChunk(affectedChunks.get(i)) && !clientList.get(j).getNeededChunks().contains(clientList.get(j)))
                {
                    clientList.get(j).getNeededChunks().add(affectedChunks.get(i));
                }
            }
        }
    }

    public Chunk getChunkByID(int px, int py) 
    {
    	for(Chunk chunk : activeChunks)
    	{
    		if(chunk.getChunkIDX() == px && chunk.getChunkIDZ() == py) 
    		{
    			return chunk;
    		}
    	}
        return null;
    }

    public Chunk getChunkByID(Vector2f Position) 
    {
    	for(Chunk chunk : activeChunks)
    	{
    		 if (chunk.getChunkIDX() == (int) Position.getX() && chunk.getChunkIDZ() == (int) Position.getY()) {
                 return chunk;
             }
    	}
        return null;
    }

    public Queue<Chunk> getChunkList() 
    {
        return activeChunks;
    }

    public boolean containsChunk(int px, int py) 
    {
    	for(Chunk chunk : activeChunks)
    	{
    		if (chunk.getChunkIDX() == px && chunk.getChunkIDZ() == py) {
                return true;
            }
    	}
        return false;
    }

    public void AttachChunkToNode(final Geometry SomeGeometry) 
    {
        main.enqueue(new Callable<Spatial>()
    	{
            public Spatial call() throws Exception {

            	if(SomeGeometry == null || chunkNode == null)
            	{
            		return null;
            	}
            	
                Spatial geom = SomeGeometry;
                for (int i = 0; i < chunkNode.getChildren().size(); i++) {
                    if (chunkNode.getChild(i).getName().equals(geom.getName())) {
                        chunkNode.detachChildAt(i);
                        break;
                    }
                }
                chunkNode.attachChild(geom);
                return null;
            }
        });
    }

    public void DetachChunkFromNode(final Geometry SomeGeometry) {
        main.enqueue(new Callable<Spatial>() {
            public Spatial call() throws Exception {

            	if(SomeGeometry == null || chunkNode == null)
            	{
            		return null;
            	}
            	
                Spatial geom = SomeGeometry;
                for (int i = 0; i < chunkNode.getChildren().size(); i++) {
                    if (chunkNode.getChild(i).getName().equals(geom.getName())) {
                        chunkNode.detachChildAt(i);
                        return null;
                    }
                }
                return null;
            }
        });
    }

    public void sendChunkDataToClient(Client Client, Chunk Chunk) 
    {
        byte[] ChunkData = ChunkManagerUtil.GenerateCompressedChunkData(Chunk.chunkIDX, Chunk.chunkIDZ, Chunk.materials);
        //if(ChunkData.length > 0)
        //{
//		Client.addOutBoundTCP(new NetworkPacket(ChunkData, Client.getName(), 3+"", 0+""));
        Client.addOutBoundTCP(new NetworkPacket(Client.getName(), ChunkData, new int[]{3, 0}));
        //}
    }

    public void sendBackGroundDataToClient(Client Client) 
    {
        byte[] ChunkData = ChunkManagerUtil.GenerateBackgroundData(mapGen.map, worldSettings);
//		Client.addOutBoundTCP(new NetworkPacket(ChunkData, Client.getName(), 3+"", 1+""));
        Client.addOutBoundTCP(new NetworkPacket(Client.getName(), ChunkData, new int[]{3, 1}));
    }

//    public int calculateChunkMaterialDepth(Vector3f Position, int...Materials)
//    {
//    	int chunkIDX = (int) Position.x / worldSettings.chunkSize - 1;
//        int chunkIDZ = (int) Position.z / worldSettings.chunkSize - 1;
//        Chunk chunk = getChunkByID(chunkIDX, chunkIDZ);
//        if (chunk == null) 
//        {
//            return 0;
//        }
//        else if (Position.y > 127 || Position.y < 0) 
//        {
//            return 0;
//        }
//    	return chunk.calculateDepth((int) Position.x % worldSettings.chunkSize, (int) Position.y, (int) Position.z % worldSettings.chunkSize, Materials);
//    	
//    }
    
    public float calculateChunkMaterialDepth(Vector3f Position, int...Materials)
    {
    	int chunkIDX = (int) Position.x / worldSettings.chunkSize - 1;
        int chunkIDZ = (int) Position.z / worldSettings.chunkSize - 1;
        Chunk chunk = getChunkByID(chunkIDX, chunkIDZ);
        if (chunk == null) 
        {
            return 0;
        }
        else if (Position.y > 127 || Position.y < 0) 
        {
            return 0;
        }
    	return chunk.calculateDepth(new Vector3f(Position.x%worldSettings.chunkSize,Position.y%worldSettings.worldHeight,Position.z%worldSettings.chunkSize), Materials);
    }
    
    public boolean hasVoxel(Vector3f Position) {
        int chunkIDX = (int) Position.x / worldSettings.chunkSize - 1;
        int chunkIDZ = (int) Position.z / worldSettings.chunkSize - 1;
        Chunk Chunk = getChunkByID(chunkIDX, chunkIDZ);
        if (Chunk == null) {
            return false;
        }
        else if (Position.y > 127 || Position.y < 0) 
        {
            return false;
        }
        return Chunk.GetVertexValue((int) Position.x % worldSettings.chunkSize, (int) Position.y, (int) Position.z % worldSettings.chunkSize);
    }

    public boolean[] getVoxelValue(Vector3f Position) 
    {
        int chunkIDX = (int) Position.x / worldSettings.chunkSize;
        int chunkIDZ = (int) Position.z / worldSettings.chunkSize;

        Chunk Chunk = getChunkByID(chunkIDX, chunkIDZ);
        if (Chunk == null) 
        {
            return new boolean[9];
        }
        boolean[] val = new boolean[9];
        boolean[] result = Chunk.getVoxelValue((int) Position.x % worldSettings.chunkSize, (int) Position.y, (int) Position.z % worldSettings.chunkSize);

        for (int i = 0; i < result.length; i++) {
            val[i] = result[i];
        }
        val[8] = true;

        return val;
    }

    public short getVertexValue(Vector3f Position)
    {
    	 int chunkIDX = (int) Position.x / worldSettings.chunkSize;
         int chunkIDZ = (int) Position.z / worldSettings.chunkSize;

         Chunk chunk = getChunkByID(chunkIDX, chunkIDZ);
         if (chunk == null) 
         {
             return 0;
         }
         
         return chunk.getVertexMaterial((int)Position.getX() % worldSettings.chunkSize, (int)Position.getY() % worldSettings.worldHeight, (int)Position.getZ() % worldSettings.chunkSize);
    }
    
    public float getTopHeight(int px, int pz)
    {
    	int chunkIDX = px / worldSettings.chunkSize - 1;
        int chunkIDZ = pz / worldSettings.chunkSize - 1;
    	
        Chunk Chunk = getChunkByID(chunkIDX, chunkIDZ);
        if (Chunk == null) 
        {
        	return 0;
        }
        return Chunk.getMaxHeight(px % worldSettings.chunkSize, pz % worldSettings.chunkSize);
    }
    
    public boolean hasChunkLoaded(int IDX, int IDZ) 
    {
    	for(Chunk chunk : activeChunks)
    	{
    		if (chunk.getChunkIDX() == IDX && chunk.getChunkIDZ() == IDZ) {
                return chunk.Model != null;
            }
    	}
        return false;
    }

}
