
package com.stottlerhenke.simbionic.editor;


import com.stottlerhenke.simbionic.editor.gui.SB_Condition;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_Rectangle;

/**
 * Represents the current state of an execution frame
 * in the interactive debugger.
 */
public class SB_Frame {
	public SB_Polymorphism _poly;
	public SB_Condition _condition;
	public SB_Rectangle _rectangle;
	public int _parent;

	public SB_Frame(SB_Polymorphism poly, SB_Rectangle rectangle, int parent)
	{
		_poly = poly;
		_rectangle = rectangle;
		_parent = parent;
	}
	
	public String toString()
	{
	    String pName = (_poly != null) ? _poly.getParent().getName() : "none";
	    String pNode = (_condition != null) ? _condition.getExpr() : ((_rectangle != null) ? _rectangle.getExpr() : "none");
	    return pName + " : " + pNode;
	}
}
