package com.stottlerhenke.simbionic.engine.core;
import java.io.Serializable;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.debug.EEventType;

public class SB_ExecutionStack implements Serializable  
{
  protected SB_Entity _owner;
  protected ArrayList _stack = new ArrayList(); //SB_ExecutionFrame
  protected SB_ExecutionFrame _currentFrame;
  
  protected ArrayList _execMode = new ArrayList(); //Integer
  protected ArrayList _modeInvoker = new ArrayList(); //SB_ExecutionFrame
  protected ArrayList _interruptFrame = new ArrayList(); //SB_ExecutionFrame
  protected ArrayList _noninterruptibleFrame = new ArrayList(); //SB_ExecutionFrame
  
  protected SB_Exception _currentException = null;
  protected SB_ExecutionFrame _currentExceptionFrame = null;
  protected SB_ExecutionFrame _currentExceptionParent = null;
  protected boolean	_currentExceptionCaught = false;
  protected ArrayList _temporaryExceptionStack = new ArrayList(); //SB_ExecutionFrame
  protected SB_ExecutionFrameState _currentExceptionFrameState = null; //State prior to execption
  private SB_ExecutionFrame _checkedFrame;
	
  public SB_ExecutionStack(SB_Entity owner) {
    _owner = owner;
    _currentFrame = null;
    _checkedFrame = null;
  }
   public void Reset(){
      _stack.clear();
      _currentFrame = null;
      _checkedFrame = null;
      
      _execMode.clear();
      _modeInvoker.clear();
      _interruptFrame.clear();
      _noninterruptibleFrame.clear();
   }

   public SB_ExecutionFrame getCurrentFrame(){
     return _currentFrame;
   }

   public void SetCurrentFrame(SB_ExecutionFrame frame){
     _currentFrame = frame;

      if( SIM_Constants.AI_DEBUGGER )
      {
      	if ((frame != null) && (frame.GetStackDepth() != -1))
	{
          DMFieldMap fields = new DMFieldMap();
          fields.ADD_ID_FIELD("entity",  _owner.GetId()._id );
          fields.ADD_INT_FIELD("frame", frame.GetStackDepth() );
          frame.GetDebugger().RecordEvent(EEventType.kEVT_FRAME_CURRENT, fields);
        }
      }
   }

   /**
    * @return the frame whose transitions are currently being checked, or null if this is not the transition-checking phase
    */
   public SB_ExecutionFrame getCheckedFrame()
   {
       return _checkedFrame;
   }

   /**
    * Sets the frame whose transitions are currently being checked.
    * @param checkedFrame the frame being checked
    */
   public void setCheckedFrame(SB_ExecutionFrame checkedFrame)
   {
       _checkedFrame = checkedFrame;
   }
   
 	/**
 	 * Indicates that the current frame is entering a sub-behavior.
 	 * @param subMode the execution mode of the sub-behavior
 	 */
 	public void enterSubBehavior(int subMode)
	{
 		// if the current subbehavior mode is higher precedence, use that instead
 		if (subMode > getExecMode())
 		{
 			_execMode.add(new Integer(subMode));
 			_modeInvoker.add( getCurrentFrame() );		
 		}
 	}

 	/**
 	 * Indicates that the current frame is exiting a sub-behavior.
 	 */
 	public void exitSubBehavior()
	{
 		// entering a sub-behavior doesn't necessarily change the execution mode,
 		// so exiting shouldn't either
		 if ((_modeInvoker.size() > 0) && (getCurrentFrame().equals( _modeInvoker.get(_modeInvoker.size()-1) )))
		 {
				// end of the current execution mode
				_execMode.remove( _execMode.size()-1 );
				_modeInvoker.remove( _modeInvoker.size()-1 );
		 }
 	}

 	/**
 	 * Indicates the current frame is entering a compound node
 	 */
 	public void enterCompoundNode()
 	{
		enterSubBehavior(SB_BehaviorClass.kMODE_ONETICK | SB_BehaviorClass.kMODE_NONINTERRUPTIBLE);
		if( _currentFrame.GetBehavior().GetClass().IsInterruptible())
			_noninterruptibleFrame.add(_currentFrame);
 	}
 	
