package Villages;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;

import worldGen.IslandMap;

public abstract class Settlement {

	int townSize = 128;
	
	public Vector2f centerPosition;
    public IslandMap IslandMap;

    public List<Structure> StructureList = new ArrayList<Structure>();

    public Settlement(Vector2f Position, IslandMap Map)
    {
        centerPosition = Position;
        IslandMap = Map;
    }

    public abstract void GenerateLayout();
	
}
