package Main;
import java.util.Map;
import java.util.TreeMap;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

import Console.ConsoleAppState;
import Console.Parser;
import Input.InputHandler;
import Menu.Crosshair;
import Menu.MenuManager;
import Mob.PlayerSettings;
import VoxelModels.VoxelModels;

public class Main extends SimpleApplication {

	Map<String, AppState> appStates = new TreeMap<String, AppState>();
	
	InputHandler inputHandler;
	MenuManager menuManager;
	AssetLoaderManager assetLoaderManager;
	ConsoleAppState console;
	Crosshair crosshair;
	Parser parser;
	
	GamePlayManager gameplay;
	PlayerSettings playerSettings;
	
	public static void main(String[] args)
    {
        Main app = new Main();
        
        AppSettings newSetting = new AppSettings(true);
		newSetting.setFrameRate(144);	
		newSetting.setResolution(1600, 900);
		
		app.setSettings(newSetting);
		app.setShowSettings(true);
		
        app.start();
    }
	
	@Override
	public void simpleInitApp() 
	{
        parser = new Parser();
        parser.addAllowedClass(this, "Main.Main");
        parser.addAllowedMethod("exit", "Main.Main");
        parser.addAllowedMethod("map", "Main.Main");
        parser.addAllowedMethod("close", "Main.Main");

        inputHandler = new InputHandler(this,parser);

        assetLoaderManager = new AssetLoaderManager(getStateManager(), this);
        VoxelModels.LoadAssets();

        menuManager = new MenuManager(parser);
        menuManager.initialize(getStateManager(), this);

        console = new ConsoleAppState(parser);
        console.initialize(getStateManager(), this);

        playerSettings = new PlayerSettings();
        crosshair = new Crosshair();
        crosshair.initialize(stateManager, this);

        appStates.put("menuManager", menuManager);
        appStates.put("console", console);
        appStates.put("crosshair", crosshair);

        for(Map.Entry<String, AppState> entry : appStates.entrySet())
        {
            stateManager.attach(entry.getValue());
        }

        Parser.parseString("createmenu menu");
        Parser.execute("config");
	}

	@Override
        public void simpleUpdate(float TPF)
	{	
		inputHandler.update(TPF);
	}
	
	@Override
        public void simpleRender(RenderManager rm) 
	{
		
	}
        
	public void map(Object[] Value)
	{
		if(gameplay != null)
		{
			gameplay.close();
			gameplay = null;
		}
		menuManager.clearmenu();

		gameplay = new GamePlayManager(Value, menuManager, this);
		System.gc();
	}
	
	public void connect()
	{
		
	}
	
	public void close(Object[] Value)
	{
        if(gameplay != null)
        {
            if(Value.length > 0)
            {
                ConsoleAppState.addMessage((String) Value[0]);
            }
            gameplay.close();
            gameplay = null;

        }
        Parser.parseString("createmenu menu");
        Crosshair.changeToMenuMode();
        System.gc();
	}
	
	public void exit(Object[] Value) throws InterruptedException
	{
        if(gameplay != null)
        {
            gameplay.close();
        }
		System.exit(0);
	}
	
	public AppState getAppStateByName(String Name)
	{
		return appStates.get(Name);
	}
	
	public void addAppState(String Name, AppState AppState)
	{
    	appStates.put(Name, AppState);
    	stateManager.attach(AppState);
	}
	
	public void removeAppState(String Name)
	{
		stateManager.detach(appStates.get(Name));
		appStates.remove(Name);
	}
	
	public Map<String, AppState> getAppStateList()
	{
		return appStates;
	}
	
	public InputHandler getInputHandler()
	{
		return inputHandler;
		
	}

	public PlayerSettings getPlayerSettings() 
	{
		return playerSettings;
	}
	
}
