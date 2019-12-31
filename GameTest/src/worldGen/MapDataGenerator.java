package worldGen;

import java.util.ArrayList;
import java.util.List;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import ServerRigidBodies.ServerRigidBody;
import Util.MathUtil;
import VoxelGenUtil.MapVoxelGenerator;
import VoxelModels.VoxelModels;
import serverlevelgen.ChunkRegion;
import serverlevelgen.WorldSettings;


public class MapDataGenerator
{
    MapGenerator mapGen;
    
    public IslandMap map;
    public WorldSettings worldSettings;
    
    public boolean hasLoadedMap = true;
    
    public MapDataGenerator(Object[] Data)
    {	
    	if(Data.length == 0)
    	{
    		hasLoadedMap = false;
    	}
    	else if(Data.length == 1)
    	{
    		String name = ((String)Data[0]).replace("`", " ");
    		loadWorldSettings(name);
    		loadMap(name);
    		
    		if(map == null || worldSettings == null)
    		{
    			hasLoadedMap = false;
    		}
    	}
    	else if(Data.length == 4)
    	{
    		//load using imported heightmaps
    		String name = ((String)Data[0]).replace("`", "");
    		if(MapIOUtil.hasMap(name) || WorldSettings.hasWorlSettings(name))
        	{
        		int fileCount = 1;
            	while(MapIOUtil.hasMap(name + fileCount) || WorldSettings.hasWorlSettings(name + fileCount))
            	{
        			fileCount++;
            	}
            	name += fileCount;
        	}
    		
    		String heightMapPath = ((String)Data[1]).replace("`", " ");
    		String waterHeightMapPath = ((String)Data[2]).replace("`", " ");
    		String environmentMapPath = ((String)Data[3]).replace("`", " ");
    		
    		map = IslandImageMapImporter.generateMapfromImages(name, heightMapPath, waterHeightMapPath, environmentMapPath);
    		
    		int chunksize = 32;
    		int worldHeight = 128;
    		worldSettings = new WorldSettings(name, 1, 1, chunksize, 16,3, 1, map.l.length/chunksize, worldHeight);
    		map.generateBackgroundMaps(worldSettings.resolution);
    		
    		if(map == null)
    		{
    			hasLoadedMap = false;
    		}
    		else
    		{
    			MapIOUtil.exportMap(map);
    			worldSettings.exportSettings();
    		}
    	}
    	else
    	{
        	String name = ((String)Data[0]).replace("`", "");
        	String seed = ((String)Data[1]).replace("`", "");
        	String mapSize = ((String)Data[2]).replace("`", "");
        	String mapScale = ((String)Data[3]).replace("`", "");
        	String PlaceHolder = ((String)Data[4]).replace("`", "");
        	
        	if(name.length() == 0)
        	{
        		name = "untitled";
        	}
        	if(MapIOUtil.hasMap(name) || WorldSettings.hasWorlSettings(name))
        	{
        		int fileCount = 1;
            	while(MapIOUtil.hasMap(name + fileCount) || WorldSettings.hasWorlSettings(name + fileCount))
            	{
        			fileCount++;
            	}
            	name += fileCount;
        	}
        	
        	if(seed.length() == 0)
        	{
        		seed = System.currentTimeMillis() + "";
        	}
        	else if(seed.length() < 6)
        	{
        		int x = seed.length() * 97;
        		
        		for(int i = seed.length(); i < 6; i++)
        		{
        			seed+= x%10;
        			x+=x+i;
        		}
        	}
        	int chunksize = 32;
    		int worldHeight = 128;
        	int size = Integer.parseInt(mapSize);
        	int sx = MathUtil.stringToNumeric( seed.substring(0, seed.length()/2)) % size;
        	int sy = MathUtil.stringToNumeric( seed.substring(seed.length()/2, seed.length()-1)) % size;
        	
        	int scale = Integer.parseInt(mapScale);
        	
        	while(sx < size * scale/chunksize/2)
        	{
        		sx+= sx;
        	}
        	while(sy < size * scale/chunksize/2)
        	{
        		sy+= sy;
        	}
        	
    		worldSettings = new WorldSettings(name, sx, sy, chunksize, 16, 3, 1, size * scale/chunksize, worldHeight);
    		worldSettings.exportSettings();
        	
        	mapGen = new MapGenerator(worldSettings, name, scale, sx, sy, size, size);
            map = mapGen.IslandMap;
            map.generateBackgroundMaps(worldSettings.resolution);
            MapIOUtil.exportMap(map);
        	
    		hasLoadedMap = true;
    	}
    }
    
    public void loadMap(String Name)
    {
    	if(MapIOUtil.hasMap(Name))
    	{
    		map = MapIOUtil.loadMap(worldSettings, Name);
    	}
    }

    public void loadWorldSettings(String Name)
    {
    	if(WorldSettings.hasWorlSettings(Name))
    	{
    		worldSettings = new WorldSettings(WorldSettings.importWorldSettings(Name));
    	}
    }
    
