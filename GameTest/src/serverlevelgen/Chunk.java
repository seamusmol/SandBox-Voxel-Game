package serverlevelgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.Control;

import Configs.ServerSettings;
import Configs.SettingsLibrary;
import Main.AssetLoaderManager;
import Util.MathUtil;
import Util.VoxelUtil;
import worldGen.Cell;

public class Chunk {

	short[][][] materials;

	public List<Cell> ChunkCellList = new ArrayList<Cell>();
	// public List<Cell> TransparentCellList = new ArrayList<Cell>();

	public Geometry Model;
	Control control;
	
	public boolean hasProcessed = false;

	public int chunkIDX;
	public int chunkIDZ;

	public Chunk(int ChunkIDX, int ChunkIDZ, short[][][] Materials) 
	{
		chunkIDX = ChunkIDX;
		chunkIDZ = ChunkIDZ;
		materials = Materials;
	}
	
	public void processChunk(int ChunkSize)
	{
		// generate Cell List
		hasProcessed = true;
		Object[] CellLists = VoxelUtil.GenerateVoxelCellList(materials, true);
		ChunkCellList = (List<Cell>) CellLists[0];

		if(ChunkCellList.size() > 0)
		{
			// Cell list to buffer lists
			Object[] data = VoxelUtil.createCellData(ChunkCellList);
			// buffer lists to buffers
			Object[] buffers = VoxelUtil.createBuffers(data);
			// buffers to geometries
			
			Model = VoxelUtil.GenerateGeometry(ChunkSize, chunkIDX, chunkIDZ, (FloatBuffer) buffers[0],
					(FloatBuffer) buffers[1], (IntBuffer) buffers[2], (FloatBuffer) buffers[3], 0, "water");
			Model.setLocalTranslation(chunkIDX * ChunkSize, 0, chunkIDZ * ChunkSize);
		
			control = new RigidBodyControl(0.0f);
			Model.addControl(control);
			
			ChunkCellList = null;
		}
	}

	//TODO
	//replace waterMat with array
//	public int calculateDepth(int X, int Y, int Z, int...PassThroughMaterials)
//	{
//		short waterMat = (short)PassThroughMaterials[0];
//		int topSpot = Y;
//		for(int j = Y+1; j < materials[0].length; j++)
//		{
//			if( materials[X][j][Z] != 1023)
//			{
//				break;
//			}
//			topSpot = j;
//		}
//		return topSpot - Y;
//	}
	
	public float calculateDepth(Vector3f Position, int... PassThroughMaterials)
	{
		int X = (int)Position.x;
		int Y = (int)Position.y;
		int Z = (int)Position.z;
		
		short waterMat = (short)PassThroughMaterials[0];
		int topSpot = Y;
		for(int j = Y+1; j < materials[0].length; j++)
		{
			topSpot = j;
			if( materials[X][j][Z] != 1023)
			{
				break;
			}
		}
		
		return topSpot-Position.getY();
	}
	
	public float getMaxHeight(int x, int z)
	{
		if (x < 0 || x >= materials.length || z < 0 || z >= materials[0][0].length)
		{
			return 0;
		}
		for(int i = materials[0].length-1; i >= 0; i--)
		{
			if(materials[x][i][z] != 0)
			{
				return i;
			}
		}
		return 0;
	}
	
	public boolean[] getVoxelValue(int x, int y, int z) 
	{
		return VoxelUtil.getVoxelValue(materials, x, y, z);
	}

	public short getVertexMaterial(int x, int y, int z)
	{
		if (x < 0 || x >= materials.length || y < 0 || y >= materials[0].length || z < 0 || z >= materials[0][0].length)
		{
			return 0;
		}
		
		return materials[x][y][z];
	}
	
	public boolean GetVertexValue(int x, int y, int z) {
		return materials[x][y][z] == 0;
	}

	public void SetVertexMaterial(int x, int y, int z, int material) {
		if (y < 0 || y >= materials[0].length) {
			return;
		}
		materials[x][y][z] = (short)material;
	}

	public boolean hasVoxel(int x, int y, int z) {
		if (y < 0 || y >= materials[0].length) {
			return false;
		}
		return materials[x][y][z] != 0;
	}

	public int GetVertexMaterial(int x, int y, int z) {
		return materials[x][y][z];
	}

	public boolean modifyChunk(Vector3f CellPosition, int DestructionType, int Material) {
		int px = (int) CellPosition.x;
		int py = (int) CellPosition.y;
		int pz = (int) CellPosition.z;

		boolean HasChanged = false;

		switch (DestructionType) {
		case 0:

			if (hasVoxel(px, py, pz)) {
				SetVertexMaterial(px, py, pz, 0);
				HasChanged = true;
			}
			// call updates

			break;
		case 1:

			if (!hasVoxel(px, py, pz)) {
				SetVertexMaterial(px, py, pz, Material);
				HasChanged = true;
			}
			break;
		}
		if (HasChanged) {
			hasProcessed = false;
		}
		return HasChanged;
	}

	public List<int[]> createLookUpList(String FileName) {
		List<int[]> lookupList = new ArrayList<int[]>();

		BufferedReader bufferedReader = null;
		String nextLine = null;
		try {
			File file = new File(FileName);

			bufferedReader = new BufferedReader(new FileReader(FileName));
			for (int i = 0; i < 256; i++) {
				nextLine = bufferedReader.readLine();
				if (nextLine != null && !nextLine.equals("")) {
					String[] indexes = nextLine.split(",");
					int[] indexTable = new int[indexes.length];

					for (int j = 0; j < indexes.length; j++) {
						indexTable[j] = Integer.parseInt(indexes[j]);
					}
					lookupList.add(indexTable);
				} else {
					int[] indexTable = new int[3];
					indexTable[0] = 0;
					indexTable[1] = 0;
					indexTable[2] = 0;
					lookupList.add(indexTable);
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return lookupList;
	}

	public boolean loadChunk() {
		return false;
	}

	// TODO
	// store 8 booleans in byte
	public byte[] byteArray3DToArray(boolean[][][] Data) {
		byte[] array = new byte[Data.length * Data[0].length * Data[0][0].length];

		int bytecount = 0;
		for (int i = 0; i < Data.length; i++) {
			for (int j = 0; j < Data.length; j++) {
				for (int k = 0; k < Data.length; k++) {
					array[bytecount++] = (byte) (Data[i][j][k] ? 1 : 0);
				}
			}
		}
		return array;
	}

	public void close() {
	}

	public int getChunkIDX() {
		return chunkIDX;
	}

	public int getChunkIDZ() {
		return chunkIDZ;
	}

}
