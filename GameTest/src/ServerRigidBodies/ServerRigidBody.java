package ServerRigidBodies;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.Control;

import Configs.ServerSettings;
import Util.CompressionUtil;
import Util.MathUtil;
import Util.VoxelUtil;
import worldGen.Cell;

public class ServerRigidBody {

	short[][][] materials;
	private Vector3f position = new Vector3f();
	private Quaternion rotation = new Quaternion();
	
	public float voxelScale = 1.0f;
	
	private int id = -1;
	public Geometry Model;
	public Control modelControl;
	
	public boolean hasProcessedCreation = false;
	public boolean hasProcessedMovement = false;
	
	public boolean needsCreationDataSent = true;
	public boolean needsMovementDataSent = true;
	
	public boolean hasControlModification = false;
	public boolean hasStaticGeometryUpdate = false;
	
	public boolean isUpright = true;
	
	public Queue<Vector3f> forceLocations = new ConcurrentLinkedQueue<Vector3f>(); 
	public Queue<Vector3f> forceDirections = new ConcurrentLinkedQueue<Vector3f>();
	
	public ServerRigidBody(Vector3f Position, Quaternion Rotation, float VoxelScale, short[][][] Materials, boolean GhostPhysics)
	{
		position = Position;
		rotation = Rotation;
		materials = Materials;
		voxelScale = 1.0f;
//		voxelScale = VoxelScale;
		isUpright = GhostPhysics;
		
	}
	
	public ServerRigidBody(Object[] Values)
	{
//		voxelScale = (float) Values[0];
		id = (int)Values[1];
		position = (Vector3f) Values[2];
		rotation = (Quaternion) Values[3];
		materials = (short[][][]) Values[7];
		isUpright = (boolean) Values[9];
	}
	
	//clone
	public ServerRigidBody(ServerRigidBody Original)
	{
		this.materials = Original.materials;
		this.position = Original.position;
		this.rotation = Original.rotation;
		this.voxelScale = Original.voxelScale;
		this.id = Original.id;
		this.Model = Original.Model;
		this.modelControl = Original.modelControl;
		this.hasProcessedCreation = Original.hasProcessedCreation;
		this.hasProcessedMovement = Original.hasProcessedMovement;
		this.needsCreationDataSent = Original.needsCreationDataSent;
		this.needsMovementDataSent = Original.needsMovementDataSent;
		this.isUpright = Original.isUpright;
	}
	
	public void deactivate()
	{
		hasProcessedCreation = false;
		hasProcessedMovement = false;
		needsCreationDataSent = true;
		needsMovementDataSent = true;	
	}
	
	public void setID(int ID)
	{
		id = ID;
	}
	
	public void updatePosition()
	{
		if(modelControl != null)
		{
			Vector3f physicsLocation = ((PhysicsRigidBody) modelControl).getPhysicsLocation();
			Quaternion physicsRotation = ((PhysicsRigidBody) modelControl).getPhysicsRotation();
			
			position.set(physicsLocation);
			needsMovementDataSent = true;
			rotation.set(physicsRotation);
			needsMovementDataSent = true;
		}
	}
	
	//0:removal 1:creation
	
	public void modify(Vector3f ModifyPosition, int ModificationType, int Material)
    {
	 	Vector3f voxelPosition = getVoxelFieldPosition(ModifyPosition);
	 	int px = (int)voxelPosition.x;
	 	int py = (int)voxelPosition.y;
	 	int pz = (int)voxelPosition.z;
	 	boolean hasChanged = false;
		switch(ModificationType)
		{
			case 0:
				if (hasVoxel(px,py,pz))
	            {
					SetVertexMaterial(px,py,pz, 0);
					hasChanged = true;
	            }
				//call updates
				break;
			case 1:
				if (!hasVoxel(px,py,pz))
	            {
					SetVertexMaterial(px,py,pz, Material);
					checkExpansion(px, py, pz);
					
					hasChanged = true;
	            }
				break;
			case 2:
				//freeze/unfreeze
				isUpright = !isUpright;
				hasControlModification = true;
				break;
			case 3: 
				setPhysicsLocation(ModifyPosition);
				break;
		}
		if(hasChanged)
		{
			hasProcessedCreation = false;
			hasProcessedMovement = false;
			needsCreationDataSent = true;
			needsMovementDataSent = true;	
		}
	}
	
	
	public void setPhysicsLocation(Vector3f NewPosition)
	{
		if(isUpright)
		{
			hasStaticGeometryUpdate = true;
		}
		else
		{
			((PhysicsRigidBody) modelControl).setPhysicsLocation(NewPosition);
		}
	}
	
