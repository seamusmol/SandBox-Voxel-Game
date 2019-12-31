package Input;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;

import Console.Parser;
import Main.Main;
import network.ClientPacketProcessor;

public class InputHandler{

	private static Map<String, String> digitalInputMap = new HashMap<String, String>();
	private static Map<String, String> analogInputMap = new HashMap<String, String>();
	
	private static List<Integer> textInputList = new ArrayList<Integer>();
	
	private static List<String> heldKeysNames = new ArrayList<String>();
	private static List<Float> heldKeysValues = new ArrayList<Float>();
	
	private static List<String> pressedKeysNames = new ArrayList<String>();
	private static List<Float> pressedKeysValues = new ArrayList<Float>();
	
	Main main;
	float sensitivity = 2f;
	float inputReceiveCheck = 0;
	float inputReceiveCheckPass = 0;
	
	Vector2f mousePos;
	
	ClientPacketProcessor clientPacketProcessor;
	
	public InputHandler(Main Main, Parser ConsoleParser)
	{
		main = Main;
		main.getInputManager().clearMappings();
		main.getInputManager().addListener(actionListener);
		main.getInputManager().addListener(analogListener);
		main.getInputManager().addRawInputListener(textInputListener);
		
		ConsoleParser.addAllowedClass(this, "Input.InputHandler");
		ConsoleParser.addAllowedMethod("bind", "Input.InputHandler");
	}
	
	public void initGameInput(ClientPacketProcessor ClientPacketProcessor)
	{
		clientPacketProcessor = ClientPacketProcessor;
	}
	
	public void disableGameInput()
	{
		clientPacketProcessor = null;
	}
	
	public void update(float TPF) 
	{
		pressedKeysNames.clear();
		pressedKeysValues.clear();
		
		pressedKeysNames.addAll(heldKeysNames);
		pressedKeysValues.addAll(heldKeysValues);
		
		textInputList.clear();
	}
	
	public static Object[] getInput()
	{
		Object[] keys = new Object[2];
		keys[0] = pressedKeysNames;
		keys[1] = pressedKeysValues;
		
		return keys;
	}
	
	public static List<String> getTextInput()
	{
		List<String> input = new ArrayList<String>();
		
		for(int i = 0; i < textInputList.size(); i++)
		{
			input.add(getKeyBind(textInputList.get(i)));
		}
		return input;
	}
	
	public static float getKeyValue(String Key)
	{
		float value = 0;
		for(int i = 0; i < pressedKeysNames.size(); i++)
		{
			if(pressedKeysNames.get(i).equals(Key))
			{
				return pressedKeysValues.get(i);
			}
		}
		return value;
	}
	
	public static boolean hasInput(String Key)
	{
		return pressedKeysNames.contains(Key);
	}

	/*
	 * bind <key> <commandline> 
	 */
	public void bind(Object[] Value)
	{
		if(Value.length == 2)
		{
			String input = Value[0].toString().toUpperCase();
			String inputAction = Value[1].toString().toLowerCase();
			if(input.startsWith("KEY_"))
			{
				main.getInputManager().addMapping(inputAction, new KeyTrigger(getKeyBindValue(input)));
			}
			else if(input.startsWith("MOUSE_"))
			{
				int value = Integer.parseInt(input.replace("MOUSE_", ""));
				main.getInputManager().addMapping(inputAction, new MouseButtonTrigger(value));
			}
			
			digitalInputMap.put(input, inputAction);
			
			String[] triggers = new String[digitalInputMap.size()];
			int count = 0;
			for(Map.Entry<String, String> entry : digitalInputMap.entrySet())
			{
				triggers[count] = entry.getValue();
				count++;
			}
			main.getInputManager().removeListener(actionListener);
			main.getInputManager().addListener(actionListener, triggers);
		}
		else if(Value.length == 3)
		{
			String input = Value[0].toString().toUpperCase();
			String inputAction = Value[1].toString().toLowerCase();
			boolean value = Boolean.parseBoolean(Value[2].toString());
			main.getInputManager().addMapping(inputAction, new MouseAxisTrigger(getMouseBind(input), value));
			
			analogInputMap.put(input, inputAction);
			String[] triggers = new String[analogInputMap.size()];
			int count = 0;
			for(Map.Entry<String, String> entry : analogInputMap.entrySet())
			{
				triggers[count] = entry.getValue();
				count++;
			}
			
			main.getInputManager().addListener(analogListener, triggers);
		}
	}
	
	
	private int getKeyBindValue(String Input)
	{
		int value = 0;
		try
		{
			Field someField = KeyInput.class.getDeclaredField(Input);
			value = someField.getInt(null);
		} 
		catch (NoSuchFieldException e){
			e.printStackTrace();
		} 
		catch (SecurityException e){
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e){
			e.printStackTrace();
		} 
		catch (IllegalAccessException e){
			e.printStackTrace();
		}
		return value;
	}
	
