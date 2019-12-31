package RigidBodies;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.util.BufferUtils;

import ClientServer.NetworkPacket;
import Configs.SettingsLibrary;
import Input.PlayerCamera;
import Main.AssetLoaderManager;
import Mob.PlayerSettings;
import ServerRigidBodies.RigidBodyCompressionUtil;
import Util.CompressionUtil;
import Util.VoxelUtil;

public class RigidBodyManager {

	ExecutorService executor = Executors.newSingleThreadExecutor();
	RigidBodyUpdateThread updateThread;

	ExecutorService processExecutor = Executors.newCachedThreadPool();
	ExecutorService fillExecutor = Executors.newCachedThreadPool();

	List<RigidBody> rigidBodies = new ArrayList<RigidBody>();

	PlayerCamera playerCamera;
	PlayerSettings playerSettings;
	
	FloatBuffer vertexBuffer;
	FloatBuffer texcoordBuffer;
	FloatBuffer instancePosBuffer1;
	FloatBuffer instancePosBuffer2;
	IntBuffer indexBuffer;
	FloatBuffer normalBuffer;

	FloatBuffer transVertexBuffer;
	FloatBuffer transTexcoordBuffer;
	FloatBuffer transInstancePosBuffer1;
	FloatBuffer transInstancePosBuffer2;
	IntBuffer transIndexBuffer;
	FloatBuffer transNormalBuffer;

	List<List<Vector3f>> transVertexMap = new ArrayList<List<Vector3f>>();
	List<List<Vector2f>> transTexcoordMap = new ArrayList<List<Vector2f>>();
	List<List<Vector3f>> transNormalMap = new ArrayList<List<Vector3f>>();

	List<List<Vector3f>> vertexMap = new ArrayList<List<Vector3f>>();
	List<List<Vector2f>> texcoordMap = new ArrayList<List<Vector2f>>();
	List<List<Vector3f>> normalMap = new ArrayList<List<Vector3f>>();

	Geometry instanceGeom;
	Geometry transInstanceGeom;

	boolean hasUpdate = false;
	boolean waitMovement = false;
	boolean hasMovementUpdate = false;

	public RigidBodyManager(PlayerSettings PlayerSettings, PlayerCamera PlayerCamera)
	{
		vertexBuffer = BufferUtils.createFloatBuffer(0);
		texcoordBuffer = BufferUtils.createFloatBuffer(0);
		instancePosBuffer1 = BufferUtils.createFloatBuffer(0);
		instancePosBuffer2 = BufferUtils.createFloatBuffer(0);
		indexBuffer = BufferUtils.createIntBuffer(0);
		normalBuffer = BufferUtils.createFloatBuffer(0);
		
		transVertexBuffer = BufferUtils.createFloatBuffer(0);
		transTexcoordBuffer = BufferUtils.createFloatBuffer(0);
		transInstancePosBuffer1 = BufferUtils.createFloatBuffer(0);
		transInstancePosBuffer2 = BufferUtils.createFloatBuffer(0);
		transIndexBuffer = BufferUtils.createIntBuffer(0);
		transNormalBuffer = BufferUtils.createFloatBuffer(0);
		
		playerSettings = PlayerSettings;
		
		playerCamera = PlayerCamera;
		playerCamera.attachNode(new Node("RigidBodyNode"));
		playerCamera.attachNode(new Node("TransparentRigidBodyNode"));

		instanceGeom = generateRigidBodyGeometry("ClientRigidBodyInstance", vertexBuffer, texcoordBuffer, indexBuffer, normalBuffer, "test");
		transInstanceGeom = generateRigidBodyTransGeometry("ClientRigidBodyTransInstance", transVertexBuffer, transTexcoordBuffer, transIndexBuffer, transNormalBuffer, "test");

		updateThread = new RigidBodyUpdateThread();
		
		executor.execute(updateThread);
	}

	public void close()
	{
		updateThread.isRunning = false;
		executor.shutdown();
	}
	
	
	private class RigidBodyUpdateThread implements Runnable 
	{
		boolean isRunning = true;

		public RigidBodyUpdateThread() {
		}