	public float calculateMass()
	{
		float mass = 0;
		for(int i = 0; i < materials.length; i++)
		{
			for(int j = 0; j < materials[0].length; j++)
			{
				for(int k = 0; k < materials[0][0].length; k++)
				{
					if(materials[i][j][k] != 0)
					{
						mass += 1.0f;
					}
				}
			}
		}
		return mass;
	}
	
	public void processControlModification()
	{
		if(isUpright)
		{
			((PhysicsRigidBody) modelControl).setMass(0.0f);
			((PhysicsRigidBody) modelControl).setKinematic(true);
			((PhysicsRigidBody) modelControl).setFriction(1.0f);
			((PhysicsRigidBody) modelControl).setDamping(0.75f, 0.75f);
		}
		else
		{
			((PhysicsRigidBody) modelControl).setMass(calculateMass());
			((PhysicsRigidBody) modelControl).setKinematic(false);
			((PhysicsRigidBody) modelControl).setFriction(1.0f);
			((PhysicsRigidBody) modelControl).setDamping(0.75f, 0.75f);
		}
		hasControlModification = false;
	}
	
	public void processCreation()
    {
	   	Object[] CellLists = VoxelUtil.GenerateVoxelCellList(materials, false);
	   	
       	Object[] data = VoxelUtil.createCellData((List<Cell>)CellLists[0]);
		Object[] buffers = VoxelUtil.createBuffers(data, getCenterBoundingDistance().negate());
		
		Model = VoxelUtil.GenerateRigidBodyGeometry(id + "", (FloatBuffer)buffers[0], (FloatBuffer)buffers[1], (IntBuffer)buffers[2], (FloatBuffer)buffers[3], 0, "test");
		Model.setLocalTranslation(position);
		Model.setLocalScale(voxelScale);
		
		if(isUpright)
		{
			modelControl = new RigidBodyControl( new HullCollisionShape(Model.getMesh()), 0.0f);
			((PhysicsRigidBody) modelControl).setKinematic(true);
			((PhysicsRigidBody) modelControl).setFriction(1.0f);
			((PhysicsRigidBody) modelControl).setDamping(0.75f, 0.75f);
		}
		else
		{
			modelControl = new RigidBodyControl( new HullCollisionShape(Model.getMesh()), calculateMass());
			((PhysicsRigidBody) modelControl).setKinematic(false);
			((PhysicsRigidBody) modelControl).setFriction(1.0f);
			((PhysicsRigidBody) modelControl).setDamping(0.75f, 0.75f);
		}
		
		Model.addControl(modelControl);
		needsMovementDataSent = true;
		needsCreationDataSent = true;
		hasProcessedCreation = true;
		hasProcessedMovement = true;
    }
	
	public void addForce(Vector3f ForceLocation, Vector3f ForceDirection)
	{
		forceLocations.add(ForceLocation);
		forceDirections.add(ForceDirection);
	}
	
	public void setPosition(Vector3f NewPosition)
	{
		position = NewPosition;
	}
	
	public void processMovement()
	{
		hasProcessedMovement = true;
	}
	
	public Vector3f getVoxelFieldPosition(Vector3f VoxelPosition)
	{
		Vector3f offset = rotation.mult(getCenterBoundingDistance());
		
		int px = Math.round(MathUtil.distance(VoxelPosition.x, position.x - offset.x)/getVoxelScale());
		int py = Math.round(MathUtil.distance(VoxelPosition.y, position.y - offset.y)/getVoxelScale());
		int pz = Math.round(MathUtil.distance(VoxelPosition.z, position.z - offset.z)/getVoxelScale());
		
		return new Vector3f(px,py,pz);
	}
	
	public Vector3f getCenter()
	{
		return position.add( new Vector3f(materials.length/2,materials[0].length/2,materials[0][0].length/2).mult(voxelScale));
	}
	
	public Vector3f getBoundingDistance()
	{
		return new Vector3f(materials.length, materials[0].length, materials[0][0].length);
	}
	
