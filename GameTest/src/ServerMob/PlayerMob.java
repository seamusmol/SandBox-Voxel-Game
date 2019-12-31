package ServerMob;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import ClientDataManager.Client;
import ClientServer.NetworkPacket;

public class PlayerMob extends Mob {

	int searchDistance = 100;
	int searchX = 50;
	int searchZ = 32;
	
	int gameMode = 1;
	
	float velocity = 6f;
	
	private Vector3f position = new Vector3f();
	
	Vector3f cubePosition = new Vector3f();
	Quaternion cubeRotation = new Quaternion();
	Vector3f cameraOffset = new Vector3f(0, 1.75f,0);
	
	Vector2f startPosition;
	Vector2f searchPosition;
	
	Vector3f direction = new Vector3f();
	Quaternion rotation = new Quaternion();
	
	Vector3f bouyancyOffset = new Vector3f(0.75f, 1.25f, 0.75f);
	
	public PlayerMob(Vector3f StartPosition)
	{
		startPosition = new Vector2f(StartPosition.x, StartPosition.y);
		searchPosition = new Vector2f(StartPosition.x, StartPosition.y);
		
		this.position = StartPosition;
		this.health = 100;
		this.mass = 100;
		this.isFlammable = true;
		this.isMovable = false;
		
		generateControl();
	}
	