 	/**
 	 * Indicates the current frame is exiting a compound node
 	 *
 	 */
 	public void exitCompoundNode()
 	{
 		exitSubBehavior();
 		if( _currentFrame.GetBehavior().GetClass().IsInterruptible())
			_noninterruptibleFrame.remove(_currentFrame);
 	}
 	
	/**
	 * @return the current execution mode
	 */
 	public int getExecMode()
  {
		if (_execMode.size() == 0)
		{
			// haven't yet entered a "special" mode
			return SB_BehaviorClass.kMODE_MULTITICK;
		}
		else
		{
			int currMode = ((Integer)_execMode.get(_execMode.size()-1)).intValue();
			return currMode & ~SB_BehaviorClass.kMODE_NONINTERRUPTIBLE; 
		}
  }
   		
  public boolean IsBelow(SB_ExecutionFrame frame1,SB_ExecutionFrame frame2)
	{
    return frame1.GetStackDepth() < frame2.GetStackDepth();
  }
   
 	/**
 	 * @return true if the stack is in one-tick mode
 	 */
  boolean isOneTick() 
	{ 
		return (getExecMode() & SB_BehaviorClass.kMODE_ONETICK) != 0; 
	}

 	/**
 	 * @return true if the stack is in run-until-blocked mode
 	 */
  boolean isRunUntilBlocked() 
	{ 
	 	return (getExecMode() & SB_BehaviorClass.kMODE_RUNUNTIL) != 0; 
	}

	/**
	 * @return a string summarizing the current execution mode
	 */
	String getStatusString()
	{
		String temp = "";
		temp += (isOneTick() ? "OT" : (isRunUntilBlocked() ? "RUB" : "MT"));
		
		if (_noninterruptibleFrame.size() > 0) 
			temp += "/NI";
		if (_interruptFrame.size() > 0)
			temp += "/INT";

		return temp; 		
	}
  
  

   public SB_ExecutionFrame GetFrameByDepth(int depth){
     if ((depth > _stack.size()) || (_stack.size() == 0))
       return null;

     return (SB_ExecutionFrame)_stack.get(depth-1);
   }

   public void Push(SB_ExecutionFrame newFrame)
   {
     _stack.add(newFrame);

     SetCurrentFrame(newFrame);

     newFrame.SetStackDepth( _stack.size() );

		 int newMode = newFrame.GetBehavior().GetClass().GetExecMode();
		 if (newMode > getExecMode())
		 {
				// entering a higher-precedence execution mode
				_execMode.add(new Integer(newMode));
				_modeInvoker.add(newFrame);
		 }
 		 if (newFrame.IsInterrupt())
		 {
				// starting a new interrupt chain
				_interruptFrame.add(newFrame);
		 }
		 if (!newFrame.GetBehavior().GetClass().IsInterruptible())
		 {
				// starting a new non-interruptible chain
				_noninterruptibleFrame.add(newFrame);
		 }
	 }
   
   /**
    * NOTE: Pop has been extended to include runtime exception handling
    *
    *  The Pop function checks the exception storage variables.
	  * 	If it is the exception behavior
	  * 	- Pop the node as normal
	  *  If it is the exception behaviors parent
	  *  - Call Handle Exception
	  *  If it is an intervening node
	  *  - Put on temp stack
	  * 
    * @return An execution frame if the frame is complete, null if focus transferred to the always node.
    */
   	public SB_ExecutionFrame Pop() throws SB_Exception
    {
   	    if (getCurrentFrame() == null)
   	        throw new SB_Exception("[" + _owner.toString() + "] Attempted to pop empty behavior stack.");
   	    
        SB_Node tempNode = getCurrentFrame().GetBehavior().GetAlwaysNode();
        if (tempNode != null && !getCurrentFrame().GetAlwaysNodeInvoked())
        {
            getCurrentFrame().SetCurrNode(tempNode);
            return null;
        }	
   		
       	SB_ExecutionFrame popped = PerformPop();
   	 
		//Select the next item as the current frame. Depends on the exception
        //status
        if (_stack.size() == 0)
        {
            SetCurrentFrame(null);

            if (!_currentExceptionCaught && _currentException != null)
            {
                throw _currentException;
            }
        } else
        {
            if (!_currentExceptionCaught && _currentException != null)
            {
                //At this point, we want to pop to the parent. However, we
                //can't do this unless the temporaryExceptionStack is empty. Each item also
                //needs to be able to perform its always node.

                if (_temporaryExceptionStack.size() > 0)
                {
                    int lastIndex = _temporaryExceptionStack.size() - 1;
                    SB_ExecutionFrame tempFrame = (SB_ExecutionFrame) _temporaryExceptionStack
                            .get(lastIndex);
                    _temporaryExceptionStack.remove(lastIndex);
                    Push(tempFrame);
                    return Pop();
                } else
                {
                    SB_ExecutionFrame parent = PopDownToUsingTempStack(_currentExceptionParent);
                    if (parent == null)
                    {
                        // make sure the stack is cleaned up, just in case we were in the midst
                        // of invoking a behavior when the exception was thrown
                        Reset();
                        throw _currentException;
                    } else
                    {
                        SetCurrentFrame((SB_ExecutionFrame) _stack.get(_stack
                                .size() - 1));
                        HandleException(_currentException, parent);
                    }
                }
            } else
                SetCurrentFrame((SB_ExecutionFrame) _stack
                        .get(_stack.size() - 1));
        }

        return popped;
    }
   
