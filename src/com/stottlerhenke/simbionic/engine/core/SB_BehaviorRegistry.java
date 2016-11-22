
package com.stottlerhenke.simbionic.engine.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;


/**
 * An instance of this class is responsible for loading all behaviors at system
 * startup and providing global access to them by name.  It also deallocates them
 * at system shutdown.  Note that as part of this duty, it is also responsible
 * for keeping track of all actions, predicates, and hierarchies.
 */
public class SB_BehaviorRegistry
{
  private int _nextBehaviorId;
  protected int _version;
  protected String _filename;
  protected HashMap _behaviorClasses;
  protected ArrayList _newBehaviorClasses;
  protected ArrayList _hierarchies;
  protected HashMap _actions;
  protected HashMap _predicates;
  protected SB_Logger _logger;

  public SB_BehaviorRegistry(SB_Logger logger) {
    _logger = logger;
    setup();
  }


// Initialization methods

	/**
	 * Adds the given descriptor hierarchy to the registry.
	 * @param hier the hierarchy to add
	 */
	public void addHierarchy(SB_TypeHierarchy hier)
	{
		_hierarchies.add(hier);
	}

	/**
	 * Adds the given action to the registry.
	 * @param action the action to add
	 */
	public void addAction(SB_Action action)
	{
	    Object oldValue = _actions.put(action.GetName(),action);
	    if (oldValue != null)
	    {
	        // updating an existing, which means an AP module was loaded
	        updateMethod(action);
	    }
	}

	/**
	 * Adds the given predicate to the registry.
	 * @param pred the predicate to add
	 */
	public void addPredicate(SB_Function pred)
	{
	    
		Object oldValue = _predicates.put(pred.GetName(),pred);
	    if (oldValue != null)
	    {
	        // updating an existing, which means an AP module was loaded
	        updateMethod(pred);
	    }
	}
	
	/**
	 * Replaces all existing instances of the specified action or
	 * predicate in currently loaded behaviors.  Typically used when
	 * a new AP module is loaded.
	 * @param newMethod the method with which to replace the existing method instances
	 */
	public void updateMethod(SB_Method newMethod)
	{
        if(SIM_Constants.DEBUG_INFO_ON)
            _logger.log("Refreshing method " + newMethod,SB_Logger.INIT);
	    Iterator behavior = _behaviorClasses.keySet().iterator();
	    while (behavior.hasNext())
	    {
	      ((SB_BehaviorClass)_behaviorClasses.get(behavior.next())).replaceMethod(newMethod);
	  	}
	}

	/**
	 * @return a unique behavior id
	 */
	public int requestBehaviorId()
	{
		return _nextBehaviorId++;
	}

	/**
	 * Completes the initialization of newly-loaded behaviors.
	 * Must be called after a package specification is loaded.
	 */
  public void initializeNewPackage(SB_SingletonBook book) throws SB_Exception
  {
    // initialize all new behaviors
    for (int i = 0; i < _newBehaviorClasses.size(); i++)
    {
  		// expressions aren't parsed until this point
      ((SB_BehaviorClass)_newBehaviorClasses.get(i)).initialize(book);
  	}
  	_newBehaviorClasses.clear();
  }

	/**
	 * Completes the initialization of all registry data structures.
	 * Must be called after a project specification is loaded.
	 */
	public void initializeNewProject(SB_SingletonBook book) throws Exception
	{
		initializeNewPackage(book);
	}


	/**
	 * Adds the given uninitialized behavior to the registry. Note that
	 * initializeNewBehaviors() must be called before the added behavior
	 * can be invoked.
	 * @param behavior the behavior to add
	 */
	public void addBehavior(SB_BehaviorClass behavior)
	{
		_newBehaviorClasses.add(behavior);

		// need to store it in the hashmap now even though it's not invokable yet
		// so that behavior nodes can reference it during initialization
		_behaviorClasses.put( behavior.getName(), behavior );
	}

