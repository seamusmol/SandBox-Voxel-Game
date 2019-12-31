package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import VoxelModels.VoxelModel;


public class VoxelIOUtil {

	public static void exportVoxelModel(VoxelModel Model, String FileLocation)
	{
		File exportFile = new File(FileLocation + ".vxl");
		byte[] voxelData = generateCompressedVoxelData(Model);
		FileOutputStream writeStream = null;
        try 
        {
        	if(!exportFile.exists())
        	{
        		exportFile.createNewFile();
        		exportFile.setReadable(true);
        		exportFile.setWritable(true);
        	}
        	writeStream = new FileOutputStream(exportFile,false);
        	writeStream.write(voxelData);
        	writeStream.close();
        } 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
	
	public static byte[] generateCompressedVoxelData(VoxelModel Model)
	{
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		
		short[][][] materials = Model.getMaterials();
		
		for(int i = 0; i < materials.length; i++)
		{
			for(int j = 0; j < materials[0].length; j++)
			{
				for(int k = 0; k < materials[0][0].length; k++)
				{
					flatMaterialArray.add((materials[i][j][k]& 0xFF));
				}
			}
		}
		List<Integer> compressedMaterialValues = CompressionUtil.compress(flatMaterialArray).get(0);
		List<Integer> compressedMaterialQuantities = CompressionUtil.compress(flatMaterialArray).get(1);
		
		List<Byte> data = new ArrayList<Byte>();
		
		CompressionUtil.AddValueBytes(Model.getScale(), data);
		CompressionUtil.AddValueBytes(materials.length, data);
		CompressionUtil.AddValueBytes(materials[0].length, data);
		CompressionUtil.AddValueBytes(materials[0][0].length, data);
		CompressionUtil.AddValueBytes(compressedMaterialValues.size(), data);
		
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
		byte[] compressedVoxelData =  new byte[data.size()];
		for(int i = 0; i < compressedVoxelData.length; i++)
		{
			compressedVoxelData[i] = data.get(i);
		}
		return compressedVoxelData;
	}

	public static Object[] importModelData(File File)
	{
		byte[] compressedData = readData(File);
		if(compressedData == null)
		{
			return null;
		}
		Object[] data = unpackVoxelData(compressedData);
		return data;
	}
	
	public static VoxelModel importModel(File File)
	{
		byte[] compressedData = readData(File);
		if(compressedData == null)
		{
			return null;
		}
		Object[] data = unpackVoxelData(compressedData);
		VoxelModel newModel = new VoxelModel((float)data[0], (int)data[1], (int)data[2], (int)data[3], (boolean[][][])data[4], (short[][][])data[5], File.getAbsolutePath());
		newModel.setName(File.getName().substring(0, File.getName().indexOf('.')));
		return newModel;
	}
	
	public static byte[] readData(File File)
	{
		byte[] data = null;
		try 
		{
			InputStream inputStream = new FileInputStream(File);
			data = new byte[(int) File.length()];
			
			inputStream.read(data);
			inputStream.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		return data;
	}
	
	public static Object[] unpackVoxelData(byte[] Data)
	{
		List<Integer> values = new ArrayList<Integer>();
		List<Integer> quantities = new ArrayList<Integer>();
		
		List<Integer> materialValues = new ArrayList<Integer>();
		List<Integer> materialQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		//scale
		int iscale = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		float scale = Float.intBitsToFloat(iscale);
		
		int width = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int height = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int length = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int cellShapeSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int cellMaterialSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			values.add(vertexValue);
		}
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValueQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			quantities.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			materialValues.add(cellMaterialValue);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			materialQuantities.add(cellMaterialQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < values.size(); i++)
		{
			for(int j = 0; j < quantities.get(i); j++)
			{
				flatVoxelArray.add(values.get(i));
			}
		}
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		for(int i = 0; i < materialValues.size(); i++)
		{
			for(int j = 0; j < materialQuantities.get(i);j++)
			{
				flatMaterialArray.add(materialValues.get(i));
			}
		}
		short[][][] Materials = new short[width][height][length];
		
		int voxelCount = 0;
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
					Materials[i][j][k] = flatMaterialArray.get(voxelCount).shortValue();
					voxelCount++;
				}
			}
		}
		Object[] Values = new Object[5];
		Values[0] = scale;
		Values[1] = Materials.length;
		Values[2] = Materials[0].length;
		Values[3] = Materials[0][0].length;
		Values[4] = Materials;
		return Values;
	}
}
