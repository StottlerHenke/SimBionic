
package com.stottlerhenke.simbionic.engine;

import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Config;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.api.SB_ParamType;
import com.stottlerhenke.simbionic.common.EIdType;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_VarInvalid;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * The internal interface to the simulation.  ENGINE's interaction with
 * the simulation will occur primarily through this class.
 */
public class SB_SimInterface 
{
  public SB_SimInterface() {
  }


  static public SB_ID TranslateId(long lokiId) { return new SB_ID(lokiId, EIdType.kEntityId); }
  static public long TranslateId(SB_ID sbId){ return sbId._id; }

  static public boolean IsDefinedClass(Class c, SB_SingletonBook book)
  {
    boolean bReturn = book.getUserClassMap().isDefinedJavaClass(c);
    
    return bReturn;
  }
  
  static public Object ConvertObject(SB_Variable var)
  throws SB_Exception
  {
    Object v = var.getValue(); // var.getSB_Param().getData();
    
    if( v instanceof List ) //Then unwrap params
      v = UnwrapArrayObjects( (List) v);
    
    return v;
  }
 
  /**
   * Check to see if the vector contains SB_Param objects. If so, unwrap
   * @param v
   * @return a vector with unwrapped objects
   */
  static public List UnwrapArrayObjects(List v) throws SB_Exception
  {
    if( !SB_Config.USE_SB_PARAM_ARRAYS )
        return v;
    else
    {
	    List unwrapped;
	    
	    //Create the same type of list as was passed in
	    try
	    {
	      unwrapped = (List) v.getClass().newInstance();
	    }
	    catch(Exception ex)
	    {
	      throw new SB_Exception("Converting array objects to SB_Params failed: " + ex.toString());
	    }
	    
	    int count = v.size();
	    for( int x = 0; x < count; x++ )
	    {
	      if(v.get(x) instanceof SB_Param)
	      {
	        Object o = ((SB_Param)v.get(x)).getData();
	        if( o instanceof List )
	          o = UnwrapArrayObjects( (List) o);
	        
	        unwrapped.add(o);
	      }
	      else
	      {
	        return v;
	      }
	    }
	    
	    return unwrapped;
    }
  }
  
  /**
   * Convert SB_Variable objects to objects
   * 
   * @param vars array of SB_Variable objects
   * @return an array of objects
   */
  static public ArrayList ConvertObjects(ArrayList vars)
  throws SB_Exception
  {
  	ArrayList params = new ArrayList();
    params.clear();
    for(int i = 0; i < vars.size(); i++)
    {
      params.add(ConvertObject((SB_Variable)vars.get(i)));
    }
    
    return params;
  }
  
  /**
   * @return an SB_Param based on the value of the object, the default as a SB_VarData
   */
  static public SB_Param ConvertObjectToSB_Param(Object obj) throws SB_Exception
  {
  	SB_Param var = null;
  	
  	if(obj instanceof SB_Param)
  	    return (SB_Param) obj;
  
  	if( obj == null)
  		var = new SB_Param((Object) null);
  	else
  	if( obj instanceof Float)
  		var = new SB_Param( ((Float) obj).floatValue() );
  	
  	// XXX: treat Double like Integer
//  	else
//  	if( obj instanceof Double)
//  		var = new SB_Param( ((Double) obj).intValue() );
  	
  	
  	else
  	if( obj instanceof Integer)
  		var = new SB_Param( ((Integer) obj).intValue() );
  	else
  	if( obj instanceof Boolean)
    	var = new SB_Param( ((Boolean) obj).booleanValue() );	
  	else	
  	if( obj instanceof String)
     	var = new SB_Param( ((String) obj) );
  	else
	if( obj instanceof List)
		var = new SB_Param((List) obj);
	else
	if( obj instanceof Long )
		//var = new SB_Param( TranslateId( ((Long) obj).longValue() ));
		var = new SB_Param( ((Long) obj).longValue() );
	else
	if( obj instanceof List )
	    var = new SB_Param( (List) obj );
	else
		var = new SB_Param(obj);
  	
  	return var;
  }
  
	
  /**
   * Convert an array of java objects to an array of SB_Param objects
   * 
   * @param array of java objects
   * @return an array of SB_Param Objects
   */
  static public List ConvertObjectsToSB_Param(List array)
  throws SB_Exception
  {  
    if(!SB_Config.USE_SB_PARAM_ARRAYS)
    {
        return array;
    }
    else
    {
        List paramVector = SB_Param.createNewList(array);
        
	    for( int x = 0; x < array.size(); x++ )
	    {
	      if(array.get(x) instanceof SB_Param)
	      {
	        return array; //In this case, already converted so just return original array
	      }
	      else
	      {
	        paramVector.add( ConvertObjectToSB_Param(array.get(x)) );
	      }
	    }
	    
	    return paramVector;
    }  
  }
  
  /**
   * Convert the parameter based on the type specified in the parameter.
   * Since params perform casting, specifying the param type controls
   * what type of SB_Var the param will convert to. In the case of Data param types,
   * known classes become SB_VarClass, otherwise SB_VarData
   * @param param
   * @param book
   * @return
   * @throws SB_Exception
   */
  static public SB_Variable ConvertParamAuto(Object paramObject, SB_SingletonBook book) throws SB_Exception
  {
      SB_Param param = null;
      if(! (paramObject instanceof SB_Param) )
          param = ConvertObjectToSB_Param(paramObject);
      else
          param = (SB_Param) paramObject;
      
    SB_ParamType pType = param.getType();
    Object o = param.getData();
    
    if( pType == SB_ParamType.kSB_Data)
    {
       return ConvertParam(param, book); 
    } 
    else if( pType == SB_ParamType.kSB_Invalid) {
      return new SB_VarInvalid();
    }
    
    throw new SB_Exception("Can't automatically convert SB_Param to SB_Variable");

  }
  
  /**
   * Even though each SB_Variable object is a container for an SB_Param, we want to creat a new SB_Param so leave this function as is.
   * @convertTo create an SB_Variable of this type
   * @param param
   * @return
   */
  static public SB_Variable ConvertParam(SB_Param param, SB_SingletonBook book) throws SB_Exception
  {
    //int newType = convertTo.getState();
    SB_Variable var = new SB_VarClass();
    Object data = param.getData();
    var.setValue(data);
    
    return var;
  }

  static public ArrayList ConvertParams(ArrayList params, SB_SingletonBook book)
  throws SB_Exception
  {
    ArrayList vars = new ArrayList();
    for(int i = 0; i < params.size(); i++){
      vars.add(ConvertParamAuto( params.get(i), book));
    }
    return vars;
  }

  /**
   * Even though each variable is a container for an SB_Param, we want to return a copy so leave this function as is.
   *
   * @param var
   * @return
   */
  static public SB_Param ConvertVariable(SB_Variable var) throws SB_Exception
  {
    if(var == null)
      return new SB_Param();
 
     return new SB_Param(var.getValue());
  }

  static public void ConvertVariables(ArrayList vars, ArrayList params) throws SB_Exception
  {
    params.clear();
    for(int i = 0; i < vars.size(); i++){
      params.add(ConvertVariable((SB_Variable)vars.get(i)));
    }
  }
}