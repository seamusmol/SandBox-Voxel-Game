package serverlevelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ServerRigidBodies.ServerRigidBody;

public class ChunkRegion {

	
	private Queue<Chunk> chunkList = new ConcurrentLinkedQueue<Chunk>();
	private Queue<Byte>[][] regionDataList = new ConcurrentLinkedQueue[0][0];
	
	private Queue<ServerRigidBody> rigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
	private Queue<ServerRigidBody> inactiveBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
	
	private Queue<Chunk> unloadedChunks = new ConcurrentLinkedQueue<Chunk>();
	private Queue<ServerRigidBody> unloadedRigidBodies = new ConcurrentLinkedQueue<ServerRigidBody>();
	
	boolean hasExport = false;
	boolean hasFinishedExport = false;
	
	String mapName = "";
	
	int idx;
	int idz;
	
	WorldSettings worldSettings;
	
	public ChunkRegion(WorldSettings WorldSettings, String MapName, int RegionIDX, int RegionIDZ, Queue<Byte>[][] RegionDataList, Queue<ServerRigidBody> RigidBodyList)
	{
		worldSettings = WorldSettings;
		mapName = MapName;
		idx = RegionIDX;
		idz = RegionIDZ;
		
		regionDataList = RegionDataList;
		rigidBodies = RigidBodyList;
		
		regionDataList = new ConcurrentLinkedQueue[worldSettings.regionSize][worldSettings.regionSize];
		for(int i = 0; i < regionDataList.length; i++)
		{
			for(int j = 0; j < regionDataList[0].length; j++)
			{
				if(regionDataList[i][j] == null)
				{
					regionDataList[i][j] = new ConcurrentLinkedQueue<Byte>();
				}
			}
		}
	}
	
	public ChunkRegion(WorldSettings WorldSettings, String MapName, int RegionIDX, int RegionIDZ)
	{
		worldSettings = WorldSettings;
		mapName = MapName;
		idx = RegionIDX;
		idz = RegionIDZ;
		
		regionDataList = new ConcurrentLinkedQueue[worldSettings.regionSize][worldSettings.regionSize];
		for(int i = 0; i < regionDataList.length; i++)
		{
			for(int j = 0; j < regionDataList.length; j++)
			{
				regionDataList[i][j] = new ConcurrentLinkedQueue<Byte>();
			}
		}
	}
	
	public void addRigidBody(ServerRigidBody RigidBody)
	{
		int px = (int)RigidBody.getPosition().x/worldSettings.chunkSize;
		int py = (int)RigidBody.getPosition().z/worldSettings.chunkSize;
		boolean hasChunk = false;
		
		for(Chunk chunk : chunkList)
		{
			if(px == chunk.getChunkIDX() && py == chunk.getChunkIDZ())
			{
				hasChunk = true;
				break;
			}
		}
		if(hasChunk)
		{
			rigidBodies.add(RigidBody);
			
		}
		else
		{
			inactiveBodies.add(RigidBody);
		}
		
	}
	//check if rigidBody in correct region
	//returns list of rigidbodies not in correct region(this)
	public List<ServerRigidBody> updateRigidBodies()
	{
		for(ServerRigidBody RigidBody : inactiveBodies)
		{
			int px = (int)RigidBody.getPosition().x/worldSettings.chunkSize;
			int py = (int)RigidBody.getPosition().z/worldSettings.chunkSize;
			for(Chunk chunk : chunkList)
			{
				if(px == chunk.getChunkIDX() && py == chunk.getChunkIDZ())
				{
					ServerRigidBody clone = new ServerRigidBody(RigidBody);
					rigidBodies.add(clone);
					inactiveBodies.remove(RigidBody);
					break;
				}
			}
		}
		
		for(ServerRigidBody RigidBody : rigidBodies)
		{
			int px = (int)RigidBody.getPosition().x/worldSettings.chunkSize;
			int py = (int)RigidBody.getPosition().z/worldSettings.chunkSize;
			boolean hasChunk = false;
			for(Chunk chunk : chunkList)
			{
				if(px == chunk.getChunkIDX() && py == chunk.getChunkIDZ())
				{
					hasChunk = true;
					break;
				}
			}
			if(!hasChunk)
			{
				ServerRigidBody clone = new ServerRigidBody(RigidBody);
				clone.deactivate();
				inactiveBodies.add(clone);
				unloadedRigidBodies.add(RigidBody);
				rigidBodies.remove(RigidBody);
			}
		}
		
		List<ServerRigidBody> incorrectRigidBodies = new ArrayList<ServerRigidBody>();
//		for(ServerRigidBody RigidBody : rigidBodies)
//		{
//			int regX = (int)RigidBody.getPosition().x/worldSettings.chunkSize/worldSettings.regionSize;
//			int regZ = (int)RigidBody.getPosition().z/worldSettings.chunkSize/worldSettings.regionSize;
//			
//			if(regX != idx || regZ != idz)
//			{
//				incorrectRigidBodies.add(RigidBody);	
//				rigidBodies.remove(RigidBody);
//			}
//		}
		return incorrectRigidBodies;
	}
	
