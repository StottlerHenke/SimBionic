package com.stottlerhenke.simbionic.test.dynamicscripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.stottlerhenke.dynamicscripting.DynamicScriptingWrapper;

/**
 * This class defines a simple interface for the dynamic scripting test
 */
public class DSTestInterface
{
	protected ArrayList<DSTestActionType> actions = new ArrayList<>();
	protected Map<DSTestActionType, Integer> countMap;

	public DSTestInterface() {
			
		initCountMap();
	}
	
	public void initCountMap() {
		
		countMap = new HashMap<>();
		for(DSTestActionType type : DSTestActionType.values()) {
			countMap.put(type, 0);
		}	
	}
	
	/**
	 * Perform an action by adding the name of the action to the list
	 * 
	 * @param name
	 */
	public void performAction(DSTestActionType name) {

		//System.out.println("performed action: " + name);

		//Add to action list
		if(name != null) {
			actions.add(name);

			//increment count
			int count = countMap.get(name);
			countMap.put(name, count + 1);
		}
	}
	
	public Map<DSTestActionType, Integer> getCountMap() {
		return countMap;
	}
	
	public void clearActions() {
		
		//System.out.println("cleared actions.");
		
		actions.clear();
	}
	
	public DSTestActionType getLastAction() {
		
		//System.out.println("getLastAction:" + actions.toString());
		
		if(actions.isEmpty())
			return null;
		else
			return actions.get(actions.size() - 1);
		
	}
		
	/**
	 * Reward the actions on the action list
	 * 
	 */
	public void reward(String choicePointName) {
			
		double reward = 0;
		if(actions.size() == 1) {
			//Reward for immediate learning
			DSTestActionType lastAction = getLastAction();
			switch(lastAction) {
			case ZERO:
			case ONE:
				reward = 1.0;
				break;
			default:
				reward = 0;
				break;
			}
		}
		else
		if(actions.size() > 1) {
			//Reward for episodic learning

			if(actions.contains(DSTestActionType.ZERO))
				reward += 10;
			
			if(actions.contains(DSTestActionType.ONE))
				reward  += 10;
			
			if(actions.contains(DSTestActionType.TWO))
				reward  -= 10;
		}
			
		DynamicScriptingWrapper.getInstance().rewardChoicePoint(choicePointName, reward);
		actions.clear();
	}
}