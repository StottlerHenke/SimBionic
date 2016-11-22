package com.stottlerhenke.dynamicscripting;



/**
 * This class encapsulates a single action by storing its name and the 
 * value associated with the action.
 *
 */
public class DsAction implements Cloneable, Comparable
{
	public String name;
	public Double value;
	public Integer priority;
	
	/** The corresponding transition index in SimBionic */
	public Integer sbIndex;
	
	public DsAction(String name, double value, int priority, int sbIndex)
	{
		this.name = name;
		this.value = value;
		this.priority = priority;
		this.sbIndex = sbIndex;
	}
	
	
	public String toString()
	{
		return name + ":" + value.intValue();
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
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
	 * @return the action value
	 */
	public Double getValue()
	{
		return value;
	}

	/**
	 * @param value the new action value
	 */
	public void setValue(Double value)
	{
		this.value = value;
	}


	/**
	 * @return Returns the priority.
	 */
	public Integer getPriority()
	{
		return priority;
	}


	/**
	 * @param priority The priority to set.
	 */
	public void setPriority(Integer priority)
	{
		this.priority = priority;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o)
	{
		if(o instanceof DsAction)
		{
			DsAction a = (DsAction) o;

			int value = this.priority.compareTo(a.priority);
			return value;
		}
		
		return 0;
	}


	/**
	 * @return Returns the sbIndex.
	 */
	public Integer getSbIndex()
	{
		return sbIndex;
	}


	/**
	 * @param sbIndex The sbIndex to set.
	 */
	public void setSbIndex(Integer sbIndex)
	{
		this.sbIndex = sbIndex;
	}
}
