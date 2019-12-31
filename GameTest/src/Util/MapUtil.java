package Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import Villages.Structure;

public class MapUtil
{
	
	public static boolean withinRange(List<Vector2f> List, Vector2f Position, float Distance)
	{
		for(int i = 0; i < List.size(); i++)
		{
			if(List.get(i).distance(Position) < Distance)
			{
				return true;
			}
		}
		return false;
	}
	
	public static void SetArea(int PX, int PY, int SizeX, int SizeY, int[][] Map, int Value)
	{
		for(int i = 0; i < SizeX; i++)
		{
			for(int j = 0; j < SizeY; j++)
			{
				if( PX+i >= 0 && PY+j >= 0 && PX+i < Map.length && PY+j < Map[0].length)
				{
					Map[PX+i][PY+j] = Value;	
				}
			}
		}
	}
	
	public static void SetAreaGradient2D(int PX, int PY, int SizeX, int SizeY, int[][] Map, int V1, int V2, int V3, int V4)
	{
		for(int i = 0; i < SizeX; i++)
		{
			for(int j = 0; j < SizeY; j++)
			{
				if( PX+i >= 0 && PY+j >= 0 && PX+i < Map.length && PY+j < Map[0].length)
				{
					Map[PX+i][PY+j] = Math.round(MathUtil.biLerp(i, j, 0, 0, SizeX, SizeY, V1, V2, V3, V4));
				}
			}
		}
	}
	
	
	public static boolean IsMapType(Vector2f P1, Vector2f P2, boolean[][] Map)
	{
		Vector2f dir = new Vector2f(P2.x-P1.x,P2.y-P1.y).normalize();
		
		int distance = (int) P1.distance(P2);
		for(int i = 0; i < distance; i++)
		{
			int px = (int)(dir.x * i + P1.x);
			int py = (int)(dir.y * i + P1.y);
			
			if( px >= 0 && py >= 0 && px < Map.length && py < Map[0].length)
			{
				if(!Map[px][py])
				{
					return false;
				}
			}
		}
		return true;
	}
	
