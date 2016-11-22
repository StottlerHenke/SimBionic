package com.stottlerhenke.simbionic.common.debug;

import java.util.HashMap;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.api.SB_Param;

/**
 * Contains a hashmap of field-value pairs and an array list for a sorted index of the elements in the hashmap </p>
 */

public class DMFieldMap
{
/*  static public final Integer INT_FIELD = new Integer(SB_DebugMessage.INT_TYPE);
  static public final Integer LONG_FIELD = new Integer(SB_DebugMessage.LONG_TYPE);
  static public final Integer STR_FIELD = new Integer(SB_DebugMessage.STRING_TYPE);
  static public final Integer SA_FIELD = new Integer(SB_DebugMessage.STRING_ARRAY_TYPE);
  static public final Integer PARAM_FIELD = new Integer(SB_DebugMessage.PARAM_TYPE);
  static public final Integer PA_FIELD = new Integer(SB_DebugMessage.PARAM_ARRAY_TYPE);
*/
  public DMFieldMap()
  {
    _fields = new HashMap();
    _fieldNames = new ArrayList();
  }

  public void ADD_PARAM_FIELD(String fieldName, SB_Param value) throws SB_Exception
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(PARAM_FIELD);
    _fields.put(fieldName, new SB_Param(value) );
  }

  public void ADD_INT_FIELD(String fieldName, int value)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(INT_FIELD);
    _fields.put(fieldName, new Integer(value));
  }

  public void ADD_INT_FIELD(String fieldName, boolean value)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(INT_FIELD);

    if( value )
      _fields.put(fieldName, new Integer(1));
    else
      _fields.put(fieldName, new Integer(0));
  }

  public void ADD_INT_FIELD(String fieldName, long value)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(INT_FIELD);
    _fields.put(fieldName, new Integer((int)value));
  }

  public void ADD_LONG_FIELD(String fieldName, long value)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(LONG_FIELD);
    _fields.put(fieldName, new Long(value));
  }

  public void ADD_ID_FIELD(String fieldName, long value)
  {
    ADD_LONG_FIELD(fieldName, value);
  }

  public void ADD_STR_FIELD(String fieldName, String value)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(STR_FIELD);
    _fields.put(fieldName, new String(value));
  }

  public void ADD_SA_FIELD(String fieldName, ArrayList strings)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(SA_FIELD);
    _fields.put(fieldName, strings ); //@todo - do we want to do a deep copy here?
  }

  public void ADD_PA_FIELD(String fieldName, ArrayList strings)
  {
    _fieldNames.add(fieldName);
    //_fieldTypes.add(PA_FIELD);
    _fields.put(fieldName, strings ); //@todo - do we want to do a deep copy here?
  }

  public HashMap _fields;     //Map of field-value pairs
  public ArrayList _fieldNames;  //Sorted index of fields
  //public ArrayList _fieldTypes; //Sorted index of field types
}