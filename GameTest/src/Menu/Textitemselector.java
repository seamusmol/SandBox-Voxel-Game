package Menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import Console.Parser;
import Main.AssetLoaderManager;

public class Textitemselector extends Button{
	
	int x = 0;
	int y = 0;
	
	int sx = 0;
	int sy = 0;
	
	int my = 0;
	
	Vector2f pkp = new Vector2f();
	Vector2f pks = new Vector2f();
	
	boolean isVertical = true;
	
	Node textFieldNode;
	String[] lines = new String[10];
	
	public Textitemselector(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		pkp = new Vector2f(
				Arguments[11] instanceof String ? Integer.parseInt((String)Arguments[11]) : (int)Arguments[11], 
				Arguments[12] instanceof String ? Integer.parseInt((String)Arguments[12]) : (int)Arguments[12]);
		
		pks = new Vector2f(
				Arguments[13] instanceof String ? Integer.parseInt((String)Arguments[13]) : (int)Arguments[13], 
				Arguments[14] instanceof String ? Integer.parseInt((String)Arguments[14]) : (int)Arguments[14]);
		
		sx = Arguments[15] instanceof String ? Integer.parseInt((String)Arguments[15]) : (int)Arguments[15];
		sy = Arguments[16] instanceof String ? Integer.parseInt((String)Arguments[16]) : (int)Arguments[16];
		
		int importType = Arguments[17] instanceof String ? Integer.parseInt((String)Arguments[17]) : (int)Arguments[17];
		
		isVertical = Boolean.parseBoolean((String) Arguments[18]);
		
		switch(importType)
		{
			case 0:
				lines = new String[Arguments.length-19];
				
				for(int i = 19; i < Arguments.length; i++)
				{
					lines[i-19] = (String)Arguments[i];
				}
				break;
			case 1:
				lines = importConfigList((String)Arguments[19]);
				break;
			case 2:
				lines = importFolderList((String)Arguments[19]);
				break;
		}
		textFieldNode = new Node(buttonName + "text");
		pz = 0.5f;
		attachButton();
	}

	@Override
	public String getData()
	{
		if(lines.length == 0)
		{
			return "";
		}
		return lines[sy*x+y];
	}
	
	public Object[] getValue()
	{
		return new Object[]{x,y,sy*x+y};
	}
	
	public void setValue(int value)
	{
		int nx = value/sx;
		int ny = value%sy;
		
		if(ny >= lines.length && nx >= lines.length)
		{
			return;
		}
		
		if(nx < sx && ny < sy)
		{
			x = nx;
			y = ny;
		}
		attachButton();
	}
	
	public void setX(int NX)
	{
		if(NX > lines.length)
		{
			return;
		}
		x = NX;
		attachButton();
	}
	
	public void setY(int NY)
	{
		if(NY > lines.length)
		{
			return;
		}
		y = NY;
		attachButton();
	}
	
	@Override
	public boolean onClick(Vector2f MousePos)
	{
		if(hasFocus(MousePos))
		{
			//set x,y,v
			int ny = (int) ( (1.0f-Math.round(MousePos.y - position.y)/size.y) * sy);
			int nx = (int) (Math.round(MousePos.x - position.x)/size.x * sx);
			y = ny < lines.length ? ny : y;
			x = nx < lines.length ? nx : x;
			attachButton();
			
			return true;
		}
		return false;
	}
	
