package com.stottlerhenke.simbionic.common.debug;

import java.util.ArrayList;
import java.util.Vector;
import java.io.*;

import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;


public class SB_DebugMessage
{

/**
 * Message type IDs for all of the Interactive Debugging messages.
 * NOTE: do not change the order of these IDs!
 */

  // editor messages
  public static final int kDBG_GUI_INIT = 0;
  public static final int kDBG_GUI_STOP = 1;
  public static final int kDBG_GUI_PAUSE = 2;
  public static final int kDBG_GUI_START = 3;
  public static final int kDBG_GUI_STEP = 4;
  public static final int kDBG_GUI_STEP_INTO = 5;
  public static final int kDBG_GUI_STEP_ONE_TICK = 6;
  public static final int kDBG_GUI_RUN_TO_FINAL = 7;
  public static final int kDBG_GUI_ADD_BREAK_BEH = 8;
  public static final int kDBG_GUI_ADD_BREAK_FUNC = 9;
  public static final int kDBG_GUI_ADD_BREAK_VAR = 10;
  public static final int kDBG_GUI_ADD_BREAK_ELEM = 11;
  public static final int kDBG_GUI_DISABLE_BREAKPOINT = 12;
  public static final int kDBG_GUI_ENABLE_BREAKPOINT = 13;
  public static final int kDBG_GUI_REMOVE_BREAKPOINT = 14;
  public static final int kDBG_GUI_SELECT_ENTITY = 15;
  public static final int kDBG_GUI_SELECT_FRAME = 16;
  public static final int kDBG_GUI_ADD_TO_WATCH = 17;
  public static final int kDBG_GUI_REMOVE_FROM_WATCH = 18;
  public static final int kDBG_GUI_GET_FRAME = 19;
  public static final int kDBG_GUI_GET_LOCAL_VARS = 20;
  public static final int kDBG_GUI_GET_GLOBAL_VARS = 21;
  public static final int kDBG_GUI_GET_ENTITY = 22;
  public static final int kDBG_GUI_SET_LOCAL = 23;
  public static final int kDBG_GUI_SET_GLOBAL = 24;
  public static final int kDBG_GUI_SHUTDOWN = 25;
  public static final int kDBG_GUI_SHUTDOWN_OK = 26;

  // engine messages
  public static final int kDBG_ENG_ENTITY_CREATED = 27;
  public static final int kDBG_ENG_ENTITY_DESTROYED = 28;
  public static final int kDBG_ENG_BEHAVIOR_CHANGED = 29;
  public static final int kDBG_ENG_ENTITY_STARTING = 30;
  public static final int kDBG_ENG_ENTITY_ENDING = 31;
  public static final int kDBG_ENG_FRAME_CREATED = 32;
  public static final int kDBG_ENG_FRAME_COMPLETED = 33;
  public static final int kDBG_ENG_FRAME_DISCARDED = 34;
  public static final int kDBG_ENG_FRAME_CURRENT = 35;
  public static final int kDBG_ENG_VAR_CHANGED = 36;
  public static final int kDBG_ENG_NODE_CHANGED = 37;
  public static final int kDBG_ENG_GLOBAL_CHANGED = 38;
  public static final int kDBG_ENG_CONDITION_CHECKED = 39;
  public static final int kDBG_ENG_CONDITION_FOLLOWED = 40;
  public static final int kDBG_ENG_BREAKPOINT_HIT = 41;
  public static final int kDBG_ENG_STEP_FINISHED = 42;
  public static final int kDBG_ENG_INIT_OK = 43;
  public static final int kDBG_ENG_INIT_FAILED = 44;
  public static final int kDBG_ENG_FRAME_INFO = 45;
  public static final int kDBG_ENG_LOCAL_VARS_INFO = 46;
  public static final int kDBG_ENG_GLOBAL_VARS_INFO = 47;
  public static final int kDBG_ENG_ENTITY_INFO = 48;
  public static final int kDBG_ENG_SHUTDOWN = 49;
  public static final int kDBG_ENG_SHUTDOWN_OK = 50;
  public static final int kDBG_ENG_MSG = 51;

  public static final int kDBG_TEST_MSG = 52;

  public static final int kDBG_LAST_MESSAGE = 53;

  //sizes
  private static final int INT_LENGTH = 4;
  private static final int LONG_LENGTH = 4;
  private static final int FLOAT_LENGTH = 4;

