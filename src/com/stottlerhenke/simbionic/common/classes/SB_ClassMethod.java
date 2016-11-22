package com.stottlerhenke.simbionic.common.classes;

import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;


/**
 * This class contains a description of a single method and the means to 
 * match actual argument types to defined argument types.
 */
public class SB_ClassMethod
{
  public String _name;
  public Integer _intReturnValue;	//Temporary return value holder
  public Class _returnValue;
  public ArrayList _params;
  public ArrayList _paramNames;
  protected double _score;
  
	/**
	 * For overload methods (methods that have the same method name but different
	 * argument types and/or names), this is the index of this method among the
	 * overload methods. The index is zero-based.
	 */
  public int overloadIndex = -1;
  
  public SB_ClassMethod(String name, Integer returnValue, ArrayList paramClasses)
  {
    _name = name;
    _intReturnValue = returnValue;
    _params = paramClasses;
  }
  
  /**
   * Do not use this constructor with convertToClassRepresentation
   * 
   * @param name of method
   * @param returnValue of method, a class object
   * @param classes class objects
   */
  public SB_ClassMethod(String name, Class returnValue, ArrayList classes)
  {
    _name = name;
    _returnValue = returnValue;
    _params = classes;
  }
  
  public SB_ClassMethod(String name, Class returnValue, ArrayList classes, ArrayList paramNames)
  {
    _name = name;
    _returnValue = returnValue;
    _params = classes;
    _paramNames = paramNames;
  }
  
  
  public String getName() {return _name;}
  /**
   * This should only be called once per method. It converts from SB types to 
   * actual classes AFTER all of the classes have been defined.
   * 
   * @param book
   * @throws SB_Exception
   */
  public void convertToClassRepresentation(SB_ClassMap map) throws SB_Exception
  {
    _returnValue = SB_ClassDescription.convertSBTypeToClass(map, _intReturnValue.intValue() );
    
    int count = _params.size();
    for(int x = 0; x < count; x++)
    {
      Class c = SB_ClassDescription.convertSBTypeToClass(map, ((Integer)_params.get(x)).intValue());
      _params.set(x, c);
    }
  }
  
  /**
   * Determine how closely the given and method arguments match
   * 
   * @param args list of Object or Class objects
   * @return 1 if the method is an exact match, 0 if they don't match at all
   */
  protected double scoreMethod(ArrayList args)
  	throws SB_Exception
  {
    _score = 0;
    
    //If not same size then they don't match
    if( args.size() != _params.size() )
    {
      _score = 0;
      return _score;
    }
    
    int count = args.size();
    //If there aren't any parameters, then they match perfectly
    if( count == 0)
    {
      _score = 1.0;
      return _score;
    }
    
    double tempScore = 0;
    for(int x = 0; x < count; x++)
    {
      double temp = 0.0;
      Object o = args.get(x);
      
      if( o != null && o instanceof Class)
      {
          temp = scoreArgumentsByClass( (Class) o, (Class) _params.get(x));
      }
      else
      {
          temp = scoreArguments( (Object) o, (Class) _params.get(x));
      }
      
      if( temp == 0.0 )
        return 0;
      else
        tempScore += temp;
    }
    
    _score = tempScore / args.size(); //This only works because size >= 1
    return _score;
  }
  
  /**
   * Determine how closely one pair of arguments matches
   * @param a1 argument given by the simbionic
   * @param c2 class expected by method
   * @return 1 if they match exactly, 0 if they don't match at all
   */
  protected double scoreArguments(Object o1, Class c2)
  	throws SB_Exception
  {
	  // null is ok if the target type is a non-primitive class,
	  // but not ok if the target type is a primitive type, e.g., int, float, double
    if( o1 == null)
      return (c2.isPrimitive() ? 0.0 : 1.0);
    
    Class c1 = o1.getClass();
    
    return scoreArgumentsByClass(c1, c2);
  }
   
  /**
   * Determine how closely one pair of arguments matches
   * @param c1 class of the given argument
   * @param c2 class expected by method
   * @return 1 if they match exactly, 0 if they don't match at all
   */
  protected double scoreArgumentsByClass(Class c1, Class c2) throws SB_Exception
  {
	  if (c1.equals(c2))
		  return 1.0;
	  
	  double penalty = 1;
	  if (c1.isPrimitive() && !c2.isPrimitive() || !c1.isPrimitive() && c2.isPrimitive())
		  penalty = 0.5;
	  
	  c1 = getNonPrimitiveClass(c1);
    c2 = getNonPrimitiveClass(c2); //Reflection doesn't care about primitives
    if (c1.equals(c2))
    	return 0.9 * penalty;
    
    if(c2.isAssignableFrom(c1))
      return 0.8 * penalty;
    
    if(Number.class.isAssignableFrom(c1) &&
       Number.class.isAssignableFrom(c2) )
        return 0.7 * penalty;
    
    //Special case for List types (convert between)
    String className = c1.getName();
    if( 		(className.compareTo("java.util.List") == 0 || 
            className.compareTo("java.util.ArrayList") == 0 || 
            className.compareTo("java.util.Vector") == 0) && 
            List.class.isAssignableFrom(c2))
    //if(o1 instanceof List && List.class.isAssignableFrom(c2) )
      return 0.8 * penalty;

    return 0;
  }
  
  /**
   * @param c a class object that represents a primitive java type, may be null
   * @return a class object that represents the boxed version of the given primitive type
   * @throws SB_Exception
   */
  public Class getNonPrimitiveClass(Class c) throws SB_Exception
  {
    if( c == null )
        return c;
    
    if( c.isPrimitive() )
    {
      if( c.equals(Boolean.TYPE) ) return Boolean.class;
      if( c.equals(Character.TYPE) ) return Character.class;
      if( c.equals(Byte.TYPE) ) return Byte.class;
      if( c.equals(Short.TYPE) ) return Short.class;
      if( c.equals(Integer.TYPE) ) return Integer.class;
      if( c.equals(Long.TYPE) ) return Long.class;
      if( c.equals(Float.TYPE) ) return Float.class;
      if( c.equals(Double.TYPE) ) return Double.class;
      
      throw new SB_Exception("Could not convert primitive type to class: " + c.toString());
    }
    else
      return c;
  }
  
  /**
   * @return an array of class objects that corresponds to the defined parameters
   * of this method.
   */
  public Class[] getClassArgArray()
  {
    Class[] cArray = new Class[_params.size()];
    
    for( int x = 0; x < _params.size(); x++)
      cArray[x] = (Class)_params.get(x);
    
    return cArray;
  }
  
  /**
   * 
   * @return the Class object that represents the return type of this function
   */
  public Class getReturnType()
  {
      return _returnValue;
  }
  
  
  /**
   * Gets the human-readable names of the parameters
   * @return an array list of string objects if set, otherwise null
   */
  public ArrayList getParamNames()
  {
      return _paramNames;
  }
}
