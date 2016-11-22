package com.stottlerhenke.simbionic.engine.core;
import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Connector;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Local;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Node;
import com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Start;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * An instance of this class represents a single Behavior Transition
 * Network defined by the author.
 */
public class SB_Behavior extends SB_BehaviorElement 
{
	// nodes are actions
  protected ArrayList	_nodes = new ArrayList();
  protected ArrayList	_conditions = new ArrayList();
  protected ArrayList	_transitions = new ArrayList();
  
  // initial node in this behavior.
  protected SB_Node	_initNode;
  protected ArrayList	_hierIndices;
  protected ArrayList	_hierDepths = new ArrayList();
  
  private static final int ACTION_NODE_TYPE = 0;
  private static final int CONDITION_NODE_TYPE = 1;

  // one set of variables must be created for each instance of this behavior
  protected SB_VariableMap	_varTemplate;

  public SB_Behavior(SB_BehaviorElement owner) {
    super(owner);
    _initNode = null;
    _hierIndices = new ArrayList();//null;
  }
  /**
   * @return true if behavior has the named variable, false otherwise
   */
  public boolean HasVariable( String id){
    if (!_varTemplate.IsVariable(id))
                return true;
    return false;
  }

  /**
   * @return the name of this behavior
   */
  public String getName()  { return _owner.getName(); }

  /**
   * @return the initial node
   */
  public SB_Node GetInitNode() { return _initNode;}

  /**
   * @return the parent class of the behavior
   */
  public SB_BehaviorClass GetClass() { return (SB_BehaviorClass)_owner; }
  /**
   * @return the list of hierarchy depths (for polymorphism resolution)
   */
  public ArrayList GetHierarchyDepths() { return _hierDepths; }

  /**
   * @return the list of all nodes
   */
  public ArrayList GetNodes() {return _nodes;}

  /**
   * @return the node with the given id if found, null otherwise
   */
  public SB_Node GetNode(int id)
  {
      SB_Node returnValue = null;
      
    	int count = _nodes.size();
    	for( int x = 0; x < count; x++ )
    	{
    		SB_Node tempNode = (SB_Node) _nodes.get(x);
    		if(tempNode.getId() == id )
    		{
    		    returnValue = tempNode;
    		    break;
    		}
      }
      
      return returnValue;
  }
  
  /**
   * @return the list of all conditions
   */
  public ArrayList GetConditions() {return _conditions;}
  
  /**
   * @return the condition node with the given id if found, null otherwise
   */
  public SB_Condition GetCondition(int id) {
     SB_Condition returnValue = null;
     
     int count = _conditions.size();
     for( int x = 0; x < count; x++ )
     {
        SB_Condition tempNode = (SB_Condition) _conditions.get(x);
        if(tempNode.getId() == id )
        {
            returnValue = tempNode;
            break;
        }
     }
     
     return returnValue;
     
  }

  /**
   * @return the list of all transitions
   */
  public ArrayList GetTransitions() {return _transitions;}

  /**
   * @return the variable template for this behavior
   */
  public SB_VariableMap GetVarTemplate() { return _varTemplate; }

  /**
   * There can be at most one catch node per behavior
   * 
   * @return The catch node if one is specified for this node, null otherwise
   */
  public SB_Node GetCatchNode()
  {
  	int count = _nodes.size();
  	
  	for( int x = 0; x < count; x++ )
  	{
  		SB_Node tempNode = (SB_Node) _nodes.get(x);
  		if( tempNode.IsCatchNode())
  		{
  			return tempNode;
  		}
  	}
  	
  	return null;
  }
 
  /**
   * There can be at most one always node per behavior.
   * 
   * @return The always node if one is specified for this node, null otherwise
   */
  public SB_Node GetAlwaysNode()
  {
  	int count = _nodes.size();
  	
  	for( int x = 0; x < count; x++ )
  	{
  		SB_Node tempNode = (SB_Node) _nodes.get(x);
  		if( tempNode.IsAlwaysNode())
  		{
  			return tempNode;
  		}
  	}
  	
  	return null;
  }
  
