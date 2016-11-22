package com.stottlerhenke.simbionic.engine;

import java.util.ArrayList;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.EIdType;
import com.stottlerhenke.simbionic.common.SB_ID;
import com.stottlerhenke.simbionic.engine.comm.SB_CommMsg;
import com.stottlerhenke.simbionic.engine.core.SB_DelayedAction;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;
import com.stottlerhenke.simbionic.engine.manager.SB_EntityManager;
import com.stottlerhenke.simbionic.engine.parser.SB_VarClass;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * ActionPredicateAPI surfaces the built-in actions and predicates for access in JavaScript.
 * For example, the predicate GetEntityName() is accessed in JavaScript as follows:
 * <pre>
 * function GetEntityName() {
 *    return com.stottlerhenke.simbionic.engine
 *              .ActionPredicateAPI.getInstance().GetEntityName();
 * }
 * </pre>
 * 
 *
 */
public class ActionPredicateAPI {
	
	  // actions
    public static final int ACTION_None = -1;
    /*
	public static final int ACTION_CreateBBoard = 2;
	public static final int ACTION_DestroyBBoard = 4; */
	public static final int ACTION_DestroyEntity = 31;
	//public static final int ACTION_DestroyGroup = 7;
	//public static final int ACTION_JoinGroup = 5;
	//public static final int ACTION_NextMsg = 1;
	//public static final int ACTION_PostBBoard = 3;
    public static final int ACTION_PushBehavior = 35;
	//public static final int ACTION_QuitGroup = 6;
	public static final int ACTION_Resume = 33;
	public static final int ACTION_Rethrow = 34;
	public static final int ACTION_Retry = 32;
	//public static final int ACTION_SendMsg = 0;
	public static final int ACTION_SetBehavior = 28;
	public static final int ACTION_SetEntityGlobal = 27;
	public static final int ACTION_SetUpdateFrequency = 29;
	public static final int ACTION_SetUpdatePriority = 30;

// predicates
	public static final int PRED_CreateEntity = 23;
	public static final int PRED_GetEntityID = 10;
	/*public static final int PRED_GetMsgData = 4;
	public static final int PRED_GetMsgSender = 2;
	public static final int PRED_GetMsgType = 3; */
	public static final int PRED_If = 9;
	//public static final int PRED_IsBBoard = 6;
	public static final int PRED_IsDone = 0;
	/*public static final int PRED_IsMsg = 1;
	public static final int PRED_NumMembers = 8;
	public static final int PRED_ReadBBoard = 5;*/
	public static final int PRED_IsNull = 25;
	public static final int PRED_IsEntityFinished = 26;
	public static final int PRED_GetEntityName = 27;
	
	/** the singleton ActionPredicateAPI */
	private static ActionPredicateAPI _instance;
	
	/** the book of singletons used by the engine */
	private SB_SingletonBook _book;
	
	/**
	 * Hidden constructor to create the singleton with the specified book.
	 * 
	 * @param book the book of singletons used by the engine
	 */
	private ActionPredicateAPI (SB_SingletonBook book)
	{
		_book = book;
	}
	
	/**
	 * Reset the singleton instance.
	 */
	public static void resetInstance() {
		_instance = null;
	}

	/**
	 * Initialize the singleton with the specified book.
	 * 
	 * @param book the book of singletons used by the engine
	 * 
	 * @return true if successful
	 */
	public static boolean initInstance(SB_SingletonBook book) {
		if (_instance == null) {
			_instance = new ActionPredicateAPI(book);
			return true;
		}
		return false;	// already initialized
	}
	
	/**
	 * Get the singleton ActionPredicateAPI.
	 * 
	 * @return the singleton
	 */
	public static ActionPredicateAPI getInstance() {
		return _instance;
	}

	/**
	 * Create an entity with the specified entity name, behavior, update frequency,
	 * and update priority.
	 * 
	 * @param entityName the entity name
	 * @param behavior the initial behavior
	 * @param updateFrequency the update frequency
	 * @param updatePriority the update priority
	 * @return the entity id
	 */
	public int CreateEntity(String entityName, String behavior, ArrayList params, int updateFrequency,
			int updatePriority) throws SB_Exception {	 

	    SB_ID newId  = _book.getEntityManager().GetIdDispenser().ClaimId(EIdType.kEntityId);
	    
	    ArrayList variables = new ArrayList();
	    variables.add(entityName);
	    variables.add(behavior);
	    
	    if (params != null) {
	    	ArrayList list = SB_SimInterface.ConvertParams( params, _book );
	    	variables.add(list);	    	
	    }
	    else {
	    	variables.add(new ArrayList());	// no params
	    }
	    
	    variables.add(updateFrequency);
	    variables.add(updatePriority);
	    
	    SB_VarClass entityVar = new SB_VarClass();
	    entityVar.setValue(newId);
	    variables.add(entityVar);
	    
	    // create "delayed" action and execute immediately
	    SB_DelayedAction action = new SB_DelayedAction(PRED_CreateEntity, variables, true);
        action.DoDelayedAction( _book.getEntityManager() );
        
	    return (int)newId._id; 
	}
	