		@Override
		public void run() 
		{
			Thread.currentThread().setName("Util-ClientRigidBody");
			while (isRunning) 
			{
				long startTime = System.currentTimeMillis();
				removeRigidBodies();
				generateRigidBodyMeshes();
				if (!waitMovement) 
				{
					if(hasMovementUpdate)
					{
						updateMaterial();
					}
				} 
				else 
				{
					if (playerCamera.hasGeometry("RigidBodyNode", "ClientRigidBodyInstance")) 
					{
						waitMovement = false;
					}
				}
				
				long frameTime = System.currentTimeMillis() - startTime;
				if (frameTime < 0) 
				{
					frameTime = 0;
				} 
				else if (frameTime > SettingsLibrary.updateTime - frameTime) 
				{
					frameTime = (long) (SettingsLibrary.updateTime - frameTime);
				}

				try {
					Thread.sleep((long) (SettingsLibrary.updateTime - frameTime));
				} 
				catch (InterruptedException e) {

				}
			}
		}
	}
	

	private void generateRigidBodyMeshes() 
	{
		int updateCount = 0;
		for (int i = 0; i < rigidBodies.size(); i++) 
		{
			if (rigidBodies.get(i).needsCreationUpdate) 
			{
				Object[] data = rigidBodies.get(i).processCreation();
				if (vertexMap.size() > i) 
				{
					vertexMap.set(i, (List<Vector3f>) data[0]);
					texcoordMap.set(i, (List<Vector2f>) data[1]);
					normalMap.set(i, VoxelUtil.generateNormalList((List<Vector3f>) data[0]));

					transVertexMap.set(i, (List<Vector3f>) data[2]);
					transTexcoordMap.set(i, (List<Vector2f>) data[3]);
					transNormalMap.set(i, VoxelUtil.generateNormalList((List<Vector3f>) data[2]));
				} 
				else 
				{
					vertexMap.add((List<Vector3f>) data[0]);
					texcoordMap.add((List<Vector2f>) data[1]);
					normalMap.add(VoxelUtil.generateNormalList((List<Vector3f>) data[0]));

					transVertexMap.add((List<Vector3f>) data[2]);
					transTexcoordMap.add((List<Vector2f>) data[3]);
					transNormalMap.add(VoxelUtil.generateNormalList((List<Vector3f>) data[2]));
				}
				updateCount++;
			}
		}

		if (updateCount > 0 || hasUpdate) 
		{
			sortRigidBodies();
			
			List<Vector3f> vertexList = new ArrayList<Vector3f>();
			List<Vector2f> texcoordList = new ArrayList<Vector2f>();
			List<Vector3f> normalList = new ArrayList<Vector3f>();

			for (int i = 0; i < vertexMap.size(); i++) 
			{
				vertexList.addAll(vertexMap.get(i));
				texcoordList.addAll(texcoordMap.get(i));
				normalList.addAll(normalMap.get(i));
			}

			List<Vector3f> transVertexList = new ArrayList<Vector3f>();
			List<Vector2f> transTexcoordList = new ArrayList<Vector2f>();
			List<Vector3f> transNormalList = new ArrayList<Vector3f>();

			for (int i = 0; i < transVertexMap.size(); i++) 
			{
				transVertexList.addAll(transVertexMap.get(i));
				transTexcoordList.addAll(transTexcoordMap.get(i));
				transNormalList.addAll(transNormalMap.get(i));
			}

			if (vertexList.size() > 0) 
			{
				Vector3f[] vertexArray = new Vector3f[vertexList.size()];
				Vector2f[] texcoordArray = new Vector2f[texcoordList.size()];
				Vector3f[] normalArray = new Vector3f[normalList.size()];

				for (int i = 0; i < vertexArray.length; i++) 
				{
					vertexArray[i] = vertexList.get(i);
					texcoordArray[i] = texcoordList.get(i);
					normalArray[i] = normalList.get(i);
				}
				vertexBuffer = BufferUtils.createFloatBuffer(vertexArray);
				texcoordBuffer = BufferUtils.createFloatBuffer(texcoordArray);
				normalBuffer = BufferUtils.createFloatBuffer(normalArray);

				int[] indices = new int[vertexArray.length];
				// fill index buffer
				for (int i = 0; i < indices.length; i++) 
				{
					indices[i] = i;
				}
				indexBuffer = BufferUtils.createIntBuffer(indices);
				instanceGeom = generateRigidBodyGeometry("ClientRigidBodyInstance", vertexBuffer, texcoordBuffer,
						indexBuffer, normalBuffer, "test");
				Object[] movementBuffers = generateMovementBuffers(vertexMap);
//				System.out.println(indexBuffer.capacity() + "-" + instancePosBuffer1.capacity() + "-" + instancePosBuffer2.capacity());
				if(movementBuffers != null)
				{
					playerCamera.attachChild(instanceGeom, "RigidBodyNode", (FloatBuffer) movementBuffers[0], (FloatBuffer) movementBuffers[1]);
				}
			}

			if (transVertexList.size() > 0) 
			{
				Vector3f[] transVertexArray = new Vector3f[transVertexList.size()];
				Vector2f[] transTexcoordArray = new Vector2f[transTexcoordList.size()];
				Vector3f[] transNormalArray = new Vector3f[transNormalList.size()];

				for (int i = 0; i < transVertexArray.length; i++) 
				{
					transVertexArray[i] = transVertexList.get(i);
					transTexcoordArray[i] = transTexcoordList.get(i);
					transNormalArray[i] = transNormalList.get(i);
				}

				transVertexBuffer = BufferUtils.createFloatBuffer(transVertexArray);
				transTexcoordBuffer = BufferUtils.createFloatBuffer(transTexcoordArray);
				transNormalBuffer = BufferUtils.createFloatBuffer(transNormalArray);

				int[] transIndices = new int[transVertexArray.length];
				// fill index buffer
				for (int i = 0; i < transIndices.length; i++) {
					transIndices[i] = i;
				}
				transIndexBuffer = BufferUtils.createIntBuffer(transIndices);
				transInstanceGeom = generateRigidBodyTransGeometry("ClientRigidBodyTransInstance", transVertexBuffer, transTexcoordBuffer, transIndexBuffer, transNormalBuffer, "test");
//				Object[] transMovementBuffers = generateMovementBuffers(transVertexMap);
				
				Object[] transMovementBuffers = generateMovementBuffers(transVertexMap);
//				System.out.println(transIndexBuffer.capacity() + "-" + transInstancePosBuffer1.capacity() + "-" + transInstancePosBuffer2.capacity());
				if(transMovementBuffers != null)
				{
					playerCamera.attachChild(transInstanceGeom, "TransparentRigidBodyNode", (FloatBuffer) transMovementBuffers[0], (FloatBuffer) transMovementBuffers[1]);
				}
			}

			waitMovement = true;
			hasUpdate = false;
		}
	}

