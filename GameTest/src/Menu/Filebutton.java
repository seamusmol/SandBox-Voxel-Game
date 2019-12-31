package Menu;

import java.io.File;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;
import Util.FileChooserUtil;

public class Filebutton extends Button {

	Texture tex = null;
	String selectedFileName = "";
	int fileActionType = 0;
	public Filebutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		fileActionType = Integer.parseInt((String)Arguments[11]);
		
		attachButton();
	}

	@Override
	public String getData()
	{
		return selectedFileName;
	}
	
	@Override
	public boolean onClick(Vector2f MousePos)
	{
		boolean hasFocus = hasFocus(MousePos);
		if(hasFocus)
		{
			switch(fileActionType)
			{
				case 0:
					File lookupFile = FileChooserUtil.getFile("", "", "");
					if(lookupFile != null)
					{
						selectedFileName = lookupFile.getPath();
					}
					break;
				case 1:
					//change texture
					if(!hasAtlas)
					{
						Object[] result = FileChooserUtil.getImageTexture("", "png", "png");
						if(result != null)
						{
							tex = (Texture) result[0];
							selectedFileName = (String) result[1];
							attachButton();
						}
					}
					break;
			}
		}
		return hasFocus;
	}
	
	@Override
	public void attachButton()
	{
		if(tex == null)
		{
			tex = AssetLoaderManager.getTexture(textureName);
		}
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
		
		someMaterial.setTexture("ColorMap", tex);
		
		someGeometry.setMaterial(someMaterial);
		
		someGeometry.setLocalTranslation(0,0, pz);
		
		if(buttonNode.getChild(buttonName) != null)
		{
			buttonNode.detachChildNamed(buttonName);
		}
		
		buttonNode.attachChild(someGeometry);
	}
}
