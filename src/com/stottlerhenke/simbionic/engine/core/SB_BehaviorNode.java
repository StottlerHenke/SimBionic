
package com.stottlerhenke.simbionic.engine.core;

import java.util.ArrayList;
import java.util.List;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/** 
 * Class for all nodes that invoke a behavior when applied.
 */
public class SB_BehaviorNode extends SB_Node {
  // the name of the behavior node.	
  protected String _behaviorName;
  // String expression for this behavior node.
  protected String _expr;
  // Javascript function expression for this behavior node.
  protected String _jsFunction;

  public SB_BehaviorNode(SB_BehaviorElement owner,int nodeID) {
    super(owner, nodeID);
    _behaviorName = "";
    _expr = "";
    _jsFunction = "";
  }

  /**
   * Reads a single behavior node from the given ActionNode.
   */
  public void load(ActionNode node,SB_SingletonBook book, List<Integer> transitionIds)
		throws SB_FileException 
	{
    super.load(node,book, transitionIds);
    // get behavior name from expression
    _expr = node.getExpr();
    _behaviorName = getBehaviorName(_expr);
    
    // read always and catch flags.
	_alwaysNodeFlag = node.isAlways();
	_catchNodeFlag = node.isCatch();

  }

  /**
   * Get the behavior name from the given expression.
   * e.g. if expression is 'run()', then 'run' is returned.
   * @param expr String expression 
   * @return The name of the behavior.
   */
  public static String getBehaviorName(String expr) {
	 if (expr == null || expr.isEmpty()) {
		 return "";
	 }
	 
     int index = expr.indexOf('(');
     if (index == -1)
         index = expr.length();
     
     return expr.substring(0, index);
  }

	public void initialize(ArrayList transitions,SB_SingletonBook book) 
		throws SB_Exception
	{
    SB_BehaviorClass behavior = book.getBehaviorRegistry().getBehaviorClass(_behaviorName);

    super.initialize(transitions,book);
    // initialize parameter bindings.
    _bindings.initParamBindings( behavior.GetParams() );
	}

  public ENodeType GetNodeType() {
    return ENodeType.kBehaviorNode;
  }

  public String GetLogName() {
	  return _behaviorName;
  }

  public void Execute(SB_Entity entity, SB_ExecutionFrame contextFrame, SB_SingletonBook book)  throws SB_Exception
  {
     if (!contextFrame.HasNodeBeenInvoked()) {
        generateAndExecuteJsFunction(entity, contextFrame, book);
     }
  }
  
  private void generateAndExecuteJsFunction(SB_Entity entity, SB_ExecutionFrame contextFrame, SB_SingletonBook book) 
  throws SB_Exception {
     // generate javascript function to be called
     // when this behavior node is executed.
     // e.g. function behaviorName(param1, param2) {
     //      // body of the js function
     // }
     SB_BehaviorClass behavior = book.getBehaviorRegistry().getBehaviorClass(_behaviorName);
     int size = behavior.GetParams().GetNumParams();
     if (_jsFunction.isEmpty()) {

        _jsFunction = _behaviorName + "(";
        for (int i = 0; i < size; i++) {
           _jsFunction += behavior.GetParams().GetParam(i).GetName();
           if (i < size - 1) {
              _jsFunction += ", ";
           }
        }

        _jsFunction += ")";
     }
     
     StringBuilder builder = new StringBuilder();
     builder.append("function " + _jsFunction + " {\n");

     // the body of javascript function
     // create an array to pass all the parameters to runBehavior method.
     builder.append(" var params = new Array(); \n");
     for (int i = 0; i < size; i++) {
    	 String param = behavior.GetParams().GetParam(i).GetName();
    	 builder.append("  params[" + i + "] = " + param + "; \n");
     }

     builder.append("  _behaviorNode.runBehavior(_behaviorEntity, _behaviorContextFrame, _behaviorBook, params); \n" );
     builder.append("}"); // end of javaScript function

     // put the js function in the javaScript engine to be executed.
     SB_JavaScriptEngine jsEngine = book.getJavaScriptEngine();
     jsEngine.evaluate(builder.toString(), contextFrame);

     // store required variables in the javascript engine.
     jsEngine.put(contextFrame, "_behaviorNode", this); 
     jsEngine.put(contextFrame, "_behaviorEntity", entity);
     jsEngine.put(contextFrame, "_behaviorContextFrame", contextFrame); 
     jsEngine.put(contextFrame, "_behaviorBook", book);

     // bind variables
     
     _bindings.ApplyForVariables(entity, contextFrame);
     _bindings.ApplyForInvocation(entity, contextFrame, contextFrame);
     //_bindings.print();

     // finally evaluate the expression.  This will execute the _jsFunction that was created and put 
     // in the js engine.
     jsEngine.evaluate(_expr, contextFrame);
     
  }
  

  /**
   * This method is called by the javaScript engine.  See generateAndExecuteJsFunction method.
   * @param entity The current behavior entity
   * @param contextFrame The behavior context frame to execute this behavior.
   * @param book The singleton book 
   * @param params Array of behavior parameters
   * @throws SB_Exception Thrown if any error occurs
   */
  public void runBehavior(SB_Entity entity, SB_ExecutionFrame contextFrame, SB_SingletonBook book, Object[] params) throws SB_Exception {
       // only execute the node when first entering it (ie not on every
       // clock tick afterward while remaining in the node)
       if (!contextFrame.HasNodeBeenInvoked()) {
         SB_EntityData state = entity.GetState();
         
         // keep track of whether this node has been applied
         contextFrame.SetNodeBeenInvoked(true);
         
       
         // apply the variable bindings for the node
         _bindings.ApplyForVariables(entity, contextFrame);

         try {
           // put a new frame on the stack
           SB_ExecutionFrame newFrame = state.InvokeBehavior(_behaviorName,
               contextFrame, book);

           // create bindings for each parameter for the behavior that's called.
           SB_BehaviorClass behavior = book.getBehaviorRegistry().getBehaviorClass(_behaviorName);
           for (int index = 0; index < params.length; index++) {
        	   // get the parameter name from the behavior
              String paramName = behavior.GetParams().GetParam(index).GetName();
              // create SB_Variable for each behavior's parameter.
              SB_Variable variable = new SB_VarClass();
              variable.setValue(params[index]);
              // add the variable to the new frame.
              newFrame._variables.AddVariable(paramName, variable);
           }
           if (!contextFrame.InvokedInterrupt())
           {
              // only clear the invoked-behavior-finished flag
              // for non-interrupt behavior invocations (otherwise
              // the interrupt behavior might overwrite the status
              // of the already-invoked normal behavior)
              contextFrame.SetInvokedDone(false);
           }

           // invoking the behavior shouldn't waste a clock tick
           state.SetDoAnotherTick(true);
         } catch(SB_Exception e){
            System.err.println(e.getMessage());
         }
       }
       
     }
  
}