package serverlevelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import Util.CompressionUtil;
import worldGen.IslandMap;

public class ChunkManagerUtil {

	public static byte[] GenerateBackgroundData(IslandMap Map, WorldSettings WorldSettings)
	{
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		
		for(int i = 0; i < Map.bh.length; i++)
		{
			for(int j = 0; j < Map.bh[0].length; j++)
			{
				flatVoxelArray.add(Map.bh[i][j]);
				flatMaterialArray.add(Map.bm[i][j]);
			}
		}
		
		List<Integer> compressedVoxelsValues = CompressionUtil.compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = CompressionUtil.compress(flatVoxelArray).get(1);
		
		//8 values
		List<Integer> compressedMaterialValues = CompressionUtil.compress(flatMaterialArray).get(0);
		List<Integer> compressedMaterialQuantities = CompressionUtil.compress(flatMaterialArray).get(1);
		
		List<Byte> data = new ArrayList<Byte>();
		
		CompressionUtil.AddValueBytes(WorldSettings.chunkSize, data);
		CompressionUtil.AddValueBytes(WorldSettings.backgroundTerrainResolution, data);
		CompressionUtil.AddValueBytes(compressedVoxelsValues.size(), data);
		CompressionUtil.AddValueBytes(compressedMaterialValues.size(), data);
		CompressionUtil.AddValueBytes(Map.bh.length-1, data);
		CompressionUtil.AddValueBytes(Map.bh[0].length-1, data);
		
		data.addAll(CompressionUtil.IntListToByteList(compressedVoxelsValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedVoxelsQuantities));
		
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
		byte[] compressedChunkData =  new byte[data.size()];
		for(int i = 0; i < compressedChunkData.length; i++)
		{
			compressedChunkData[i] = data.get(i);
		}
    	return compressedChunkData;
	}
	
	public static Object[] unpackBackgroundData(byte[] Data)
	{
		List<Integer> flatVoxelArrayValues = new ArrayList<Integer>();
		List<Integer> flatVoxelArrayQuantities = new ArrayList<Integer>();
		
		List<Integer> flatMaterialArrayValues = new ArrayList<Integer>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		int chunkSize = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int resolution = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int cellShapeSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int cellMaterialSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int chunkWidth = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int chunkHeight = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatVoxelArrayValues.add(vertexValue);
		}
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValueQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatVoxelArrayQuantities.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayValues.add(cellMaterialValue);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		
		for(int i = 0; i < flatVoxelArrayValues.size(); i++)
		{
			for(int j = 0; j < flatVoxelArrayQuantities.get(i); j++)
			{
				flatVoxelArray.add(flatVoxelArrayValues.get(i));
			}
		}
		
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
		int[][] Voxels = new int[chunkWidth+1][chunkHeight+1];
		int[][] Materials = new int[chunkWidth+1][chunkHeight+1];
		
		int voxelCount = 0;
		
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				Voxels[i][j] = flatVoxelArray.get(voxelCount);
				Materials[i][j] = flatMaterialArray.get(voxelCount);
				voxelCount++;
			}
		}
		Object[] Values = new Object[4];
		Values[0] = chunkSize;
		Values[1] = resolution;
		Values[2] = Voxels;
		Values[3] = Materials;
		
		return Values;
	}
	
	public static Object[] unpackBackgroundData(byte[] Data, int Offset)
	{
		List<Integer> flatVoxelArrayValues = new ArrayList<Integer>();
		List<Integer> flatVoxelArrayQuantities = new ArrayList<Integer>();
		
		List<Integer> flatMaterialArrayValues = new ArrayList<Integer>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		int chunkSize = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		int resolution = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		
		int cellShapeSize =  Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		int cellMaterialSize =  Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		
		int chunkWidth = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		int chunkHeight = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValue = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
			flatVoxelArrayValues.add(vertexValue);
		}
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValueQuantity = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
			flatVoxelArrayQuantities.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialValue = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
			flatMaterialArrayValues.add(cellMaterialValue);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		
		for(int i = 0; i < flatVoxelArrayValues.size(); i++)
		{
			for(int j = 0; j < flatVoxelArrayQuantities.get(i); j++)
			{
				flatVoxelArray.add(flatVoxelArrayValues.get(i));
			}
		}
		
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
		int[][] Voxels = new int[chunkWidth+1][chunkHeight+1];
		int[][] Materials = new int[chunkWidth+1][chunkHeight+1];
		
		int voxelCount = 0;
		
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				Voxels[i][j] = flatVoxelArray.get(voxelCount);
				Materials[i][j] = flatMaterialArray.get(voxelCount);
				voxelCount++;
			}
		}
		Object[] Values = new Object[4];
		Values[0] = chunkSize;
		Values[1] = resolution;
		Values[2] = Voxels;
		Values[3] = Materials;
		
		return Values;
	}
 	
	public static byte[] GenerateCompressedChunkData(int ChunkIDX, int ChunkIDZ, short[][][]Materials)
    {
//		List<Byte> flatVoxelArray = new ArrayList<Byte>();
		List<Short> flatMaterialArray = new ArrayList<Short>();
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
//					flatVoxelArray.add((byte) (Voxels[i][j][k] ? 1:0));
					flatMaterialArray.add((Materials[i][j][k]));
				}
			}
		}
		
