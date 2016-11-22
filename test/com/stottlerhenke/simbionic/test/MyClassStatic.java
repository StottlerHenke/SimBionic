package com.stottlerhenke.simbionic.test;

import java.util.ArrayList;
import java.util.Vector;

import com.stottlerhenke.simbionic.common.Table;

/**
 * Custom class for testing SimBionic class functionality
 */
public class MyClassStatic
{
	public MyClassStatic()
	{
	}
	
	static public Object getNull() {return null;}
	
	static public void setMyInt(int newInt)
	{
		_integer = newInt;
	}
	
	
	static public int getMyInt()
	{
		return _integer;
	}
	
	static public void setMyInteger(Integer newInt)
	{
	  _Integer = newInt;
	}
	
	
	static public Integer getMyInteger()
	{
		return _Integer;
	}
	
	static public Integer add( Integer one, Integer two )
	{
	  return new Integer( one.intValue() + two.intValue() );
	}

	static public Vector getOtherArray()
	{ 
	  Vector v = new Vector();
	  v.add( new Integer(4) );
	  v.add( new Float(5.0) );
	  v.add( new String("6") );
	  
	  return v;
	}
	
	static public Vector getArray() {
	   Vector v = new Vector();
	   v.add( new Integer(1) );
	   v.add( new Float(2.0) );
	   v.add( new String("3") );
	   return v;
	}
	
	static public ArrayList getArrayListOne()
	{ 
	   ArrayList arrayList = new ArrayList();
      arrayList.add(new Integer(1));
      arrayList.add( new Float(2.0) );
      arrayList.add( new String("3") );
         
      return arrayList;
	}
	
	
	static public Table getTable()
	{
	  Vector v = new Vector();
	  v.add(getArray());
	  v.add(getArray());
	  v.add(getOtherArray());
	  
	  return new Table(v);
	}
	
	static public String printVector(Vector list)
	{
	  StringBuffer buf = new StringBuffer();
	  for( int x = 0; x < list.size(); x++)
	   buf.append(list.get(x).toString());
	  return buf.toString();	  
	}
	
	static public String printArrayList(ArrayList list)
	{
	  StringBuffer buf = new StringBuffer();
	  for( int x = 0; x < list.size(); x++)
	   buf.append(list.get(x).toString());
	  return buf.toString();
	}
	
	static public ArrayList getArrayListTwo()
	{ 
	  ArrayList v = new ArrayList();
	  v.add( new Integer(9) );
	  v.add( new Float(10.0) );
	  v.add( new String("11") );
	  
	  return v;
	}
	
	static public int _integer;
	static public Integer _Integer;
}
