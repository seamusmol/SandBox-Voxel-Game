package ServerMob;

import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;


public abstract class Entity
{
   
	public int entityID = 0;
    public int health = 0;
    public int mass = 0;
    public Boolean isFlammable = false;
    public Boolean isMovable = false;
    public Vector3f position = new Vector3f();
    public Quaternion rotation = new Quaternion();
    
    public List<Vector3f> Points = new ArrayList<Vector3f>();
    public Boolean IsEnabled = true;
	public boolean hasControlAttached = false;
    
	public Control control;
	
    public Entity()
    {
    }
    
    public abstract void OnUpdate(EntityManager EntityManager, float tpf);
    public abstract void CallAI(EntityManager EntityManager, float tpf);
    public abstract void OnImpact(EntityManager EntityManager, float tpf);
    public abstract void OnFire(EntityManager EntityManager, float tpf);
	public abstract void OnMove(EntityManager EntityManager, float tpf);
		
	
	public Control getControl()
	{
		return control;
	}
	
}