   /**
    * The helper function that actually performs the pop. It simply removes the
    * top items and performs the cleanup.
    *
    */
   private SB_ExecutionFrame PerformPop()
   {
		 SB_ExecutionFrame popped = (SB_ExecutionFrame)_stack.get(_stack.size() - 1);
		 _stack.remove(_stack.size() - 1);
	
		 if ((_modeInvoker.size() > 0) && (popped.equals( _modeInvoker.get(_modeInvoker.size()-1) )))
		 {
				// end of the current execution mode
				_execMode.remove( _execMode.size()-1 );
				_modeInvoker.remove( _modeInvoker.size()-1 );
		 }
		 if ((_interruptFrame.size() > 0) && (popped.equals( _interruptFrame.get( _interruptFrame.size()-1 ))))
		 {
				// end of the current interrupt chain
				_interruptFrame.remove( _interruptFrame.size()-1 );
		 }
		 if ((_noninterruptibleFrame.size() > 0) && 
		 			(popped.equals( _noninterruptibleFrame.get( _noninterruptibleFrame.size()-1 ))))
		 {
				// end of the current noninterruptible chain
				_noninterruptibleFrame.remove( _noninterruptibleFrame.size()-1 );
		 }  
		 
		 return popped;
   }
   
   /**
    * Similar to PopDownTo except that rather than Pop, PerformPop is called and
    * popped items are places on the tempstack. The temp stack is emptied at the beginning
	  * of the process since the existing nodes can never be used.
    * @param newTop
    * @return newTop if found, null otherwise
    */
   public SB_ExecutionFrame PopDownToUsingTempStack(SB_ExecutionFrame newTop)
   {
   	if( newTop == null)
   		return null;
   	
		 if (SIM_Constants.DEBUG_INFO_ON)
	 		newTop.GetLogger().log("[" + _owner.toString() + ",STK " + 
	 								((SB_ExecutionFrame)_stack.get(_stack.size()-1)).GetStackDepth() 
	 								+ "] BEHAVIOR:\tpopping down to " + 
	 								newTop.GetLogName(),
									SB_Logger.BEHAVIOR); 
   	
    while (!_stack.isEmpty() && (_stack.get(_stack.size() - 1) != newTop))  
    {

			 if( SIM_Constants.AI_DEBUGGER )
			 {
			    DMFieldMap fields = new DMFieldMap();
			    fields.ADD_ID_FIELD("entity",  _owner.GetId()._id );
					fields.ADD_INT_FIELD( "frame", ((SB_ExecutionFrame)_stack.get(_stack.size() - 1)).GetStackDepth() );
			    newTop.GetDebugger().RecordEvent(EEventType.kEVT_FRAME_DISCARDED, fields);
			 }

			 if (SIM_Constants.DEBUG_INFO_ON)
			 		newTop.GetLogger().log("[" + _owner.toString() + ",STK " + 
			 								((SB_ExecutionFrame)_stack.get(_stack.size()-1)).GetStackDepth() 
			 								+ "] BEHAVIOR:\tput on temp stack " + ((SB_ExecutionFrame)_stack.get(_stack.size()-1)).GetLogName(),SB_Logger.BEHAVIOR); 
			 
			 SB_ExecutionFrame popped = PerformPop();
			 _temporaryExceptionStack.add(popped);	
    }
    
    if( !_stack.isEmpty() )
    	return newTop;
    else
    	return null;
   }
   
