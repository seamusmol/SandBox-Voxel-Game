package network;


import ClientServer.NetworkPacket;
import Configs.SettingsLibrary;
import Console.ConsoleAppState;

/*
 * send Messages to group
 * 
 */
public class ClientMessageHandler {

	ClientPacketProcessor packetProcessor;
	
	public ClientMessageHandler(ClientPacketProcessor PacketProcessor)
	{
		packetProcessor = PacketProcessor;
	}
	
	public void sendMessage(Object[] Value)
	{
//		NetworkPacket messagePackage = new NetworkPacket(new byte[0],SettingsLibrary.playerName, 1+"", (String)Value[0], (String)Value[1]);
//		packetProcessor.addOutBoundUDPPacket(messagePackage);
	}
	
	public static void sendMessage(String GroupName, String Message)
	{
		//playername, package id, 
		
//		NetworkPacket messagePackage = new NetworkPacket(new byte[0],SettingsLibrary.playerName, 1+"", GroupName, Message);
//		packetProcessor.addOutBoundUDPPacket(messagePackage);
	}
	
	//handle received messages
	public void processMessage(NetworkPacket Message)
	{
		//send to console
		
//		String message = "("+Message.getStringValue(2) + ")-(" + Message.getStringValue(0) + ") : " + Message.getStringValue(4);
//		ConsoleAppState.addMessage(message);
		
		//TODO
		//add chat UI element
		
	}
	
	
	
}
