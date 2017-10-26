/**
 * 
 */
package com.stottlerhenke.dynamicscripting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of static methods for weight adjustment
 *
 */
public class WeightAdjust
{	
	/**
	 * A scaled adjustment based on action usage:
	 * 
	 * a * ((k * payout)/n)
	 * 
	 * where, a = number of time action executed
	 * k = #distinct actions taken
	 * n = #actions taken
	 * 
	 * @param completed
	 * @param notCompleted
	 * @param notInScript
	 * @param maxCompleted
	 */
	public static void adjustWeightsScaledSum(
			ArrayList<DsAction> completed, 
			ArrayList<DsAction> notCompleted, 
			ArrayList<DsAction> notInScript,
			double reward,
			int MIN_VALUE, int MAX_VALUE)
	{
		
//		no adjustment if we didn't actually complete anything
		if(completed.size() == 0)
			return;
		
		double halfReward = reward / 2.0;
		double rewardSum = completed.size() * reward + notCompleted.size() * halfReward; //Total reward as normal
		
		//Now, divide rewardSum among the actions.
		int n = 0;
		n += completed.size();
		n += notCompleted.size();
		
		double slice = rewardSum / n;
		
		//Now, create action sets
		Set<DsAction> completedSet = new HashSet<DsAction>();
		completedSet.addAll(completed);
		Set<DsAction> notCompletedSet = new HashSet<DsAction>();
		notCompletedSet.addAll(notCompleted);
		Set<DsAction> notInScriptSet = new HashSet<DsAction>();
		notInScriptSet.addAll(notInScript);
		
		double remainder = 0;
		double rewardGiven = 0;
		for(DsAction a : completedSet)
		{
			int completions = getCompletions(a, completed) + 1;
			
			rewardGiven += completions *slice;
			
			//NwnLogger.getInstance().log("---Scaled2: " + this.getName() + " " + a.getName() + " " + 
			//		completions + " of " + n, Level.WARNING);
	
			remainder += adjustWeight(a, slice * completions, MIN_VALUE, MAX_VALUE);
		}
		
		for(DsAction a : notCompletedSet)
		{
			rewardGiven += slice;
			
			//NwnLogger.getInstance().log("---Partial2: " + this.getName() + " " + a.getName(), Level.WARNING);
			remainder += adjustWeight(a, slice, MIN_VALUE, MAX_VALUE);
		}
		
		double compensation = 0;
		if(notInScript.size() > 0  )
			compensation =  -( rewardGiven / (double) notInScript.size());
		else
		{
			remainder += - rewardGiven;
		}
		
		for(DsAction a : notInScriptSet)
		{
			//NwnLogger.getInstance().log("---Compensation2: " + this.getName() + " " + a.getName(), Level.WARNING);
			remainder += adjustWeight(a, compensation, MIN_VALUE, MAX_VALUE);
		}
		
		distributeRemainder(completedSet, notCompletedSet, notInScriptSet, remainder, MIN_VALUE, MAX_VALUE);
	}
	
	/**
	 * 
	 * @param a
	 * @param list
	 * @return the number of times a occurs in list
	 */
	protected static int getCompletions(DsAction a, ArrayList<DsAction> list)
	{
		int count = 0;
		for(DsAction l : list)
		{
			if(a.getName().equals(l.getName()))
				count++;
		}
		return count;
	}
	
	/**  Perform the dynamic scripting weight adjustment. 
	 * 
	 *  Adjust the weights on the three lists
	 */
	public static void adjustWeights(
			Set<DsAction> completed, 
			Set<DsAction> notCompleted, 
			Set<DsAction> notInScript,
			double reward,
			int MIN_VALUE, int MAX_VALUE)
	{
		//no adjustment if we didn't actually complete anything
		if(completed.size() == 0)
			return;
		
		double remainder = 0;
		
		double halfReward = reward / 2.0;
		double compensation = 0;
		if(notInScript.size() > 0  )
			compensation =  -((double) (completed.size() * reward + notCompleted.size() * halfReward ) 
					/ (double) notInScript.size());
		else
			remainder =  -((double) (completed.size() * reward + notCompleted.size() * halfReward ));
	
		
		
		for(DsAction a : completed)
		{
			remainder += adjustWeight(a, reward, MIN_VALUE, MAX_VALUE);
		}
		
		for(DsAction a : notCompleted)
		{
			remainder += adjustWeight(a, halfReward, MIN_VALUE, MAX_VALUE);
		}
		
		for(DsAction a : notInScript)
		{
			remainder += adjustWeight(a, compensation, MIN_VALUE, MAX_VALUE);
		}
		distributeRemainder(completed, notCompleted, notInScript, remainder, MIN_VALUE, MAX_VALUE);
	}
	
	/**
	 * Adjust the weight of action a by the given amount, returning any remainder
	 * @param a
	 * @param adjustment
	 * @return
	 */
	private static double adjustWeight(
			DsAction a, 
			double adjustment, 
			int MIN_VALUE, int MAX_VALUE)
	{
		double value = a.getValue() + adjustment;
		double remainder = 0;
		if(value > MAX_VALUE)
		{
			remainder = value - MAX_VALUE;
			value = MAX_VALUE;
		}
		else
		if(value < MIN_VALUE)
		{
			remainder = value;
			value = MIN_VALUE;
		}
		a.setValue( value );
		return remainder;
	}
	
	private static void distributeRemainder(
			Set<DsAction> completed, 
			Set<DsAction> notCompleted, 
			Set<DsAction> notInScript, 
			double r, 
			int MIN_VALUE, int MAX_VALUE)
	{
		ArrayList<DsAction> set = new ArrayList<DsAction>();
		set.addAll(completed);
		set.addAll(notCompleted);
		set.addAll(notInScript);
	
		ArrayList<DsAction> toRemove = new ArrayList<DsAction>();
		
		if( r > 0)
		while(r > 0)
		{	
			for(DsAction a : set)
			{
				if(a.getValue() >= (MAX_VALUE - 1))
				{
					toRemove.add(a);
					continue;
				}
				
				if(r == 0)
					break;
				
				double toApply = 1;
				if( r < 1)
				{
					toApply = r;
					r = 0;
				}
				else
					r = r - 1;
				
				a.setValue(a.getValue() + toApply);
			}
			
			set.removeAll(toRemove);
			toRemove.clear();
		}
		else
		if( r < 0)
		while(r < 0)
		{
			for(DsAction a : set)
			{
				if(a.getValue() <= MIN_VALUE)
				{
					toRemove.add(a);
					continue;
				}
				
				if(r == 0)
					break;
				
				double toApply = -1;
				if( r > -1)
				{
					toApply = r;
					r = 0;
				}
				else
					r = r + 1;
				
				a.setValue(a.getValue() + toApply);
			}
			
			set.removeAll(toRemove);
			toRemove.clear();
		}	
		
		
		 set = new ArrayList<DsAction>();
			set.addAll(completed);
			set.addAll(notCompleted);
			set.addAll(notInScript);
			
		/*int sum = 0;
		for(DsAction a : set)
			sum += a.getValue();
		System.out.println("Action value: " + sum);*/
	}
}