	/**
	 * Destroy an entity with the specified entity id.
	 * 
	 * @param entityId the entity id
	 */
	public void DestroyEntity(long entityId) throws SB_Exception {
		ArrayList variables = new ArrayList();
		variables.add(entityId);
		
	    // create "delayed" action and execute immediately
		SB_DelayedAction action = new SB_DelayedAction(ACTION_DestroyEntity, variables, false);
		action.DoDelayedAction( _book.getEntityManager() );   
	}
	
	/**
	 * Get the SimBionic name of the current entity.
	 * 
	 * @return the entity name
	 */
	public String GetEntityName() throws SB_Exception {
	    SB_Entity entity = _book.getEntityManager().GetEntity(_book.getEntityManager().GetCurrentEntity());
		
	    String name = "";
	    if( entity != null )
	        name = entity.GetName();
	    
		return name;
	}
	
	protected SB_Entity GetCurrentEntity() {
		SB_EntityManager em = _book.getEntityManager();
		return em.GetEntity(em.GetCurrentEntity());
	}
	
    /**
     * Invokes the specified behavior
     * @param entityId : entity [in]
     * @param behaviorId : string [in]
     * @param params : array [in]
     */
    public void PushBehavior(long entityId, String behavior, ArrayList params) throws SB_Exception {
         ArrayList variables = new ArrayList();
         variables.add( entityId );
         variables.add( behavior );
    
         //An SB_VarArray filled with SB_Param objects
         ArrayList list = SB_SimInterface.ConvertParams( params, _book );
         variables.add(list);
         
         // create "delayed" action and execute immediately
         SB_DelayedAction action = new SB_DelayedAction(ACTION_PushBehavior, variables, false);
         action.DoDelayedAction( _book.getEntityManager() );
    } 

  
	/**
	 * The Resume action causes execution to resume at the node where
	 * the exception was thrown without attempting to retry execution
	 */
	public void Resume() throws SB_Exception {
		GetCurrentEntity().GetState().GetExecStack().Resume();
	}

	/**
	 * The Rethrow action rethrows the current exception down the stack
	 * to the current behavior's invoking behavior and then terminates
	 * the current behavior just as if a final node had been
	 */
	public void Rethrow() throws SB_Exception {
		GetCurrentEntity().GetState().GetExecStack().Rethrow();
	}

	/**
	 * The Retry action causes behavior execution to jump to the node
	 * in this behavior where the exception was thrown, executing that
	 * node exactly as if it had just become the current node in normal
	 * fashion.  That is, the bindings for that node will be evaluated,
	 */
	public void Retry() throws SB_Exception {
		GetCurrentEntity().GetState().GetExecStack().Retry();
	}

	
	/**
	 * @param entityId : entity [in]
	 * @param behaviorId : string [in]
	 * @param params : array [in]
	 */
	public void SetBehavior(long entityId, String behavior, ArrayList params) throws SB_Exception {
		 ArrayList variables = new ArrayList();
		 variables.add( entityId );
		 variables.add( behavior );
		 
		 //An SB_VarArray filled with SB_Param objects
		 ArrayList list = SB_SimInterface.ConvertParams( params, _book );
		 variables.add(list);
		 
		 // create "delayed" action and execute immediately
		 SB_DelayedAction action = new SB_DelayedAction(ACTION_SetBehavior, variables, false);
         action.DoDelayedAction( _book.getEntityManager() );
	}

	// XXX: problem with value
	// XXX: problem handling array value
	// XXX: untested
	/**
	 * @param entityId : entity [in]
	 * @param varName : string [in]
	 * @param value : any [in]
	 */
	public void SetEntityGlobal(long entityId, String varName, Object value) throws SB_Exception {
		ArrayList variables = new ArrayList();
		variables.add( entityId );
		variables.add( varName );

      variables.add(SB_SimInterface.ConvertParamAuto(value, _book));

    // create "delayed" action and execute immediately
    SB_DelayedAction action = new SB_DelayedAction(ACTION_SetEntityGlobal, variables, false);
    action.DoDelayedAction( _book.getEntityManager() );
	}

