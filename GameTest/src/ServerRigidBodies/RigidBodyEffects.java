package ServerRigidBodies;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;

import Util.MathUtil;
import serverlevelgen.ServerChunkManager;
import serverlevelgen.WorldSettings;

public class RigidBodyEffects {

	public static void calculateBuoyancy(float tpf, ServerRigidBody RigidBody, ServerChunkManager ChunkManager)
	{
		Vector3f centerPosition = RigidBody.getPosition();
		Vector3f dir = new Vector3f();
		
		float height = 0;
		
		float dens = 0.7f;
		
		List<Vector3f> pos = new ArrayList<Vector3f>();
		
		int volumeCount = 0;
		short[][][] materials = RigidBody.materials;
		
		float b = 0.5f;
		float bc = 0;
		
		float depth = 0f;
		
		float waterCount = 0;
		
		for(int i = 0; i < materials.length; i++)
		{
			for(int j = 0; j < materials[0].length; j++)
			{
				for(int k = 0; k < materials[0][0].length; k++)
				{
					if(materials[i][j][k] != 0)
					{
						volumeCount++;
						Vector3f wp = RigidBody.getWorldPosition(i,j,k);
						short val = ChunkManager.getVertexValue( wp);
						
						if(val == 1023)
						{
							depth += (float)ChunkManager.calculateChunkMaterialDepth( wp, 1023);
							bc+= 2.0f;
							
							waterCount+= 1.0f;
							dir = dir.add( new Vector3f(0,1,0));
						}
						else
						{
							
						}
					}
				}
			}
		}
		
		float m = RigidBody.calculateMass();
		if(m * b <= bc)
		{
			RigidBody.addForce(new Vector3f(), new Vector3f( 0, depth/waterCount*9.8f, 0));
		}
		
	}
	
}