	public void generateControl()
	{
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 2f, 1);
		control = new CharacterControl(capsuleShape, 0.5f);
		((CharacterControl) control).setGravity( new Vector3f(0,-9.8f,0));
		((CharacterControl) control).setPhysicsLocation( position);
	}
	
	public void generateCollisionPoints()
	{
		
	}
	
	public void processPlayerInput(EntityManager EntityManager, NetworkPacket Packet, Client Client, float tpf) 
    {
        byte[] data = Packet.getData();

        int byteCount = 0;
        long timeStamp = ((data[byteCount++] & 0xFFL) << 56)
                | ((data[byteCount++] & 0xFFL) << 48)
                | ((data[byteCount++] & 0xFFL) << 40)
                | ((data[byteCount++] & 0xFFL) << 32)
                | ((data[byteCount++] & 0xFFL) << 24)
                | ((data[byteCount++] & 0xFFL) << 16)
                | ((data[byteCount++] & 0xFFL) << 8)
                | ((data[byteCount++] & 0xFFL) << 0);

        int rix = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
        int riy = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
        int riz = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
        int riw = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;

        float rx = Float.intBitsToFloat(rix);
        float ry = Float.intBitsToFloat(riy);
        float rz = Float.intBitsToFloat(riz);
        float rw = Float.intBitsToFloat(riw);

        boolean isNaN = Float.isNaN(rx) || Float.isNaN(ry) || Float.isNaN(rz) || Float.isNaN(rw);
        if (!isNaN) 
        {
        	rotation = new Quaternion(rx, ry, rz, rw);
        }

        direction = new Vector3f();
        
        byte[] inputData = new byte[data.length - byteCount];
        for (int i = 0; i < inputData.length; i++) 
        {
            inputData[i] = data[byteCount++];
        }
        String inputString = new String(inputData);

        for (int i = 0; i < inputString.length() ; i+=3) 
        {
            String curInput = inputString.substring(i, i + 3);

            if (gameMode == 0) 
            {
                switch (curInput) {
                
                	case "gm1":
                		gameMode = 1;
                		break;
                	case "fwd":
                		direction = direction.add( getMovement(1.0f, "FORWARD/BACKWARD"));
                        break;
                    case "bkw":
                    	
                    	direction = direction.add( getMovement(-1.0f, "FORWARD/BACKWARD"));
                        break;
                    case "lft":
                    	direction = direction.add( getMovement(1.0f, "LEFT/RIGHT"));
                        break;
                    case "rgt":
                    	direction = direction.add( getMovement(-1.0f, "LEFT/RIGHT"));
                        break; 
                    case "spc":
                        break;
                    case "m_p":
                        Client.primary();
                        break;
                    case "m_s":
                        Client.secondary();
                        break;
                    case "swm":
                        int Material = Integer.parseInt(inputString.substring(i + 3, i + 7));
                        i++;
                        Client.switchMaterial(Material);
                        break;
                    case "vpt":
                    	String primarySelector = inputString.substring(i + 3, i + 6);
            			int primaryVal = Integer.parseInt(inputString.substring(i + 6, i + 10));
            			i+=7;
            			Client.setToolValue(0, primarySelector, primaryVal);
                    	break;
                    case "vst":
                    	String secondarySelector = inputString.substring(i + 3, i + 6);
            			int secondaryVal = Integer.parseInt(inputString.substring(i + 6, i + 10));
            			i+=7;
            			Client.setToolValue(1, secondarySelector, secondaryVal);
                    	break;
                }
            } 
            else if (gameMode == 1) 
            {
                switch (curInput) 
                {
                	case "gm0":
                		gameMode = 0;
                		break;
                	case "fwd":
                		direction = direction.add( getDirection(1.0f, "FORWARD/BACKWARD"));
//                        direction = direction.add( getDirection(tpf * velocity, "FORWARD/BACKWARD"));
                        break;
                    case "bkw":
                    	direction = direction.add( getDirection(-1.0f, "FORWARD/BACKWARD"));
//                    	direction = direction.add( getDirection(-tpf * velocity, "FORWARD/BACKWARD"));
                        break;
                    case "lft":
                    	direction = direction.add( getDirection(1.0f, "LEFT/RIGHT"));
//                    	direction = direction.add( getDirection(tpf * velocity, "LEFT/RIGHT"));
                        break;
                    case "rgt":
                    	direction = direction.add( getDirection(-1.0f, "LEFT/RIGHT"));
//                    	direction = direction.add( getDirection(-tpf * velocity, "LEFT/RIGHT"));
                        break;
                    case "upp":
                    	direction = direction.add( getDirection(1.0f, "UP/DOWN"));
//                    	direction = direction.add( getDirection(tpf * velocity, "UP/DOWN"));
                        break;
                    case "dwn":
                    	direction = direction.add( getDirection(-1.0f, "UP/DOWN"));
//                    	direction = direction.add( getDirection(-tpf * velocity, "UP/DOWN"));
                        break;
                    case "m_p":
                        Client.primary();
                        break;
                    case "m_s":
                        Client.secondary();
                        break;
                    case "swm":
                        int Material = Integer.parseInt(inputString.substring(i * 3 + 3, i * 3 + 7));
                        i++;
                        Client.switchMaterial(Material);
                        break;
                    case "spc":
                        break;
                }
            }
            if(curInput.substring(0, 2).equals("ps"))
            {
            	Client.setSkill(1, Integer.parseInt(curInput.substring(2, 3)));
            }
            if(curInput.substring(0, 2).equals("ss"))
            {
            	Client.setSkill(2, Integer.parseInt(curInput.substring(2, 3)));
            }
        }
        OnMove(EntityManager, tpf);
    }
	
	private Vector3f getMovement(float Value, String Direction)
    {
    	Vector3f vel = new Vector3f();
        switch (Direction) {
            case "FORWARD/BACKWARD":
                vel = rotation.getRotationColumn(2, vel);
                break;
            case "LEFT/RIGHT":
                vel = rotation.getRotationColumn(0, vel);
                break;
            case "UP/DOWN":
                vel = rotation.getRotationColumn(1, vel);
                break;
        }
        vel.setY(0).normalizeLocal().multLocal(Value);
        
        return vel;
    }
    
    private Vector3f getDirection(float Value, String Direction) 
    {
        Vector3f vel = new Vector3f();
        switch (Direction) {
            case "FORWARD/BACKWARD":
                vel = rotation.getRotationColumn(2, vel);
                break;
            case "LEFT/RIGHT":
                vel = rotation.getRotationColumn(0, vel);
                break;
            case "UP/DOWN":
                vel = rotation.getRotationColumn(1, vel);
                break;
        }
        vel.multLocal(Value);
        return vel;
    }
    
	@Override
	public void OnUpdate(EntityManager EntityManager, float tpf) 
	{
		position = new Vector3f(((CharacterControl) control).getPhysicsLocation().clone());
		
	}
	
	@Override
	public void OnImpact(EntityManager EntityManager, float tpf) 
	{
		
	}

	@Override
	public void OnMove(EntityManager EntityManager, float tpf) 
	{
		if(gameMode == 0)
		{
			short val = EntityManager.serverChunkManager.getVertexValue( position.add(bouyancyOffset));
			short val2 = EntityManager.serverChunkManager.getVertexValue( position.add(bouyancyOffset).add( new Vector3f(0,0.5f,0)));
			if(val == 1023 || val2 == 1023)
			{
				velocity = 1.5f;
				
				//if swimming
				if(this.hasWaterBuoyancy)
				{
					Vector3f up = new Vector3f();
					if(direction.getX()!= 0 && direction.getZ() != 0)
					{
						up = rotation.getRotationColumn(2, new Vector3f()).setX(0).setZ(0);
					}
					
					if(val2 != 1023)
					{
						up = up.getY() <= 0.0f ? up : new Vector3f();
						
						Vector3f movement = direction.add(up).mult(velocity).mult(tpf);
						((CharacterControl) control).setWalkDirection( movement);
                        ((CharacterControl) control).setGravity( new Vector3f(0,0,0));
					}
					else
					{
						Vector3f movement = direction.add(up).mult(velocity).mult(tpf);
						((CharacterControl) control).setWalkDirection( movement);
                        ((CharacterControl) control).setGravity( new Vector3f(0,0,0));
					}
				}
				else
				{
					((CharacterControl) control).setWalkDirection( direction.mult(velocity).mult(tpf));
                    ((CharacterControl) control).setGravity( new Vector3f(0, -2.45f,0));
				}
			}
			else
			{
				velocity = 2.8f;
				
				Vector3f movement = direction.mult(velocity).mult(tpf).setY(0.0f);
				((CharacterControl) control).setWalkDirection( movement);
                ((CharacterControl) control).setGravity( new Vector3f(0, -9.8f,0));
			}
		}
		else if(gameMode == 1)
		{
			velocity = 12f;
			position = position.add( direction.mult(velocity).mult(tpf));
			
			((CharacterControl) control).setPhysicsLocation(position);
			((CharacterControl) control).setGravity( new Vector3f(0,0,0));
		}
	}
	
	@Override
	public void OnFire(EntityManager EntityManager, float tpf) 
	{	
		
	}
	
	@Override
	public CharacterControl getControl()
	{
		return ((CharacterControl) control);
	}
	
	public void setHasBuoyancy(boolean HasBuoyancy)
	{
		this.hasWaterBuoyancy = HasBuoyancy;
	}
	
	public void setGameMode(int NewGameMode)
	{
		this.gameMode = NewGameMode;
	}
	
	public int getGameMode()
	{
		return this.gameMode;
	}

	public int getSearchDistance() {
		return searchDistance;
	}

	public void setSearchDistance(int searchDistance) {
		this.searchDistance = searchDistance;
	}

	public int getSearchX() {
		return searchX;
	}

	public void setSearchX(int searchX) {
		this.searchX = searchX;
	}

	public int getSearchZ() {
		return searchZ;
	}

	public void setSearchZ(int searchZ) {
		this.searchZ = searchZ;
	}

	public float getVel() {
		return velocity;
	}

	public void setVel(float vel) {
		this.velocity = vel;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getCubePosition() {
		return cubePosition;
	}

	public void setCubePosition(Vector3f cubePosition) {
		this.cubePosition = cubePosition;
	}

	public Quaternion getCubeRotation() {
		return cubeRotation;
	}

	public void setCubeRotation(Quaternion cubeRotation) {
		this.cubeRotation = cubeRotation;
	}

	public Vector3f getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(Vector3f cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	public Vector2f getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Vector2f startPosition) {
		this.startPosition = startPosition;
	}

	public Vector2f getSearchPosition() {
		return searchPosition;
	}

	public void setSearchPosition(Vector2f searchPosition) {
		this.searchPosition = searchPosition;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}
	
	
	
}
