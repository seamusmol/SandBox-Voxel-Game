package Console;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import Main.Main;

/*
 * Notes
 * allowed classes can contain duplicate references of the same object
 */
public class Parser{

	private static String scriptExtensionName = ".cfg";
	private static Map<String,String> allowedMethods;
	private static Map<String,Object> allowedClasses;
	
	//method name, class name
	public Parser()
	{
		allowedMethods = new HashMap<String,String>();
		allowedClasses = new HashMap<String, Object>();
//		for(Map.Entry<String, String> entry: allowedMethods.entrySet())
//		{
//			System.setSecurityManager(new SecurityManager());
//			Class someClass;
//			Method someMethod = null;
//			try 
//			{
//				someClass = Class.forName(entry.getValue());
//				for(int i = 0; i < someClass.getMethods().length; i++)
//				{
//					if(entry.getKey().equals(someClass.getMethods()[i].getName()))
//					{
//						someMethod = someClass.getMethods()[i];
//					}
//				}	 
//				someMethod.setAccessible(true);
//			} 
//			catch (ClassNotFoundException | SecurityException e) 
//			{
//				e.printStackTrace();
//			}
//		}
	}
	public void addAllowedClass(Object Object, String Name)
	{
		allowedClasses.put(Name, Object);
	}
	
	public void addAllowedMethod(String MethodName, String ClassName)
	{
		allowedMethods.put(MethodName, ClassName);
	}
	
	public static Object[] executeClassCommand(String ClassName, String MethodName)
	{
		Class someClass = null;
		Method method = null;
		try 
		{
			for (Map.Entry<String, Object> classEntry : allowedClasses.entrySet()) 
			{
				if(classEntry.getKey().toLowerCase().equals(ClassName.toLowerCase()))
				{
					boolean hasMethod = false;
					for (Map.Entry<String, String> methodEntry : allowedMethods.entrySet()) 
					{
						if(methodEntry.getKey().toString().toLowerCase().equals(MethodName.toLowerCase()))
						{
							hasMethod = true;
							break;
						}
					}
					if(hasMethod)
					{
						someClass = classEntry.getValue().getClass();
						for(int i = 0; i < someClass.getMethods().length; i++)
						{
							if(someClass.getMethods()[i].getName().toLowerCase().equals(MethodName.toLowerCase()))
							{
								return new Object[]{ someClass.getMethods()[i].invoke( allowedClasses.get(classEntry.getKey()))};
							}
						}
					}
					return new Object[0];
				}
			}
//			if(hasClass && hasMethod)
//			{
//				someClass = Class.forName(formattedClass);
//				Object[] data = new Object[]{ formattedMethod.invoke( allowedClasses.get(ClassName))};
//				System.out.println(data.toString());
//				return data;
//			}
		} 
		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
		}
		return new Object[0];
	}
	
	public static void parseString(String Command)
	{
		String commandLine = Command.toLowerCase();
		String[] commandBits = commandLine.split(" ");
		String commandMethod = commandBits[0];
		
		Object[] arguments = new Object[commandBits.length-1];
		
		for(int i = 1; i < commandBits.length; i++)
		{
			arguments[i-1] = commandBits[i];
		}
		
		if(commandMethod != null )
		{
			executeCommand(commandMethod, arguments);
		}
	}
	
	public static void executeCommand(String MethodName, Object[] Arguments)
	{
		for(Map.Entry<String,String> entry: allowedMethods.entrySet())
		{
			if(MethodName.equals(entry.getKey()))
			{
				Class someClass = null;
				Method someMethod = null;
				try 
				{
					someClass = Class.forName(entry.getValue());
					for(int i = 0; i < someClass.getMethods().length; i++)
					{
						if(MethodName.equals(someClass.getMethods()[i].getName()))
						{
							someClass.getMethods()[i].invoke( allowedClasses.get(entry.getValue()), (Object)Arguments);
							return;
						}
					}
				} 
				catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
				{
				}
			}
		}
	}
	
	public static Object getMethodValue(Method Method)
	{
		try {
			return (Object)Method.invoke(null, null);
		} 
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void execute(String Value)
	{
		String fileName = Value + scriptExtensionName;
		try 
		{
			BufferedReader script = new BufferedReader( new FileReader(System.getProperty("user.home") + "/Desktop/FinalBuild/assets/config/" + fileName));
			for(String line = script.readLine(); line != null ; line = script.readLine())
			{
                Parser.parseString(line);
			}
			script.close();
			
		} 
		catch(FileNotFoundException e) 
		{
			return;
		}
		catch(IOException e) 
		{
			return;
		}
	}
	
	public static void test(Object[] Value)
	{
		System.out.println(Value[0].toString());
	}
	
}
