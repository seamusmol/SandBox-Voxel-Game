package Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jme3.math.Vector3f;

import worldGen.Cell;

public class GreedyMesher {

	public static Object[] OptimizeBuffers(List<Vector3f> VertexList, List<Integer> Indices)
	{
		List<Vector3f> reduced = new ArrayList<Vector3f>();
		List<Integer> newIndices = new ArrayList<Integer>();
		for(int i = 0; i < VertexList.size(); i++)
		{
			if(!reduced.contains(VertexList.get(i)))
			{
				reduced.add(VertexList.get(i));
			}
		}
		
		Vector3f v = null;
		for(int i = 0; i < Indices.size(); i++)
		{
			v = VertexList.get(Indices.get(i));
			newIndices.add( reduced.indexOf(v));
		}
		
//		for(int i = 0; i < reduced.size(); i++)
//		{
//			for(int j = 0; j < Indices.size(); j++)
//			{
//				if(reduced.get(i).equals(VertexList.get(Indices.get(j))))
//				{
//					newIndices.add(i);
//				}
//			}
//		}
		
//		System.out.println("-----------");
//		System.out.println(VertexList.size());
//		System.out.println(reduced.size());
//		System.out.println(newIndices.size());
		Vector3f[] vertexArray = new Vector3f[reduced.size()];
		for(int i = 0; i < reduced.size(); i++)
		{
			vertexArray[i] = reduced.get(i);
		}
		int[] indexArray = new int[newIndices.size()];
		for(int i = 0; i < newIndices.size(); i++)
		{
			indexArray[i] = newIndices.get(i);
		}
		
		return new Object[]{vertexArray,indexArray};
	}
	
