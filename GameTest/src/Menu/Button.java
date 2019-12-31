package Menu;


import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class Button{
	
	Vector2f position = new Vector2f();
	float pz = 0;
	Vector2f size = new Vector2f();

	Vector2f texPos = new Vector2f();
	Vector2f texSize = new Vector2f();
	
	Node parentNode;
	Node buttonNode;
	
	String buttonName;
	String commandLine = "";
	String textureName = "";
	
	boolean hasFocus = false;
	boolean isEnabled = true;
	boolean isLocked = false;
	
	boolean hasAtlas = true;
	/*
	 * newButton buttonType buttonName posx posy width height texX texY texwidth texheight
	 */
	public Button(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		buttonName = (String) Arguments[0];
		hasAtlas = Boolean.parseBoolean((String) Arguments[1]);
		textureName = (String)Arguments[2];
		
		position = new Vector2f(Float.parseFloat(Arguments[3].toString())*ScreenWidth, Float.parseFloat(Arguments[4].toString())*ScreenHeight);
		size =  new Vector2f(Float.parseFloat(Arguments[5].toString())*ScreenWidth, Float.parseFloat(Arguments[6].toString())*ScreenHeight);
		
		texPos = new Vector2f(Float.parseFloat(Arguments[7].toString()), Float.parseFloat(Arguments[8].toString()));
		texSize = new Vector2f(Float.parseFloat(Arguments[9].toString()), Float.parseFloat(Arguments[10].toString()));
		
		parentNode = ParentNode;
		buttonNode = new Node(buttonName);
		parentNode.attachChild(buttonNode);
		pz = buttonName.contains("background") ? -1.0f: 0.5f;
		isLocked = buttonName.contains("background");
		isEnabled = !buttonName.contains("background");
	}
	
	public void destroy()
	{
		parentNode.detachChildNamed(buttonName);
		position = null;
		size = null;
		texPos = null;
		texSize = null;
		parentNode = null;
		buttonName = null;
		commandLine = null;
	}
	
	public void hideButton()
	{
		buttonNode.setCullHint(CullHint.Always);
		isEnabled = false;
	}
	
	public void showButton()
	{
		buttonNode.setCullHint(CullHint.Never);
		isEnabled = isLocked ? false:true;
	}
	
	public String getData()
	{
		return buttonName;
	}
	
	public void update()
	{
	}
	
	public void onFocus(Vector2f MousePos)
	{
		if(hasFocus(MousePos))
		{
		}
	}
	
	public boolean onClick(Vector2f MousePos)
	{
		return hasFocus(MousePos);
	}
	
	public void setTexture(String TextureName)
	{
		textureName = TextureName;
		attachButton();
	}
	
	public boolean hasFocus(Vector2f MousePos)
	{
		Vector3f dir = new Vector3f(0,0,-1);
		Vector3f mousePos = new Vector3f(MousePos.getX(),MousePos.getY(), 1);
		Ray ray = new Ray(mousePos,dir);
		CollisionResults results = new CollisionResults();
		
		if(buttonNode.getChild(buttonName) != null)
		{
			buttonNode.getChild(buttonName).collideWith(ray, results);
			if(results.size() > 0)
			{	
				return true;
			}
		}
		return false;
	}
	
	public void attachButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indices = new int[6];
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), pz);
		coords[1] = new Vector3f(position.getX(), position.getY(), pz);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(),pz);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), pz);
		
		float padding = 0.000f;
		if(hasAtlas)
		{
			texcoords[0] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - padding);
			texcoords[1] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
			texcoords[2] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
			texcoords[3] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - padding);
		}
		else
		{
			texcoords[0] = new Vector2f(padding, 1 - padding);
			texcoords[1] = new Vector2f(padding, 0 + padding);
			texcoords[2] = new Vector2f(1 - padding, 0 + padding);
			texcoords[3] = new Vector2f(1 - padding, 1 - padding);
		}
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		indices[3] = 2;
		indices[4] = 3;
		indices[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		
		Geometry someGeometry = new Geometry(buttonName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indices));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("default").clone();
		
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(textureName));
		
		someGeometry.setMaterial(someMaterial);
		
		someGeometry.setLocalTranslation(0,0, pz);
		
		if(buttonNode.getChild(buttonName) != null)
		{
			buttonNode.detachChildNamed(buttonName);
		}
		
		buttonNode.attachChild(someGeometry);
	}
	
	/*
	public void updateGeometry()
	{
		Spatial someSpatial = (Geometry)parentNode.getChild(buttonName);
		Geometry someGeometry = (Geometry) someSpatial;
		someGeometry.getMaterial().setBoolean("onFocus", hasFocus);
		someGeometry.updateGeometricState();
	}
	*/
	
	public void setFocus(boolean Focus)
	{
		hasFocus = Focus;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setLock(boolean Val)
	{
		isLocked = Val;
	}

	public void setEnabled(boolean IsEnabled) {
		isEnabled = IsEnabled;
	}

	public boolean getFocus()
	{
		return hasFocus;
	}
	
	public String getName()
	{
		return buttonName;
	}
	
}