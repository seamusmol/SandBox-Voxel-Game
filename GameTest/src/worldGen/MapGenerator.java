package worldGen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import Util.MapUtil;
import Util.MathUtil;
import VoxelGenUtil.ForestGenUtil;
import serverlevelgen.WorldSettings;

public class MapGenerator {
	
	int islandPointCount;
	
	int islandSectionPointCount = 0;
	int islandSectionMaxPointCount = 0;
	
	int islandSectionsize = 0;
	int islandSectionTolerance = 0;
	int islandSectionSizeTolerance= 0;
	int distTol = 0;
    int sectionDistTol = 0;
	
	int boundary = 0;
    int useRegion;
	
    int lakeCount = 10;
    int minlakeSize = 0;
    int maxLakeSize = 0;
    
    int minRiverWaves = 0;
    int maxRiverWaveDistance = 0;
    int maxRiverAmplitude = 0;
    
    int minRiverWidth = 2;
    int maxRiverWidth = 8;
    
    int landSlopeDistance = 32;
    int maxStepDistance = 32;
    
    int oceanStepDistance = 4;
    int maxStepOceanDistance = 96;
    
    int riverStepDistance = 2;
    int maxRiverStepDistance = maxRiverWidth*2;
    
    int lakeStepDistance = 3;
    int maxLakeStepDistance = 21;
    
    int lakeMountainDistance = 16;
    
    int biomeSize = 512;
   
    int oceanFloor;
    int maxSlopeHeight;
    int maxLakeDepth;
    
    int seaLevel = 64;
    
	int sx;
	int sy;
    int wx;
    int wy;

    Vector2f Center;

    WorldSettings worldSettings;
    public IslandMap IslandMap;

    public MapGenerator(WorldSettings WorldSettings,String MapName,int Scale, int SeedX, int SeedY, int WX, int WY)
    {
    	worldSettings = WorldSettings;
        wx = WX;
        wy = WY;

        sx = SeedX;
        sy = SeedY;

        lakeCount = wx/256;
        
        minlakeSize = 64;
        maxLakeSize = wx/32;
        
        minRiverWaves = 2;
        maxRiverWaveDistance = 128;
        maxRiverAmplitude = wx/128 + 1;
        
        islandSectionsize = 128;
        islandSectionTolerance = 48;
        islandSectionSizeTolerance = 24;
    	
 		islandSectionMaxPointCount = 128;
 		islandSectionPointCount = 128;
 		islandPointCount = wx/14;
 		
 		distTol = 64;
 		sectionDistTol = 8;
 		
        boundary = wx/8;
        useRegion = wx - boundary;
        Center = new Vector2f(wx/2,wy/2);
        
        oceanFloor = (int)(seaLevel - Math.round(maxStepOceanDistance/oceanStepDistance));
        maxSlopeHeight = (int)(seaLevel + Math.round(maxStepDistance/landSlopeDistance));
        
        maxLakeDepth = (int)(Math.round(maxRiverStepDistance/riverStepDistance));
        
        IslandMap = new IslandMap(MapName, wx, sx, sy, WorldSettings.resolution);

        generateMainLand();
        System.out.println("mainland done");
        generateLake();
        System.out.println("lakes done");
        generateHeightMap();
        System.out.println("Heightmap done");
        generateMountainsTest();
        System.out.println("mountains done");
        generateBiomes();
        System.out.println("Biomes done");
        generateLandBiomes();
        System.out.println("Land Biomes done");
        
        if(Scale > 1)
        {
        	IslandMap newMap = new IslandMap(IslandMap, Scale, WorldSettings.resolution);
        	IslandMap = newMap;
        }
        System.out.println("Map Resized");
        
        fixBorder();
        StructureGenerator.GenerateSettlements(IslandMap, WorldSettings);
        
        System.out.println("Settlements Done");
        
        ForestGenUtil.GenerateVegetation(IslandMap);
        
        System.out.println("Vegetation done");
        
        IslandMap.mapName = MapName;
        
        System.gc();
    }
    
    private class MainLandThread extends Thread
   	{
    	int index;
    	int di;
       	boolean[][] map = new boolean[(wx + 1)][ (wy + 1)];
       	List<Vector2f> p = new ArrayList<Vector2f>();
       	boolean isDone = false;
   		private MainLandThread(int Index, int DI, List<Vector2f> P)
   		{
   			index = Index;
   			di = DI;
   			p = P;
   		}

   		@Override
   		public void run() 
   		{
   			Thread.currentThread().setName("Util-MainLandThread");
   			for(int i = index; i < index+di; i+=2)
   			{
   				int wavecount = (int)p.get(i).distance(p.get(+1))/200 + 1;
   				int amplitude = (int)p.get(i).distance(p.get(+1))/200 + 1;
   				
   				MapUtil.generateSineWave(map, p.get(i), p.get(i+1), wavecount, 2, amplitude);
   			}
   			isDone = true;
   		}
   	}
    
