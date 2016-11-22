package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.debug.EEventType;

/**
 * An instance of this class represents a condition node in a behavior. It has
 * variable bindings, an associated expression and at least one outgoing edge.
 */
public class SB_Condition extends SB_EdgeSink
{
  // condition expression
  protected String _rawCondition;

  public SB_Condition(SB_BehaviorElement owner)
  {
    super(owner);
  }

  public ESinkType GetType()
  {
    return ESinkType.kCondition;
  }

  /**
   * Loads a single condition from the given Condition model.
   * 
   * @param reader
   *          the stream reader from which to read it
   */
  public void load(Condition condition, SB_SingletonBook book, List<Integer> transitionIds)
      throws SB_FileException
  {
    _id = condition.getId();

    if (SIM_Constants.DEBUG_INFO_ON)
      book.getLogger().log(".Loading condition " + _id, SB_Logger.INIT);

    super.load(condition, book, transitionIds);
    _rawCondition = condition.getExpr();
  }

  public void initialize(ArrayList transitions, SB_SingletonBook book)
  {
    AssignTransitionEdges(transitions);
    _bindings.initialize(book);
  }

  /**
   * Replaces all instances of SB_Method that match the name of newMethod.
   * 
   * @param newMethod
   *          the method with which to replace existing methods
   */
  public void replaceFunction(SB_Function newMethod)
  {
    
     // TODO: syl - what do we do wihtout SB_Parser?  For now, we ignore this
    _bindings.replaceFunction(newMethod);
  }

  public boolean IsValidDestination(SB_Entity ent,
      SB_ExecutionFrame contextFrame, SB_EdgeSink source) throws SB_Exception
  {
    boolean result = true;

    // apply the condition node's bindings, which will be undone if unsatisfied
    _bindings.ApplyForVariables(ent, contextFrame);

    if (SIM_Constants.DEBUG_INFO_ON)
      contextFrame.GetLogger()
          .log(
              "[" + ent.toString() + ",STK " + contextFrame.GetStackDepth()
                  + "] CONDITION: evaluating " + _rawCondition,
              SB_Logger.CONDITION);

    // execute the condition expression in the js engine.
    SB_JavaScriptEngine javaScriptEngine = contextFrame.GetBook().getJavaScriptEngine();
    Object value = null;
    try {
    	value = javaScriptEngine.evaluate(_rawCondition, contextFrame);
    } catch (SB_Exception ex) {
    	SB_EntityData state = ent.GetState();
		state.GetExecStack().HandleException(ex, contextFrame);
    }
    
    boolean boolValue = (value == null) ? false :  Boolean.parseBoolean(value.toString());
    if (!boolValue)
    {
      result = false;
      if (SIM_Constants.DEBUG_INFO_ON)
        contextFrame.GetLogger().log(
            "[" + ent.toString() + ",STK " + contextFrame.GetStackDepth()
                + "] CONDITION: => FALSE", SB_Logger.CONDITION);
    } else
    {
      if (SIM_Constants.DEBUG_INFO_ON)
        contextFrame.GetLogger().log(
            "[" + ent.toString() + ",STK " + contextFrame.GetStackDepth()
                + "] CONDITION: => TRUE", SB_Logger.CONDITION);
    }

    if (SIM_Constants.AI_DEBUGGER)
    {
      DMFieldMap fields = new DMFieldMap();
      fields.ADD_ID_FIELD("entity", ent.GetId()._id);
      fields.ADD_INT_FIELD("frame", contextFrame.GetStackDepth());
      fields.ADD_INT_FIELD("conditionId", getId());
      SB_Param param = new SB_Param(boolValue);
      fields.ADD_PARAM_FIELD("conditionValue", param);
      //fields.ADD_PARAM_FIELD("conditionValue", SB_SimInterface
      //    .ConvertVariable(value));
      contextFrame.GetDebugger().RecordEvent(EEventType.kEVT_CONDITION_CHECKED,
          fields); 
    }

    return result;
  }

}