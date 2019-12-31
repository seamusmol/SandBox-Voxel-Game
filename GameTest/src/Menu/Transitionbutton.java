package Menu;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import Console.Parser;

public class Transitionbutton extends Button{

	public Transitionbutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		this.commandLine = "createmenu " + this.buttonName;
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
