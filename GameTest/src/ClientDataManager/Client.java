package ClientDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import ClientServer.NetworkPacket;
import Configs.ServerSettings;
import Inventory.Inventory;
import Inventory.ItemAbilities;
import Main.Main;
import ServerMob.Entity;
import ServerMob.PlayerMob;
import ServerRigidBodies.ServerRigidBody;
import serverlevelgen.ServerCollisionPicker;

public class Client {

	Stack<Vector3f> positions = new Stack<Vector3f>();
	
	PlayerMob playerEntity;
	
	long timeStamp = System.currentTimeMillis();
	long lastGameModeSwitch = System.currentTimeMillis();
	long lastPrimaryAction = System.currentTimeMillis();
	long lastSecondaryAction = System.currentTimeMillis();
	
	boolean hasLoggedIn = false;
	boolean hasPinged = false;
	boolean hasReceivedPing = false;
	boolean needsTerrainBackground = true;
	
	int toggletime = 100;
	int rigidbodyDistance = 8;
	int renderDistance = 3;
	String name;
	String IP;
	
	Inventory inventory;
	
	List<Vector2f> activeChunks = new ArrayList<Vector2f>();
	List<Integer> rigidBodyIDs = new ArrayList<Integer>();
	
	Stack<Vector2f> neededChunks = new Stack<Vector2f>();
	Stack<Vector2f> removalChunks = new Stack<Vector2f>();
	
	Stack<Vector3f> cellModificationPositions = new Stack<Vector3f>();
	Stack<Integer> cellDestructionType = new Stack<Integer>();
	Stack<Integer> MaterialType = new Stack<Integer>();
	
	Stack<Integer> rigidBodyModificationID = new Stack<Integer>();
	Stack<Vector3f> rigidBodyModificationPositions = new Stack<Vector3f>();
	Stack<Integer> rigidBodyModificationType = new Stack<Integer>();
	Stack<Integer> rigidBodyMaterialType = new Stack<Integer>();
	
	Stack<ServerRigidBody> rigidbodyCreationRequests = new Stack<ServerRigidBody>();
	
	List<String> groupNames = new ArrayList<String>();
	
	Stack<NetworkPacket> clientPackets = new Stack<NetworkPacket>();
	Stack<NetworkPacket> outboundTCP = new Stack<NetworkPacket>();
	Stack<NetworkPacket> outboundUDP = new Stack<NetworkPacket>();
	
	public Client(Vector3f StartPosition, int RenderDistance, String Name, String InitIP, Main Main, ServerCollisionPicker CollisionPicker)
	{
		renderDistance = RenderDistance;
		name = Name;
		IP = InitIP;
		inventory = new Inventory(this, Main, CollisionPicker);
		playerEntity = new PlayerMob(StartPosition);
		playerEntity.setGameMode(1);
	}
	
	
	public void primary()
	{
		if(System.currentTimeMillis() - getLastPrimaryAction() >= ServerSettings.playerRigidBodiesCreationTime)
		{
			getInventory().primary();
			setLastPrimaryAction(System.currentTimeMillis());
		}
	}
	
	public void secondary()
	{
		if(System.currentTimeMillis() - getLastSecondaryAction() >= ServerSettings.playerRigidBodiesCreationTime)
		{
			getInventory().secondary();
			setLastSecondaryAction(System.currentTimeMillis());
		}
	}
	
	public void setToolValue(int ToolSlot,String Tool, int Value)
	{
		getInventory().setToolValue(ToolSlot, Tool, Value);
	}
	
	public void setCubeRotation(Quaternion Rot)
	{
		playerEntity.setCubeRotation(Rot);
	}
	
	public void switchMaterial(int NewMaterial)
	{
		inventory.setTerrainMaterial(NewMaterial);
	}
	
	public boolean needsTerrainBackground()
	{
		return needsTerrainBackground;
	}
	
	public Quaternion getCubeRotation() {
		return playerEntity.getCubeRotation();
	}