	// XXX: untested
	/**
	 * @param newFreq : integer [in]
	 * @param entityId : entity [in]
	 */
	public void SetUpdateFrequency(int newFreq, long entityId) throws SB_Exception {
		ArrayList variables = new ArrayList();
		variables.add(newFreq);
		variables.add(entityId);
		
	    // create "delayed" action and execute immediately
		SB_DelayedAction action = new SB_DelayedAction(ACTION_SetUpdateFrequency, variables, false);
	    action.DoDelayedAction( _book.getEntityManager() );
	}

	// XXX: untested
	/**
	 * @param newPriority : integer [in]
	 * @param entityId : entity [in]
	 */
	public void SetUpdatePriority(int newPriority, long entityId) throws SB_Exception {
		ArrayList variables = new ArrayList();
		variables.add(newPriority);
		variables.add(entityId);
		
	    // create "delayed" action and execute immediately
		SB_DelayedAction action = new SB_DelayedAction(ACTION_SetUpdatePriority, variables, false);
	    action.DoDelayedAction( _book.getEntityManager() );
	}

	
	/**
	 * Returns the SimBionic ID of the current
	 * @return entity
	 */
	public int GetEntityID() {
		long entityId = SB_SimInterface.TranslateId(_book.getEntityManager().GetCurrentEntity());
		return (int)entityId;
	}
	
	/**
	 * If condition is true, returns parameter value1, otherwise
	 * returns parameter
	 * @param condition : boolean [in]
	 * @param value1 : any [in]
	 * @param value2 : any [in]
	 * @return any
	 */
	public Object If(boolean condition, Object value1, Object value2) throws SB_Exception {
		Object retVal;
		
		if (condition)
			// return value1
			retVal = value1;
		else
			// return value2
			retVal = value2;
		
		return retVal;
	}


	/**
	 * Indicates whether the behavior invoked by the frame whose transitions are currently
     * being checked has completed.
	 * Return true if behavior is finished, false
	 * @return boolean
	 */
	public boolean IsDone() {
        SB_ExecutionFrame checkedFrame = GetCurrentEntity().GetState().GetExecStack().getCheckedFrame();
		return checkedFrame.IsInvokedDone();
	}

	
	
	/**
	 * Returns true if the specified entity's stack is empty, false
	 * otherwise.
	 * @param entity : entity [in]
	 * @return boolean
	 */
	public boolean IsEntityFinished(long entityId) throws SB_Exception {
		return _book.getEntityManager().IsEntityFinished(new SB_ID(entityId));
	}

	/* Messaging below this point */

	public void DestroyGroup(String name) {
		
		GetCurrentEntity().GetCommLink().DestroyGroup(name);
	}
	
	public void JoinGroup(String name) {
		
		GetCurrentEntity().GetCommLink().JoinGroup(name);
	}
	
	public void NextMsg() {
		
		GetCurrentEntity().GetCommLink().NextMsg();
	}

	public void QuitGroup(String name) {
		
		GetCurrentEntity().GetCommLink().QuitGroup(name);
	}
	
	public void SendGroupMsg(String group, Integer type, Object msg) throws SB_Exception {
		
		SB_Variable var = new SB_VarClass();
		var.setValue(msg);
		SB_CommMsg commMsg = new SB_CommMsg(GetCurrentEntity().GetId(), group, type, var);
		GetCurrentEntity().GetCommLink().Send(commMsg);
	}

	/**
	 * 2016-07-25 - jmmao
	 * <p>
	 * This method links the API's GetMsgData to the underlying
	 * SimBionic functionality.
	 * <p>
	 * Like other changes on 2016-07-25, this method exposes
	 * existing functionality for use through the coreActionsPredicates.
	 * */
	public SB_Variable GetMsgData() {
		return GetCurrentEntity().GetCommLink().CurrentMsg().GetData();
	}

	/**
	 * 2016-07-25 - jmmao
	 * <p>
	 * The phrase
	 * "Unique number codes that SimBionic's run-time engine dynamically
	 * assigns to the various entities" (SimBionic User Guide, 2016-07-01)
	 * seems to imply that SB_Entity objects are never directly exposed
	 * to the javascript layer; instead, the <tt>SB_ID</tt> are used.
	 * However, the fact that {@link #GetEntityID()} returns an <tt>int</tt>
	 * suggests that further conversion is necessary; this method
	 * uses the same conversion through <tt>SB_SimInterface.TranslateId</tt>
	 * (and cast from long to int).
	 * 
	 * */
	public int GetMsgSender() {
		long primID = SB_SimInterface.TranslateId(
				GetCurrentEntity().GetCommLink().CurrentMsg().GetSender());
		return (int) primID;
	}

