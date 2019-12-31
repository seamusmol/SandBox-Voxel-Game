package Util;

public class Vector3i
{
	int x,y,z = 0;
	
	public Vector3i()
	{
		
	}
	
	public Vector3i(int X, int Y, int Z)
	{
		x = X;
		y = Y;
		z = Z;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public void addLocal(Vector3i Add)
	{
		x+= Add.x;
		y+= Add.y;
		z+= Add.z;
	}
	
	public void divLocal(int Div)
	{
		x/= Div;
		y/= Div;
		z/= Div;
	}
	
	public String toString()
	{
		return x + "," + y + "," + z;
	}
	
}