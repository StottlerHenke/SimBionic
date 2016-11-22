package com.stottlerhenke.dynamicscripting;



import java.util.ArrayList;
import java.util.HashMap;

/**
 * A HashMap wrapper that takes care of loading and saving.
 * 
 *
 */
public class ChoicePointTable
{
	protected HashMap<String, ChoicePoint> choicePointMap = new HashMap<String, ChoicePoint>();
	
	public ChoicePointTable()
	{
		super();
	}
	
	/**
	 * Add a choice point with a set of initial values
	 * @param choicePoint
	 * @param values Double objects
	 */
	public void addChoicePoint(String choicePointName, ArrayList<DsAction> actions, int scriptSize, int minActionValue, int maxActionValue)
	{
		ChoicePoint c  = new ChoicePoint(choicePointName, actions, scriptSize, minActionValue, maxActionValue);
		choicePointMap.put(choicePointName, c);
	}
	
	/**
	 * Get the actions for a particular choice point
	 * 
	 * @param choicePoint
	 * @return An array list of Doubles if found, null otherwise
	 */
	public ArrayList<DsAction> getChoicePointActions(String choicePoint)
	{
		return choicePointMap.get(choicePoint).getActions(0);
	}
	
	public ChoicePoint getChoicePoint(String choicePoint)
	{
		return choicePointMap.get(choicePoint);
	}
	
	/**
	 * Clear any selections that haven't been processed.
	 *
	 */
	public void clearSelections()
	{
		for(ChoicePoint c : choicePointMap.values())
		{
			c.clearSelections();
		}
	}
}
