package Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.TextureArray;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;

import Configs.SettingsLibrary;
import com.jme3.asset.plugins.UrlLocator;


public class AssetLoaderManager{

	static Map<String, Material> materialStorage = new HashMap<String, Material>();
	static Map<String, Texture> textureStorage = new HashMap<String, Texture>();
	static Map<String, TextureArray> textureArrayStorage = new HashMap<String, TextureArray>();
	
	static Map<String, Object[]> modelDataStorage = new HashMap<String, Object[]>();
	
	static Map<String, int[][]> storeImageArrays = new HashMap<String, int[][]>();
	
	static AssetManager assetManager;
	
	static String[] heightMaps;
	
	public AssetLoaderManager(AppStateManager StateManager, Main App)
	{
		if(textureStorage.size() == 0)
		{
			Main main = (Main)App;
			
			assetManager = App.getAssetManager();
			
			loadMaterials();
			loadModels();
			loadTextures(main);
			//temporary
		}
	}
	
	public AssetEventListener asl = new AssetEventListener() {
		public void assetLoaded(AssetKey key)
		{
		}
	
		public void assetRequested(AssetKey key) {
		    if (key.getExtension().equals("png") || key.getExtension().equals("jpg") || key.getExtension().equals("dds")) 
		    {
		        TextureKey tkey = (TextureKey) key;
		        tkey.setAnisotropy(SettingsLibrary.anistropicFactor);
		    }
		}
	
		public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) 
		{
		}
	};
	
	private void loadTextures(Main Main)
	{
		File folder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/gfx");
		File[] listOfFiles = folder.listFiles();
		assetManager.registerLocator(folder.getAbsolutePath(), FileLocator.class);
		
		AWTLoader loader = new AWTLoader();
		
		for(int i = 0; i < listOfFiles.length; i++)
		{
			if(listOfFiles[i].getName().startsWith("map_"))
			{
				
			}
			else if(listOfFiles[i].getName().endsWith(".png"))
			{
				BufferedImage bm = null;
				try 
				{
					bm = ImageIO.read(listOfFiles[i]);
				    Image load = loader.load(bm, true);
				    Texture tex = new Texture2D();
				    tex.setImage(load);
				    textureStorage.put(listOfFiles[i].getName().replace(".png", ""),tex);
				} 
				catch (IOException e)
				{
				}
			}
		}
	}
	
	private void loadModels()
	{
            textureStorage = new HashMap<String,Texture>();
            File folder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/models");
            File[] listOfFiles = folder.listFiles();
            assetManager.registerLocator(folder.getAbsolutePath(), FileLocator.class);

            for(int i = 0; i < listOfFiles.length; i++)
            {
                Spatial someModel = (Spatial) assetManager.loadModel(listOfFiles[i].getName());
                Geometry someGeometry = (Geometry) someModel;

                Object[] modelData = new Object[3];

                modelData[0] = BufferUtils.getVector3Array(someGeometry.getMesh().getFloatBuffer(Type.Position));
                modelData[1] = BufferUtils.getVector2Array(someGeometry.getMesh().getFloatBuffer(Type.TexCoord));

                int[] indices =  new int[someGeometry.getMesh().getIndexBuffer().size()];

                for(int k = 0; k < someGeometry.getMesh().getIndexBuffer().size(); k++)
                {
                        //System.out.println(someGeometry.getMesh().getIndexBuffer().get(k));
                        indices[k] = someGeometry.getMesh().getIndexBuffer().get(k);
                }
                modelData[2] = indices;
                modelDataStorage.put(listOfFiles[i].getName().replace(".obj", ""), modelData);
            }
            }

            private void loadMaterials()
            {
                materialStorage.put("default", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
                materialStorage.put("background", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/background.j3md"));
                materialStorage.put("clouds", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/clouds.j3md"));
                materialStorage.put("instancetest", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/instancetest.j3md"));
                materialStorage.put("test", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/test.j3md"));

    //        File headFolder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/shaders");
    //        File[] modelFolders = headFolder.listFiles();
    //
    //        assetManager.registerLocator(headFolder.getAbsolutePath(), FileLocator.class);
    //
    //        for(int i = 0; i < modelFolders.length; i++)
    //        {
    //            if(modelFolders[i].getName().endsWith(".j3md"))
    //            {
    //                materialStorage.put(modelFolders[i].getName().replaceAll(".j3md", ""), new Material(AssetLoaderManager.getAssetManager(), modelFolders[i].getName()));
    //            }
    //        }   
	}
        
	public static Object[] getModelData(String Key)
	{
		return modelDataStorage.get(Key);
	}
	
	public static Material getMaterial(String Key)
	{
		return materialStorage.get(Key);
		
	}
	
	public static Texture getTexture(String Key)
	{
		return textureStorage.get(Key);
	}
	
	public static String[] getHeightMaps()
	{
		return heightMaps;
	}
	
	public static TextureArray getTextureArray(String Key)
	{
		return textureArrayStorage.get(Key);
	}
	
	public static AssetManager getAssetManager()
	{
		return assetManager;
	}
	
}
