
package com.stottlerhenke.simbionic.editor;

import java.util.Vector;

/**
 * Represents an entity's status in the interactive
 * debugger.
 * 
 */
public class SB_Entity {
	public long _entityId;
	public String _name;
	public int _currentFrame = -1;  // -1 == empty stack
	public Vector _frames = new Vector();
	public long _alive;
	public boolean _watch;

	public SB_Entity(long entityId, String name)
	{
		_entityId = entityId;
		_name = name;
	}
	
	public String toString()
	{
	    return _name + " (" + _entityId + ") : " + _currentFrame + "/" + _frames.size();
	}
}