	// TODO
	// Seperate RigidBody into opaque and transparent mesh
	public Geometry generateRigidBodyGeometry(String BodyName, FloatBuffer BodyVertexBuffer,
			FloatBuffer BodyTexCoordBuffer, IntBuffer BodyIndexBuffer, FloatBuffer BodyNormalBuffer,
			String BodyTextureName) {
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(BodyName, someMesh);

		// test material
		someMesh.setBuffer(Type.Position, 3, BodyVertexBuffer);
		someMesh.setBuffer(Type.TexCoord, 2, BodyTexCoordBuffer);
		someMesh.setBuffer(Type.Index, 3, BodyIndexBuffer);
		someMesh.setBuffer(Type.Normal, 3, BodyNormalBuffer);
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("instancetest").clone();
		someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
		someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
		
		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(BodyTextureName));
		someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		// someMaterial.getAdditionalRenderState().setWireframe(true);
		someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
		someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);

		someGeometry.setMaterial(someMaterial);
		someGeometry.setQueueBucket(Bucket.Opaque);

		return someGeometry;
	}

	public Geometry generateRigidBodyTransGeometry(String BodyName, FloatBuffer BodyVertexBuffer,
			FloatBuffer BodyTexCoordBuffer, IntBuffer BodyIndexBuffer, FloatBuffer BodyNormalBuffer,
			String BodyTextureName) {
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(BodyName, someMesh);

		// test material
		someMesh.setBuffer(Type.Position, 3, BodyVertexBuffer);
		someMesh.setBuffer(Type.TexCoord, 2, BodyTexCoordBuffer);
		someMesh.setBuffer(Type.Index, 3, BodyIndexBuffer);
		someMesh.setBuffer(Type.Normal, 3, BodyNormalBuffer);
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("instancetest").clone();
		
		someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
		someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
		
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(BodyTextureName));
		someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
		someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
		someMaterial.setTransparent(true);
		someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
		someGeometry.setMaterial(someMaterial);
		someGeometry.setQueueBucket(RenderQueue.Bucket.Translucent);

		return someGeometry;
	}
	
	public Object[] generateMovementBuffers(List<List<Vector3f>> List) 
	{
		if(rigidBodies.size() == 0)
		{
			return null;
		}
		
		List<Vector2f> ar1 = new ArrayList<Vector2f>();
		List<Vector2f> ar2 = new ArrayList<Vector2f>();
		
		for (int i = 0; i < List.size(); i++) 
		{
			float x = rigidBodies.get(i).getPosition().x;
			float y = rigidBodies.get(i).getPosition().y;
			float z = rigidBodies.get(i).getPosition().z;

			float voxelScale = rigidBodies.get(i).getVoxelScale();
			
			Quaternion rotation = rigidBodies.get(i).getRotation();
			
			int size = List.get(i).size();
			
			for (int j = 0; j < size; j++) 
			{
				Vector3f rot = rotation.mult(List.get(i).get(j).mult(voxelScale));
//				movement.add(new Vector3f(rot.x + x, rot.y + y, rot.z + z));
//				Vector3f pos = List.get(i).get(j);
				
				ar1.add( new Vector2f(x + rot.x,y + rot.y));
				ar2.add( new Vector2f(z + rot.z,0));
			}
		}

		Vector2f[] ara1 = new Vector2f[ar1.size()];
		Vector2f[] ara2 = new Vector2f[ar1.size()];
		
		for (int i = 0; i < ara1.length; i++) 
		{
			ara1[i] = ar1.get(i);
			ara2[i] = ar2.get(i);
		}
		return new Object[] { BufferUtils.createFloatBuffer(ara1), BufferUtils.createFloatBuffer(ara2)};
	}
	
	public void sortRigidBodies()
	{
		if(rigidBodies.size() < 2)
		{
			return;
		}
		
		List<List<Vector3f>> tempTransVertexMap = new ArrayList<List<Vector3f>>();
		List<List<Vector2f>> tempTransTexcoordMap = new ArrayList<List<Vector2f>>();
		List<List<Vector3f>> tempTransNormalMap = new ArrayList<List<Vector3f>>();

		List<List<Vector3f>> tempVertexMap = new ArrayList<List<Vector3f>>();
		List<List<Vector2f>> tempTexcoordMap = new ArrayList<List<Vector2f>>();
		List<List<Vector3f>> tempNormalMap = new ArrayList<List<Vector3f>>();
		
		List<RigidBody> sortedList = new ArrayList<RigidBody>();
		sortedList.addAll(rigidBodies);
		Collections.sort(sortedList, (p1,p2)-> p2.getDistance(playerCamera.getPosition()).compareTo(p1.getDistance(playerCamera.getPosition())));
		
		for(int i = 0; i < sortedList.size(); i++)
		{
			for(int j = 0; j < rigidBodies.size(); j++)
			{
				if(sortedList.get(i).getID().intValue() == rigidBodies.get(j).getID().intValue())
				{
					//copy to 
					tempVertexMap.add(vertexMap.get(j));
					tempTexcoordMap.add(texcoordMap.get(j));
					tempNormalMap.add(normalMap.get(j));
					
					tempTransVertexMap.add(transVertexMap.get(j));
					tempTransTexcoordMap.add(transTexcoordMap.get(j));
					tempTransNormalMap.add(transNormalMap.get(j));
					break;
				}
			}
		}
		vertexMap = tempVertexMap;
		texcoordMap = tempTexcoordMap;
		normalMap = tempNormalMap;
		
		transVertexMap = tempTransVertexMap;
		transTexcoordMap = tempTransTexcoordMap;
		transNormalMap = tempTransNormalMap;
		
		rigidBodies = sortedList;
	}
	
	public void updateMaterial()
	{
		if (playerCamera.hasGeometry("TransparentRigidBodyNode", "ClientRigidBodyTransInstance")) 
		{
			Object[] movementBuffers = generateMovementBuffers(vertexMap);
			
			if(movementBuffers != null)
			{
				if( ((FloatBuffer) movementBuffers[0]).limit() != 0 && ((FloatBuffer) movementBuffers[1]).limit() != 0)
				{
					Mesh mesh = instanceGeom.getMesh();
					mesh.setBuffer(Type.TexCoord2, 2, (FloatBuffer) movementBuffers[0]);
					mesh.setBuffer(Type.TexCoord3, 2, (FloatBuffer) movementBuffers[1]);
					mesh.updateBound();
					hasMovementUpdate = false;
				}
			}
		}

		if (playerCamera.hasGeometry("RigidBodyNode", "ClientRigidBodyInstance")) 
		{
			Object[] transMovementBuffers = generateMovementBuffers(transVertexMap);
			if(transMovementBuffers != null)
			{
				if( ((FloatBuffer) transMovementBuffers[0]).limit() != 0 && ((FloatBuffer) transMovementBuffers[1]).limit() != 0)
				{
					Mesh transMesh = transInstanceGeom.getMesh();
					
					transMesh.setBuffer(Type.TexCoord2, 2, (FloatBuffer) transMovementBuffers[0]);
					transMesh.setBuffer(Type.TexCoord3, 2, (FloatBuffer) transMovementBuffers[1]);
					transMesh.updateBound();
					hasMovementUpdate = false;
				}
			}
		}
		// playerCamera.attachChild(instanceGeom, "RigidBodyNode");
	}

	private void removeRigidBodies() 
	{
		if (playerCamera.getServerPosition() == null) 
		{
			return;
		}

		int chunkIDX = (int) playerCamera.getServerPosition().getX() / playerSettings.chunkSize;
		int chunkIDZ = (int) playerCamera.getServerPosition().getZ() / playerSettings.chunkSize;

		for (int i = 0; i < rigidBodies.size(); i++) 
		{
			if (i > vertexMap.size() - 1) 
			{
				return;
			}

			int px = (int) rigidBodies.get(i).getPosition().x / playerSettings.chunkSize;
			int pz = (int) rigidBodies.get(i).getPosition().z / playerSettings.chunkSize;

			if (Math.abs(px - chunkIDX) > SettingsLibrary.renderDistance || Math.abs(pz - chunkIDZ) > SettingsLibrary.renderDistance) 
			{
				vertexMap.remove(i);
				texcoordMap.remove(i);
				normalMap.remove(i);

				transVertexMap.remove(i);
				transTexcoordMap.remove(i);
				transNormalMap.remove(i);

				rigidBodies.remove(i);
				i = 0;
				hasUpdate = true;
			}
		}
	}

	public RigidBody getRigidBodyByID(int ID) 
	{
		for (int i = 0; i < rigidBodies.size(); i++) 
		{
			if (rigidBodies.get(i).getID() == ID) 
			{
				return rigidBodies.get(i);
			}
		}
		return null;
	}

	public void addRigidBodies(byte[] Data) 
	{
		int updateCount = 0;

		if (Data.length == 0) 
		{
			return;
		}
		int byteCount = 0;
		int inc = 0;
		for (byteCount = 0; byteCount < Data.length - 1; byteCount += inc) 
		{
			Object[] rigidBodyData = RigidBodyCompressionUtil.UnpackRigidBodyData(Data, byteCount);
			// generate new rigidBody
			boolean hasClone = false;
			int id = (int) rigidBodyData[1];
			for (int i = 0; i < rigidBodies.size(); i++) 
			{
				if (rigidBodies.get(i).getID() == id) 
				{
					hasClone = true;
				}
			}
			if (!hasClone) 
			{
				RigidBody newBody = new RigidBody(rigidBodyData);
				rigidBodies.add(newBody);
			} 
			else 
			{
				RigidBody clone = getRigidBodyByID(id);
				clone.modify(rigidBodyData);
			}
			updateCount++;
			inc = (int) rigidBodyData[8];
		}
	}

	public void updateRigidBodyMovement(NetworkPacket Pack) 
	{	
		byte[] data = Pack.getData();
		
		for (int i = 0; i < data.length; i += 32) 
		{
			int id = CompressionUtil.BytesToInt(data, i);
			Vector3f pos = CompressionUtil.BytesToVector3f(data, i + 4);
			Quaternion rot = CompressionUtil.BytesToQuaternion(data, i + 16);

			for (int j = 0; j < rigidBodies.size(); j++) 
			{
				if (rigidBodies.get(j).getID() == id) 
				{
					if(!rigidBodies.get(j).getPosition().equals(pos))
					{
						hasMovementUpdate = true;
					}
					rigidBodies.get(j).updateData(pos, rot);
					break;
				}
			}
		}
		
	}

	public List<RigidBody> getRigidBodies() 
	{
		return rigidBodies;
	}
}