	public static List<Cell> greedify(List<Cell> CellList)
	{
		Map<Integer, List<Cell>> sortedCaseMap = new TreeMap<Integer, List<Cell>>(); 
		
		for(int i = 0; i < CellList.size(); i++)
		{
			if(sortedCaseMap.containsKey(CellList.get(i).value))
			{
				sortedCaseMap.get(CellList.get(i).value).add(CellList.get(i));
			}
			else
			{
				sortedCaseMap.put(CellList.get(i).value, new ArrayList<Cell>());
				sortedCaseMap.get(CellList.get(i).value).add(CellList.get(i));
			}
		}
		
		List<Cell> greedyList = new ArrayList<Cell>();
		List<Cell> nonFlatList = new ArrayList<Cell>();
		
		for(Map.Entry<Integer, List<Cell>> entry : sortedCaseMap.entrySet())
		{
//			for(int i = 0; i < GreedyMesher.class.getMethods().length; i++)
//			{
//				if(GreedyMesher.class.getMethods()[i].getName().equals("case" + entry.getKey()))
//				{
//					try 
//					{
//						GreedyMesher.class.getMethods()[i].invoke(null, entry.getValue());
//					} 
//					catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) 
//					{
//						e.printStackTrace();
//					}
//				}
//			}
			switch(entry.getKey())
			{
				//wall north
				case 15:
					greedyList.addAll(case15(entry.getValue()));
					break;
				case 51:
					greedyList.addAll(case51(entry.getValue()));
					break;
				case 102:
					greedyList.addAll(case102(entry.getValue()));
					break;	
				case 153:
					greedyList.addAll(case153(entry.getValue()));
					break;
				case 204:
					greedyList.addAll(case204(entry.getValue()));
					break;	
				default:
					nonFlatList.addAll(entry.getValue());
					break;
			}
		}
//		List<List<Cell>> newCells = new ArrayList<List<Cell>>();
//		newCells.add(nonFlatList);
//		newCells.add(greedyList);
		
		List<Cell> greedCells = new ArrayList<Cell>();
		greedCells.addAll(nonFlatList);
		greedCells.addAll(greedyList);
		
		return greedCells;
	}
	//old 15
	private static List<Cell> case51(List<Cell> CaseList)
	{
		Comparator<Cell> comparator =  (a, b) -> Float.compare(a.pz, b.pz);
		Comparator<Cell> comparator2 =  (a, b) -> Float.compare(a.px, b.px);
		Comparator<Cell> comparator3 =  (a, b) -> Float.compare(a.py, b.py);
		Collections.sort( CaseList,  comparator.thenComparing(comparator2).thenComparing(comparator3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).pz == CaseList.get(j).pz)
					{
						if(CaseList.get(i).px == CaseList.get(j).px)
						{
							if(CaseList.get(i).py + CaseList.get(i).sy == CaseList.get(j).py)
							{
								CaseList.get(i).sy += CaseList.get(j).sy;
								CaseList.remove(j);
								j = i;
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		Comparator<Cell> comparatorb =  (a, b) -> Float.compare(a.pz, b.pz);
		Comparator<Cell> comparatorb2 =  (a, b) -> Float.compare(a.py, b.py);
		Comparator<Cell> comparatorb3 =  (a, b) -> Float.compare(a.px, b.px);
		Collections.sort( CaseList,  comparatorb.thenComparing(comparatorb2).thenComparing(comparatorb3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).pz == CaseList.get(j).pz)
					{
						if(CaseList.get(i).py == CaseList.get(j).py)
						{
							if(CaseList.get(i).sy == CaseList.get(j).sy)
							{
								if(CaseList.get(i).px + CaseList.get(i).sx == CaseList.get(j).px)
								{
									CaseList.get(i).sx += CaseList.get(j).sx;
									CaseList.remove(j);
									j = i;
								}
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return CaseList;
	}
	//old 51
	private static List<Cell> case153(List<Cell> CaseList) 
	{
		Comparator<Cell> comparator =  (a, b) -> Float.compare(a.px, b.px);
		Comparator<Cell> comparator2 =  (a, b) -> Float.compare(a.pz, b.pz);
		Comparator<Cell> comparator3 =  (a, b) -> Float.compare(a.py, b.py);
		Collections.sort( CaseList,  comparator.thenComparing(comparator2).thenComparing(comparator3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).px == CaseList.get(j).px)
					{
						if(CaseList.get(i).pz == CaseList.get(j).pz)
						{
							if(CaseList.get(i).py + CaseList.get(i).sy == CaseList.get(j).py)
							{
								CaseList.get(i).sy += CaseList.get(j).sy;
								CaseList.remove(j);
								j = i;
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		Comparator<Cell> comparatorb =  (a, b) -> Float.compare(a.px, b.px);
		Comparator<Cell> comparatorb2 =  (a, b) -> Float.compare(a.py, b.py);
		Comparator<Cell> comparatorb3 =  (a, b) -> Float.compare(a.pz, b.pz);
		Collections.sort( CaseList,  comparatorb.thenComparing(comparatorb2).thenComparing(comparatorb3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).px == CaseList.get(j).px)
					{
						if(CaseList.get(i).py == CaseList.get(j).py)
						{
							if(CaseList.get(i).sy == CaseList.get(j).sy)
							{
								if(CaseList.get(i).pz + CaseList.get(i).sz == CaseList.get(j).pz)
								{
									CaseList.get(i).sz += CaseList.get(j).sz;
									CaseList.remove(j);
									j = i;
								}
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return CaseList;
	}
	//old 85
	public static List<Cell> case15(List<Cell> CaseList)
	{
		Comparator<Cell> comparator =  (a, b) -> Float.compare(a.py, b.py);
		Comparator<Cell> comparator2 =  (a, b) -> Float.compare(b.px, a.px);
		Comparator<Cell> comparator3 =  (a, b) -> Float.compare(a.pz, b.pz);
		Collections.sort( CaseList,  comparator.thenComparing(comparator2).thenComparing(comparator3));
//		Collections.reverse(CaseList);
		
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).py == CaseList.get(j).py)
					{
						if(CaseList.get(i).px == CaseList.get(j).px)
						{
							if(CaseList.get(i).pz + CaseList.get(i).sz == CaseList.get(j).pz)
							{
								CaseList.get(i).sz += CaseList.get(j).sz;
								CaseList.remove(j);
								j = i;
							}
						}
						else
						{
							break;
						}
					}
					
					else
					{
						break;
					}
				}
			}
		}
		Comparator<Cell> comparatorb =  (a, b) -> Float.compare(a.py, b.py);
		Comparator<Cell> comparatorb2 =  (a, b) -> Float.compare(b.pz, a.pz);
		Comparator<Cell> comparatorb3 =  (a, b) -> Float.compare(a.px, b.px);
		Collections.sort( CaseList,  comparatorb.thenComparing(comparatorb2).thenComparing(comparatorb3));
		
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).py == CaseList.get(j).py)
					{
						if(CaseList.get(i).pz == CaseList.get(j).pz)
						{
							if(CaseList.get(i).sz == CaseList.get(j).sz)
							{
								if(CaseList.get(i).px + CaseList.get(i).sx == CaseList.get(j).px)
								{
									CaseList.get(i).sx += CaseList.get(j).sx;
									CaseList.remove(j);
									j = i;
								}
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return CaseList;
	}
	//todo
	private static List<Cell> case102(List<Cell> CaseList) 
	{
		Comparator<Cell> comparator =  (a, b) -> Float.compare(a.px, b.px);
		Comparator<Cell> comparator2 =  (a, b) -> Float.compare(a.py, b.py);
		Comparator<Cell> comparator3 =  (a, b) -> Float.compare(a.pz, b.pz);
		Collections.sort( CaseList,  comparator.thenComparing(comparator2).thenComparing(comparator3));
		
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).px == CaseList.get(j).px)
					{
						if(CaseList.get(i).py == CaseList.get(j).py)
						{
							if(CaseList.get(i).pz + CaseList.get(i).sz == CaseList.get(j).pz)
							{
								CaseList.get(i).sz += CaseList.get(j).sz;
								CaseList.remove(j);
								j = i;
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		Comparator<Cell> comparatorb =  (a, b) -> Float.compare(a.px, b.px);
		Comparator<Cell> comparatorb2 =  (a, b) -> Float.compare(a.pz, b.pz);
		Comparator<Cell> comparatorb3 =  (a, b) -> Float.compare(a.py, b.py);
		Collections.sort( CaseList,  comparatorb.thenComparing(comparatorb2).thenComparing(comparatorb3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).px == CaseList.get(j).px)
					{
						if(CaseList.get(i).pz == CaseList.get(j).pz)
						{
							if(CaseList.get(i).sz == CaseList.get(j).sz)
							{
								if(CaseList.get(i).py + CaseList.get(i).sy == CaseList.get(j).py)
								{
									CaseList.get(i).sy += CaseList.get(j).sy;
									 
									CaseList.remove(j);
									j = i;
								}
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return CaseList;
	}
	
	private static List<Cell> case204(List<Cell> CaseList) 
	{
		Comparator<Cell> comparator =  (a, b) -> Float.compare(a.pz, b.pz);
		Comparator<Cell> comparator2 =  (a, b) -> Float.compare(a.px, b.px);
		Comparator<Cell> comparator3 =  (a, b) -> Float.compare(a.py, b.py);
		Collections.sort( CaseList,  comparator.thenComparing(comparator2).thenComparing(comparator3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).pz == CaseList.get(j).pz)
					{
						if(CaseList.get(i).px == CaseList.get(j).px)
						{
							if(CaseList.get(i).py + CaseList.get(i).sy == CaseList.get(j).py)
							{
								CaseList.get(i).sy += CaseList.get(j).sy;
								 
								CaseList.remove(j);
								j = i;
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		Comparator<Cell> comparatorb =  (a, b) -> Float.compare(a.pz, b.pz);
		Comparator<Cell> comparatorb2 =  (a, b) -> Float.compare(a.py, b.py);
		Comparator<Cell> comparatorb3 =  (a, b) -> Float.compare(a.px, b.px);
		Collections.sort( CaseList,  comparatorb.thenComparing(comparatorb2).thenComparing(comparatorb3));
		for(int i = 0; i < CaseList.size(); i++)
		{
			for(int j = i; j < CaseList.size(); j++)
			{
				if(CaseList.get(i).m == CaseList.get(j).m)
				{
					if(CaseList.get(i).pz == CaseList.get(j).pz)
					{
						if(CaseList.get(i).py == CaseList.get(j).py)
						{
							if(CaseList.get(i).sy == CaseList.get(j).sy)
							{
								if(CaseList.get(i).px + CaseList.get(i).sx == CaseList.get(j).px)
								{
									CaseList.get(i).sx += CaseList.get(j).sx;
									 
									CaseList.remove(j);
									j = i;
								}
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return CaseList;
	}
	
}
