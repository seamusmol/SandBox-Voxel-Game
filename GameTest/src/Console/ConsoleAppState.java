package Console;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

import ClientServer.NetworkPacket;
import Input.InputHandler;
import Menu.TextField;
import Menu.Textinputfield;

public class ConsoleAppState extends AbstractAppState{

	private Camera cam;
	private Node parserNode;
	
	private Vector2f position = new Vector2f();
	private Vector2f size = new Vector2f();
	
	private static TextField consoleText;
	private Textinputfield consoleInput;
	
	private boolean isEnabled = false;
	
	private long toggleTime = 100;
	private long lastToggle = 0;
	
	public ConsoleAppState(Parser ConsoleParser)
	{
		ConsoleParser.addAllowedClass(this, "Console");
		
	}
	
	public void initialize(AppStateManager StateManager, Application Application)
	{
		if(parserNode == null)
		{
			SimpleApplication app = (SimpleApplication) Application;
	
			super.initialize(StateManager, app);
			parserNode = new Node("parserNode");
			parserNode.setCullHint(CullHint.Never);
		    
		    app.getGuiNode().attachChild(parserNode);
		    cam = app.getCamera();
		    
		    app.getGuiNode().updateLogicalState(1);
		    app.getGuiNode().updateGeometricState();
		    	
		    this.setEnabled(true);
		    position = new Vector2f(0.75f,0.75f);
		    size = new Vector2f(0.25f,0.25f);
		    initConsole();
		}
	}
	/*
	 * String,float float, float float,float float, float float
	 */
	public void initConsole()
	{
		Object[] displayArguments = new Object[]{"consoleDisplay", "true", "menu", position.getX(), position.getY(), size.getX(), size.getY(), 4,0,1,1};
//		displayArguments[0] = "consoleDisplay";
//		displayArguments[1] = "false";
//		displayArguments[2] = "menu";
//		displayArguments[3] = position.getX();
//		displayArguments[4] = position.getY();
//		displayArguments[5] = size.getX();
//		displayArguments[6] = size.getY();
//		displayArguments[7] = 4f;
//		displayArguments[8] = 0f;
//		displayArguments[9] = 1f;
//		displayArguments[10] = 1f;
		
		Object[] inputArguments = new Object[]{"consoleInput", "true", "menu", position.getX(), position.getY() - size.getY()/5, size.getX(), size.getY()/5, 4,0,1,1};
//		inputArguments[0] = "consoleInput";
//		inputArguments[1] = "false";
//		inputArguments[2] = "menu";
//		inputArguments[3] = position.getX();
//		inputArguments[4] = position.getY() - size.getY()/5;
//		inputArguments[5] = size.getX();
//		inputArguments[6] = size.getY()/5;
//		inputArguments[7] = 4f;
//		inputArguments[8] = 0f;
//		inputArguments[9] = 1f;
//		inputArguments[10] = 1f;
		
		consoleText = new TextField(displayArguments, parserNode, cam.getWidth(), cam.getHeight());
		consoleInput = new Textinputfield(inputArguments, parserNode, cam.getWidth(), cam.getHeight());
		consoleInput.setTextField(consoleText);
	}
	
	public static void addMessage(String Message)
	{
		consoleText.updateText(Message);
	}
	
	public static void addMessage(NetworkPacket Message)
	{
		consoleText.updateText("new message");
	}
	
	@Override
	public void update(float tpf) 
	{
		if(InputHandler.hasInput("toggleconsole"))
		{
			if((System.nanoTime() - lastToggle)/1000000 > toggleTime)
			{
				isEnabled = !isEnabled;
				consoleInput.setFocus(isEnabled);
				lastToggle = System.nanoTime();	
				System.out.println(parserNode.getCullHint());
			}
		}
		
		if(isEnabled)
		{
			parserNode.setCullHint(CullHint.Never);
			
//			if(consoleNode.getChildren().size() == 0)
//			{
//				consoleInput.attachButton();
//				consoleText.attachButton();
//			}
			consoleText.update();
			consoleInput.update();
			
		}
		else
		{
			parserNode.setCullHint(CullHint.Always);
		}
	}
	
}
