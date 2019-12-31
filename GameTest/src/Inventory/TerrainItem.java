package Inventory;

import ClientDataManager.Client;

public class TerrainItem extends Item{

    public int fillMaterial;

    public TerrainItem(Client Client)
    {
    	 super(Client);
    	 toolID = 2;
    }
    
    public TerrainItem(Client Client, int FillMaterial)
    {
        super(Client);
        client = Client;
        fillMaterial = FillMaterial;
        toolID = 2;
    }

    @Override
    public void setValue(String Tool, int Value)
    {
    	if(Tool.equals("mtl"))
    	{
    		System.out.println("material set");
    		fillMaterial = Value;
    	}
    }
    
    @Override
    public void onAction()
    {
    	switch(selectedSkill)
    	{
	    	case 1:
	    		ItemAbilities.ModifyTerrain(client, 0);
	    		break;
	    	case 2:
	    		ItemAbilities.ModifyTerrain(client, fillMaterial);
				break;
			case 3:
				//toggle select
				break;
			case 4:
				//
				break;
	    		
    	}
            
    }
}
