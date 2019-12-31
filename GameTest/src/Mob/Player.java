package Mob;

import java.nio.ByteBuffer;

import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import ClientServer.NetworkPacket;
import Configs.SettingsLibrary;
import Input.InputHandler;
import Input.PlayerCamera;
import Main.AssetLoaderManager;
import Main.Main;
import RigidBodies.RigidBodyManager;
import Util.MathUtil;
import levelgen.ChunkManager;
import network.ClientPacketProcessor;

public class Player extends Mob{

	int currentSelectedAbility;
	boolean hasNoClip;
	
	float interactiveRange = 10.0f;
	
	float PLAYER_BUMPRADIUS = 0.5f;
	float PLAYER_HEIGHT = 2.0f;
	float PLAYER_MASS = 60.5f;
	float PLAYER_VELOCITY = 50;
	
	float pickDistance = 5.0f;
	
	public Vector3f PLAYER_STARTPOS = new Vector3f(512,0,512);
	public Vector3f playerPos = PLAYER_STARTPOS;
	public Vector3f serverPlayerPos;
	public Quaternion playerViewDir = new Quaternion();
	Vector3f playerUpVec = playerViewDir.getRotationColumn(1);
	
	Vector3f cameraOffSet = new Vector3f(0,1.75f,0); 
	
	private Vector3f cubeOutLinePosition = PLAYER_STARTPOS;
	private Quaternion cubeOutLineRotation = new Quaternion();
	private Geometry cubeOutLineModel;
	
	private boolean hasOutLineTool = false;
	
	boolean isActive = true;
	long lastInput = 0;
	long lastModify = 0;
	long lastGMToggle = 0;
	long lastCubeToggle = 0;
	int gamemode = 0;
	int primaryToolID = 0;
	int secondaryToolID = 0;
	int primaryToolSkill = 0;
	int secondaryToolSkill = 0;
	
	Node playerNode;
	Main main;
	ClientPacketProcessor packetProcessor;
	ChunkManager chunkManager;
	RigidBodyManager rigidBodyManager;
	PlayerCamera playercamera;
	
	PlayerGUI playerGUI;
	PlayerSettings playerSettings;
	
	public Player(Main Main, ClientPacketProcessor ClientPacketProcessor)
	{
		main = Main;
		playerSettings = main.getPlayerSettings();
		playerNode = new Node("PlayerUtil");
		packetProcessor = ClientPacketProcessor;
		playerGUI = new PlayerGUI();
		Main.addAppState("playerGUI", playerGUI);
	}
	
	public void init(ChunkManager ChunkManager, RigidBodyManager RigidBodyManager, PlayerCamera PlayerCamera)
	{
		chunkManager = ChunkManager;
		rigidBodyManager = RigidBodyManager;
		playercamera = PlayerCamera;
		
		playercamera.attachNode(playerNode);
		GenerateCubeOutLineModel();
	}
	
