package Main;

import com.jme3.app.state.AbstractAppState;

import ClientServer.ClientServerPacketProcessor;
import Configs.ServerSettings;
import Console.ConsoleAppState;
import Console.Parser;
import Menu.Crosshair;
import Menu.MenuManager;
import network.ClientPacketProcessor;
import serverlevelgen.WorldSettings;
import worldGen.MapIOUtil;

public class GamePlayManager extends AbstractAppState{

//	String currentMap = "";
	
	MenuManager menuManager;
	
	ClientServerPacketProcessor clientServerProcessor;
	ClientPacketProcessor packageProcessor;
	
	Main main;
	
	public GamePlayManager(Object[] MapData, MenuManager MenuManager, Main Main)
	{
		main = Main;
		menuManager = MenuManager;
		
		if((MapIOUtil.hasMap((String)MapData[0]) && WorldSettings.hasWorlSettings((String)MapData[0]) && ServerSettings.networkMode == 0) || MapData.length != 1)
		{
			packageProcessor = new ClientPacketProcessor(main, "", 2400);
			
			switch(ServerSettings.networkMode)
			{
				case 0:
					//create map data
					clientServerProcessor = new ClientServerPacketProcessor(MapData, packageProcessor, main);
	//				packageProcessor.init(clientServerProcessor);
					break;
				case 1:
					//lan
					packageProcessor = new ClientPacketProcessor(main, "174.45.42.126", 2400);
					break;
				case 2:
					//online/not hosting
					packageProcessor = new ClientPacketProcessor(main, "174.45.42.126", 2400);
					break;
			}
			packageProcessor.init(clientServerProcessor);
			
			if(ServerSettings.networkMode == 2)
			{
	//			if(!packageProcessor.login())
	//			{
	//				disconnected("Unable to connect to server.");
	//				return;
	//			}
			}
			menuManager.clearmenu();
			Crosshair.changeToGamePlayMode();
			main.getStateManager().attach(this);
			main.getStateManager().attach(packageProcessor);
		}
		else
		{
			Parser.parseString("close");
		}
	}
	
	@Override
	public void update(float tpf)
	{
		
	}
	@Override
	public void cleanup()
	{
		
	}
	
	public void disconnected(String Reason)
	{
		//display message
		if(packageProcessor != null)
		{
			packageProcessor.cleanup();
		}
		ConsoleAppState.addMessage("Disconnected: " + Reason);
		close();
		Parser.parseString("close");
	}
	
	public void close()
	{
		main.getStateManager().detach(this);
		if(packageProcessor != null)
		{
			main.getStateManager().detach(packageProcessor);
			packageProcessor.cleanup();
		}
        if(clientServerProcessor != null)
        {
            clientServerProcessor.cleanup();
        }
	}
	
}
