package levelgen;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.util.BufferUtils;

import Configs.SettingsLibrary;
import Main.AssetLoaderManager;
import serverlevelgen.ChunkManagerUtil;

public class BackgroundTerrainGenerator {

	int[][] heightMap;
	int[][] materialMap;
	
	int chunkSize = 0;
	int maxResolution = 0;
	
	boolean needsGeomUpload = true;
	boolean needsUpdate = false;
	
	FloatBuffer vertexFloatBuffer = BufferUtils.createFloatBuffer(new Vector3f[]{});
	FloatBuffer texCoordFloatBuffer = BufferUtils.createFloatBuffer(new Vector2f[]{});
	IntBuffer indexIntBuffer = BufferUtils.createIntBuffer(new int[]{});
	
	Geometry geom;
	ChunkManager chunkManager;
	
	public BackgroundTerrainGenerator(ChunkManager ChunkManager)
	{
		chunkManager = ChunkManager;
	}
	
	public void updateMap(byte[] Data)
	{
		Object[] data = ChunkManagerUtil.unpackBackgroundData(Data);
		chunkSize = (int) data[0];
		maxResolution = (int) data[1];
		heightMap = (int[][]) data[2];
		materialMap = (int[][]) data[3];
		
		needsUpdate = true;
	}
	
	public void update(int IDX, int IDZ, int RenderDistance)
	{
		if(geom == null)
		{
			if(heightMap != null)
			{
				generateGeometry();
				needsGeomUpload = true;
			}
			else
			{
				return;
			}
		}
		Material mat = geom.getMaterial();
		
		mat.setFloat("IDX", IDX);
		mat.setFloat("IDZ", IDZ);
		mat.setFloat("Distance", RenderDistance);
		needsUpdate = false;
	}
	
	public boolean hasMap()
	{
		return heightMap != null;
	}
	
	public boolean hasGeometry()
	{
		return geom != null;
	}
	
