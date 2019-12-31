package ServerRigidBodies;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import Util.CompressionUtil;

public class RigidBodyCompressionUtil {

	public static List<Byte> GenerateCompressedRigidBodyData(ServerRigidBody RigidBody)
    {
		short[][][] Materials = RigidBody.materials;
		
		List<Short> flatMaterialArray = new ArrayList<Short>();
		for(int i = 0; i < Materials.length; i++)
		{
			for(int j = 0; j < Materials[0].length; j++)
			{
				for(int k = 0; k < Materials[0][0].length; k++)
				{
					flatMaterialArray.add(Materials[i][j][k]);
				}
			}
		}
		
		Object[] materialValues = CompressionUtil.compressShortList(flatMaterialArray);
		List<Short> compressedMaterialValues = (List<Short>) materialValues[0];
		List<Integer> compressedMaterialQuantities = (List<Integer>) materialValues[1];
		
		List<Byte> data = new ArrayList<Byte>();
		
		CompressionUtil.AddValueBytes(RigidBody.getID(), data);
		CompressionUtil.AddValueBytes(RigidBody.getVoxelScale(), data);
		CompressionUtil.AddValueBytes(RigidBody.getPosition().getX(), data);
		CompressionUtil.AddValueBytes(RigidBody.getPosition().getY(), data);
		CompressionUtil.AddValueBytes(RigidBody.getPosition().getZ(), data);
		CompressionUtil.AddValueBytes(RigidBody.getRotation().getX(), data);
		CompressionUtil.AddValueBytes(RigidBody.getRotation().getY(), data);
		CompressionUtil.AddValueBytes(RigidBody.getRotation().getZ(), data);
		CompressionUtil.AddValueBytes(RigidBody.getRotation().getW(), data);
		CompressionUtil.AddValueBytes(Materials.length-1, data);
		CompressionUtil.AddValueBytes(Materials[0].length-1, data);
		CompressionUtil.AddValueBytes(Materials[0][0].length-1, data);
		CompressionUtil.AddValueBytes(compressedMaterialValues.size(), data);
		CompressionUtil.AddValueBytes(RigidBody.isUpright(), data);
		
		data.addAll( CompressionUtil.ShortListToByteList(compressedMaterialValues));
		data.addAll( CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
    	return data;
	}
	
	public static Object[] UnpackRigidBodyData(byte[] Data, int Offset)
	{
		List<Short> flatMaterialArrayValues = new ArrayList<Short>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int byteCount = 0;
		
		//rigidBody ID
		int id = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		//scale
		int iscale = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		float scale = Float.intBitsToFloat(iscale);
		
		//position
		int pix = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int piy = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int piz = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		
		float px = Float.intBitsToFloat(pix);
		float py = Float.intBitsToFloat(piy);
		float pz = Float.intBitsToFloat(piz);
		
		//rotation
		int rix = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int riy = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int riz = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int riw = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		
		float rx = Float.intBitsToFloat(rix);
		float ry = Float.intBitsToFloat(riy);
		float rz = Float.intBitsToFloat(riz);
		float rw = Float.intBitsToFloat(riw);
		
		int rigidBodyWidth = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int rigidBodyHeight = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		int rigidBodyLength = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		
		int cellMaterialSize =  Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
		
		boolean isUpright = (Data[Offset + (byteCount++)] & 0xFF) == 1;
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			short vertexValueQuantity = (short) (Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8);
			flatMaterialArrayValues.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[Offset + (byteCount++)] & 0xFF | (Data[Offset + (byteCount++)] & 0xFF) << 8 | (Data[Offset + (byteCount++)] & 0xFF) << 16 | (Data[Offset + (byteCount++)] & 0xFF) << 24;
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
		
		short[][][] Materials = new short[rigidBodyWidth+1][rigidBodyHeight+1][rigidBodyLength+1];
		
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
		Object[] Values = new Object[10];
		Values[0] = scale;
		Values[1] = id;
		Values[2] = new Vector3f(px,py,pz);
		Values[3] = new Quaternion(rx,ry,rz,rw);
		Values[4] = rigidBodyWidth;
		Values[5] = rigidBodyHeight;
		Values[6] = rigidBodyLength;
		Values[7] = Materials;
		Values[8] = byteCount;
		Values[9] = isUpright;
		
		return Values;
	}
	
}
