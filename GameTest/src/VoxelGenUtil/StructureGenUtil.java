package VoxelGenUtil;

import com.jme3.math.Vector2f;

import Configs.ServerSettings;
import Util.MapUtil;
import Util.MathUtil;
import worldGen.IslandMap;

public class StructureGenUtil {

	public static void GenerateRoad(Vector2f Position, Vector2f EndPosition, IslandMap Map, boolean FillMap)
	{
		int roadWidth = 4;
		
		Vector2f dir = new Vector2f(EndPosition.x-Position.x,EndPosition.y-Position.y).normalize();
		Vector2f crossDir = new Vector2f(dir.y,dir.x);
		
		int distance = (int) Position.distance(EndPosition);
		for(int i = 0; i < distance; i++)
		{
			int px = (int)(dir.x * i + Position.x);
			int py = (int)(dir.y * i + Position.y);
			if(Map.o[px][py])
			{
				return;
			}
		}
				
		for(int i = 0; i < distance; i++)
		{
			int px = (int)(dir.x * i + Position.x);
			int py = (int)(dir.y * i + Position.y);
			
			int height = Math.round(MathUtil.lerp(i, 0, distance, Map.h[(int)(Position.x)][(int)(Position.y)], Map.h[(int)(EndPosition.x)][(int)(EndPosition.y)]));
			height = height >=64 ? height:64;
			
			for(int j = -roadWidth; j <= roadWidth; j++)
			{
				int ox = (int)(crossDir.x * j + px);
				int oy = (int)(crossDir.y * j + py);
				
				Map.ma[ox][oy] = 193;
				Map.h[ox][oy] = height;
				
			}
			Map.ma[px][py] = 192;
			
		}
	}
	
	public static void GenerateIntersection(Vector2f Position, IslandMap Map, boolean FillMap)
	{
		int roadwidth = 4;
		
		int averageHeight = 0;
		int count = 0;
		for(int i = -roadwidth; i <= roadwidth; i++)
		{
			for(int j = -roadwidth; j <= roadwidth; j++)
			{
				if(Map.l[(int)Position.x+i][(int)Position.y+j])
				{
					averageHeight+= Map.h[(int)Position.x+i][(int)Position.y+j];
					count++;
				}
			}
		}
		averageHeight = (int)(averageHeight/count);
		averageHeight = averageHeight >= 64 ? averageHeight:64;
		
		for(int i = -roadwidth; i <= roadwidth; i++)
		{
			for(int j = -roadwidth; j <= roadwidth; j++)
			{
				Map.h[(int)Position.x+i][(int)Position.y+j] = averageHeight;
				Map.ma[(int)Position.x+i][(int)Position.y+j] = 192;
				
			}
		}
	}
	
	public static void generateCityBlocks(IslandMap Map, Vector2f CityCenter, int InfrastructureType, boolean[][] CornerMap, int[][] SideMap)
	{
		int blockSize = 64;
		float infrastructureFalloff = 10;
		float infrastructureLevels = 4;
		float maxDist = blockSize * infrastructureFalloff;
		
		int roadwidth = 9;
		
		for(int i = 1; i < CornerMap.length-1; i++)
		{
			for(int j = 1; j < CornerMap[0].length-1; j++)
			{
				int px = i * blockSize;
				int py = j * blockSize;
				
				int infrastructure = 1;
				switch(InfrastructureType)
				{
					case 0:
						//coast distance
						Vector2f oceanPos = MapUtil.getClosest(CityCenter, Map.o, (int)maxDist);	
						float oceanDist = oceanPos.distance(new Vector2f(px, py));
						infrastructure = Math.round(oceanDist/maxDist * infrastructureLevels);
						break;
					case 1:
						float centerDist = CityCenter.distance(new Vector2f(px, py));
						infrastructure = Math.round(centerDist/maxDist * infrastructureLevels);
						break;
				}
				
				infrastructure = infrastructure < 1 ? 1:infrastructure;
				infrastructure = infrastructure > 3 ? 3:infrastructure;
				
				switch(SideMap[i][j])
				{
					case 15:
						generateSquareBlock(px,py, infrastructure, Map);
						break;
				}
			}
		}
	}
	
