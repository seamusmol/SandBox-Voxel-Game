package Util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.util.BufferUtils;

import Configs.ServerSettings;
import Configs.SettingsLibrary;
import Main.AssetLoaderManager;
import VoxelModels.VoxelList;
import worldGen.Cell;

public class VoxelUtil
{
	public static void modifyMaterials(int MaterialOffset, boolean[][][] BaseVoxels, short[][][] BaseMaterials)
	{
		for(int i = 0; i < BaseVoxels.length; i++)
		{
			for(int j = 0; j < BaseVoxels[0].length; j++)
			{
				for(int k = 0; k < BaseVoxels[0][0].length; k++)
				{
					if(BaseVoxels[i][j][k])
					{
						if((BaseMaterials[i][j][k] & 0xFFFF) > 129 && (BaseMaterials[i][j][k] & 0xFFFF) < 145)
						{
							BaseMaterials[i][j][k] += MaterialOffset;
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param mergeType : 1.top,2.bottom,3.west,4.east,5.north,6.south,7.same region
	 * @param BaseVoxels
	 * @param MergeVoxels
	 * @return
	 */
	public static Object[] mergeVoxelFields(int MergeType, short[][][] BaseMaterials, short[][][] MergeMaterials)
	{
		short[][][] newMaterials = null;
	
		if(MergeType == 0)
		{
			newMaterials = new short[BaseMaterials.length][BaseMaterials[0].length][BaseMaterials[0][0].length];
		
			for(int i = 0; i < BaseMaterials.length; i++)
			{
				for(int k = 0; k < BaseMaterials[0][0].length; k++)
				{
					for(int j = 0; j < BaseMaterials[0].length; j++)
					{
						newMaterials[i][j][k] = BaseMaterials[i][j][k];	
					}
					for(int j = 0; j < MergeMaterials[0].length; j++)
					{
						if(MergeMaterials[i][j][k] != 0)
						{
							if((MergeMaterials[i][j][k]) == SettingsLibrary.deleteID)
							{
								newMaterials[i][j][k] = 0;
							}
							else if(newMaterials[i][j][k] == 0)
							{
								newMaterials[i][j][k] = MergeMaterials[i][j][k];
							}
							else if(newMaterials[i][j][k] != 0 && BaseMaterials[i][j][k] != MergeMaterials[i][j][k])
							{
								newMaterials[i][j][k] = MergeMaterials[i][j][k];
							}
						}
					}
				}
			}
		}
		else if(MergeType == 1)
		{
			int height = BaseMaterials[0].length - ServerSettings.houseFloorHeight + MergeMaterials[0].length;
			
			newMaterials = new short[BaseMaterials.length][height][BaseMaterials[0][0].length];
		
			for(int i = 0; i < newMaterials.length; i++)
			{
				for(int k = 0; k < newMaterials[0][0].length; k++)
				{
					for(int j = 0; j < BaseMaterials[0].length; j++)
					{
						newMaterials[i][j][k] = BaseMaterials[i][j][k];	
					}
					
					for(int j = 0; j < MergeMaterials[0].length; j++)
					{
						int py = BaseMaterials[0].length - ServerSettings.houseFloorHeight + j;
						
						if(MergeMaterials[i][j][k] != 0)
						{
							if((MergeMaterials[i][j][k]) == SettingsLibrary.deleteID)
							{
								newMaterials[i][j][k] = 0;
							}
							else if(newMaterials[i][j][k] == 0)
							{
								newMaterials[i][j][k] = MergeMaterials[i][j][k];
							}
							else if(newMaterials[i][j][k] != 0 && BaseMaterials[i][j][k] != MergeMaterials[i][j][k])
							{
								newMaterials[i][j][k] = MergeMaterials[i][j][k];
							}
						}
					}
				}
			}
		}
		return new Object[]{newMaterials};
	}
	
    public static Object[] GenerateVoxelCellList(short[][][] materials, boolean splitFields)
    {
    	if(splitFields)
    	{
    		Object[] SortedFields = SplitFields(materials);
    		List<Cell> CellList = GenerateVoxelValues((short[][][]) SortedFields[0]);
    		List<Cell> TransLucentCellList = GenerateVoxelValues((short[][][]) SortedFields[1]);
    		Object[] Lists = {CellList, TransLucentCellList};
    		return Lists;
    	}
    	else
    	{
    		List<Cell> CellList = GenerateVoxelValues(materials);
    		Object[] List = {CellList};
    		return List;
    	}
    }
    
    public static Object[] SplitFields(short[][][] Materials)
    {
        short[][][] SolidMaterials = new short[Materials.length][ Materials[0].length][ Materials[0][0].length];
        short[][][] TranslucentMaterials = new short[Materials.length][ Materials[0].length][ Materials[0][0].length];

//        int transTolerance = 224;
        
        for (int i = 0; i < Materials.length; i++)
        {
            for (int j = 0; j < Materials[0].length; j++)
            {
                for (int k = 0; k < Materials[0][0].length; k++)
                {
                	if(Materials[i][j][k] != 0)
                	{
                		if (Materials[i][j][k] < SettingsLibrary.transRegion)
	                    {
	                        SolidMaterials[i][j][k] = Materials[i][j][k];
	                    }
	                    else if(Materials[i][j][k] >= SettingsLibrary.transRegion)
	                    {
	                        TranslucentMaterials[i][j][k] = Materials[i][j][k];
	                    }
                	}
                }
            }
        }
        Object[] Data = new Object[2];
        Data[0] = SolidMaterials;
        Data[1] = TranslucentMaterials;
        return Data;
    }
    
    //upload 1d Material Array
    //upload dimensions
    //upload vertex buffer
    //upload index buffer
    public static List<Cell> GenerateVoxelValues(short[][][] Materials)
    {
        List<Cell> cellList = new ArrayList<Cell>();
        for (int i = 0; i < Materials.length - 1; i++)
        {
            for (int j = 0; j < Materials[0].length - 1; j++)
            {
                for (int k = 0; k < Materials[0][0].length - 1; k++)
                {
                    int binaryValue = 0;
                    binaryValue += Materials[i][ j + 1][ k + 1] != 0 ? 128 : 0;
                    binaryValue += Materials[i + 1][ j + 1][ k + 1] != 0 ? 64 : 0;
                    binaryValue += Materials[i + 1][ j + 1][ k] != 0 ? 32 : 0;
                    binaryValue += Materials[i][ j + 1][ k] != 0 ? 16 : 0;
                    binaryValue += Materials[i][ j][ k + 1] != 0 ? 8 : 0;
                    binaryValue += Materials[i + 1][ j][ k + 1] != 0 ? 4 : 0;
                    binaryValue += Materials[i + 1][ j][ k] != 0 ? 2 : 0;
                    binaryValue += Materials[i][ j][ k] != 0 ? 1 : 0;

                    if (binaryValue != 255 && binaryValue != 0)
                    {
                    	int vals[] = {
                            (Materials[i][j + 1][k + 1]) ,
                            (Materials[i + 1][j + 1][k + 1]),
                            (Materials[i + 1][j + 1][k]),
                            (Materials[i][j + 1][k]),
                            (Materials[i][j][k + 1]),
                            (Materials[i + 1][j][k + 1]),
                            (Materials[i + 1][j][k]),
                            (Materials[i][j][k])
                            };
                    	
                    	int val = MathUtil.calculateMost(vals);
                    	//define rotation
                        cellList.add(new Cell(binaryValue, i, j, k, val, false));
                    }
                }
            }
        }
        cellList = GreedyMesher.greedify(cellList);
        
        for(int i = 0; i < cellList.size();i++)
        {
        	if(cellList.get(i).sz > 1)
        	{
        		cellList.get(i).rot = true;
        		continue;
        	}
        	
    		if(cellList.get(i).value == 15)
        	{
    			int m = cellList.get(i).m;
        		int px = cellList.get(i).px;
        		int pz = cellList.get(i).pz;
        		
        		for(int j = 0; j < cellList.size(); j++)
        		{
        			if(px == cellList.get(j).px-1 && pz == cellList.get(j).pz)
        			{
        				//rot = true
        				if(m == cellList.get(j).m)
        				{
        					//cellList.get(j).rot = true;
	        				cellList.get(i).rot = true;
	        				break;
        				}
        			}
        			if(px == cellList.get(j).px+1 && pz == cellList.get(j).pz)
        			{
        				//rot = true
        				if(m == cellList.get(j).m)
        				{
        					//cellList.get(j).rot = true;
	        				cellList.get(i).rot = true;
	        				break;
        				}
        			}
        			if(px == cellList.get(j).pz-1 && px == cellList.get(j).px)
        			{
        				//rot = false
        				if(m == cellList.get(j).m)
        				{
        					//cellList.get(j).rot = false;
	        				cellList.get(i).rot = false;
	        				break;
        				}
        			}
        			if(px == cellList.get(j).pz+1 && px == cellList.get(j).px)
        			{
        				//rot = false
        				if(m == cellList.get(j).m)
        				{
        					//cellList.get(j).rot = false;
	        				cellList.get(i).rot = false;
	        				break;
        				}
        			}
        		}
        	}
        }
        return cellList;
    }

    //TODO 
    //set all TexCoords to floored tex value
    public static Object[] createCellData(List<Cell> cellList)
    {
        List<Vector3f> PointList = new ArrayList<Vector3f>();
        List<Vector2f> texCoordList = new ArrayList<Vector2f>();
        int[][] indices = VoxelList.getList();
        
        float texsize = 1.0f/SettingsLibrary.tileCount;
        for (int i = 0; i < cellList.size(); i++)
        {
            int value = cellList.get(i).value;
            float px = cellList.get(i).px;
            float py = cellList.get(i).py;
            float pz = cellList.get(i).pz;
            int material = cellList.get(i).m;
            
            
            float sizeX = cellList.get(i).sx;
            float sizeY = cellList.get(i).sy;
            float sizeZ = cellList.get(i).sz;

            Vector3f[] voxelVertices = new Vector3f[12];
            voxelVertices[0] = new Vector3f(sizeX / 2f, 0, 0);
            voxelVertices[1] = new Vector3f(sizeX, 0, sizeZ / 2f);
            voxelVertices[2] = new Vector3f(sizeX / 2f, 0, sizeZ);
            voxelVertices[3] = new Vector3f(0, 0, sizeZ / 2f);

            voxelVertices[4] = new Vector3f(sizeX / 2f, sizeY, 0);
            voxelVertices[5] = new Vector3f(sizeX, sizeY, sizeZ / 2f);
            voxelVertices[6] = new Vector3f(sizeX / 2f, sizeY, sizeZ);
            voxelVertices[7] = new Vector3f(0, sizeY, sizeZ / 2);

            voxelVertices[8] = new Vector3f(0, sizeY / 2f, 0);
            voxelVertices[9] = new Vector3f(sizeX, sizeY / 2f, 0);
            voxelVertices[10] = new Vector3f(sizeX, sizeY / 2f, sizeZ);
            voxelVertices[11] = new Vector3f(0, sizeY / 2f, sizeZ);
            
            int count = 0;
            
	        float matX = texsize * ((material) / SettingsLibrary.tileCount);
	        float matY = 1.0f- (texsize * ((material) % SettingsLibrary.tileCount));
	        
            for (int indexCount = 0; indexCount < indices[value].length; indexCount+=3)
            {
            	 Vector3f p1 = new Vector3f(voxelVertices[indices[value][indexCount]].x, voxelVertices[indices[value][indexCount]].y, voxelVertices[indices[value][indexCount]].z);
                 Vector3f p2 = new Vector3f(voxelVertices[indices[value][indexCount + 1]].x, voxelVertices[indices[value][indexCount + 1]].y, voxelVertices[indices[value][indexCount + 1]].z);
                 Vector3f p3 = new Vector3f(voxelVertices[indices[value][indexCount + 2]].x, voxelVertices[indices[value][indexCount + 2]].y, voxelVertices[indices[value][indexCount + 2]].z);
                 
                 p1.x += px;
                 p2.x += px;
                 p3.x += px;
                 p1.y += py;
                 p2.y += py;
                 p3.y += py;
                 p1.z += pz;
                 p2.z += pz;
                 p3.z += pz;
                 
                 PointList.add(p1);
                 PointList.add(p2);
                 PointList.add(p3);
                 
                 texCoordList.add(new Vector2f(matX,matY));
                 texCoordList.add(new Vector2f(matX,matY));
                 texCoordList.add(new Vector2f(matX,matY));
            }
        }
        Object[] data = new Object[2];
        data[0] = PointList;
        data[1] = texCoordList;
        return data;
    }
    
    public static Object[] createCellData(List<Cell> cellList, Vector3f OffSet)
    {
        List<Vector3f> PointList = new ArrayList<Vector3f>();
        List<Vector2f> texCoordList = new ArrayList<Vector2f>();
        int[][] indices = VoxelList.getList();
        
        float texsize = 1.0f/SettingsLibrary.tileCount;
        for (int i = 0; i < cellList.size(); i++)
        {
            int value = cellList.get(i).value;
            float px = cellList.get(i).px;
            float py = cellList.get(i).py;
            float pz = cellList.get(i).pz;
            int material = cellList.get(i).m;
            
            
            float sizeX = cellList.get(i).sx;
            float sizeY = cellList.get(i).sy;
            float sizeZ = cellList.get(i).sz;

            Vector3f[] voxelVertices = new Vector3f[12];
            voxelVertices[0] = new Vector3f(sizeX / 2f, 0, 0);
            voxelVertices[1] = new Vector3f(sizeX, 0, sizeZ / 2f);
            voxelVertices[2] = new Vector3f(sizeX / 2f, 0, sizeZ);
            voxelVertices[3] = new Vector3f(0, 0, sizeZ / 2f);

            voxelVertices[4] = new Vector3f(sizeX / 2f, sizeY, 0);
            voxelVertices[5] = new Vector3f(sizeX, sizeY, sizeZ / 2f);
            voxelVertices[6] = new Vector3f(sizeX / 2f, sizeY, sizeZ);
            voxelVertices[7] = new Vector3f(0, sizeY, sizeZ / 2);

            voxelVertices[8] = new Vector3f(0, sizeY / 2f, 0);
            voxelVertices[9] = new Vector3f(sizeX, sizeY / 2f, 0);
            voxelVertices[10] = new Vector3f(sizeX, sizeY / 2f, sizeZ);
            voxelVertices[11] = new Vector3f(0, sizeY / 2f, sizeZ);
            
            int count = 0;
            
	        float matX = texsize * ((material) / SettingsLibrary.tileCount);
	        float matY = 1.0f- (texsize * ((material) % SettingsLibrary.tileCount));
	        
            for (int indexCount = 0; indexCount < indices[value].length; indexCount+=3)
            {
            	 Vector3f p1 = new Vector3f(voxelVertices[indices[value][indexCount]].x, voxelVertices[indices[value][indexCount]].y, voxelVertices[indices[value][indexCount]].z);
                 Vector3f p2 = new Vector3f(voxelVertices[indices[value][indexCount + 1]].x, voxelVertices[indices[value][indexCount + 1]].y, voxelVertices[indices[value][indexCount + 1]].z);
                 Vector3f p3 = new Vector3f(voxelVertices[indices[value][indexCount + 2]].x, voxelVertices[indices[value][indexCount + 2]].y, voxelVertices[indices[value][indexCount + 2]].z);
                 
                 p1.x += px;
                 p2.x += px;
                 p3.x += px;
                 p1.y += py;
                 p2.y += py;
                 p3.y += py;
                 p1.z += pz;
                 p2.z += pz;
                 p3.z += pz;
                 
                 PointList.add(p1.add(OffSet));
                 PointList.add(p2.add(OffSet));
                 PointList.add(p3.add(OffSet));
                 
                 texCoordList.add(new Vector2f(matX,matY));
                 texCoordList.add(new Vector2f(matX,matY));
                 texCoordList.add(new Vector2f(matX,matY));
            }
        }
        Object[] data = new Object[2];
        data[0] = PointList;
        data[1] = texCoordList;
        return data;
    }
    
    
    public static Object[] createBuffers(Object[] CellData)
    {
		List<Vector3f> vectorList = new ArrayList<Vector3f>();
		List<Vector2f> texCoordList = new ArrayList<Vector2f>();
		
		vectorList.addAll((ArrayList<Vector3f>) CellData[0]);
		texCoordList.addAll((ArrayList<Vector2f>) CellData[1]);
		
		Vector3f[] positionArray = new Vector3f[vectorList.size()];
		Vector2f[] texcoordArray = new Vector2f[texCoordList.size()];
		
		for(int i = 0; i < vectorList.size(); i++)
		{
			positionArray[i] = vectorList.get(i);
		}
		for(int i = 0; i < texCoordList.size(); i++)
		{
			texcoordArray[i] = texCoordList.get(i);
		}
		
		int[] indexBufferArray = new int[vectorList.size()];
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < vectorList.size(); i++)
		{
			indexBufferArray[i] = i;
			indices.add(i);
		}
		
		Vector3f[] normals = generateNormalArray(vectorList);
		Object[] buffers = new Object[4];
		
		buffers[0] = BufferUtils.createFloatBuffer(positionArray);
		buffers[1] = BufferUtils.createFloatBuffer(texcoordArray);
		buffers[2] = BufferUtils.createIntBuffer(indexBufferArray);
		buffers[3] = BufferUtils.createFloatBuffer(normals);
    	
    	return buffers;
    }
    
    public static Object[] createBuffers(Object[] CellData, Vector3f PositionOffset)
    {
		List<Vector3f> vectorList = new ArrayList<Vector3f>();
		List<Vector2f> texCoordList = new ArrayList<Vector2f>();
		
		vectorList.addAll((ArrayList<Vector3f>) CellData[0]);
		texCoordList.addAll((ArrayList<Vector2f>) CellData[1]);
		
		Vector3f[] positionArray = new Vector3f[vectorList.size()];
		Vector2f[] texcoordArray = new Vector2f[texCoordList.size()];
		
		for(int i = 0; i < vectorList.size(); i++)
		{
			positionArray[i] = vectorList.get(i).add(PositionOffset);
		}
		for(int i = 0; i < texCoordList.size(); i++)
		{
			texcoordArray[i] = texCoordList.get(i);
		}
		
		List<Integer> indexList = new ArrayList<Integer>();
		int[] indexBufferArray = new int[vectorList.size()];
		for(int i = 0; i < vectorList.size(); i++)
		{
			indexBufferArray[i] = i;
			indexList.add(i);
		}
		
		Vector3f[] normals = generateNormalArray(vectorList);
		Object[] buffers = new Object[4];
		
		buffers[0] = BufferUtils.createFloatBuffer(positionArray);
		buffers[1] = BufferUtils.createFloatBuffer(texcoordArray);
		buffers[2] = BufferUtils.createIntBuffer(indexBufferArray);
		buffers[3] = BufferUtils.createFloatBuffer(normals);
		
    	return buffers;
    }
    
    public static Vector3f[] generateNormalArray(List<Vector3f> Vertices)
    {
    	List<Vector3f> Normals = new ArrayList<Vector3f>();
    	for(int i = 0; i < Vertices.size(); i+=3)
    	{
    		Vector3f p1 = Vertices.get(i);
    		Vector3f p2 = Vertices.get(i+1);
    		Vector3f p3 = Vertices.get(i+2);
    		
    		Vector3f pv1 = p2.subtract(p1).cross(p3.subtract(p1)).normalize();
    		Vector3f pv2 = p3.subtract(p2).cross(p1.subtract(p2)).normalize();
    		Vector3f pv3 = p1.subtract(p3).cross(p2.subtract(p3)).normalize();
    		
    		Normals.add(pv1);
    		Normals.add(pv2);
    		Normals.add(pv3);
    		
    	}
    	Vector3f[] normalArray = new Vector3f[Normals.size()];
    	for(int i = 0; i < normalArray.length; i++)
    	{
    		normalArray[i] = Normals.get(i);
    	}
    	return normalArray;
    }

    public static List<Vector3f> generateNormalList(List<Vector3f> Vertices)
    {
    	List<Vector3f> Normals = new ArrayList<Vector3f>();
    	for(int i = 0; i < Vertices.size(); i+=3)
    	{
    		Vector3f p1 = Vertices.get(i);
    		Vector3f p2 = Vertices.get(i+1);
    		Vector3f p3 = Vertices.get(i+2);
    		
    		Vector3f pv1 = p2.subtract(p1).cross(p3.subtract(p1)).normalize();
    		Vector3f pv2 = p3.subtract(p2).cross(p1.subtract(p2)).normalize();
    		Vector3f pv3 = p1.subtract(p3).cross(p2.subtract(p3)).normalize();
    		
    		Normals.add(pv1);
    		Normals.add(pv2);
    		Normals.add(pv3);
    		
    	}
    	return Normals;
    }
    
    public static Geometry GenerateGeometry(int chunkSize, int ChunkIDX, int ChunkIDZ, FloatBuffer VertexBuffer, FloatBuffer TexCoordBuffer, IntBuffer IndexBuffer, FloatBuffer NormalBuffer, int RenderType, String TextureName)
    {
    	Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("Chunk: " + ChunkIDX + "-" + ChunkIDZ, someMesh);
		
		switch(RenderType)
		{
			case 0:
				//server
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				//someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.updateBound();
				someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md").clone();
				someGeometry.setMaterial(someMaterial);
				someGeometry.setLocalTranslation(ChunkIDX * chunkSize, 0, ChunkIDZ * chunkSize);
				
				break;
			case 1:
				//test material
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.setBuffer(Type.Normal,    3, NormalBuffer);
				someMesh.updateBound();
				someMaterial = AssetLoaderManager.getMaterial("test").clone();
				someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
				someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
				someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName).clone());
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
				someGeometry.setMaterial(someMaterial);
				someGeometry.setLocalTranslation(ChunkIDX * chunkSize, 0, ChunkIDZ * chunkSize);
				someGeometry.setQueueBucket(Bucket.Opaque);
				
				break;
			case 2:
				//test material
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.setBuffer(Type.Normal,    3, NormalBuffer);
				someMesh.updateBound();
				someMaterial = AssetLoaderManager.getMaterial("test").clone();
				someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
				someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
				someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName).clone());
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
				someGeometry.setMaterial(someMaterial);
				someGeometry.setLocalTranslation(ChunkIDX * chunkSize, 0, ChunkIDZ * chunkSize);
				someGeometry.setQueueBucket(Bucket.Transparent);
				break;
		}
		
		return someGeometry;
	}
    
    public static Geometry GenerateRigidBodyGeometry(String Name, FloatBuffer VertexBuffer, FloatBuffer TexCoordBuffer, IntBuffer IndexBuffer, FloatBuffer NormalBuffer, int RenderType, String TextureName)
    {
    	Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(Name, someMesh);
		
		switch(RenderType)
		{
			case 0:
				//server
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.updateBound();
				someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md").clone();
				someMaterial.getAdditionalRenderState().setWireframe(true);
				someMaterial.setColor("Color", ColorRGBA.Black);
				someGeometry.setMaterial(someMaterial);
				
				break;
			case 1:
				
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.updateBound();
				someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md").clone();
				someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName));
				someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
				someGeometry.setMaterial(someMaterial);
//				someGeometry.setQueueBucket(Bucket.Opaque);
				
			case 2:
				//test material
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.setBuffer(Type.Normal,    3, NormalBuffer);
				someMesh.updateBound();
				someMaterial = AssetLoaderManager.getMaterial("test").clone();
				someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
				someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
				
//				someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
				someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
				someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName));
				someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
				someGeometry.setMaterial(someMaterial);
				
				someGeometry.setQueueBucket(Bucket.Opaque);
				
				break;
	    }
		return someGeometry;
    }
    

    public static boolean[] getVoxelValue(short[][][] materials, int x, int y,int z)
    {
        boolean[] result = new boolean[8];
        if (x >= materials.length - 1 || y >= materials[0].length - 1 || z >= materials[0][0].length- 1)
        {
            return result;
        }
        else if (y < 0)
        {
        	boolean[] vals = { true, true, true, true, true, true, true,true };
            return vals;
        }
        else
        {
            //Debug.WriteLine(x + "-" + y + "-" + z);
            result[7] = materials[x][ y + 1][ z + 1] != 0;
            result[6] = materials[x + 1][ y + 1][ z + 1] != 0;
            result[5] = materials[x + 1][ y + 1][ z] != 0;
            result[4] = materials[x][ y + 1][ z] != 0;
            result[3] = materials[x][ y][ z + 1] != 0;
            result[2] = materials[x + 1][ y][ z + 1] != 0;
            result[1] = materials[x + 1][ y][ z] != 0;
            result[0] = materials[x][ y][ z] != 0;
        }
        return result;
    }

}
