package Menu;

import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import Console.Parser;

public class Inputactionbutton extends Button{

	String[] sources = new String[0];
	String classSource = "";
	
	public Inputactionbutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) {
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		commandLine =  (String)Arguments[11];
		classSource = (String)Arguments[12];
		sources = new String[Arguments.length - 12];
		
		for(int i = 12; i < Arguments.length; i++)
		{
			sources[i-12] = (String)Arguments[i];
		}
		attachButton();
	}

	@Override
	public boolean onClick(Vector2f MousePos)
	{
		if(hasFocus(MousePos))
		{
			String input = " ";
			
			Object[] data = Parser.executeClassCommand(classSource, "getActiveButtons");
			if(data.length > 0)
			{
				List<Button> buttons = (List<Button>)Parser.executeClassCommand(classSource, "getActiveButtons")[0];
				
				for(int j = 0; j < sources.length; j++)
				{
					for(int i = 0; i < buttons.size(); i++)
					{
						if(buttons.get(i).getName().equals(sources[j]))
						{
							if(buttons.get(i).getData().length() == 0)
							{
								input += "` ";
							}
							else
							{
								input += buttons.get(i).getData().replaceAll(" ", "`") + " ";
							}
							break;
						}
					}
				}
				Parser.parseString(commandLine + input); 
			}
			return true;
		}
		return false;
	}
	
	
	
	
}