   public void PopDownTo(SB_ExecutionFrame newTop) throws SB_Exception
   {
     while (!_stack.isEmpty() && (_stack.get(_stack.size() - 1) != newTop))  {

			 if( SIM_Constants.AI_DEBUGGER )
			 {
			    DMFieldMap fields = new DMFieldMap();
			    fields.ADD_ID_FIELD("entity",  _owner.GetId()._id );
					fields.ADD_INT_FIELD( "frame", ((SB_ExecutionFrame)_stack.get(_stack.size() - 1)).GetStackDepth() );
			    newTop.GetDebugger().RecordEvent(EEventType.kEVT_FRAME_DISCARDED, fields);
			 }

			 if (SIM_Constants.DEBUG_INFO_ON)
			 		newTop.GetLogger().log("[" + _owner.toString() + ",STK " + 
			 								((SB_ExecutionFrame)_stack.get(_stack.size()-1)).GetStackDepth() 
			 								+ "] BEHAVIOR:\tdisrupted " + ((SB_ExecutionFrame)_stack.get(_stack.size()-1)).GetLogName(),SB_Logger.BEHAVIOR); 
			 
			 Pop();	
     }
   }

   /**
    * @return the height of the stack
    */
   public int getSize() { return _stack.size(); }


   public void NextTransition(SB_Logger logger) throws SB_Exception 
   {
     if (getSize() == 0)
       return;		// base-level behavior has completed
       
		 int firstInterruptingFramePos = GetFirstInterruptingFrame();
		 int firstPassiveFramePos = GetFirstPassiveFrame(firstInterruptingFramePos);
		 int firstActiveFramePos = GetFirstActiveFrame();

		 int currentFramePos = (firstInterruptingFramePos != firstPassiveFramePos) ?
														firstInterruptingFramePos : firstActiveFramePos;

		 boolean foundTransition = false;

		 // check the transitions out of the current state in each execution frame, 
		 // starting from the bottom of the "interrupting" segment of the stack
		 do
		 {
				if (currentFramePos == firstPassiveFramePos) 
				{
					// skip all the frames in the passive stack and jump straight
					// to the start of the active stack
					currentFramePos = firstActiveFramePos;
				}

       SB_ExecutionFrame frame = (SB_ExecutionFrame)_stack.get(currentFramePos);
       
               setCheckedFrame(frame);  // so that IsDone knows where to look
       
       //If frame has not been executed in current node, then don't transition
   			if (frame != null && !frame.HasBeenExecuted())
   			{
   				++currentFramePos;
   				continue;
   			}
   			
       // find and follow an active transition on the current state of this frame, if possible
       ETransitionResult result = null;
       //JRL - this try catch block seems to interfere with exception handling. Not sure if
       // was even supposed to be here.
       //try{
       		result = frame.FollowTransition();
       //}catch(SB_Exception e){
       //  	return;	
       //}

       if(result == ETransitionResult.kNormal){
					// an active transition was found and followed in this frame, so
					// pop off the frames above it 
					PopDownTo(frame);

					// no need to check for other transitions
					foundTransition = true;
       }
       else if (result == ETransitionResult.kInterrupt) {
					// an interrupt transition was followed, so leave the existing
					// stack intact and set the interrupting frame to be the new current
					// behavior (so it can invoke the interrupt behavior on the next tick)
					SetCurrentFrame(frame);

					// no need to check for other transitions
					foundTransition = true;
			 }

			 // try the next frame up in the stack
			 ++currentFramePos;
			 
		 } while ( !foundTransition && (currentFramePos < getSize()) ); 
   }

	private int GetFirstInterruptingFrame()
	{
		if (_noninterruptibleFrame.size() > 0)
		{
			// at least one non-interruptible chain is on the stack,
			// so transition checking must start at the highest such chain
			SB_ExecutionFrame nonintFrame = (SB_ExecutionFrame) _noninterruptibleFrame.get(_noninterruptibleFrame.size()-1);
			return nonintFrame.GetStackDepth()-1;
		}
		else
		{
			// just start at the bottom of the stack
			return 0;
		}
	}


