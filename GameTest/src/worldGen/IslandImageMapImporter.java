package worldGen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import Console.Parser;
import VoxelGenUtil.ForestGenUtil;
import serverlevelgen.WorldSettings;

public class IslandImageMapImporter {

	public static Map<Integer, int[]> enviromentMaterialMap = new HashMap<Integer, int[]>();
	
	public static IslandMap generateMapfromImages(String Name, String HeightMapPath, String WaterHeightMapPath, String EnvironmentMapPath)
	{
		readMaterialsList();

		BufferedImage heightImage = loadMap(HeightMapPath);
		BufferedImage waterHeightImage = loadMap(WaterHeightMapPath);
		BufferedImage environmentImage = loadMap(EnvironmentMapPath);
		
		if(heightImage != null || waterHeightImage != null || environmentImage != null)
		{
			if(heightImage.getWidth() == waterHeightImage.getWidth() && heightImage.getWidth() == environmentImage.getWidth())
			{
				if( heightImage.getHeight() == waterHeightImage.getHeight() && heightImage.getHeight() == environmentImage.getHeight())
				{
					int[][] heightMap = generateHeightMap(heightImage);
					int[][] waterHeightMap = generateHeightMap(waterHeightImage);
					int[][][] environmentMap = generateColorMap(environmentImage);
					
					IslandMap map = new IslandMap(Name, environmentMap.length-1, 0, 0, false);
					map.hasGeneration = false;
					map.h = heightMap;
					map.wh = waterHeightMap;
					
					boolean[][] forestMap = generateEnvironment(map, waterHeightMap, environmentMap);
//					boolean[][] forestMap = generateVegetationMap(map, environmentMap);
//					generateWaterMaps(map, environmentMap, waterHeightMap);
//					ForestGenUtil.generateForestVegetation(map,forestMap);
					
					return map;
				}
			}
		}
		return null;
	}
	
	public static boolean[][] generateEnvironment(IslandMap Map, int[][] WaterHeightMap, int[][][] EnvironmentMap)
	{
		boolean[][] forestMap = new boolean[EnvironmentMap.length][EnvironmentMap[0].length];
		
		for(int i = 0; i < EnvironmentMap.length; i++ )
		{
			for(int j = 0; j < EnvironmentMap[0].length; j++ )
			{
				if(WaterHeightMap[i][j] > Map.h[i][j])
				{
					Map.ma[i][j] = 7;
					Map.o[i][j] = true;
				}
				else
				{
					
					Map.ma[i][j] = getColorToMaterial(EnvironmentMap[i][j]);
					
					Map.l[i][j] = true;
				}
//				forestMap[i][j] = EnvironmentMap[i][j].equals( forestColor);
			}
		}
		return forestMap;
	}
	
	
	public static int getColorToMaterial(int[] Color)
	{
		for (Map.Entry<Integer, int[]> entry : enviromentMaterialMap.entrySet())
		{
			int[] mapColor = entry.getValue();
			
			if(mapColor[0] == Color[0] && mapColor[1] == Color[1] && mapColor[2] == Color[2])
			{
				return entry.getKey();
			}
		}
		return 7;
	}
	
	public static int[][][] generateColorMap(BufferedImage Map)
	{
		int[][][] colorMap = new int[Map.getWidth()][Map.getHeight()][4];
		for(int i = 0; i < Map.getWidth(); i++)
		{
			for(int j = 0; j < Map.getHeight(); j++)
			{
				Color curRBGA = new Color(Map.getRGB(i,j),true);
				colorMap[i][j][0] = curRBGA.getRed();
				colorMap[i][j][1] = curRBGA.getGreen();
				colorMap[i][j][2] = curRBGA.getBlue();
				colorMap[i][j][3] = curRBGA.getAlpha();
			}
		}
		return colorMap;
	}
	
	public static int[][] generateHeightMap(BufferedImage Map)
	{
		int[][] heightMap = new int[Map.getWidth()][Map.getHeight()];
		for(int i = 0; i < Map.getWidth(); i++)
		{
			for(int j = 0; j < Map.getHeight(); j++)
			{
				Color curRBGA = new Color(Map.getRGB(i,j),true);
				heightMap[i][j] = curRBGA.getRed();
			}
		}
		return heightMap;
	}
	
	public static int[][] generateAdditiveHeightMap(BufferedImage Map)
	{
		int[][] heightMap = new int[Map.getWidth()][Map.getHeight()];
		
		for(int i = 0; i < Map.getWidth(); i++)
		{
			for(int j = 0; j < Map.getHeight(); j++)
			{
				Color curRBGA = new Color(Map.getRGB(i,j),true);
				heightMap[i][j] = curRBGA.getRed() + curRBGA.getGreen() + curRBGA.getBlue()+ curRBGA.getAlpha();
			}
		}
		return heightMap;
	}
	
	public static void readMaterialsList()
	{
		try 
		{
			BufferedReader script = new BufferedReader( new FileReader(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/config/environmentmaterials.cfg"));
			//material r g b a
			for(String line = script.readLine(); line != null ; line = script.readLine())
			{
               String[] bits = line.split(" ");
               enviromentMaterialMap.put( Integer.parseInt(bits[0]), new int[]{Integer.parseInt(bits[1]),Integer.parseInt(bits[2]),Integer.parseInt(bits[3]),Integer.parseInt(bits[4])});
			}
			script.close();
		} 
		catch(FileNotFoundException e) 
		{
			return;
		}
		catch(IOException e) 
		{
			return;
		}
		
	}
	
	public static BufferedImage loadMap(String FileName)
	{
		BufferedImage map = null;
		try 
		{
			map = ImageIO.read( new File(FileName));
		}
		catch (IOException e) 
		{
		}
		return map;
	}
	
}