//		Object[] compressedValues = CompressionUtil.compressByteList(flatVoxelArray);
//		List<Byte> compressedVoxelsValues = (List<Byte>) compressedValues[0];
//		List<Integer> compressedVoxelsQuantities = (List<Integer>) compressedValues[1];
		
		Object[] materialValues = CompressionUtil.compressShortList(flatMaterialArray);
		List<Short> compressedMaterialValues = (List<Short>) materialValues[0];
		List<Integer> compressedMaterialQuantities = (List<Integer>) materialValues[1];
		
		List<Byte> data = new ArrayList<Byte>();
		
		CompressionUtil.AddValueBytes(ChunkIDX, data);
		CompressionUtil.AddValueBytes(ChunkIDZ, data);
		CompressionUtil.AddValueBytes(compressedMaterialValues.size(), data);
		CompressionUtil.AddValueBytes(Materials.length-1, data);
		CompressionUtil.AddValueBytes(Materials[0].length-1, data);
		
		data.addAll( CompressionUtil.ShortListToByteList(compressedMaterialValues));
		data.addAll( CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
		byte[] compressedChunkData =  new byte[data.size()];
		for(int i = 0; i < compressedChunkData.length; i++)
		{
			compressedChunkData[i] = data.get(i);
		}
		
//		System.out.println(data.size());
		
    	return compressedChunkData;
	}
	
	public static Queue<Byte> GenerateCompressedChunkDataList(int ChunkIDX, int ChunkIDZ, short[][][]Materials)
    {
		List<Short> flatMaterialArray = new ArrayList<Short>();
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
					flatMaterialArray.add((Materials[i][j][k]));
				}
			}
		}
		
		Object[] materialValues = CompressionUtil.compressShortList(flatMaterialArray);
		List<Short> compressedMaterialValues = (List<Short>) materialValues[0];
		List<Integer> compressedMaterialQuantities = (List<Integer>) materialValues[1];
		
		List<Byte> data = new ArrayList<Byte>();
		
		CompressionUtil.AddValueBytes(ChunkIDX, data);
		CompressionUtil.AddValueBytes(ChunkIDZ, data);
		CompressionUtil.AddValueBytes(compressedMaterialValues.size(), data);
		CompressionUtil.AddValueBytes(Materials.length-1, data);
		CompressionUtil.AddValueBytes(Materials[0].length-1, data);
		
		data.addAll( CompressionUtil.ShortListToByteList(compressedMaterialValues));
		data.addAll( CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
    	return new ConcurrentLinkedQueue(data);
	}
	
	public static Object[] UnpackVoxelData(byte[] Data)
	{
		List<Short> flatMaterialArrayValues = new ArrayList<Short>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		int IDX = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int IDZ = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int cellMaterialSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int chunkWidth = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int chunkHeight = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			short vertexValueQuantity = (short) (Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8);
			flatMaterialArrayValues.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
		List<Short> flatMaterialArray = new ArrayList<Short>();
		
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
		short[][][] Materials = new short[chunkWidth+1][chunkHeight+1][chunkWidth+1];
		
		int voxelCount = 0;
		
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
					Materials[i][j][k] = flatMaterialArray.get(voxelCount);
					voxelCount++;
				}
			}
		}
		Object[] Values = new Object[7];
		Values[0] = IDX;
		Values[1] = IDZ;
		Values[2] = chunkWidth;
		Values[3] = chunkHeight;
		Values[4] = Materials;
		
		return Values;
	}
	
	public static Object[] UnpackVoxelData(byte[] Data, int Offset)
	{
//		List<Byte> flatVoxelArrayValues = new ArrayList<Byte>();
//		List<Integer> flatVoxelArrayQuantities = new ArrayList<Integer>();
		
		List<Short> flatMaterialArrayValues = new ArrayList<Short>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		int IDX = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		int IDZ = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		
//		int cellShapeSize =  Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		int cellMaterialSize =  Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		
		int chunkWidth = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		int chunkHeight = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
		
//		for(int i = 0; i < cellShapeSize; i++)
//		{
//			flatVoxelArrayValues.add(Data[Offset + (ByteCount++)]);
//		}
//		for(int i = 0; i < cellShapeSize; i++)
//		{
//			int vertexValueQuantity = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
//			flatVoxelArrayQuantities.add(vertexValueQuantity);
//		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			short vertexValueQuantity = (short) (Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8);
			flatMaterialArrayValues.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[Offset + (ByteCount++)] & 0xFF | (Data[Offset + (ByteCount++)] & 0xFF) << 8 | (Data[Offset + (ByteCount++)] & 0xFF) << 16 | (Data[Offset + (ByteCount++)] & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
//		List<Byte> flatVoxelArray = new ArrayList<Byte>();
		List<Short> flatMaterialArray = new ArrayList<Short>();
		
//		for(int i = 0; i < flatVoxelArrayValues.size(); i++)
//		{
//			for(int j = 0; j < flatVoxelArrayQuantities.get(i); j++)
//			{
//				flatVoxelArray.add(flatVoxelArrayValues.get(i));
//			}
//		}
		
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
//		boolean[][][] Voxels = new boolean[chunkWidth+1][chunkHeight+1][chunkWidth+1];
		short[][][] Materials = new short[chunkWidth+1][chunkHeight+1][chunkWidth+1];
		
		int voxelCount = 0;
		
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
//					Voxels[i][j][k] = flatVoxelArray.get(voxelCount) == 1;
					Materials[i][j][k] = flatMaterialArray.get(voxelCount);
					voxelCount++;
				}
			}
		}
		Object[] Values = new Object[7];
		Values[0] = IDX;
		Values[1] = IDZ;
		Values[2] = chunkWidth;
		Values[3] = chunkHeight;
		Values[4] = Materials;
		
		return Values;
	}
	
	public static Object[] UnpackVoxelData(Queue<Byte> Data)
	{
		List<Short> flatMaterialArrayValues = new ArrayList<Short>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int IDX = Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8 | (Data.poll() & 0xFF) << 16 | (Data.poll() & 0xFF) << 24;
		int IDZ = Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8 | (Data.poll() & 0xFF) << 16 | (Data.poll() & 0xFF) << 24;
		
		int cellMaterialSize =  Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8 | (Data.poll() & 0xFF) << 16 | (Data.poll() & 0xFF) << 24;
		
		int chunkWidth = Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8 | (Data.poll() & 0xFF) << 16 | (Data.poll() & 0xFF) << 24;
		int chunkHeight = Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8 | (Data.poll() & 0xFF) << 16 | (Data.poll() & 0xFF) << 24;
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			short vertexValueQuantity = (short) (Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8);
			flatMaterialArrayValues.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data.poll() & 0xFF | (Data.poll() & 0xFF) << 8 | (Data.poll() & 0xFF) << 16 | (Data.poll() & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
		List<Short> flatMaterialArray = new ArrayList<Short>();
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
		short[][][] Materials = new short[chunkWidth+1][chunkHeight+1][chunkWidth+1];
		
		int voxelCount = 0;
		
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
					Materials[i][j][k] = flatMaterialArray.get(voxelCount);
					voxelCount++;
				}
			}
		}
		Object[] Values = new Object[7];
		Values[0] = IDX;
		Values[1] = IDZ;
		Values[2] = chunkWidth;
		Values[3] = chunkHeight;
		Values[4] = Materials;
		
		return Values;
	}
}
