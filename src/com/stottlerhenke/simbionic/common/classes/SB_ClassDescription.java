package com.stottlerhenke.simbionic.common.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * This class contains the method and member descriptions for a single user defined class.
 * These descriptions are in terms of Java classes. 
 * 
 * However, when reading the classes the references are in terms of SBType integers.
 * This is because we need to know all of the types (have read in all of the classes)
 * before we can convert everything to class objects. 
 * 
 * Another step handled by this class is the matching of SB method calls to
 * Java method descriptions in order to determine what method to call.
 * 
 */
public class SB_ClassDescription
{
  ArrayList _methods = new ArrayList(); //SB_ClassMethod objects
  HashMap _members = new HashMap(); //Key: String name, Value: Integer or Class
  
  public SB_ClassDescription()
  {
  }
  
  /**
   * 
   * @return the same list of SB_ClassMethod objects as contained in this class description
   */
  public ArrayList getMethods()
  {
    return _methods;
  }
  
  /**
   * The key is the name of the member (String) and the value
   * is the type of the members (Class)
   * 
   * @return the same hashmap of class members as used in this description
   */
  public HashMap getMembers()
  {
    return _members;
  }
  /**
   * Add a class member
   * @param name name of the member (as defined in the java class)
   * @param sbType SB type representation of the value
   */
  public void addMemberDescription(String name, int sbType)
  { 
    _members.put(name, new Integer(sbType));
  }
  
  /**
   * Add a class member with a corresponding Class. If this function is used
   * then addMemberDescription(name, sbType) and convertClassDescription functions
   * should not be used
   * 
   * @param name
   * @param c
   */
  public void addMemberDescription(String name, Class c)
  {
    _members.put(name, c);
  }
  
  /**
   * Add a class method
   * @param name of the method (as defined in the java class)
   * @param sbReturnValue SB type represntation of the java return value
   * @param sbTypes array of SB types of the java expected argument types
   */
  public void addMethodDescription(String name, int sbReturnValue, ArrayList sbTypes)
  { 
    _methods.add(new SB_ClassMethod(name, new Integer(sbReturnValue), sbTypes));
  }

  
  /**
   * Add a class method.
   * 
   * @param name of the method (as defined in the java class)
   * @param return class object
   * @param sbTypes array of class objects of the expected argument types
   */
  public SB_ClassMethod addMethodDescription(String name, Class returnValue, ArrayList sbTypes)
  { 
	  SB_ClassMethod methodDesc = new SB_ClassMethod(name, returnValue, sbTypes);
    _methods.add(methodDesc);
    return methodDesc;
  }
  
  /**
   * 
   * Add a class method.
   * 
   * @param name of the method (as defined in the java class)
   * @param return class object
   * @param sbTypes array of class objects of the expected argument types
   * @param argNames human readable names of the type objects
   */
  public void addMethodDescription(String name, Class returnValue, ArrayList sbTypes, ArrayList argNames)
  { 
	  SB_ClassMethod methodDesc = new SB_ClassMethod(name, returnValue, sbTypes, argNames);
	  methodDesc.overloadIndex = getOverloadIndex(methodDesc);
    _methods.add(methodDesc);
  }
  
	private int getOverloadIndex(SB_ClassMethod method)
	{
		int count = 0;
		for (Iterator it = _methods.iterator(); it.hasNext();){
			if (((SB_ClassMethod)it.next()).getName().equals(method.getName()))
				count ++;
		}
		return count;
	}
  
  /**
   * Convert the class description from Integer references to Class references. This 
   * should only be done once per class AFTER all of the classes have been read in.
   * @param book
   * @throws SB_Exception
   */
  public void convertClassDescription(SB_SingletonBook book) throws SB_Exception
  {
    //Convert the members
    for (Iterator i = _members.keySet().iterator(); i.hasNext();) 
    {
      String key = (String) i.next();
      Class c = SB_ClassDescription.convertSBTypeToClass(book.getUserClassMap(), ((Integer)_members.get(key)).intValue());
      _members.put(key, c);
    }

    //Convert the methods
    int count = _methods.size();
    for(int x = 0; x < count; x++)
    {
      ((SB_ClassMethod) _methods.get(x)).convertToClassRepresentation(book.getUserClassMap());     
    }
  }
  
