package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
/**
 * This class forms a compound node - a list of action and behavior nodes that
 * are all executed in a single tick.
 */
public class SB_CompoundNode extends SB_Node
{
	// Compound action node data model.
    private CompoundActionNode _compoundActionNode;
    
	/**
	 * @param owner
	 * @param nodeID
	 */
	public SB_CompoundNode(SB_BehaviorElement owner, int nodeID)
	{
		super(owner, nodeID);
		_nodes = new Vector();
	}

	/**
	 * @see com.stottlerhenke.simbionic.engine.core.SB_Node#GetNodeType()
	 */
	public ENodeType GetNodeType()
	{
		return ENodeType.kCompoundNode;
	}

	/**
	 * Reads a compound node from the specification.
	 * 
	 * @param reader
	 */
	public void load(CompoundActionNode compoundNode, SB_SingletonBook book, List<Integer> transitionIds)
			throws SB_FileException
	{
		super.load(compoundNode, book, transitionIds); 
		
		_compoundActionNode = compoundNode;
	}

	/**
	 * Override the default implementation. Initialize this node and all subnodes
	 */
	public void initialize(ArrayList transitions,SB_SingletonBook book)
	throws SB_Exception
	{
		super.initialize(transitions, book);
	
		// create the list of ActionNodes from the compound action data model.
		ActionNode actionNodeModel = null;
		List<ActionNode> nodeModels = new ArrayList<ActionNode>();
		for (Binding binding : _compoundActionNode.getBindings()) {
		   if (SB_Binding.isAction(binding)) { // action binding
		      // create action node
		      if (actionNodeModel == null) {
		         actionNodeModel = new ActionNode();
		         actionNodeModel.setId(_compoundActionNode.getId());
		         actionNodeModel.setAlways(_compoundActionNode.isAlways());
			     actionNodeModel.setCatch(_compoundActionNode.isCatch());
		         nodeModels.add(actionNodeModel);
		      }
		      
		      actionNodeModel.setExpr(binding.getExpr());
		      actionNodeModel = null;
		      
		   } else{
		      if (actionNodeModel == null) {
		         actionNodeModel = new ActionNode();
		         actionNodeModel.setId(_compoundActionNode.getId());
		         actionNodeModel.setAlways(_compoundActionNode.isAlways());
                 actionNodeModel.setCatch(_compoundActionNode.isCatch());
		         nodeModels.add(actionNodeModel);
		      }
		      
		      actionNodeModel.addBinding(binding);
		   }
		}
		
		for (ActionNode actionNode : nodeModels) {
			String name = SB_BehaviorNode.getBehaviorName(actionNode.getExpr());
			// if this action node is behavior, then create behavior node.
			// otherwise create SB_ActionNode.
			if (book.getBehaviorRegistry().isBehaviorDefined(name)) {
				SB_BehaviorNode behaviorNode = new SB_BehaviorNode(this, actionNode.getId());
				// transition ids are empty
				behaviorNode.load(actionNode, book, new ArrayList<Integer>());
				_nodes.add(behaviorNode);
			} else {
				SB_ActionNode sbNode = new SB_ActionNode(this, actionNode.getId());
				// transition ids are empty.
				sbNode.load(actionNode, book, new ArrayList<Integer>());
				_nodes.add(sbNode);
			}
		} 

		// finally initialize the nodes.
		for (int i = 0; i < _nodes.size(); i++)
	  {
		   
			((SB_Node)_nodes.get(i)).initialize(transitions,book);
	  }		
	}
	
	
	/** 
	 * Each time this is called, execute another action
	 * 
	 */
	public void Execute(SB_Entity entity, SB_ExecutionFrame contextFrame,
			SB_SingletonBook book) throws SB_Exception
	{
		int nodeIndex;
		if (!contextFrame.HasNodeBeenInvoked())
		{
			contextFrame.SetNodeBeenInvoked(true);
			nodeIndex = 0;
		} 
		else
		{
			nodeIndex = contextFrame.GetNodeInvocationCount();
			contextFrame.SetNodeBeenInvoked(nodeIndex + 1);
		}
		
		if (nodeIndex < _nodes.size())
		{
			contextFrame.SetNodeBeenInvoked(false); //Actions require this for the current frame
			SB_Node tempNode = ((SB_Node) _nodes.get(nodeIndex));
			tempNode.Execute(entity, contextFrame, book);
			contextFrame.SetNodeBeenInvoked(nodeIndex + 1); //Set back to the correct value afterwords
			
			//Compound nodes are one-tick, so tell the entity to keep going
			//SB_EntityData state = entity.GetState();
			//state.SetDoAnotherTick(true);
		}
		else
		if(nodeIndex >= _nodes.size() ) //First time after all executed
		{
		  contextFrame.checkExitCompoundNode();
		  
		  if (SIM_Constants.DEBUG_INFO_ON)
			  book.getLogger().log( "[" + entity.toString() + "] MSG: Completed compound node, execution mode set to: " + entity.GetState().GetExecStack().getStatusString(),SB_Logger.TICK);

		}
	}

	/**
	 * @return The number of times Execute needs to be called for this node
	 *         (default is once)
	 */
	public int GetExecutionCount()
	{
		return _nodes.size() + 1; //We need to execute this one extra time to remove OT/NI
	}

	Vector _nodes;
}