package com.stottlerhenke.simbionic.engine.core;


import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.dynamicscripting.DynamicScriptingWrapper;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.debug.EEventType;


/**
 * Represents an action node in a behavior
 */
public class SB_ActionNode extends SB_Node
{
	private final static String CHOOSE = "chooseDS";
	
	protected ArrayList _args = new ArrayList(); //Arguments used in executing this action
	protected ArrayList _originalTransitionOrder = new ArrayList(); //Used to cache the original order
	
	protected String _actionExpr;
	

	public SB_ActionNode(SB_BehaviorElement owner, int nodeID)
	{
		super(owner, nodeID);
	}

	public ENodeType GetNodeType()
	{
		return ENodeType.kActionNode;
	}

	public String GetLogName()
	{
	    return _actionExpr;
	}

	public void initialize(ArrayList transitions, SB_SingletonBook book)
			throws SB_Exception
	{
		super.initialize(transitions, book);
		
		_bindings.initParamBindings();
		
		_originalTransitionOrder.addAll(_transitionEdges);
	}

	/**
	 * Replaces the action for this node if it matches the new action's name.
	 * @param newMethod the action with which to replace existing action
	 */
	public void replaceAction(SB_Action newMethod)
	{
	  // TODO: we are not storing SB_Action anymore.
	}			
	
	public void Execute(SB_Entity entity, SB_ExecutionFrame contextFrame,
			SB_SingletonBook book) throws SB_Exception
	{
		// only execute the node when first entering it (ie not on every
		// clock tick afterward while remaining in the node)
		if (!contextFrame.HasNodeBeenInvoked())
		{
			contextFrame.SetNodeBeenInvoked(true);
			
			// apply the variable bindings for the node before execution.
			_bindings.ApplyForVariables(entity, contextFrame);
			
				if (SIM_Constants.AI_DEBUGGER)
				{
					DMFieldMap fields = new DMFieldMap();
					fields.ADD_ID_FIELD("entity", entity.GetId()._id);
					fields.ADD_INT_FIELD("frame", contextFrame.GetStackDepth());
					fields.ADD_STR_FIELD("funcName", GetLogName());
					contextFrame.GetDebugger().RecordEvent(
							EEventType.kEVT_ACTION_INVOKED, fields);
				}
				if (SIM_Constants.DEBUG_INFO_ON)
					book.getLogger().log(
							"[" + entity.toString() + ",STK " + contextFrame.GetStackDepth()
									+ "] ACTION:\tinvoking " + GetLogName(),
							SB_Logger.ACTION);
				
				 
				try {
				   // run javaScript engine with the action expression
				   SB_JavaScriptEngine jsEngine = book.getJavaScriptEngine();
				   jsEngine.evaluate(_actionExpr, contextFrame); 
				} catch(SB_Exception ex){
		         System.err.println(ex.getMessage());
		      	//Log the exception
                if(SIM_Constants.DEBUG_INFO_ON)
                {
                    book.getLogger().log("[" + entity.toString() + ",STK " + contextFrame.GetStackDepth()
                                        + "] ACTION:\tcaused an exception", SB_Logger.ERROR);		    
                    book.getLogger().logException(ex);
                } 
		    	
		    	SB_EntityData state = entity.GetState();
		  		state.GetExecStack().HandleException(ex, contextFrame);
		  		return;
				} 
		    
				if (SIM_Constants.DEBUG_INFO_ON)
					book.getLogger().log(
							"[" + entity.toString() + ",STK " + contextFrame.GetStackDepth()
									+ "] ACTION:\tcompleted", SB_Logger.ACTION);
				
		}
	}

	/**
	 * Loads a single action node from the specification.
	 * 
	 * @param reader
	 *          the stream reader from which to read
	 * @throws SB_FileException
	 */
	public void load(ActionNode node, SB_SingletonBook book, List<Integer> transitionIds)
			throws SB_FileException
	{
		super.load(node, book, transitionIds);
		
		// read in the action expression.
		_actionExpr = node.getExpr();
		
		if (_actionExpr == null) {
		   _actionExpr = "None";
		}
		
		_alwaysNodeFlag = node.isAlways();
		_catchNodeFlag = node.isCatch();
	}
	
	

	@Override
	public SB_TransitionEdge FollowTransition(SB_Entity p, SB_ExecutionFrame contextFrame, SB_SingletonBook book) throws SB_Exception
	{
		// copies from SB_Node.FollowTransition
		if (!contextFrame.HasNodeBeenInvoked())
		{
			return null;
		}
		  
		//If we are at a choice point, re-order transition edges
		String choicePointName = null;
		if(isChoicePointAction())
		{
			//Get the choice point name from the chooseDS javascript function
			SB_JavaScriptEngine jsEngine = book.getJavaScriptEngine();
			choicePointName = (String) jsEngine.evaluate(_actionExpr, contextFrame);

			reorderTransitions(choicePointName, p);
		}
		
		SB_TransitionEdge edge = super.FollowTransition(p, contextFrame, book);
		
		//Reset the transitions back to the original order if changed
		if(isChoicePointAction())
		{
			_transitionEdges.clear();
			_transitionEdges.addAll(_originalTransitionOrder);
			
			int index = edge.getPriority();
			DynamicScriptingWrapper.getInstance().actionSelectedBySimbionic(choicePointName, index, p.GetName());
		}
		
		return edge;
	}
	
	/**
	 * Use the dynamic scripting wrapper to reorder the transitions out of the SB_ActionNode
	 * based on the DS selection procedure.
	 * 
	 * @throws SB_Exception
	 */
	private void reorderTransitions(String state, SB_Entity p) throws SB_Exception
	{
		if(_transitionEdges.size() > 1)
		{
			int[] order = DynamicScriptingWrapper.getInstance().orderActions(state, p.GetName(), _transitionEdges.size());
			
			//Using a subscript, may be shorter
			//if(order.length != _transitionEdges.size())
			//	throw new SB_Exception("Dynamic scripting ordering list (" +order.length + ") is not the same size as the choice action transition list (" +_transitionEdges.size() + ")");
			
			//System.out.print("[");
			ArrayList temp = new ArrayList();
			for(int x = 0; x < order.length; x++)
			{
				int sbIndex = order[x];
				SB_TransitionEdge edge = getTransitionEdge(sbIndex);
				if(edge != null) {
					temp.add(edge);
					//System.out.print(order[x] + ",");
				}
				else
					throw new RuntimeException("Null transistion edge found in dynamic script.");
			}
			//System.out.println("]");
			
			_transitionEdges.clear();
			_transitionEdges.addAll(temp);
		}
	}
	
	/**
	 * 
	 * @return the transition edge with the given SimBionic priority (1-based)
	 */
	protected SB_TransitionEdge getTransitionEdge(int sbPriority) {
		
		for(Object obj : _transitionEdges) {
			SB_TransitionEdge edge = (SB_TransitionEdge)  obj;
			if(edge.getPriority() == sbPriority)
				return edge;
		}
		
		return null; //not found
	}
	
	public boolean isChoicePointAction() {
		if(_actionExpr!= null && _actionExpr.contains(CHOOSE))
			return true;
		else
			return false;
	}
	
}