	/**
	 * Associates the given polymorphic behavior instance with the specified hierarchy node.
	 * @param whichHier the hierarchy in which the desired node resides
	 *			(hierarchies are numbered in the order that they appear in the sim file)
	 * @param nodeName the name of the node to associate with
	 * @param behaviorClassName the behavior class to associate with the node
	 * @param behaviorPoly the specific polymorphic behavior instance to associate with the node
	 * @return the depth of the given node in the specified hierarchy
	 */
	public int indexBehavior(int whichHier,String nodeName,String behaviorClassName,SB_Behavior behaviorPoly)
		throws SB_Exception
	{
    return ( (SB_TypeHierarchy) _hierarchies.get(whichHier)).associateBehaviorWithNode(nodeName,behaviorClassName,behaviorPoly,_logger);
  }


// Getters/setters

	/**
   * @return the number of type hierarchies
	 */
	public int getNumHierarchies()  { return _hierarchies.size(); }

	public SB_Action getAction(String actionName) throws SB_Exception{
    SB_Method method = (SB_Method)_actions.get(actionName);
    if(method == null)
      throw new SB_Exception("Unknown action " + actionName + ".");

    return (SB_Action)method;
  }

  public SB_Function getFunction(String funName) throws SB_Exception{
    SB_Method method = (SB_Method)_predicates.get(funName);
    if(method == null)
      throw new SB_Exception("Unknown function '"+ funName + "'");
    return (SB_Function)method;
  }

	/**
	 * @param behaviorName the name of the behavior class to be retrieved
	 * @return the named behavior
	 */
  public SB_BehaviorClass getBehaviorClass(String behaviorName) throws SB_Exception
	{
  	SB_BehaviorClass behavior = (SB_BehaviorClass)_behaviorClasses.get(behaviorName);
  	if(behavior == null)
      throw new SB_Exception("Unknown behavior " + behaviorName + ".");
    return behavior;
  }

  public String getHierarchyName(int hier){
    return ((SB_TypeHierarchy)_hierarchies.get(hier)).GetName();
  }

  public String getHierarchyDefault(int hier){
    return ((SB_TypeHierarchy)_hierarchies.get(hier)).GetDefault();
  }


  //Runtime Methods

  /**
   * Retrieves the polymorphic version of the specified behavior that
	 * matches the entity's current state.
	 * @param behaviorName the unique behavior name
	 * @param entity the entity whose state should be examined
	 * @return the correct version of the behavior
	 * @throws WG_Exception if a valid polymorphism of the behavior can't be found
   */
  public SB_Behavior getBehavior(String behaviorName,SB_Entity entity)
		throws SB_Exception
	{
  	// verify that the behavior exists
  	getBehaviorClass(behaviorName);

    // recursively check the hierarchies to find a behavior instance that
    // matches the entity's current state
    ArrayList validInstances = resolveBehavior(behaviorName, entity, 0 );
    if (validInstances.isEmpty())
      throw new SB_Exception("Unable to find a behavior " + behaviorName + " matching the entity's current state.");

    // if multiple behavior instances are possible, pick the deepest (most specific) one
    int behIt = 0;
    SB_Behavior deepest = (SB_Behavior)validInstances.get(behIt);
    ++behIt;
    for (; behIt < validInstances.size(); ++behIt){
     ArrayList depths = ((SB_Behavior)validInstances.get(behIt)).GetHierarchyDepths();
       for (int hier=0; hier < getNumHierarchies(); ++hier){
         if (((Integer)depths.get(hier)).intValue() < ((Integer)deepest.GetHierarchyDepths().get(hier)).intValue()){
           // no, this instance is definitely not deeper
           break;
         }
         else if (((Integer)depths.get(hier)).intValue() > ((Integer)deepest.GetHierarchyDepths().get(hier)).intValue())
         {
           // yes, this instance is definitely deeper, no need to look further
           deepest = (SB_Behavior)validInstances.get(behIt);
           break;
         }
         // otherwise, not sure yet, so check the next hierarchy depth...
       }
     }
     return deepest;
  }

