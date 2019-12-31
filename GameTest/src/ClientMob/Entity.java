package ClientMob;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;
import Util.CompressionUtil;

public class Entity {

	Vector3f lastPosition;
	Quaternion lastRotation;
	
	Vector3f position;
	Quaternion rotation;
	
	String name;
	int id; 
	Geometry someGeometry;
	boolean needsUpdate = true;
	private static String modelName;
	
	public Entity(byte[] Data, int ByteCount)
	{
		position = new Vector3f(CompressionUtil.BytesToFloat(Data, ByteCount),CompressionUtil.BytesToFloat(Data, ByteCount),CompressionUtil.BytesToFloat(Data, ByteCount));
		rotation = new Quaternion(CompressionUtil.BytesToFloat(Data, ByteCount),CompressionUtil.BytesToFloat(Data, ByteCount),CompressionUtil.BytesToFloat(Data, ByteCount),CompressionUtil.BytesToFloat(Data, ByteCount));
		
		lastPosition = position;
		lastRotation = rotation;
		
		int NameLength = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		name = CompressionUtil.BytesToString(Data, ByteCount, NameLength);
		
		id = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		modelName = "Entity" + (Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24);
		createGeometry();
	}
	
	public void update()
	{
		needsUpdate = true;
		if(someGeometry == null)
		{
			createGeometry();
		}
		else
		{
			someGeometry.setLocalTranslation(position);
			someGeometry.setLocalRotation(rotation);
		}
	}
	
	public static Object[] createBuffers()
    {
		Object[] modelData = AssetLoaderManager.getModelData(modelName);
		
		List<Vector3f> vectorList = (List<Vector3f>) modelData[0];
		List<Vector2f> texCoordList = (List<Vector2f>) modelData[1];
		List<Integer> indexList = (List<Integer>) modelData[2];
		
		Vector3f[] positionArray = new Vector3f[vectorList.size()];
		Vector2f[] texcoordArray = new Vector2f[texCoordList.size()];
		
		for(int i = 0; i < vectorList.size(); i++)
		{
			positionArray[i] = vectorList.get(i);
		}
		for(int i = 0; i < texCoordList.size(); i++)
		{
			texcoordArray[i] = texCoordList.get(i);
		}
		
		int[] indexBufferArray = new int[vectorList.size()];
		for(int i = 0; i < vectorList.size(); i++)
		{
			indexBufferArray[i] = indexList.get(i);
		}
		
		Object[] buffers = new Object[3];
		
		buffers[0] = BufferUtils.createFloatBuffer(positionArray);
		buffers[1] = BufferUtils.createFloatBuffer(texcoordArray);
		buffers[2] = BufferUtils.createIntBuffer(indexBufferArray);
    	
    	return buffers;
    }
	
	public void createGeometry()
	{
		Object[] buffers = createBuffers();
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		someGeometry = new Geometry("Entity: " + name + id, someMesh);
		
		//client solid
		someMesh.setBuffer(Type.Position, 3, (FloatBuffer) buffers[0]);
		someMesh.setBuffer(Type.TexCoord, 2, (FloatBuffer) buffers[1]);
		someMesh.setBuffer(Type.Index,    3, (IntBuffer)buffers[2]);
		someMesh.updateBound();
		someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(modelName));
		
		//someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.NearestNoMipMaps);
		//someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Nearest);
		
		someGeometry.setMaterial(someMaterial);
		someGeometry.setLocalTranslation(position);
	}
	
	public void modifyEntity(byte[] Data, int ByteCount)
	{
		needsUpdate = true;
		float rx = CompressionUtil.BytesToFloat(Data, ByteCount);
		float ry = CompressionUtil.BytesToFloat(Data, ByteCount);
		float rz = CompressionUtil.BytesToFloat(Data, ByteCount);
		float rw = CompressionUtil.BytesToFloat(Data, ByteCount);
		
		float px = CompressionUtil.BytesToFloat(Data, ByteCount);
		float py = CompressionUtil.BytesToFloat(Data, ByteCount);
		float pz = CompressionUtil.BytesToFloat(Data, ByteCount);
		
		lastPosition = position;
		lastRotation = rotation;
		
		position.set((lastPosition.x + px)/2, (lastPosition.y + py)/2, (lastPosition.z + pz)/2);
		rotation.set((lastRotation.getX() + rx)/2, (lastRotation.getY() + ry)/2, (lastRotation.getZ()+ rz)/2,(lastRotation.getW() + rw)/2);
		
	}
	
	
}
