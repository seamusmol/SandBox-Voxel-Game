package ClientDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import ClientServer.ClientServerPacketProcessor;
import ClientServer.NetworkPacket;
import Configs.ServerSettings;
import Inventory.ItemAbilities;
import Main.Main;
import Util.CompressionUtil;
import serverlevelgen.ServerCollisionPicker;
import serverlevelgen.WorldSettings;


public class ClientDataManager {

	public List<String> bannedPlayers = new ArrayList<String>();
	public List<String> bannedIPs= new ArrayList<String>();
	
	public List<String> groupNames = new ArrayList<String>();
	public Map<String, List<String>> playersInGroup = new HashMap<String, List<String>>();
	
	public List<Client> clients = new ArrayList<Client>();

	ClientServerPacketProcessor clientServerPacketProcessor;
	ServerCollisionPicker collisionPicker;
	WorldSettings worldSettings;
	Main main;
	
	public ClientDataManager(WorldSettings WorldSettings ,ClientServerPacketProcessor CSPP, Main Main, ServerCollisionPicker ServerCollisionPicker)
	{
		worldSettings = WorldSettings;
		clientServerPacketProcessor = CSPP;
		main = Main;
		collisionPicker = ServerCollisionPicker;
	}
	
	public List<Vector2f> getChunkPosition()
	{
		int radius = ServerSettings.maxRigidBodyRenderDistance;
		
		List<Vector2f> validPosition = new ArrayList<Vector2f>();
		
		for(int i = 0; i < clients.size(); i++)
		{
			int chunkIDX = (int)clients.get(i).getPosition().getX() / worldSettings.chunkSize;
	        int chunkIDZ = (int)clients.get(i).getPosition().getZ() / worldSettings.chunkSize;
			List<Vector2f> activeClientChunkList = clients.get(i).getActiveChunks();
			for(int j = 0; j < activeClientChunkList.size(); j++)
			{
				if(Math.abs(activeClientChunkList.get(j).x - chunkIDX) <= radius && Math.abs(activeClientChunkList.get(j).y - chunkIDZ) <= radius)
				{
					if(!validPosition.contains(activeClientChunkList.get(j)))
					{
						validPosition.add(activeClientChunkList.get(j));
					}
				}
			}
		}
		return validPosition;
	}
	
	//send packets containing position+rotation
	public void submitClientData()
	{
		ItemAbilities.generateClientCubePosition(clients);
		for(int i = 0; i < clients.size(); i++)
		{
			List<Byte> sendData = new ArrayList<Byte>();
			CompressionUtil.AddValueBytes(clients.get(i).getPosition().x, sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getPosition().y, sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getPosition().z, sendData);
			
			CompressionUtil.AddValueBytes(clients.get(i).getCubePosition().x, sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getCubePosition().y, sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getCubePosition().z, sendData);
			
			CompressionUtil.AddValueBytes(clients.get(i).getCubeRotation().getX(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getCubeRotation().getY(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getCubeRotation().getZ(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getCubeRotation().getW(), sendData);
			
			CompressionUtil.AddValueBytes(clients.get(i).getEntity().getGameMode(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getInventory().getPrimary().getID(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getInventory().getSecondary().getID(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getInventory().getPrimary().getSelectedSkill(), sendData);
			CompressionUtil.AddValueBytes(clients.get(i).getInventory().getSecondary().getSelectedSkill(), sendData);
			
			byte[] sendDataArray = new byte[sendData.size()];
			for(int j = 0;j < sendDataArray.length; j++)
			{
				sendDataArray[j] = sendData.get(j);
			}
			clients.get(i).addOutBoundTCP(new NetworkPacket(clients.get(i).getName(), sendDataArray , new int[]{4}));
		}
	}
	
	public Quaternion getPlayerRotation(String Name)
	{
		return getClientByName(Name).getRotation();
	}
	
	public Vector3f getPlayerPosition(String Name)
	{
		return getClientByName(Name).getPosition();
	}
	
	public void playerPinged(String Name)
	{
		getClientByName(Name).hasReceivedPing = true;
	}
	
	public void pingPlayer(String Name)
	{
		getClientByName(Name).hasPinged = true;
	}
	
	public void playerLoggedIn(String Name)
	{
		getClientByName(Name).hasLoggedIn = true;
	}
	
	public Client getPlayerByIP(String IP)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).IP.equals(IP))
			{
				return clients.get(i);
			}
		}
		return null;
	}
	
	public String getClientIP(String Name)
	{
		return getClientByName(Name).IP;
	}
	
	public Client getClientByName(String Name)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).name.equals(Name))
			{
				return clients.get(i);
			}
		}
		return null;
	}
	
	public void addNewClient(Vector3f StartPosition, String Name, String IP)
	{
		Client newClient = new Client(StartPosition, worldSettings.renderDistance, Name, IP, main, collisionPicker);
		clients.add(newClient);
	}
	
	public void removeClient(String Name)
	{
		clients.remove(Name);
	}
	
	public List<Client> getClientList()
	{
		return clients;
	}
	
	public boolean isBanned(String Name)
	{
		return bannedPlayers.contains(Name);
	}
	
	public boolean hasPlayer(String Name)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).name.equals(Name))
			{
				return true;
			}
		}
		return false;
	}
	
	public List<Vector3f> getClientPositions()
	{
		List<Vector3f> positions = new ArrayList<Vector3f>();
		for(int i = 0; i < positions.size(); i++)
		{
			positions.add(clients.get(i).playerEntity.getPosition());
		}
		return positions;
	}
	
}
