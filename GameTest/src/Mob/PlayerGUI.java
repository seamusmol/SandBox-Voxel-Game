package Mob;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import Input.InputHandler;
import Menu.Actionbutton;
import Menu.Button;
import Menu.Crosshair;
import Menu.Imagebutton;
import Menu.Itemmodelselector;
import Menu.Itemselector;

/*
 * Manages GUI interactions with player object
 * 
 */
public class PlayerGUI extends AbstractAppState{

	private Camera cam;
	private Node consoleNode;
	private Node popupNode;
	
	private boolean hasMenu = false;
	private boolean isSelectorEnabled = false;
	private boolean isOccupied = false;
	
	private long toggleTime = 100;
	private long lastToggle = 0;
	
	private long lastTick = System.currentTimeMillis();
	
	List<Button> buttonList = new ArrayList<Button>();
	List<Button> popupButtons = new ArrayList<Button>();
	List<Button> menuButtons = new ArrayList<Button>();
	
	int primaryToolID = 0;
	int secondaryToolID = 0;
	int primaryToolSkill = 0;
	int secondaryToolSkill = 0;
	
	int currentList = 0;
	String[] currentButtons = new String[]{};
	
	public PlayerGUI()
	{
		
	}
	
	public void initialize(AppStateManager StateManager, Application Application)
	{
		SimpleApplication app = (SimpleApplication) Application;

		super.initialize(StateManager, app);
		popupNode = new Node("PopupNode");
		consoleNode = new Node("PlayerGUINode");
	    
	    cam = app.getCamera();
	    
	    DirectionalLight sun = new DirectionalLight();
	    sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
	    sun.setColor(ColorRGBA.White.mult(0.6f));
	    app.getGuiNode().addLight(sun);
	    
	    app.getGuiNode().attachChild(consoleNode);
	    app.getGuiNode().attachChild(popupNode);
	    
	    app.getGuiNode().updateLogicalState(1);
	    app.getGuiNode().updateGeometricState();
	    	
	    this.setEnabled(true);
	    
	    initGUI();
	}
	