  /**
   * 
   * @param className the class portion of the java class name (e.g., Vector for java.util.Vector)
   * @param params Array of Objects passed as parameters
   * @return the first constructor that matches the given parameters, exception thrown if none found
   * 
   */
  public Constructor getMatchingConstructor(String className, Class c, ArrayList params)
  	throws SB_Exception
  {
    //Get list of possible matches
    ArrayList classMethods = new ArrayList(); //SB_ClassMethod objects
    for( int x = 0; x < _methods.size(); x++)
    {
      if(((SB_ClassMethod)_methods.get(x))._name.compareTo(className) == 0 )
      {
        classMethods.add(_methods.get(x));
      }
    }
    
    //Now that we have a list of possibilities, find the best match
	SB_ClassMethod bestMethod = getBestMatchingMethod("constructor", className, className, params, classMethods);
	
	try
	{
	    return c.getConstructor(bestMethod.getClassArgArray()); 
	}
	catch(NoSuchMethodException e)
	{
	    throw new SB_Exception("Java class contained no constructor that matches the following signature: " + SB_ClassUtil.getMethodString(className, className, params), e);
	}
  }
  
  /**
   *  kArray Arguments can be converted to any list type
   * @param sbMethod
   * @param params Array of Objects passed as parameters
   * @return
   */
  static public ArrayList ConvertArgsToCorrectTypes(SB_ClassMethod sbMethod, ArrayList params) 
  	throws SB_Exception
  {
    ArrayList convertedArgs = new ArrayList();
    
    int count = params.size();
    for(int x = 0; x < count; x++)
    {
      Class argClass = (Class) sbMethod._params.get(x);
      
      if(params.get(x) instanceof List && !(argClass.isAssignableFrom(params.get(x).getClass())) )
//      if(params.get(x) instanceof List && !(params.get(x).getClass().equals(argClass) ))
      { 
          try
          {
		        List newList = (List) argClass.newInstance();
		        newList.addAll((Collection) params.get(x));
		        convertedArgs.add(newList);
          }
          catch(InstantiationException ex)
          {
              throw new SB_Exception("Null constructor missing for list type: " + argClass.toString(), ex);
          }
          catch(IllegalAccessException ex)
          {
              throw new SB_Exception("Null constructor for list is not accessible (e.g. private): " + argClass.toString(), ex);
          }
      }
      else
        convertedArgs.add(params.get(x));
    }
    
    return convertedArgs;
  }
  
  public Object invokeMatchingMethod(SB_SingletonBook book, Object o, Class c, String methodName, ArrayList params)
  	throws SB_Exception
  {
      if (SIM_Constants.DEBUG_INFO_ON)
			book.getLogger().log("Attempting to call the following method: " + SB_ClassUtil.getMethodString(c.getName(), methodName, params));
			        
      //Get the method
      SB_ClassMethod sbMethod = null;
      Method m = null;
      try
      {
		  sbMethod = getMatchingMethod( c.getName(), methodName, params);
		  Class[] argTypes = sbMethod.getClassArgArray();
		  m = c.getMethod(methodName, argTypes);

      }
      catch(NoSuchMethodException ex)
      {
          throw new SB_Exception("Java class contained no method that matches the following signature: " + SB_ClassUtil.getMethodString(c.getName(), methodName, params), ex);
      }
      
      //Apply the method
      try
      {
          ArrayList convertedParams = ConvertArgsToCorrectTypes(sbMethod, params);
          return m.invoke(o, convertedParams.toArray());
      }
      catch(IllegalAccessException ex)
      {
          throw new SB_Exception("Access to the following methods was denied by Java security: "+ m.toString() , ex);
      }
      catch(IllegalArgumentException ex)
      {
          throw new SB_Exception("The Java method " + m.toString() + " has been passed an illegal or inappropriate argument by this call: " + 
                  SB_ClassUtil.getMethodString(c.getName(), methodName, params), ex);
      }
      catch(InvocationTargetException ex)
      {
          if( ex.getTargetException() != null)
              throw new SB_Exception("An " + ex.getTargetException().getClass().getName() + " was thrown when attempting to call " + m.getName(), ex.getTargetException());
          else
              throw new SB_Exception("An unspecified exception was when attempting to call " + m.getName(), ex);
      }
//      catch(Exception ex)
//      {
//          throw new SB_Exception("An " + ex.getClass().getName() + " was thrown when attempting to call " + m.getName(), ex);
//      }
  }
  
