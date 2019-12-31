package VoxelModels;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import Util.VoxelIOUtil;
import Util.VoxelUtil;
import worldGen.Cell;

public class VoxelModels
{
	//1: type
    //2: base
    //3+: extensions
    //3: second floor
    //4: third floor
    //5: west extension
    //6: east extension
    //7: north extension
    //8: south extension
	public static String[] typeNames = new String[]{"house","flat","tree"};
			
    public static List<String> modelNames = new ArrayList<String>();
    public static Map<String, Float> modelScaleLibrary = new HashMap<String, Float>();
    public static Map<String, short[][][]> modelMaterialLibrary = new HashMap<String, short[][][]>();
    
    public static Map<Integer, short[][][]> propMaterialLibrary = new HashMap<Integer, short[][][]>();
    
    public static void LoadAssets()
    {
    	loadVoxelModels();
    	loadProps();
    }
    
    public static List<Geometry> generateModelsFromFolder(String FolderName, String TextureName)
    {
    	List<Geometry> models = new ArrayList<Geometry>();
    	
    	File headFolder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/" + FolderName);
		File[] propFiles = headFolder.listFiles();
		
		for(int i = 0; i < propFiles.length; i++)
		{
			if(propFiles[i].getName().endsWith(".vxl"))
			{
				Object[] voxelData = VoxelIOUtil.importModelData(propFiles[i]);
				
				short[][][] prop = (short[][][]) voxelData[4];
				short[][][] modelMaterials = new short[prop.length][prop[0].length + 1][prop[0][0].length];
				
		    	for(int x = 0; x < prop.length; x++)
		    	{
		    		for(int y = 0; y < prop[0].length; y++)
		        	{
		    			for(int z = 0; z < prop[0][0].length; z++)
		    	    	{
		    				modelMaterials[x][y+1][z] = prop[x][y][z];
		    	    	}
		        	}
		    	}
		    	
		    	Object[] CellLists = VoxelUtil.GenerateVoxelCellList(modelMaterials, false);
			   	
		       	Object[] data = VoxelUtil.createCellData((List<Cell>)CellLists[0]);
				Object[] buffers = VoxelUtil.createBuffers(data, new Vector3f());
				
				Geometry Model = VoxelUtil.GenerateRigidBodyGeometry(FolderName + " Model: " + models.size(), (FloatBuffer)buffers[0], (FloatBuffer)buffers[1], (IntBuffer)buffers[2], (FloatBuffer)buffers[3], 0, TextureName);
				models.add(Model);
			}
		}
    	return models;
    }
    
    
    public static void loadProps()
    {
    	File headFolder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/props");
		File[] propFiles = headFolder.listFiles();
		for(int i = 0; i < propFiles.length; i++)
		{
			if(propFiles[i].getName().endsWith(".vxl"))
			{
				Object[] voxelData = VoxelIOUtil.importModelData(propFiles[i]);
				
				short[][][] prop = (short[][][]) voxelData[4];
				short[][][] modelMaterials = new short[prop.length][prop[0].length + 1][prop[0][0].length];
				
		    	for(int x = 0; x < prop.length; x++)
		    	{
		    		for(int y = 0; y < prop[0].length; y++)
		        	{
		    			for(int z = 0; z < prop[0][0].length; z++)
		    	    	{
		    				modelMaterials[x][y+1][z] = prop[x][y][z];
		    	    	}
		        	}
		    	}
				propMaterialLibrary.put(propMaterialLibrary.size(), modelMaterials);
			}
		}
    }
    
