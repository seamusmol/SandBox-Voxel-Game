package Util;

import com.jme3.math.Vector2f;

public class Vector2i
{
	int x,y = 0;
	
	public Vector2i()
	{
		
	}
	
	public Vector2i(int X, int Y)
	{
		x = X;
		y = Y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public float distance(int X, int Y)
	{
		int sx = (int) Math.pow(X-x, 2);
		int sy = (int) Math.pow(Y-y, 2);
		int dist = (int) Math.sqrt(sx+sy);
		
		return Math.abs(dist);
	}
	
	public float distance(Vector2i Vertex)
	{	
		int sx = (int) Math.pow(Vertex.getX()-x, 2);
		int sy = (int) Math.pow(Vertex.getY()-y, 2);
		int dist = (int) Math.sqrt(sx + sy);
		
		return Math.abs(dist);
	}
	
	public Vector2f getNormalizedVector(Vector2i Vector)
	{
		return new Vector2f(x - Vector.getX(), y-Vector.getY()).normalize();
	}
	
	public void addLocal(Vector2i Add)
	{
		x+= Add.x;
		y+= Add.y;
	}
	
	public void addLocal(int X, int Y)
	{
		x+= X;
		y+= Y;
	}
	
	public void addLocal(int Val)
	{
		x+= Val;
		y+= Val;
	}
	
	public void divLocal(int Div)
	{
		x/= Div;
		y/= Div;
	}
	
	public String toString()
	{
		return x + "," + y;
	}
	
}