	public Vector3f getCenterBoundingDistance()
	{
		return new Vector3f(materials.length, materials[0].length, materials[0][0].length).mult(0.5f);
	}
	
	public float getVoxelScale()
	{
		return voxelScale;
	}
	
	public Vector3f getWorldPosition(int X,int Y, int Z)
	{
		return position.add( rotation.mult( new Vector3f(X - materials.length/2,Y - materials[0].length/2,Z - materials[0][0].length/2)));
//		return position.add( rotation.mult( new Vector3f(X,Y,Z).mult(voxelScale)) );
	}
	
	public Vector3f getNearestVoxelWordPosition(Vector3f VoxelPosition)
	{
		return getVoxelFieldPosition(VoxelPosition).mult(voxelScale).add(position);
	}
	
	public List<Byte> GetData()
	{
		return RigidBodyCompressionUtil.GenerateCompressedRigidBodyData(this);
	}
	
	public List<Byte> GetMovementData()
	{
		List<Byte> data = new ArrayList<Byte>();
		byte[] idBytes = ByteBuffer.allocate(4).putInt(id).array();
		data.add(idBytes[3]);
		data.add(idBytes[2]);
		data.add(idBytes[1]);
		data.add(idBytes[0]);
		//px
		byte[] pxBytes = ByteBuffer.allocate(4).putFloat(position.getX()).array();
		data.add(pxBytes[3]);
		data.add(pxBytes[2]);
		data.add(pxBytes[1]);
		data.add(pxBytes[0]);
		//py
		byte[] pyBytes = ByteBuffer.allocate(4).putFloat(position.getY()).array();
		data.add(pyBytes[3]);
		data.add(pyBytes[2]);
		data.add(pyBytes[1]);
		data.add(pyBytes[0]);
		//pz
		byte[] pzBytes = ByteBuffer.allocate(4).putFloat(position.getZ()).array();
		data.add(pzBytes[3]);
		data.add(pzBytes[2]);
		data.add(pzBytes[1]);
		data.add(pzBytes[0]);
		//Rotation----------\\
		//rx
		byte[] rxBytes = ByteBuffer.allocate(4).putFloat(rotation.getX()).array();
		data.add(rxBytes[3]);
		data.add(rxBytes[2]);
		data.add(rxBytes[1]);
		data.add(rxBytes[0]);
		//ry
		byte[] ryBytes = ByteBuffer.allocate(4).putFloat(rotation.getY()).array();
		data.add(ryBytes[3]);
		data.add(ryBytes[2]);
		data.add(ryBytes[1]);
		data.add(ryBytes[0]);
		//rz
		byte[] rzBytes = ByteBuffer.allocate(4).putFloat(rotation.getZ()).array();
		data.add(rzBytes[3]);
		data.add(rzBytes[2]);
		data.add(rzBytes[1]);
		data.add(rzBytes[0]);
		//rw
		byte[] rwBytes = ByteBuffer.allocate(4).putFloat(rotation.getW()).array();
		data.add(rwBytes[3]);
		data.add(rwBytes[2]);
		data.add(rwBytes[1]);
		data.add(rwBytes[0]);
	
		return data;
	}
	
    public void SetVertexMaterial(int x, int y, int z, int material)
    {
	   	 if(x < 0 || x >= materials.length || y < 0 || y >= materials[0].length || z < 0 || z >= materials[0][0].length)
	   	 {
	   		 return;
	   	 }
	   	 materials[x][y][z] = (short)material;
    }
    
    public void checkExpansion(int x, int y, int z)
    {
    	int sx = materials.length;
    	int sy = materials[0].length;
    	int sz = materials[0][0].length;
    	
    	int px = x == 0 ? 1:0;
    	int py = y == 0 ? 1:0;
    	int pz = z == 0 ? 1:0;
    	
    	int ox = x >= sx-1 ? 1:0;
    	int oy = y >= sy-1 ? 1:0;
    	int oz = z >= sz-1 ? 1:0;
    	
    	if(px != 0 || py != 0 || pz != 0 || ox != 0 || oy != 0 || oz != 0)
    	{
    		short[][][] newMaterials = new short[sx + px + ox][sy + py + oy][sz + pz + oz];
    		//fill
    		for(int i = 0; i < sx; i++)
    		{
    			for(int j = 0; j < sy; j++)
        		{
    				for(int k = 0; k < sz; k++)
    	    		{
    					newMaterials[i + px][j + py][k + pz] = materials[i][j][k];
    	    		}
        		}
    		}
    		materials = newMaterials;
    		Model.setLocalTranslation(position.add(new Vector3f(px - ox,py - oy,pz - oy).mult(voxelScale)));
    	}
    }
    