    public void exportWorldSettings()
    {
    	worldSettings.exportSettings();
    }
    
    public ChunkRegion importChunkRegion()
    {
    	return null;
    }
    
    public Object[] CreateChunkData(int ChunkIDX, int ChunkIDZ)
    {
        short[][][] materials = new short[worldSettings.chunkSize + 1][ (int) worldSettings.worldHeight + 1][ worldSettings.chunkSize + 1];
    	
        if (ChunkIDX > 0 && ChunkIDX < worldSettings.worldSize && ChunkIDZ > 0 && ChunkIDZ < worldSettings.worldSize)
        {
        	int ox = ChunkIDX * worldSettings.chunkSize;
        	int oy = ChunkIDZ * worldSettings.chunkSize;
            
            Object[] VoxelData = MapVoxelGenerator.GenerateUnderGround(ChunkIDX * worldSettings.chunkSize, ChunkIDZ * worldSettings.chunkSize, map, worldSettings);
            
            materials = (short[][][]) VoxelData[0];
            
            for(int i = 0; i < materials.length; i++)
            {
        		for(int k = 0; k < materials[0][0].length; k++)
                {
        			for(int j = map.h[ox + i][oy+k]; j <= map.wh[ox + i][oy+k]; j++)
                    {
						materials[i][j][k] = 1023;
                    }
                }
            }
        }
        Object[] Data = new Object[2];
        Data[0] = materials;
        Data[1] = CreateRigidBodyData(ChunkIDX, ChunkIDZ);
        return Data;
    }

    //TODO 
    //Change voxelModel to new blender voxel model library
    public void DrawVoxelModel(String ModelName, int ChunkIDX, int ChunkIDZ, double Radian, Vector3f Position, boolean[][][] Voxels, short[][][] Materials)
    {
        short[][][] VoxelMaterials = VoxelModels.GetMaterialsByName(ModelName + "Materials");
        for (int i = 0; i < VoxelMaterials.length; i++)
        {
            for (int j = 0; j < VoxelMaterials[0].length; j++)
            {
                for (int k = 0; k < VoxelMaterials[0][0].length; k++)
                {
                    Vector2f rotPoint = MathUtil.RotatePoint(new Vector2f(i, k), Radian);

                    int PX = (int)Math.round(Position.x + rotPoint.x - ChunkIDX * worldSettings.chunkSize);
                    int PY = (int)Math.round(Position.y + j);
                    int PZ = (int)Math.round(Position.z + rotPoint.y - ChunkIDZ * worldSettings.chunkSize);

                    if (PX >= 0 && PX < Voxels.length)
                    {
                        if (PY >= 0 && PY < Voxels[0].length)
                        {
                            if (PZ >= 0 && PZ < Voxels[0][0].length)
                            {
                                if (VoxelMaterials[i][j][k] != 0)
                                {
                                    Materials[(int)PX][ (int)PY][ (int)PZ] = VoxelMaterials[i][ j][ k];
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public List<ServerRigidBody> CreateRigidBodyData(int ChunkIDX, int ChunkIDZ)
    {
		int px = ChunkIDX * worldSettings.chunkSize;
		int pz = ChunkIDZ * worldSettings.chunkSize;
		
		List<ServerRigidBody> newRigidBodies = new ArrayList<ServerRigidBody>();
		
		for(int i = 0; i < worldSettings.chunkSize; i++)
	    {
			for(int k = 0; k < worldSettings.chunkSize; k++)
	        {
				if(map.st[px + i][pz + k] != 0)
	 			{
					ServerRigidBody newRigidBody = generateStructureRigidBody(new Vector2f(px+i,pz+k), map.str[px + i][pz + k], map.st[px + i][pz + k], map.ma[px + i][pz + k]);
					newRigidBodies.add(newRigidBody);
					newRigidBody.setID(worldSettings.getRigidBodyIDCount());
					worldSettings.incrementRigidBodyCount();
	 			}
	        }
	    }
        return newRigidBodies;
    }

	private ServerRigidBody generateStructureRigidBody(Vector2f Position, float Rot, int ID, int Biome)
	{
		Object[] structureModel = VoxelModels.GenerateModel(ID, Biome);
		
		short[][][] materials = (short[][][]) structureModel[0];
		float scale = (float) structureModel[1];
		
		float radian = (float) (Rot * Math.PI/180f);
		
		Vector3f position = new Vector3f(Position.x, map.h[(int)Position.x][(int)Position.y] + (materials[0].length * scale), Position.y);
		Quaternion rot = new Quaternion();
		rot.fromAngleAxis(radian, Vector3f.UNIT_Y);
		
		//Vector3f Position, Quaternion Rotation, float VoxelScale, boolean[][][] Voxels, int[][][] Materials, boolean GhostPhysics
		ServerRigidBody structureRigidBody = new ServerRigidBody(position, rot, scale, materials, true);
		structureRigidBody.setID(worldSettings.getRigidBodyIDCount());
		worldSettings.incrementRigidBodyCount();
		
		return structureRigidBody;
	}
    
	
	
}
