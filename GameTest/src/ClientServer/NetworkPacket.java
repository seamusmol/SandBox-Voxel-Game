package ClientServer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class NetworkPacket {

	/*
	 * 0: playername
	 * 1: password, packetID
	 * 2: ServerIP, GroupDestination
	 * 3: Message
	 */
	
	private String designation;
	private byte[] data;
	private int[] indentifierData;
	
	public NetworkPacket(String Name, byte[] Data, int[] IndentifierData)
	{	
		designation = Name;
		data = Data;
		indentifierData = IndentifierData;
	}
	
	public NetworkPacket(String Name, byte[] Data, int[] IndentifierData, int...IntVals)
	{	
		designation = Name;
		data = new byte[Data.length + IntVals.length*4];
		for(int i = 0; i < Data.length; i++)
		{
			data[i] = Data[i];
		}
		
		for(int i = 0; i < IntVals.length; i++)
		{
			data[i*4] = (byte) (IntVals[i] >>> 24);
			data[i*4+1] = (byte) (IntVals[i] >>> 16);
			data[i*4+2] = (byte) (IntVals[i] >>> 8);
			data[i*4+3] = (byte) (IntVals[i]);
		}
		
	}
	
	//0 = int
	//1 = long
	//2 = Vector3f
	//3 = Quaternion
	public Object[] castData(int[] CastTypes)
	{
		Object[] vals = new Object[CastTypes.length];
		
		int bc = 0;
		for(int i = 0; i < CastTypes.length; i++)
		{
			switch(CastTypes[i])
			{
				case 0:
					int intVal = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					vals[i] = intVal;
					break;
				case 1:
					long longVal = 
							((data[bc++] & 0xFFL) << 56) |
					        ((data[bc++] & 0xFFL) << 48) |
					        ((data[bc++] & 0xFFL) << 40) |
					        ((data[bc++] & 0xFFL) << 32) |
					        ((data[bc++] & 0xFFL) << 24) |
					        ((data[bc++] & 0xFFL) << 16) |
					        ((data[bc++] & 0xFFL) <<  8) |
					        ((data[bc++] & 0xFFL) <<  0) ;
//					System.out.println(longVal);
//					System.out.println(System.currentTimeMillis());
					vals[i] = longVal;
					break;
				case 2:
					int pix = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int piy = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int piz = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					
					float px = Float.intBitsToFloat(pix);
					float py = Float.intBitsToFloat(piy);
					float pz = Float.intBitsToFloat(piz);
					
					vals[i] = new Vector3f(pix,piy,piz);
					break;
					
				case 3:
					int rix = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int riy = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int riz = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int riw = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					
					float rx = Float.intBitsToFloat(rix);
					float ry = Float.intBitsToFloat(riy);
					float rz = Float.intBitsToFloat(riz);
					float rw = Float.intBitsToFloat(riw);
					
					vals[i] = new Quaternion(rx,ry,rz,rw);
					break;
			}
		}
		return vals;
	}
	
	public Object[] castData(int ByteCount, int[] CastTypes)
	{
		Object[] vals = new Object[CastTypes.length];
		
		int bc = ByteCount;
		for(int i = 0; i < CastTypes.length; i++)
		{
			switch(CastTypes[i])
			{
				case 0:
					int intVal = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					vals[i] = intVal;
					break;
				case 1:
					long longVal = 
							((data[bc++] & 0xFFL) << 56) |
					        ((data[bc++] & 0xFFL) << 48) |
					        ((data[bc++] & 0xFFL) << 40) |
					        ((data[bc++] & 0xFFL) << 32) |
					        ((data[bc++] & 0xFFL) << 24) |
					        ((data[bc++] & 0xFFL) << 16) |
					        ((data[bc++] & 0xFFL) <<  8) |
					        ((data[bc++] & 0xFFL) <<  0) ;
//					System.out.println(longVal);
//					System.out.println(System.currentTimeMillis());
					vals[i] = longVal;
					break;
				case 2:
					int pix = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int piy = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int piz = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					
					float px = Float.intBitsToFloat(pix);
					float py = Float.intBitsToFloat(piy);
					float pz = Float.intBitsToFloat(piz);
					
					vals[i] = new Vector3f(pix,piy,piz);
					break;
					
				case 3:
					int rix = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int riy = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int riz = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					int riw = data[bc++] & 0xFF | (data[bc++] & 0xFF) << 8 | (data[bc++] & 0xFF) << 16 | (data[bc++] & 0xFF) << 24;
					
					float rx = Float.intBitsToFloat(rix);
					float ry = Float.intBitsToFloat(riy);
					float rz = Float.intBitsToFloat(riz);
					float rw = Float.intBitsToFloat(riw);
					
					vals[i] = new Quaternion(rx,ry,rz,rw);
					break;
			}
		}
		return vals;
	}

	public int[] getIndentifierData() {
		return indentifierData;
	}

	public byte[] getData() {
		return data;
	}
	
	public String getDesignation()
	{
		return designation;
	}
	
}