	@Override
	public void attachButton()
	{
		if(textFieldNode != null)
		{
			textFieldNode.detachAllChildren();
		}
		float sxx = size.x/sx;
		float syy = size.y/sy;
		
		float px = sxx * x;
		float py = syy * (sy-y - 1);
		
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText[] textfield = new BitmapText[lines.length];
		for(int i = 0; i < lines.length; i++)
		{
			float tfx = position.getX() + (sxx* (i/sy)-1) + sxx*0.05f;
			float tfy = position.getY() + (-syy *(i%sy) + size.y);
			
			textfield[i] = new BitmapText(font, false);
			textfield[i].setSize((size.getY()/sy)*0.9f);
			textfield[i].setText(lines[i]);
			textfield[i].setColor(ColorRGBA.White);
			textfield[i].setLocalTranslation(tfx + sxx*0.05f, tfy, pz+0.1f);
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
		//
		
		Vector3f[] tilePickercoords = new Vector3f[4];
		Vector2f[] tilePickerTexcoords = new Vector2f[4];
		int[] tilePickerIndices = new int[6];
		
		tilePickercoords[0] = coords[0].add(px , (py + syy), pz+0.1f).subtract(0,size.y,0);
		tilePickercoords[1] = coords[0].add(px , py, pz+0.1f).subtract(0,size.y,0);
		tilePickercoords[2] = coords[0].add(px + sxx, py, pz+0.1f).subtract(0,size.y,0);
		tilePickercoords[3] = coords[0].add(px + sxx, (py + syy), pz+0.1f).subtract(0,size.y,0);
		
		tilePickerTexcoords[0] = new Vector2f(pkp.getX()* 0.0625f + padding, 1 - pkp.getY() * 0.0625f - padding);
		tilePickerTexcoords[1] = new Vector2f(pkp.getX()* 0.0625f + padding, 1 - pkp.getY() * 0.0625f - pks.getY() * 0.0625f + padding);
		tilePickerTexcoords[2] = new Vector2f(pkp.getX()* 0.0625f + pks.getX() * 0.0625f - padding, 1 - pkp.getY() * 0.0625f - pks.getY() * 0.0625f + padding);
		tilePickerTexcoords[3] = new Vector2f(pkp.getX()* 0.0625f + pks.getX() * 0.0625f - padding, 1 - pkp.getY() * 0.0625f - padding);
		
		tilePickerIndices[0] = 0;
		tilePickerIndices[1] = 1;
		tilePickerIndices[2] = 2;
		tilePickerIndices[3] = 2;
		tilePickerIndices[4] = 3;
		tilePickerIndices[5] = 0;
		
		Mesh tilePickerMesh = new Mesh();
		Material tilePickerMaterial = null;
		Geometry tilePickerGeometry = new Geometry("tilePicker", tilePickerMesh);
		tilePickerMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(tilePickercoords));
		tilePickerMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(tilePickerTexcoords));
		tilePickerMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(tilePickerIndices));
		tilePickerMesh.updateBound();
		tilePickerMaterial = AssetLoaderManager.getMaterial("default").clone();
		tilePickerMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		tilePickerMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		tilePickerGeometry.setQueueBucket(Bucket.Gui);
		tilePickerGeometry.setMaterial(tilePickerMaterial);
		
		if(buttonNode.getChildren().size() > 0)
		{
			buttonNode.detachAllChildren();
		}
		buttonNode.attachChild(someGeometry);
		buttonNode.attachChild(tilePickerGeometry);
		buttonNode.attachChild(textFieldNode);
	}
	
	
	private String[] importConfigList(String FileName)
	{
		try 
		{
			List<String> names = new ArrayList<String>();
			BufferedReader script = new BufferedReader( new FileReader(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/config/" + FileName + ".cfg"));
			for(String line = script.readLine(); line != null ; line = script.readLine())
			{
				names.add(line);
			}
			script.close();
			
			String[] nameArray = new String[names.size()];
			for(int i = 0; i < nameArray.length; i++)
			{
				nameArray[i] = names.get(i);
			}
		} 
		catch(FileNotFoundException e) 
		{
			return new String[0];
		}
		catch(IOException e) 
		{
			return new String[0];
		}
		return new String[0];
	}
	
	private String[] importFolderList(String FolderName)
	{
		File headFolder = new File(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/" + FolderName);
		File[] files = headFolder.listFiles();
		
		List<String> fileNames = new ArrayList<String>();
		
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].getName().endsWith(".ws"))
			{
				fileNames.add(files[i].getName().replace(".ws", ""));
			}
		}
		String[] names = new String[fileNames.size()];
		for(int i = 0; i < fileNames.size(); i++)
		{
			names[i] = fileNames.get(i);
		}
		return names;
	}
	
}
