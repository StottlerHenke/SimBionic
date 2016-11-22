package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;


/**
 * An instance of this class represents a single class of behaviors,
 * which may have in turn many polymorphic implementations that are
 * tied to various hierarchy indices.
 */
public class SB_BehaviorClass extends SB_BehaviorElement 
{
	public final static int kMODE_MULTITICK		= 0x0;
	public final static int kMODE_NONINTERRUPTIBLE = 0x1;
	public final static int kMODE_RUNUNTIL			= 0x2;
	public final static int kMODE_ONETICK			= 0x4;

  protected SB_ParamList _params;
  protected ArrayList _polys = new ArrayList();
  protected int _execMode;

  /**
   * Construct
   * @param id unique id of this behavior
   */
  public SB_BehaviorClass(int id) 
	{
    super(null);
    _id = id;
  }
  /**
   * @return the list of parameters for this behavior
   */
  SB_ParamList GetParams() {
	  //_params.print();
	  return _params; }

  /**
   * @return true if this class of behaviors is interruptible, false otherwise
   */
  boolean IsInterruptible() { return (_execMode & kMODE_NONINTERRUPTIBLE) == 0; }

  /**
   * @return the execution mode for this class of behaviors
   */
  int GetExecMode() { return _execMode & ~kMODE_NONINTERRUPTIBLE; }

  public void initialize(SB_SingletonBook book)
		throws SB_Exception
	{
  	for (int i = 0; i < _polys.size(); i++)
  	{
  		SB_Behavior polyIt = (SB_Behavior)_polys.get(i);
  		polyIt.initialize(book);
  	}
  }
  
  /**
   * Replaces all existing instances of the specified method with this new
   * version.
   * @param newMethod the new instance of the method with which to replace the old version
   */
  public void replaceMethod(SB_Method newMethod)
  {
     for (int i=0; i<_polys.size(); ++i)
     {
   		SB_Behavior polyIt = (SB_Behavior)_polys.get(i);
  		polyIt.replaceMethod(newMethod);
     }
  }

	/**
	 * Loads an entire class of behaviors from a data model
	 * @param behaviorModel the behavior model.
	 */
  public void load(Behavior behaviorModel, SB_SingletonBook book) throws SB_FileException 
  {
  	// read the behavior name
    _name = behaviorModel.getName();
	
    if(book.getBehaviorRegistry().isBehaviorDefined(_name))
        book.getBehaviorRegistry().removeBehavior(_name);
    
    
	  if(SIM_Constants.DEBUG_INFO_ON)
	    book.getLogger().log(".Loading behavior " + _name + " -------------------->",SB_Logger.INIT);
	
	  // read the run-to-completion flag
	  int exectionMode = behaviorModel.getExec();
	  int interruptible = behaviorModel.isInterrupt() ? 0 : 1;
	  
	  _execMode = exectionMode | interruptible;
	
	  // read parameter list
	  _params = new SB_ParamList();
	  for (Parameter param : behaviorModel.getParameters()) {
	     _params.add(new SB_Parameter(param.getName(), param.getType()));
	  }
	
	  // read polys
	  for (Poly polyModel : behaviorModel.getPolys()) 
	  {
	    SB_Behavior poly = new SB_Behavior(this);
	    poly.setName(_name); // simplifies debugging
	    poly.load(polyModel, book);
	
	    _polys.add(poly);
	  }
	}

}