    public static void loadVoxelModels()
    {
    	File headFolder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/voxelmodels");
		File[] modelFolders = headFolder.listFiles();
		for(int i = 0; i < modelFolders.length; i++)
		{
			File[] modelSubFolder = modelFolders[i].listFiles();
			for(int j = 0; j < modelSubFolder.length; j++)
			{
				File[] modelSubSubFolder = modelSubFolder[j].listFiles();
				if(modelSubSubFolder != null)
				{
					for(int k = 0; k < modelSubSubFolder.length; k++)
					{
						if(modelSubSubFolder[k].getName().endsWith(".vxl"))
						{
							Object[] voxelData = VoxelIOUtil.importModelData(modelSubSubFolder[k]);
							
							String fileName = modelFolders[i].getName() + "_" + modelSubFolder[j].getName() + "_" + modelSubSubFolder[k].getName().substring(0, modelSubSubFolder[k].getName().indexOf("."));
							
							modelNames.add(fileName);
							modelScaleLibrary.put(fileName, (float) voxelData[0]);
							modelMaterialLibrary.put(fileName, (short[][][]) voxelData[4]);
						}
					}
				}
				else
				{
					if(modelSubFolder[j].getName().endsWith(".vxl"))
					{
						Object[] voxelData = VoxelIOUtil.importModelData(modelSubFolder[j]);
						String fileName = modelFolders[i].getName() + "_" + modelSubFolder[j].getName().substring(0, modelSubFolder[j].getName().indexOf("."));
						
						modelNames.add(fileName);
						modelScaleLibrary.put(fileName, (float) voxelData[0]);
//						ModelVoxelLibrary.put(fileName, (boolean[][][]) voxelData[4]);
						modelMaterialLibrary.put(fileName, (short[][][]) voxelData[4]);
					}
					
				}
				
			}
		}
    }
    
    
    public static Object[] GenerateModel(int ID, int Biome)
    {
    	String stringID = ID + "";
    	stringID = String.format("%010d", Integer.parseInt(stringID));
    	
    	int ModelType = Integer.parseInt(stringID.charAt(0)+""+stringID.charAt(1));
    	
    	Object[] data = null;
    	switch(ModelType)
    	{
	    	case 0:
	    	case 1:
	    		data = generateBuildingModel(ID, Biome);
	    		break;
	    	case 2:
	    		data = generateVegetationModel(ID, Biome);
	    		break;
	    	case 3:
	    		
	    		break;
    	}
    	return data;
    }
    
    
    //type id
    //biome for plant type
    //tree_biome_size
    
    
    public static Object[] generateVegetationModel(int ID, int Biome)
    {	
    	String stringID = ID + "";
    	stringID = String.format("%010d", Integer.parseInt(stringID));
    	
    	int height = Integer.parseInt(stringID.charAt(2)+"")+1;
    	int biomeDig1 =  Integer.parseInt(stringID.charAt(3)+ "")+1;
    	int biomeDig2 = Integer.parseInt(stringID.charAt(4)+ "")+1;
    	
    	//change to 2 digit biomeval
    	
    	String type = typeNames[Integer.parseInt(stringID.charAt(0)+""+stringID.charAt(1))] + "_" + biomeDig1 + "_" + biomeDig2 + "_" + height;
    	
    	short[][][] baseMaterials = modelMaterialLibrary.get(type);
    	
    	short[][][] modelMaterials = new short[baseMaterials.length][baseMaterials[0].length + 1][baseMaterials[0][0].length];
		
    	for(int i = 0; i < baseMaterials.length; i++)
    	{
    		for(int j = 0; j < baseMaterials[0].length; j++)
        	{
    			for(int k = 0; k < baseMaterials[0][0].length; k++)
    	    	{
    				modelMaterials[i][j+1][k] = baseMaterials[i][j][k];
    	    	}
        	}
    	}
    	return new Object[]{modelMaterials, GetScaleByName(type)};
    }
    
    
    public static Object[] generateSingleModel(int ID, int Biome)
    {
    	return null;
    }
    