	/**
	 * Loads a single polymorphism from a data model
	 * @param polyModel the poly model
	 */
  public void load(Poly polyModel, SB_SingletonBook book) 
		throws SB_FileException
	{
    if(SIM_Constants.DEBUG_INFO_ON)
      book.getLogger().log(".Loading polymorphic behavior...",SB_Logger.INIT);

    // read hierarchy indices 
    int numHiers = book.getBehaviorRegistry().getNumHierarchies();
    for (int hier=0; hier < numHiers; ++hier){
      String hierNode = polyModel.getIndices().get(hier);
            
      // store the index for debugging convenience
      _hierIndices.add(hier,hierNode);

      if(SIM_Constants.DEBUG_INFO_ON)
        book.getLogger().log(".Index: " + hierNode,SB_Logger.INIT);
         
      try {
      	_hierDepths.add(hier, new Integer(book.getBehaviorRegistry().indexBehavior(hier,hierNode,_owner.getName(),this)));
      } catch (SB_Exception e) {
      	throw new SB_FileException("Behavior " + getName() + " indexed by nonexistent descriptor " + hierNode);
      }
    }

    // read behavior variables 
    _varTemplate = new SB_VariableMap();
    try
    {
       if(SIM_Constants.DEBUG_INFO_ON)
          book.getLogger().log(".Loading " + polyModel.getLocals().size() + " variables...",SB_Logger.INIT);

       for (Local local : polyModel.getLocals()) {
          // read local variable name
          String name = local.getName();

          // read variable, always create an instance of SB_VarClass.
          SB_Variable var = new SB_VarClass();
          var.setType(local.getType());

          if(SIM_Constants.DEBUG_INFO_ON) {
             String varVal = null;
             if (var.getValue() != null) {
                varVal = var.getValue().getClass().getName();
             }
             book.getLogger().log(".\tLoaded variable " + name + " (" + varVal
                   + ")",SB_Logger.INIT);
          }


          _varTemplate.AddVariable(name, var);
       }
    }
    catch(SB_Exception ex)
    {
      throw new SB_FileException(ex.toString());
    }

    // read nodes 
    readNodes(polyModel, book);

    // read conditions 
    int num = polyModel.getConditions().size();
    for (Condition conditionModel : polyModel.getConditions())
    {
       List<Integer> transitionIds = 
             getTransitionIds(polyModel, conditionModel.getId(), CONDITION_NODE_TYPE);
       SB_Condition cond = new SB_Condition(this);
       cond.load(conditionModel, book, transitionIds);
       _conditions.add(cond);
    }

    // read in the transitions one by one
    SB_TransitionEdge trans;
    for (Start start : polyModel.getConnectors()) {
       for (Connector connector : start.getConnectors()) {
          trans = new SB_TransitionEdge(this);
          trans.load(connector, book);
          trans.setPriority(connector.getPriority());
          _transitions.add(trans);
       }
    }
	}
  
  /**
   * Read the action nodes (including compounds action nodes) from the specified Poly model.
   */
  private void readNodes(Poly polyModel, SB_SingletonBook book) throws SB_FileException {
     // read nodes 
     NodeGroup nodes = polyModel.getNodes();
     int initNodeIndex = nodes.getInitial();

     // the total number of nodes.
     int num = nodes.getActionNodes().size() + nodes.getCompoundActionNodes().size();

     if (initNodeIndex >= num)
        throw new SB_FileException("Illegal initial node " + initNodeIndex + " in behavior spec.");

     for (ActionNode actionNode : nodes.getActionNodes())
     {
        logLoadAction(actionNode, book);
        List<Integer> transitionIds = 
              getTransitionIds(polyModel, actionNode.getId(), ACTION_NODE_TYPE);

        int id = actionNode.getId();
        if (actionNode.isFinal()) {
        	// final node
           SB_FinalNode sbNode = new SB_FinalNode(this, id);
           sbNode.load(actionNode, book, transitionIds);
           _nodes.add(sbNode);
        } else if (actionNode.isBehavior()) {
           // behavior node
           SB_BehaviorNode sbNode = new SB_BehaviorNode(this, id);
           sbNode.load(actionNode, book, transitionIds);
           _nodes.add(sbNode);
        } else {
           // create SB_ActionNode
           SB_ActionNode sbNode = new SB_ActionNode(this, id);
           sbNode.load(actionNode, book, transitionIds);
           _nodes.add(sbNode);
        }
     }

     // load compound action nodes.
     for (CompoundActionNode compoundActionNode : nodes.getCompoundActionNodes())
     {
        logLoadAction(compoundActionNode, book);
        int id = compoundActionNode.getId();
        List<Integer> transitionIds = 
              getTransitionIds(polyModel, compoundActionNode.getId(), ACTION_NODE_TYPE);
        if (compoundActionNode.isFinal()) {
           SB_FinalNode sbNode = new SB_FinalNode(this, id);
           sbNode.load(compoundActionNode, book, transitionIds);
           _nodes.add(sbNode);
        } else {
           SB_CompoundNode sbNode = new SB_CompoundNode(this, id);
           sbNode.load(compoundActionNode, book, transitionIds);
           _nodes.add(sbNode);
        }
     }
     
     // assign the initial node after loading all the nodes.
     //JRL - I think this is wrong -> _initNode = (SB_Node)_nodes.get(initNodeIndex);
     _initNode = GetNode(initNodeIndex); //and this is right

  }
  
