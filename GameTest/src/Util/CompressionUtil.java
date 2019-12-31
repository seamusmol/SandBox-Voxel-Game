package Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class CompressionUtil 
{
	public static Object[] GenerateByteListBoolean2D(boolean[][] Map)
	{
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < Map.length; i++)
		{
			for(int j = 0; j < Map[0].length; j++)
			{
				flatVoxelArray.add(Map[i][j] ? 1:0);
			}
		}
		List<Integer> compressedVoxelsValues = compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = compress(flatVoxelArray).get(1);
		
		Object[] Data = new Object[3];
		Data[0] = compressedVoxelsValues.size();
		Data[1] = CompressionUtil.IntListToByteList(compressedVoxelsValues);
		Data[2] = CompressionUtil.IntListToByteList(compressedVoxelsQuantities);
		return Data;
	}

	public static Object[] GenerateByteListInt2D(int[][] Map)
	{
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < Map.length; i++)
		{
			for(int j = 0; j < Map[0].length; j++)
			{
				flatVoxelArray.add(Map[i][j]);
			}
		}
		List<Integer> compressedVoxelsValues = compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = compress(flatVoxelArray).get(1);
		
		Object[] Data = new Object[3];
		Data[0] = compressedVoxelsValues.size();
		Data[1] = CompressionUtil.IntListToByteList(compressedVoxelsValues);
		Data[2] = CompressionUtil.IntListToByteList(compressedVoxelsQuantities);
		return Data;
	}

	public static List<Byte> GenerateByteListVector3f(List<Vector3f> List)
	{
		List<Byte> data = new ArrayList<Byte>();
		for(int i = 0; i < List.size(); i++)
		{
			AddValueBytes(List.get(i).x,data);
			AddValueBytes(List.get(i).y,data);
			AddValueBytes(List.get(i).z,data);
		}
		return data;
	}
	
	public static List<Byte> GenerateByteListVector2f(List<Vector2f> List)
	{
		List<Byte> data = new ArrayList<Byte>();
		for(int i = 0; i < List.size(); i++)
		{
			AddValueBytes(List.get(i).x,data);
			AddValueBytes(List.get(i).y,data);
		}
		return data;
	}
	
	public static List<Byte> GenerateByteListInt(List<Integer> List)
	{
		List<Byte> data = new ArrayList<Byte>();
		for(int i = 0; i < List.size(); i++)
		{
			AddValueBytes(List.get(i),data);
		}
		return data;
	}
	
	public static void AddValueBytes(byte[] ByteData, List<Byte> Data)
	{
		for(byte i : ByteData)
		{
			Data.add(i);
		}
	}
	
	public static void AddValueBytes(float Value, List<Byte> Data)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(Value).array();
		Data.add(compressSizeValL1[3]);
		Data.add(compressSizeValL1[2]);
		Data.add(compressSizeValL1[1]);
		Data.add(compressSizeValL1[0]);
	}
	
	public static void AddValueBytes(int Value, List<Byte> Data)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(Value).array();
		Data.add(compressSizeValL1[3]);
		Data.add(compressSizeValL1[2]);
		Data.add(compressSizeValL1[1]);
		Data.add(compressSizeValL1[0]);
	}
	
	public static void AddValueBytes(boolean isUpright, List<Byte> Data) 
	{
		Data.add((byte) (isUpright ? 1:0));
	}
	
	public static void AddValueBytes(Short Value, List<Byte> Data)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(Value).array();
		Data.add(compressSizeValL1[1]);
		Data.add(compressSizeValL1[0]);
	}
	
	public static byte[] getIntArray(int Value)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(Value).array();
		return compressSizeValL1;
	}
	
	public static byte[] getFloatArray(float Value)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(Value).array();
		return compressSizeValL1;
	}
	
	public static Object[] CreateFlatArrayList2D(int[][] Array)
	{
		List<Integer> flatArray = new ArrayList<Integer>();
		for(int i = 0; i < Array.length; i++)
		{
			for(int j = 0; j < Array.length; j++)
			{
				flatArray.add(Array[i][j]);
			}
		}
		return CreateCompressListInt(flatArray);
	}
	
	public static Object[] CreateFlatArrayList2D(boolean[][] Array)
	{
		List<Integer> flatArray = new ArrayList<Integer>();
		for(int i = 0; i < Array.length; i++)
		{
			for(int j = 0; j < Array.length; j++)
			{
				flatArray.add(Array[i][j] ? 1:0);
			}
		}
		return CreateCompressListInt(flatArray);
	}
	
	public static Object[] CreateCompressListInt(List<Integer> List)
	{
		List<Integer> compressedValues = compress(List).get(0);
		List<Integer> compressedQuantities = compress(List).get(1);
		
		Object[] CompressedBytes = new Object[2];
		CompressedBytes[0] = IntListToByteList(compressedValues);
		CompressedBytes[1] = IntListToByteList(compressedQuantities);
		
		return CompressedBytes;
	}
	
    public static List<List<Integer>> compress(List<Integer> List)
	{
		List<Integer> quantities = new ArrayList<Integer>();
		List<Integer> values = new ArrayList<Integer>();
		
		if(List.size() == 0)
		{
			quantities.add(0);
			values.add(0);
			List<List<Integer>> compressedData = new ArrayList<List<Integer>>();
			compressedData.add(values);
			compressedData.add(quantities);
			
			return compressedData;
		}
		
		int currentValue = List.get(0);
		int currentQuantity = 0;
		
		for(int i = 0; i < List.size(); i++)
		{
			if(i == List.size()-1)
			{
				if(currentValue == List.get(i))
				{
					currentQuantity++;
				}
				else
				{
					quantities.add(currentQuantity);
					values.add(currentValue);
					
					currentValue = List.get(i);
					currentQuantity = 1;
				}
				
				quantities.add(currentQuantity);
				values.add(currentValue);
			}
			else if(currentValue == List.get(i))
			{
				currentQuantity++;
			}
			else
			{
				quantities.add(currentQuantity);

				values.add(currentValue);
				
				currentValue = List.get(i);
				currentQuantity = 1;
			}
		}
		
		List<List<Integer>> compressedData = new ArrayList<List<Integer>>();
		compressedData.add(values);
		compressedData.add(quantities);
		
		return compressedData;
	}
    
    public static Object[] compressByteList(List<Byte> List)
   	{
   		List<Integer> quantities = new ArrayList<Integer>();
   		List<Byte> values = new ArrayList<Byte>();
   		
   		if(List.size() == 0)
   		{
   			quantities.add(0);
   			values.add((byte)0);
   			Object[] compressedLists = new Object[2];
   	   		compressedLists[0] = values;
   	   		compressedLists[1] = quantities;
   	   		
   	   		return compressedLists;
   		}
   		
   		int currentValue = List.get(0);
   		int currentQuantity = 0;
   		
   		for(int i = 0; i < List.size(); i++)
   		{
   			if(i == List.size()-1)
   			{
   				if(currentValue == List.get(i))
   				{
   					currentQuantity++;
   				}
   				else
   				{
   					quantities.add(currentQuantity);
   					values.add( (byte) currentValue);
   					
   					currentValue = List.get(i);
   					currentQuantity = 1;
   				}
   				
   				quantities.add(currentQuantity);
   				values.add( (byte) currentValue);
   			}
   			else if(currentValue == List.get(i))
   			{
   				currentQuantity++;
   			}
   			else
   			{
   				quantities.add(currentQuantity);
   				values.add( (byte) currentValue);
   				
   				currentValue = List.get(i);
   				currentQuantity = 1;
   			}
   		}
   		
   		Object[] compressedLists = new Object[2];
   		compressedLists[0] = values;
   		compressedLists[1] = quantities;
   		
   		return compressedLists;
   	}
    
    public static Object[] compressShortList(List<Short> List)
   	{
   		List<Integer> quantities = new ArrayList<Integer>();
   		List<Short> values = new ArrayList<Short>();
   		
   		if(List.size() == 0)
   		{
   			quantities.add(0);
   			values.add((short)0);
   			Object[] compressedLists = new Object[2];
   	   		compressedLists[0] = values;
   	   		compressedLists[1] = quantities;
   	   		
   	   		return compressedLists;
   		}
   		
   		int currentValue = List.get(0);
   		int currentQuantity = 0;
   		
   		for(int i = 0; i < List.size(); i++)
   		{
   			if(i == List.size()-1)
   			{
   				if(currentValue == List.get(i))
   				{
   					currentQuantity++;
   				}
   				else
   				{
   					quantities.add(currentQuantity);
   					values.add( (short) currentValue);
   					
   					currentValue = List.get(i);
   					currentQuantity = 1;
   				}
   				
   				quantities.add(currentQuantity);
   				values.add( (short) currentValue);
   			}
   			else if(currentValue == List.get(i))
   			{
   				currentQuantity++;
   			}
   			else
   			{
   				quantities.add(currentQuantity);
   				values.add( (short) currentValue);
   				
   				currentValue = List.get(i);
   				currentQuantity = 1;
   			}
   		}
   		
   		Object[] compressedLists = new Object[2];
   		compressedLists[0] = values;
   		compressedLists[1] = quantities;
   		
   		return compressedLists;
   	}
    
    public static boolean[][] unpackBooleanArray2D(int byteCount, byte[] data, int Size, int mapSize)
	{
    	List<Integer> values = new ArrayList<Integer>(); 
		for(int i = 0; i < Size; i++)
		{
			int vertexValue = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			values.add(vertexValue);
		}
		List<Integer> quantities = new ArrayList<Integer>();
		for(int i = 0; i < Size; i++)
		{
			int vertexValueQuantity = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			quantities.add(vertexValueQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < values.size(); i++)
		{
			for(int j = 0; j < quantities.get(i); j++)
			{
				flatVoxelArray.add(values.get(i));
			}
		}
		
		boolean[][] Voxels = new boolean[mapSize][mapSize];
		int voxelCount = 0;
		
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				Voxels[i][j] = flatVoxelArray.get(voxelCount) == 1;
				voxelCount++;
			}
		}
		return Voxels;
	}
    
    public static int[][] unpackIntArray2D(int byteCount, byte[] data, int Size, int mapSize)
	{
    	if(Size == 0)
    	{
    		return new int[mapSize][mapSize];
    	}
    	
    	List<Integer> flatArrayValues = new ArrayList<Integer>();
    	List<Integer> flatArrayQuantities = new ArrayList<Integer>();
    	
    	for(int i = 0; i < Size; i++)
		{
			int vertexValue = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			flatArrayValues.add(vertexValue);
		}
		for(int i = 0; i < Size; i++)
		{
			int vertexValueQuantity = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			flatArrayQuantities.add(vertexValueQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < flatArrayValues.size(); i++)
		{
			for(int j = 0; j < flatArrayQuantities.get(i); j++)
			{
				flatVoxelArray.add(flatArrayValues.get(i));
			}
		}
		
		int index = 0;
		int[][] array = new int[mapSize][mapSize];
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < array[0].length; j++)
			{
				array[i][j] = flatVoxelArray.get(index);
				index++;
			}
		}
		return array;
	}
    
    public static int BytesToInt(List<Byte> Data, int Index)
    {
    	int ix = Data.get(Index) & 0xFF | (Data.get(Index+1) & 0xFF) << 8 | (Data.get(Index+2) << 16 | (Data.get(Index+2) & 0xFF) << 24);
    	return ix;
    }
    
    public static int BytesToInt(byte[] Data, int Index)
    {
    	int ix = Data[Index] & 0xFF | (Data[Index+1] & 0xFF) << 8 | (Data[Index+2] & 0xFF) << 16 | (Data[Index+3] & 0xFF) << 24;
    	return ix;
    }
    
    public static float BytesToFloat(byte[] Data, int Index)
    {
    	int ix = Data[Index] & 0xFF | (Data[Index+1] & 0xFF) << 8 | (Data[Index+2] & 0xFF) << 16 | (Data[Index+3] & 0xFF) << 24;
    	float fx = Float.intBitsToFloat(ix);
    	return fx;
    }
    
    public static Vector3f BytesToVector3f(byte[] Data, int Index)
    {
    	int pix = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		int piy = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		int piz = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		
		float px = Float.intBitsToFloat(pix);
		float py = Float.intBitsToFloat(piy);
		float pz = Float.intBitsToFloat(piz);
		
		return new Vector3f(px,py,pz);
    }
    
    public static Quaternion BytesToQuaternion(byte[] Data, int Index)
    {
    	int rix = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		int riy = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		int riz = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		int riw = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
		
		float rx = Float.intBitsToFloat(rix);
		float ry = Float.intBitsToFloat(riy);
		float rz = Float.intBitsToFloat(riz);
		float rw = Float.intBitsToFloat(riw);
		
		return new Quaternion(rx,ry,rz,rw);
    }
    
    public static String BytesToString(byte[] Data, int Index, int DataLength)
    {
    	byte[] stringData = new byte[DataLength];
    	for(int i = 0; i < stringData.length; i++)
    	{
    		stringData[i] = Data[Index++];
    	}
    	String someString = new String(stringData);
    	
    	return someString;
    }
    
    public static List<Integer> GenerateIntList(byte[] Data, int Index, int DataLength)
    {
    	List<Integer> list = new ArrayList<Integer>();
    	for(int i = 0; i < DataLength; i++)
    	{
    		int ix = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
    		list.add(ix);
    	}
    	return list;
    }
    
    public static List<Vector2f> GenerateVector2fList(byte[] Data, int Index, int DataLength)
	{
		List<Vector2f> list = new ArrayList<Vector2f>();
		
		for(int i = 0; i < DataLength; i++)
		{
			list.add(new Vector2f(BytesToFloat(Data,Index),BytesToFloat(Data,Index)));
		}
		return list;
	}
    
    public static List<Vector3f> GenerateVector3fList(byte[] Data, int Index, int DataLength)
	{
		List<Vector3f> list = new ArrayList<Vector3f>();
		
		for(int i = 0; i < DataLength; i++)
		{
			list.add(new Vector3f(BytesToFloat(Data,Index),BytesToFloat(Data,Index),BytesToFloat(Data,Index)));
		}
		return list;
	}
    
	public static List<Byte> IntListToByteList(List<Integer> IntList)
	{
		List<Byte> ByteList = new ArrayList<Byte>();
		for(int i = 0; i < IntList.size(); i++)
		{
			byte[] byteList = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(IntList.get(i)).array();
			ByteList.add(byteList[3]);
			ByteList.add(byteList[2]);
			ByteList.add(byteList[1]);
			ByteList.add(byteList[0]);
		}
		return ByteList;
	}
	
	public static List<Byte> ShortListToByteList(List<Short> ShortList)
	{
		List<Byte> ByteList = new ArrayList<Byte>();
		for(int i = 0; i < ShortList.size(); i++)
		{
			byte[] byteList = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(ShortList.get(i)).array();
			ByteList.add(byteList[1]);
			ByteList.add(byteList[0]);
		}
		return ByteList;
	}

	

	

}