    public static Object[] generateBuildingModel(int ID, int Biome)
    {
    	String stringID = ID+"";
    	
    	stringID = String.format("%010d", Integer.parseInt(stringID));
    	
    	int infrastructure = (Integer.parseInt(stringID.charAt(9)+""));
		
    	String type = typeNames[Integer.parseInt(stringID.charAt(0)+""+stringID.charAt(1))] + "_" + infrastructure;
    	String base = type+"_base_" + (Integer.parseInt(stringID.charAt(2)+"")+1);
    	String secondfloor = type+"_floor_2_" + (Integer.parseInt(stringID.charAt(3)+"")+1);
    	String extensionWest = type+"_extension_west_" + (Integer.parseInt(stringID.charAt(4)+"")+1);
    	String extensionEast = type+"_extension_east_" + (Integer.parseInt(stringID.charAt(5)+"")+1);
    	String extensionNorth = type+"_extension_north_" + (Integer.parseInt(stringID.charAt(6)+"")+1);
    	String extensionSouth = type+"_extension_south_" + (Integer.parseInt(stringID.charAt(7)+"")+1);
    	
    	String roof = type+"_roof_" + (Integer.parseInt(stringID.charAt(8)+"")+1);
    	
    	short[][][] baseMaterials = modelMaterialLibrary.get(base);
		Object[] materials = new Object[]{
				GetMaterialsByName(extensionWest),
				GetMaterialsByName(extensionEast),
				GetMaterialsByName(extensionNorth),
				GetMaterialsByName(extensionSouth)
			};
		
		for(int i = 0; i < 4; i++)
		{
			Object[] extensionMergeData = VoxelUtil.mergeVoxelFields(0, baseMaterials, (short[][][])materials[i]);
			baseMaterials = (short[][][])extensionMergeData[0];
		}
		Object[] extensionMergeData = VoxelUtil.mergeVoxelFields(0, baseMaterials, (short[][][])materials[1]);
		baseMaterials = (short[][][])extensionMergeData[0];
		
		if(infrastructure > 1)
    	{
    		int checksum = 0;
        	for(int i = 0; i < stringID.length(); i++)
        	{
        		checksum+= Integer.parseInt(stringID.charAt(i)+"")+1;
        	}
        	int floors = (int) Math.pow(2, checksum%infrastructure-1);
        	for(int i = 0; i < floors; i++)
        	{
        		Object[] floorMergeData = VoxelUtil.mergeVoxelFields(1, baseMaterials, GetMaterialsByName(secondfloor));
        		baseMaterials = (short[][][])floorMergeData[1];
        	}
    	}

		//additional stories
		Object[] roofMergeData = VoxelUtil.mergeVoxelFields(1, baseMaterials, GetMaterialsByName(roof));
		baseMaterials = (short[][][])roofMergeData[0];
		
//		if(infrastructure != 0)
//		{
//			int biomeVal = Biome;
//	    	int biomePer = Integer.parseInt(stringID.charAt(0)+"")+Integer.parseInt(stringID.charAt(1)+"")+ Integer.parseInt(stringID.charAt(2)+"")*Integer.parseInt(stringID.charAt(2)+"")-Integer.parseInt(stringID.charAt(2)+"") + 3;
//	    	biomePer%=10;
//	    	for(int i = 4; i >= 0; i--)
//	    	{
//	    		if(biomeVal < 5)
//	    		{
//	    			if(biomePer >= biomeMaterialDistributions[Biome][i])
//		    		{
//		    			biomeVal = biomeMaterialDistributions[Biome][i];
//		    			break;
//		    		}
//	    		}	
//	    	}
//		}
		
		int px = Biome/32;
		int py = Biome%32;
		
		int om = px * 16;
		
		for(int i = 0; i < baseMaterials.length; i++)
    	{
    		for(int j = 0; j < baseMaterials[0].length; j++)
        	{
    			for(int k = 0; k < baseMaterials[0][0].length; k++)
    	    	{
    				baseMaterials[i][j][k] = (short) (baseMaterials[i][j][k] + om);
    	    	}
        	}
    	}
		
    	short[][][] modelMaterials = new short[baseMaterials.length][baseMaterials[0].length + 1][baseMaterials[0][0].length];
		
    	for(int i = 0; i < baseMaterials.length; i++)
    	{
    		for(int j = 0; j < baseMaterials[0].length; j++)
        	{
    			for(int k = 0; k < baseMaterials[0][0].length; k++)
    	    	{
    				modelMaterials[i][j+1][k] = baseMaterials[i][j][k];
    	    	}
        	}
    	}
    	return new Object[]{modelMaterials, GetScaleByName(base)};
    }
    
    public static short[][][] getPropByID(int ID)
    {
    	return propMaterialLibrary.get(ID);
    }
    
    public static List<String> GetModelNameList(List<Integer> IDList)
    {
    	List<String> modelNames = new ArrayList<String>();
    	for(int i = 0; i < IDList.size(); i++)
    	{
    		String name = GetModelNameByIndex(IDList.get(i));
    		if(name != null)
    		{
    			modelNames.add(name);
    		}
    	}
    	return modelNames;
    }
    
    public static List<Integer> GetModelIDList(List<String> Names)
    {
    	List<Integer> modelIDList = new ArrayList<Integer>();
		for(int i = 0; i < Names.size(); i++)
		{
			int id = GetModelIndexByName(Names.get(i));
			if(id != -1)
			{
				modelIDList.add(id);
			}
		}
		return modelIDList;
    }
    
    public static boolean hasProp(int ID)
    {
    	return propMaterialLibrary.size() > ID;
    }
    
    public static String GetModelNameByIndex(int Index)
    {
    	return modelNames.get(Index);
    }
    
    public static int GetModelIndexByName(String Name)
    {
    	return modelNames.indexOf(Name);
    }
    
    public static short[][][] GetMaterialsByName(String Key)
    {
    	if(modelMaterialLibrary.get(Key)!= null)
    	{
    		return modelMaterialLibrary.get(Key);
    	}
    	return new short[1][1][1];
    }
    
    public static float GetScaleByName(String Key)
    {
    	if(modelScaleLibrary.get(Key)!= null)
    	{
    		return modelScaleLibrary.get(Key);
    	}
    	return 1.0f;
    }
    
}