    public void checkShrinkage(int x, int y, int z)
    {
    	int sx = materials.length;
    	int sy = materials[0].length;
    	int sz = materials[0][0].length;
    	
    	int px = x == 0 ? 1:0;
    	int py = y == 0 ? 1:0;
    	int pz = z == 0 ? 1:0;
    	
    	int ox = x >= sx-1 ? 1:0;
    	int oy = y >= sy-1 ? 1:0;
    	int oz = z >= sz-1 ? 1:0;
    	
    	
    	boolean[][][] checkMap = new boolean[materials.length][materials[0][0].length][materials[0][0].length];
    	for(int i = 0; i < checkMap.length; i++)
    	{
    		for(int j = 0; j < checkMap.length; j++)
        	{
    			for(int k = 0; k < checkMap.length; k++)
    	    	{
    				checkMap[i][j][k] = materials[i][j][k] != 0;
    	    	}
        	}
    	}
    	
    	List<ServerRigidBody> splitList = new ArrayList<ServerRigidBody>();
    	for(int i = 0; i < checkMap.length; i++)
    	{
    		for(int j = 0; j < checkMap[0].length; j++)
        	{
    			for(int k = 0; k < checkMap[0][0].length; k++)
    	    	{
    				
    	    	}
        	}
    	}
    	
    	//if size == 3 && s,y,z == 1
    	//remove rigidBody
    	
    }
    //recursive 3d floodfill check
    public void checkSplit(boolean IsTop, int px, int py, int pz, boolean[][][] CheckMap)
    {
    	if(px == 0 || py == 0 || pz == 0)
    	{
    		if(px == CheckMap.length-1 || py == CheckMap[0].length-1 || pz == CheckMap[0][0].length-1)
    		{
    			return;
    		}
    	}
    	CheckMap[px][py][pz] = true;
    	
    	if(CheckMap[px-1][py][pz])
    	{
    		checkSplit(false, px-1, py, pz, CheckMap);
    		
    	}
    	if(CheckMap[px+1][py][pz])
    	{
    		checkSplit(false, px+1, py, pz, CheckMap);
    	}
    	if(CheckMap[px][py-1][pz])
    	{
    		checkSplit(false, px, py-1, pz, CheckMap);
    	}
    	if(CheckMap[px][py+1][pz])
    	{
    		checkSplit(false, px, py+1, pz, CheckMap);
    	}
    	if(CheckMap[px][py][pz-1])
    	{
    		checkSplit(false, px, py, pz-1, CheckMap);
    	}
    	if(CheckMap[px][py][pz+1])
    	{
    		checkSplit(false, px, py, pz+1, CheckMap);
    	}
    	
    	if(IsTop)
    	{
    		
    	}
    }
    
    public boolean hasVoxel(int x, int y, int z)
    {
	   	 if(x < 0 || x >= materials.length || y < 0 || y >= materials[0].length || z < 0 || z >= materials[0][0].length)
	   	 {
	   		 return false;
	   	 }
	   	 
	   	 return  materials[x][y][z] != (short)0;
    }

	public short[][][] getMaterials() {
		return materials;
	}
	
	public void setPhysicsPosition(Vector3f NewPosition)
	{
		if(Model != null)
		{
			Model.setLocalTranslation(NewPosition);
		}
	}
	
//	public Vector2f getChunkPosition()
//	{
//		return new Vector2f((int)position.getX()/worldSettings.chunkSize, (int)position.getZ()/worldSettings.chunkSize);
//	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public int getID() {
		return id;
	}

	public boolean isUpright() {
		return isUpright;
	}

	public void setStaticUpdate(boolean Val)
	{
		hasStaticGeometryUpdate = Val;
	}

	public Queue<Vector3f> getForceLocations() {
		return forceLocations;
	}

	public Queue<Vector3f> getForceDirections() {
		return forceDirections;
	}

	public Control getModelControl() {
		return modelControl;
	}

	
}
