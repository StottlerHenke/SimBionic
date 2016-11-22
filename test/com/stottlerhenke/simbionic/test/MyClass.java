package com.stottlerhenke.simbionic.test;
import java.util.HashMap;
import java.util.Vector;

/**
 * Custom class for testing SimBionic class functionality
 */
public class MyClass
{
	public MyClass()
	{
		_integer = 5;
	}
	
	public OtherClass methodA(Object o) { return new OtherClass(o); }
	public Object methodB(Integer i) { return new OtherClass(i); }
	public Integer methodC(int i) 
	{
	  return new Integer(i); 
	}
	
	public int methodD() {return 10;}
	public String methodE(long l) {return "" + l;}
	public short methodF(double d) {return (short) d; }
	public Vector methodG(Vector v) { v.add("4"); return v; }
	
	public void setMyInt(int newInt)
	{
		_integer = newInt;
	}
	
	public void setMyInt( MyClass other )
	{
		_integer = other._integer;
	}
	
	public int getMyInt()
	{
		return _integer;
	}
	
	static public Integer add( Integer one, Integer two )
	{
	  return new Integer( one.intValue() + two.intValue() );
	  //return new Integer( one + two );
	}
	
	public String toString()
	{
	  return new String("" + _integer);
	}
	
	static public boolean failFirstTime(String id)
	{
	    if(_failMap == null)
	        _failMap = new HashMap();
	    
	    if(_failMap.get(id) != null )
	        return true;
	    
	    _failMap.put(id, new Boolean(true));
	    
	    return false; 
	}
	
	public int _integer;
	static public HashMap _failMap;
}

class OtherClass
{
  protected Object _o = null;
  
  public OtherClass(Object o) 
  {
    _o = o;
  }
  
  public Object getObject() {return _o;}
  
  public String toString()
  {
    if( _o == null)
      return "null";
    else
      return _o.toString();
  }
}
