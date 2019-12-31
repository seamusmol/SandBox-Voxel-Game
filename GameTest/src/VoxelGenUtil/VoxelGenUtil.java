package VoxelGenUtil;

import com.jme3.math.Vector3f;

import Util.MathUtil;

public class VoxelGenUtil {

	public static short[][][] GenerateTunnel(short[][][] Materials, Vector3f P1, Vector3f P2, int width)
	{
		short[][][] newMap = Materials;

		Vector3f dir = new Vector3f(P2.x - P1.x, P2.y - P1.y, P2.z - P1.z);
        dir = dir.normalize();

        float distance = Math.round(MathUtil.distance(P1, P2));

        for (float i = 0; i < distance; i++)
        {
            int px = (int) Math.round(P1.x + dir.x*i);
            int py = (int) Math.round(P1.y + dir.y*i);
            int pz = (int) Math.round(P1.z + dir.z*i);
            
            for (int countX = -width; countX <= width; countX++)
            {
                for (int countY = -width; countY <= width; countY++)
                {
                	for (int countZ = -width; countZ <= width; countZ++)
                    {
	                	if(px+countX >= 0 && px+countX < newMap.length)
	                	{
	                		if(py+countY >= 0 && py+countY < newMap[0].length)
	                		{
	                			if(pz+countZ >= 0 && pz+countZ < newMap[0][0].length)
		                		{
	                				newMap[px + countX][ py + countY][pz + countZ] = 0;
		                		}
	                		}
	                	}
                    }
                }
            }
        }
        return newMap;
	}
	
	public static short[][][] GenerateTunnel(short[][][] Materials, Vector3f P1, Vector3f P2, int w1, int w2)
	{
		short[][][] newMap = Materials;

		Vector3f dir = new Vector3f(P2.x - P1.x, P2.y - P1.y, P2.z - P1.z);
        dir = dir.normalize();

        int distance = MathUtil.distance(P1, P2).intValue();

        for (int i = 0; i < distance; i++)
        {
            int px = (int) (P1.x + dir.x*i);
            int py = (int) (P1.y + dir.y*i);
            int pz = (int) (P1.z + dir.z*i);
            
            int width = (int)MathUtil.lerp(i, 0, distance, w1, w2);
            
            for (int countX = -width; countX <= width; countX++)
            {
                for (int countY = -width; countY <= width; countY++)
                {
                	for (int countZ = -width; countZ <= width; countZ++)
                    {
	                	if(px+countX >= 0 && px+countX < newMap.length)
	                	{
	                		if(py+countY >= 0 && py+countY < newMap[0].length)
	                		{
	                			if(pz+countZ >= 0 && pz+countZ < newMap[0][0].length)
		                		{
	                				newMap[px + countX][ py + countY][pz + countZ] = 0;
		                		}
	                		}
	                	}
                    }
                }
            }
        }
        return newMap;
	}
	
	
	public static short[][][] GenerateCurvedTunnel( short[][][] Materials, Vector3f P1, Vector3f P2, int w1, int w2, int ampx, int ampy,int ampz)
	{
		short[][][] newMap = Materials;

		Vector3f dir = new Vector3f(P2.x - P1.x, P2.y - P1.y, P2.z - P1.z);
        dir = dir.normalize();

        float distance = Math.round(MathUtil.distance(P1, P2));

        for (float i = 0; i < distance; i++)
        {
            int px = (int) Math.round(P1.x + dir.x*i);
            int py = (int) Math.round(P1.y + dir.y*i);
            int pz = (int) Math.round(P1.z + dir.z*i);
            
            int width = Math.round(MathUtil.lerp(i, 0, distance, w1, w2));
            
            for (int countX = -width; countX <= width; countX++)
            {
                for (int countY = -width; countY <= width; countY++)
                {
                	for (int countZ = -width; countZ <= width; countZ++)
                    {
	                	if(px+countX >= 0 && px+countX < newMap.length)
	                	{
	                		if(py+countY >= 0 && py+countY < newMap[0].length)
	                		{
	                			if(pz+countZ >= 0 && pz+countZ < newMap[0][0].length)
		                		{
	                				newMap[px + countX][ py + countY][pz + countZ] = 0;
		                		}
	                		}
	                	}
                    }
                }
            }
        }
        return newMap;
	}
	
	
	public static short[][][] GenerateEnding(short[][][] Voxels, Vector3f P1, int width)
	{
		short[][][] newMap = Voxels;
		
		int px = (int) (P1.x);
        int py = (int) (P1.y);
        int pz = (int) (P1.z);
        
        for (int countX = -width; countX <= width; countX++)
        {
            for (int countY = -width; countY <= width; countY++)
            {
            	for (int countZ = -width; countZ <= width; countZ++)
                {
                	if(px+countX >= 0 && px+countX < newMap.length)
                	{
                		if(py+countY >= 0 && py+countY < newMap[0].length)
                		{
                			if(pz+countZ >= 0 && pz+countZ < newMap[0][0].length)
	                		{
                				newMap[px + countX][ py + countY][pz + countZ] = 0;
	                		}
                		}
                	}
                }
            }
        }
		return newMap;
	}
	
}