  /**
 * String names of all message types (for debugging).
 */
  public static final String DBG_MSG_LIST[] =
  {
	"gui_init",
	"gui_stop",
	"gui_pause",
	"gui_start",
	"gui_step",
	"gui_step_into",
	"gui_step_one_tick",
	"gui_run_to_final",
	"gui_add_break_beh",
	"gui_add_break_func",
	"gui_add_break_var",
	"gui_add_break_elem",
	"gui_disable_breakpoint",
	"gui_enable_breakpoint",
	"gui_remove_breakpoint",
	"gui_select_entity",
	"gui_select_frame",
	"gui_add_to_watch",
	"gui_remove_from_watch",
	"gui_get_frame",
	"gui_get_local_vars",
	"gui_get_global_vars",
	"gui_get_entity",
	"gui_set_local",
	"gui_set_global",
	"gui_shutdown",
	"gui_shutdown_ok",

	"eng_entity_created",
	"eng_entity_destroyed",
	"eng_behavior_changed",
	"eng_entity_starting",
	"eng_entity_ending",
	"eng_frame_created",
	"eng_frame_completed",
	"eng_frame_discarded",
	"eng_frame_current",
	"eng_var_changed",
	"eng_node_changed",
	"eng_global_changed",
	"eng_condition_checked",
	"eng_condition_followed",
	"eng_breakpoint_hit",
	"eng_step_finished",
	"eng_init_ok",
	"eng_init_failed",
	"eng_frame_info",
	"eng_local_vars_info",
	"eng_global_vars_info",
	"eng_entity_info",
	"eng_shutdown",
	"eng_shutdown_ok",
	"eng_msg",

        "test_msg",
  };

  public static final int INT_TYPE = 0; private static final int kDM_FIELD_INT = 0;
  public static final int LONG_TYPE = 1; private static final int kDM_FIELD_LONG = 1;
  public static final int ID_TYPE = 1; private static final int kDM_FIELD_ID = 1;
  public static final int PARAM_TYPE = 2; private static final int kDM_FIELD_PARAM = 2;
  public static final int STRING_TYPE = 3; private static final int kDM_FIELD_STRING = 3;
  public static final int STRING_ARRAY_TYPE = 4; private static final int kDM_FIELD_STRING_ARRAY = 4;
  public static final int PARAM_ARRAY_TYPE = 5; private static final int kDM_FIELD_PARAM_ARRAY = 5;

