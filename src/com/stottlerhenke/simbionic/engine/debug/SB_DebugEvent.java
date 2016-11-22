package com.stottlerhenke.simbionic.engine.debug;

import java.util.*;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;

/**
 * An instance of this class represents a single execution event (e.g., an
 * entity's current node changing, the popping of a stack frame) of interest
 * to the debugger
 */

public class SB_DebugEvent
{
  public SB_DebugEvent(int type, DMFieldMap map)
  {
    _type = type;
    _map = map;
  }

  /**
   * @return a cloned copy of the event's fields
   */
  public DMFieldMap CloneFields(SB_Logger logger) throws SB_Exception
  {
    DMFieldMap newMap = new DMFieldMap();

    int nCount = _map._fieldNames.size();
    for( int x = 0; x < nCount; x++ )
    {
      String fieldName = (String) _map._fieldNames.get(x);
      Object value = _map._fields.get(fieldName);

      String newString = new String(fieldName);
      Object newValue = CloneField( value, logger );
      newMap._fieldNames.add( newString );
      newMap._fields.put( newString, newValue );
    }

    return newMap;
  }

  /**
   *
   * @param value An object of type Double, Integer, String, Long, Boolean, AI_Param, ArrayList or Vector
   * @return A copy of value
   */
  public Object CloneField(Object value, SB_Logger logger) throws SB_Exception
  {
    Object returnValue = null;

    if( value.getClass() == Double.class )
    {
      returnValue = new Double(((Double) value).doubleValue());
    }
    else
    if( value.getClass() == Integer.class )
    {
      returnValue = new Integer(((Integer) value).intValue());
    }
    else
    if( value.getClass() == String.class )
    {
      returnValue = new String( value.toString() );
    }
    else
    if( value.getClass() == Long.class )
    {
      returnValue = new Long(((Long) value).longValue());
    }
    else
    if( value.getClass() == Boolean.class )
    {
      returnValue = new Boolean(((Boolean) value).booleanValue());
    }
    else
    if( value.getClass() == SB_Param.class )
    {
      returnValue = new SB_Param();
      ((SB_Param) returnValue).copy( (SB_Param) value );
    }
    else
    if( value instanceof List )
    {
      //@todo - do we want a shallow copy here or a deep copy?
      returnValue = value;
    }
    else
      logger.log("Failed to clone a field in SB_DebugEvent: Unknown field type");

    return returnValue;

  }

  /**
   * @return the type of the event
   */
  public int GetType() { return _type; }

  /**
   * @return true if the named field exists, false otherwise
   */
  public boolean IsField(String fieldName)
  {
    return _map._fields.containsKey(fieldName);
  }

  /**
   * @return the named field in the message
   */
  public int GetIntField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return ((Integer) value).intValue();
  }

  /**
   * @return the named field in the message
   */
  public String GetStringField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return value.toString();
  }

  /**
   * @return the named field in the message
   */
  public long GetIdField( String fieldName)
  {
    return GetLongField(fieldName);
  }

  /**
   * @return the named field in the message
   */
  public long GetLongField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return ((Long) value).longValue();
  }

  /**
   * @return the named field in the message
   */
  public SB_Param GetParamField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return (SB_Param) value;
  }

  /**
   * @return the named field in the message
   */
  public ArrayList GetStringArrayField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return (ArrayList) value;
  }

  /**
   * @return the named field in the message
   */
  public Vector GetParamArrayField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return (Vector) value;
  }

  /**
   * @return the named field
   * @throws WG_Exception if the field does not exist
   */
  public Object GetField(String fieldName)
  {
    Object value = _map._fields.get(fieldName);
    return value;
  }

  int _type;
  DMFieldMap _map;
}