	/**
	 * 2016-07-25 - jmmao
	 * <p>
	 * This method implements GetMsgType for the "current message"
	 * of the current entity by calling GetMsgType on the
	 * "current message" accessed trhough the comm link.
	 * */
	public int GetMsgType() {
		return GetCurrentEntity().GetCommLink().CurrentMsg().GetMsgType();
	}

	/**
	 * 2016-07-25 - jmmao
	 * <p>
	 * Renamed IsMsg functionality exposed to entities
	 * */
	public boolean HasMsg() {
		return GetCurrentEntity().GetCommLink().HasMsg();
	}

	/**
	 * 2016-07-27 - jmmao
	 * <p>
	 * Exposure of GetNumMembers to the javascript (and hence SimBionic) layer
	 * */
	public int NumMembers(String name) {
		return GetCurrentEntity().GetCommLink().GetNumMembers(name);
	}

	/* Blackboard methods below this point 2016-07-26 -jmm */

	/**
	 * 2016-07-26 -jmm
	 * This method is part of the (re)exposure of blackboard functionality.
	 * This method creates blackboard using the method outlined in
	 * {@link com.stottlerhenke.simbionic.api.SB_Engine#createBBoard(long, String)
	 * SB_Engine#createBBoard}: besides the user-provided name, a logger
	 * retrieved from a SB_SingletonBook is passed to CreateBBoard.
	 * as an argument
	 * */
	public void CreateBBoard(String name) {
		GetCurrentEntity().GetCommLink().CreateBBoard(name,
				_book.getLogger());
	}

	/**
	 * 2016-07-26 -jmm
	 * <p>
	 * This method is part of the (re)exposure of blackboard functionality.
	 * As described by its name, this method attempts to remove a blackboard
	 * with name <tt>name</tt> if it exists. If the specified blackboard
	 * does not exist, this function does nothing (see 
	 * {@link com.stottlerhenke.simbionic.engine.comm.SB_CommCenter
	 * #DestroyBlackboard(String) SB_CommCenter.DestroyBlackboard}).
	 */
	public void DestroyBBoard(String name) {
		GetCurrentEntity().GetCommLink().DestroyBBoard(name);
	}

	/**
	 * 2016-07-26 -jmm
	 * <p>
	 * This method is part of the (re)exposure of blackboard functionality.
	 * This method places information on a blackboard; blackboards
	 * appear to behave as key-value stores, so this method places
	 * <tt>value</tt> with key <tt>key</tt> on the board with name
	 * <tt>boardName</tt>
	 * It appears that PostBBoard does nothing if boardName
	 * does not correspond to an existing board (see
	 * {@link
	 * com.stottlerhenke.simbionic.engine.comm.SB_CommCenter
	 * #PostBlackboard(String, String, SB_Variable)
	 * SB_CommCenter.PostBlackboard}
	 * ).
	 * <p>
	 * The logic here is copied from SendGroupMsg; Java <tt>Object</tt>s
	 * are converted into <tt>SB_Variable</tt> instances before
	 * being passed to the appropriate methods in <tt>SB_CommLink</tt>.
	 * */
	public void PostBBoard(String boardName, String key, Object value)
			throws SB_Exception {
		SB_Variable var = new SB_VarClass();
		var.setValue(value);
		GetCurrentEntity().GetCommLink().PostBBoard(boardName, key, var);
	}

	/**
	 * 2016-07-26 -jmm
	 * <p>
	 * This method is part of the (re)exposure of blackboard functionality.
	 * This method returns true iff a blackboard with <tt>boardName</tt>
	 * exists and false otherwise.
	 * */
	public boolean IsBBoard(String boardName) {
		return GetCurrentEntity().GetCommLink().IsBBoard(boardName);
	}

	/**
	 * 2016-07-26 - jmmao
	 * <p>
	 * This method links the javascript API's ReadBBoard to the
	 * underlying SimBionic functionality.
	 * */
	public SB_Variable ReadBBoard(String boardName, String key) {
		return GetCurrentEntity().GetCommLink().ReadBBoard(boardName, key);
	}
}
