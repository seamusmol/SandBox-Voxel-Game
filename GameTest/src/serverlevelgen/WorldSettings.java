package serverlevelgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import Util.CompressionUtil;

public class WorldSettings 
{
	private String fileName;
	private int sx = 0;
	private int sy = 0;
	private int rigidBodyIDCount = 0;
	
	public boolean hasRigidBodyVertexCalculation = false;
	public boolean hasRigidBodyPhysics = true;
	
	public int tickTime = 16;
	public int chunkSize;
    public int regionSize = 16;
    public int renderDistance = 3;
    public int resolution = 8;
    public int worldSize;
    public int worldHeight;
    
    public int mountainTolerance = 8;
    
    public int seaLevel = 64;
    public int settlementDistance = 512;
    public int settlementCount = ((worldSize*chunkSize)/settlementDistance - 1)*((worldSize*chunkSize)/settlementDistance - 1) + 1;
    
    public int backgroundTerrainResolution = 8;
	
	public WorldSettings(String MapName, int SX, int SY, int ChunkSize, int RegionSize, int RenderDistance, int BlockSize, int WorldSize, int WorldHeight)
	{
		fileName = MapName;
		sx = SX;
		sy = SY;
		chunkSize = ChunkSize;
		regionSize = RegionSize;
		renderDistance = RenderDistance;
		worldSize = WorldSize;
		worldHeight = WorldHeight;
	}
	
	public WorldSettings(int[] Data)
	{
		sx = Data[0];
		sy = Data[1];
		chunkSize = Data[2];
		regionSize = Data[3];
		renderDistance = Data[4];
		worldSize = Data[5];
		worldHeight = Data[6];
		rigidBodyIDCount = Data[7];
	}
	
	public void incrementRigidBodyCount()
	{
		rigidBodyIDCount++;
	}
	
	public int getRigidBodyIDCount()
	{
		return rigidBodyIDCount;
	}
	
	public void exportSettings()
	{
		if(fileName == null)
		{
			return;
		}
		
		FileOutputStream writeStream = null;
		File tempFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/worldsettings/" + fileName + ".ws");
		try 
		{
			if (!tempFile.exists()) 
			{
				tempFile.createNewFile();
				tempFile.setReadable(true);
				tempFile.setWritable(true);
			}
			writeStream = new FileOutputStream(tempFile, false);
			
			byte[][] exportData = new byte[8][4];
			exportData[0] = CompressionUtil.getIntArray(sx);
			exportData[1] = CompressionUtil.getIntArray(sy);
			exportData[2] = CompressionUtil.getIntArray(chunkSize);
			exportData[3] = CompressionUtil.getIntArray(regionSize);
			exportData[4] = CompressionUtil.getIntArray(renderDistance);
			exportData[5] = CompressionUtil.getIntArray(worldSize);
			exportData[6] = CompressionUtil.getIntArray(worldHeight);
			exportData[7] = CompressionUtil.getIntArray(rigidBodyIDCount);
			
			for(int i = 0; i < exportData.length; i++)
			{
				for(int j = exportData[0].length-1; j >= 0; j--)
				{
					 writeStream.write(exportData[i][j]);
				}
			}
			writeStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int[] importWorldSettings(String FileName)
	{
		byte[] data = null;
		try 
		{
			File newFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/worldsettings/" + FileName + ".ws");
			InputStream inputStream = new FileInputStream(newFile);
			data = new byte[(int) newFile.length()];
			
			inputStream.read(data);
			inputStream.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		int byteCount = 0;
		
		int[] vals = new int[data.length/4];
		for(int i = 0; i < vals.length; i++)
		{
			vals[i] = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		}
		return vals;
	}
	
	public static boolean hasWorlSettings(String FileName)
	{
		File tempFile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/worldsettings/" + FileName + ".ws");
		return tempFile.exists();
	}
	
	
}
