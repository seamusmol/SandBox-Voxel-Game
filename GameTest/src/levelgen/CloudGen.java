package levelgen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;

import Configs.ServerSettings;
import Configs.SettingsLibrary;
import Input.PlayerCamera;
import Main.AssetLoaderManager;
import Util.MathUtil;
import Util.VoxelUtil;
import worldGen.Cell;
import worldGen.IslandMap;

public class CloudGen {

	/*
	 * 
	 * Cloud Map(High Density)
	 * Cloud BlendMap(low Density)
	 * 
	 * sample square region of cloudMap, having cloud coordinate wrapping
	 * 
	 * use fibnoise to determine sample region movement
	 * use fibnoise to determine cloud density
	 * 
	 * use last position+currentposition to determine sample region square rotation
	 * 
	 */
	
	int wx;
	int chunkSize;
	boolean needsUpdate = true;
//	boolean needsGeomUpload = true;
	
	int[][] densityA;
	int[][] densityB;
	int[][] densityC;
	int[][] densityD;
	
	Texture2D cloudMap;
	
	Geometry geom;
	ChunkManager chunkManager;
	PlayerCamera playerCamera;
	
	FloatBuffer vertexFloatBuffer = BufferUtils.createFloatBuffer(new Vector3f[]{});
	FloatBuffer texCoordFloatBuffer = BufferUtils.createFloatBuffer(new Vector2f[]{});
	IntBuffer indexIntBuffer = BufferUtils.createIntBuffer(new int[]{});
	
	public CloudGen(ChunkManager ChunkManager, PlayerCamera PlayerCamera)
	{
		chunkManager = ChunkManager;
		playerCamera = PlayerCamera;
	}
	
	public void update(Vector3f Position)
	{
		if(geom == null)
		{
			if(chunkManager.backgroundGen.heightMap != null)
			{
				chunkSize = chunkManager.backgroundGen.chunkSize;
				wx = chunkManager.backgroundGen.heightMap.length * chunkManager.backgroundGen.maxResolution;
				
				generateData();
				
				generateGeometry();
				playerCamera.attachChild(geom, "BackgroundNode");
//				playerCamera.attachToRoot(geom);
//				needsGeomUpload = true;
			}
			else
			{
				return;
			}
		}
		Material mat = geom.getMaterial();
		mat.setParam("X", VarType.Int, Math.round(Position.x));
		mat.setParam("Z", VarType.Int, Math.round(Position.z));
		
		geom.setCullHint(CullHint.Never);
		
		needsUpdate = false;
	}
	
	private void generateBuffers(Object[] Data)
	{
		List<Vector3f> vectorList = (List<Vector3f>) Data[0];
		List<Integer> indices = (List<Integer>) Data[1];
		
		Vector3f[] positionArray = new Vector3f[vectorList.size()];
		
		for(int i = 0; i < vectorList.size(); i++)
		{
			positionArray[i] = vectorList.get(i);
		}
		
		int[] indexBufferArray = new int[indices.size()];
		for(int i = 0; i < indices.size(); i++)
		{
			indexBufferArray[i] = indices.get(i);
		}
		
		vertexFloatBuffer = BufferUtils.createFloatBuffer(positionArray);
		indexIntBuffer = BufferUtils.createIntBuffer(indexBufferArray);
		
		//generate texture2d
		
		//bufferedImage
		BufferedImage cloudImage = new BufferedImage(densityA.length, densityA[0].length, 1);
		for(int i = 0; i < densityA.length; i++)
		{
			for(int j = 0; j < densityA[0].length; j++)
			{
				Color testColor = new Color(densityA[i][j], densityB[i][j], densityC[i][j], densityD[i][j]);
				cloudImage.setRGB(i, j, testColor.getRGB());
			}
		}
		AWTLoader loader = new AWTLoader();
		cloudMap = (Texture2D) AssetLoaderManager.getTexture("test").clone();
		cloudMap.setImage(loader.load(cloudImage, false));

	}
	
	public boolean hasGeometry()
	{
		return geom != null;
	}
	
