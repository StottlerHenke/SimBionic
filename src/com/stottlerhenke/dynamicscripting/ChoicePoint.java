package com.stottlerhenke.dynamicscripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;



/**
 * A single choice point. Each choice point could have more than one
 * policy when state abstraction is implemented, but for now there is only
 * one item in the array of actionList, scriptList, and selectionList
 * 
 *
 */
public class ChoicePoint
{
	public static final int TEMP = 5;
	
	/** The name of this choice point */
	protected String name;

	/** a copy of the default action set */
	protected ArrayList<DsAction> originalActions;
	
	/** The actions available to this choice point for each policy */
	protected ArrayList<ArrayList<DsAction>> actionList = new ArrayList<ArrayList<DsAction>>() ;

	/** The actions available to this choice point for each policy */
	protected ArrayList<ArrayList<DsAction>> scriptList = new ArrayList<ArrayList<DsAction>>() ;
	
	/** The list of selections that have occurred in this choice point for each policy */
	protected ArrayList<ArrayList<DsSelection>> selectionList= new ArrayList<ArrayList<DsSelection>>();	
	
	/** The size of the generated scripts */
	protected int scriptSize;
	
	/** Whether or not to use scaled value updating */
	protected boolean scaled = false;
	
	/** count the number of rewards */
	int rewardCount = 0;
	
	int MIN_VALUE = 0;
	
	int MAX_VALUE = 1000;
	
	
	
	/**
	 * Create a choice point with a single policy
	 */
	public ChoicePoint(String name, 
			ArrayList<DsAction> actions,
			int scriptSize,
			int MIN_VALUE,
			int MAX_VALUE)
	{
		super();
		
		this.name = name;
		
		this.scriptSize = scriptSize;
		this.MIN_VALUE = MIN_VALUE;
		this.MAX_VALUE = MAX_VALUE;

		try
		{
			actionList = new ArrayList<ArrayList<DsAction>>();
			
			ArrayList<DsAction> copy = new ArrayList<DsAction>();
			originalActions = new ArrayList<DsAction>();
			for(DsAction a : actions)
			{
				copy.add( (DsAction) a.clone());
				originalActions.add((DsAction) a.clone());
			}
			
			actionList.add( copy );
			
		} 
		catch (CloneNotSupportedException ex)
		{
			ex.printStackTrace();
		}
		
		selectScript();
	}
	
	/**
	 * Clear any selections that have not been rewarded and select a new 
	 * script 
	 *
	 */
	public void clearSelections()
	{
		selectScript();
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 
	 * @return the dynamically generated script, in order
	 */
	public ArrayList<DsAction> getScript()
	{
		ArrayList<DsAction> script = scriptList.get(0);
		Collections.sort(script, new DsActionComparator());
		
		return script;
	}
	
	/**
	 * @return Returns the actions.
	 */
	public ArrayList<DsAction> getActions()
	{
		return actionList.get(0);
	}

	/**
	 * @return Returns the actions.
	 */
	protected ArrayList<DsAction> getActions(int policy)
	{
		return actionList.get(policy);
	}
	
	/**
	 * @param actions The actions to set.
	 */
	protected void setActions(int policy, ArrayList<DsAction> actions)
	{
		this.actionList.set(policy, actions);
	}

	/**
	 * @return Returns all of the selections
	 */
	protected ArrayList<DsSelection> getSelections(int policy)
	{
		return selectionList.get(policy);
	}
	
	/**
	 * @return Returns the scaled.
	 */
	public boolean isScaled()
	{
		return scaled;
	}

	/**
	 * @param scaled The scaled to set.
	 */
	public void setScaled(boolean scaled)
	{
		this.scaled = scaled;
	}
	
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append(this.getName() + " ");
		for(int x = 0; x < actionList.size(); x++)
		{
			buf.append( "" + x + ":" + getActions(x) + " ");
		}
		
		return buf.toString();
	}
	

	/**
	 * Generate a script for this choice point and clear
	 * the previous selections.
	 * 
	 * @param scriptSize
	 * @return
	 */
	public void selectScript()
	{	
		selectionList.clear();
		scriptList.clear();
		for(ArrayList<DsAction> actionValueSet : actionList)
		{
			ArrayList<DsAction> script = createScript(actionValueSet);
			scriptList.add(script);
			selectionList.add(new ArrayList<DsSelection>());
		}
	}
	
	/**
	 * Create a script for the action value set
	 * @param actionValueSet
	 * @return
	 */
	protected ArrayList<DsAction> createScript(ArrayList<DsAction> actionValueSet)
	{
		int selected = 0;
		ArrayList<DsAction> script = new ArrayList<DsAction>();
		ArrayList<DsAction> allActions = new ArrayList<DsAction>();
		allActions.addAll(actionValueSet);
		
		while(selected < scriptSize)
		{
			DsAction a = softMaxSelection( allActions );
			allActions.remove(a);
			script.add( a );
			++selected;
			
			if(allActions.size() == 0)
				break;
		}
		
		return script;
	}
	
