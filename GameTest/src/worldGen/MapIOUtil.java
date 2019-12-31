package worldGen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;

import Util.CompressionUtil;
import Util.MathUtil;
import serverlevelgen.WorldSettings;

public class MapIOUtil {

	public static boolean hasMap(String MapName)
	{
		File tempFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/worldsave/" + MapName);
		return tempFile.exists();
	}

	@SuppressWarnings("unchecked")
	public static void exportMap(IslandMap Map)
	{
		List<Byte> data = new ArrayList<Byte>();
		
		Object[] dataL = CompressionUtil.GenerateByteListBoolean2D(Map.l);
		Object[] dataO = CompressionUtil.GenerateByteListBoolean2D(Map.o);
		Object[] dataLK = CompressionUtil.GenerateByteListBoolean2D(Map.lk);
		Object[] dataR = CompressionUtil.GenerateByteListBoolean2D(Map.r);
		Object[] dataM = CompressionUtil.GenerateByteListBoolean2D(Map.m);
		
		Object[] dataMA = CompressionUtil.GenerateByteListInt2D(Map.ma);
		Object[] dataH = CompressionUtil.GenerateByteListInt2D(Map.h);
		Object[] dataWH = CompressionUtil.GenerateByteListInt2D(Map.wh);
		Object[] dataST = CompressionUtil.GenerateByteListInt2D(Map.st);
		Object[] dataSTR = CompressionUtil.GenerateByteListInt2D(Map.str);
		
		byte[] nameData = Map.mapName.getBytes();
		CompressionUtil.AddValueBytes(nameData.length, data);
		CompressionUtil.AddValueBytes(nameData, data);
		
		CompressionUtil.AddValueBytes(Map.sx, data);
		CompressionUtil.AddValueBytes(Map.sy, data);
		CompressionUtil.AddValueBytes(Map.l.length, data);
		
		CompressionUtil.AddValueBytes((int)dataL[0], data);
		CompressionUtil.AddValueBytes((int)dataO[0], data);
		CompressionUtil.AddValueBytes((int)dataLK[0], data);
		CompressionUtil.AddValueBytes((int)dataR[0], data);
		CompressionUtil.AddValueBytes((int)dataM[0], data);
		
		CompressionUtil.AddValueBytes((int)dataMA[0], data);
		CompressionUtil.AddValueBytes((int)dataH[0], data);
		CompressionUtil.AddValueBytes((int)dataWH[0], data);
		CompressionUtil.AddValueBytes((int)dataST[0], data);
		CompressionUtil.AddValueBytes((int)dataSTR[0], data);
		
		data.addAll( (List<Byte>) dataL[1]);
		data.addAll( (List<Byte>) dataL[2]);
		data.addAll( (List<Byte>) dataO[1]);
		data.addAll( (List<Byte>) dataO[2]);
		data.addAll( (List<Byte>) dataLK[1]);
		data.addAll( (List<Byte>) dataLK[2]);
		data.addAll( (List<Byte>) dataR[1]);
		data.addAll( (List<Byte>) dataR[2]);
		data.addAll( (List<Byte>) dataM[1]);
		data.addAll( (List<Byte>) dataM[2]);
		
		data.addAll( (List<Byte>) dataMA[1]);
		data.addAll( (List<Byte>) dataMA[2]);
		data.addAll( (List<Byte>) dataH[1]);
		data.addAll( (List<Byte>) dataH[2]);
		data.addAll( (List<Byte>) dataWH[1]);
		data.addAll( (List<Byte>) dataWH[2]);
		data.addAll( (List<Byte>) dataST[1]);
		data.addAll( (List<Byte>) dataST[2]);
		data.addAll( (List<Byte>) dataSTR[1]);
		data.addAll( (List<Byte>) dataSTR[2]);
//		data.addAll(modelPosBytes);
//		data.addAll(modelRotBytes);
//		data.addAll(modelIDBytes);
		
		byte[] dataArray =  new byte[data.size()];
		for(int i = 0; i < dataArray.length; i++)
		{
			dataArray[i] = data.get(i);
		}
		writeData(Map.mapName, dataArray);
	}