  private static void initializeTypes()
  {
    _initializeTypes = true;

    _messageFieldNames = new ArrayList();
    for( int x = 0; x < kDBG_LAST_MESSAGE; x++ )
    {
      _messageFieldNames.add(new ArrayList());
    }

    // editor messages
    ArrayList list = (ArrayList) _messageFieldNames.get(kDBG_GUI_INIT);
    list.add(new MessageTypeAssoc("simfileName", STRING_TYPE));
    list.add(new MessageTypeAssoc("simfileVersion", INT_TYPE));
    list.add(new MessageTypeAssoc("simFormatVersion", INT_TYPE));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_STEP_ONE_TICK);
    list.add(new MessageTypeAssoc("stepEntity", ID_TYPE));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_RUN_TO_FINAL);
    list.add(new MessageTypeAssoc("stepEntity", ID_TYPE));
    list.add(new MessageTypeAssoc("frame", INT_TYPE));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_ADD_BREAK_BEH);
    list.add(new MessageTypeAssoc("breakpointId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc("behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc("polyIndices", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc("entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc("iterations", kDM_FIELD_INT ));
    list.add(new MessageTypeAssoc("constraint", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_ADD_BREAK_FUNC);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "name", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "type", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "iterations", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "constraint", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_ADD_BREAK_VAR);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "polyIndices", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "variable", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "iterations", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "constraint", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_ADD_BREAK_ELEM);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "polyIndices", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "elemId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "type", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "iterations", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "constraint", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_DISABLE_BREAKPOINT);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_ENABLE_BREAKPOINT);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_REMOVE_BREAKPOINT);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_SELECT_ENTITY);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_SELECT_FRAME);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));	// deprecated
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));	// deprecated

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_ADD_TO_WATCH);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_REMOVE_FROM_WATCH);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_GET_FRAME);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_GET_LOCAL_VARS);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_GET_GLOBAL_VARS);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_GET_ENTITY);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_SET_LOCAL);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "varName", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "varValue", kDM_FIELD_PARAM));

    list = (ArrayList) _messageFieldNames.get(kDBG_GUI_SET_GLOBAL);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "varName", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "varValue", kDM_FIELD_PARAM));

	// engine messages

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_ENTITY_CREATED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "name", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_ENTITY_DESTROYED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_BEHAVIOR_CHANGED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "polyIndices", kDM_FIELD_STRING_ARRAY));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_ENTITY_STARTING);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_ENTITY_ENDING);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_FRAME_CREATED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "parent", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "polyIndices", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "currentNode", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "interrupt", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_FRAME_COMPLETED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_FRAME_DISCARDED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_FRAME_CURRENT);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_VAR_CHANGED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "varName", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "value", kDM_FIELD_PARAM));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_NODE_CHANGED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "nodeId", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_GLOBAL_CHANGED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "varName", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "value", kDM_FIELD_PARAM));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_CONDITION_CHECKED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "conditionId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "conditionValue", kDM_FIELD_PARAM));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_CONDITION_FOLLOWED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "conditionId", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_BREAKPOINT_HIT);
    list.add(new MessageTypeAssoc( "breakpointId", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "iteration", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_STEP_FINISHED);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "alive", kDM_FIELD_LONG));
    list.add(new MessageTypeAssoc( "query", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_INIT_OK);
    list.add(new MessageTypeAssoc( "engineVersion", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_INIT_FAILED);
    list.add(new MessageTypeAssoc( "simfileName", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "simfileVersion", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_FRAME_INFO);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "parent" , kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "polyIndices", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "currentNode", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "interrupt", kDM_FIELD_INT));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_LOCAL_VARS_INFO);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "frame", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "varNames", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "varValues", kDM_FIELD_PARAM_ARRAY));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_GLOBAL_VARS_INFO);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "varNames", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "varValues", kDM_FIELD_PARAM_ARRAY));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_ENTITY_INFO);
    list.add(new MessageTypeAssoc( "entity", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "stackSize", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "behavior", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "polyIndices", kDM_FIELD_STRING_ARRAY));
    list.add(new MessageTypeAssoc( "alive", kDM_FIELD_LONG));
    list.add(new MessageTypeAssoc( "updates", kDM_FIELD_LONG));

    list = (ArrayList) _messageFieldNames.get(kDBG_ENG_MSG);
    list.add(new MessageTypeAssoc( "message", kDM_FIELD_STRING));

    list = (ArrayList) _messageFieldNames.get(kDBG_TEST_MSG);
    list.add(new MessageTypeAssoc( "one", kDM_FIELD_INT));
    list.add(new MessageTypeAssoc( "two", kDM_FIELD_STRING));
    list.add(new MessageTypeAssoc( "three", kDM_FIELD_ID));
    list.add(new MessageTypeAssoc( "four", kDM_FIELD_PARAM));
    list.add(new MessageTypeAssoc( "five", kDM_FIELD_PARAM_ARRAY));
    list.add(new MessageTypeAssoc( "six", kDM_FIELD_STRING_ARRAY));
  }

  public SB_DebugMessage(int msgType)
  {
    _fieldMap = new DMFieldMap();
    _msgType = msgType;
  }

  public SB_DebugMessage(int msgType, DMFieldMap fieldMap)
  {
    _fieldMap = fieldMap;
    _msgType = msgType;
  }

  public int GetMsgType()
  {
    return _msgType;
  }

  public String GetTypeName()
  {
    return DBG_MSG_LIST[_msgType];
  }

  public Object GetField(String fieldName)
  {
    return _fieldMap._fields.get(fieldName);
  }

  /**
   * @return the named field in the message
   */
  public int GetIntField(String fieldName)
  {
    Object value = _fieldMap._fields.get(fieldName);
    return ((Integer) value).intValue();
  }

  /**
   * @return the named field in the message
   */
  public String GetStringField(String fieldName)
  {
    Object value = _fieldMap._fields.get(fieldName);
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
    Object value = _fieldMap._fields.get(fieldName);
    return ((Long) value).longValue();
  }

  /**
   * @return the named field in the message
   */
  public SB_Param GetParamField(String fieldName)
  {
    Object value = _fieldMap._fields.get(fieldName);
    return (SB_Param) value;
  }

  /**
   * @return the named field in the message
   */
  public ArrayList GetStringArrayField(String fieldName)
  {
    Object value = _fieldMap._fields.get(fieldName);
    return (ArrayList) value;
  }

  /**
   * @return the named field in the message
   */
  public ArrayList GetParamArrayField(String fieldName)
  {
    Object value = _fieldMap._fields.get(fieldName);
    return (ArrayList) value;
  }


  public void AddField(String fieldName, Object value)
  {
    _fieldMap._fieldNames.add(fieldName);
    _fieldMap._fields.put(fieldName, value);
  }

  /**
   *
   * @param fieldName
   * @return An integer representation of the type, -1 if not found. To be used by the JNI debugger.
   */
   /*
  public int GetFieldType(String fieldName)
  {

    Object value = _fieldMap._fields.get(fieldName);

    if( value != null )
    {
      if( value.getClass().equals(Long.class) )
        return LONG_TYPE;
      else
      if( value.getClass().equals(Integer.class) )
        return INT_TYPE;
      else
      if( value.getClass().equals(String.class) )
        return STRING_TYPE;
      else
      if( value.getClass().equals(SB_Param.class) )
        return PARAM_TYPE;
      else
      if( value.getClass().equals(Vector.class) )
        return PARAM_ARRAY_TYPE;
      else
      if( value.getClass().equals(ArrayList.class) )
        return STRING_ARRAY_TYPE;
      else
        SB_Logger.log("Unkown field type", SB_Logger.ERROR);
    }

    return -1;*/