	private int GetFirstPassiveFrame(int firstInterruptingFramePos)
	{
		if (_interruptFrame.size() > 0)
		{
			// at least one interrupt chain on the stack, so the passive segment
			// of the stack begins at the parent of the topmost interrupt 
			SB_ExecutionFrame intFrame = (SB_ExecutionFrame)_interruptFrame.get( _interruptFrame.size()-1 );
			int highestIntInvokerPos = intFrame.GetParent().GetStackDepth()-1;
			if (highestIntInvokerPos >= firstInterruptingFramePos)
			{
				return highestIntInvokerPos;
			}
			else 
			{
				// the invoker of the highest interrupt chain is *below* the highest
				// non-interruptible chain and thus not open to consideration
				return -1;
			}
		}
		else 
		{
			// no interrupt chain, so we can safely check transitions all the
			// way to the top of the stack
			return -1;
		}
	}
	
	private int GetFirstActiveFrame()
	{
		if (_interruptFrame.size() > 0)
		{
			// the active stack starts at the beginning of the highest interrupt chain
			SB_ExecutionFrame intFrame = (SB_ExecutionFrame)_interruptFrame.get( _interruptFrame.size()-1 );			
			return intFrame.GetStackDepth()-1;
		}
		else
		{
			// no interrupt chains, so the active stack == the interrupting stack
			return -1;
		}
	}
	
	/**
	 * If an exception has been called then
	 *  - Setup exception storage variables
	 * 	- Check the current behavior for a catch node.
	 * 			If it has a catch node, set the current node to be the catch node, proceed as normal
	 *  - Otherwise Pop this node
	 * 
	 * NOTE: The Pop function checks the exception storage variables.
	 * If it is the exception behavior
	 * 	- Pop the node as normal
	 * If it is the exception behaviors parent
	 *  - Call Handle Exception
	 * If it is an intervening node
	 *  - Put on temp stack
	 * 
	 * @param ex
	 * @param exceptionFrame
	 * @param exceptionBehavior
	 * @param bNewException true if this is a new exception, false if it is the same exception thrown up
	 */
	public void HandleException(SB_Exception ex,
            SB_ExecutionFrame exceptionFrame) throws SB_Exception
    {
        _currentException = ex;
        _currentExceptionFrame = exceptionFrame;
        _currentExceptionFrameState = exceptionFrame.GetState();
        _currentExceptionCaught = false;
        _currentExceptionParent = _currentExceptionFrame.GetParent();

        if (SIM_Constants.DEBUG_INFO_ON)
        {
            if(_stack.size() > 0)
                exceptionFrame.GetLogger().log(
	            		"["
	                + _owner.toString()
	                + ",STK "
	                + ((SB_ExecutionFrame) _stack.get(_stack.size() - 1)).GetStackDepth()
	                + "] BEHAVIOR:\tHandle Exception "
	                + exceptionFrame.GetLogName(), SB_Logger.BEHAVIOR);
        }

        
        SB_Behavior behavior = _currentExceptionFrame.GetBehavior();
        
        if( behavior == null )
            throw ex;
        
        SB_Node tempNode = behavior.GetCatchNode();
        if (tempNode != null)
        {
            //  If there was an interupt, mark it as done before handling the
            // node
            //  Also, mark sub behaviors as completed
            _currentExceptionFrame.SetInvokedDone(true);
            if (_currentExceptionFrame.InvokedInterrupt())
                _currentExceptionFrame.InterruptDone();

            _currentExceptionCaught = true;
            _currentExceptionFrame.SetCurrNode(tempNode);

            if (SIM_Constants.DEBUG_INFO_ON)
                _currentExceptionFrame.GetLogger().log(
                        "["
                                + _owner.toString()
                                + ",STK "
                                + ((SB_ExecutionFrame) _stack
                                        .get(_stack.size() - 1))
                                        .GetStackDepth()
                                + "] BEHAVIOR:\tCaught Exception in "
                                + _currentExceptionFrame.GetLogName(),
                        SB_Logger.BEHAVIOR);

        } 
        else
        {
            Pop();
        }
    }
	
