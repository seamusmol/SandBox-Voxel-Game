package Menu;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class Itemselector extends Button{
	
	int x = 0;
	int y = 0;
	
	int sx = 0;
	int sy = 0;
	
	Vector2f pkp = new Vector2f();
	Vector2f pks = new Vector2f();
	
	public Itemselector(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
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
		pz = 1.0f;
		attachButton();
	}

	public Object[] getValue()
	{
		return new Object[]{x,y,sy*x+y};
	}
	
	public void setValue(int value)
	{
		int nx = value/sx;
		int ny = value%sy;
		if(nx < sx && ny < sy)
		{
			x = nx;
			y = ny;
		}
		attachButton();
	}
	
	public void setX(int NX)
	{
		x = NX;
		attachButton();
	}
	
	public void setY(int NY)
	{
		y = NY;
		attachButton();
	}
	
	@Override
	public String getData()
	{
		return String.format(String.format("%04d", (sy*x+y)));
	}
	
	@Override
	public boolean onClick(Vector2f MousePos)
	{
		if(hasFocus(MousePos))
		{
			//set x,y,v
			x = (int) (Math.round(MousePos.x - position.x)/size.x * sx);
			y = (int) ( (1.0f-Math.round(MousePos.y - position.y)/size.y) * sy);
			attachButton();
			
			return true;
		}
		return false;
	}
	
	@Override
	public void attachButton()
	{
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
		float sxx = size.x/sx;
		float syy = size.y/sy;
		
		float px = sxx * x;
		float py = syy * (sy-y - 1);
		
		Vector3f[] tilePickercoords = new Vector3f[4];
		Vector2f[] tilePickerTexcoords = new Vector2f[4];
		int[] tilePickerIndices = new int[6];
		
		tilePickercoords[0] = coords[0].add(px , (py + syy), 0.1f).subtract(0,size.y,0);
		tilePickercoords[1] = coords[0].add(px , py, 0.1f).subtract(0,size.y,0);
		tilePickercoords[2] = coords[0].add(px + sxx, py, 0.1f).subtract(0,size.y,0);
		tilePickercoords[3] = coords[0].add(px + sxx, (py + syy), 0.1f).subtract(0,size.y,0);
		
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
//			buttonNode.detachChildNamed(buttonName);
			buttonNode.detachAllChildren();
		}
		buttonNode.attachChild(someGeometry);
		buttonNode.attachChild(tilePickerGeometry);
		
	}
	
}
