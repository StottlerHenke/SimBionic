package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
public class SB_Line {
    public static final int ERROR = 0;
    public static final int WARNING = 1;
    public static final int MESSAGE = 2;
    
	protected String _text;
	protected Color _color;
	protected SB_Polymorphism _poly = null;
	protected SB_Drawable _drawable = null;
	protected Object _data = null; // behavior or binding or global or constant
								   // or descriptors
	protected int _index1 = -1;
	protected int _index2 = -1;
    protected int _priority = MESSAGE;
    
	public SB_Line(String text, Color color, SB_Polymorphism poly,
			SB_Drawable drawable, Object data, int index1, int index2) {
		_text = text;
		_color = color;
		_poly = poly;
		_drawable = drawable;
		_data = data;
		_index1 = index1;
		_index2 = index2;
	}
	public SB_Line(String text, Color color, SB_Polymorphism poly,
			SB_Drawable drawable, Object data, int priority) {
		_text = text;
		_color = color;
		_poly = poly;
		_drawable = drawable;
		_data = data;
        _priority = priority;
	}
	public SB_Line(String text, Color color) {
		_text = text;
		_color = color;
	}
	public SB_Line(String text) {
		_text = text;
		_color = Color.black;
	}
	/**
	 * @return Returns the data.
	 */
	public Object getData() {
		return _data;
	}
    
    public String getText()
    {
        return _text;
    }
    
    /**
     * @return the priority of this line (MESSAGE, WARNING, ERROR)
     */
    public int getPriority()
    {
        return _priority;
    }
}