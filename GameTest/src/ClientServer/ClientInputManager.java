package ClientServer;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import ClientDataManager.ClientDataManager;
import Input.InputHandler;

public class ClientInputManager {

	ClientDataManager clientData;
	
	public ClientInputManager(ClientDataManager ClientData)
	{
		clientData = ClientData;
	}
	
	public void processInput(NetworkPacket Pack, String ClientName)
	{
		//# of keys
		//string length
		//string name
		//string value
		List<String> keys = new ArrayList<String>();
		List<Float> keyValues = new ArrayList<Float>();
		generateKeys(Pack, keys, keyValues);
		
		for(int i = 0; i < keys.size(); i++)
		{
			switch(keys.get(i))
			{
				case "mouse_left":
					rotateCamera(ClientName, keyValues.get(i), clientData.getPlayerRotation(ClientName), true);
					break;
				case "mouse_right":
					rotateCamera(ClientName, InputHandler.getKeyValue("mouse_left"), clientData.getPlayerRotation(ClientName), true);
					break;
				case "mouse_up":
					rotateCamera(ClientName, -InputHandler.getKeyValue("mouse_up"), clientData.getPlayerRotation(ClientName), false);
					break;
				case "mouse_down":
					rotateCamera(ClientName, -InputHandler.getKeyValue("mouse_up"), clientData.getPlayerRotation(ClientName), false);
					break;
			}
		}
	}
	
	public void rotateCamera(String Name, float value, Quaternion Rotation, boolean IsUp)
	{
		Matrix3f mat = new Matrix3f();
		
		if(IsUp)
		{
			mat.fromAngleNormalAxis(5 * value, Rotation.getRotationColumn(0));
		}
		else
		{
			mat.fromAngleNormalAxis(5 * value, new Vector3f());
		}
        
        Vector3f up = Rotation.getRotationColumn(1);
        Vector3f left = Rotation.getRotationColumn(0);
        Vector3f dir = Rotation.getRotationColumn(2);

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion rot = new Quaternion();
        rot = rot.fromAxes(left, up, dir);
        rot = rot.normalizeLocal();
        
        clientData.getClientByName(Name).setRotation(rot);
	}
	
	public void generateKeys(NetworkPacket Pack, List<String> Keys, List<Float> KeyValues)
	{
		int byteCount = 0;
		byte[] data = Pack.getData();
		int keyCount = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
		for(int i = 0; i < keyCount; i++)
		{
			int stringLength = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			byte[] stringData = new byte[stringLength];
			for(int j = 0; j < stringData.length; j++)
			{
				stringData[j] = data[byteCount++];
			}
			Keys.add(new String(stringData));
			KeyValues.add((float)(data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24));
		}
	}
	
}
