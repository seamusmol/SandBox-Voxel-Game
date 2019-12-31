package Util;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;

import worldGen.Cell;

public class GreedyMeshUtil {

	
	
	public static Object[] OptimizeBuffers(List<Vector3f> VertexList, List<Integer> Indices)
	{
		List<Vector3f> reduced = new ArrayList<Vector3f>();
		for(int i = 0; i < VertexList.size(); i++)
		{
			reduced.add(VertexList.get(i));
		}
		
		for(int i = 0; i < reduced.size(); i++)
		{
			for(int j = 0; j < reduced.size(); j++)
			{
				if(reduced.get(i).equals((reduced.get(j))))
				{
					reduced.remove(reduced.get(j));
				}
			}
		}
		
		List<Integer> newIndices = new ArrayList<Integer>();
		for(int i = 0; i < Indices.size(); i++)
		{
			for(int j = 0; j < VertexList.size(); j++)
			{
				if(reduced.get(j).equals(VertexList.get(Indices.get(i))))
				{
					newIndices.add(j);
				}
			}
		}
		return new Object[]{reduced,newIndices};
	}
	
	public static List<Cell> GenerateVoxelValues(boolean[][][] Voxels, int[][][] Materials)
    {
		int[][][] voxelValues = new int[Voxels.length-1][Voxels[0].length-1][Voxels[0][0].length-1];
		boolean[][][] shadeMap = new boolean[Voxels.length-1][Voxels[0].length-1][Voxels[0][0].length-1];
		for (int i = 0; i < voxelValues.length-1; i++)
        {
            for (int j = 0; j < voxelValues[0].length-1; j++)
            {
                for (int k = 0; k < voxelValues[0][0].length-1; k++)
                {
                    int binaryValue = 0;
                    binaryValue += Voxels[i][ j + 1][ k + 1] ? 128 : 0;
                    binaryValue += Voxels[i + 1][ j + 1][ k + 1] ? 64 : 0;
                    binaryValue += Voxels[i + 1][ j + 1][ k] ? 32 : 0;
                    binaryValue += Voxels[i][ j + 1][ k] ? 16 : 0;
                    binaryValue += Voxels[i][ j][ k + 1] ? 8 : 0;
                    binaryValue += Voxels[i + 1][ j][ k + 1] ? 4 : 0;
                    binaryValue += Voxels[i + 1][ j][ k] ? 2 : 0;
                    binaryValue += Voxels[i][ j][ k] ? 1 : 0;

                    voxelValues[i][j][k] = binaryValue;
                    
                    if(binaryValue == 0 || binaryValue == 255)
                    {
                    	shadeMap[i][j][k] = true;
                    }
                }
            }
        }
		
		List<Cell> cellList = new ArrayList<Cell>();
		processCase15(cellList,voxelValues,shadeMap);
		
		//add left over
		for (int i = 0; i < voxelValues.length; i++)
        {
            for (int j = 0; j < voxelValues[0].length; j++)
            {
                for (int k = 0; k < voxelValues[0][0].length; k++)
                {
                	if(!shadeMap[i][j][k])
                	{
                		//cellList.add(e)
                		
                	}
                }
            }
		}
		
		return cellList;
    }
	
	//flats
	
	public static void processCase15(List<Cell> CellList, int[][][] VoxelValues, boolean[][][] ShadeMap)
	{
		for (int j = 0; j < VoxelValues[0].length; j++)
        {
			int px = 0;
			int pz = 0;
			int sx = 0;
			int sz = 0;
			
			for (int i = 0; i < VoxelValues.length; i++)
	        {
				for (int k = 0; k < VoxelValues[0][0].length; k++)
                {
					if(!ShadeMap[i][j][k] && VoxelValues[i][j][k] == 15)
					{
						//x axis
						px = i;
						pz = k;
						sx++;
					}
					else
					{
						if(sx > 0)
						{
							for (int z = 0; z < VoxelValues[0][0].length; z++)
			                {
								//z
								boolean isValidRow = true;
								for(int x = px; x < px+sx; x++)
								{
									if(ShadeMap[i][j][k] && VoxelValues[i][j][k] != 15)
									{
										isValidRow = false;
										break;
									}
								}
								if(!isValidRow)
								{
									break;
								}
								else
								{
									sz++;
									for(int x = px; x < px+sx; x++)
									{
										ShadeMap[i][j][k] = true;
									}
								}
							}
							i = px+sx;
							k = pz+sz;
							
							px+=sx;
							pz+=sz;
							sx = 0;
							sz = 1;
							
							//add new cell
//							CellList.add();
							CellList.add( new Cell(15, px, j, pz, sx, 1, sz, false));
							
							//reset position and size
						}
						else
						{
							//blank space
						}
					}
                }
            }
			
		}
		
		
	}
	//ceiling
	public static void processCase240()
	{
		
		
		
	}
	
	
}