	private Object[] GenerateBackgroundTerrain()
	{
		float texsize = 1.0f/SettingsLibrary.tileCount;
		List<Vector3f> positions = new ArrayList<Vector3f>();
		List<Vector2f> texcoords = new ArrayList<Vector2f>();
		
		float hof = 1.25f;
		int chunks = (heightMap.length)*maxResolution/chunkSize - 1;
		
		for(int i = 0; i < chunks; i++)
		{
			for(int j = 0; j < chunks; j++)
			{
				int px = i * chunkSize;
				int py = j * chunkSize;
				
				for(int countX = 0; countX < chunkSize/maxResolution; countX++)
				{
					for(int countZ = 0; countZ < chunkSize/maxResolution; countZ++)
					{
						int ix = i * chunkSize/maxResolution;
						int iy = j * chunkSize/maxResolution;
						
						int ox = maxResolution * countX;
						int oy = maxResolution * countZ;
						
						positions.add(new Vector3f(px + ox, heightMap[ix + countX][iy + countZ]-hof, py + oy));
						positions.add(new Vector3f(px + ox, heightMap[ix + countX][iy + countZ+1]-hof, py + oy + maxResolution));
						positions.add(new Vector3f(px + ox + maxResolution, heightMap[ix + countX+1][iy + countZ+1]-hof, py + oy + maxResolution));
						positions.add(new Vector3f(px + ox + maxResolution, heightMap[ix + countX+1][iy + countZ+1]-hof, py + oy + maxResolution));
						positions.add(new Vector3f(px + ox + maxResolution, heightMap[ix + countX+1][iy + countZ]-hof, py + oy));
						positions.add(new Vector3f(px + ox, heightMap[ix + countX][iy + countZ]-hof, py + oy));
						
						int material = materialMap[ix + countX][iy + countZ];
						
						float matX = texsize * ((material) / SettingsLibrary.tileCount);
				        float matY = 1.0f- (texsize * ((material) % SettingsLibrary.tileCount));
				        
				        Vector2f v5 = new Vector2f(matX, matY);
				        
						texcoords.add(v5);
						texcoords.add(v5);
						texcoords.add(v5);
						texcoords.add(v5);
						texcoords.add(v5);
						texcoords.add(v5);
					}
				}
			}
		}
		
		for(int i = 0; i < chunks; i++)
		{
			for(int j = 0; j < chunks; j++)
			{
				int px = i * chunkSize;
				int py = j * chunkSize;
				
				for(int countX = 0; countX < chunkSize/maxResolution; countX++)
				{
					for(int countZ = 0; countZ < chunkSize/maxResolution; countZ++)
					{
						int ix = i * chunkSize/maxResolution;
						int iy = j * chunkSize/maxResolution;
						
						int ox = maxResolution * countX;
						int oy = maxResolution * countZ;
						
						int val = 0;
						val+= heightMap[ix + countX][iy + countZ] <= 64 && (materialMap[ix + countX][iy + countZ] >= 6 && materialMap[ix + countX][iy + countZ] < 32) ? 1:0;
						val+= heightMap[ix + countX][iy + countZ+1] <= 64 && (materialMap[ix + countX][iy + countZ] >= 6 && materialMap[ix + countX][iy + countZ] < 32) ? 1:0;
						val+= heightMap[ix + countX+1][iy + countZ+1] <= 64 && (materialMap[ix + countX][iy + countZ] >= 6 && materialMap[ix + countX][iy + countZ] < 32) ? 1:0;
						val+= heightMap[ix + countX+1][iy + countZ] <= 64 && (materialMap[ix + countX][iy + countZ] >= 6 && materialMap[ix + countX][iy + countZ] < 32) ? 1:0;
						
						if(val > 0)
						{
							positions.add(new Vector3f(px + ox, 64.5f, py + oy));
							positions.add(new Vector3f(px + ox, 64.5f, py + oy + maxResolution));
							positions.add(new Vector3f(px + ox + maxResolution, 64.5f, py + oy + maxResolution));
							positions.add(new Vector3f(px + ox + maxResolution, 64.5f, py + oy + maxResolution));
							positions.add(new Vector3f(px + ox + maxResolution, 64.5f, py + oy));
							positions.add(new Vector3f(px + ox, 64.5f, py + oy));
							
							texcoords.add(new Vector2f(0.96875f, 0.03125f));
							texcoords.add(new Vector2f(0.96875f, 0.03125f));
							texcoords.add(new Vector2f(0.96875f, 0.03125f));
							
							texcoords.add(new Vector2f(0.96875f, 0.03125f));
							texcoords.add(new Vector2f(0.96875f, 0.03125f));
							texcoords.add(new Vector2f(0.96875f, 0.03125f));
						}
					}
				}
			}
		}
		
		return new Object[]{positions,texcoords};
	}
	
	private void generateBuffers(Object[] Data)
	{
		List<Vector3f> vectorList = (List<Vector3f>) Data[0];
		List<Vector2f> texCoordList = (List<Vector2f>) Data[1];
		
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
		for(int i = 0; i < vectorList.size(); i++)
		{
			indexBufferArray[i] = i;
		}
		
		vertexFloatBuffer = BufferUtils.createFloatBuffer(positionArray);
		texCoordFloatBuffer = BufferUtils.createFloatBuffer(texcoordArray);
		indexIntBuffer = BufferUtils.createIntBuffer(indexBufferArray);
		
	}
	
	public void generateGeometry()
	{
		generateBuffers(GenerateBackgroundTerrain());
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("TerrainBackground", someMesh);
		
		someMesh.setBuffer(Type.Position, 3, vertexFloatBuffer);
		someMesh.setBuffer(Type.TexCoord, 2, texCoordFloatBuffer);
		someMesh.setBuffer(Type.Index,    3, indexIntBuffer);
		someMesh.updateBound();
		
		someMaterial = AssetLoaderManager.getMaterial("background").clone();
		someMaterial.setFloat("TexSize", 1.0f/SettingsLibrary.tileCount);
		someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
		
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("test-low").clone());
		
		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
		someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
		someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
		
		someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		someGeometry.setQueueBucket(Bucket.Opaque);
		someGeometry.setMaterial(someMaterial);
		
		geom = null;
		geom = someGeometry;
	}
	
}