	public void GenerateCubeOutLineModel()
	{
		Object[] ModelData = AssetLoaderManager.getModelData("CubeOutLine");
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("CubeOutLine", someMesh);
		
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer((Vector3f[]) ModelData[0]));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer((Vector2f[]) ModelData[1]));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer((int[]) ModelData[2]));
		someMesh.updateBound();
		someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("CubeOutLine"));
		
		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		
		someGeometry.setMaterial(someMaterial);
		
		cubeOutLineModel = someGeometry;
	}
	
	public void unpackSnapShot(byte[] Data)
	{
		//receive server player location
		//move slowly towards it(average two points?)
		int byteCount = 0;
		int pix = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int piy = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int piz = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
		float px = Float.intBitsToFloat(pix);
		float py = Float.intBitsToFloat(piy);
		float pz = Float.intBitsToFloat(piz);
		
		serverPlayerPos = new Vector3f(px,py,pz);
		playerPos = new Vector3f(px,py,pz);
		
//		float disX = (px-playerPos.x)/SettingsLibrary.smoothTime;
//		float disY = (py-playerPos.y)/SettingsLibrary.smoothTime;
//		float disZ = (pz-playerPos.z)/SettingsLibrary.smoothTime;
		
		int cix = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int ciy = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int ciz = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
		float cpx = Float.intBitsToFloat(cix);
		float cpy = Float.intBitsToFloat(ciy);
		float cpz = Float.intBitsToFloat(ciz);
		
		cubeOutLinePosition = new Vector3f(cpx,cpy,cpz);
		
		int crix = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int criy = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int criz = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		int criw = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
		float crx = Float.intBitsToFloat(crix);
		float cry = Float.intBitsToFloat(criy);
		float crz = Float.intBitsToFloat(criz);
		float crw = Float.intBitsToFloat(criw);
		
		cubeOutLineRotation = new Quaternion(crx,cry,crz,crw);
		
		gamemode = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
		primaryToolID = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		secondaryToolID = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		primaryToolSkill = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		secondaryToolSkill = Data[byteCount++] & 0xFF | (Data[byteCount++] & 0xFF) << 8 | (Data[byteCount++] & 0xFF) << 16 | (Data[byteCount++] & 0xFF) << 24;
		
//		serverPlayerPos = new Vector3f(px,py,pz);
//		playerPos = new Vector3f(playerPos.x+disX, playerPos.y+disY,playerPos.z+disZ);
		
		playerGUI.updateToolGUI(gamemode, primaryToolID, secondaryToolID, primaryToolSkill, secondaryToolSkill);
	}
	
	public void update(float tpf) 
	{
		if(isActive)
		{
			String inputString = "";
			
			if(!playerGUI.isOccupied())
			{
				if(InputHandler.hasInput("mode0"))
				{
					inputString += "gm0";
				}
				
				if(InputHandler.hasInput("mode1"))
				{
					inputString += "gm1";
				}
				
				if(InputHandler.hasInput("mouse_up"))
				{
					rotateCamera(-InputHandler.getKeyValue("mouse_up"), playerViewDir.getRotationColumn(0));
				}
				if(InputHandler.hasInput("mouse_down"))
				{
					rotateCamera(InputHandler.getKeyValue("mouse_down"), playerViewDir.getRotationColumn(0));
				}
				if(InputHandler.hasInput("mouse_left"))
				{
					rotateCamera(InputHandler.getKeyValue("mouse_left"), playerUpVec);
				}
				if(InputHandler.hasInput("mouse_right"))
				{
					rotateCamera(-InputHandler.getKeyValue("mouse_right"), playerUpVec);
				}
				
				if(gamemode == 0)
				{
//					Vector2f direction = new Vector2f();
					if(InputHandler.hasInput("forward"))
					{
//						direction.x +=1f;
						inputString += "fwd";
					}
					if(InputHandler.hasInput("backward"))
					{
//						direction.x -=1f;
						inputString += "bkw";
					}
					if(InputHandler.hasInput("left"))
					{
//						direction.y -= 1f;
						inputString += "lft";
					}
					if(InputHandler.hasInput("right"))
					{
//						direction.y += 1f;
						inputString += "rgt";
					}
					
				}
				else if(gamemode == 1)
				{
					if(InputHandler.hasInput("forward"))
					{
//						moveCamera(tpf, "FORWARD/BACKWARD");
						inputString += "fwd";
					}
					if(InputHandler.hasInput("backward"))
					{
//						moveCamera(-tpf, "FORWARD/BACKWARD");
						inputString += "bkw";
					}
					if(InputHandler.hasInput("left"))
					{
//						moveCamera(tpf, "LEFT/RIGHT");
						inputString += "lft";
					}
					if(InputHandler.hasInput("right"))
					{
//						moveCamera(-tpf, "LEFT/RIGHT");
						inputString += "rgt";
					}
					if(InputHandler.hasInput("up"))
					{
//						moveCamera(tpf, "UP/DOWN");
						inputString += "upp";
					}
					if(InputHandler.hasInput("down"))
					{
//						moveCamera(-tpf, "UP/DOWN");
						inputString += "dwn";
					}
					if(InputHandler.hasInput("space"))
					{
						inputString += "spc";
					}
				}
				
				if(InputHandler.hasInput("primaryskill"))
				{
					if(InputHandler.hasInput("skill1"))
					{
						inputString += "ps1";
					}
					else if(InputHandler.hasInput("skill2"))
					{
						inputString += "ps2";
					}
					else if(InputHandler.hasInput("skill3"))
					{
						inputString += "ps3";
					}
					else if(InputHandler.hasInput("skill4"))
					{
						inputString += "ps4";
					}
				}
				else if(InputHandler.hasInput("secondaryskill"))
				{
					if(InputHandler.hasInput("skill1"))
					{
						inputString += "ss1";
					}
					else if(InputHandler.hasInput("skill2"))
					{
						inputString += "ss2";
					}
					else if(InputHandler.hasInput("skill3"))
					{
						inputString += "ss3";
					}
					else if(InputHandler.hasInput("skill4"))
					{
						inputString += "ss4";
					}
				}
				
				if(InputHandler.hasInput("mouse_primary"))
				{
					inputString += "m_p";
				}
				if(InputHandler.hasInput("mouse_secondary"))
				{
					inputString += "m_s";
				}
				
				if(System.currentTimeMillis()-lastCubeToggle > SettingsLibrary.toggletime)
				{
					if(InputHandler.hasInput("cubetoggle"))
					{
						hasOutLineTool = !hasOutLineTool;
						//inputString += "tgm";
						lastCubeToggle = System.currentTimeMillis();
					}
				}
				DrawCubeOutLine();
			}
			else if(playerGUI.isOccupied())
			{
				if(InputHandler.hasInput("mouse_primary"))
				{
					String result = playerGUI.onClick();
					String[] bits = result.split(" ");
					if(bits.length == 2)
					{
						inputString+= "vpt";
						inputString+= bits[0];
						inputString+= bits[1];
					}
				}
				else if(InputHandler.hasInput("mouse_secondary"))
				{
					String result = playerGUI.onClick();
					String[] bits = result.split(" ");
					if(bits.length == 2)
					{
						inputString+= "vst";
						inputString+= bits[0];
						inputString+= bits[1];
					}
				}
			}
			
			//timestamp, rotations,keys
			if(System.currentTimeMillis()-lastInput > SettingsLibrary.updateTime)
			{
				long timeStamp = System.currentTimeMillis();
				byte[] data = new byte[24 + inputString.getBytes().length];
				byte[] timeStampData = ByteBuffer.allocate(8).putLong(timeStamp).array();
				int byteCount = 0;
				for(int i = 0; i < timeStampData.length; i++)
				{
					data[byteCount++] = timeStampData[i];
				}
				
				byte[]rotX = ByteBuffer.allocate(4).putFloat(playerViewDir.getX()).array();
				byte[]rotY = ByteBuffer.allocate(4).putFloat(playerViewDir.getY()).array();
				byte[]rotZ = ByteBuffer.allocate(4).putFloat(playerViewDir.getZ()).array();
				byte[]rotW = ByteBuffer.allocate(4).putFloat(playerViewDir.getW()).array();
				
				for(int i = 3; i >= 0; i--)
				{
					data[byteCount++] = rotX[i];
				}
				for(int i = 3; i >= 0; i--)
				{
					data[byteCount++] = rotY[i];
				}
				for(int i = 3; i >= 0; i--)
				{
					data[byteCount++] = rotZ[i];
				}
				for(int i = 3; i >= 0; i--)
				{
					data[byteCount++] = rotW[i];
				}
				byte[] stringData = inputString.getBytes();
				for(int i = 0; i < stringData.length; i++)
				{
					data[byteCount++] = stringData[i];
				}
				
				NetworkPacket packet = new NetworkPacket(  SettingsLibrary.playerName, data, new int[]{4});
				packetProcessor.addOutBoundTCPPacket(packet);
				lastInput = timeStamp;
			}
		}
	}
	
	public Vector3f getCameraPosition()
	{
		return new Vector3f(playerPos.x + cameraOffSet.x, playerPos.y + cameraOffSet.y, playerPos.z + cameraOffSet.z);
	}
	
	public void DrawCubeOutLine()
    {
        if(hasOutLineTool)
        {
            if(!playercamera.hasGeometry("PlayerUtil","CubeOutLine"))
        	{
        		playercamera.attachChild(cubeOutLineModel, "PlayerUtil");
//        		Crosshair.setIsVisible(false);
        	}
        	cubeOutLineModel.setLocalTranslation(cubeOutLinePosition);
        	cubeOutLineModel.setLocalRotation(cubeOutLineRotation);
        }
        else
        {
//        	if(!playercamera.hasGeometry("PlayerUtil","CubeOutLine"))
//        	{
        		playercamera.removeFromNode("CubeOutLine", "PlayerUtil");
//        		Crosshair.setIsVisible(true);
//        	}
        }
    }
	
	private void moveCamera(float Value, String Direction)
	{
        Vector3f vel = new Vector3f();
        switch(Direction)
        {
        	case "FORWARD/BACKWARD":
        		vel = playerViewDir.getRotationColumn(2,vel);
        		break;
        	case "LEFT/RIGHT":
        		vel = playerViewDir.getRotationColumn(0,vel);
        		break;
        	case "UP/DOWN":
        		vel = playerViewDir.getRotationColumn(1,vel);
        		break;
        }
	    vel = vel.multLocal(Value * 12);
	    playerPos.addLocal(vel);
    }
	
	public void rotateCamera(float value, Vector3f Vector)
	{
		Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(5 * value, Vector);

        Vector3f up = playerViewDir.getRotationColumn(1);
        Vector3f left = playerViewDir.getRotationColumn(0);
        Vector3f dir = playerViewDir.getRotationColumn(2);

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion rot = new Quaternion();
        rot = rot.fromAxes(left, up, dir);
        rot = rot.normalizeLocal();
        playerViewDir = rot;
	}
	
	public Vector3f getPosition()
	{
		return playerPos;
	}
	
	public Vector3f getServerPlayerPosition()
	{
		return serverPlayerPos;
	}
	
	public void setPosition(Vector3f Position)
	{
		playerPos = Position;
	}
	
	public Quaternion getViewDirection()
	{
		return playerViewDir;
	}
}