    public void generateMainLand()
    {
        int attempts = 0;
        
        List<Vector2f> pointList = new ArrayList<Vector2f>();
        
        int dx = (sx*sx)%useRegion;
        int dy = (sy*sy)%useRegion;
        
        float offset = 0.05f;
        
    	while(pointList.size() < islandPointCount && attempts < 200)
        {
    		Vector2f newPosition = new Vector2f(boundary + dx, boundary + dy);
    		newPosition = newPosition.add( Center.subtract(newPosition).mult(offset));
    		
    		boolean withinRange = MapUtil.withinRange(pointList, newPosition, distTol);
    		if(withinRange)
    		{
    			attempts++;
    		}
    		else
    		{
    			pointList.add(newPosition);
    			attempts = 0;
    		}
    		
    		int tempx = dx;
    		dx += dy;
    		dx%= useRegion;
    		dy = tempx;
    		dy%= useRegion;
        }
        
    	List<Vector2f> p = new ArrayList<Vector2f>();
        for(int i = 0; i < pointList.size(); i++)
        {
        	List<Vector2f> curSectionPoints = new ArrayList<Vector2f>();
        	
            int idx = ((sx+35 + (i*i))%islandSectionsize)-(islandSectionsize/2);
            int idy = ((sy+76 + i)%islandSectionsize)-(islandSectionsize/2);
            
            int curAttempts = 0;
        	while(curSectionPoints.size() < islandSectionPointCount  && curAttempts < 200)
            {
        		Vector2f newPosition = new Vector2f(pointList.get(i).x + idx, pointList.get(i).y + idy);
        		
        		boolean withinRange = MapUtil.withinRange(curSectionPoints, newPosition, sectionDistTol);
        		if(withinRange)
        		{
        			curAttempts++;
        		}
        		else
        		{
        			curSectionPoints.add(newPosition);
        			curAttempts = 0;
        		}
        		
        		int tempx = idx;
        		
        		idx += idy;
        		idy = tempx;
        		
        		idx = (idx%islandSectionsize)-(islandSectionsize/2);
        		idy = (idy%islandSectionsize)-(islandSectionsize/2);
        		
            }
        	p.addAll(curSectionPoints);
        }
        
        p = MapUtil.sortByNeighbor(p, islandSectionTolerance, islandSectionSizeTolerance);
        
        float dist = p.size() < wx * 7.5f ? 5.0f:0;
        distortPositions(p, 5f + dist);
        
        boolean[][] map = new boolean[(wx + 1)][ (wy + 1)];
        
        int threadCount = Runtime.getRuntime().availableProcessors()-1;
        threadCount = 1;
        ExecutorService executor = Executors.newCachedThreadPool();
        MainLandThread[] threads = new MainLandThread[threadCount];
		
        int size = p.size()/threadCount;
        
        for (int i = 0; i < threadCount; i++)
        {
        	int minX = i*size;
        	int endDist = (p.size()) - (i*size);
        	int maxSize = i == threadCount-1 ? endDist: size;
        	threads[i] = new MainLandThread(minX, maxSize,p);
        	threads[i].start();
        }
        
        boolean isProcessing = true;
        while(isProcessing)
        {
    		boolean isDone = true;
    		for(int i = 0; i < threads.length; i++)
    		{
    			if(!threads[i].isDone)
    			{
    				isDone = false;
    			}
    		}
    		if(isDone)
    		{
    			isProcessing = false;
    		}
    	}
        executor.shutdown();
        //combine new maps
        for(int i = 0; i < threads.length; i++)
        {	
        	for(int x = 0; x < map.length; x++)
        	{
        		for(int y = 0; y < map[0].length; y++)
        		{
        			if(threads[i].map[x][y])
        			{
        				map[x][y] = true;
        			}
        		}
        	}
        }
        
        boolean[][] shadeMap = MapUtil.basicFill(map);
        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[0].length; j++)
            {
                if (shadeMap[i][j])
                {
                    IslandMap.o[i][j] = shadeMap[i][j];
                }
            }
        }
        
        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[0].length; j++)
            {
                IslandMap.l[i][j] = !shadeMap[i][j];
            }
        }
    }
    
    public void generateLake()
    {
        int px = sx;
        int py = sy;

        int CurrentLakeCount = 0;

        int attempts = 0;
        
        while (CurrentLakeCount < lakeCount && attempts < 200)
        {
            Vector2f Position = new Vector2f(boundary + (px % (useRegion)), boundary + (py % (useRegion)));
            double Oceandistance = MapUtil.GetDist((int)Position.x, (int)Position.y, IslandMap.o, wx / 2);
            int lakeSize = minlakeSize + ((px+py)%maxLakeSize);
            
            if ((Oceandistance > lakeSize || Oceandistance == -1) )
            {
                int[] LakePoints = MathUtil.generateFibonocciNumbers(sx + px + 12, sy + py + 13, 20, 2);

                List<Vector2f> P = new ArrayList<Vector2f>();
                for (int j = 0; j < LakePoints.length / 2; j++)
                {
                	Vector2f newPoint = new Vector2f(Position.x + ((LakePoints[j] % lakeSize) - lakeSize / 2), Position.y + ((LakePoints[j + 1] % lakeSize) - lakeSize / 2));	
                    P.add(newPoint);
                }
                P.add(Position);
                P.add(P.get(0));
                
                boolean[][] Map = new boolean[(wx + 1)][ (wy + 1)];
                for (int j = 0; j < P.size() - 1; j++)
                {
                    MapUtil.generateSineWave(Map, P.get(j), P.get(j+1), 5, 2, 2);
                }
                boolean[][] shadeMap = MapUtil.basicFill(Map);
                for (int countX = 0; countX < Map.length; countX++)
                {
                    for (int countY = 0; countY < Map[0].length; countY++)
                    {
                        if (!IslandMap.lk[countX][ countY])
                        {
                            IslandMap.lk[countX][ countY] = !shadeMap[countX][ countY];
                        }
                    }
                }

                boolean[][] RiverMap = new boolean[(wx + 1)][ (wy + 1)];
                Vector2f OceanPoint = MapUtil.getClosest((int)Position.x, (int)Position.y, IslandMap.o, wx / 4);
                Vector2f RiverPoint = MapUtil.getClosest((int)Position.x, (int)Position.y, IslandMap.r, wx / 4);

                Vector2f ClosestPoint = MathUtil.distance(Position, OceanPoint) < MathUtil.distance(Position, RiverPoint) ? OceanPoint : RiverPoint;
                int distance = MathUtil.distance(Position, ClosestPoint).intValue();
                
                MapUtil.generateSineWave(RiverMap, Position, ClosestPoint, distance/maxRiverWaveDistance + minRiverWaves, (IslandMap.nm[(int)Position.x][(int)Position.y])%maxRiverWidth + minRiverWidth, (IslandMap.nm[(int)Position.x][(int)Position.y])%maxRiverAmplitude + 2);
                
                for (int countX = 0; countX < Map.length; countX++)
                {
                    for (int countY = 0; countY < Map[0].length; countY++)
                    {
                        if (!IslandMap.r[countX][ countY] && RiverMap[countX][ countY])
                        {
                            IslandMap.r[countX][ countY] = RiverMap[countX][ countY];
                        }
                    }
                }
                CurrentLakeCount++;
                attempts = 0;
            }
            else
            {
            	attempts++;
            }
            int tx = px;
            px += py + 1;
            px %= wx;
            py = (tx) % wx;
        }

        for (int i = 0; i < IslandMap.r.length; i++)
        {
            for (int j = 0; j < IslandMap.r[0].length; j++)
            {
                if (IslandMap.r[i][j] || IslandMap.lk[i][j])
                {
                    IslandMap.l[i][j] = false;
                }
            }
        }
    }
    
    public void generateMountainsTest()
    {
    	int resolution = 32;
    	int mountainTolerance = 110;
    	int size = 64;
    	
    	int[][] noise = MathUtil.generateFibonocciNumbers2D(sx, sy, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	for(int i = 0; i < noise.length; i++)
    	{
    		for(int j = 0; j < noise[0].length; j++)
        	{
    			noise[i][j]%= 56;
    			noise[i][j]+=worldSettings.seaLevel;
    			
    			int lkmtd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.lk, lakeMountainDistance * 2);
    			int rmtd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.r, lakeMountainDistance * 2);
    			int otmd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.o, lakeMountainDistance * 2);
    			
    			if(noise[i][j] < mountainTolerance)
    			{
    				noise[i][j] = worldSettings.seaLevel;
    			}
    			else if(lkmtd <= lakeMountainDistance && lkmtd != -1)
    			{
    				noise[i][j] = worldSettings.seaLevel;
    			}
    			else if(rmtd <= lakeMountainDistance&& rmtd != -1)
    			{
    				noise[i][j] = worldSettings.seaLevel;
    			}
    			else if(otmd <= lakeMountainDistance&& otmd != -1)
    			{
    				noise[i][j] = worldSettings.seaLevel;
    			}
    			
        	}
    	}
    	int[][] newNoise = MathUtil.generateFibonocciNumbers2D(sx, sy, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	for(int i = 0; i < noise.length; i++)
    	{
    		for(int j = 0; j < noise[0].length; j++)
        	{
    			if(noise[i][j] > worldSettings.seaLevel)
    			{
    				noise[i][j] = newNoise[i][j]%50 + worldSettings.seaLevel;
    			}
        	}
    	}
    	
    	int[][] connectedNoise = new int[noise.length][noise[0].length];
    	for(int i = 1; i < noise.length-1; i++)
    	{
    		for(int j = 1; j < noise[0].length-1; j++)
        	{
    			if(noise[i][j] == worldSettings.seaLevel)
    			{
    				int surroundingNoiseCount = 0;
    				surroundingNoiseCount+= noise[i-1][j] > worldSettings.seaLevel ? 1:0;
    				surroundingNoiseCount+= noise[i+1][j] > worldSettings.seaLevel ? 1:0;
    				surroundingNoiseCount+= noise[i][j-1] > worldSettings.seaLevel ? 1:0;
    				surroundingNoiseCount+= noise[i][j+1] > worldSettings.seaLevel ? 1:0;
    				
    				surroundingNoiseCount+= noise[i-1][j-1] > worldSettings.seaLevel ? 1:0;
    				surroundingNoiseCount+= noise[i+1][j-1] > worldSettings.seaLevel ? 1:0;
    				surroundingNoiseCount+= noise[i-1][j+1] > worldSettings.seaLevel ? 1:0;
    				surroundingNoiseCount+= noise[i+1][j+1] > worldSettings.seaLevel ? 1:0;
    				
    				if(surroundingNoiseCount > 2)
    				{
    					int MaxHeight = MathUtil.getMax(noise[i-1][j],noise[i+1][j],noise[i][j-1],noise[i][j+1],noise[i-1][j-1],noise[i+1][j-1],noise[i-1][j+1]);
    					connectedNoise[i][j] = MaxHeight;
    				}
    				else
    				{
    					connectedNoise[i][j] = worldSettings.seaLevel;
    				}
    			}
    			else
    			{
    				connectedNoise[i][j] = noise[i][j];
    			}
        	}
    	}
    	int[][] secondaryNoise = MathUtil.generateFibonocciNumbers2D(sx, sy, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	int noiseHeight = 4;
    	for(int i = 0; i < connectedNoise.length; i++)
    	{
    		for(int j = 0; j < connectedNoise[0].length; j++)
        	{
    			if(connectedNoise[i][j] > worldSettings.seaLevel)
    			{
    				connectedNoise[i][j] = connectedNoise[i][j] + secondaryNoise[i][j]%noiseHeight;
    			}
        	}
    	}
    	int[][] finalArray = MathUtil.scaleArray(connectedNoise, resolution);
    	
    	int terrainNoiseResolution = 32;
    	int terrainNoiseHeight = 14;
    	int terrainNoiseDepth = 4;
    	int noiseDistance = 32;
    	
    	int[][] terrainNoise = MathUtil.generateFibonocciNumbers2D(sx, sy, wx/terrainNoiseResolution+1, wx/terrainNoiseResolution+1, (int)Math.log(size)+1);
    	for(int i = 0; i < terrainNoise.length; i++)
    	{
    		for(int j = 0; j < terrainNoise.length; j++)
        	{
    			int lkd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.lk, lakeMountainDistance * 2);
    			int rd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.r, lakeMountainDistance * 2);
    			
    			if(lkd <= noiseDistance && lkd != -1)
    			{
    				terrainNoise[i][j] = 0;
    			}
    			else if(rd <= noiseDistance && rd != -1)
    			{
    				terrainNoise[i][j] = 0;
    			}
    			else
    			{
    				terrainNoise[i][j] = terrainNoise[i][j]%terrainNoiseHeight - terrainNoiseDepth;
    			}
    			
        	}
    	}
    	int[][] scaledNoise = MathUtil.scaleArray(terrainNoise, terrainNoiseResolution);
    	for(int i = 1; i < scaledNoise.length-1; i++)
    	{
    		for(int j = 1; j < scaledNoise.length-1; j++)
        	{
    			if(IslandMap.o[i-1][j] || IslandMap.o[i+1][j]|| IslandMap.o[i][j-1]|| IslandMap.o[i][j+1])
    			{
    				finalArray[i][j] = worldSettings.seaLevel;
    			}
    			else
    			{
    				finalArray[i][j]+= scaledNoise[i][j];
    			}
        	}
    	}

    	int peakDepth = 10;
    	int[][] removeMountainNoise = MathUtil.generateFibonocciNumbers2D(sx+8, sy+7, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	for(int i = 0; i < removeMountainNoise.length; i++)
    	{
    		for(int j = 0; j < removeMountainNoise[0].length; j++)
        	{
    			if(connectedNoise[i][j] > worldSettings.seaLevel)
    			{
    				int lkd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.lk, lakeMountainDistance * 2);
        			int rd = (int)MapUtil.getBudgetDistance(i * resolution, j * resolution, IslandMap.r, lakeMountainDistance * 2);
        			
        			if(lkd <= noiseDistance && lkd != -1)
        			{
        				removeMountainNoise[i][j] = 0;
        			}
        			else if(rd <= noiseDistance && rd != -1)
        			{
        				removeMountainNoise[i][j] = 0;
        			}
        			else
        			{
        				removeMountainNoise[i][j]%= (peakDepth + 1);
        				removeMountainNoise[i][j]+=4;
        			}
    			}
    			else
    			{
    				removeMountainNoise[i][j] = 0;
    			}

        	}
    	}
    	int[][] PeakArray = MathUtil.scaleArray(removeMountainNoise, resolution);
    	
    	for(int i = 0; i < finalArray.length; i++)
    	{
    		for(int j = 0; j < finalArray[0].length; j++)
        	{
    			if(IslandMap.l[i][j])
    			{
    				IslandMap.h[i][j] = finalArray[i][j];
    				IslandMap.h[i][j] -= PeakArray[i][j];
    				
    				if(IslandMap.h[i][j] > worldSettings.seaLevel + worldSettings.mountainTolerance)
    				{
    					IslandMap.m[i][j] = true;
    				}
    				
    				IslandMap.h[i][j] = IslandMap.h[i][j] < 0 ? 0: IslandMap.h[i][j];
            		IslandMap.h[i][j] = IslandMap.h[i][j] > worldSettings.worldHeight ? worldSettings.worldHeight: IslandMap.h[i][j];
    			}
        	}
    	}
    }
    
    private class HeightMapThread extends Thread
   	{
    	int ix;
    	int size;
    	int radius;
    	boolean isDone = false;
    	
   		private HeightMapThread(int IX, int Size, int Radius)
   		{
   			ix = IX;
   			size = Size;
   			radius = Radius;
   		}

   		@Override
   		public void run() 
   		{
   			for (int i = ix; i < ix+size; i++)
   	        {
   	            for (int j = radius; j < IslandMap.m[0].length-radius; j++)
   	            {
   	            	IslandMap.h[i][j] = 64;
   	            }
   	        }
   			
   			for (int i = ix; i < ix+size; i++)
   	        {
   	            for (int j = radius; j < IslandMap.m[0].length-radius; j++)
   	            {
   	            	if(IslandMap.l[i][j])
   			       	{
   	            		if(IslandMap.o[i-1][j] || IslandMap.o[i+1][j] || IslandMap.o[i][j-1] || IslandMap.o[i][j-1])
   	            		{	
   	            			for (int countX = -radius; countX <= radius; countX++)
   	            	        {
   	            	            for (int countY = -radius; countY <= radius; countY++)
   	            	            {
   	            	                if (IslandMap.o[i + countX][ j + countY])
   	            	                {
   	            	                    int dist = (int)Math.sqrt(countX * countX + countY * countY);
   	            	                    int tempHeight = (int) (seaLevel - dist/oceanStepDistance);
   	            	                    if (IslandMap.h[i + countX][ j + countY] < tempHeight)
   	            	                    {
   	            	                    	IslandMap.h[i + countX][ j + countY] = tempHeight;
   	            	                    }
   	            	                }
   	            	            }
   	            	        }
   	            			
   	            		}
   	            		else
   	            		{
   	   			       		IslandMap.h[i][j] = 64;	
   	            		}
   			       	}
   	            	else
   	            	{
   	            		IslandMap.h[i][j] = 60;	
   	            	}
   	            }
   	        }
   			isDone = true;
   		}
   	}
    
    public void generateHeightMap()
    {
    	int threadCount = 3;
        HeightMapThread[] threads = new HeightMapThread[threadCount];
		
        int size =  Math.round(IslandMap.m.length/threadCount);
        int radius = 32;
        
        for (int i = 0; i < threadCount; i++)
        {
        	int minX = i*size > radius ? i * size:radius;
        	int endDist = (IslandMap.h.length-radius) - (i*size);
        	int maxSize = endDist < size ? endDist: size;
        	
        	threads[i] = new HeightMapThread(minX, maxSize, radius);
        	threads[i].start();
        }
        
        long startTime = System.currentTimeMillis();
        
        boolean isProcessing = true;
        while(isProcessing)
        {
    		for(int i = 0; i < threads.length; i++)
    		{
    			if(!threads[i].isDone)
    			{
					try
					{
						threads[i].join(1);
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
    			}
    		}
    		boolean isDone = true;
    		for(int i = 0; i < threads.length; i++)
    		{
    			if(!threads[i].isDone)
    			{
    				isDone = false;
    			}
    		}
    		if(isDone)
    		{
    			isProcessing = false;
    		}
    	}

        int lakeSize = minlakeSize + maxLakeSize;
        
        //depth pass
        for (int i = boundary; i < IslandMap.m.length-boundary; i++)
        {
            for (int j = boundary; j < IslandMap.m[0].length-boundary; j++)
            {
            	if(IslandMap.lk[i][j])
            	{
            		int coastDistance = (int)MapUtil.getBudgetDistance(i, j, IslandMap.l, lakeSize*2);
        			int lowHeight = (int)MapUtil.GetLowestSurroundingValue(i, j, IslandMap.l, IslandMap.h, lakeSize*2);
            		
            		IslandMap.wh[i][j] = lowHeight;
            		IslandMap.h[i][j] = coastDistance > maxLakeStepDistance ? lowHeight - coastDistance/maxLakeStepDistance:lowHeight - coastDistance/lakeStepDistance;
            	}
            	else if(IslandMap.r[i][j])
		    	{
            		int coastDistance = (int)MapUtil.getBudgetDistance(i, j, IslandMap.l, maxRiverStepDistance);
        			int lowHeight = (int)MapUtil.GetLowestSurroundingValue(i, j, IslandMap.l, IslandMap.h, maxRiverStepDistance);
		    		
		    		IslandMap.wh[i][j] = lowHeight;
		    		IslandMap.h[i][j] = lowHeight - coastDistance/riverStepDistance;
		    	}
            }
        }
        //lake pass
        for (int i = boundary; i < IslandMap.m.length-boundary; i++)
        {
            for (int j = boundary; j < IslandMap.m[0].length-boundary; j++)
            {
            	//find lowest lake point
            	if(IslandMap.lk[i][j])
            	{
            		int lowestWaterHeight = (int)MapUtil.GetLowestSurroundingValue(i, j, IslandMap.lk, IslandMap.wh, lakeSize*2);
            		int dif = IslandMap.wh[i][j] - lowestWaterHeight;
            		if(dif > 0)
            		{
            			IslandMap.wh[i][j] -= dif;	
            			IslandMap.h[i][j] -=dif;
            		}
            	}
            	else if(IslandMap.r[i][j])
            	{
            		int lowestWaterHeight = (int)MapUtil.GetLowestSurroundingValue(i, j, IslandMap.lk, IslandMap.wh, lakeSize*2);
            		int dif = IslandMap.wh[i][j] - lowestWaterHeight;
            		if(dif > 0)
            		{
            			IslandMap.wh[i][j] -= dif;	
            			IslandMap.h[i][j] -=dif;
            		}
            	}
            }
        }
        
        for (int i = 0; i < IslandMap.h.length; i++)
        {
        	for (int j = 0; j < IslandMap.h[0].length; j++)
            {
        		if(IslandMap.o[i][j])
        		{
        			IslandMap.wh[i][j] = worldSettings.seaLevel;
        		}
        		
        		IslandMap.h[i][j] = IslandMap.h[i][j] < 0 ? 0: IslandMap.h[i][j];
        		IslandMap.h[i][j] = IslandMap.h[i][j] > worldSettings.worldHeight ? worldSettings.worldHeight: IslandMap.h[i][j];
        		
        		IslandMap.wh[i][j] = IslandMap.wh[i][j] < 0 ? 0: IslandMap.wh[i][j];
        		IslandMap.wh[i][j] = IslandMap.wh[i][j] > worldSettings.worldHeight ? worldSettings.worldHeight: IslandMap.wh[i][j];
        		
            }
        }
    }
   
    //apply base Polar Moisture
    //draw fill method over Polar Moisture
    public void generateLandBiomes()
    {
    	List<Vector2f> positions = new ArrayList<Vector2f>();
    	int distance = 175;
    	int fillDistance = 200;
    	
        boolean[][] shadeMap = new boolean[IslandMap.l.length][IslandMap.l[0].length];
        boolean[][] landMap = IslandMap.l;
        
        int[][] originalMA = IslandMap.ma.clone();
        
        int attempts = 0;
        int px = sx;
        int py = sy;
        while (attempts < 200)
        {
        	if(px > 0 && py > 0 && px < landMap.length && py < landMap[0].length)
        	{
	        	if(landMap[px][ py] && !shadeMap[px][ py])
	        	{
	        		
	        		attempts = 0;
    				int moisture = originalMA[px][py];
    				
    				if(moisture < 5)
	    			{
						moisture += ((px+py)%4) * 32;
	    			}
	    			else if(moisture <= 97)
	    			{
//	    				moisture -= ((px+py+py)%2);
	    			}
	    			else
	    			{
	    				moisture = 133;
	    			}
	        		
	        		Stack<Integer> bagX = new Stack<Integer>();
	        		Stack<Integer> bagY = new Stack<Integer>();
	        		
	        		bagX.add(px);
	        		bagY.add(py);
	        		
	        		int ox = px;
	        		int oy = py;
	        		
	                while (bagX.size() > 0)
	                {
	                	int cx = bagX.pop();
	                	int cy = bagY.pop();
	                	
	                	if(cx > 0 && cy > 0 && cx < landMap.length && cy < landMap[0].length)
	                	{
	                		if( IslandMap.h[cx][cy] >= worldSettings.seaLevel + 24 && IslandMap.h[cx][cy] <= 128)
	                		{
	                			IslandMap.ma[cx][cy] = moisture%32 + 128;
	                		}
	                		else
	                		{
	                			IslandMap.ma[cx][cy] = moisture;
	                		}
	                		
	                		shadeMap[cx][cy] = true;
	                	}
	                	
	                    if(MathUtil.distance(ox,cx,oy,cy) <= fillDistance)
	                    {
	            			if (landMap[cx - 1][ cy] && !shadeMap[cx - 1][ cy])
	                        {
            					bagX.push(cx - 1);
            					bagY.push(cy);
	                        }
	                        if (landMap[cx + 1][ cy] && !shadeMap[cx + 1][ cy])
	                        {
	                        	
	                        	bagX.push(cx + 1);
	            				bagY.push(cy);
	                        }
	                        
	                        if (landMap[cx][ cy - 1] && !shadeMap[cx][ cy - 1])
	                        {
	                        	
	                        	bagX.add(cx);
	            				bagY.add(cy - 1);
	            				
	                        }
	                        if (landMap[cx][ cy + 1] && !shadeMap[cx][ cy + 1])
	                        {
	                        	bagX.add(cx);
	            				bagY.add(cy + 1);
	            				
	                        }
	                    }	
	                }
	        	}
	        	else
	        	{
	        		attempts++;
	        	}
        	}
        	else
        	{
        		attempts++;
        	
        	}
            int tx = px;
            px += py + 1;
            px %= wx;
            py = (tx) % wx;
            
        }
    }
    
    public void generateBiomes()
    {
    	//take land map
    	//generate 
    	int zoneDistance1 = (int) (IslandMap.ma[0].length/2 * 0.5f); 
    	int zoneDistance2 = (int) (IslandMap.ma[0].length/2 * 0.80f); 
    	
    	int biomeZoneDiv1 = zoneDistance1/5;
    	int biomeZoneDiv2 = (zoneDistance2 - zoneDistance1)/3;
    	
    	int waveHeight = 25 + IslandMap.sx%25;
    	float distance = 1.0f/ (50.0f + IslandMap.sy%100);
    	
    	for(int i = 0; i < IslandMap.ma.length; i++)
    	{
    		for(int j = 0; j < IslandMap.ma[0].length; j++)
        	{
    			IslandMap.ma[i][j] = 6;
        	}
    	}
    	
    	for(int i = 0; i < IslandMap.ma.length; i++)
    	{
    		int oy = (int) ((waveHeight * Math.sin(i * distance)) - (waveHeight/2*Math.cos(i*distance) ));
    		
			for(int j = 0; j < IslandMap.ma[0].length; j++)
	    	{
    			if(IslandMap.l[i][j])
    			{
    				int dist = Math.abs(IslandMap.ma.length/2 - j) + oy; 
    				dist += Math.sin(dist * waveHeight);
    				
	    			if(dist < zoneDistance1)
	    			{
	    				//vertical
	    				int biome = dist/biomeZoneDiv1;
	    				biome++;
	    				
	    				biome = biome <= 1 ? 1:biome;
	    				biome = biome >= 5 ? 5:biome;
	    				
	    				IslandMap.ma[i][j] = biome;
	    				
	    				//+ height
	    			}
	    			else if(dist <= zoneDistance2)
	    			{
	    				//max+horizontal
	    				int biome = 3-(zoneDistance2-dist)/biomeZoneDiv2;
	    				biome*=32;
	    				biome+=5;
	    				
	    				biome = biome <= 5 ? 5: biome;
	    				biome = biome >= 133 ? 133: biome;
	    				
	    				IslandMap.ma[i][j] = biome;
	    			}
	    			else
	    			{
	    				//pole
	    				IslandMap.ma[i][j] = 133;
	    			}
    			}
    			else
    			{
    				IslandMap.ma[i][j] = 6;
    			}
        	}
    	}
    	
    }
    
    public void generateMoistureMap()
    {
    	int biomeSize = wy/512 + 1;
    	
    	int[] biomeVals = MathUtil.generateFibonocciNumbers(sx, sy, biomeSize, 2);
    	
    	int[] poleBiomeOrder = new int[biomeVals.length];
    	for(int i = 0; i < biomeVals.length; i++)
    	{
    		poleBiomeOrder[i] = biomeVals[i]%5 + 1;
    	}
    	
        int biomeHeight = IslandMap.ma.length/poleBiomeOrder.length;
        
        int biomeSizeX = 64;
        int biomePointCount = IslandMap.ma.length/biomeSizeX;
        int biomeAmplitude = biomeHeight/2;	
        
        int[][] biomeMap = new int[IslandMap.ma.length][IslandMap.ma[0].length];
        
    	for(int j = 0; j < poleBiomeOrder.length-1; j++)
    	{
    		boolean[][] curBiomeMap = new boolean[IslandMap.ma.length][IslandMap.ma[0].length];
    		List<Vector2f> P = new ArrayList<Vector2f>();
    		int[] points = MathUtil.generateFibonocciNumbers(sx+j, sy+j*11, biomePointCount, (int)Math.log10(biomeAmplitude)+1);
    		
    		for(int i = 0; i < biomePointCount; i++)
    		{
    			int py = j*biomeHeight + biomeHeight + ((points[i]%biomeAmplitude) - biomeAmplitude/2 + 2);
    			P.add(new Vector2f(biomeSizeX * i, py));
    		}
    		
    		for(int i = 0; i < P.size()-1; i++)
    		{
    			MapUtil.generateSineWave(curBiomeMap, P.get(i), P.get(i+1), points[i] % 10 + 2, 4, points[i] % 10 + 1);
    			//MapUtil.createCurve(curBiomeMap, P.get(i), P.get(i+1), 4);
    		}
    		
    		for(int countX = 0; countX < curBiomeMap.length; countX++)
    		{
    			for(int countY = 0; countY < curBiomeMap[0].length; countY++)
    			{
    				if(curBiomeMap[countX][countY])
    				{
    					break;
    				}
    				if(biomeMap[countX][countY] == 0)
    				{
    					biomeMap[countX][countY] = poleBiomeOrder[j];
    				}
    			}
    		}
        }
        
		for(int i = worldSettings.chunkSize; i < IslandMap.ma.length-worldSettings.chunkSize; i++)
	    {
			for(int j = worldSettings.chunkSize; j < IslandMap.ma[0].length-worldSettings.chunkSize; j++)
	 		{
	 			if(biomeMap[i][j] == 0)
	    		{
	 				biomeMap[i][j] = poleBiomeOrder[poleBiomeOrder.length-1];
	     		}
	 		}
	    }
    	
        for(int i = worldSettings.chunkSize; i < IslandMap.ma.length-worldSettings.chunkSize; i++)
        {
        	for(int j = worldSettings.chunkSize; j < IslandMap.ma[0].length-worldSettings.chunkSize; j++)
            {
        		//IslandMap.ma[i][j] = biomeMap[i][j];
        		if(biomeMap[i][j] != 0)
        		{
        			if(IslandMap.ma[i][j] == 0)
        			{
        				IslandMap.ma[i][j] = biomeMap[i][j];
        			}
        		}
            }
        }
        
        int[] lakeFloor = {7,7,7,7,7};
        int[] riverFloor = {7,7,7,7,7};
        int[] riverSides = {7,7,7,7,7};
        int[] lakeSides = {7,7,7,7,7};
        
        for (int i = worldSettings.chunkSize; i < IslandMap.ma.length-worldSettings.chunkSize; i++)
        {
            for (int j = worldSettings.chunkSize; j < IslandMap.ma[0].length-worldSettings.chunkSize; j++)
            {
            	if (MapUtil.HasSurrounding(i, j, IslandMap.o, 1))
                {
        			IslandMap.ma[i][j] = 6;
                }
                else if(MapUtil.HasSurrounding(i, j, IslandMap.lk, 2))
                {
                	if(IslandMap.l[i][j])
                	{
                		if(IslandMap.h[i][j] <= worldSettings.seaLevel+2)
                		{
                			IslandMap.ma[i][j] = lakeSides[IslandMap.ma[i][j]-1];
                		}
                	}
                	else
                	{
                		IslandMap.ma[i][j] = lakeFloor[IslandMap.ma[i][j]-1];
                	}
                }
                else if (MapUtil.HasSurrounding(i, j, IslandMap.r, 2))
                {
                	if(IslandMap.l[i][j])
                	{
                		if(IslandMap.h[i][j] <= worldSettings.seaLevel+2)
                		{
                			IslandMap.ma[i][j] = riverSides[IslandMap.ma[i][j]-1];
                		}
                	}
                	else
                	{
                		IslandMap.ma[i][j] = riverFloor[IslandMap.ma[i][j]-1];
                	}
                }
            }
        }
        
        for (int i = 0; i < IslandMap.ma.length; i++)
        {
            for (int j = 0; j < IslandMap.ma[0].length; j++)
            {
                IslandMap.ma[i][j] = IslandMap.ma[i][j] < 1 ? 1 : IslandMap.ma[i][j];
            }
        }
    }
    
    public void distortPositions(List<Vector2f> List, float Amount)
    {
    	for(int i = 0; i < List.size(); i++)
    	{
    		float px = List.get(i).x;
    		float py = List.get(i).y;
    	
    		float dx = (float)(-Amount*Math.cos(px%Amount * px%Amount) *  + Amount*Math.sin(py%Amount * py%Amount));
    		float dy = (float)(Amount*-Math.sin(px%Amount * px%Amount) + Amount*Math.cos(py%Amount * py%Amount));
    		List.set(i, new Vector2f(px + dx,py + dy));
    	}
    }
    
    public void fixBorder()
    {
    	for(int i = 0; i < IslandMap.h.length;i++)
    	{
    		for(int j = 0; j < boundary;j++)
        	{
    			//height
    			IslandMap.h[i][j] = IslandMap.h[i][j] == 0 ? worldSettings.seaLevel-8 : IslandMap.h[i][j];
    			IslandMap.h[i][IslandMap.h.length-1-j] = IslandMap.h[i][IslandMap.h.length-1-j]==0? worldSettings.seaLevel-8:IslandMap.h[i][IslandMap.h.length-1-j];
    			
    			IslandMap.h[j][i] = IslandMap.h[j][i] == 0 ? worldSettings.seaLevel-8 : IslandMap.h[j][i];
    			IslandMap.h[IslandMap.h.length-1-j][i] = IslandMap.h[IslandMap.h.length-1-j][i] == 0 ? worldSettings.seaLevel-8 : IslandMap.h[IslandMap.h.length-1-j][i];
    			//moisture
    			IslandMap.ma[i][j] = IslandMap.ma[i][j] == 0 ? 6:IslandMap.ma[i][j];
    			IslandMap.ma[i][IslandMap.h.length-1-j] = IslandMap.ma[i][IslandMap.h.length-1-j] == 0 ? 6 : IslandMap.ma[i][IslandMap.h.length-1-j];
    			
    			IslandMap.ma[j][i] = IslandMap.ma[j][i] == 0 ? 6:IslandMap.ma[j][i];
    			IslandMap.ma[IslandMap.h.length-1-j][i] = IslandMap.ma[IslandMap.h.length-1-j][i] == 0 ? 6:IslandMap.ma[IslandMap.h.length-1-j][i];
    			//water height
    			IslandMap.wh[i][j] = worldSettings.seaLevel;
    			IslandMap.wh[i][IslandMap.h.length-1-j] = worldSettings.seaLevel;
    			
    			IslandMap.wh[j][i] = worldSettings.seaLevel;
    			IslandMap.wh[IslandMap.h.length-1-j][i] = worldSettings.seaLevel;
        	}
    	}
    	
    	
    }
    
}