//  }

  /**
   *
   * @return a list of all the field names
   */
  public ArrayList GetFieldNames()
  {
    return _fieldMap._fieldNames;
  }

  /**
   *
   * @param in stream to serialize from
   */
  public void deserialize(MFCSocketInputStream in, SB_Logger logger)
  {
    if( !_initializeTypes )
      initializeTypes();

    try
    {
      ArrayList list = (ArrayList) _messageFieldNames.get(_msgType);
      int count = list.size();
      for( int x = 0; x < count; x++ )
      {
        MessageTypeAssoc assoc = (MessageTypeAssoc) list.get(x);

        switch(assoc._type)
        {
          case(INT_TYPE ):
            Integer tempInt = in.readMFCInteger();
            AddField( assoc._name, tempInt );
            break;
          case( STRING_TYPE ):
            String tempString = in.readPascalString();
            AddField( assoc._name, tempString );
            break;
          case( LONG_TYPE ):
            long tempLong = in.readMFCLong();
            AddField( assoc._name, new Long(tempLong) );
            break;
          case( PARAM_TYPE ):
            {
              SB_Param tempParam = new SB_Param();
              tempParam.deserialize(in, logger);
              AddField( assoc._name, tempParam );
            }
            break;
          case( PARAM_ARRAY_TYPE ):
            {
              int numberParams = in.readMFCInt();
              Vector params = new Vector();
              for( int q = 0; q < numberParams; q++ )
              {
                SB_Param tempParam = new SB_Param();
                tempParam.deserialize(in, logger);
                params.add(tempParam);
              }
              AddField( assoc._name, params );
            }
            break;
            case( STRING_ARRAY_TYPE ):
            {
              int numberStrings = in.readMFCInt();
              ArrayList strings = new ArrayList();
              for( int q = 0; q < numberStrings; q++ )
              {
                String str = in.readPascalString();
                strings.add(str);
              }
              AddField( assoc._name, strings );
            }
            break;
          default:
            logger.log("SB_DebugMessage.deserialize - don't know how to deserialize for: " + GetTypeName());
        }
      }
    }
    catch(IOException ex)
    {
      logger.log("Deserializing error: " + ex.toString(), SB_Logger.ERROR);
    }
  }

  public byte[] serialize(SB_Logger logger)
  {
    if( !_initializeTypes )
      initializeTypes();

    
    
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    MFCSocketOutputStream out = new MFCSocketOutputStream(byteOut);
    //int _dataLength = 1; //Always have to write out the message type

    try
    {
      ArrayList list = (ArrayList) _messageFieldNames.get(_msgType);
      int count = list.size();

      //Write out the message
      out.writeByte(_msgType);
      for( int x = 0; x < count; x++ )
      {
        MessageTypeAssoc assoc = (MessageTypeAssoc) list.get(x);
        switch(assoc._type)
        {
          case(INT_TYPE):
            out.writeMFCInt(GetIntField(assoc._name));
            break;
          case(LONG_TYPE):
            out.writeMFCLong(GetLongField(assoc._name));
            break;
          case( STRING_TYPE):
            out.writePascalString(GetStringField(assoc._name));
            break;
          case( PARAM_TYPE ):
            GetParamField(assoc._name).serialize(out, logger);
            break;
          case( PARAM_ARRAY_TYPE ):
            {
              ArrayList params = GetParamArrayField(assoc._name);
              int numberParams = params.size();
              out.writeMFCInt(numberParams);
              for( int q = 0; q < numberParams; q++ )
              {
                ((SB_Param) params.get(q)).serialize(out, logger);
              }
            }
            break;
          case( STRING_ARRAY_TYPE ):
            {
              ArrayList strings = GetStringArrayField(assoc._name);
              int numberStrings = strings.size();
              out.writeMFCInt(numberStrings);
              for( int q = 0; q < numberStrings; q++ )
              {
                out.writePascalString( (String) strings.get(q) );
              }
            }
            break;
          default:
            logger.log("SB_DebugMessage.serialize - don't know how to serialize for: " + GetTypeName());
        }
      }
    }
    catch(IOException ex)
    {
      logger.log("Deserializing error: " + ex.toString(), SB_Logger.ERROR);
    }
    catch(SB_Exception ex)
    {
      logger.log("Deserializing error: " + ex.toString(), SB_Logger.ERROR);
    }

    return byteOut.toByteArray();
  }

  int _msgType;
  DMFieldMap _fieldMap;

  static boolean _initializeTypes = false;
  static ArrayList _messageFieldNames;
}


