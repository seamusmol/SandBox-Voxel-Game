package Villages;

import com.jme3.math.Vector2f;

import Util.MapUtil;
import VoxelGenUtil.StructureGenUtil;
import worldGen.IslandMap;

public class CoastSettlement extends Settlement {

	
	public CoastSettlement(Vector2f Position, IslandMap Map) 
	{
		super(Position, Map);
	}

	@Override
	public void GenerateLayout()
	{
		int blockSize = 64;
		townSize = 192;
		
		boolean[][] townshadeMap = new boolean[IslandMap.l.length][IslandMap.l[0].length];
		
		for(int i = -townSize; i < townSize; i++)
		{
			for(int j = -townSize; j < townSize; j++)
			{
				int px = (int)(centerPosition.x + i);
				int py = (int)(centerPosition.y + j);
				
				if(px >= 0 && py >= 0 && px < townshadeMap.length && py < townshadeMap[0].length)
				{
					
					townshadeMap[px][py] = true;
				}
			}
		}
		
		int roadwidth = 4;
		
		boolean[][] cornerMap = new boolean[townshadeMap.length/blockSize][townshadeMap[0].length/blockSize];
		for(int i = 1; i < townshadeMap.length/blockSize; i++)
		{
			for(int j = 1; j < townshadeMap[0].length/blockSize; j++)
			{
				int px = i * blockSize;
				int py = j * blockSize;
				
				if(px >= 0 && py >= 0 && px < IslandMap.l.length && py < IslandMap.l[0].length)
				{
					if(townshadeMap[px][py] && IslandMap.l[px][py])
					{
						int sideValue = 0;
						sideValue+= MapUtil.IsMapType(new Vector2f(px,py), new Vector2f(px - blockSize, py), IslandMap.l) ? 1:0;
						sideValue+= MapUtil.IsMapType(new Vector2f(px,py), new Vector2f(px, py - blockSize), IslandMap.l) ? 1:0;
						sideValue+= MapUtil.IsMapType(new Vector2f(px,py),new Vector2f(px+blockSize, py), IslandMap.l) ? 1:0;
						sideValue+= MapUtil.IsMapType(new Vector2f(px,py), new Vector2f(px, py + blockSize), IslandMap.l) ? 1:0;
						
						if(sideValue > 0)
						{
							cornerMap[i][j] = true;
							StructureGenUtil.GenerateIntersection(new Vector2f(px,py), IslandMap, true);
						}
					}
				}
			}
		}
		
		int[][] sideMap = new int[townshadeMap.length/blockSize][townshadeMap[0].length/blockSize];
		//construct roads
		for(int i = 1; i < townshadeMap.length/blockSize; i++)
		{
			for(int j = 1; j < townshadeMap[0].length/blockSize; j++)
			{
				int px = i * blockSize;
				int py = j * blockSize;
				
				int sideValue = 0;
				
				if(px >= 0 && py >= 0 && px < IslandMap.l.length && py < IslandMap.l[0].length)
				{
					if(townshadeMap[px][py] && IslandMap.l[px][py])
					{
						if(townshadeMap[px-blockSize][py] && IslandMap.l[px-blockSize][py])
						{
							if(Math.abs(IslandMap.h[px][py] - IslandMap.h[px-blockSize][py]) < blockSize/2)
							{
								if(MapUtil.IsMapType(new Vector2f(px,py), new Vector2f(px - blockSize, py), IslandMap.l))
								{
									sideValue += 1;
									StructureGenUtil.GenerateRoad(new Vector2f(px,py).add(new Vector2f(-roadwidth,0)), new Vector2f(px - blockSize, py).add(new Vector2f(roadwidth,0)), IslandMap, true);
								}
							}
						}
	
						if(townshadeMap[px][py-blockSize] && IslandMap.l[px][py-blockSize])
						{
							if(Math.abs(IslandMap.h[px][py] - IslandMap.h[px][py-blockSize]) < blockSize/2)
							{
								if(MapUtil.IsMapType(new Vector2f(px,py), new Vector2f(px, py - blockSize), IslandMap.l))
								{
									sideValue += 2;
									StructureGenUtil.GenerateRoad(new Vector2f(px,py).add(new Vector2f(0,-roadwidth)), new Vector2f(px, py - blockSize).add(new Vector2f(0,roadwidth)), IslandMap, true);
								}
							}
						}
						//
						if(townshadeMap[px+blockSize][py] && IslandMap.l[px+blockSize][py])
						{
							if(Math.abs(IslandMap.h[px][py] - IslandMap.h[px+blockSize][py]) < blockSize/2)
							{
								if(MapUtil.IsMapType(new Vector2f(px,py),new Vector2f(px+blockSize, py), IslandMap.l))
								{
									sideValue += 4;
									StructureGenUtil.GenerateRoad(new Vector2f(px,py).add(new Vector2f(roadwidth,0)), new Vector2f(px+blockSize, py).add(new Vector2f(-roadwidth,0)), IslandMap, true);
								}
							}
						}
						//
						if(townshadeMap[px][py+blockSize] && IslandMap.l[px][py+blockSize])
						{
							if(Math.abs(IslandMap.h[px][py] - IslandMap.h[px][py+blockSize]) < blockSize/2)
							{
								if(MapUtil.IsMapType(new Vector2f(px,py), new Vector2f(px, py + blockSize), IslandMap.l))
								{
									sideValue += 8;
									StructureGenUtil.GenerateRoad(new Vector2f(px,py).add(new Vector2f(0,roadwidth)), new Vector2f(px, py + blockSize).add(new Vector2f(0,-roadwidth)), IslandMap, true);
								}
							}
						}
						sideMap[i][j] = sideValue;
					}
				}
			}
		}
		
		StructureGenUtil.generateCityBlocks(IslandMap, centerPosition, 1, cornerMap, sideMap);
	}

}
