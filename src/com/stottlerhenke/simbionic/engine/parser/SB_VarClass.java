package com.stottlerhenke.simbionic.engine.parser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.EErrCode;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;


/**
 * User defined class variables that correspond to instances of actual Java classes
 */
public class SB_VarClass extends SB_Variable
{
   
	/**
	 * Creates a new SB_VarClass 
	 * The class instance is not actually created until load is called.
	 * 
	 */
	public SB_VarClass()
	{
		super();
	}

	public SB_VarClass(SB_VarClass other) throws SB_Exception
	{
		_value = other.getValue();
		_type = other.getType();
	}
	
	
	public SB_Variable Clone() throws SB_Exception
	{
    return new SB_VarClass(this);
	}


	
	public SB_Variable equals( SB_Variable arg2) throws SB_Exception  
	{
	   SB_Variable result = null;

	   Object obj1 = _value;
	   Object obj2 = arg2.getValue();
	   Boolean isEquals = obj1.equals(obj2);

	   result = new SB_VarClass();
	   result.setValue(isEquals);

	   return result;

  }
	

	
	/**
	 * Create an class instance
	 * 
	 * @param book
	 * @param name
	 * @param parameters
	 * @return
	 * @throws SB_Exception
	 */
	static public SB_VarClass createInstance(SB_SingletonBook book, String name, ArrayList params) throws SB_Exception
	{
	  if(params == null)
	    throw new SB_Exception("Unable to create class instance of : " + name + "due to: null parameter value",
          EErrCode.kE_ERROR);		
	    
		String className = new String();
		SB_VarClass returnValue = new SB_VarClass();
		try
		{
			className = (String) book.getUserClassMap().getJavaClassName(name);
			
			if( className == null )
				throw new SB_Exception("Class name not associated with any class package : " + name,
	          EErrCode.kE_ERROR);	
			
			Class c = Class.forName(className);
			
			if( SIM_Constants.DEBUG_INFO_ON && c == null)
			  book.getLogger().log("Couldn't find class for: " + className, SB_Logger.ERROR);
			
			Object o = null;
			
      Constructor[] theConstructors = c.getConstructors();
      
			if( SIM_Constants.DEBUG_INFO_ON && theConstructors == null)
			  book.getLogger().log("Couldn't find constructors for: " + className, SB_Logger.ERROR);
			
      for (int i = 0; i < theConstructors.length; i++) 
      {
  			if( SIM_Constants.DEBUG_INFO_ON && theConstructors[i] == null)
  			  book.getLogger().log("Null constructor at index: " + i, SB_Logger.ERROR);

        Class[] parameterTypes = theConstructors[i].getParameterTypes();
      
  			if( SIM_Constants.DEBUG_INFO_ON && parameterTypes == null)
  			  book.getLogger().log("Null parameters for constructor at index: " + i, SB_Logger.ERROR);

        if(CompareParameters(params, parameterTypes))
        {
          o = theConstructors[i].newInstance(params.toArray());
          break;
        }
      }
			returnValue.setValue(o);
		}
		catch(Exception ex)
		{
			throw new SB_Exception("Unable to create class instance of : " + className + "due to: " + ex.toString(),
          EErrCode.kE_ERROR);		
		}
		
		return returnValue;
	}
	
  /**
   * 
   * @param params array of objects
   * @param pvec array of class objects
   * @return true if the two parameters arrays are of the same type
   */
  static public boolean CompareParameters(ArrayList params, Class[] pvec)
  { 
  	boolean bReturn = true;
  	
  	int count = params.size();
  	if( pvec.length == count ) //at least they are the same size
  	{
  		for( int x = 0; x < count; x++)
  		{
  			Class classOne = pvec[x]; //What the function expects
  			
  			if( classOne.isPrimitive())
  			{
  				Object obj = params.get(x); //What the user has given
  				
  				if( obj instanceof Boolean && classOne.equals(Boolean.TYPE))
  					bReturn = true;
  				else
  				if( obj instanceof Character && classOne.equals(Character.TYPE))
  					bReturn = true;
  				else
    				if( obj instanceof Byte && classOne.equals(Byte.TYPE))
  					bReturn = true;
    			else
      		if( obj instanceof Short && classOne.equals(Short.TYPE))
  					bReturn = true;
      		else
        	if( obj instanceof Integer && classOne.equals(Integer.TYPE))
  					bReturn = true;
        	else
          if( obj instanceof Long && classOne.equals(Long.TYPE))
  					bReturn = true;
  				else
    			if( obj instanceof Float && classOne.equals(Float.TYPE))
  					bReturn = true;
  				else
  				if( obj instanceof Double && classOne.equals(Double.TYPE))
  					bReturn = true;
  				else
  				{
  					bReturn = false;
  					break;
  				}
  			}
  			else
  			if( !classOne.isInstance(params.get(x)) ) //According to doc, doesn't work with primitives
  			{
  					bReturn = false;
  					break;
  			}
  		}
  	}
  	else
  		bReturn = false;
  	
  	return bReturn;
  }
  
}
