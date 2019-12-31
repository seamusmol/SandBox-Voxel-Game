package serverlevelgen;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsSpace.BroadphaseType;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import ClientMob.Entity;
import Configs.ServerSettings;
import Main.Main;
import ServerRigidBodies.ServerRigidBody;
import Util.MathUtil;

public class ServerPhysicsManager extends BulletAppState{

	Main main;
	WorldSettings worldSettings;
	
	public ServerPhysicsManager(WorldSettings WorldSettings, Main Main)
	{
		main = Main;
		worldSettings = WorldSettings;
	}
	
	public void initialize(AppStateManager StateManager, Application Application)
	{
		this.getPhysicsSpace().setAccuracy(1f/30f);
		this.getPhysicsSpace().setBroadphaseType(BroadphaseType.SIMPLE);
		this.getPhysicsSpace().setSolverNumIterations(8);
        this.getPhysicsSpace().setMaxSubSteps(0);
		this.setThreadingType(ThreadingType.SEQUENTIAL);
	}
	
	public void ApplyEntitiesForces(final Queue<Entity> Entities)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception
	        {
	        	for(Entity entity : Entities)
	        	{
//	        		if(entit == null)
//	        		{
//	        			continue;
//	        		}
//	        		PhysicsRigidBody control = (PhysicsRigidBody) rigidBody.getModelControl();
//					Queue<Vector3f> forceDirections = rigidBody.getForceDirections();
//					Queue<Vector3f> forceLocations = rigidBody.getForceLocations();
					
//					Vector3f curVec = new Vector3f();
//					int div = forceDirections.size();
//					float c = 0.016f;
//					while(forceDirections.size() > 0)
//					{
//						curVec = curVec.add(forceDirections.poll());
//					}
//					
//					forceDirections.clear();
//					forceLocations.clear();
	        	}
	        	return null;
	        }
		});
	}
	
	public void ApplyPhysicsForces(final Queue<ServerRigidBody> RigidBodies)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception
	        {
	        	for(ServerRigidBody rigidBody : RigidBodies)
	        	{
	        		if(rigidBody.getModelControl() == null)
	        		{
	        			continue;
	        		}
	        		PhysicsRigidBody control = (PhysicsRigidBody) rigidBody.getModelControl();
					Queue<Vector3f> forceDirections = rigidBody.getForceDirections();
					Queue<Vector3f> forceLocations = rigidBody.getForceLocations();
//					control.clearForces();
					
					Vector3f curVec = new Vector3f();
					int div = forceDirections.size();
					float c = 0.016f;
					while(forceDirections.size() > 0)
					{
						curVec = curVec.add(forceDirections.poll());
					}
					
					if(div != 0)
					{
						Vector3f ov = control.getLinearVelocity().clone();
						ov = ov.mult(1.0f-c);
						
//						curVec = curVec.divide(div);
						curVec = curVec.mult(c);
						control.setLinearVelocity( curVec.add(ov));
					}
					else
					{
//						control.setLinearVelocity( new Vector3f(0,-9.8f,0));
//						control.clearForces();
						
					}
					
					forceDirections.clear();
					forceLocations.clear();
	        	}
	        	return null;
	        }
		});
	}
	
	public void checkDropOffs()
	{
		
	}
	
	public void attachControl(Control Control)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
				if(Control == null || getPhysicsSpace() == null)
				{
					return null;
				}
				if(getPhysicsSpace().getRigidBodyList().contains(Control))
				{
					getPhysicsSpace().remove(Control);
					return null;
				}
		    	getPhysicsSpace().add(Control);
		    	
		    	return null;
	        }
		});
	}
	
	
	public void UpdateControlPositions(final List<Control> Controls, final List<Vector3f> NewPositions)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	for(int i = 0; i < Controls.size(); i++)
	        	{
	        		((PhysicsRigidBody)Controls.get(i)).setPhysicsLocation(NewPositions.get(i));
	        	}
	        	
//	        	for(int i = 0; i < Controls.size(); i++)
//	        	{
//	        		for(PhysicsRigidBody control : getPhysicsSpace().getRigidBodyList())
//	        		{
//	        			if(control.equals(Controls.get(i)))
//	        			{
//	        				control.setPhysicsLocation(NewPositions.get(i));
//	        			}
//	        		}
//	        	}
	        	return null;
	        }
		});
		
		
	}
	
	public void checkControlPositions(final Queue<Vector2f> ChunkPositions)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
				
	        	PhysicsSpace space = getPhysicsSpace();
	        	
	        	for(PhysicsRigidBody Body : space.getRigidBodyList())
	        	{
	        		int px = (int)Body.getPhysicsLocation().getX()/worldSettings.chunkSize;
	        		int pz = (int)Body.getPhysicsLocation().getZ()/worldSettings.chunkSize;
	        		
	        		boolean hasChunk = false;
	        		for(Vector2f chunkPosition : ChunkPositions)
	        		{
	        			if(chunkPosition.x == px && chunkPosition.y == pz)
	        			{
	        				hasChunk = true;
	        				break;
	        			}
	        		}
	        		if(!hasChunk)
	        		{
	        			space.remove(Body);
	        		}
	        	}
				return null;
	        }
		});
	}
	
	public void processRigidBodyEffects(final Queue<ServerRigidBody> RigidBodyList)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception 
	        {
	        	
	        	
	        	
	        	return null;
	        }
		});
		
		
	}
	
	public void detachControl(Control Control)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
				if(Control == null|| getPhysicsSpace() == null)
				{
					return null;
				}
				if(getPhysicsSpace().getRigidBodyList().contains(Control))
				{
					getPhysicsSpace().remove(Control);
				}
				return null;
	        }
		});
	}

	
}