	private static String getKeyBind(int KeyValue)
	{
		String value = "";
		try
		{
			for(int i = 0; i < KeyInput.class.getFields().length; i++)
			{
				Field field = KeyInput.class.getDeclaredField(KeyInput.class.getFields()[i].getName());
				
				if(field.getInt(KeyInput.class) == KeyValue)
				{
					value = KeyInput.class.getFields()[i].getName().replace("KEY_", "").toLowerCase();
				}
			}
		} 
		catch (NoSuchFieldException e){
			e.printStackTrace();
		} 
		catch (SecurityException e){
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e){
			e.printStackTrace();
		} 
		catch (IllegalAccessException e){
			e.printStackTrace();
		}
		return value;
	}
	
	public int getMouseBind(String Input)
	{
		String input = Input.replace("MOUSE_", "AXIS_");
		
		int value = 0;
		try
		{
			Field someField = MouseInput.class.getDeclaredField(input);
			value = someField.getInt(null);
		} 
		catch (NoSuchFieldException e){
			e.printStackTrace();
		} 
		catch (SecurityException e){
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e){
			e.printStackTrace();
		} 
		catch (IllegalAccessException e){
			e.printStackTrace();
		}
		return value;
	}
	
	/*
	 * TODO
	 */
	public void unbind(Object[] Value)
	{
		
	}
	
	//records input used in text fields
	private RawInputListener textInputListener = new RawInputListener()
	{
		@Override
		public void beginInput() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endInput() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onJoyAxisEvent(JoyAxisEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onJoyButtonEvent(JoyButtonEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onKeyEvent(KeyInputEvent key) {
			// TODO Auto-generated method stub
			if(key.isPressed())
			{
				textInputList.add(key.getKeyCode());
			}
		}

		@Override
		public void onMouseButtonEvent(MouseButtonEvent key) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onMouseMotionEvent(MouseMotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTouchEvent(TouchEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	//records standard action input
	private ActionListener actionListener = new ActionListener() 
	{
		@Override
		public void onAction(String KeyName, boolean KeyPressed, float TPF) 
		{
			
			if(KeyPressed)
			{
				heldKeysNames.add(KeyName);
				heldKeysValues.add(1.0f);
			}
			else
			{
				if(heldKeysNames.contains(KeyName))
				{
					heldKeysValues.remove(heldKeysNames.indexOf(KeyName));
					heldKeysNames.remove(KeyName);
				}
			}
		}
	};
	
	//records mouse input
	private AnalogListener analogListener = new AnalogListener() 
	{
		@Override
		public void onAnalog(String Name, float Value, float TPF) 
		{
			if(pressedKeysNames.contains(Name))
			{
				pressedKeysValues.set(pressedKeysNames.indexOf(Name), pressedKeysValues.get(pressedKeysNames.indexOf(Name)) + Value);
			}
			else
			{
				pressedKeysNames.add(Name);
				pressedKeysValues.add(Value);
			}
		}
	};
	
	private void importInputSettings()
	{
		
	}
	
	private void addBytes(List<Byte> List, int Val)
	{
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(Val);
		
		for(int i = 0; i < b.limit(); i++)
		{
			List.add(b.get(i));
		}
	}
	
	private void addBytes(List<Byte> List, Float Val)
	{
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putFloat(Val);
		
		for(int i = 0; i < b.limit(); i++)
		{
			List.add(b.get(i));
		}
	}
	
}
