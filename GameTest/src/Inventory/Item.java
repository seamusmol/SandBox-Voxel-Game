package Inventory;

import ClientDataManager.Client;

public class Item {

	Client client;
	
	int selectedSkill = 1;
	int toolID = 0;
	int value = 0;
	String[] uiButtons = new String[]{};
	
	public Item(Client Client)
	{
		this.client = Client;
	}
	
	public void setValue(String Tool, int Value)
	{
		
	}
	
	public void onAction()
	{
		
	}
	
	public void onUpdate()
	{
		
	}
	
	public int getID()
	{
		return toolID;
	}
	public int getSelectedSkill()
	{
		return selectedSkill;
	}
	
	public void setSelectedSkill(int NewSkill)
	{
		selectedSkill = NewSkill;
	}
	
}
