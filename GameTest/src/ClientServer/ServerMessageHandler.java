package ClientServer;

import java.util.List;

import ClientDataManager.Client;
import ClientDataManager.ClientDataManager;
import Console.ConsoleAppState;

/*
 * send Messages to group
 * 
 */
public class ServerMessageHandler {

	ClientServerPacketProcessor packageProcessor;
	ClientDataManager clientDataManager;
	
	public ServerMessageHandler(ClientServerPacketProcessor PackageProcessor, ClientDataManager ClientDataManager)
	{
		packageProcessor = PackageProcessor;
		clientDataManager = ClientDataManager;
	}
	//String PlayerName, Integer PacketID, String GroupDestination, String PlayerDestination, String Message
	public void say(Object[] Data)
	{
		String message = (String) Data[0];
		String groupDestination = "global";
		
		if(Data.length > 0)
		{
			groupDestination = (String) Data[1];
		}

//		processMessage(new NetworkPacket(new byte[0],"Server", 2+"", groupDestination, "",  message));
	}
	
	public void processMessage(NetworkPacket Package_Message)
	{
		//send to console regardless of validity
		ConsoleAppState.addMessage(Package_Message);
		List<Client> clients = clientDataManager.getClientList();
			
		for(int i = 0; i < clients.size(); i++)
		{
//			if(clients.get(i).inGroup(Package_Message.getStringValue(2)))
//			{
//				NetworkPacket pack = new NetworkPacket(new byte[0],Package_Message.getStringValue(0), 2+"", Package_Message.getStringValue(2), clients.get(i).getName(), Package_Message.getStringValue(4));
////				packageProcessor.addOutBoundTCPPacket(pack);
//				clients.get(i).addOutBoundTCP(pack);
//			}
		}
	}
}
