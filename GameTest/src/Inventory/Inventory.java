package Inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.jme3.scene.Node;

import ClientDataManager.Client;
import Main.Main;
import Menu.Button;
import serverlevelgen.ServerCollisionPicker;

public class Inventory {

	Client client;
	Main main;
	ServerCollisionPicker collisionPicker;
	ItemAbilities itemAbilities;
	
	int maxInventory = 10;
	
	//primary item
	//secondary item
	//
	//array containing resource amounts
	Item primaryItem;
	Item secondaryItem;
	Item backPrimaryItem;
	Item backsecondaryItem;
	
	public Inventory(Client Client, Main Main, ServerCollisionPicker CollisionPicker)
	{
		client = Client;
		collisionPicker = CollisionPicker;
		main = Main;
		itemAbilities = new ItemAbilities(main, CollisionPicker);
		
		primaryItem = new RigidBodyItem(Client, (short)129);
		secondaryItem = new TerrainItem(Client, 129);
		backPrimaryItem = new TerrainItem(Client, 0);
		backsecondaryItem = new TerrainItem(Client, 129);
	}
	
	public void addItem()
	{
		
	}
	
	public void removeItem()
	{
		
	}
	
	public void update()
	{
		primaryItem.onUpdate();
		secondaryItem.onUpdate();
	}
	
	public void setToolValue(int WeaponSlot, String Tool, int Value)
	{
		if(Tool.equals("tol"))
		{	
			Item newTool = null;
			List<String> toolNames = ItemAbilities.selectionLists.get("toolselectionlist");
			
			if(Value >= toolNames.size())
			{
				return;
			}
			Class someClass = null;
			Object newObject = null;
			try 
			{
				someClass = Class.forName("Inventory." + toolNames.get(Value));
			} 
			catch (ClassNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Constructor con = null;
			try 
			{
				//Object[].class, Node.class,float.class, float.class
				con = someClass.getConstructor(Client.class);
				newTool = (Item)con.newInstance( client);
			} 
			catch(Error | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				
			}
			
			switch(WeaponSlot)
			{
				case 0:
					primaryItem = newTool;
					break;
				case 1:
					secondaryItem = newTool;
					break;
			}
		}
		else
		{
			switch(WeaponSlot)
			{
				case 0:
					primaryItem.setValue(Tool, Value);
					break;
				case 1:
					secondaryItem.setValue(Tool, Value);
					break;
			}
		}
		
		
	}
	
	public void setSkill(int Slot, int Skill)
	{
		switch(Slot)
		{
			case 1:
				primaryItem.setSelectedSkill(Skill);
				break;
			case 2:
				secondaryItem.setSelectedSkill(Skill);
				break;
			case 3:
				primaryItem.setSelectedSkill(Skill);
				break;
			case 4:
				primaryItem.setSelectedSkill(Skill);
				break;
		}
		
	}
	
	public void setTerrainMaterial(int NewMaterial)
	{
		if(primaryItem instanceof TerrainItem)
		{
			((TerrainItem) primaryItem).fillMaterial = NewMaterial;
		}
	}
        
        public short getPrimaryMaterial()
        {
            if(primaryItem instanceof TerrainItem)
            {
                return (short) ((TerrainItem) primaryItem).fillMaterial;
            }
            else
            {
                return 0;
            }
        }
	
	public void setInventoryIndex(int Index)
	{
		
	}
	
	public void primary()
	{
		primaryItem.onAction();
	}
	
	public void secondary()
	{
		secondaryItem.onAction();
	}
	
	public Item getPrimary()
	{
		return primaryItem;
	}
	public Item getSecondary()
	{
		return secondaryItem;
	}
}
