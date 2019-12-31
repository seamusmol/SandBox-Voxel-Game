package Villages;

import com.jme3.math.Vector2f;

import Configs.ServerSettings;
import Util.MapUtil;
import worldGen.IslandMap;

public class VillageDefinitions {

	public static Settlement GetDefinition(IslandMap Map, Vector2f SettlementPosition)
	{
		//priority
		//lake
		//river
		//coast
		//mountain
		//valley
//		if(IsLakeSettlement(Map, SettlementPosition))
//		{
//			System.out.println("Created Lake Settlement: " + SettlementPosition);
//			return new LakeSettlement(SettlementPosition, Map);
//		}
//		if(IsRiverSettlement(Map, SettlementPosition))
//		{
//			System.out.println("Created River Settlement: " + SettlementPosition);
//			return new RiverSettlement(SettlementPosition, Map);
//		}
		if(IsCoastSettlement(Map, SettlementPosition))
		{
			System.out.println("Created Coast Settlement: " + SettlementPosition);
			return new CoastSettlement(SettlementPosition, Map);
		}
		if(IsMountainSettlement(Map, SettlementPosition))
		{
			System.out.println("Created Mountain Settlement: " + SettlementPosition);
			return new MountainSettlement(SettlementPosition, Map);
		}
		System.out.println("Created Valley Settlement: " + SettlementPosition);
		return new ValleySettlement(SettlementPosition, Map);
	}
	
//	public static boolean IsLakeSettlement(IslandMap Map, Vector2f SettlementPosition)
//	{
//		if(MapUtil.HasSurrounding(SettlementPosition, Map.lk, ServerSettings.settlementLakeDistance))
//		{
//			Vector2f lakePosition = MapUtil.getClosest(SettlementPosition, Map.lk, ServerSettings.settlementLakeDistance);
//			SettlementPosition = lakePosition;
//			return true;
//		}
//		return false;
//	}
	
//	public static boolean IsRiverSettlement(IslandMap Map, Vector2f SettlementPosition)
//	{
//		if(MapUtil.HasSurrounding(SettlementPosition, Map.r, ServerSettings.settlementRiverDistance))
//		{
//			Vector2f riverPosition = MapUtil.getClosest(SettlementPosition, Map.r, ServerSettings.settlementRiverDistance);
//			SettlementPosition = riverPosition;
//			return true;
//		}
//		return false;
//	}
	
	public static boolean IsCoastSettlement(IslandMap Map, Vector2f SettlementPosition)
	{
		if(MapUtil.HasSurrounding(SettlementPosition, Map.o, ServerSettings.settlementCoastDistance))
		{
			Vector2f coastPosition = MapUtil.getClosest(SettlementPosition, Map.o, ServerSettings.settlementCoastDistance);
			SettlementPosition = coastPosition;
			return true;
		}
		return false;
	}
	
	public static boolean IsMountainSettlement(IslandMap Map, Vector2f SettlementPosition)
	{
		if(MapUtil.HasSurrounding(SettlementPosition, Map.m, ServerSettings.settlementMountainDistance))
		{
			Vector2f mountainPosition = MapUtil.getClosest(SettlementPosition, Map.m, ServerSettings.settlementMountainDistance);
			SettlementPosition = mountainPosition;
			return true;
		}
		return false;
	}
	
	
}