	public void setNeedsTerrainBackground(boolean Value)
	{
		needsTerrainBackground = Value;
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public void addInboundPacket(NetworkPacket Packet)
	{
		if(Packet.getIndentifierData()[0] == 4)
		{
			if((long)Packet.castData(new int[]{1})[0] + ServerSettings.tickTime > System.currentTimeMillis())
			{
				clientPackets.add(Packet);
			}
		}
		else
		{
			clientPackets.add(Packet);
		}
	}
	
	public void setSkill(int Slot, int SkillID)
	{
		inventory.setSkill(Slot, SkillID);
	}
	
	public boolean hasInput()
	{
		for(int i = 0; i < clientPackets.size(); i++)
		{
			if(clientPackets.get(i).getIndentifierData()[0] == 4)
			{
				return true;
			}
		}
		return false;
	}

	public long getLastPrimaryAction() {
		return lastPrimaryAction;
	}

	public long getLastSecondaryAction() {
		return lastSecondaryAction;
	}

	public void setLastPrimaryAction(long lastPrimaryAction) {
		this.lastPrimaryAction = lastPrimaryAction;
	}

	public void setLastSecondaryAction(long lastSecondaryAction) {
		this.lastSecondaryAction = lastSecondaryAction;
	}

	public Vector3f GetCameraPosition()
	{
		return playerEntity.getPosition().add(playerEntity.getCameraOffset());
	}
	
	public boolean withinRigidBodyRange(Vector3f BodyPosition, int ChunkSize)
	{
		int bodyIDX = (int)BodyPosition.getX() / ChunkSize;
        int bodyIDZ = (int)BodyPosition.getZ() / ChunkSize;
		
		int clientIDX = (int)playerEntity.getPosition().getX() / ChunkSize;
        int clientIDZ = (int)playerEntity.getPosition().getZ() / ChunkSize;
		
        if(Math.abs(clientIDX - bodyIDX) <= rigidbodyDistance && Math.abs(clientIDZ - bodyIDZ) <= rigidbodyDistance)
        {
        	return true;
        }
		return false;
	}
	
	public boolean hasChunkRequest(Vector2f ChunkPosition)
	{
		return neededChunks.contains(ChunkPosition);
	}
	
	public Stack<NetworkPacket> getClientPackets() {
		return clientPackets;
	}

	public Stack<NetworkPacket> getOutboundTCP() {
		return outboundTCP;
	}

	public Stack<NetworkPacket> getOutboundUDP() {
		return outboundUDP;
	}

	public boolean inGroup(String GroupName)
	{
		return groupNames.contains(GroupName);
	}
	
	public String getIP() {
		return IP;
	}

	public boolean hasLoggedIn() {
		return hasLoggedIn;
	}

	public boolean hasPinged() {
		return hasPinged;
	}

	public boolean hasReceivedPing() {
		return hasReceivedPing;
	}

	public void setCubePosition(Vector3f CubePosition)
	{
		playerEntity.setCubePosition(CubePosition);
	}
	
	public Vector3f getCubePosition()
	{
		return playerEntity.getCubePosition();
	}
	
	public Vector3f getPosition() {
		return playerEntity.getPosition();
	}
	
	public void setPosition(Vector3f Position)
	{
		playerEntity.setPosition(Position);
	}

	public Quaternion getRotation()
	{
		return playerEntity.getRotation();
	}
	
	public void setRotation( Quaternion Rot)
	{
		playerEntity.setRotation(Rot);
	}

	public int getRenderDistance() 
	{
		return renderDistance;
	}

	public void addActiveChunk(Vector2f Pos)
	{
		activeChunks.add(Pos);
	}
	
	public void addOutBoundTCP(NetworkPacket Packet)
	{
		outboundTCP.add(Packet);
	}
	
	public void addOutBoundUDP(NetworkPacket Packet)
	{
		outboundUDP.add(Packet);
	}
	
	public boolean hasChunk(Vector2f Pos)
	{
		for(int i = 0; i < activeChunks.size(); i++)
		{
			if(Pos.getX() == activeChunks.get(i).getX() && Pos.getY() == activeChunks.get(i).getY())
			{
				return true;
			}
		}
		return false;
	}
	
	public List<Vector2f> getActiveChunks() {
		return activeChunks;
	}
	
	public void setActiveChunks(List<Vector2f> ActiveChunks)
	{
		activeChunks = ActiveChunks;
	}
	
	public String getName() {
		return name;
	}

	public Stack<Vector2f> getNeededChunks() {
		return neededChunks;
	}

	public Stack<Vector2f> getRemovalChunks() {
		return removalChunks;
	}

	public Stack<Vector3f> getCellModificationPositions() {
		return cellModificationPositions;
	}

	public Stack<Integer> getMaterialType() {
		return MaterialType;
	}

	public Stack<Integer> getCellDestructionType() {
		return cellDestructionType;
	}

	public Stack<ServerRigidBody> getRigidbodyCreationRequests() {
		return rigidbodyCreationRequests;
	}

	public Stack<Vector3f> getRigidBodyModificationPositions() {
		return rigidBodyModificationPositions;
	}

	public Stack<Integer> getRigidBodyModificationType() {
		return rigidBodyModificationType;
	}

	public Stack<Integer> getRigidBodyMaterialType() {
		return rigidBodyMaterialType;
	}

	public Stack<Integer> getRigidBodyModificationID() {
		return rigidBodyModificationID;
	}

	public List<Integer> getRigidBodyIDs() {
		return rigidBodyIDs;
	}

	public PlayerMob getEntity() {
		
		return (PlayerMob) playerEntity;
	}
	
	
	
}
