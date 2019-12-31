package Menu;

import java.util.List;

import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Console.Parser;
import Input.InputHandler;
import Main.AssetLoaderManager;

public class Textinputfield extends Button{
	
	String currentInput = "";
	
	TextField affectedField;
	Node textFieldNode;
	boolean hasInputFocus = false;
	
	int characterlimit;
	
	public Textinputfield(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		textFieldNode = new Node(buttonName + "text");
		characterlimit = (int) (size.getX()*0.95f / (size.getY()/2));
		pz = 0.75f;
		attachButton();
	}
	
	public void setTextField(TextField  TextField)
	{
		affectedField = TextField;
	}
	
	@Override
	public String getData()
	{
		return currentInput.replace(" ", ",");
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

	@Override 
	public boolean onClick(Vector2f MousePos)
	{
		boolean focusClicked = hasFocus(MousePos);
		hasInputFocus = focusClicked;
		
		return focusClicked;
	}
	
	private void addCharacter(String Input)
	{
		if(currentInput.length() < characterlimit)
		{
			currentInput += Input;
		}
	}
	
	private void spaceBar()
	{
		if(currentInput.length() < characterlimit)
		{
			currentInput += " ";
		}
	}
	
	private void backSpace()
	{
		if(currentInput.length() > 0)
		{
			currentInput = currentInput.substring(0, currentInput.length()-1);
		}
	}
	
	private void submitInput()
	{
		if(affectedField!= null)
		{
			affectedField.updateText(currentInput);
			Parser.parseString(currentInput);
		}
		currentInput = "";
	}
	
	@Override
	public void update()
	{
		if( hasInputFocus)
		{
			List<String> pressedKeysNames = (List<String>) InputHandler.getTextInput();
			
			if(pressedKeysNames.size() > 0)
			{
				for(int i = 0; i < pressedKeysNames.size(); i++)
				{
					if(pressedKeysNames.get(i).equals("space"))
					{
						spaceBar();
						attachButton();
					}
					else if(pressedKeysNames.get(i).equals("back"))
					{
						backSpace();
						attachButton();
					}
					else if(pressedKeysNames.get(i).equals("return"))
					{
						submitInput();
						attachButton();
					}
					else if(pressedKeysNames.get(i).length() == 1)
					{
						addCharacter(pressedKeysNames.get(i));
						attachButton();
					}
					else
					{
						
					}
				}
			}
		}
	}
	
	public void setFocus(boolean Val)
	{
		hasInputFocus = Val;
	}
	
	@Override
	public void attachButton()
	{
		if(textFieldNode != null)
		{
			textFieldNode.detachAllChildren();
		}
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText textfield = new BitmapText(font);
		float textpad = size.getY() * 0.02f;
		
		textfield.setSize(size.getY() * 0.9f);
		textfield.setText(currentInput);
		textfield.setColor(ColorRGBA.White);
		textfield.setLocalTranslation(position.getX() + size.getX()* 0.05f, position.getY() + size.getY() + textpad, pz+0.1f);
		textFieldNode.attachChild(textfield);
		
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indices = new int[6];
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), pz);
		coords[1] = new Vector3f(position.getX(), position.getY(), pz);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), pz);
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
		
		if(buttonNode.getChildren().size() > 0)
		{
			buttonNode.detachAllChildren();
		}
		buttonNode.attachChild(someGeometry);
		buttonNode.attachChild(textFieldNode);
	}
	
}
