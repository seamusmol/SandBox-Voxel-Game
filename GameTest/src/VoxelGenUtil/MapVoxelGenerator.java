package VoxelGenUtil;

import com.jme3.math.Vector3f;

import Configs.ServerSettings;
import Util.MapUtil;
import serverlevelgen.WorldSettings;
import worldGen.IslandMap;

public class MapVoxelGenerator {

	public static Object[] GenerateUnderGround(int SX, int SY, IslandMap Map, WorldSettings WorldSettings)
	{
		//return GenerateUnderGroundCaves(SX, SY, HeightMap, Voxels);
		short[][][] materials = new short[WorldSettings.chunkSize+1][WorldSettings.worldHeight][WorldSettings.chunkSize+1];
		
		if(Map.hasGeneration)
		{
			GenerateTunnels(SX, SY, Map, materials);
		}
		else
		{
			generateBlankVoxels(SX, SY, Map, materials);
		}
		
		GenerateCaveMaterials(SX, SY, Map, materials);
		
		Object[] chunkData = {materials};
		
		return chunkData;
	}
	
	public static void GenerateTunnels(int SX, int SY, IslandMap Map, short[][][] Materials)
	{
		//generate Voxels
		for(int i = 0; i < Materials.length; i++)
		{
			for(int k = 0; k < Materials[0][0].length; k++)
			{
				for(int j = 0; j <= Map.h[SX+i][SY+k]; j++)
				{
					Materials[i][j][k] = 1;
				}
			}
		}
		
		//generate Noise for tunnel vertices
		int[][] noise = new int[4][4];
		for(int i = 0; i < noise.length; i++)
		{
			for(int j = 0; j < noise[0].length; j++)
			{
				//TODO
				int px = SX + i*32 - 32 < Map.nm.length ? SX + i*32 - 32 : Map.nm.length-1;
				int py = SY + j*32 - 32 < Map.nm[0].length ? SY + j*32 - 32 : Map.nm[0].length-1;
				noise[i][j] = Map.nm[px][py];
			}
		}
		
		int oceanDist = 32;
		int lakeDist = 32;
		
		boolean[][][] caveNoise = new boolean[4][11][4];
		for(int i = 0; i < noise.length; i++)
		{
			for(int k = 0; k < noise[0].length; k++)
			{
				int val = (noise[i][k] % 64)/8;
				
				int od = (int)MapUtil.getBudgetDistance(SX + i * 32 - 32, SY + k * 32 - 32, Map.o, oceanDist);
				int lkd = (int)MapUtil.getBudgetDistance(SX + i * 32 - 32, SY + k * 32 - 32, Map.lk, lakeDist);
				
				if(od != -1)
				{
					val = (noise[i][k] % 32)/8;
				}
				else if(lkd != -1)
				{
					val = (noise[i][k] % 48)/8;
				}
				
				val = val > 1 ? val: 1;
				caveNoise[i][val][k] = true;
			}
		}
		
		for(int i = 0; i <= 1; i++)
		{
			for(int j = 1; j < caveNoise[0].length-1; j++)
			{
				for(int k = 0; k <= 1; k++)
				{
					if(caveNoise[i+1][j][k+1])
					{
						for(int countX = -1; countX <= 1; countX++)
						{
							for(int countY = -1; countY <= 1; countY++)
							{
								for(int countZ = -1; countZ <= 1; countZ++)
								{
									if(i + countX + 1 >= 0 && i + countX + 1 < caveNoise.length)
									{
										if(j + countY + 1 >= 0 && j + countY + 1 < caveNoise[0].length)
										{
											if(k + countZ + 1 >= 0 && k + countZ + 1 < caveNoise[0][0].length)
											{
												if(caveNoise[i + countX + 1][j + countY][k + countZ + 1])
												{
													Vector3f P1 = new Vector3f(i * 32, j * 8, k * 32);
													Vector3f P2 = new Vector3f(i * 32 + countX * 32,j * 8 + countY * 8,k * 32 + countZ * 32);
													
													//Voxels = VoxelGenUtil.GenerateEnding(Voxels, P1, 4);
//													Voxels = VoxelGenUtil.GenerateTunnel(Voxels, P1, P2, 3);
//													Voxels = VoxelGenUtil.GenerateTunnel(Voxels, P2, P1, 3);
													
													int w1 = noise[i+1][k+1] % 5 + 1;
													int w2 = noise[i + countX + 1][k + countZ + 1] % 5 + 1;
													
													int ampx = 4;
													Materials = VoxelGenUtil.GenerateCurvedTunnel(Materials, P1, P2, w1, w2, 4, 4, 4);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void generateBlankVoxels(int SX, int SY, IslandMap Map, short[][][] Materials)
	{
		for(int i = 0; i < Materials.length; i++)
		{
			for(int k = 0; k < Materials[0][0].length; k++)
			{
				for(int j = 0; j <= Map.h[SX+i][SY+k]; j++)
				{
					Materials[i][j][k] = 1;
				}
			}
		}
	}
	
	public static void GenerateCaveMaterials(int SX, int SY, IslandMap Map, short[][][] Materials)
	{
		short stoneMat = 7;
		int surfaceDepth = 1;
		int soilLayerDepth = 3;
		
		for(int i = 0; i < Materials.length; i++)
		{
			for(int k = 0; k < Materials[0][0].length; k++)
			{
				for(int j = 0; j < Map.h[SX+i][SY+k]-surfaceDepth-soilLayerDepth; j++)
				{
					if(Materials[i][j][k] != 0)
					{
						Materials[i][j][k] = stoneMat;
					}
				}
			}
		}
		
		for(int i = 0; i < Materials.length; i++)
		{
			for(int k = 0; k < Materials[0][0].length; k++)
			{
				for(int j = Map.h[SX+i][SY+k] - surfaceDepth - soilLayerDepth; j <= Map.h[SX+i][SY+k] - surfaceDepth; j++)
				{
					if(Materials[i][j][k] != 0)
					{
						Materials[i][j][k] = stoneMat;
					}
				}
			}
		}
		
		//cave level
		for(int i = 0; i < Materials.length; i++)
		{
			for(int k = 0; k < Materials[0][0].length; k++)
			{
				for(int j = Map.h[SX+i][SY+k] - surfaceDepth - soilLayerDepth; j <= Map.h[SX+i][SY+k]; j++)
				{
					if(Materials[i][j][k] != 0)
					{
						short surfaceMat = (short)Map.ma[SX+i][SY+k];
						
						Materials[i][j][k] = surfaceMat;
					}
				}
			}
		}
	}
	
}
