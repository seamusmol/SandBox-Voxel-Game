package levelgen;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import Configs.SettingsLibrary;
import Util.CompressionUtil;
import Util.VoxelUtil;
import serverlevelgen.ChunkManagerUtil;
import worldGen.Cell;

/*
 * TODO 
 * Add packet voxel density(and worlsize?)
 * 
 * 
 */
public class Chunk {
	
	public int chunkIDX;
	public int chunkIDZ;
	public int chunkSize;
	public int chunkHeight;
	public int chunkLength;
	private short[][][] materials;
	
	Geometry ChunkGeometry;
	Geometry TransparentChunkGeometry;
	
	boolean needsUpdate = true;
	
	public Chunk(byte[] Data)
	{
		Object[] Values = ChunkManagerUtil.UnpackVoxelData(Data);
		chunkIDX = (int) Values[0];
		chunkIDZ = (int) Values[1];
		chunkSize = (int) Values[2];
		chunkHeight = (int) Values[3];
		materials = (short[][][]) Values[4];
	}
	
	public void updateData(byte[] Data)
	{
		Object[] Values = ChunkManagerUtil.UnpackVoxelData(Data);
		chunkIDX = (int) Values[0];
		chunkIDZ = (int) Values[1];
		chunkSize = (int) Values[2];
		chunkHeight = (int) Values[3];
		materials = (short[][][]) Values[4];
		
		needsUpdate = true;
	}
	
	public void processChunk()
	{
		needsUpdate = false;
		Object[] CellLists = VoxelUtil.GenerateVoxelCellList(materials, true);
		List<Cell> CellList = (List<Cell>)CellLists[0];
		List<Cell> TransCellList = (List<Cell>)CellLists[1];
		
		if(CellList.size() > 0)
		{
			//create vector list
			Object[] data = VoxelUtil.createCellData(CellList);
			//create buffers
			Object[] buffers = VoxelUtil.createBuffers(data);
			//create Geometry
			ChunkGeometry = VoxelUtil.GenerateGeometry(chunkSize, chunkIDX, chunkIDZ, (FloatBuffer)buffers[0], (FloatBuffer)buffers[1], (IntBuffer)buffers[2], (FloatBuffer)buffers[3],1,"test");
			
		}
		if(TransCellList.size() > 0)
		{
			//create vector list
			Object[] transData = VoxelUtil.createCellData(TransCellList);
			//create buffers
			Object[] transBuffers = VoxelUtil.createBuffers(transData);
			//create Geometry
			TransparentChunkGeometry = VoxelUtil.GenerateGeometry(chunkSize, chunkIDX, chunkIDZ, (FloatBuffer)transBuffers[0], (FloatBuffer)transBuffers[1], (IntBuffer)transBuffers[2],(FloatBuffer)transBuffers[3], 2, "test");
		}
		
		CellLists = null;
		CellList = null;
		TransCellList = null;
	}
	
	public boolean HasGeometry()
	{
		return ChunkGeometry != null;
	}
	
	public boolean HasTransparentGeometry()
	{
		return TransparentChunkGeometry != null;
	}
	
	public Geometry GetChunkGeometry()
	{
		return ChunkGeometry;
	}
	
	public Geometry GetTransparentChunkGeometry()
	{
		return TransparentChunkGeometry;
	}
	
	public int getChunkIDX() {
		return chunkIDX;
	}

	public int getChunkIDZ() {
		return chunkIDZ;
	}
	
	public boolean getVoxel(int x, int y, int z)
	{
		return materials[x][y][z] != 0;
	}
	
	public boolean[] getVoxelValue(int x, int y, int z)
    {
        return VoxelUtil.getVoxelValue(materials, x, y, z);
    }
}


