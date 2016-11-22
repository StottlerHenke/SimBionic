package com.stottlerhenke.simbionic.editor;

import com.stottlerhenke.simbionic.editor.gui.SB_Element;

/**
 * Defines a breakpoint maintained in the Editor/Debugger.  Not
 * actually used by the engine as a breakpoint, merely reflects
 * such a breakpoint for processing on the GUI side.
 *
 */
public class SB_Breakpoint implements Comparable{
    public static final long ALL_ENTITIES = -1;
    public static final long WATCHED_ENTITIES = -2;

    public long _entityId = ALL_ENTITIES;
	public String _varName;
	public String _behavior;
	public String _constraint; // no constraint by default
	public int _breakpointId;
	public boolean _enabled;
	public int _iterations = 0;
	
	//neccessary for breakpoints on canvas elements
	public SB_Element _elem;
	public int _elemId;
	
	public int _type;
	public static final int RECTANGLE = 0;
	public static final int CONDITION = 1;
	public static final int VARIABLE = 2;

	public SB_Breakpoint(int breakpointID)
	{
		_breakpointId = breakpointID;
	}
	
	public int compareTo(Object anotherBP) throws ClassCastException {
	    return this._breakpointId - ((SB_Breakpoint)anotherBP)._breakpointId;  
	  }
}
