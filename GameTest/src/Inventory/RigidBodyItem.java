package Inventory;

import com.jme3.math.Vector3f;

import ClientDataManager.Client;
import Configs.ServerSettings;
import ServerRigidBodies.ServerRigidBody;

public class RigidBodyItem extends Item{

	short material;
	ServerRigidBody grabbedRigidBody = null;
	Vector3f grabbedPosition = new Vector3f();
	
	int propID = 0;
	
	public RigidBodyItem(Client Client)
	{
		super(Client);
	}
	
	public RigidBodyItem(Client Client, short Material)
	{
		super(Client);
		material = Material;
		toolID = 1;
	}
	
	@Override
	public void onAction()
	{
		selectedSkill = selectedSkill <=1 ? 1: selectedSkill;
		selectedSkill = selectedSkill >=4 ? 4: selectedSkill;
		switch(selectedSkill)
		{
			case 1:
//				ItemAbilities.DropItem(client, material);
				ItemAbilities.dropProp(client, propID);
				break;
			case 2:
				//freeze/unfreeze rigidbody
				ItemAbilities.FreezeRigidBody(client);
				break;
			case 3:
				//toggle grab
				if(grabbedRigidBody == null)
				{
					//grab rigidbody
					ItemAbilities.grabRigidBody(client, this);
				}
				else
				{
					grabbedRigidBody = null;
					grabbedPosition = null;
				}
				
				break;
			case 4:
				//copy
				break;
		}
		
	}
	
	@Override
	public void onUpdate()
	{
		if(selectedSkill == 3)
		{
			if(grabbedRigidBody != null)
			{
				ItemAbilities.moveGrabbedRigidBody(client, this);
			}
		}
	}

	@Override
	public void setValue(String Tool, int Value)
	{
		switch(Tool)
		{
		case "mtl":
			material = (short) Value;
			break;
		case "prp":
			propID = Value;
			break;
		}
		
	}
	
	public void setGrabbedRigidBody(ServerRigidBody ServerRigidBody)
	{
		grabbedRigidBody = ServerRigidBody;
	}
	
	public ServerRigidBody getGrabbedRigidBody()
	{
		return grabbedRigidBody;
	}
	
	public void setGrabbedPosition(Vector3f GrabPosition)
	{
		grabbedPosition = GrabPosition;
	}
	
}