	private static void writeData(String MapName, byte[] Data)
	{
        FileOutputStream writeStream = null;
        File tempFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/worldsave/" + MapName);
        try 
        {
        	if(!tempFile.exists())
        	{
        		tempFile.createNewFile();
        		tempFile.setReadable(true);
        		tempFile.setWritable(true);
        	}
        	writeStream = new FileOutputStream(tempFile,false);
        	writeStream.write(Data);
        	writeStream.close();
        } 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
	
	//TODO
	//add import for Structure data
	public static IslandMap loadMap(WorldSettings WorldSettings, String MapName)
	{
		byte[] data = readData(MapName);
		
		int byteCount = 0;
		
		int length = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		
		byte[] stringdata = new byte[length];
		for(int i = 0; i < stringdata.length; i++)
		{
			stringdata[i] = data[byteCount++];
		}
		String name = new String(stringdata); 
		
		int sx = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sy = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		
		int mapSize = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		
		int sizeL = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeO = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeLK = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeR = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeM = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		
		int sizeMA = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeH = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeWH = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeST = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		int sizeSTR = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
//		int posSize = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
//		int rotSize = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
//		int idSize = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		
		boolean[][] l = CompressionUtil.unpackBooleanArray2D(byteCount, data, sizeL, mapSize);
		byteCount = byteCount + sizeL*8;
		boolean[][] o = CompressionUtil.unpackBooleanArray2D(byteCount, data, sizeO, mapSize);
		byteCount = byteCount + sizeO*8;
		boolean[][] lk = CompressionUtil.unpackBooleanArray2D(byteCount, data, sizeLK, mapSize);
		byteCount = byteCount + sizeLK*8;
		boolean[][] r = CompressionUtil.unpackBooleanArray2D(byteCount, data, sizeR, mapSize);
		byteCount = byteCount + sizeR*8;
		boolean[][] m = CompressionUtil.unpackBooleanArray2D(byteCount, data, sizeM, mapSize);
		byteCount = byteCount + sizeM*8;
		int[][] ma = CompressionUtil.unpackIntArray2D(byteCount, data, sizeMA, mapSize);
		byteCount = byteCount + sizeMA*8;
		int[][] h = CompressionUtil.unpackIntArray2D(byteCount, data, sizeH, mapSize);
		byteCount = byteCount + sizeH*8;
		int[][] wh = CompressionUtil.unpackIntArray2D(byteCount, data, sizeWH, mapSize);
		byteCount = byteCount + sizeWH*8;
		int[][] st = CompressionUtil.unpackIntArray2D(byteCount, data, sizeST, mapSize);
		byteCount = byteCount + sizeST*8;
		int[][] str = CompressionUtil.unpackIntArray2D(byteCount, data, sizeSTR, mapSize);
		byteCount = byteCount + sizeSTR*8;
		int[][] nm = MathUtil.generateFibonocciNumbers2D(sx, sx, mapSize, mapSize, 3);
		
//		List<Vector2f> positions = CompressionUtil.GenerateVector2fList(data, byteCount, posSize);
//		
//		List<Integer> rotations = CompressionUtil.GenerateIntList(data, byteCount, rotSize);
//		
//		List<Integer> idList = CompressionUtil.GenerateIntList(data, byteCount, idSize);
		
//		List<String> modelNames = VoxelModels.GetModelNameList(idList);
		IslandMap map = new IslandMap(name, WorldSettings.resolution, sx, sy, l, o, lk, r, m, ma, h, wh, st, str, nm);
		
		return map;
//		return map;
	}
	
	public static byte[] readData(String MapName)
	{
		byte[] data = null;
		try 
		{
			File newFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/worldsave/" + MapName);
			InputStream inputStream = new FileInputStream(newFile);
			data = new byte[(int) newFile.length()];
			
			inputStream.read(data);
			inputStream.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		return data;
	}
	
}
