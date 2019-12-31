package Configs;

import Console.Parser;

public class SettingsLibrary {

	//graphics
	public static int anistropicFactor = 8;
	
	//network
	public static float updateTime = 16;
	public static float smoothTime = 8;
	public static String playerName = "user";
	public static String playerPassword = "password";
	
	//terrain
	public static int renderDistance = 3;
	public static int cloudRenderDistance = 15;
	public static int chunkSize = 32;
	public static int worldSize = 255;
	public static int toggletime = 200;
		
	public static boolean hasBackGround = true;
	public static boolean hasClouds = false;
	
	public static int texSize = 4096;
	public static int tileCount = 32;
	public static int deleteID = tileCount;
	public static int transRegion = 896;
	
	public static void init()
	{
		Parser.execute("config");
	}

	
}
