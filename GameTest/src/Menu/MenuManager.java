package Menu;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

import Console.Parser;
import Input.InputHandler;

/*
 * TODO
 * 
 * Organize argument order for Button
 */
public class MenuManager extends AbstractAppState{

	private Camera cam;
	private Node menuNode;
	
	private List<Button> activeButtons = new ArrayList<Button>();
	
	private long tickTime = 100;
	private long lastTick = 0;
	
	public MenuManager(Parser ConsoleParser)
	{
		ConsoleParser.addAllowedClass(this, "Menu.MenuManager");
		ConsoleParser.addAllowedMethod("createMenu", "Menu.MenuManager");
		ConsoleParser.addAllowedMethod("newbutton", "Menu.MenuManager");
		ConsoleParser.addAllowedMethod("createmenu", "Menu.MenuManager");
		ConsoleParser.addAllowedMethod("clearmenu", "Menu.MenuManager");
		ConsoleParser.addAllowedMethod("getactivebuttons", "Menu.MenuManager");
	}
	
	@Override
	public void initialize(AppStateManager StateManager, Application Application)
	{
		if(menuNode == null)
		{
			SimpleApplication app = (SimpleApplication) Application;
	
			super.initialize(StateManager, app);
			menuNode = new Node("MenuManagerNode");
			menuNode.setCullHint(CullHint.Never);
		    
		    app.getGuiNode().attachChild(menuNode);
		    cam = app.getCamera();
		    
		    app.getGuiNode().updateLogicalState(1);
		    app.getGuiNode().updateGeometricState();
		    	
		    this.setEnabled(true);
		}
	}
	
	public void clearmenu()
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			activeButtons.get(i).destroy();
		}
		menuNode.detachAllChildren();
		activeButtons = new ArrayList<Button>();
	}
	
	public void createmenu(Object[] Value)
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			activeButtons.get(i).destroy();
		}
		menuNode.detachAllChildren();
		activeButtons = new ArrayList<Button>();
		Parser.execute(Value[0].toString());
	}
	
	public void newbutton(Object[] Value)
	{
		Object[] arguments = new Object[Value.length-1];
		for(int i = 0; i < arguments.length; i++)
		{
			arguments[i] = Value[i+1];
		}
		activeButtons.add( createButton(Value[0].toString(), arguments));
	}
	
	public List<Button> getactivebuttons()
	{
		return activeButtons;
	}
	
	public Button createButton(String ButtonTypeName, Object[] Arguments)
	{
		Class someClass = null;
		Object newObject = null;
		try 
		{
			someClass = Class.forName("Menu." + ButtonTypeName.substring(0, 1).toUpperCase() + ButtonTypeName.substring(1));
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
			con = someClass.getConstructor(Object[].class, Node.class, float.class, float.class);
		} 
		catch (NoSuchMethodException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{
			//Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight
			Button newButton = (Button)con.newInstance(Arguments, menuNode, cam.getWidth(), cam.getHeight());
			return newButton;
		} 
		catch (InstantiationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void mouse_primary()
	{
		if((System.nanoTime() - lastTick)/1000000 > tickTime)
		{
			for(int i = 0; i < activeButtons.size(); i++)
			{
				if(activeButtons.get(i).isEnabled)
				{
					if(activeButtons.get(i).onClick( new Vector2f(Crosshair.getPosition().getX() + Crosshair.getSize().getX()/2, Crosshair.getPosition().getY() + Crosshair.getSize().getY()/2)))
					{
						lastTick = System.nanoTime();
					}
				}
			}
		}
	}
	
	@Override
	public void update(float tpf) 
	{
		//unfocus buttons on no click
		
		if(InputHandler.hasInput("mouse_primary"))
		{
			mouse_primary();
		}
		
		for(int i = 0; i < activeButtons.size(); i++)
		{
			if(activeButtons.get(i).isEnabled)
			{
				activeButtons.get(i).update();
			}
		}
		
	}
	
	public void removeButton(String ButtonName)
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			if(activeButtons.get(i).getName().equals(ButtonName))
			{
				activeButtons.get(i).destroy();
				activeButtons.remove(i);
				return;
			}
		}
		
	}
	
	
	public boolean hasMenu()
	{
		return activeButtons.size() > 0 && menuNode.getQuantity() > 0;
	}
	
}
