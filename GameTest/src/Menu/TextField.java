package Menu;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class TextField extends Button{

	String[] lines = new String[10];

	Node textFieldNode;
	public TextField(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		textFieldNode = new Node(buttonName + "text");
		pz = 0.75f;
		for(int i = 0; i < lines.length; i++)
		{
			lines[i] = "";
		}
		attachButton();
	}
	
	public void updateText(String NewLine)
	{
		if(!NewLine.equals(""))
		{
			String[] newLines = new String[10];
			for(int i = 1; i < lines.length; i++)
			{
				newLines[i-1] = lines[i];
			}
			newLines[9] = NewLine;
			lines = newLines;
			attachButton();
		}
	}
	
	public void update()
	{
		attachButton();
	}
	
	@Override
	public void attachButton()
	{
		if(textFieldNode != null)
		{
			textFieldNode.detachAllChildren();
		}
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText[] textfield = new BitmapText[lines.length];
		float textpad = size.getY() * 0.02f;
		for(int i = 0; i < lines.length; i++)
		{
			textfield[i] = new BitmapText(font, false);
			textfield[i].setSize((size.getY()/lines.length)*0.9f);
			textfield[i].setText(lines[i]);
			textfield[i].setColor(ColorRGBA.White);
			textfield[i].setLocalTranslation(position.getX() + size.getX()* 0.05f, position.getY() + size.getY() - ((size.getY()/lines.length)*(i)) + textpad, pz+0.1f);
			textFieldNode.attachChild(textfield[i]);
		}
		
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