	/**
	 * Throw the current exception up. This is done by setting caught to false
	 * and popping the current frame. 
	 * 
	 * This assumes that the node was caught in the function HandleException
	 *
	 */
	public void Rethrow() throws SB_Exception
	{
		_currentExceptionCaught = false;
		Pop();
	}
	
	/**
	 * Put the intermediate items back on the stack and transfer control to the top 
	 * intermediate item as if the node had completed.
	 * 
	 * @throws SB_Exception
	 */
	public void Resume() throws SB_Exception
	{
		//	Reset state of the handling frame
		_currentExceptionFrame.SetState(_currentExceptionFrameState);
		
		//Set it up as completed
		_currentExceptionFrame.SetNodeExecuted();
		
		//Put items on stack
		 while (!_temporaryExceptionStack.isEmpty())
		 {
		 	int lastIndex = _temporaryExceptionStack.size() - 1;
 	 		SB_ExecutionFrame tempFrame = (SB_ExecutionFrame)  _temporaryExceptionStack.get(lastIndex); 
 	 		_temporaryExceptionStack.remove(lastIndex);
 	 		Push( tempFrame );	
		 }	
	}
	
	/**
	 * Put the intermediate items back on the stack and trasfer control to the top intermediate
	 * item as if had NOT been completed.
	 * 
	 * @throws SB_Exception
	 */
	public void Retry() throws SB_Exception
	{
		//	Reset state of the handling frame
		_currentExceptionFrame.SetState(_currentExceptionFrameState);
		
		//Set it up as not completed
		_currentExceptionFrame.SetNodeBeenInvoked(false);
		if(_currentExceptionFrame.InvokedInterrupt())
  		_currentExceptionFrame.InterruptDone();
		
		//Put items on stack
		 while (!_temporaryExceptionStack.isEmpty())
		 {
		 	int lastIndex = _temporaryExceptionStack.size() - 1;
 	 		SB_ExecutionFrame tempFrame = (SB_ExecutionFrame)  _temporaryExceptionStack.get(lastIndex); 
 	 		_temporaryExceptionStack.remove(lastIndex);
 	 		Push( tempFrame );	
		 }	
	}
	
	/**
	 * Trickle down finishing for every SB_ExecutionFrame and SB_ExceuctionFrameState
	 * 
	 * @param book
	 * @throws SB_Exception
	 */
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	    //protected ArrayList _stack = new ArrayList(); //SB_ExecutionFrame
	    finishArray(_stack, book);
	    
	    //protected SB_ExecutionFrame _currentFrame;
	    if(_currentFrame != null)
	        _currentFrame.finishDeserialization(book);
	    
	    //protected ArrayList _modeInvoker = new ArrayList(); //SB_ExecutionFrame
	    finishArray(_modeInvoker, book);
	    
	    //protected ArrayList _interruptFrame = new ArrayList(); //SB_ExecutionFrame
	    finishArray(_interruptFrame, book);
	    
	    //protected ArrayList _noninterruptibleFrame = new ArrayList(); //SB_ExecutionFrame
	    finishArray(_noninterruptibleFrame, book);
	    
	  	//protected SB_ExecutionFrame _currentExceptionFrame = null;
	    if(_currentExceptionFrame != null)
	        _currentExceptionFrame.finishDeserialization(book);
	    
	  	//protected SB_ExecutionFrame	_currentExceptionParent = null;
	    if(_currentExceptionParent != null)
	        _currentExceptionParent.finishDeserialization(book);
	    
	  	//protected ArrayList _temporaryExceptionStack = new ArrayList(); //SB_ExecutionFrame
	    finishArray(_temporaryExceptionStack, book);
	    
	  	//protected SB_ExecutionFrameState _currentExceptionFrameState = null; //State prior to execption
	    if(_currentExceptionFrameState != null)
	        _currentExceptionFrameState.finishDeserialization(book);
  }
	
	/**
	 * Finish deserializatin for an array of SB_ExecutionFrames
	 * @param array
	 * @param book
	 */
	void finishArray(ArrayList array, SB_SingletonBook book) throws SB_Exception
	{
	    int count = array.size();
	    for(int x = 0; x < array.size(); x++)
	    {
	        ((SB_ExecutionFrame) array.get(x)).finishDeserialization(book);
	    }
	}
}