	/**
	 * Select a value from the list using a soft max (Boltzmann) 
	 * distribution.
	 * 
	 * @param actions
	 * @return the selected index, null if no decision  is made
	 */
	public static DsAction softMaxSelection(ArrayList<DsAction> actions)
	{
		ArrayList<Double> probs = new ArrayList<Double>();

		//Create the softmax values and the sum
		double sum = 0;
		for(int x = 0; x < actions.size(); x++)
		{
			Double v = actions.get(x).value;
			Double q = new Double(generateSoftMaxValue(v, TEMP));
			probs.add(x, q);
			sum += q.doubleValue();
		}
		
		//Set the probabilities
		for(int x = 0; x < probs.size(); x++)
		{
			Double q = probs.get(x);
			probs.set(x, new Double( q.doubleValue()/sum));
		}

		//System.out.println("\tProbabilities: " + probs.toString());
		
		//Select when p exceeds the threshold of the 
		//probablility of the given action plus the actions that
		//were not chosen
		double p = Math.random();
		double partialSum = 0;
		for(int x = 0; x < probs.size(); x++)
		{
			Double q = probs.get(x);
			if(p <= (q.doubleValue() + partialSum))
			{
				return actions.get(x);
			}
			
			partialSum += q.doubleValue();
		}
		
		//Rounding error, return the last one
		return actions.get(actions.size() -1 );
	}
	
	/**
	 * 
	 * @param v
	 * @return e^(Q/t)
	 */
	protected static double generateSoftMaxValue(Double q, double temperature)
	{
		return fastExp(q.doubleValue()/temperature);
		//return Math.exp(q.doubleValue()/temperature);
	}
	
	/**
	 * The paper A Fast, Compact Approximation of the 
	 * Exponential Function describes a C macro that 
	 * does a good job at exploiting the IEEE 754 
	 * floating-point representation to calculate e^(x).
	 *  
	 *  NOTE: Slightly faster, but not a lot
	 *  
	 * @param val
	 * @return
	 */
	public static double fastExp(double val) 
	{
	    final long tmp = (long) (1512775 * val + (1072693248 - 60801));
	    return Double.longBitsToDouble(tmp << 32);
	}
	
	/**
	 * Select an action, in order, from the given script
	 * @return the first valid action, null if none
	 * @deprecated The SimBionic version should use selectAction instead
	 */
	public DsAction selectAction()
	{	
		ArrayList<DsAction> script = scriptList.get(0);
		Collections.sort(script, new DsActionComparator());
		
		for(DsAction a : script)
		{
			selectAction(a);
			return a;
		}
		
		return null;
	}
	
	/**
	 * Add the given action to the selection list
	 * 
	 * @param action
	 */
	public void selectAction(DsAction action)
	{
		selectionList.get(0).add(new DsSelection(action));
	}
	

	/**
	 * Sort actions by priority then value
	 * @author ludwig
	 *
	 */
	class DsActionComparator implements Comparator<DsAction>
	{
		public int compare(DsAction o1, DsAction o2)
		{
			//Lower value = higher 
			int value = o1.getPriority().compareTo(o2.getPriority());
			if(value != 0)
				return value;
			else  //Higher value = lower
				return new Double(o2.getValue()).compareTo(o1.getValue());
		}
	}
	
	/**
	 * Reward this choice point for all of the selections that it has made.
	 * Following this, a new script is generated.
	 * 
	 * @param reward
	 */
	public void reward(double reward)
	{
		for(int x = 0; x < actionList.size(); x++)
		{	
			if(isScaled())
			{
				ArrayList<DsAction> completed = new ArrayList<DsAction>();
				ArrayList<DsAction> notCompleted = new ArrayList<DsAction>();
				ArrayList<DsAction> notInScript = new ArrayList<DsAction>();
				
				for(DsSelection selection : selectionList.get(x))
					completed.add(selection.getAction());

				notCompleted.addAll(scriptList.get(x));
				notCompleted.removeAll(completed);
				notInScript.addAll(actionList.get(x));
				notInScript.removeAll(scriptList.get(x));
				
				WeightAdjust.adjustWeightsScaledSum(completed, notCompleted, notInScript, reward, MIN_VALUE, MAX_VALUE);
				
			}
			else
			{
				Set<DsAction> completed = new HashSet<DsAction>();
				Set<DsAction> notCompleted = new HashSet<DsAction>();
				Set<DsAction> notInScript = new HashSet<DsAction>();
				
				for(DsSelection selection : selectionList.get(x))
					completed.add(selection.getAction());

				notCompleted.addAll(scriptList.get(x));
				notCompleted.removeAll(completed);
				notInScript.addAll(actionList.get(x));
				notInScript.removeAll(scriptList.get(x));
				
				WeightAdjust.adjustWeights(completed, notCompleted, notInScript, reward, MIN_VALUE, MAX_VALUE);
				
			}
		}
		
		++rewardCount;
		selectScript();
	}
	
	public void reward(ArrayList<Double> rewards)
	{
		double sum = 0;
		for(Double d:rewards)
			sum += d;
		
		reward(sum);
	}
}
