package VoxelGenUtil;

import Configs.ServerSettings;
import Util.MapUtil;
import Util.MathUtil;
import worldGen.IslandMap;

public class ForestGenUtil {

	public static  void GenerateVegetation(IslandMap Map)
	{
		boolean[][] vegetationShadeMap = GenerateForestAreas(Map);
		generateForestVegetation(Map, vegetationShadeMap);
	}
	
	private static boolean[][] GenerateForestAreas(IslandMap Map)
	{
		int resolution = 32;
    	int forestTolerance = 50;
    	int size = 64;
    	
    	int[][] noise = MathUtil.generateFibonocciNumbers2D(Map.sx * Map.sx + 14, Map.sy * Map.sy + 13, Map.h.length/resolution+1, Map.h[0].length/resolution+1, (int)Math.log(size)+1);
    	for(int i = 0; i < noise.length; i++)
    	{
    		for(int j = 0; j < noise[0].length; j++)
        	{
    			noise[i][j]%= 100;
    			
    			if(noise[i][j] < forestTolerance)
    			{
    				noise[i][j] = 0;
    			}
        	}
    	}
		
    	int[][] connectedNoise = new int[noise.length][noise[0].length];
    	for(int i = 1; i < noise.length-1; i++)
    	{
    		for(int j = 1; j < noise[0].length-1; j++)
        	{
    			if(noise[i][j] == 0)
    			{
    				int surroundingNoiseCount = 0;
    				surroundingNoiseCount+= noise[i-1][j] > 0 ? 1:0;
    				surroundingNoiseCount+= noise[i+1][j] > 0 ? 1:0;
    				surroundingNoiseCount+= noise[i][j-1] > 0 ? 1:0;
    				surroundingNoiseCount+= noise[i][j+1] > 0 ? 1:0;
    				
    				surroundingNoiseCount+= noise[i-1][j-1] > 0 ? 1:0;
    				surroundingNoiseCount+= noise[i+1][j-1] > 0 ? 1:0;
    				surroundingNoiseCount+= noise[i-1][j+1] > 0 ? 1:0;
    				surroundingNoiseCount+= noise[i+1][j+1] > 0 ? 1:0;
    				
    				if(surroundingNoiseCount > 2)
    				{
    					//int surroundingNoise = (noise[i-1][j] + noise[i+1][j] + noise[i][j-1] + noise[i][j+1] + noise[i-1][j-1] + noise[i+1][j-1] + noise[i-1][j+1] + noise[i+1][j+1])/8;
    					
    					int MaxHeight = MathUtil.getMax(noise[i-1][j],noise[i+1][j],noise[i][j-1],noise[i][j+1],noise[i-1][j-1],noise[i+1][j-1],noise[i-1][j+1]);
    					connectedNoise[i][j] = MaxHeight;
    				}
    			}
    			else
    			{
    				connectedNoise[i][j] = noise[i][j];
    			}
        	}
    	}
    	
    	int forestRangeTolerance = 80;
    	int heightTolerance = 80;
    	
    	boolean[][] shadeMap = new boolean[Map.l.length][Map.l[0].length];
    	int[][] finalArray = MathUtil.scaleArray(connectedNoise, resolution);
    	for(int i = 0; i < finalArray.length; i++)
    	{
    		for(int j = 0; j < finalArray[0].length; j++)
        	{
    			if(Map.l[i][j])
    			{	
    				if(Map.h[i][j] < heightTolerance)
    				{
    					if(finalArray[i][j] > forestRangeTolerance)
	    				{
	    					shadeMap[i][j] = true;
	    				}
    				}
    			}
        	}
    	}
    	return shadeMap;
	}
	
	//altitude,climate
	public static void generateForestVegetation(IslandMap Map, boolean[][] ShadeMap)
	{
//		boolean[][] vegetationMap = new boolean[ShadeMap.length][ShadeMap[0].length];
		
		for(int i = 0 ; i < ShadeMap.length; i++)
		{
			for(int j = 0 ; j < ShadeMap[0].length; j++)
			{
				if(ShadeMap[i][j])
				{
					int distance = 0;
					int height = Map.h[i][j];
					int moisture = Map.ma[i][j];
					
					
					if(moisture % 32 >= 1 && moisture % 32 <= 5)
					{
						if(moisture /32 <= 4)
						{
							distance = 5 + height/12;
						}
					}
					
					
					if(distance != 0)
					{
						if(!MapUtil.HasSurrounding(i, j, Map.st, distance))
						{
//							vegetationMap[i][j] = true;
							//generate tree id
							String treeVal = 2 + "";
							
//							int heightLevel = (height-ServerSettings.seaLevel)/8 > 9 ? 9 : (height-ServerSettings.seaLevel)/8;
//							treeVal+= heightLevel;
							
							int val = Map.nm[i][j]%10;
							treeVal+= val;
							
							treeVal+= moisture/32;
							treeVal+= moisture%32-1;
							treeVal+= 0;
							treeVal+= 0;
							treeVal+= 0;
							treeVal+= 0;
							treeVal+= 0;
							
							Map.st[i][j] = Integer.parseInt(treeVal);
							Map.str[i][j] = (Map.nm[i][j]%3) * 90 - 90;
							
						}
					}
				}
			}
		}
	}
	
}