	public Chunk requestChunk(int ChunkIDX, int ChunkIDZ)
	{
		//already active
		for(Chunk chunk : chunkList)
		{
			if(chunk.chunkIDX == ChunkIDX && chunk.chunkIDZ == ChunkIDZ )
			{
				return chunk;
			}
		}
		
		if(regionDataList[ChunkIDX% worldSettings.regionSize][ChunkIDZ%worldSettings.regionSize].size() == 0)
		{
			//doesn't exist
			return null;
		}
		else
		{
			Queue<Byte> data = regionDataList[ChunkIDX%worldSettings.regionSize][ChunkIDZ%worldSettings.regionSize];
			Object[] chunkData = ChunkManagerUtil.UnpackVoxelData(data);
			Chunk chunk = new Chunk((int)chunkData[0], (int)chunkData[1], (short[][][])chunkData[4]);
			chunkList.add(chunk);
			
			for(ServerRigidBody RigidBody : inactiveBodies)
			{
				int px = (int)RigidBody.getPosition().x/worldSettings.chunkSize;
				int pz = (int)RigidBody.getPosition().z/worldSettings.chunkSize;
				if(px == ChunkIDX && pz == ChunkIDZ)
				{
					ServerRigidBody clone = new ServerRigidBody(RigidBody);
					rigidBodies.add(clone);
					inactiveBodies.remove(RigidBody);
				}
			}
			return chunk;
		}
	}
	
	public void addChunk(Chunk Chunk)
	{
		chunkList.add(Chunk);
		int chunkIDX = Chunk.getChunkIDX();
		int chunkIDZ = Chunk.getChunkIDZ();
		
		Queue<Byte> data = ChunkManagerUtil.GenerateCompressedChunkDataList(chunkIDX, chunkIDZ, Chunk.materials);
		regionDataList[chunkIDX%worldSettings.regionSize][chunkIDZ%worldSettings.regionSize] = data;
	}
	
	public boolean containsChunk(int ChunkIDX, int ChunkIDZ)
	{
		for(Chunk chunk : chunkList)
		{
			if(chunk.chunkIDX == ChunkIDX && chunk.chunkIDZ == ChunkIDZ )
			{
				return true;
			}
		}
		return false;
	}
	
	public void unloadChunk(int ChunkIDX, int ChunkIDZ)
	{
		for(Chunk chunk : chunkList)
		{
			if(chunk.chunkIDX == ChunkIDX && chunk.chunkIDZ == ChunkIDZ )
			{
				regionDataList[ChunkIDX%worldSettings.regionSize][ChunkIDZ%worldSettings.regionSize] = ChunkManagerUtil.GenerateCompressedChunkDataList(ChunkIDX, ChunkIDZ, chunk.materials);
				unloadedChunks.add(chunk);
				chunkList.remove(chunk);
				return;
			}
		}
		
		for(ServerRigidBody RigidBody : inactiveBodies)
		{
			int px = (int)RigidBody.getPosition().x/worldSettings.chunkSize;
			int py = (int)RigidBody.getPosition().z/worldSettings.chunkSize;
			
			if(px == ChunkIDX && py == ChunkIDZ)
			{
				ServerRigidBody clone = new ServerRigidBody(RigidBody);
				rigidBodies.add(clone);
				inactiveBodies.remove(RigidBody);
				break;
			}
		}
		
	}
	
	public void saveActiveChunkData()
	{
		for(Chunk chunk : chunkList)
		{
			int chunkIDX = chunk.chunkIDX;
			int chunkIDZ = chunk.chunkIDZ;
			regionDataList[chunkIDX%worldSettings.regionSize][chunkIDZ%worldSettings.regionSize] = ChunkManagerUtil.GenerateCompressedChunkDataList(chunkIDX, chunkIDZ, chunk.materials);
		}
	}
	
	public Queue<ServerRigidBody> getRigidBodyList()
	{
		return rigidBodies;
	}
	
	public Queue<Chunk> getChunkList() {
		return chunkList;
	}

	//chunk
	//rigidbody
	public void export()
	{
            if(hasExport)
            {
                    return;
            }
            hasExport = true;
            hasFinishedExport = false;

            saveActiveChunkData();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            ChunkThreadExportThread exportThread = new ChunkThreadExportThread(mapName, this);
            executor.execute(exportThread);

            try
            {
               Thread.sleep(3000); 
            }
            catch(InterruptedException e)
            {
                executor.shutdown();
            }
            executor.shutdown();
                
	}
	
	public int getIDX() {
		return idx;
	}

	public int getIDZ() {
		return idz;
	}

	public Queue<ServerRigidBody> getInactiveBodies() {
		return inactiveBodies;
	}

	public Queue<Byte>[][] getRegionDataList() {
		return regionDataList;
	}
	
	public Queue<ServerRigidBody> getRigidBodies() {
		return rigidBodies;
	}

	public Queue<Chunk> getUnloadedChunks() {
		return unloadedChunks;
	}

	public Queue<ServerRigidBody> getUnloadedRigidBodies() {
		return unloadedRigidBodies;
	}
	
	public void resetUnloadedList()
	{
		unloadedRigidBodies.clear();
	}
	
	public void resetUnloadedChunkList()
	{
		unloadedChunks.clear();
	}
	
	public boolean hasFinishedExport()
	{
		return hasExport && hasFinishedExport;
	}
	
}
