package worldGen;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import Configs.ServerSettings;
import Util.MapUtil;
import Villages.Settlement;
import Villages.VillageDefinitions;
import serverlevelgen.WorldSettings;

public class StructureGenerator {

	public static void GenerateSettlements(IslandMap Map, WorldSettings WorldSettings)
	{
		List<Settlement> settlementList = GeneratePositions(Map,WorldSettings);
		for(int i = 0; i < settlementList.size(); i++)
		{
			System.out.println("Generating Village:" + settlementList.get(i).getClass().toString());
			settlementList.get(i).GenerateLayout();
		}
	}
	
	//identify valid positions
	//identify type of settlement(physical features surrounding settlement)
	
	private static List<Settlement> GeneratePositions(IslandMap Map, WorldSettings WorldSettings)
	{
		
		List<Vector2f> settlementPositions = new ArrayList<Vector2f>();
		
		int px = Map.h.length/2 + Map.nm[0][Map.h[0].length/2];
		int py = Map.h.length/2 + Map.nm[Map.h.length/2][0];
		
		for(int i = 0; i < WorldSettings.settlementCount; i++)
		{
			Vector2f Position = new Vector2f(px + ServerSettings.settlementOffsetDistance,py + ServerSettings.settlementOffsetDistance);
			if(px > ServerSettings.settlementOffsetDistance && py > ServerSettings.settlementOffsetDistance && px < Map.h.length - ServerSettings.settlementOffsetDistance  && py < Map.h[0].length - ServerSettings.settlementOffsetDistance)
			{
				if(!MapUtil.HasSurrounding(Position, settlementPositions, ServerSettings.settlementDistance))
				{
					settlementPositions.add(Position);
				}
			}
			int tempx = px%(Map.h.length/2);
			px+= py + Map.nm[(px%Map.h.length/2)][(py%Map.h[0].length/2)];
			px%= (Map.h[0].length/2);
			py = tempx;
		}
		
		List<Settlement> settlements = new ArrayList<Settlement>();
		for(int i = 0; i < settlementPositions.size(); i++)
		{
			settlements.add(VillageDefinitions.GetDefinition(Map, settlementPositions.get(i)));
		}
		return settlements;
	}
	
	
}
