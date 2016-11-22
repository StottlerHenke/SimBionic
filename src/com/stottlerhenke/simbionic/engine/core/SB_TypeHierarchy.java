package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Category;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * This class stores and provides access to a complete behavior type hierarchy (as defined 
 * in the authoring tool). 
 */
public class SB_TypeHierarchy  
{
  protected String _name;
  protected SB_TypeNode _root;
  protected HashMap _nodeMap = new HashMap();
  public static final int MAX_DESCR_CAT_LENGTH = 30;

  public SB_TypeHierarchy() {
  }
  public String GetName(){ return _name; }
  public String GetDefault(){
    return _root.GetName();
  }
	
  /**
   * Loads a type hierarchy from the Category data model
   * @param categoryModel the Category model
   */
  public void load(Category categoryModel, SB_SingletonBook book)
		throws SB_FileException
	{
	  int nodeIndex = 0;
	  _root = new SB_TypeNode(null,nodeIndex++);
	  _root.loadSubtree(categoryModel, book,nodeIndex,_nodeMap);
	
	  // the name of the hierarchy itself (as distinct from its root node)
	  // should start with 'g' so that it matches the corresponding global
	  // variable (avoiding unnecessary string concatenation later)
	  _name = new String("g" + _root.GetName());
	}

	/** 
	 * Associates the given polymorphic behavior instance with the named hierarchy node.
	 * @param node name of the node to associate with
	 * @param behaviorClassName the behavior class to be associated with
	 * @param behaviorPoly the specific polymorphic behavior instance to associate with the node
	 * @return the depth of the named node
	 */
  public int associateBehaviorWithNode(String node,String behaviorClassName,SB_Behavior behaviorPoly,SB_Logger logger)
  	throws SB_Exception
  {
    SB_TypeNode typeNode = (SB_TypeNode)_nodeMap.get(node);
    if (typeNode == null)
    	throw new SB_Exception("Behavior referenced unknown descriptor " + typeNode);

    typeNode.associateBehavior(behaviorClassName,behaviorPoly,logger);

    return typeNode.GetDepth();
  }

	/**
	 * Finds the node with the specified name.
	 * @param nodeName the name of the node to look for
	 * @return the named node
	 * @throws WG_Exception if no such node exists
	 */
  public SB_TypeNode FindNode(String name)
		throws SB_Exception
	{
    // find the named type node
    SB_TypeNode node = (SB_TypeNode)_nodeMap.get(name);
    if (node == null)
      throw new SB_Exception("Unknown behavior " + name + " referenced.");

    return node;
  }


	/**
	 * Computes an ordered list of possible behavior instances that match the entity's
	 * current state and the specified behavior class.  This list is ordered from most
	 * to least specific -- that is, from the deepest node in the type hierarchy to the
	 * shallowest.
	 * @param state the entity's current state w.r.t. this hierarchy
	 * @param behaviorClassName the behavior class for which to compute instances
	 * @return the ordered list of possible behaviors (empty if none exist)
	 */
  public ArrayList computePossibleBehaviorInstances(String state,String behaviorClassName)
  	throws SB_Exception
  {
    // start with the deepest node that matches the specified state
    SB_TypeNode node = FindNode(state);
    if (node == null)
      throw new SB_Exception("Entity has an unknown descriptor " + state + ".");

    ArrayList instances = new ArrayList();

    // collect the instances from each node and then move up the tree
    do
    {
      ArrayList assoc = node.getBehaviorInstances(behaviorClassName);
      if (assoc != null)
      {
        // this node has behavior instances for this behavior class
        for(int i = 0; i < assoc.size(); i++)
        {
          instances.add(assoc.get(i));
        }
      }
      node = node.GetParent();
    } while (node != null);

    return  instances;
  }

	/**
	 * Removes all associations with the named behavior from the hierarchy.
	 * @param behaviorClassName the behavior class to be removed
	 */
	public void removeBehavior(String behaviorClassName)
	{
		_root.removeBehaviorFromSubtree(behaviorClassName);
	}

  public String toString(){
    String out;
    out = new String(".TypeHierarchy:\r\n" + _root.toString());
    return out;
  }
}