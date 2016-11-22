package com.stottlerhenke.dynamicscripting;

import java.util.ArrayList;

/**
 * Classes that implement this method are responsible for implementing reward functions
 *
 */
public interface I_DynamicScriptingAdjustor
{
	/**
	 * When a reward node is reached, this method returns the value 
	 * of the reward for application to current selections.
	 * 
	 * @param state the selection state
	 * @param selectionState the cached state information from the time of selection,
	 * this will be null if a reward is being generated for multiple selections
	 * @return the reward for the given state
	 */
	public double getReward(String state, ArrayList<DsSelection> actionSelections);
	
}
