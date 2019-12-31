package Configs;

import java.util.ArrayList;
import java.util.List;

public class ServerSettings {
	
	public static int serverTCPPortIn = 2400;
	public static int serverTCPPortOut = 2399;
	public static int serverUDPPortIn = 2398;
	public static int serverUDPPortOut = 2397;
	
	public static int clientTCPPort = 2300;
	public static int clientUDPPort = 2301;
	
	public static String serverAddress = "174.45.42.126";
	
	public static int TCPOutThreadCount = 1;
	public static int TCPInThreadCount = 1;
	public static int UDPInThreadCount = 1;
	public static int UDPOutThreadCount = 1;
	public static int inBoundThreadCount = 1;
	
	public static int pingTime = 1000;
	
	public static long tickTime = 32;
	public static int maxRenderDistance = 3;
	public static int maxRigidBodyRenderDistance = 3;
	public static int snapShotLength = 10;
	
	public static List<String> bannedPlayers = new ArrayList<String>();
	public static List<String> bannedIPs= new ArrayList<String>();
	
	public static int networkMode = 0;
   
//    public static int SX = 321;
//    public static int SY = 321;

    public static int seaLevel = 64;

    public static int SkyDomeDistance = 250;

    //physical feature settings
    public static int mountainTolerance = 8;
    
    //settlementSettings
    public static int settlementDistance = 1024;
    public static int settlementOffsetDistance = 255;
//	public static int settlementCount = ((worldSize*chunkSize)/ServerSettings.settlementDistance - 1)*((worldSize*chunkSize)/ServerSettings.settlementDistance - 1) + 1;
	public static int settlementRadius = 64;
    
	public static int settlementLakeDistance = 128;
	public static int settlementRiverDistance = 128;
	public static int settlementCoastDistance = 128;
	public static int settlementMountainDistance = 128;
    
	//collision settings
	public static int playerChunkCheckDistance = 2;
	public static int playerPickDistance = 5;
	public static int playerRigidBodiesCreationTime = 100;
	
	
	public static int blockSpawnRate_Government = 90;
	public static int blockSpawnRate_Offices = 80;
	public static int blockSpawnRate_BigBusinesses = 70;
	public static int blockSpawnRate_SmallBusinesses = 60;
	public static int blockSpawnRate_Flats = 40;
	public static int blockSpawnRate_Houses = 0;
	
	public static int houseFloorHeight = 10;
	
	
	public void init()
	{
		//load server settings files(banned player list, etc.)
		//add clients to groups on first login(global)
		//allow player to join groups through console
		//create seperate list of active and non active players in group
	}
	
}
