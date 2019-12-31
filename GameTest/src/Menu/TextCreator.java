package Menu;


import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class TextCreator {

	private static String characters = "abcdefghijklmnopqrstuvwxyz0123456789-_=+<>";
	private static String[] validbits = characters.split("");
	
	public static Geometry createText(Vector2f Position, Vector2f Size, String Text, String Font, String GeomName)
	{
		String[] bits = Text.split("(?!^)");
		int[] characterlocation = new int[bits.length];
		
		for(int i = 0; i < bits.length; i++)
		{
			for(int j = 0; j < validbits.length; j++)
			{
				if(bits[i].equals(validbits[j]))
				{
					characterlocation[i] = j;
				}
			}
		}	
		Vector3f[] coords = new Vector3f[bits.length * 4];
		Vector2f[] texcoords = new Vector2f[bits.length * 4];
		int[] indexes = new int[bits.length * 6];
		
		for(int i = 0; i < bits.length; i++)
		{
			coords[i*4] = new Vector3f(Position.getX() + i * Size.getX(), Position.getY() + Size.getY(), 0);
			coords[i*4+1] = new Vector3f(Position.getX() + i * Size.getX(), Position.getY() + 0, 0);
			coords[i*4+2] = new Vector3f(Position.getX() + i * Size.getX() + Size.getX(), Position.getY() + 0, 0);
			coords[i*4+3] = new Vector3f(Position.getX() + i * Size.getX() + Size.getX(), Position.getY() + Size.getY(), 0);
			
			float posX = (int)characterlocation[i]%16;
			float posY = (int)characterlocation[i]/16;
			
			texcoords[i*4] = new Vector2f(posX * 0.0625f, 1 - posY * 0.0625f);
			texcoords[i*4+1] = new Vector2f(posX * 0.0625f, 1 - posY * 0.0625f - 0.0625f);
			texcoords[i*4+2] = new Vector2f(posX * 0.0625f + 0.0625f, 1 - posY * 0.0625f - 0.0625f);
			texcoords[i*4+3] = new Vector2f(posX * 0.0625f + 0.0625f, 1 - posY * 0.0625f);
			
			indexes[i*6] = i*4 +0;
			indexes[i*6+1] = i*4 +1;
			indexes[i*6+2] = i*4 +2;
			indexes[i*6+3] = i*4 +2;
			indexes[i*6+4] = i*4 +3;
			indexes[i*6+5] = i*4 +0;
		}
		Mesh someMesh = new Mesh();
		Geometry someGeometry = new Geometry(GeomName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		someMesh.updateBound();
		Material someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("font"));
		someGeometry.setMaterial(someMaterial);
		
		return someGeometry;
	}
	
}
