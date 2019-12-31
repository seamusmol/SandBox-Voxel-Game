package Menu;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import Console.Parser;

public class Actionbutton extends Button{

	/*
	 * 
	 */
	public Actionbutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		for(int i = 11; i < Arguments.length; i++)
		{
			commandLine += Arguments[i].toString() + " ";
		}
		attachButton();
	}
	
	@Override
	public boolean onClick(Vector2f MousePos)
	{
		if(hasFocus(MousePos))
		{
			Parser.parseString(commandLine);
			return true;
		}
		return false;
	}
}
