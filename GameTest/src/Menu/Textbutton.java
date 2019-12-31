package Menu;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class Textbutton extends Button{

	String text = "";
	Vector2f textSize = new Vector2f();
	
	public Textbutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		for(int i = 11; i < Arguments.length; i++)
		{
			text+= Arguments[i].toString() + " ";
		}
		
		textSize = new Vector2f(size.getX()*0.75f, size.getY()*0.75f);
		updateText(text);
	}

	public void updateText(String Value)
	{
		text = Value;
		attachButton();
	}
	
	@Override
	public void attachButton()
	{
		
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText textfield = new BitmapText(font);
		
		textfield = new BitmapText(font, false);
		textfield.setSize(size.getY()*0.65f);
		textfield.setText(text);
		textfield.setColor(ColorRGBA.White);
		textfield.setLocalTranslation(position.getX() + size.getX()*0.05f, position.getY() + size.getY() * 0.9f, pz+0.1f);
		
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
		buttonNode.attachChild(textfield);
	}
	
}