	public static void generateSquareBlock(int X, int Y, int Infrastructure, IslandMap Map)
	{
		int sx = Map.nm[X][Y]%100;
		
		int x1 = 24;
		int x2 = 48;
		int y1 = 24;
		int y2 = 48;
		
		int modeldigitLength = 7;
		
		int ID1 = 0; 
		int ID2 = 0;
		int ID3 = 0;
		int ID4 = 0;
		
		if(sx >= ServerSettings.blockSpawnRate_Government)
		{
			
		}
		else if(sx >= ServerSettings.blockSpawnRate_Offices)
		{
		}
		else if(sx >= ServerSettings.blockSpawnRate_BigBusinesses)
		{
		}
		else if(sx >= ServerSettings.blockSpawnRate_SmallBusinesses)
		{
		}
		else if(sx >= ServerSettings.blockSpawnRate_Flats)
		{
			ID1 = 1; 
			ID2 = 1;
			ID3 = 1;
			ID4 = 1;
		}
		else if(sx >= ServerSettings.blockSpawnRate_Houses)
		{
			ID1 = 0; 
			ID2 = 0;
			ID3 = 0;
			ID4 = 0;
		}
		
		String houseString1 = ID1 + "";
		String houseString2 = ID2 + "";
		String houseString3 = ID3 + "";
		String houseString4 = ID4 + "";
		
		int v1 = Map.nm[X+x1][Y+y1]; 
		int v2 = Map.nm[X+x2][Y+y1]; 
		int v3 = Map.nm[X+x1][Y+y2]; 
		int v4 = Map.nm[X+x2][Y+y2]; 
		
		for(int i = 0; i < modeldigitLength; i++)
		{
			v1+= Map.nm[X+x1 + i][Y+y1];
			v2+= Map.nm[X+x2 + i][Y+y1];
			v3+= Map.nm[X+x1 + i][Y+y2];
			v4+= Map.nm[X+x2 + i][Y+y2];
			
			v1%=10;
			v2%=10;
			v3%=10;
			v4%=10;
		
			houseString1+= v1;
			houseString2+= v2;
			houseString3+= v3;
			houseString4+= v4;
		}
		
		houseString1+= Infrastructure%10;
		houseString2+= Infrastructure%10;
		houseString3+= Infrastructure%10;
		houseString4+= Infrastructure%10;
		
		v1+= Map.nm[X+x1 + modeldigitLength][Y+y1];
		v2+= Map.nm[X+x2 + modeldigitLength][Y+y1];
		v3+= Map.nm[X+x1 + modeldigitLength][Y+y2];
		v4+= Map.nm[X+x2 + modeldigitLength][Y+y2];
		
		v1%=2;
		v2%=2;
		v3%=2;
		v4%=2;
		
		int rot1 = v1 == 1 ? -90:0;
		int rot2 = v2 == 1 ? 0:90;
		int rot3 = v3 == 1 ? 90:180;
		int rot4 = v4 == 1 ? 180:-90;
		
		Vector2f pr1 = new Vector2f(X+x1,Y+y1);
		Vector2f pr2 = new Vector2f(X+x2,Y+y1);
		Vector2f pr3 = new Vector2f(X+x1,Y+y2);
		Vector2f pr4 = new Vector2f(X+x2,Y+y2);
		
		int hv1 = (Map.h[X][Y] + Map.h[X][Y] + Map.h[X][Y] + Map.h[X][Y])/4;
		int hv2 = (Map.h[X][Y] + Map.h[X+32][Y] + Map.h[X][Y] + Map.h[X+32][Y])/4;
		int hv3 = (Map.h[X][Y] + Map.h[X][Y] + Map.h[X][Y+32] + Map.h[X][Y+32])/4;
		int hv4 = (Map.h[X][Y] + Map.h[X+32][Y] + Map.h[X][Y+32] + Map.h[X+32][Y+32])/4;
		
		int h1 = Math.abs(hv1 - Map.h[X+x1][Y+y1]); 
		int h2 = Math.abs(hv2 - Map.h[X+x2][Y+y1]); 
		int h3 = Math.abs(hv3 - Map.h[X+x1][Y+y2]); 
		int h4 = Math.abs(hv4 - Map.h[X+x2][Y+y2]); 
		
		int heightTolerance = 20;
		
		if(h1 < heightTolerance)
		{
			MapUtil.SetArea(X+x1 - 4, Y+y1 - 4, 9, 9, Map.h, Map.h[X+x1][Y+y1]);
			Map.st[(int) pr1.x][(int) pr1.y] = Integer.parseInt(houseString1);
			Map.str[(int) pr1.x][(int) pr1.y] = rot1;
		}
		if(h2 < heightTolerance)
		{
			MapUtil.SetArea(X+x2 - 4, Y+y1 - 4, 9, 9, Map.h, Map.h[X+x2][Y+y1]);
			Map.st[(int) pr2.x][(int) pr2.y] = Integer.parseInt(houseString2);
			Map.str[(int) pr2.x][(int) pr2.y] = rot2;
		}
		if(h3 < heightTolerance)
		{
			MapUtil.SetArea(X+x1 - 4, Y+y2 - 4, 9, 9, Map.h, Map.h[X+x1][Y+y2]);
			Map.st[(int) pr3.x][(int) pr3.y] = Integer.parseInt(houseString3);
			Map.str[(int) pr3.x][(int) pr3.y] = rot3;
		}
		if(h4 < heightTolerance)
		{
			MapUtil.SetArea(X+x2 - 4, Y+y2 - 4, 9, 9, Map.h, Map.h[X+x2][Y+y2]);
			Map.st[(int) pr4.x][(int) pr4.y] = Integer.parseInt(houseString4);
			Map.str[(int) pr4.x][(int) pr4.y] = rot4;
		}
	}
	
	
}
