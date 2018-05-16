package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * Each instance of this class represents a single node in a type hierarchy.
 * It stores information about all behaviors that have one or more polymorphic
 * version indexed at that type.
 */
public class SB_TypeNode  
{
  protected int _index;
  protected int _depth;
  protected String _name;
  protected SB_TypeNode _parent;
  protected List<SB_TypeNode> _children = new ArrayList<>();
  private Map<String, List<SB_Behavior>> _behaviors = new HashMap<>();

  public SB_TypeNode(SB_TypeNode parent, int index) {
    _parent = parent;
    _index = index;
  }
  /**
   * @return the unique name of this hierarchy node
   */
  public String GetName() { return _name; }

  /**
   * @return the unique index of this node
   */
  public int GetIndex()  { return _index; }

  /**
   * @return the depth of this node in the hierarchy
   */
  public int GetDepth()  { return _depth; }

  /**
   * @return the unique parent of the node
   */
  public SB_TypeNode GetParent() { return _parent; }

	/**
	 * Recursively loads the hierarchy subtree rooted at this node.
	 * @param reader the stream reader from which to load the tree
	 * @param index the unique id number for this node
	 * @param nodeNames a map from node names to node pointers [being constructed]
	 */
  public void loadSubtree(Descriptor descriptorModel,SB_SingletonBook book,int index, HashMap nodeNames)
		throws SB_FileException
	{
     
    loadSubtree(descriptorModel,book,index,nodeNames,0);
  }
  
	/**
	 * Helper method for the recursive loading.
	 * @param reader the stream reader from which to load the tree
	 * @param index the unique id number for this node
	 * @param nodeNames a map from node names to node pointers [being constructed]
	 * @param depth the depth of this subtree in the hierarchy
	 */
  public void loadSubtree(Descriptor descriptorModel,SB_SingletonBook book,int index, HashMap nodeNames,int depth)
		throws SB_FileException
	{
	  _depth = depth;
	
	  // read node name
	  _name = descriptorModel.getName();
	  if(nodeNames == null) nodeNames = new HashMap();
	  nodeNames.put( _name, this);
	
	  // recursively read the subtree
	  for (Descriptor child : descriptorModel.getDescriptors()) {
	    SB_TypeNode node = new SB_TypeNode(this,index++);
	    node.loadSubtree(child,book,index,nodeNames,depth+1);
	    _children.add(node);
	  }
  }

    /**
     * Associates the given polymorphic behavior instance with this node.
     * 
     * @param behaviorClassName the behavior class to associate with
     * @param behaviorPoly the specific polymorphic behavior instance to
     *            associate with the node
     */
    public void associateBehavior(String behaviorClassName,
            SB_Behavior behaviorPoly, SB_Logger logger) {

        _behaviors.putIfAbsent(behaviorClassName, new ArrayList<>());
        List<SB_Behavior> assoc = _behaviors.get(behaviorClassName);

        if (SIM_Constants.DEBUG_INFO_ON)
            logger.log(".HierNode " + GetIndex() + " associated with behavior "
                    + behaviorClassName, SB_Logger.INIT);

        // add this polymorphic behavior instance to the list of instances
        // associated with this node
        assoc.add(behaviorPoly);
    }

    /**
     * Retrieves the set of polymorphic instances of the given behavior class
     * that are associated with this node.
     * <br>
     * Implicit assumption: all SB_Behavior instances added through {@link
     * #associateBehavior(String, SB_Behavior, SB_Logger) associateBehavior}
     * are polymorphic.
     * <br>
     * XXX: 2018-05 Returns null instead of an empty list if no behaviors are
     * present for {@code behaviorClassName}.
     * @param behaviorClassName the class whose instances should be retrieved
     * @return A list of all polymorphic SB_Behavior instances corresponding
     * to {@cod behaviorClassName}, or {@code null} if none are present for
     * {@code behaviorClassName}. 
     */
    public List<SB_Behavior> getBehaviorInstances(String behaviorClassName) {
        return _behaviors.get(behaviorClassName);
    }

	/**
	 * Removes all associations with the specified behavior from this node and its subtree.
	 * @param behaviorClassName the behavior class to remove
	 */
	public void removeBehaviorFromSubtree(String behaviorClassName)
	{
		// remove the behavior from this node
		_behaviors.remove(behaviorClassName);

		// remove the behavior from this node's children
		for (int i=0; i < _children.size(); ++i)
		{
			((SB_TypeNode)_children.get(i)).removeBehaviorFromSubtree(behaviorClassName);
		}
		
	}

	public String toString(){
    String out;
    out = new String( ".TypeNode #" + _index + " " + _name + ", "
                     + _children.size() + " children\r\n") ;
    for (int i=0;  i < _children.size(); i++){
      out = out + ((SB_TypeNode)_children.get(i)).toString();
    }
    return out;
  }
}