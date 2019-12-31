package ServerRigidBodies;

import java.util.ArrayList;
import java.util.List;

public class RigidBodyCollisionResults {

	List<RigidBodyCollisionResult> results = new ArrayList<RigidBodyCollisionResult>();
	public RigidBodyCollisionResults()
	{
		
	}
	
	public int size()
	{
		return results.size();
	}
	
	public void add(RigidBodyCollisionResult result)
	{
		results.add(result);
	}
	
	public RigidBodyCollisionResult getClosestNonHitResult(float minDist, float maxDist)
	{
		List<RigidBodyCollisionResult> nonHitList = new ArrayList<RigidBodyCollisionResult>();
		for(int i = 0; i < results.size(); i++)
		{
			if(!results.get(i).hasHit)
			{
				nonHitList.add(results.get(i));
			}
		}
		
		if(nonHitList.size() == 0)
		{
			return null;
		}
		
		int curIndex = 0;
		for(int i = 0; i < nonHitList.size(); i++)
		{
			float d =  nonHitList.get(i).distance;
			
			if(nonHitList.get(curIndex).distance > d)
			{
				if(d >= minDist && d <= maxDist )
				{
					curIndex = i;
				}
			}
		}
		return nonHitList.get(curIndex);
	}
	
	public RigidBodyCollisionResult getClosestHitResult()
	{
		List<RigidBodyCollisionResult> hitList = new ArrayList<RigidBodyCollisionResult>();
		for(int i = 0; i < results.size(); i++)
		{
			if(results.get(i).hasHit)
			{
				hitList.add(results.get(i));
			}
		}
		if(hitList.size() == 0)
		{
			return null;
		}
		
		int curIndex = 0;
		for(int i = 0; i < hitList.size(); i++)
		{
			if(hitList.get(curIndex).distance > hitList.get(i).distance)
			{
				curIndex = i;
			}
		}
		return hitList.get(curIndex);
	}
	
//	public RigidBodyCollisionResult getClosestResult()
//	{
//		RigidBodyCollisionResult result = null;
//		if(results.size() == 0)
//		{
//			return null;
//		}
//		
//		int curIndex = 0;
//		for(int i = 0; i < results.size(); i++)
//		{
//			if(results.get(curIndex).distance > results.get(i).distance)
//			{
//				curIndex = i;
//			}
//		}
//		return results.get(curIndex);
//	}
}