  private void logLoadAction(Node node, SB_SingletonBook book) {
     if(SIM_Constants.DEBUG_INFO_ON)
        book.getLogger().log(".Loading node " + node.getId() + "(" + node.getClass().getSimpleName() + ")...",SB_Logger.INIT);
  }
  
  /**
   * Get the list of transitions ids for the given node with the id and type.
   * @param polyModel Poly model to retrieve the transtion ids for the given node.
   * @param nodeId The id of the node to retrieve its transition ids.
   * @param nodeType The type of the node to retrieve its transition ids.
   * @return The list of transtion ids.
   */
  private List<Integer> getTransitionIds(Poly polyModel, int nodeId, int nodeType) {
     List<Integer> transitionIds = new ArrayList<Integer>();
     Start start = polyModel.getConnector(nodeId, nodeType);
     if (start != null) {
        for (Connector connector : start.getConnectors()) {
           transitionIds.add(new Integer(connector.getId()));
        }
     }
     return transitionIds;
  }

  public void initialize(SB_SingletonBook book)
		throws SB_Exception 
  {
    for (int i = 0; i < _nodes.size(); i++)
    {
    	((SB_Node)_nodes.get(i)).initialize(_transitions,book);
    }

    for (int j = 0; j < _conditions.size(); j++)
    {
    	((SB_Condition)_conditions.get(j)).initialize(_transitions,book);
    }
    
    for (int k = 0; k < _transitions.size(); k++)
    {
    	((SB_TransitionEdge)_transitions.get(k)).initialize(book);
    }
  }
  
  // Note: not sure if this method is called at all.
  public void replaceMethod(SB_Method method)
  {
      if (method instanceof SB_Action)
      {
          // action -- check action nodes only
          SB_Action newAction = (SB_Action)method;
	      for (int i = 0; i < _nodes.size(); i++)
	      {
	          SB_Node node = (SB_Node)_nodes.get(i);
	          if (node instanceof SB_ActionNode)
	          {
	              ((SB_ActionNode)node).replaceAction(newAction);
	          }
	      }
      }
      else
      {
          // predicate -- check bindings and conditions
          SB_Function newFun = (SB_Function)method;
	      for (int i = 0; i < _nodes.size(); i++)
	      {
	          ((SB_Node)_nodes.get(i)).replaceFunction(newFun);
	      }
	      for (int j = 0; j < _conditions.size(); j++)
	      {
	      	((SB_Condition)_conditions.get(j)).replaceFunction(newFun);
	      }
	      
	      for (int k = 0; k < _transitions.size(); k++)
	      {
	      	((SB_TransitionEdge)_transitions.get(k)).replaceFunction(newFun);
	      }
      }
  }

  public ArrayList GetHierarchyIndices() {
    ArrayList strVec = new ArrayList();
    for (int i = 0; i < _hierIndices.size();  i++){
      strVec.add( _hierIndices.get(i) );
     }
     return strVec;
  }

  public String toString(){
    String out;
    out =  "Polymorph: " + getName() + " [";
    for (int i = 0; i < _hierIndices.size(); i++){
     out = out + ((String)_hierIndices.get(i)) + " ";
    }
    out = out + "]";
    return out;

  }

}