package Inventory;

import ClientDataManager.Client;

public class UnArmedItem extends Item{

	int fillMaterial;
	
	public UnArmedItem(Client Client)
	{
		super(Client);
	}
	
	@Override
	public void onAction()
	{
		switch(selectedSkill)
		{
			case 0:
				ItemAbilities.ModifyTerrain(client, 0);
				break;
		}
	}
	
}