  /**
   * @param methodName name of method
   * @param params Array of Objects passed as parameters or Class objects that represent parameters
   * @return the first method that matches the given parameters, exception thrown if none found
   * 
   */
  public SB_ClassMethod getMatchingMethod(String className, String methodName, ArrayList params)
  	throws SB_Exception
  {
    //Get list of possible matches
    ArrayList classMethods = new ArrayList(); //SB_ClassMethod objects
    for( int x = 0; x < _methods.size(); x++)
    {
      if(((SB_ClassMethod)_methods.get(x))._name.compareTo(methodName) == 0 )
      {
        classMethods.add(_methods.get(x));
      }
    }
    
    //Now that we have a list of possibilities, find the best match
    return getBestMatchingMethod("method", className, methodName, params, classMethods);
  }

  /**
   * 
   * @param methodType Used in creating debug statements, should be "method" or "constructor"
   * @param methodName name of the method/constructor
   * @param args list of Object or Class objects representing the given arguments
   * @param classMethods list of potential matching SB_ClassMethods
   * @return the first matching method found.
   * @throws SB_Exception
   */
  protected SB_ClassMethod getBestMatchingMethod(String methodType, String className, String methodName, ArrayList args, ArrayList classMethods)
  	throws SB_Exception
  {    
	  double maxScore = 0;
	  SB_ClassMethod bestMatch = null;
    for(int x = 0; x < classMethods.size(); x++)
    {
      double methodScore = ((SB_ClassMethod) classMethods.get(x)).scoreMethod(args);
      if (methodScore > 0 && methodScore > maxScore)
      {
    	  maxScore = methodScore;
    	  bestMatch = (SB_ClassMethod) classMethods.get(x);
      }
//      if( methodScore >= 1.0 )
//        return (SB_ClassMethod) classMethods.get(x);
    }
    
    if (bestMatch != null)
    	return bestMatch;
    else
        
        throw new SB_NoSuchMethodException("Could not find a SimBionic " + methodType + " description matching the following name and parameters: " +
        		SB_ClassUtil.getMethodString(className, methodName, args));
//    throw new SB_Exception("Could not find a SimBionic " + methodType + " description matching the following name and parameters: " +
//    		SB_ClassUtil.getMethodString(className, methodName, args));
  }
  
  /**
   * Convert from the SBType to a Class object. Note that some
   * SB Types are not valid (such as data).
   * 
   * This information is the required argument type, so we need to know
   * the difference between int and Integer.
   * 
   * @param sbType An integer representing a built in type or class or user defined class
   * @return a Class object, null in the case of void
   */
  protected static Class convertSBTypeToClass(SB_ClassMap map, int sbType) throws SB_Exception
  {
     Class c = null;
     String className = map.getJavaClassName(sbType);
     if (className == null)
         throw new SB_Exception("Unable to convert from type " + sbType + " to a valid class!");
     try {
     c = Class.forName(className);
     } catch(ClassNotFoundException ex)
     {
        throw new SB_Exception("Can't find class for internal type: " + sbType + " " + ex.toString());
      } 
     
     return c;
    
    
  /*  try
    { 
      if(sbType == SB_VarType.kVoid.getState())
        c = null;
	    else if(sbType == SB_VarType.kFloat.getState())
	    	c = Float.TYPE;
	    else if(sbType == SB_VarType.kInteger.getState())
	      c = Integer.TYPE;
	    else if(sbType == SB_VarType.kBoolean.getState())
	      c = Boolean.TYPE;
	    else if(sbType == SB_VarType.kString.getState())
	      c = String.class;
	    else if(sbType == SB_VarType.kArray.getState() || sbType == SB_VarType.kTable.getState())
	    	c = List.class;
	    else if(sbType == SB_VarType.kEntity.getState())
	      c = Long.TYPE;
	    else if(sbType == SB_VarType.kVector.getState())
	    	c = SB_Vector.class; 
	    else if(sbType == SB_VarType.kShort.getState())
	    	c = Short.TYPE;
	    else if(sbType == SB_VarType.kByte.getState())
	    	c = Byte.TYPE;
	    else if(sbType == SB_VarType.kChar.getState())
	    	c = Character.TYPE;
	    else if(sbType == SB_VarType.kDouble.getState())
	    	c = Double.TYPE; 
	    else if(sbType >= SB_VarType.kCLASS_START.getState())
	    {
	      String className = map.getJavaClassName(sbType);
          if (className == null)
              throw new SB_Exception("Unable to convert from type " + sbType + " to a valid class!");
	      c = Class.forName(className);
	    }
	    else
	      throw new ClassNotFoundException();
    }
    catch(ClassNotFoundException ex)
    {
      throw new SB_Exception("Can't find class for internal type: " + sbType + " " + ex.toString());
    } 

    return c; */
  }
  
}
