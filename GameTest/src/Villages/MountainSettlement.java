package Villages;

import com.jme3.math.Vector2f;

import VoxelGenUtil.StructureGenUtil;

public class MountainSettlement extends Settlement {

	public MountainSettlement(Vector2f Position, worldGen.IslandMap Map) {
		super(Position, Map);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void GenerateLayout() {
		// TODO Auto-generated method stub
		int blockSize = 64;
		StructureGenUtil.GenerateRoad(centerPosition, new Vector2f(centerPosition.x - blockSize, centerPosition.y), IslandMap, true);
		StructureGenUtil.GenerateRoad(centerPosition, new Vector2f(centerPosition.x, centerPosition.y - blockSize), IslandMap, true);
		StructureGenUtil.GenerateRoad(centerPosition, new Vector2f(centerPosition.x + blockSize, centerPosition.y), IslandMap, true);
		StructureGenUtil.GenerateRoad(centerPosition, new Vector2f(centerPosition.x, centerPosition.y + blockSize), IslandMap, true);
	}

}
