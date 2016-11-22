package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Node;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * Base class for all behavior elements that can be endpoints
 * of a transition edge (ie sinks or conditions).
 */
public abstract class SB_EdgeSink extends SB_BehaviorElement
{
  protected ArrayList _transitionEdges = new ArrayList();
  protected ArrayList _transIds = new ArrayList();
  protected SB_Bindings _bindings;
  public SB_EdgeSink(SB_BehaviorElement owner) {
    super(owner);
    _bindings = null;
  }

	/**
	 * Reads a single sink from the given Node data model.
	 */
  public void load(Node node,SB_SingletonBook book, List<Integer> transitionIds)
		throws SB_FileException
	{
	  int i, num;

	  _bindings = new SB_Bindings();
	  if (!(node instanceof CompoundActionNode)) {
	     _bindings.load(node.getBindings(),book);   
	  }

	  // read transition IDs
	  num = transitionIds.size();
	  int id;
	  for (i = 0; i < num; i++) {
	    id = transitionIds.get(i);
	    _transIds.add(i, new Integer(id));
	  }
	}

  public SB_TransitionEdge FollowTransition(SB_Entity p,SB_ExecutionFrame contextFrame)
  throws SB_Exception
  {
    // check outgoing edges one by one, following the first active transition
    for (int i=0; i < _transitionEdges.size(); i++){
      SB_TransitionEdge edge = (SB_TransitionEdge)_transitionEdges.get(i);

      if(SIM_Constants.DEBUG_INFO_ON)
        contextFrame.GetLogger().log("["+p.toString() + ",STK " + contextFrame.GetStackDepth() + "] MSG: checking edge "
                         + toString() + " -> " + edge.GetSink().toString());

      // temporarily apply edge bindings so they can be undone after checking this edge
      contextFrame.SaveVariables();
      edge.ApplyBindings(p,contextFrame);

      SB_EdgeSink sink = edge.GetSink();

      if (sink.IsValidDestination(p,contextFrame,this)){
        // keep the variable bindings
        return edge;
      }

      // undo the edge (and possibly condition) variable bindings
      contextFrame.RestoreVariables();

    }
    // if no transitions are active, stay in current node
    return null;
  }
  public String toString(){
    return new String( "SINK" + getId() + ((GetType() == ESinkType.kNode) ? " (node) " : " (cond) "));
  }
  public abstract ESinkType GetType();
  public abstract boolean IsValidDestination(SB_Entity p, SB_ExecutionFrame contextFrame, SB_EdgeSink source) throws SB_Exception;
  public void AssignTransitionEdges(ArrayList allTransitionEdges){
    int numTrans = _transIds.size();

    // replace temporary transition indices with actual transition pointers
    for (int i=0; i < numTrans; i++){
      for (int j = 0; j < allTransitionEdges.size(); j++){
        if (((Integer)_transIds.get(i)).intValue() == ((SB_TransitionEdge)allTransitionEdges.get(j)).getId()){
          _transitionEdges.add(i, ((SB_TransitionEdge)allTransitionEdges.get(j)));
        }
      }
    }
    
    Comparator<SB_TransitionEdge> sortByPriority = new Comparator<SB_TransitionEdge> () {
		public int compare(SB_TransitionEdge arg0, SB_TransitionEdge arg1) {
			return Integer.compare(arg0.getPriority(), arg1.getPriority());
		}
    };
    Collections.sort(_transitionEdges,sortByPriority);
  }
}