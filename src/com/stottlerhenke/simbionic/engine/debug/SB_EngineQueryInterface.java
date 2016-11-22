package com.stottlerhenke.simbionic.engine.debug;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionStack;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.engine.SB_SimInterface;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

import com.stottlerhenke.simbionic.engine.parser.*;
import com.stottlerhenke.simbionic.engine.manager.*;

/**
 * This class is the interface between the debugger and the engine.
 * It allows the debugger to query the engine for current state
 * information while concealing the structure of the engine itself.
 */

public class SB_EngineQueryInterface
{
  public SB_EngineQueryInterface(SB_EntityManager entityMgr, SB_Logger logger, SB_SingletonBook book)
  {
    _book = book;
    _entityMgr = entityMgr;
    _logger = logger;
  }

  /**
  * @return the entity currently being updated
  */
  public long GetCurrentEntity()
  {
    return _entityMgr.GetCurrentEntity()._id;
  }

  /**
   * @param entityId the entity of interest
   * @return the current stack frame for the specified entity
   */
  public int GetEntityCurrentFrame(long entityId)
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_ExecutionStack stack = entity.GetState().GetExecStack();
      if( stack.getSize() == 0)
      {
        return SB_ExecutionFrame.NULL_FRAME;
      }
      else
      {
        return stack.getCurrentFrame().GetStackDepth();
      }
    }

    _logger.log("!! ERROR: client queried unknown entity " + entityId, SB_Logger.ERROR);

    return SB_ExecutionFrame.NULL_FRAME;
  }

  /**
   * Retrieves basic information about the specified frame.
   * @param entityId the entity whose frame is being queried
   * @param frameId the frame being queried
   * @param isInterrupt [returned] true if the frame is an interrupt frame, false otherwise
   * @param parentId [returned] the ID of the invoking frame, or -1 if the root frame
   */
  public FrameInfo GetFrameInfo(long entityId, int frameId)
  {
    boolean isInterrupt;
    int parentId;

    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_ExecutionFrame frame = entity.GetState().GetExecStack().GetFrameByDepth( frameId );

      if (frame != null)
      {
        isInterrupt = frame.IsInterrupt();
        SB_ExecutionFrame parent = frame.GetParent();
        parentId = (parent == null) ? -1 : parent.GetStackDepth();
        return new FrameInfo(isInterrupt, parentId);	// SUCCESS
      }
    }

    _logger.log("!! ERROR: client queried unknown entity (" + entityId + ") or frame (" + frameId + ")", SB_Logger.ERROR);

    return null;
  }

   /**
   * Retrieves the unique name and polymorphic indices of the behavior
   * in the specified stack frame of the specified entity.
   * @param entityId the entity whose behavior is being retrieved
   * @param frameId the frame whose behavior is being retrieved
   * @param behavior [returned] the unique name of the behavior
   * @param polys [returned] the polymorphic indices of the behavior (Strings)
   */
  public FrameBehavior GetFrameBehavior(long entityId, int frameId)
  {
    String behavior = new String("INVALID_ENTITY");
    ArrayList polys = new ArrayList();

    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_ExecutionFrame frame = entity.GetState().GetExecStack().GetFrameByDepth( frameId );

      if (frame != null)
      {
        behavior = frame.GetBehavior().GetClass().getName().toString();
        polys = frame.GetBehavior().GetHierarchyIndices();
        return new FrameBehavior(behavior, polys);	// SUCCESS
      }
      behavior = "INVALID_FRAME";
    }

    _logger.log( "!! ERROR: client queried unknown entity (" + entityId + ") or frame (" + frameId + ")", SB_Logger.ERROR);

    return new FrameBehavior(behavior, polys);
  }

  /**
   * Retrieves the behavior-unique ID of the current node in the specified
   * stack frame of the specified entity.
   * @param entityId the entity whose behavior is being retrieved
   * @param frameId the frame whose behavior is being retrieved
   * @return the ID of the current node
   */
  public int GetFrameCurrentNode(long entityId, int frameId)
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_ExecutionFrame frame = entity.GetState().GetExecStack().GetFrameByDepth( frameId );

      if (frame != null)
      {
        return frame.GetCurrNode().getId();
      }
    }

    _logger.log( "!! ERROR: client queried unknown entity (" + entityId + ") or frame (" + frameId + ")", SB_Logger.ERROR);

    return -1;
  }

  /**
   * Retrieves the names and values of the variables in the specified stack
   * frame of the specified entity.
   * @param entityId the entity whose variables are being retrieved
   * @param frameId the frame whose variables are being retrieved
   * @param varNames [returned] the names of the variables in the frame (Strings)
   * @param varValues [returned] an ordered list of the variable values (same order as the names) (AI_Params)
   */
  public FrameVarValues GetFrameVarValues(long entityId, int frameId ) throws SB_Exception
  {
    ArrayList varValues = new ArrayList();
    ArrayList varNames = new ArrayList();

    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_ExecutionFrame frame = entity.GetState().GetExecStack().GetFrameByDepth( frameId );

      if (frame != null)
      {
        varNames = frame.GetVariableNames();

        int nCount = varNames.size();
        for( int x = 0; x < nCount; x++ )
        {
          String varName = varNames.get(x).toString();
          SB_Variable value = frame.GetVariable(varName);
          SB_Param param = new SB_Param(value.getValue());
          varValues.add(param);
        }
        return new FrameVarValues(varNames, varValues);	// SUCCESS
      }
    }

    _logger.log( "!! ERROR: client queried unknown entity (" + entityId + ") or frame (" + frameId + ")", SB_Logger.ERROR);
    return null;
  }

  /**
   * Sets the value of the specified variable in the specified stack frame.
   * @param entityId the entity whose variable is being set
   * @param frameId the frame whose variable is being set
   * @param varName the name of the variable to be set
   * @param varValue the variable's new value
   */
  public void SetFrameVarValue(long entityId, int frameId ,String varName, SB_Param varValue) throws SB_Exception
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_ExecutionFrame frame = entity.GetState().GetExecStack().GetFrameByDepth( frameId );

      if (frame != null)
      {
        SB_Variable value = SB_SimInterface.ConvertParamAuto( varValue, _book );
        frame.SetVariable( varName, value );
      }
    }
  }

  /**
   * Retrieves the values of the specified global variables for the specified
   * entity.
   * @param entityId the entity whose variables are being retrieved
   * @param varNames [returned] the names of all global variables (Strings)
   * @param varValues [returned] an ordered list of the variable values (same order as the names) (AI_Params)
   */
  public FrameVarValues GetGlobalVarValues(long entityId) throws SB_Exception
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    ArrayList varNames = new ArrayList();
    ArrayList varValues = new ArrayList();

    if (entity != null)
    {
      varNames = entity.GetState().GetGlobalNames();

      int nCount = varNames.size();
      for( int x = 0; x < nCount; x++ )
      {
        SB_Variable value = entity.GetState().GetGlobal( varNames.get(x).toString() );
        varValues.add(new SB_Param(value.getValue()) );
      }
    }

    _logger.log("!! ERROR: client queried unknown entity " + entityId, SB_Logger.ERROR);

    return new FrameVarValues(varNames, varValues);
  }

  /**
   * Sets the value of the specified global variable.
   * @param entityId the entity whose variable is being set
   * @param varName the name of the variable to be set
   * @param varValue the variable's new value
   */
  public void SetGlobalVarValue(long entityId, String varName, SB_Param varValue) throws SB_Exception
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      SB_Variable value = SB_SimInterface.ConvertParamAuto( varValue, _book );
      entity.GetState().SetGlobal( varName, value, _logger );
    }
  }

  /**
   * Evaluates the given expression for the specified entity (and frame,
   * if available).
   * @param expression the expression tree to evaluate
   * @param entityId the entity to evaluate the expression for
   * @param frameId the frame to evaluate the expression for, or NULL_FRAME
   * @return the value of the expression
   */
  public SB_Variable EvaluateExpression(String expression, long entityId, int frameId)
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );
    SB_VarClass result = new SB_VarClass();

    if (entity != null)
    {
      SB_ExecutionFrame frame = entity.GetState().GetExecStack().GetFrameByDepth( frameId );

      if (frame != null)
      {
        // make a temporary frame to do the evaluation
        frame = new SB_ExecutionFrame(null,entity,true, _book);
      }

      
      try
      {
         Object evalResult = _book.getJavaScriptEngine().evaluate(expression, frame);
         result.setValue(evalResult);
         return result;
      }
      catch (SB_Exception exc)
      {
         _logger.log("!! ERROR: SB_EngineQueryInterface cannot evaluate expression: " + exc.getMessage(), SB_Logger.ERROR);
      }
     
    }

    result.setValue(false); // if expression can't be evaluated, treat as false
    return result;
    
  }

  /**
   * Retrieves the stack size for the specified entity.
   * @param entityId the entity of interest
   * @return the stack size
   */
  public int GetEntityStackSize(long entityId)
  {
    SB_Entity entity = _entityMgr.GetEntity( new SB_ID(entityId) );

    if (entity != null)
    {
      return entity.GetState().GetExecStack().getSize();
    }

    _logger.log("!! ERROR: client queried unknown entity " + entityId, SB_Logger.ERROR);

    return -1;
  }

  /**
   * Retrieves the total wallclock time that this entity has existed.
   * @param entityId the entity of interest
   * @return the wallclock time alive in secs
   */
  public long GetEntityTimeAlive(long entityId)
  {
    SB_EntityRecord entityRec = _entityMgr.GetRecord( new SB_ID(entityId) );
    if (entityRec != null)
    {
      return entityRec.GetRealTimeAlive();
    }

    _logger.log("!! ERROR: client queried unknown entity " + entityId, SB_Logger.ERROR);

    return 0;
  }

  /**
   * Retrieves the total number of updates so far for this entity.
   * @param entityId the entity of interest
   * @return the number of updates
   */
  public long GetEntityTotalUpdates(long entityId)
  {
    SB_EntityRecord entityRec = _entityMgr.GetRecord( new SB_ID(entityId) );
    if (entityRec != null)
    {
      return entityRec.GetTotalUpdates();
    }

    _logger.log("!! ERROR: client queried unknown entity " + entityId, SB_Logger.ERROR);
    return 0;
  }

  private SB_EntityManager _entityMgr;
  private SB_Logger _logger;
  private SB_SingletonBook _book;
}