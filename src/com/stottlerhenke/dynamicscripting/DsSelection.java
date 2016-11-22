/**
 * 
 */
package com.stottlerhenke.dynamicscripting;

import java.util.ArrayList;

/**
 * A single dynamic scripting selection that contains both the selected
 * action and (optionally) additional state information.
 *
 */
public class DsSelection
{
	protected DsAction action;
	protected ArrayList<Object> state;
	
	/**
	 * 
	 */
	public DsSelection()
	{
		super();
	}
	
	public DsSelection(DsAction action)
	{
		setAction(action);
		setState(null);
	}
	
	public DsSelection(DsAction action, ArrayList<Object> state)
	{
		setAction(action);
		setState(state);
	}
	
	/**
	 * @return Returns the action.
	 */
	public DsAction getAction()
	{
		return action;
	}

	/**
	 * @param action The action to set.
	 */
	public void setAction(DsAction action)
	{
		this.action = action;
	}

	/**
	 * @return Returns the state.
	 */
	public ArrayList<Object> getState()
	{
		return state;
	}

	/**
	 * Creates a deep copy of the state.
	 * 
	 * @param state The state to set.
	 */
	public void setState(ArrayList<Object> state)
	{
		if(state == null)
		{
			this.state = null;
			return;
		}
		
		this.state = new ArrayList<Object>();
		
		for(Object o : state)
		{
			if(o instanceof Double)
				this.state.add( new Double( (Double) o));
			else
			if(o instanceof Integer)
				this.state.add( new Integer( (Integer) o));
			else
				this.state.add(o.toString());
		}
	}


}