	public void generateGeometry()
	{
		generateBuffers(generateCloudMesh());
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("Clouds", someMesh);
		
		someMesh.setBuffer(Type.Position, 3, vertexFloatBuffer);
		someMesh.setBuffer(Type.Index,    3, indexIntBuffer);
		someMesh.updateBound();
		
		someMaterial = AssetLoaderManager.getMaterial("clouds").clone();
		
//		someMaterial.getTexturePaaaram("ColorMap").getTextureValue().setMinFilter(MinFilter.Trilinear);
//		someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
		
//		someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//		someGeometry.setQueueBucket(Bucket.Opaque);
		someGeometry.setMaterial(someMaterial);
		
		geom = someGeometry;
		geom.setCullHint(CullHint.Never);
		
		someMaterial.setParam("CloudMapSize", VarType.Int, wx+1);
		someMaterial.setParam("BufferSize", VarType.Int, geom.getMesh().getVertexCount()/2);
		someMaterial.setTexture("CloudMap", cloudMap);
		
	}
	
	public void generateData()
	{
		int sx = 321;
		int sy = 321;
		
		int resolution = 32;
    	int cloudTolerance = 75;
    	int size = 64;
    	
    	int[][] n1 = MathUtil.generateFibonocciNumbers2D(sx * sx + 2, sy * sy + 16, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	int[][] n2 = MathUtil.generateFibonocciNumbers2D(sx * sx + 4, sy * sy + 24, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	int[][] n3 = MathUtil.generateFibonocciNumbers2D(sx * sx + 6, sy * sy + 32, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	int[][] n4 = MathUtil.generateFibonocciNumbers2D(sx * sx + 8, sy * sy + 40, wx/resolution+1, wx/resolution+1, (int)Math.log(size)+1);
    	
    	for(int i = 0; i < n1.length; i++)
    	{
    		for(int j = 0; j < n1[0].length; j++)
        	{
    			n1[i][j]%= 100;
    			n2[i][j]%= 100;
    			n3[i][j]%= 100;
    			n4[i][j]%= 100;
    			
    			n1[i][j] = n1[i][j] < cloudTolerance ? 0:n1[i][j];
    			n2[i][j] = n2[i][j] < cloudTolerance ? 0:n2[i][j];
    			n3[i][j] = n3[i][j] < cloudTolerance ? 0:n3[i][j];
    			n4[i][j] = n4[i][j] < cloudTolerance ? 0:n4[i][j];
        	}
    	}
		
    	int cloudRangeTolerance = 20;
    	
		densityA = MathUtil.scaleArray(n1, resolution);
		densityB = MathUtil.scaleArray(n2, resolution);
		densityC = MathUtil.scaleArray(n3, resolution);
		densityD = MathUtil.scaleArray(n4, resolution);
		
    	for(int i = 0; i < densityA.length; i++)
    	{
    		for(int j = 0; j < densityA[0].length; j++)
        	{
    			densityA[i][j] = densityA[i][j] < cloudRangeTolerance ? 0: densityA[i][j];
    			densityB[i][j] = densityB[i][j] < cloudRangeTolerance ? 0: densityB[i][j];
    			densityC[i][j] = densityC[i][j] < cloudRangeTolerance ? 0: densityC[i][j];
    			densityD[i][j] = densityD[i][j] < cloudRangeTolerance ? 0: densityD[i][j];
        	}
    	}
	}
	
	//TODO
	//add LOD levels
	public Object[] generateCloudMesh()
	{
		int renderSize = SettingsLibrary.cloudRenderDistance * SettingsLibrary.chunkSize;
		
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		for(int i = 0; i < renderSize; i++)
		{
			for(int j = 0; j < renderSize; j++)
			{
				vertices.add(new Vector3f(i - renderSize/2, 0,j - renderSize/2));
			}
		}
		for(int i = 0; i < renderSize; i++)
		{
			for(int j = 0; j < renderSize; j++)
			{
				vertices.add(new Vector3f(i - renderSize/2, 0,j - renderSize/2));
			}
		}
		int offset = vertices.size()/2;
		for(int i = 0; i < renderSize-1; i++)
		{
			for(int j = 0; j < renderSize-1; j++)
			{
				//top first
				indices.add(offset + i*renderSize + j);
				indices.add(offset + i*renderSize + j + 1);
				indices.add(offset + i*renderSize + j + renderSize + 1);
				//top second
				indices.add(offset + i*renderSize + j + renderSize + 1);
				indices.add(offset + i*renderSize + j + renderSize);
				indices.add(offset + i*renderSize + j);
				//bottom first
				indices.add(i*renderSize + j + renderSize + 1);
				indices.add(i*renderSize + j + 1);
				indices.add(i*renderSize + j);
				//bottom second
				indices.add(i*renderSize + j);
				indices.add(i*renderSize + j + renderSize);
				indices.add(i*renderSize + j + renderSize + 1);
			}
		}
		return new Object[]{vertices,indices};
	}
}
