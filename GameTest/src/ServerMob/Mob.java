package ServerMob;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

public class Mob extends Entity {

	boolean hasWaterBuoyancy = true;
	
	public Mob()
	{
		
	}

	@Override
	public void OnUpdate(EntityManager EntityManager, float tpf) {
		// TODO Auto-generated method stub
		float waterDepth = EntityManager.serverChunkManager.calculateChunkMaterialDepth(this.position, 1023);
		
		if(waterDepth != 1.0f)
		{
			
		}
		
	}

	@Override
	public void CallAI(EntityManager EntityManager, float tpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnImpact(EntityManager EntityManager, float tpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnFire(EntityManager EntityManager, float tpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnMove(EntityManager EntityManager, float tpf) 
	{
		// TODO Auto-generated method stub
		
	}

}
