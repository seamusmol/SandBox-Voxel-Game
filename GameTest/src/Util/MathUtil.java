package Util;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class MathUtil {

	public static Vector3f round(Vector3f Vec)
	{
		return new Vector3f( Math.round(Vec.getX()), Math.round(Vec.getY()),Math.round(Vec.getZ()));
	}
	
	public static Vector2f round(Vector2f Vec)
	{
		return new Vector2f( Math.round(Vec.getX()), Math.round(Vec.getY()));
	}
	
	public static int stringToNumeric(String Seed)
	{
		if(Seed.length() == 0)
		{
			return 0;
		}
		
		byte[] byteval = Seed.getBytes();
		int val = 0;
		for(int i =0; i < byteval.length; i++)
		{
			val+= (byteval[i]+127);
		}
		
		return val;
	}
	
	public static int vectorToFace(Vector3f Vector)
	{
		Vector3f vec = Vector.normalize();
		
		float absX = Math.abs(vec.x);
		float absY = Math.abs(vec.y);
		float absZ = Math.abs(vec.z);
		  
		boolean isXPositive = vec.x > 0.0f;
		boolean isYPositive = vec.y > 0.0f;
		boolean isZPositive = vec.z > 0.0f;
		
		if (isXPositive && absX >= absY && absX >= absZ) 
		{
			return 0;
		}
		if (!isXPositive && absX >= absY && absX >= absZ)
		{
			return 1;
		}
		if (isYPositive && absY >= absX && absY >= absZ) 
		{
			return 2;
		}
		if (!isYPositive && absY >= absX && absY >= absZ) 
		{
			return 3;
		}
		if (isZPositive && absZ >= absX && absZ >= absY) 
		{
			return 4;
		}
		if (!isZPositive && absZ >= absX && absZ >= absY) 
		{
			return 5;
		}
		return 0;
	}
	
	public static Vector3f vectorToFaceVector(Vector3f Vector)
	{
		Vector3f vec = Vector.normalize();
		
		float absX = Math.abs(vec.x);
		float absY = Math.abs(vec.y);
		float absZ = Math.abs(vec.z);
		  
		boolean isXPositive = vec.x > 0.0f;
		boolean isYPositive = vec.y > 0.0f;
		boolean isZPositive = vec.z > 0.0f;
		
		if (isXPositive && absX >= absY && absX >= absZ) 
		{
			return new Vector3f(1,0,0);
		}
		if (!isXPositive && absX >= absY && absX >= absZ)
		{
			return new Vector3f(-1,0,0);
		}
		if (isYPositive && absY >= absX && absY >= absZ) 
		{
			return new Vector3f(0,1,0);
		}
		if (!isYPositive && absY >= absX && absY >= absZ) 
		{
			return new Vector3f(0,-1,0);
		}
		if (isZPositive && absZ >= absX && absZ >= absY) 
		{
			return new Vector3f(0,0,1);
		}
		if (!isZPositive && absZ >= absX && absZ >= absY) 
		{
			return new Vector3f(0,0,-1);
		}
		return new Vector3f();
	}
	
	
	
	
	public static float getMinDist(Vector3f V1, Vector3f V2)
	{
		return MathUtil.getMin(MathUtil.distance(V1.x, V2.x), MathUtil.distance(V1.y, V2.y), MathUtil.distance(V1.z, V2.z));
	}
	
	public static float getMaxDist(Vector3f V1, Vector3f V2)
	{
		return MathUtil.getMax(MathUtil.distance(V1.x, V2.x), MathUtil.distance(V1.y, V2.y), MathUtil.distance(V1.z, V2.z));
	}
	
	public static float round(float Val, float Roundant)
	{
		int round = (int) (1.0f/Roundant);
		
		return Math.round(Val * round) / round;
	}
	
	public static boolean between(float p1, float p2, float v1)
	{
		return (v1 - p1) * (v1 - p2) <= 0;
	}
	
	public static float distance(float x1, float x2)
	{
		return (float)Math.sqrt((x1-x2)*(x1-x2));
	}
	
	public static int calculateMost(int[] array)
    {
    	int most = 9999;
        int count = 0;
        for (int countX = 0; countX < array.length; countX++)
        {
            if (array[countX] == most)
            {
                continue;
            }
            int curCount = 0;
            for (int countY = 0; countY < array.length; countY++)
            {
                if (array[countX] == array[countY])
                {
                    curCount++;
                }
            }
            if (curCount >= count && array[countX] < most && array[countX] != 0)
            {
                most = array[countX];
                count = curCount;
            }
        }
        return most;
    }
	
	public static int calculateMost(List<Integer> array)
    {
    	int most = 9999;
        int count = 0;
        for (int countX = 0; countX < array.size(); countX++)
        {
            if (array.get(countX) == most)
            {
                continue;
            }
            int curCount = 0;
            for (int countY = 0; countY < array.size(); countY++)
            {
                if (array.get(countX) == array.get(countY))
                {
                    curCount++;
                }
            }
            if (curCount >= count && array.get(countX) < most && array.get(countX) != 0)
            {
                most = array.get(countX);
                count = curCount;
            }
        }
        return most;
    }
	
	public static Double GetAngle(Vector2f Center, Vector2f Point)
    {
        Vector2f Vec = new Vector2f(Point.x - Center.x, Point.y - Center.y);
        double radian = Math.atan2(Vec.x, Vec.y);
        double degrees = radian * (180f / Math.PI);
        return degrees;
    }

//    public static Double GetRadian(double Angle)
//    {
//        return Angle * Math.PI / 180f;
//    }

    public static Vector2f RotatePointRadian(Vector2f P, double Radian)
    {
        double cos = Math.cos(Radian);
        double sin = Math.sin(Radian);

        double px = (P.x * cos) - (P.y * sin);
        double py = (P.x * sin) + (P.y * cos);
        
        return new Vector2f((float)px,(float)py);
    }
    
    public static Vector2f RotatePoint(Vector2f P, double Angle)
    {
        double radians = Angle == 0f ? 0f: Angle* Math.PI / 180f;
        
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double px = (P.x * cos) - (P.y * sin);
        double py = (P.x * sin) + (P.y * cos);
        
        return new Vector2f((float)px,(float)py);
    }
    
    public static Vector2f RotatePoint(double X, double Y, double Angle)
    {
        double radians = Angle == 0f ? 0f : Angle * Math.PI / 180f;

        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double px = X * cos - Y * sin;
        double py = X * sin + Y * cos;

        return new Vector2f((float)px,(float)py);
    }
	
	 public static Double distance(int X1, int X2, int Y1, int Y2)
     {
         return Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2));
     }

     public static Double distance(Vector2f V1, int X2, int Y2)
     {
         return Math.sqrt(Math.pow(V1.x - X2, 2) + Math.pow(V1.y - Y2, 2));
     }

     public static Double distance(Vector2f V1, Vector2f V2)
     {
         return Math.sqrt(Math.pow(V1.x - V2.x, 2) + Math.pow(V1.y - V2.y, 2));
     }

     public static Double distance(Vector3f P1, Vector3f P2)
     {
         return Math.sqrt(Math.pow(P1.x - P2.x, 2) + Math.pow(P1.y - P2.y, 2) + Math.pow(P1.z - P2.z, 2));
     }
	
	public static int getMin(List<Integer> vals)
	{
		int min = vals.get(0);
		
		for(int i = 1; i < vals.size(); i++)
		{
			if(min > vals.get(i))
			{
				min = vals.get(i);
			}
		}
		return min;
	}
	
	public static float getMin(float ... vals)
	{
		float min = vals[0];
		for(int i = 0; i < vals.length; i++)
		{
			if(vals[i] < min)
			{
				min = vals[i];
			}
		}
		return min;
	}
	
	public static int getMin(int ... vals)
	{
		int min = vals[0];
		for(int i = 0; i < vals.length; i++)
		{
			if(vals[i] < min)
			{
				min = vals[i];
			}
		}
		return min;
	}
	
	public static float getMax(float ...vals)
	{
		float max = vals[0];
		for(int i = 0; i < vals.length; i++)
		{
			if(vals[i] > max)
			{
				max = vals[i];
			}
		}
		return max;
	}
	
	public static int getMax(int ...vals)
	{
		int max = vals[0];
		for(int i = 0; i < vals.length; i++)
		{
			if(vals[i] > max)
			{
				max = vals[i];
			}
		}
		return max;
	}
	
	public static int getMax(int Val1, int Val2)
	{
		if(Val1 > Val2) return Val1;
		return Val2;
	}
	
	public static int getMin(int Val1, int Val2)
	{
		if(Val1 < Val2) return Val1;
		return Val2;
	}
	
	public static int getMax(List<Integer> vals)
	{
		int max = vals.get(0);
		
		for(int i = 1; i < vals.size(); i++)
		{
			if(max < vals.get(i))
			{
				max = vals.get(i);
			}
		}
		return max;
	}
	
	public static int getAverage(List<Integer> Vals)
	{
		int sum = 0;
		for(int i = 0; i < Vals.size(); i++)
		{
			sum+= Vals.get(i);
		}
		return sum/Vals.size();
	}
	
	
	public static int getAverage(int Val1, int Val2, int Val3, int Val4)
	{
		int average = 0;
		average+=Val1;
		average+=Val2;
		average+=Val2;
		average+=Val3;
		average/=4;
		
		return average;
	}

	public static void generateBilinearMoistureMap(int PolySizeX, int PolySizeY, byte[][] AffectedMap, byte[][] ValueMap)
	{
		Runnable[][] threads = new Runnable[ValueMap.length-1][ValueMap[0].length-1];
		ExecutorService executor = Executors.newCachedThreadPool();
		for(int i = 0; i < threads.length; i++)
		{
			for(int j = 0; j < threads[0].length; j++)
			{
				threads[i][j] = new MoistureMapThread(AffectedMap, 
						new Vector2i(PolySizeX * i, PolySizeY * j), 
						new Vector2i(PolySizeX * i + PolySizeX, PolySizeY * j),
						new Vector2i(PolySizeX * i, PolySizeY * j + PolySizeY),
						new Vector2i(PolySizeX * i + PolySizeX, PolySizeY * j + PolySizeY),
						ValueMap[i][j], 
						ValueMap[i+1][j], 
						ValueMap[i][j+1], 
						ValueMap[i+1][j+1]);
			}
		}
		for(int i = 0; i < threads.length; i++)
		{
			for(int j = 0; j < threads.length; j++)
			{
				executor.execute(threads[i][j]);
			}
		}
		executor.shutdown();
		try 
		{
			executor.awaitTermination(100, TimeUnit.MILLISECONDS);
	    } 
		catch (InterruptedException e) 
		{
	    }
	}
	
	private static class MoistureMapThread implements Runnable 
	{
		byte[][]moistureMap;
		Vector2i pos1;
		Vector2i pos2; 
		Vector2i pos3; 
		Vector2i pos4; 
		int q11; 
		int q21; 
		int q12; 
		int q22;
		
		public MoistureMapThread(byte[][]MoistureMap, Vector2i Pos1, Vector2i Pos2, Vector2i Pos3, Vector2i Pos4, int Q11, int Q21, int Q12, int Q22)
		{
			moistureMap = MoistureMap;
			pos1 = Pos1;
			pos2 = Pos2; 
			pos3 = Pos3; 
			pos4 = Pos4; 
			q11 = Q11; 
			q21 = Q21; 
			q12 = Q12; 
			q22 = Q22;
		}
		@Override
		public void run() 
		{
			Thread.currentThread().setName("Util-MoistureMap");
			
			int minX = MathUtil.getMin(pos1.getX(), pos2.getX(), pos3.getX(), pos4.getX());
			int minY = MathUtil.getMin(pos1.getY(), pos2.getY(), pos3.getY(), pos4.getY());
			int maxX = MathUtil.getMax(pos1.getX(), pos2.getX(), pos3.getX(), pos4.getX());
			int maxY = MathUtil.getMax(pos1.getY(), pos2.getY(), pos3.getY(), pos4.getY());
			
			for(int countX = minX; countX < maxX; countX++)
			{
				for(int countY = minY; countY < maxY; countY++)
				{
					moistureMap[countX][countY] = (byte) MathUtil.biLerp(countX, countY, minX, maxX, minY, maxY, q11, q21, q12, q22);
				}
			}
		}
	}
	
	public static int[][] generateNoise(int X, int Y, int scaleX, int scaleY)
	{
		int[][] vals = generateFibonocciNumbers2D(0, 0, (int)Math.ceil(X/scaleX), (int)Math.ceil(Y/scaleY), 1);
		int[][] scaledVals = new int[X][Y];
		
		for(int i = 0; i < vals.length-1; i++)
		{
			for(int j = 0; j < vals[0].length-1; j++)
			{
				for(int countX = 0; countX <= scaleX; countX++)
				{
					for(int countY = 0; countY <= scaleY; countY++)
					{
						int posX = i * scaleX + i + countX;
						int posY = j * scaleY + j + countY;
						scaledVals[posX][posY] = (int) biLerp(countX, countY, 0, scaleX, 0, scaleY, vals[i][j], vals[i+1][j], vals[i][j+1], vals[i+1][j+1]);
					}
				}
			}
		}
		return scaledVals;
	}
	
	public static boolean[]	generateBinaryNoise(int SeedX, int SeedY, int X, int DigitLength)
	{
		boolean values[] = new boolean[X];
		
		BigInteger bignum = new BigInteger("0");
		
		long num = 2+10 + SeedX;
		long lastNum = 1+10 + SeedY;
		
		long n1 = num;
		long n2 = lastNum;
		
		int count = 0;
		
		int x = 0;
		
		while(true)
		{
			for(int i = 0; i < bignum.toString().length(); i+=DigitLength)
			{
				if(i+DigitLength < bignum.toString().length())
				{
					if(x < X)
					{
						//String tempString = Long.toString(num).substring(i, i+DigitLength);
						
						String tempString = bignum.toString().substring(i, i+DigitLength);
						values[x] = Integer.parseInt(tempString) > 0;
						x++;
					}
					else
					{
						return values;
					}
				}
			}
			
			bignum.add(new BigInteger(num + ""));
			
			if(Math.log10(num) < 17)
			{
				long tempNum = num;
				num += lastNum;
				lastNum = tempNum;
			}
			else
			{
				num = 2+10 + n1;
				lastNum = 2+10 + n2;
				n1 = num;
				n2 = lastNum;
			}
		}
	}
	
	//Return every 2 digits of fibinocci
	public static int[] generateFibonocciNumbers(int SeedX, int SeedY, int X, int DigitLength)
	{
		int values[] = new int[X];
		
		long num = 2+10 + SeedX;
		long lastNum = 1+10 + SeedY;
		
		long n1 = num;
		long n2 = lastNum;
		
		int count = 0;
		
		int x = 0;
		
		while(true)
		{
			for(int i = 0; i < Long.toString(num).length(); i+=DigitLength)
			{
				if(i+DigitLength < Long.toString(num).length())
				{
					if(x < X)
					{
						String tempString = Long.toString(num).substring(i, i+DigitLength);
						values[x] = Integer.parseInt(tempString);
						x++;
					}
					else
					{
						return values;
					}
				}
			}
			
			if(Math.log10(num) < 17)
			{
				long tempNum = num;
				num += lastNum;
				lastNum = tempNum;
			}
			else
			{
				num = 2+10 + n1;
				lastNum = 2+10 + n2;
				n1 = num;
				n2 = lastNum;
//				count++;
//				System.out.println("Count: " + count);
			}
		}
	}
	
	
	public static int[][] generateFibonocciNumbers2D(int SeedX, int SeedY, int X, int Y, int DigitLength)
	{
		int values[][] = new int[X][Y];
		
		long num = 2+10 + SeedX;
		long lastNum = 1+10 + SeedY;
		
		long n1 = num;
		long n2 = lastNum;
		
		int count = 0;
		
		int x = 0;
		int y = 0;
		
		while(true)
		{
			for(int i = 0; i < Long.toString(num).length()-1; i+=DigitLength)
			{
				if(i+DigitLength < Long.toString(num).length())
				{
					if(x < X && y < Y)
					{
						if(i+DigitLength < Long.toString(num).length())
						{
							String tempString = Long.toString(num).substring(i, i+DigitLength);
							values[x][y] = Integer.parseInt(tempString);
							x++;
						}
					}
					else if(x == X && y < Y)
					{
						x = 0;
						y++;
					}
					else
					{
						return values;
					}
				}
			}
			
			if(Math.log10(num) < 17)
			{
				long tempNum = num;
				num += lastNum;
				lastNum = tempNum;
			}
			else
			{
				num = 2+10 + n1;
				lastNum = 2+10 + n2;
				n1 = num;
				n2 = lastNum;
//				count++;
//				System.out.println("Count: " + count);
			}
		}
	}
	
	public static int[][][] generateFibonocciNumbers3D(int SeedX, int SeedY, int X, int Y, int Z, int DigitLength)
	{
		int values[][][] = new int[X][Y][Z];
		
		long num = 2+10 + SeedX;
		long lastNum = 1+10 + SeedY;
		
		long n1 = num;
		long n2 = lastNum;
		
		int count = 0;
		
		int x = 0;
		int y = 0;
		int z = 0;
		
		while(true)
		{
			for(int i = 0; i < Long.toString(num).length()-1; i+=DigitLength)
			{
				if(i+DigitLength < Long.toString(num).length())
				{
					if(x < X && y < Y && z < Z)
					{
						if(i+DigitLength < Long.toString(num).length())
						{
							String tempString = Long.toString(num).substring(i, i+DigitLength);
							values[x][y][z] = Integer.parseInt(tempString);
							x++;
						}
					}
					else if(x == X && y < Y)
					{
						x = 0;
						y++;
					}
					else if(x == X && y == Y && z < Z)
					{
						x= 0;
						y = 0;
						z++;
					}
					else
					{
						return values;
					}
				}
			}
			
			if(Math.log10(num) < 17)
			{
				long tempNum = num;
				num += lastNum;
				lastNum = tempNum;
			}
			else
			{
				num = 2+10 + n1;
				lastNum = 2+10 + n2;
				n1 = num;
				n2 = lastNum;
//				count++;
//				System.out.println("Count: " + count);
			}
		}
	}
	
	public static float lerp(float x, float x1, float x2, float q0, float q1)
	{
	  return ((x2 - x) / (x2 - x1)) * q0 + ((x - x1) / (x2 - x1)) * q1;
	}
	
	public static float biLerp(float x, float y, float x1, float x2, float y1, float y2,float q11, float q12, float q21, float q22) 
	{
	  float v1 = lerp(x, x1, x2, q11, q21);
	  float v2 = lerp(x, x1, x2, q12, q22);

	  return lerp(y, y1, y2, v1, v2);
	}
	
	public static float triLerp(float x, float y, float z, float x1, float x2, float y1, float y2, float z1, float z2, float q000, float q001, float q010, float q011, float q100, float q101, float q110, float q111) 
	{
		float x00 = lerp(x, x1, x2, q000, q100);
	    float x10 = lerp(x, x1, x2, q010, q110);
	    float x01 = lerp(x, x1, x2, q001, q101);
	    float x11 = lerp(x, x1, x2, q011, q111);
	    float r0 = lerp(y, y1, y2, x00, x01);
	    float r1 = lerp(y, y1, y2, x10, x11);
	    
	    return lerp(z, z1, z2, r0, r1);
	}
	
	public static int[][][] scaleArray(int[][][] OldArray, int factor)
	{
		int[][][] newArray = new int[(int) ((OldArray.length-1)*factor)+1][(int) ((OldArray[0].length-1)*factor)+1][(int) ((OldArray[0][0].length-1)*factor)+1];
		for(int i = 0; i < OldArray.length-2; i++)
		{
			for(int j = 0; j < OldArray[0].length-2; j++)
			{
				for(int k = 0; k < OldArray[0][0].length-2; k++)
				{
					float q000 = OldArray[i][j+1][k];
					float q001 = OldArray[i][j][k];
					float q010 = OldArray[i][j+1][k+1];
					float q011 = OldArray[i][j][k+1];
					
					float q100 = OldArray[i+1][j+1][k];
					float q101 = OldArray[i+1][j][k];
					float q110 = OldArray[i+1][j+1][k+1];
					float q111 = OldArray[i+1][j][k+1];
					
					for(int countX = 0; countX < factor; countX++)
					{
						for(int countY = 0; countY < factor; countY++)
						{
							for(int countZ = 0; countZ < factor; countZ++)
							{
								float px = (1.00f/factor * (countX%factor))%1.0f; 
								float py = (1.00f/factor * (countY%factor))%1.0f;
								float pz = (1.00f/factor * (countZ%factor))%1.0f;
										
								int val = (int)triLerp(px, py, pz, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, q000, q001, q010, q011, q100, q101, q110, q111);
								newArray[i * factor + countX][j * factor + countY][k * factor + countZ] = val;
							}
						}
					}
					
				}
			}
		}
		
//		for(int i = 0; i < newArray.length; i++)
//		{
//			for(int k = 0; k < newArray[0][0].length; k++)
//			{
//				newArray[i][newArray[0].length-1][k] = newArray[i][newArray[0].length-2][k];
//			}
//		}
		
		
		
		return newArray;
	}
	
	//scales array using bilerp
	
	//float posX, float posY, float X1, float X2, float Y1, float Y2, float Q11, float Q21, float Q12, float Q22
	
	public static int[][] scaleArray(int[][] OldArray, int factor)
	{
		int[][] newArray = new int[(int) ((OldArray.length-1)*factor)+1][(int) ((OldArray[0].length-1)*factor)+1];
		
		for(int i = 0; i < OldArray.length-2; i++)
		{
			for(int j = 0; j < OldArray.length-2; j++)
			{
				float q11 = OldArray[i][j];
				float q21 = OldArray[i][j+1];
				float q12 = OldArray[i+1][j];
				float q22 = OldArray[i+1][j+1];
				
				for(int countX = 0; countX < factor; countX++)
				{
					for(int countY = 0; countY < factor; countY++)
					{
						float px = (1.00f/factor * (countX%factor))%1.0f; 
						float py = (1.00f/factor * (countY%factor))%1.0f;
						
						int val = (int)Math.round(biLerp(px, py, 0.0f, 1.0f, 0.0f, 1.0f, q11, q21, q12, q22));
						//int val = (int)MathUtil.bilerp(px, py, 0, 1.0f, 0, 1.0f, q11, q21, q12, q22);
						
						newArray[i * factor + countX][j * factor + countY] = val;
						
					}
				}
			}
		}
		return newArray;
	}
	
	public static boolean[][] scaleArray(boolean[][] OldArray, int factor)
	{
		boolean[][] newArray = new boolean[(int) ((OldArray.length-1)*factor)+1][(int) ((OldArray[0].length-1)*factor)+1];
		
		for(int i = 0; i < OldArray.length-2; i++)
		{
			for(int j = 0; j < OldArray.length-2; j++)
			{
				float q11 = OldArray[i][j] ? 1:0;
				float q21 = OldArray[i][j+1] ? 1:0;
				float q12 = OldArray[i+1][j] ? 1:0;
				float q22 = OldArray[i+1][j+1] ? 1:0;
				
				for(int countX = 0; countX < factor; countX++)
				{
					for(int countY = 0; countY < factor; countY++)
					{
						float px = (1.00f/factor * (countX%factor))%1.0f; 
						float py = (1.00f/factor * (countY%factor))%1.0f;
						
						int val = (int) Math.round(biLerp(px, py, 0.0f, 1.0f, 0.0f, 1.0f, q11, q21, q12, q22));
						//int val = (int)MathUtil.bilerp(px, py, 0, 1.0f, 0, 1.0f, q11, q21, q12, q22);
						
						newArray[i * factor + countX][j * factor + countY] = val == 1;
						
					}
				}
			}
		}
		return newArray;
	}
	
	public static int[][] scaleArrayClosestNeighbor(int[][] OldArray, int factor)
	{
		int[][] newArray = new int[(int) ((OldArray.length-1)*factor)+1][(int) ((OldArray[0].length-1)*factor)+1];
		
		for(int i = 0; i < OldArray.length-1; i++)
		{
			for(int j = 0; j < OldArray.length-2; j++)
			{
				float q11 = OldArray[i][j];
				float q21 = OldArray[i][j+1];
				float q12 = OldArray[i+1][j];
				float q22 = OldArray[i+1][j+1];
				
				for(int countX = 0; countX < factor; countX++)
				{
					for(int countY = 0; countY < factor; countY++)
					{
						float px = (1.00f/factor * (countX%factor))%1.0f; 
						float py = (1.00f/factor * (countY%factor))%1.0f;
						
						newArray[i * factor + countX][j * factor + countY] = OldArray[i + Math.round(countX/factor)][j + Math.round(countX/factor)];
					}
				}
			}
		}
		return newArray;
	}
	
}
