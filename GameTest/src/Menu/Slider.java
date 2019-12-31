package Menu;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

/*
 * 
 * Slider
 * 2 buttons
 * 
 */
public class Slider extends Button {

	boolean isVertical;
	
	int sliderValue = 0;
	int maxValue = 100;
	
	public Slider(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		super(Arguments, ParentNode, ScreenHeight, ScreenHeight);
		isVertical = Boolean.getBoolean(Arguments[10].toString());
	}
	
	public void increase()
	{
		if(sliderValue < maxValue)
		{
			sliderValue++;
		}
	}
	
	public void decrease()
	{
		if(sliderValue > 0)
		{
			sliderValue--;
		}
	}
	
	public void setValue(int NewValue)
	{
		sliderValue = NewValue;
	}
	
	@Override
	public String getData()
	{
		return sliderValue+"";
	}
	
	@Override
	public boolean onClick(Vector2f MousePos)
	{
		boolean value = false;
		
		switch(getSelectedElement(MousePos))
		{
			case 0:
				break;
			case 1:
				System.out.println(sliderValue);
				float location = MousePos.getX();
				float test = position.getX() + size.getX()*0.2f;
				float percentage = ((location - test));
				percentage /= size.getX()*0.6f;
				setValue(Math.round(percentage*100));
				value = true;
				break;
			case 2:
				System.out.println(sliderValue);
				increase();
				value = true;
				break;
			case 3:
				System.out.println(sliderValue);
				decrease();
				value = true;
				break;
		}
		
		return value;
	}

	@Override
	public boolean hasFocus(Vector2f MousePos)
	{
		Vector3f dir = new Vector3f(0,0,-1);
		Vector3f mousePos = new Vector3f(MousePos.getX(),MousePos.getY(), 1);
		Ray ray = new Ray(mousePos,dir);
		CollisionResults results = new CollisionResults();
		
		buttonNode.collideWith(ray, results);
		
		if(results.size() > 0)
		{	
			if(results.getClosestCollision().getGeometry().getName().equals(buttonName))
			{
				return true;
			}
			else if(results.getClosestCollision().getGeometry().getName().equals(buttonName + "+"))
			{
				return true;
			}
			else if(results.getClosestCollision().getGeometry().getName().equals(buttonName + "-"))
			{
				return true;
			}
		}
		return false;
	}
	
	public int getSelectedElement(Vector2f MousePos)
	{
		Vector3f dir = new Vector3f(0,0,-1);
		Vector3f mousePos = new Vector3f(MousePos.getX(),MousePos.getY(), 1);
		Ray ray = new Ray(mousePos,dir);
		CollisionResults results = new CollisionResults();
		
		buttonNode.collideWith(ray, results);
		
		if(results.size() > 0)
		{	
			if(results.getClosestCollision().getGeometry().getName().equals(buttonName))
			{
				return 1;
			}
			else if(results.getClosestCollision().getGeometry().getName().equals(buttonName + "+"))
			{
				return 2;
			}
			else if(results.getClosestCollision().getGeometry().getName().equals(buttonName + "-"))
			{
				return 3;
			}
		}
//		return 0;
		return 0;
	}
	
	public void attachNegativeButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), 1);
		coords[1] = new Vector3f(position.getX(), position.getY(), 1);
		coords[2] = new Vector3f(position.getX() + size.getX()* 0.2f, position.getY(), 1);
		coords[3] = new Vector3f(position.getX() + size.getX()* 0.2f, position.getY() + size.getY(), 1);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - padding);
		texcoords[1] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[2] = new Vector2f(texPos.getX()* 0.0625f + (texSize.getX() * 0.0625f - padding) * (0.125f), 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[3] = new Vector2f(texPos.getX()* 0.0625f + (texSize.getX() * 0.0625f - padding) * (0.125f), 1 - texPos.getY() * 0.0625f - padding);
		
		indexes[0] = 0;
		indexes[1] = 1;
		indexes[2] = 2;
		indexes[3] = 2;
		indexes[4] = 3;
		indexes[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		
		Geometry someGeometry = new Geometry(buttonName+"-", someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("button");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		
		someGeometry.setMaterial(someMaterial);
		
		if(buttonNode.getChild(buttonName+"-") != null)
		{
			buttonNode.detachChildNamed(buttonName+"-");
		}
		buttonNode.attachChild(someGeometry);
	}
	
	public void attachPlusButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(position.getX() + size.getX() * 0.8f, position.getY() + size.getY(), 1);
		coords[1] = new Vector3f(position.getX() + size.getX() * 0.8f, position.getY(), 1);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), 1);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), 1);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX()* 0.0625f + padding + (texSize.getX() * 0.0625f - padding)*0.875f, 1 - texPos.getY() * 0.0625f - padding);
		texcoords[1] = new Vector2f(texPos.getX()* 0.0625f + padding + (texSize.getX() * 0.0625f - padding)*0.875f, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[2] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[3] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - padding);
		
		indexes[0] = 0;
		indexes[1] = 1;
		indexes[2] = 2;
		indexes[3] = 2;
		indexes[4] = 3;
		indexes[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		
		Geometry someGeometry = new Geometry(buttonName+"+", someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("button");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		
		someGeometry.setMaterial(someMaterial);
		
		if(buttonNode.getChild(buttonName+"+") != null)
		{
			buttonNode.detachChildNamed(buttonName+"+");
		}
		buttonNode.attachChild(someGeometry);
	}
	
	@Override
	public void attachButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(position.getX() + size.getX()*0.2f, position.getY() + size.getY(), 1);
		coords[1] = new Vector3f(position.getX() + size.getX()*0.2f, position.getY(), 1);
		coords[2] = new Vector3f(position.getX() + size.getX() - size.getX()*0.2f, position.getY(), 1);
		coords[3] = new Vector3f(position.getX() + size.getX() - size.getX()*0.2f, position.getY() + size.getY(), 1);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - padding);
		texcoords[1] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[2] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[3] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - padding);
		
		indexes[0] = 0;
		indexes[1] = 1;
		indexes[2] = 2;
		indexes[3] = 2;
		indexes[4] = 3;
		indexes[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		
		Geometry someGeometry = new Geometry(buttonName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("button");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		
		someGeometry.setMaterial(someMaterial);
		
		if(buttonNode.getChild(buttonName) != null)
		{
			buttonNode.detachChildNamed(buttonName);
		}
		buttonNode.attachChild(someGeometry);
		
		attachPlusButton();
		attachNegativeButton();
	}
	
	
}