    public static boolean HasSurrounding(int X, int Y, boolean[][] Map, int Radius)
    {
        for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
            	
	                if (Map[X + countX][ Y + countY])
	                {
	                    int dist = (int)Math.sqrt(countX * countX + countY * countY);
	                    if (dist <= Radius)
	                    {
	                        return true;
	                    }
	                }
				}
            }
        }
        return false;
    }
    
    public static boolean HasSurrounding(Vector2f Position, boolean[][] Map, int Radius)
    {
    	int X = (int)Position.x;
    	int Y = (int)Position.y;
    	
        for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
	                if (Map[X + countX][ Y + countY])
	                {
	                    int dist = (int)Math.sqrt(countX * countX + countY * countY);
	                    if (dist <= Radius)
	                    {
	                        return true;
	                    }
	                }
				}
            }
        }
        return false;
    }

    public static boolean HasSurrounding(Vector3f Position, List<Vector3f> Points, double Distance)
    {
        for (int i = 0; i < Points.size(); i++)
        {
            if (MathUtil.distance(Position, Points.get(i)) <= Distance)
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean HasSurrounding(Vector2f Position, List<Vector2f> Points, double Distance)
    {
        for (int i = 0; i < Points.size(); i++)
        {
            if (MathUtil.distance(Position, Points.get(i)) <= Distance)
            {
                return true;
            }
        }
        return false;
    }
    

    public static boolean HasSurrounding(int X, int Y, int[][] Map, int Radius)
    {
        for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
	                if (Map[X + countX][ Y + countY] != 0)
	                {
	                    int dist = (int)Math.sqrt(countX * countX + countY * countY);
	                    if (dist <= Radius)
	                    {
	                        return true;
	                    }
	                }
				}
            }
        }
        return false;
    }
    
    public static double GetLowestSurroundingValue(int X, int Y, boolean[][] Map, int[][] ValueMap, int Radius)
    {
    	double val = 999;
        for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
	            	if(Map[X + countX][ Y + countY])
	            	{
		                if (ValueMap[X + countX][Y + countY] != 0)
		                {
		                    int tempval = ValueMap[X + countX][ Y + countY];
		                    if (tempval < val)
		                    {
		                    	val = tempval;
		                    }
		                }
	            	}
				}
            }
        }
        return val;
    }
    
    public static double GetHighestSurroundingValue(int X, int Y, boolean[][] Map, int[][] ValueMap, int Radius)
    {
    	double val = 999;
        for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
	            	if(Map[X + countX][ Y + countY])
	            	{
		                if (ValueMap[X + countX][Y + countY] != 0)
		                {
		                    int tempval = ValueMap[X + countX][ Y + countY];
		                    if (tempval > val)
		                    {
		                    	val = tempval;
		                    }
		                }
	            	}
				}
            }
        }
        return val;
    }

    //4 sided search
    public static int GetDirection(int X, int Y, boolean[][] Map)
    {
        int[] vals = { 999, 999, 999, 999 };

        for (int i = X; i < Map.length; i++)
        {
        	if( i >= 0 && Y >= 0 && i < Map.length && Y < Map[0].length)
			{
	            if (Map[i][ Y])
	            {
	                vals[1] = i - X;
	                break;
	            }
			}
        }
        for (int i = X; i >= 0; i--)
        {
        	if( i >= 0 && Y >= 0 && i < Map.length && Y < Map[0].length)
			{
	            if (Map[i][ Y])
	            {
	                vals[3] = X - i;
	                break;
	            }
			}
        }
        for (int j = Y; j < Map[0].length; j++)
        {
        	if( X >= 0 && j >= 0 && X < Map.length && j < Map[0].length)
			{
	            if (Map[X][ j])
	            {
	                vals[2] = j - Y;
	                break;
	            }
			}
        }
        for (int j = Y; j >= 0; j--)
        {
        	if( X >= 0 && j >= 0 && X < Map.length && j < Map[0].length)
			{
	            if (Map[X][ j])
	            {
	                vals[0] = Y - j;
	                break;
	            }
			}
        }

        int lowestIndex = 0;
        for (int i = 0; i < vals.length; i++)
        {
            if (vals[lowestIndex] > vals[i])
            {
                lowestIndex = i;
            }
        }
        return vals[0] == 999 && vals[1] == 999 && vals[2] == 999 && vals[3] == 999 ? 4 : lowestIndex;
    }
    
    public static boolean HasStructureSurrounding(Structure NewStructure, boolean[][] Map, int Radius)
    {
        for (int nx = 0; nx < NewStructure.WX; nx++)
        {
            for (int nz = 0; nz < NewStructure.WZ; nz++)
            {
                Vector2f NRotPoint = MathUtil.RotatePoint(nx, nz, NewStructure.Angle);
                NRotPoint.x += NewStructure.Position.x;
                NRotPoint.y += NewStructure.Position.z;

                if (HasSurrounding((int)(NRotPoint.x), (int)(NRotPoint.y), Map, Radius))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static int GetDirectionAngle(int X, int Y, boolean[][] Map)
    {
        int[] vals = { 0, 90, 180, 270, -999 };

        return vals[GetDirection(X, Y, Map)];
    }

    public static int GetSurroundingCount(int X, int Y, boolean[][] Map, int Radius)
    {
        int count = 0;
        for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
	                if (Map[X + countX][ Y + countY])
	                {
	                    int dist = (int)Math.sqrt(countX * countX + countY * countY);
	                    if (dist <= Radius * Radius)
	                    {
	                        count++;
	                    }
	                }
				}
            }
        }
        return count;
    }

    public static boolean[][] createCurve(boolean[][] Map, Vector2f P1, Vector2f P2, int width)
    {
        boolean[][] newMap = Map;

        Vector2f dir = new Vector2f(P2.x - P1.x, P2.y - P1.y);
        dir = dir.normalize();

        int distance = MathUtil.distance(P1, P2).intValue();

        for (int i = 0; i <= distance; i++)
        {
            float wavePosX = (float)(dir.x * i + dir.x * Math.sin(Math.PI * i / distance) * 10);
            float wavePosY = (float)(dir.y * i + dir.y * Math.sin(Math.PI * i / distance) / 10);

            int px = (int)(P1.x + wavePosX);
            int py = (int)(P1.y + wavePosY);

            for (int countX = -width; countX < width; countX++)
            {
                for (int countY = -width; countY < width; countY++)
                {
                	if( px + countX >= 0 && py + countY >= 0 && px + countX < Map.length && py + countY < Map[0].length)
    				{
            			newMap[px + countX][ py + countY] = true;
                	}
                }
            }
        }
        
        int px = (int) P2.x;
        int py = (int) P2.y;
        for (int countX = -width; countX < width; countX++)
        {
            for (int countY = -width; countY < width; countY++)
            {
            	if( px + countX >= 0 && py + countY >= 0 && px + countX < Map.length && py + countY < Map[0].length)
				{
    				newMap[(int)P2.x + countX][ (int)P2.y + countY] = true;
				}
            }
        }
		
        return newMap;
    }

    public static void generateSineWave(boolean[][] Map, Vector2f StartPoint, Vector2f EndPoint, int WaveCount, int Width, int Amplitude)
    {
        Vector2f direction = new Vector2f(EndPoint.x - StartPoint.x, EndPoint.y - StartPoint.y);
        direction = direction.normalize();
        Vector2f crossDir = new Vector2f(direction.y, direction.x);

        int distance = MathUtil.distance(StartPoint, EndPoint).intValue();

        for (int i = 0; i <= distance; i++)
        {
            int posX = (int)Math.round(StartPoint.x + direction.x * i);
            int posY = (int)Math.round(StartPoint.y + direction.y * i);

            float waveX = (float)((Amplitude * Math.sin(Math.PI * i / distance * WaveCount)) * direction.y);
            float waveY = (float)((Amplitude * Math.sin(Math.PI * i / distance * WaveCount)) * direction.x);

            Vector2f position = new Vector2f(posX + waveX, posY + waveY);

            for (int j = -Width; j < Width; j++)
            {
                int crossOffsetX = (int)Math.round(crossDir.x * j);
                int crossOffsetY = (int)Math.round(crossDir.y * j);
                if (position.x + crossOffsetX > 1 && position.x + crossOffsetX < Map.length - 1 && position.y + crossOffsetY > 1 && position.y + crossOffsetY < Map[0].length - 1)
                {
	                Map[(int)position.x + crossOffsetX][ (int)position.y + crossOffsetY] = true;
	                Map[(int)position.x + crossOffsetX - 1][(int)position.y + crossOffsetY] = true;
	                Map[(int)position.x + crossOffsetX][(int)position.y + crossOffsetY - 1] = true;
	                Map[(int)position.x + crossOffsetX + 1][(int)position.y + crossOffsetY] = true;
	                Map[(int)position.x + crossOffsetX][(int)position.y + crossOffsetY + 1] = true;
                }
            }
        }
    }
    
    public static void generateSineWave(boolean[][] Map, Vector2f StartPoint, Vector2f EndPoint, int WaveCount, int Width, int SX, int WX)
    {
        Vector2f direction = new Vector2f(EndPoint.x - StartPoint.x, EndPoint.y - StartPoint.y);
        direction = direction.normalize();
        Vector2f crossDir = new Vector2f(direction.y, direction.x);

        int distance = MathUtil.distance(StartPoint, EndPoint).intValue();

        for (int i = 0; i <= distance; i++)
        {
            int posX = (int)Math.round(StartPoint.x + direction.x * i);
            int posY = (int)Math.round(StartPoint.y + direction.y * i);

            float waveX = (float)((20 * Math.sin(Math.PI * i / distance * WaveCount)) * direction.y);
            float waveY = (float)((20 * Math.sin(Math.PI * i / distance * WaveCount)) * direction.x);

            Vector2f position = new Vector2f(posX + waveX, posY + waveY);

            int width = (int)(SX % (WX / 64) * Math.sin(Math.PI * i / distance * WaveCount * WaveCount) + (WX / 64) + 1);

            for (int j = -width; j < width; j++)
            {
                int crossOffsetX = (int)Math.round(crossDir.x * j);
                int crossOffsetY = (int)Math.round(crossDir.y * j);
                if (position.x + crossOffsetX > 1 && position.x + crossOffsetX < WX - 1 && position.y + crossOffsetY > 1 && position.y + crossOffsetY < WX - 1)
                {
                    Map[(int)position.x + crossOffsetX][ (int)position.y + crossOffsetY] = true;
                    Map[(int)position.x + crossOffsetX - 1][(int)position.y + crossOffsetY] = true;
                    Map[(int)position.x + crossOffsetX][(int)position.y + crossOffsetY - 1] = true;
                    Map[(int)position.x + crossOffsetX + 1][(int)position.y + crossOffsetY] = true;
                    Map[(int)position.x + crossOffsetX][(int)position.y + crossOffsetY + 1] = true;
                }
            }
        }
    }
    
    public static double getBudgetDistance(int X, int Y, boolean[][] Map, int Radius)
    {
    	int maxDist = Radius;
    	for(int i = 2; i <= Radius; i++)
    	{
    		if(X-i <= 0)
    		{
    			maxDist = i;
    			break;
    		}
    		if(X+i >= Map.length-1)
    		{
    			maxDist = i;
    			break;
    		}
    		if(Y-i <= 0)
    		{
    			maxDist = i;
    			break;
    		}
    		if(Y+i >= Map.length-1)
    		{
    			maxDist = i;
    			break;
    		}
    	}
    	
    	if(maxDist <= 2)
    	{
    		return -1;
    	}
    	
    	for(int l = 2; l < maxDist; l++)
    	{
    		int length = l*2-1;
    		
    		int halflength = (length-1)/2;
    		
    		for(int i = 1; i < l*2-2; i++)
    		{
    			if(Map[X-halflength+i][Y-l])
    			{
    				return l;
    			}
    			if(Map[X-halflength+i][Y+l])
    			{
    				return l;
    			}
    			if(Map[X-l][Y-halflength+i])
    			{
    				return l;
    			}
    			if(Map[X+l][Y-halflength+i])
    			{
    				return l;
    			}
    		}
    		
    		if(Map[X-halflength][Y-halflength])
    		{
    			return l;
    		}
    		if(Map[X+halflength][Y-halflength])
    		{
    			return l;
    		}
    		if(Map[X-halflength][Y+halflength])
    		{
    			return l;
    		}
    		if(Map[X+halflength][Y+halflength])
    		{
    			return l;
    		}
    	}
    	return -1;
    }
    
    public static double getBudgetDistance(int X, int Y, int[][] Map, int Radius)
    {
    	int maxDist = Radius;
    	for(int i = 0; i <= Radius; i++)
    	{
    		if(X-i <= 0)
    		{
    			maxDist = i;
    			break;
    		}
    		if(X+i >= Map.length-1)
    		{
    			maxDist = i;
    			break;
    		}
    		if(Y-i <= 0)
    		{
    			maxDist = i;
    			break;
    		}
    		if(Y+i >= Map.length-1)
    		{
    			maxDist = i;
    			break;
    		}
    	}
    	
    	if(maxDist <= 2)
    	{
    		return -1;
    	}
    	
    	for(int l = 0; l < maxDist; l++)
    	{
    		int length = l*2-1;
    		
    		int halflength = (length-1)/2;
    		
    		for(int i = 0; i < l*2-2; i++)
    		{
    			if(Map[X-halflength+i][Y-l] != 0)
    			{
    				return l;
    			}
    			if(Map[X-halflength+i][Y+l] != 0)
    			{
    				return l;
    			}
    			if(Map[X-l][Y-halflength+i] != 0)
    			{
    				return l;
    			}
    			if(Map[X+l][Y-halflength+i] != 0)
    			{
    				return l;
    			}
    		}
    		
    		if(Map[X-halflength][Y-halflength] != 0)
    		{
    			return l;
    		}
    		if(Map[X+halflength][Y-halflength] != 0)
    		{
    			return l;
    		}
    		if(Map[X-halflength][Y+halflength] != 0)
    		{
    			return l;
    		}
    		if(Map[X+halflength][Y+halflength] != 0)
    		{
    			return l;
    		}
    	}
    	return -1;
    }
    
    public static int getBudgetValue(int X, int Y, boolean[][] Map, int[][] ValueMap, int Radius)
    {
    	int maxDist = Radius;
    	for(int i = 0; i <= Radius; i++)
    	{
    		if(X-i < 0)
    		{
    			maxDist = i;
    			break;
    		}
    		if(X+i > Map.length-1)
    		{
    			maxDist = i;
    			break;
    		}
    		if(Y-i < 0)
    		{
    			maxDist = i;
    			break;
    		}
    		if(Y+i > Map.length-1)
    		{
    			maxDist = i;
    			break;
    		}
    	}
    	
    	if(maxDist < 2)
    	{
    		return -1;
    	}
    	
    	for(int l = 0; l < maxDist; l++)
    	{
    		int length = l*2-1;
    		
    		int halflength = (length-1)/2;
    		
    		for(int i = 0; i < l*2-2; i++)
    		{
    			if(Map[X-halflength+i][Y-l])
    			{
    				return ValueMap[X-halflength+i][Y-l];
    			}
    			if(Map[X-halflength+i][Y+l])
    			{
    				return ValueMap[X-halflength+i][Y+l];
    			}
    			if(Map[X-l][Y-halflength+i])
    			{
    				return ValueMap[X-l][Y-halflength+i];
    			}
    			if(Map[X+l][Y-halflength+i])
    			{
    				return ValueMap[X+l][Y-halflength+i];
    			}
    		}
    		
    		if(Map[X-halflength][Y-halflength])
    		{
    			return ValueMap[X-halflength][Y-halflength];
    		}
    		if(Map[X+halflength][Y-halflength])
    		{
    			return ValueMap[X+halflength][Y-halflength];
    		}
    		if(Map[X-halflength][Y+halflength])
    		{
    			return ValueMap[X-halflength][Y+halflength];
    		}
    		if(Map[X+halflength][Y+halflength])
    		{
    			return ValueMap[X+halflength][Y+halflength];
    		}
    	}
    	return -1;
    }
    
    
    public static double GetDist(int X, int Y, boolean[][] Map, int Radius)
    {
        double dist = Radius * 4;
        int minX = X - Radius > 0 ? X - Radius : 0;
        int minY = Y - Radius > 0 ? Y - Radius : 0;
        int maxX = X + Radius < Map.length ? X + Radius : Map.length;
        int maxY = Y + Radius < Map[0].length ? Y + Radius : Map[0].length;
        for (int i = minX; i < maxX; i++)
        {
            for (int j = minY; j < maxY; j++)
            {
                if (Map[i][j])
                {
                    double tempDist = MathUtil.distance(new Vector2f(X, Y), new Vector2f(i, j));
                    if (tempDist <= dist)
                    {
                        dist = tempDist;
                    }
                }
				
            }
        }
        return dist == Radius * 4 ? -1 : dist;
    }


    public static Vector2f getClosest(Vector2f Position, boolean[][] Map, int Radius)
    {
    	int X = (int)Position.x;
    	int Y = (int)Position.y;
    	
    	Vector2f curpos = new Vector2f();
    	int curdist = (int)Position.distance(curpos);
    	for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
        			if (Map[X + countX][ Y + countY])
	                {
	                	Vector2f pos = new Vector2f(X + countX,Y + countY);
	                    int dist = (int)Position.distance(pos);
	                    if (dist < curdist)
	                    {
	                       curpos = pos;
	            		   curdist = dist;
	                    }
	                }
            	}
            }
        }
    	return curpos;
    }
    
    public static Vector2f getClosest(int X, int Y, boolean[][] Map, int Radius)
    {
    	Vector2f Position = new Vector2f(X,Y);
    	Vector2f curpos = new Vector2f();
    	int curdist = (int)Position.distance(curpos);
    	for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
        			if (Map[X + countX][ Y + countY])
	                {
	                	Vector2f pos = new Vector2f(X + countX,Y + countY);
	                    int dist = (int)Position.distance(pos);
	                    if (dist < curdist)
	                    {
	                       curpos = pos;
	            		   curdist = dist;
	                    }
                	}
            	}
            }
        }
    	return curpos;
    }

    public static Vector2f getClosest(int X, int Y, int[][] Map, int Radius)
    {
    	Vector2f Position = new Vector2f(X,Y);
    	Vector2f curpos = new Vector2f();
    	int curdist = (int)Position.distance(curpos);
    	for (int countX = -Radius; countX <= Radius; countX++)
        {
            for (int countY = -Radius; countY <= Radius; countY++)
            {
            	if( X + countX >= 0 && Y + countY >= 0 && X + countX < Map.length && Y + countY < Map[0].length)
				{
        			if (Map[X + countX][ Y + countY] != 0)
	                {
	                	Vector2f pos = new Vector2f(X + countX,Y + countY);
	                    int dist = (int)Position.distance(pos);
	                    if (dist < curdist)
	                    {
	                       curpos = pos;
	            		   curdist = dist;
	                    }
	                }
            	}
            }
        }
    	return curpos;
    }

    private static void recursive(Vector2f StartPosition, List<Vector2f> List, int Tolerance, List<Vector2f> CollectionList)
    {
    	for(int i = 0; i < List.size(); i++)
    	{
    		Vector2f entry = List.get(i);
    		if(List.get(i).distance(StartPosition) < Tolerance)
    		{
    			CollectionList.add(StartPosition);
    			CollectionList.add(entry);
    			List.remove(entry);
    			recursive(entry,List, Tolerance, CollectionList);
    			break;
    		}
    	}
    }
    
    public static List<Vector2f> sortByNeighbor(List<Vector2f> List, int Tolerance, int SizeTolerance)
    {
    	List<List<Vector2f>> lists = new ArrayList<List<Vector2f>>();
    	 
    	List<Vector2f> tempList = new ArrayList<Vector2f>();
    	tempList.addAll(List);
    	
    	while( tempList.size() > 1)
    	{
    		for(int i = 0; i < tempList.size(); i++)
			{
				List<Vector2f> CollectList = new ArrayList<Vector2f>();
				recursive(tempList.get(i), tempList, Tolerance, CollectList);
				if(CollectList.size() > SizeTolerance)
				{
					lists.add(CollectList);
				}
			}
    	}
		
    	List<Vector2f> sortedList = new ArrayList<Vector2f>();
    	for(int i = 0; i < lists.size(); i++)
    	{
    		sortedList.addAll(lists.get(i));
    		
    		for(int j = 0; j < lists.get(i).size(); j++)
    		{
    			sortedList.add(lists.get(i).get(j));
    		}
    	}
    	return sortedList;
    }
    
   

    public static boolean listsContainPoint(Vector2f Point, List<List<Vector2f>> Lists)
    {
    	for(int j = 0; j < Lists.size(); j++)
		{
			if(Lists.get(j).contains(Point))
			{
				return true;
			}
		}
    	return false;
    	
    }
    
    public static boolean[][] OverrideMap(boolean[][] ShadeMap, boolean[][] Map)
    {
        boolean[][] newMap = ShadeMap;
        for (int i = 0; i < newMap.length; i++)
        {
            for (int j = 0; j < newMap[0].length; j++)
            {
            	if( i >= 0 && j >= 0 && i < Map.length && j < Map[0].length)
				{
	                if (Map[i][ j])
	                {
	                    newMap[i][ j] = Map[i][ j];
	                }
				}
            }
        }
        return newMap;
    }

    public static boolean[][] FillShape(List<Vector2f> PointList, boolean[][] ShadeMap)
    {
    	Vector2f Center = new Vector2f();
        for (int i = 0; i < PointList.size(); i++)
        {
            Center.x += PointList.get(i).x;
            Center.y += PointList.get(i).y;
        }
        Center.x /= PointList.size();
        Center.y /= PointList.size();

        List<Vector2f> SortedList = PointList;
        Comparator<Vector2f> angSort = (Vector2f v1,Vector2f v2)-> MathUtil.GetAngle(Center, v1).compareTo(MathUtil.GetAngle(Center, v2));
        PointList.sort(angSort);

        SortedList.add(SortedList.get(0));
        
        for (int i = 0; i < SortedList.size() - 1; i++)
        {
            RayTrace(SortedList.get(i), SortedList.get(i+1), ShadeMap);
        }

        boolean[][] FillMap = basicFill(ShadeMap);
        
        boolean[][] NewMap = new boolean[FillMap.length][ FillMap[0].length];
        for (int i = 0; i < NewMap.length; i++)
        {
            for (int j = 0; j < NewMap[0].length; j++)
            {
            	if( i >= 0 && j >= 0 && i < NewMap.length && j < NewMap[0].length)
				{
            		NewMap[i][ j] = !FillMap[i][ j];
				}
            }
        }
        return NewMap;
    }
    
    public static boolean[][] topBasicFill(boolean[][] PointMap)
    {
    	 boolean[][] shadeMap = new boolean[PointMap[0].length][ PointMap[0].length];
         for (int i = 0; i < shadeMap[0].length; i++)
         {
             for (int j = shadeMap[0].length - 1; j >= 0; j--)
             {
            	 if( i >= 0 && j >= 0 && i < PointMap.length && j < PointMap[0].length)
            	 {
	                 if (PointMap[i][ j])
	                 {
	                     break;
	                 }
	                 shadeMap[i][ j] = true;
            	 }
             }
         }
         return shadeMap;
    }
    
    public static void invertMap(boolean[][] Map)
    {
    	for(int i = 0; i < Map.length; i++)
    	{
    		for(int j = 0; j < Map.length; j++)
        	{
    			Map[i][j] = !Map[i][j];
        	}
    	}
    }
    
    /*
    public static void FloodFill(int PX, int PY, boolean[][] FillMap, boolean[][] ShadeMap)
    {
    	if(PX < 0 || PY < 0 || PX >= FillMap.length|| PY >= FillMap[0].length)
    	{
    		return;
    	}
    	
    	if(ShadeMap[PX][PY] || FillMap[PX][PY])
    	{
    		return;
    	}
    	
    	FillMap[PX][PY] = true;
    	FloodFill(PX - 1, PY, FillMap,ShadeMap);
    	FloodFill(PX, PY - 1, FillMap,ShadeMap);
    	FloodFill(PX + 1, PY, FillMap,ShadeMap);
    	FloodFill(PX, PY + 1, FillMap,ShadeMap);
    }
    */
    
    public static boolean[][] basicFill(boolean[][] PointMap)
    {
        boolean[][] shadeMap = new boolean[PointMap.length][ PointMap[0].length];
        //j++;
        for (int i = 0; i < shadeMap.length; i++)
        {
            for (int j = 0; j < shadeMap[0].length; j++)
            {
                if (PointMap[i][j])
                {
                    break;
                }
                shadeMap[i][ j] = true;
            }
        }
        //j--;
        for (int i = 0; i < shadeMap.length; i++)
        {
            for (int j = shadeMap[0].length - 1; j >= 0; j--)
            {
                if (PointMap[i][ j])
                {
                    break;
                }
                shadeMap[i][ j] = true;
            }
        }

        //i++;
        for (int i = 0; i < shadeMap.length; i++)
        {
            for (int j = 0; j < shadeMap[0].length; j++)
            {
                if (PointMap[i][ j])
                {
                    break;
                }
                shadeMap[i][ j] = true;
            }
        }
        //i--;
        for (int j = 0; j < shadeMap[0].length; j++)
        {
            for (int i = shadeMap.length - 1; i >= 0; i--)
            {
                if (PointMap[i][ j])
                {
                    break;
                }
                shadeMap[i][ j] = true;
            }
        }

        boolean hasMove = true;
        while (hasMove)
        {
            hasMove = false;
            for (int i = 1; i < PointMap.length - 1; i++)
            {
                for (int j = 1; j < PointMap[0].length - 1; j++)
                {
                	if (shadeMap[i][j])
                    {
                		if(i > 0 && j > 0)
                		{
                			if (!PointMap[i - 1][ j] && !shadeMap[i - 1][ j])
	                        {
	                        	shadeMap[i - 1][ j] = true;
	                            hasMove = true;
	                            i--;
	                            j--;
	                        }
	                        else if (!PointMap[i + 1][ j] && !shadeMap[i + 1][ j])
	                        {
	                            shadeMap[i + 1][ j] = true;
	                            hasMove = true;
	                            i--;
	                            j--;
	                        }
	                        else if (!PointMap[i][ j - 1] && !shadeMap[i][ j - 1])
	                        {
	                            shadeMap[i][ j - 1] = true;
	                            hasMove = true;
	                            i--;
	                            j--;
	                        }
	                        else if (!PointMap[i][ j + 1] && !shadeMap[i][ j + 1])
	                        {
	                            shadeMap[i][ j + 1] = true;
	                            hasMove = true;
	                            i--;
	                            j--;
	                        }
                		}
                    }
                }
            }
        }
        return shadeMap;
    }
    
    public static void nonFill(boolean[][] PointMap)
    {
        for (int i = 1; i < PointMap.length - 1; i++)
        {
            for (int j = 1; j < PointMap[0].length - 1; j++)
            {
                if (!PointMap[i][ j])
                {
                    int val = 0;
                    val += rayTest(new Vector2f(i, j), new Vector2f(0, j), PointMap) ? 1 : 0;
                    val += rayTest(new Vector2f(i, j), new Vector2f(PointMap.length, j), PointMap) ? 1 : 0;
                    val += rayTest(new Vector2f(i, j), new Vector2f(i, 0), PointMap) ? 1 : 0;
                    val += rayTest(new Vector2f(i, j), new Vector2f(i, PointMap[0].length), PointMap) ? 1 : 0;

                    if (val == 4)
                    {
                        PointMap[i][ j] = true;
                    }
                }
            }
        }
    }

    public static void RayTrace(Vector2f Start, Vector2f End, boolean[][] ShadeMap)
    {
    	Vector2f dir = new Vector2f(End.x - Start.x, End.y - Start.y);
        dir = dir.normalize();
        int distance = MathUtil.distance(Start, End).intValue();
        for (int i = 0; i <= distance; i++)
        {
        	
            ShadeMap[(int)(Start.x + dir.x * i)][ (int)(Start.y + dir.y * i)] = true;
        }
    }

    public static boolean rayTest(Vector2f Start, Vector2f End, boolean[][] PointMap)
    {
    	Vector2f dir = new Vector2f(End.x - Start.x, End.y - Start.y);
        dir = dir.normalize();
        int distance = MathUtil.distance(Start, End).intValue();
        for (int i = 0; i <= distance; i++)
        {
            if (PointMap[(int)(Start.x + dir.x * i)][ (int)(Start.y + dir.y * i)])
            {
                return true;
            }
        }
        return false;
    }
}
