package ServerMob;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import Util.MathUtil;

public class TestMob extends Mob{

	int searchDistance = 100;
	int searchX = 50;
	int searchZ = 32;
	
	Vector2f startPosition;
	Vector2f searchPosition;
	
	public TestMob(Vector3f Position)
	{
		startPosition = new Vector2f(Position.x,Position.y);
		searchPosition = new Vector2f(Position.x, Position.y);
		
		this.position = Position;
		this.health = 100;
		this.mass = 100;
		this.isFlammable = true;
		this.isMovable = false;
	}
	
	public void generateCollisionPoints()
	{
		
	}
	
	
	@Override
	public void OnUpdate(EntityManager EntityManager, float tpf) 
	{
		CallAI(EntityManager, tpf);
	}
	
	@Override
	public void CallAI(EntityManager EntityManager, float tpf) 
	{
		if(new Vector2f(position.x,position.z).distance(searchPosition) <= 5)
		{
			int tempSearchX = searchX;
			searchX+= searchZ+1;
			searchX%= searchDistance;
			searchZ = tempSearchX;
			searchPosition = new Vector2f(startPosition.x + (searchX - searchDistance/2), startPosition.y + (searchZ - searchDistance/2));
		}
		Vector3f direction = new Vector3f(searchPosition.x - position.x, 0, searchPosition.y - position.z).normalize();
		OnMove(EntityManager, tpf);
	}

	@Override
	public void OnImpact(EntityManager EntityManager, float tpf) 
	{
		
	}

	@Override
	public void OnMove(EntityManager EntityManager, float tpf) 
	{
		
	}

	@Override
	public void OnFire(EntityManager EntityManager, float tpf) 
	{	
	}
	
}