	public void initGUI()
	{
		Object[] selectorBackgroundData = new Object[]{"background", "false","selector_background",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,0,0,1,1};
		Object[] selector1Data = new Object[]{"selectormtl", "false","test",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,32,32};
		Object[] selector2Data = new Object[]{"selectortol", "false","selector_tools",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,16,16};
		Object[] selector3Data = new Object[]{"selectorprp", "false","selector_props",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,16,16, "props", "test"};
		Object[] selector4Data = new Object[]{"selectorbd1", "false","selector_background",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,16,16};
		Object[] selector5Data = new Object[]{"selectorbd2", "false","selector_background",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,16,16};
		Object[] selector6Data = new Object[]{"selectorbd3", "false","selector_background",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,16,16};
		Object[] selector7Data = new Object[]{"selectorbd4", "false","selector_background",0.2, 0.15,0.7,0.7,0,0,1,1,15,15,1,1,16,16};
		
		popupButtons.add(new Itemselector(selectorBackgroundData, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemselector(selector1Data, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemselector(selector2Data, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemmodelselector(selector3Data, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemselector(selector4Data, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemselector(selector5Data, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemselector(selector6Data, popupNode, cam.getWidth(), cam.getHeight()));
		popupButtons.add(new Itemselector(selector7Data, popupNode, cam.getWidth(), cam.getHeight()));
		
		Object[] tab1Data = new Object[]{"changetab mtl", "true", "menu", 0.1, 0.75, 0.1, 0.1,3,4.5,1,0.5};
		Object[] tab2Data = new Object[]{"changetab prp", "true", "menu", 0.1, 0.65, 0.1, 0.1,3,5,1,0.5};
		Object[] tab3Data = new Object[]{"changetab tol", "true", "menu", 0.1, 0.55, 0.1, 0.1,3,5.5,1,0.5};
		Object[] tab4Data = new Object[]{"changetab bd1", "true", "menu", 0.1, 0.45, 0.1, 0.1,3,6,1,0.5};
		Object[] tab5Data = new Object[]{"changetab bd2", "true", "menu", 0.1, 0.35, 0.1, 0.1,3,6,1,0.5};
		Object[] tab6Data = new Object[]{"changetab bd3", "true", "menu", 0.1, 0.25, 0.1, 0.1,3,6,1,0.5};
		Object[] tab7Data = new Object[]{"changetab bd4", "true", "menu", 0.1, 0.15, 0.1, 0.1,3,6,1,0.5};
		
		Imagebutton tab1 = new Imagebutton(tab1Data, popupNode, cam.getWidth(), cam.getHeight());
		Imagebutton tab2 = new Imagebutton(tab2Data, popupNode, cam.getWidth(), cam.getHeight());
		Imagebutton tab3 = new Imagebutton(tab3Data, popupNode, cam.getWidth(), cam.getHeight());
		Imagebutton tab4 = new Imagebutton(tab4Data, popupNode, cam.getWidth(), cam.getHeight());
		Imagebutton tab5 = new Imagebutton(tab5Data, popupNode, cam.getWidth(), cam.getHeight());
		Imagebutton tab6 = new Imagebutton(tab6Data, popupNode, cam.getWidth(), cam.getHeight());
		Imagebutton tab7 = new Imagebutton(tab7Data, popupNode, cam.getWidth(), cam.getHeight());
		popupButtons.add(tab1);
		popupButtons.add(tab2);
		popupButtons.add(tab3);
		popupButtons.add(tab4);
		popupButtons.add(tab5);
		popupButtons.add(tab6);
		popupButtons.add(tab7);
		
		Object[] primaryTool = new Object[]{"primarySelector", "false","tool_0",0.2,0.1, 0.25,0.05,0,0,1,1,15,15,1,1,4,1 };
		Itemselector primarySelector = new Itemselector(primaryTool, consoleNode, cam.getWidth(), cam.getHeight());
		primarySelector.setEnabled(false);
		buttonList.add(primarySelector);
		
		Object[] secondaryTool = new Object[]{"secondarySelector", "false","tool_0", 0.55,0.1,0.25,0.05,0,0,1,1,15,15,1,1,4,1};
		Itemselector secondarySelector = new Itemselector(secondaryTool, consoleNode, cam.getWidth(), cam.getHeight());
		secondarySelector.setEnabled(false);
		buttonList.add(secondarySelector);
		
		Object[] primaryDisplayData = new Object[]{"primaryDisplay","false","tooldisplay_0",0.45,0.1,0.05,0.05,0,0,1,1};
		Object[] secondaryDisplayData = new Object[]{"secondaryDisplay","false","tooldisplay_0",0.5,0.1,0.05,0.05,0,0,1,1};
		
		Imagebutton primaryDisplay = new Imagebutton(primaryDisplayData, consoleNode, cam.getWidth(), cam.getHeight());
		Imagebutton secondaryDisplay = new Imagebutton(secondaryDisplayData, consoleNode, cam.getWidth(), cam.getHeight());
		buttonList.add(primaryDisplay);
		buttonList.add(secondaryDisplay);
		
		Object[] menuBackgroundData = new Object[]{"menubackground", "true", "menu" , 0.35, 0.2, 0.3, 0.7, 0, 8, 3, 8};
		Imagebutton menuBackground = new Imagebutton(menuBackgroundData, popupNode, cam.getWidth(), cam.getHeight());
		menuBackground.setLock(true);
		menuBackground.setEnabled(false);
		menuButtons.add(menuBackground);

		Object[] exitButtonData = new Object[]{"close", "true", "menu", 0.40, 0.3, 0.2, 0.1, 3, 4, 1, 0.5, "close"};
		Actionbutton exitButton = new Actionbutton(exitButtonData, popupNode, cam.getWidth(), cam.getHeight());
		menuButtons.add(exitButton);
	}
	
	@Override
	public void update(float tpf) 
	{
		for(Button button : popupButtons)
		{
			//selector
			button.hideButton();
		}
		for(Button button : menuButtons)
		{
			button.hideButton();
		}
		
		if(InputHandler.hasInput("selectortoggle"))
		{
			if((System.nanoTime() - lastToggle)/1000000 > toggleTime)
			{
				//if has tool with 
				if(!hasMenu)
				{
					isSelectorEnabled = !isSelectorEnabled;
					isOccupied = isSelectorEnabled;
					
					currentButtons = isSelectorEnabled ? new String[]{"changetab mtl","changetab tol","changetab prp","changetab bd1","changetab bd2","changetab bd3","changetab bd4","background"}: new String[]{""};
					currentList = isSelectorEnabled ? 1:2;
					
					Crosshair.setIsCrossHairLocked(!isOccupied);
					Crosshair.setIsVisible(isOccupied);
					lastToggle = System.nanoTime();
					if(!isOccupied)
					{
						Crosshair.resetPosition();
					}
				}
			}
		}
		
		if(InputHandler.hasInput("escape"))
		{
			if((System.nanoTime() - lastToggle)/1000000 > toggleTime)
			{
				hasMenu = !hasMenu;
				isOccupied = hasMenu;
				isSelectorEnabled = hasMenu ? false: isSelectorEnabled;
				
				currentButtons = hasMenu ? new String[]{"menubackground","close"}: new String[]{""};
				currentList = hasMenu ? 2:1;
				
				Crosshair.setIsCrossHairLocked(!isOccupied);
				Crosshair.setIsVisible(isOccupied);
				lastToggle = System.nanoTime();
				if(!isOccupied)
				{
					Crosshair.resetPosition();
				}
			}
		}
		
		switch(currentList)
		{
			case 0:
				//non
				break;
			case 1:
				//gameplay-popup
				
				for(Button button : popupButtons)
				{
					for(String Name: currentButtons)
					{
						if(button.getName().equals(Name))
						{
							button.showButton();
							break;
						}
					}
				}
				
				break;
			case 2:
				//menu popup
				for(Button button : menuButtons)
				{
					for(String Name: currentButtons)
					{
						if(button.getName().equals(Name))
						{
							button.showButton();
							break;
						}
					}
				}
				break;
		}
	}
	
	public void updateToolGUI(int GameMode, int SelectedPrimary, int SelectedSecondary, int PrimarySkill, int SecondarySkill)
	{
		if(SelectedPrimary != primaryToolID)
		{
			Itemselector primarySelector = (Itemselector)getButtonByName("primarySelector");
			primarySelector.setTexture("tool_" + SelectedPrimary);
			Button primaryDisplay = getButtonByName("primaryDisplay");
			primaryDisplay.setTexture("tooldisplay_" + SelectedPrimary);
			primaryToolID = SelectedPrimary;
		}
		
		if(SelectedSecondary != secondaryToolID)
		{
			Itemselector secondarySelector = (Itemselector)getButtonByName("secondarySelector");
			secondarySelector.setTexture("tool_" + SelectedSecondary);
			Button secondaryDisplay = getButtonByName("secondaryDisplay");
			secondaryDisplay.setTexture("tooldisplay_" + SelectedSecondary);
			secondaryToolID = SelectedSecondary;
		}
		
		if(PrimarySkill != primaryToolSkill)
		{
			Itemselector primarySelector = (Itemselector)getButtonByName("primarySelector");
			primarySelector.setX(PrimarySkill-1);
			primaryToolSkill = PrimarySkill;
		}
			
		if(SecondarySkill != secondaryToolSkill)
		{
			Itemselector secondarySelector = (Itemselector)getButtonByName("secondarySelector");
			secondarySelector.setX(SecondarySkill-1);
			secondaryToolSkill = SecondarySkill;
		}
	}
	
	public void updateSelectorUI(String Data)
	{
		String[] bits = Data.split(" ");
		
		currentButtons = new String[]{"changetab mtl","changetab tol","changetab prp","changetab bd1","changetab bd2","changetab bd3","changetab bd4", "selector" + bits[1]};
	}
	
	public String onClick()
	{
		if((System.currentTimeMillis() - lastTick) > 4)
		{
			for(Button button : menuButtons)
			{
				if(button.isEnabled())
				{
					if(button.onClick( new Vector2f(Crosshair.getPosition().getX() + Crosshair.getSize().getX()/2, Crosshair.getPosition().getY() + Crosshair.getSize().getY()/2)))
					{
						lastTick = System.currentTimeMillis();
						//get data
						return button.getData();
					}
				}
			}
			
			for(Button button : popupButtons)
			{
				if(button.isEnabled())
				{
					if(button.onClick( new Vector2f(Crosshair.getPosition().getX() + Crosshair.getSize().getX()/2, Crosshair.getPosition().getY() + Crosshair.getSize().getY()/2)))
					{
						lastTick = System.currentTimeMillis();
						String name = button.getName();
						if(name.startsWith("changetab"))
						{
							updateSelectorUI(name);
							return "";
						}
						else if(name.startsWith("selector"))
						{
							return name.replaceAll("selector", "") + " " + button.getData();
						}
						return "";
					}
				}
			}
			
		}
		return "";
	}
	
	
	
	private Button getButtonByName(String Name)
	{
		for(Button button : buttonList)
		{
			if(button.getName().equals(Name))
			{
				return button;
			}	
		}
		return null;
	}
	
	private void cleanUpConsole()
	{
		popupNode.detachAllChildren();
	}
	
	public boolean isOccupied()
	{
		return isOccupied;
	}
}
