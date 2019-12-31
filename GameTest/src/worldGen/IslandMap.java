package worldGen;

import Configs.ServerSettings;
import Util.MathUtil;

public class IslandMap
{
    public int sx = 0;
    public int sy = 0;

    public boolean[][] l;
    public boolean[][] o;
    public boolean[][] lk;
    public boolean[][] r;
    public boolean[][] m;

    public int[][] ma;
    public int[][] h;
    public int[][] wh;
    //structure 
    public int[][] st;
    //structure rotation
    public int[][] str;

    public int[][] nm;
    
    //background
    public int[][] bh;
    public int[][] bm;
    
    public String mapName;
    
    //0-randomgen, 1-import(no caves)
    public boolean hasGeneration = true;
    public String generatorName = "";
    
    //TODO
    //on Import, generate new NM Array using SX,SY
    public IslandMap(String MapName, int X, int SX, int SY, int Resolution)
    {
    	mapName = MapName;
    	System.out.println("new map: " + mapName);
        sx = SX;
        sy = SY;

        l = new boolean[X + 1][X + 1];
        o = new boolean[X + 1][ X + 1];
        lk = new boolean[X + 1][ X + 1];
        r = new boolean[X + 1][ X + 1];
        m = new boolean[X + 1][ X + 1];
        
        ma = new int[X + 1][ X + 1];
        h = new int[X + 1][ X + 1];
        wh = new int[X + 1][ X + 1];
        nm = new int[X + 1][ X + 1];
        st = new int[X + 1][ X + 1];
        str = new int[X + 1][ X + 1];
        generateNoiseMap();
        generateBackgroundMaps(Resolution);
    }

    public IslandMap(String MapName, int X, int SX, int SY, boolean hasGeneration)
    {
		mapName = MapName;
		System.out.println("new map: " + mapName);
		sx = SX;
		sy = SY;
		
		l = new boolean[X + 1][X + 1];
		o = new boolean[X + 1][ X + 1];
		lk = new boolean[X + 1][ X + 1];
		r = new boolean[X + 1][ X + 1];
		m = new boolean[X + 1][ X + 1];
		 
		ma = new int[X + 1][ X + 1];
		h = new int[X + 1][ X + 1];
		wh = new int[X + 1][ X + 1];
		nm = new int[X + 1][ X + 1];
		st = new int[X + 1][ X + 1];
		str = new int[X + 1][ X + 1];
         
    }
    
    private void generateNoiseMap()
    {
    	nm = MathUtil.generateFibonocciNumbers2D(sx, sx, nm.length, nm[0].length, 3);
    }
    
    
    public void generateBackgroundMaps(int Resolution)
    {
    	
    	int mapsize = (h.length-1)/ Resolution + 1;
    	
    	bh = new int[mapsize][mapsize];
    	bm = new int[mapsize][mapsize];
    	
    	for(int i = 0; i < h.length-1; i+=Resolution)
    	{
    		for(int j = 0; j < h[0].length-1; j+=Resolution)
        	{
    			bh[i/Resolution][j/Resolution] = h[i][j];
    			bm[i/Resolution][j/Resolution] = ma[i][j];
        	}
    	}
    }
    
    
    public IslandMap(IslandMap Map, int Scale, int Resolution)
    {
    	mapName = Map.mapName;
        sx = Map.sx;
        sy = Map.sy;

        l = MathUtil.scaleArray(Map.l, Scale);
        o = MathUtil.scaleArray(Map.o, Scale);
        lk = MathUtil.scaleArray(Map.lk, Scale);
        r = MathUtil.scaleArray(Map.r, Scale);
        m = MathUtil.scaleArray(Map.m, Scale);
        
        //change to closest neighbor
//        ma = MathUtil.scaleArray(Map.ma, Scale);
        ma = MathUtil.scaleArrayClosestNeighbor(Map.ma, Scale);
        
        h = MathUtil.scaleArray(Map.h, Scale);
        wh = MathUtil.scaleArray(Map.wh, Scale);
        st = MathUtil.scaleArray(Map.st, Scale);
        str = MathUtil.scaleArray(Map.st, Scale);
        nm = MathUtil.scaleArray(Map.nm, Scale);
        generateBackgroundMaps(Resolution);
    }
    
    public IslandMap(String MapName,int Resolution, int SX, int SY, boolean[][] L, boolean[][] O, boolean[][] LK, boolean[][] R, boolean[][] M, int[][] MA, int[][] H, int[][] WH, int[][] ST, int[][] STR, int[][] NM)
    {
    	mapName = MapName;
    	sx = SX;
    	sy = SY;
    	l = L;
    	o = O;
    	lk = LK;
    	r = R;
    	m = M;
    	
    	ma = MA;
    	h = H;
    	wh = WH;
    	st = ST;
    	str = STR;
    	nm = NM;
    	
    	generateBackgroundMaps(Resolution);
    }
    
    
}