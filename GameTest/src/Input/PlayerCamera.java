package Input;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;

import Main.Main;
import Mob.Player;

/*
 * 
 * TODO
 * implement support for different camera positions relative to player position
 * keep track of viewports and rootnodes
 * add closeup viewport, attached closeupviewport to a closeupviewportmanager
 */
public class PlayerCamera{
	
	Camera cam;
	
	Main main;
	
	private ViewPort viewPort;
	private Node cameraRootNode;
	
	private Camera menuCamera;
	
//	private Vector3f CameraOffset = new Vector3f(0, 1.75f,0);
	
	private int camMode = 0;
	
	//entities affected by playerCamera
	Player player;
	
	public PlayerCamera(Main Main,Player Player)
	{
		player = Player;
		main = Main;
		menuCamera = main.getCamera().clone();
		cameraRootNode = new Node("RootNode");
		cameraRootNode.setCullHint(CullHint.Never);
	    cam = main.getCamera();
	    
	    viewPort = main.getRenderManager().createMainView("playerCamera", cam);
	    
	    viewPort.setEnabled(true);
	    viewPort.setClearFlags(true, true, true);
	    viewPort.setBackgroundColor(ColorRGBA.Blue);
	    viewPort.attachScene(cameraRootNode);
	   
	    cam.setFrustumPerspective( 95.0f, 1.7777f, 0.125f, 1000.0f);
	    cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
	    
	    cam.setLocation(player.getCameraPosition());
	   
	    cam.update();
	    
	    AmbientLight sun = new AmbientLight();
	    sun.setColor(ColorRGBA.White.mult(1.0f));
	    
	    cameraRootNode.addLight(sun);
	    
	    cameraRootNode.updateLogicalState(1);
	    cameraRootNode.updateGeometricState();
	}
	
	public void cleanup()
	{
		main.getGuiNode().detachChildNamed("PopupNode");
		main.getGuiNode().detachChildNamed("PlayerGUINode");
		main.getRenderManager().removeMainView("playerCamera");
	    cam.copyFrom(menuCamera);
	    cam.update();
	}
	
	public void update()
	{
		cam.setLocation(player.getCameraPosition());
		cam.setRotation(player.getViewDirection());
		cameraRootNode.updateLogicalState(1);
	    cameraRootNode.updateGeometricState();
	}																																																																																												
	
	public void attachToRoot(final Geometry Geom)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
	        	cameraRootNode.attachChild(Geom);
	            return null;
	        }
	    });
	}
	
	public void attachNode(final Node NewNode)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
	        	cameraRootNode.attachChild(NewNode);
	            return null;
	        }
	    });
	}
	
	public void attachChild(final Geometry Geometry, final String NodeName)
	{	
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
	        	
	        	if(cameraRootNode.getChild(NodeName) == null)
	        	{
	        		return null;
	        	}
	        	Node node = (Node) cameraRootNode.getChild(NodeName);
	        	
	        	for(int i = 0; i < node.getChildren().size(); i++)
	    		{
	    			if(node.getChild(i).getName().equals(Geometry.getName()))
	    			{
	    				node.detachChildAt(i);
	    			}
	    		}
	        	
	        	if(Geometry == null)
	        	{
	        		return null;
	        	}
	        	
	        	node.attachChild(Geometry);
	            return null;
	        }
	    });
	}
	
	public void attachChild(final Geometry Geometry, final String NodeName, final FloatBuffer BF1, final FloatBuffer BF2)
	{	
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
	        	
	        	if(cameraRootNode.getChild(NodeName) == null)
	        	{
	        		return null;
	        	}
	        	Node node = (Node) cameraRootNode.getChild(NodeName);
	        	
	        	for(int i = 0; i < node.getChildren().size(); i++)
	    		{
	    			if(node.getChild(i).getName().equals(Geometry.getName()))
	    			{
	    				node.detachChildAt(i);
	    			}
	    		}
	        	
	        	if(Geometry == null)
	        	{
	        		return null;
	        	}
	        	
	        	Mesh mesh = Geometry.getMesh();
	   		 	mesh.setBuffer(Type.TexCoord2, 2, BF1);
	   		 	mesh.setBuffer(Type.TexCoord3, 2, BF2);
	   		 	mesh.updateBound();
	        	node.attachChild(Geometry);
	            return null;
	        }
	    });
	}
	
	public void attachChild(final List<Geometry> GeometryList, final List<String> NodeNames)
	{	
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
	        	
	        	for(int i = 0; i < GeometryList.size(); i++)
	        	{
	        		if(cameraRootNode.getChild(NodeNames.get(i)) == null)
		        	{
		        		continue;
		        	}
		        	Node node = (Node) cameraRootNode.getChild(NodeNames.get(i));
		        	
//		        	for(int j = 0; j < node.getChildren().size(); j++)
//		    		{
//		    			if(node.getChild(j).getName().equals(Geometry.getName()))
//		    			{
//		    				node.detachChildAt(j);
//		    			}
//		    		}
		        	
		        	if(GeometryList.get(i) == null)
		        	{
		        		continue;
		        	}
		        	node.attachChild(GeometryList.get(i));
	        	}
	            return null;
	        }
	    });
	}
	
	
	public void updateGeometryPositions(final List<Geometry> GeometryList, final List<Vector3f> Positions, final List<String> NodeNames)
	{
		main.enqueue(new Callable<Spatial>() {
	        public Spatial call() throws Exception {
	        	
	        	for(int i = 0; i < GeometryList.size(); i++)
	        	{
	        		if(cameraRootNode.getChild(NodeNames.get(i)) == null)
		        	{
		        		continue;
		        	}
		        	if(GeometryList.get(i) == null)
		        	{
		        		continue;
		        	}
		        	GeometryList.get(i).setLocalTranslation(Positions.get(i));
	        	}
	            return null;
	        }
	    });
		
	}
	
	public void removeFromNode(final String Name, final String NodeName)
	{
		main.enqueue(new Callable<Spatial>() 
		{
	        public Spatial call() throws Exception 
	        {
	        	if(NodeName == null)
	        	{
	        		return null;
	        	}
	        	
	        	if(cameraRootNode.getChild(NodeName) == null)
	        	{
	        		return null;
	        	}
	        	Node node = (Node) cameraRootNode.getChild(NodeName);
	        	
	        	for(int i = 0; i < node.getChildren().size(); i++)
	    		{
	    			if(node.getChild(i).getName().equals(Name))
	    			{
	    				node.detachChildAt(i);
	    				return null;
	    			}
	    		}
	            return null;
	        }
	    });
	}
	
	public Vector3f getServerPosition()
	{
		return player.getServerPlayerPosition();
	}
	
	public boolean hasGeometry(String NodeName, String GeometryName)
	{
		if(cameraRootNode.getChild(NodeName) == null)
		{
			return false;
		}
		Node node = (Node) cameraRootNode.getChild(NodeName);
		if(node != null)
		{
			return node.getChild(GeometryName) != null;
		}
		return false;
	}
	
	public boolean hasNode(String Name)
	{
		return cameraRootNode.getChild(Name) != null;
	}
	
	public Node getRootNode()
	{
		return cameraRootNode;
	}
	
	public Vector3f getPosition()
	{
		return cam.getLocation();
	}

}
