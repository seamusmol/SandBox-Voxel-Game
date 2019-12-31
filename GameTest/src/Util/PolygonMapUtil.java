package Util;

public class PolygonMapUtil
{
	public static Vector3i getCenter(Vector3i p1,Vector3i p2,Vector3i p3,Vector3i p4)
	{
		Vector3i centVec = new Vector3i();
		centVec.addLocal(p1);
		centVec.addLocal(p2);
		centVec.addLocal(p3);
		centVec.addLocal(p4);
		centVec.divLocal(4);
		
		return centVec;
	}
	
	public static Vector2i getCenter(Vector2i p1,Vector2i p2,Vector2i p3,Vector2i p4)
	{
		Vector2i centVec = new Vector2i();
		centVec.addLocal(p1);
		centVec.addLocal(p2);
		centVec.addLocal(p3);
		centVec.addLocal(p4);
		centVec.divLocal(4);
		
		return centVec;
	}
	
	public static Vector3i getCenterDistance(Vector3i c1, Vector3i c2)
	{
		Vector3i dis = new Vector3i();
		dis.addLocal(c1);
		dis.addLocal(c2);
		dis.divLocal(2);
		
		return dis;
	}
	
	public static int[][] scaleArray(int[][] Array, Float ScaleFactor)
	{
		for(int i = 0; i < Array.length; i++)
		{
			for(int j = 0; j < Array[0].length; j++)
			{
				Array[i][j] *= ScaleFactor;
			}
		}
		return Array;
	}
	
	public static int[][] getMapSnapShot(int[][] MapOne, int Width, int Height, Vector2i Pos)
	{
		int[][] map = new int[Width][Height];
		
		for(int i = 0; i < Width; i++)
		{
			for(int j = 0; j < Height; j++)
			{
				map[i][j] = MapOne[Pos.getX()+i][Pos.getY()+j];
			}
		}
		return map;
	}
	
	public static boolean[][] mergePolyLandMaps(boolean[][] MapOne, boolean[][] MapTwo, boolean OverrideMode)
	{
		boolean[][] newMap = MapOne;
		if(newMap.length == MapTwo.length ||newMap[0].length == MapTwo[0].length)
		{
			for(int i = 0; i < newMap.length; i++)
			{
				for(int j = 0; j < newMap[0].length; j++)
				{
					if(OverrideMode)
					{
						if(MapTwo[i][j])
						{
							newMap[i][j] = MapTwo[i][j];
						}
					}
					else
					{
						if(!MapTwo[i][j])
						{
							newMap[i][j] = MapTwo[i][j];
						}
					}
				}
			}
			
			System.out.println("Merge complete");
			
		}
		return newMap;
	}
	
	public static boolean[][] mergeDifferentSizedPolyLandMaps(boolean[][] MapOne, boolean[][] MapTwo, Vector2i StartPos, boolean OverrideMode)
	{
		boolean[][] newMap = MapOne.clone();
		
		
		for(int i = 0; i < MapTwo.length; i++)
		{
			for(int j = 0; j < MapTwo[0].length; j++)
			{
				if(OverrideMode)
				{
					if(MapTwo[i][j])
					{
						newMap[StartPos.getX()+i][StartPos.getY()+j] = MapTwo[i][j];
					}
				}
				else
				{
					if(!MapTwo[i][j])
					{
						newMap[StartPos.getX()+i][StartPos.getY()+j] = MapTwo[i][j];
					}
				}
			}
		}
			System.out.println("Merge Successful");
			
		
		return newMap;
	}
	
	public static int[][] addNoise(int[][] Map, int DigitSize)
	{
		int[][] noise = MathUtil.generateFibonocciNumbers2D(0, 0, Map.length, Map[0].length, DigitSize);
		
		for(int i = 0; i < Map.length; i++)
		{
			for(int j = 0; j < Map.length; j++)
			{
				Map[i][j] += Math.pow(10, DigitSize-1)/2 - noise[i][j];
			}
		}
		return Map;
	}
	
	public static int[][] mergeDifferentSizedHeightMaps(int[][] MapOne, int[][] MapTwo, Vector2i StartPos, boolean OverrideMode)
	{
		int[][] newMap = MapOne.clone();
		
		for(int i = 0; i < MapTwo.length; i++)
		{
			for(int j = 0; j < MapTwo[0].length; j++)
			{
				if(OverrideMode)
				{
					if(MapTwo[i][j] > newMap[StartPos.getX()+i][StartPos.getY()+j])
					{
						newMap[StartPos.getX()+i][StartPos.getY()+j] = MapTwo[i][j];
					}
				}
				else
				{
					if(MapTwo[i][j] < newMap[StartPos.getX()+i][StartPos.getY()+j])
					{
						newMap[StartPos.getX()+i][StartPos.getY()+j] = MapTwo[i][j];
					}
				}
			}
		}
		
			
		
		
		return newMap;
	}
	
}