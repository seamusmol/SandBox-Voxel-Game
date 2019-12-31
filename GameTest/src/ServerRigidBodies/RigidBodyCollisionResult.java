package ServerRigidBodies;

import com.jme3.math.Vector3f;

public class RigidBodyCollisionResult {

	ServerRigidBody rigidBody;
	Vector3f collisionPoint;
	Vector3f voxelFieldLocation;
	float distance;
	int rigidBodyID;
	boolean hasHit = false;
	
	public RigidBodyCollisionResult( ServerRigidBody ServerRigidBody, Vector3f CollisionPoint,Vector3f VoxelFieldLocation, Float Distance, int RigidBodyID, boolean HasHit)
	{
		rigidBody = ServerRigidBody;
		collisionPoint = CollisionPoint;
		voxelFieldLocation = VoxelFieldLocation;
		distance = Distance;
		rigidBodyID = RigidBodyID;
		hasHit = HasHit;
	}
	
	public float distance()
	{
		return distance;
	}
	
	public Vector3f getCollisionPosition()
	{
		return collisionPoint;
	}
	
	public Vector3f getVoxelPosition()
	{
		return voxelFieldLocation;
	}
	
	public int getID()
	{
		return rigidBodyID;
	}

	public ServerRigidBody getRigidBody() {
		return rigidBody;
	}
	
	
	
}
