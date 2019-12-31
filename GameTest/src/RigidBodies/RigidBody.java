package RigidBodies;

import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import Util.MathUtil;
import Util.VoxelUtil;
import worldGen.Cell;

public class RigidBody {

	private int id;
	private float voxelScale = 1.0f;
	private int sx;
	private int sy;
	private int sz;
	private short[][][] materials;
	
	private Vector3f position;
	Quaternion rotation;
	
	boolean needsCreationUpdate = true;
	boolean needsMovementUpdate = true;
	
	public RigidBody(Object[] Values)
	{
		voxelScale = 1.0f;
		
		id = (int)Values[1];
		position = (Vector3f) Values[2];
		rotation = (Quaternion) Values[3];
		sx = (int)Values[4];
		sy = (int)Values[5];
		sz = (int)Values[6];
		materials = (short[][][]) Values[7];
	}
	
	public void modify(Object[] Values)
	{
		voxelScale = 1.0f;
		id = (int)Values[1];
		position = (Vector3f) Values[2];
		rotation = (Quaternion) Values[3];
		sx = (int)Values[4];
		sy = (int)Values[5];
		sz = (int)Values[6];
		materials = (short[][][]) Values[7];
		
		needsCreationUpdate = true;
		needsMovementUpdate = true;
	}
	
	public void updateData(byte[] Data)
	{
		int byteCount = 0;
		//position
		int pix = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int piy = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int piz = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		float px = Float.intBitsToFloat(pix);
		float py = Float.intBitsToFloat(piy);
		float pz = Float.intBitsToFloat(piz);
		position.set(px, py, pz);
		
		//rotation
		int rix = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int riy = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int riz = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int riw = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		float rx = Float.intBitsToFloat(rix);
		float ry = Float.intBitsToFloat(riy);
		float rz = Float.intBitsToFloat(riz);
		float rw = Float.intBitsToFloat(riw);
		rotation.set(rx, ry, rz, rw);
		needsMovementUpdate = true;
		
	}
	
	public void updateData(Vector3f UpdatePosition, Quaternion UpdateRotation)
	{
		position.set(UpdatePosition);
		rotation.set(UpdateRotation);
		needsMovementUpdate = true;
	}

	public Float getDistance(Vector3f PlayerPosition)
	{
		return MathUtil.getMaxDist(position, PlayerPosition);
	}
	
	public Object[] processCreation()
	{
		needsCreationUpdate = false;
		needsMovementUpdate = false;
		
		Object[] CellLists = VoxelUtil.GenerateVoxelCellList(materials, true);
		List<Cell> CellList = (List<Cell>)CellLists[0];
		List<Cell> TransCellList = (List<Cell>)CellLists[1];
		
		Object[] normalData = VoxelUtil.createCellData(CellList, new Vector3f(materials.length, materials[0].length, materials[0][0].length).mult(0.5f).negate());
		Object[] transData = VoxelUtil.createCellData(TransCellList, new Vector3f(materials.length, materials[0].length, materials[0][0].length).mult(0.5f).negate());
		
		Object[] data = new Object[]{normalData[0], normalData[1], transData[0], transData[1]};
		
		return data;
	}
	
	public boolean hasVoxel(int px, int py, int pz)
	{
		if(px < 0 || px > sx || py < 0 || py > sy || pz < 0 || pz > sz)
		{
			return false;
		}
		return materials[px][py][pz] != 0;
	}
	
	public Vector3f getStartVertex()
	{
		return position;
	}
	
	public Vector3f getCenter()
	{
		return position.add( new Vector3f(sx,sy,sz).mult(voxelScale));
	}
	
	public Vector3f getBoundingDistance()
	{
		return new Vector3f(sx,sy,sz);
	}
	
	public float getVoxelScale() {
		return voxelScale;
	}

	public short[][][] getMaterials() {
		return materials;
	}

	public boolean[] getVoxelValue(int x, int y, int z)
    {
        return VoxelUtil.getVoxelValue(materials, x, y, z);
    }

	public Vector3f getPosition() {
		return position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public Integer getID() {
		return id;
	}
	
	
	
}
