package com.stottlerhenke.dynamicscripting;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A singleton interface into an implemenation of dynamic scripting
 *
 */
public class DynamicScriptingWrapper
{
	static protected DynamicScriptingWrapper _instance;
	
	/** Logger to use for this class*/
	protected Logger _logger;
	
	/** A list of the choice points */
	ChoicePointTable choicePoints = new ChoicePointTable();
	
	/**
	 * @return the DS singleton
	 */
	static public DynamicScriptingWrapper getInstance()
	{
		if(_instance == null)
			_instance = new DynamicScriptingWrapper();
		
		return _instance;
	}
	
	public DynamicScriptingWrapper()
	{
	}
	
	
	/**
	 * Clear any existing unrewarded selections
	 *
	 */
	public void clear()
	{
		choicePoints.clearSelections();
	}
	
	/**
	 * Set the logger to use. If null, logging will be performed to the
	 * console.
	 * 
	 * @param logger
	 */
	public void setLogger(Logger logger)
	{
		_logger = logger;
	}
	
	/**
	 * Log the given msg
	 * @param msg
	 */
	public void log(String msg)
	{
		if(_logger != null)
			_logger.log(Level.INFO, msg);
	}
	
	/**
	 * Log the given msg
	 * @param msg
	 */
	public void log(Level level, String msg)
	{
		if(_logger != null)
			_logger.log(level, msg);
	}
	
	/**
	 * Log the given exception
	 * @param msg
	 */
	public void log(Exception ex)
	{
		if(_logger != null)
			_logger.log(Level.SEVERE, ex.toString());
	
		ex.printStackTrace();
	}
	
	
	/**
	 * Add a state with an initial set of values
	 * @param state
	 * @param values Double objects
	 */
	public void addChoicePoint(String choicePoint, ArrayList<DsAction> values, int scriptSize, int minActionValue, int maxActionValue)
	{
		choicePoints.addChoicePoint(choicePoint, values, scriptSize, minActionValue, maxActionValue);
	}
	
	/**
	 *  Add a choice point loaded from a file
	 */
	public void addChoicePoint(ChoicePoint cp)
	{
		choicePoints.addChoicePoint(cp);
	}
	
	/**
	 * Lookup the values in the state table and return the order in which
	 * to try the given transitions.
	 * 
	 * @return the indexes to try, in order
	 */
	public int[] orderActions(String choicePoint, String agentName, int numberTransitions)
	{	
		ChoicePoint c = getChoicePoint(choicePoint);
		
		if(c == null)
		{
			_logger.log(Level.SEVERE, "No choice point found for: '" + choicePoint + "'");
			return new int[]{0};
		}
		
		ArrayList<DsAction> actions = c.getScript();
		int[] list = new int[actions.size()];
		
		for(int x = 0; x < actions.size(); x++)
			list[x] = actions.get(x).getSbIndex();
		
		return list;
	}
	

	/**
	 * Called when an action is selected by an action node
	 * @param choicePoint
	 * @param index
	 */
	public void actionSelectedBySimbionic(String choicePoint, int index, String agentName)
	{		
		log("Action selected - State: " + choicePoint + " Index: " + index + " Agent:" + agentName);
		
		ChoicePoint c = getChoicePoint(choicePoint);
		
		ArrayList<DsAction> actions = c.getActions();
		DsAction selected = null;
		for(DsAction a : actions)
		{
			if(a.getSbIndex() == index)
				selected = a;
		}
		
		if(selected == null)
		{
			log(new Exception("Invalid action selected for State: " + choicePoint + " Index: " + index));
			return;
		}
		else
			c.selectAction(selected);
	}
	
	
	public void rewardChoicePoint(String choicePoint, double reward)
	{
		ChoicePoint c = getChoicePoint(choicePoint);
		if(c == null)
		{
			_logger.log(Level.SEVERE, "No choice point found for: '" + choicePoint + "'");
			return;
		}
		
		c.reward(reward);
	}
	
	
	/**
	 * Find a choice point
	 * 
	 * @param choicePoint
	 * @return
	 */
	public ChoicePoint getChoicePoint(String choicePoint)
	{
		return choicePoints.getChoicePoint(choicePoint);
	}

	public void saveChoicePoint(String choicePoint, String filename) throws FileNotFoundException {
		choicePoints.saveChoicePoint(choicePoint, filename);
	}
	
	/**
	 * Load the choice point from the specified file
	 * @throws FileNotFoundException 
	 */
	public ChoicePoint loadChoicePoint(String filename) throws FileNotFoundException {
		return choicePoints.loadChoicePoint(filename);
	}
}
