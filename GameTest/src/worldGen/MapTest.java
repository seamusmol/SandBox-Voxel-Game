package worldGen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import Configs.ServerSettings;

public class MapTest {

	
	public static BufferedImage createHeightMap(IslandMap Map)
	{
		BufferedImage image = new BufferedImage(Map.l.length, Map.l[0].length, BufferedImage.TYPE_INT_ARGB);
		
		Color oceanColor = new Color(0,0,128,255);
		Color lakeColor = new Color(0,0,255,255);
		Color defaultColor = new Color(0,0,0,255);
	
		Map<Integer,Color> colorLookup = new HashMap<Integer,Color>();
		
		colorLookup.put(1, new Color(255,255,0,255));
		colorLookup.put(2, new Color(0,255,0,255));
		colorLookup.put(3, new Color(0,196,0,255));
		colorLookup.put(4, new Color(0,128,0,255));
		colorLookup.put(5, new Color(0,64,0,255));
		
		colorLookup.put(33, new Color(255,96,0,255));
		colorLookup.put(34, new Color(196,255,0,255));
		colorLookup.put(35, new Color(96,196,64,255));
		colorLookup.put(36, new Color(64,128,0,255));
		colorLookup.put(37, new Color(0,128,64,255));
		
		colorLookup.put(65, new Color(255,196,128,255));
		colorLookup.put(66, new Color(196,196,0,255));
		colorLookup.put(67, new Color(128,128,0,255));
		colorLookup.put(68, new Color(128,128,196,255));
		colorLookup.put(69, new Color(0,128,128,255));
		
		colorLookup.put(97, new Color(255,128,0,255));
		colorLookup.put(98, new Color(196,128,0,255));
		colorLookup.put(99, new Color(196,196,96,255));
		colorLookup.put(100, new Color(128,196,196,255));
		colorLookup.put(101, new Color(128,196,255,255));
		
		colorLookup.put(129, new Color(128,64,0,255));
		colorLookup.put(130, new Color(255,196,64,255));
		colorLookup.put(131, new Color(255,196,128,255));
		colorLookup.put(132, new Color(196,255,255,255));
		colorLookup.put(133, new Color(255,255,255,255));
		
		Color[] landColors = new Color[6];
		landColors[0] = new Color(255,255,0,255);
		landColors[1] = new Color(192,255,0,255);
		landColors[2] = new Color(96,128,0,255);
		landColors[3] = new Color(0,192,0,255);
		landColors[4] = new Color(0,128,0,255);
		landColors[5] = new Color(255,255,255,255);
		
		for(int i = 0; i < Map.l.length; i++)
		{
			for(int j = 0; j < Map.l[0].length; j++)
			{
				if(Map.l[i][j])
				{
					if(Map.st[i][j] != 0)
					{
						image.setRGB(i, j, landColors[5].getRGB());
						image.setRGB(i, j, landColors[5].getRGB());
					}
					else if(Map.h[i][j] > ServerSettings.seaLevel + 8)
					{
						Color biomeMixColor = new Color(0,0,0,255);

						if(colorLookup.containsKey(Map.ma[i][j]))
						{
							biomeMixColor = colorLookup.get(Map.ma[i][j]);
						}
						
						Color mixColor = new Color((Map.h[i][j] + biomeMixColor.getRed())/2, (Map.h[i][j] + biomeMixColor.getGreen())/2, (Map.h[i][j] + biomeMixColor.getBlue())/2,255);
						image.setRGB(i, j, mixColor.getRGB());
					}
					else
					{
						if( colorLookup.containsKey(Map.ma[i][j]))
						{
							image.setRGB(i, j, colorLookup.get(Map.ma[i][j]).getRGB());
						}
						else
						{
							image.setRGB(i, j, defaultColor.getRGB());
						}
					}
				}
				else if(Map.o[i][j])
				{
					image.setRGB(i, j, oceanColor.getRGB());
				}
				else if(Map.lk[i][j])
				{
					image.setRGB(i, j, lakeColor.getRGB());
				}
				else if(Map.r[i][j])
				{
					image.setRGB(i, j, lakeColor.getRGB());
				}
				else
				{
					image.setRGB(i, j, defaultColor.getRGB());
				}
			}
		}
		
		
		try 
		{
		    File outputfile = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/gfx/map_test.png");
		    outputfile.setWritable(true);
		    outputfile.setReadable(true);
		    ImageIO.write(image, "png", outputfile);
		} 
		catch (IOException e)
		{
			
		    System.out.println(e);
		}
		
		return image;
	}
	
}