	/**
   * Recursive method that retrieves the appropriate polymorphic versions
	 * of the specified behavior for the entity's current state.
	 * @param behaviorClassName the name of the behavior class to look up
	 * @param entity the entity whose state should be examined
	 * @param hier index of the hierarchy being processed (aka recursion depth)
	 * @return a vector containing all polymorphic behavior instances that have been
	 *			resolved for hierarchies with index>hier
   */
  public ArrayList resolveBehavior(String behaviorClassName,SB_Entity entity, int hier)
		throws SB_Exception
	{
    if (hier == getNumHierarchies())
      // base case of recursion
      return null;

    // get the entity's state with respect to this hierarchy
    SB_Variable var = entity.GetState().GetGlobal(((SB_TypeHierarchy)_hierarchies.get(hier)).GetName());
    String state = var.getValue().toString();

    // get the hierarchy node matching the entity's current state
    SB_TypeHierarchy hierarchy = (SB_TypeHierarchy)_hierarchies.get(hier);

    // compute the set of consistent behavior instances for all hierarchies *after* this one
    ArrayList consistentPolys = resolveBehavior(behaviorClassName,entity,hier+1);

    // get the ordered list of possible behavior instances for this hierarchy
    ArrayList localPolys = hierarchy.computePossibleBehaviorInstances(state,behaviorClassName);

    // reconcile the set of possible instances from this hierarchy with those of subsequent hierarchies
    ArrayList finalPolys;
    if (consistentPolys == null)
    {
      // this is the last hierarchy, so it's automatically consistent
      finalPolys = new ArrayList(localPolys);
    }
    else
    {
      // construct the set of behavior instances available at this node that are consistent
      // with those from deeper hierarchies
      finalPolys = new ArrayList();
      for (int i = 0;  i < localPolys.size(); i++)
      {
        // is this behavior instance consistent with lower hierarchies?
        for (int j = 0; j < consistentPolys.size(); j++)
        {
          if (localPolys.get(i) == consistentPolys.get(j))
          {
            finalPolys.add(localPolys.get(i));
            break;
          }
        }
      }
    }

    if (finalPolys.isEmpty())
      throw new SB_Exception("Can't match behavior " + behaviorClassName
      											+ " to current entity descriptor " + state);
    return finalPolys;
  }

  /**
   * Determine if a behavior with the given name exists
   * @param behaviorName
   * @return true if the behavior is defined, false otherwise
   */
  public boolean isBehaviorDefined(String behaviorName)
  {
      return _behaviorClasses.containsKey(behaviorName);
  }
  
	/**
	 * Removes the named behavior from the registry and destroys it.
	 * @param behaviorName the behavior to remove
	 * @throws WG_Exception if the behavior does not exist
	 */
	public void removeBehavior(String behaviorName)
	{
        if(SIM_Constants.DEBUG_INFO_ON)
            _logger.log("+Unloading behavior " + behaviorName,SB_Logger.INIT);

		for (int i=0; i<_hierarchies.size(); ++i)
		{
			((SB_TypeHierarchy)_hierarchies.get(i)).removeBehavior(behaviorName);
		}
		_behaviorClasses.remove(behaviorName);
	}

	/**
	 * Removes all currently-loaded behaviors and destroys them.
	 */
	public void removeAllBehaviors()
	{
        if(SIM_Constants.DEBUG_INFO_ON)
            _logger.log("+Unloading all behaviors",SB_Logger.INIT);

		Iterator behIt = _behaviorClasses.keySet().iterator();
		while (behIt.hasNext())
		{
			String behaviorName = (String)behIt.next();
			for (int i=0; i<_hierarchies.size(); ++i)
			{
				((SB_TypeHierarchy)_hierarchies.get(i)).removeBehavior(behaviorName);
			}
            if(SIM_Constants.DEBUG_INFO_ON)
                _logger.log("+" + behaviorName,SB_Logger.INIT);
		}
		_behaviorClasses.clear();
	}

	public void shutdown()
	{
    setup();
  }

  public void setup()
  {
    _behaviorClasses = new HashMap();
    _newBehaviorClasses = new ArrayList();
    _hierarchies = new ArrayList();
    _actions = new HashMap();
    _predicates = new HashMap();
  }

  
}
