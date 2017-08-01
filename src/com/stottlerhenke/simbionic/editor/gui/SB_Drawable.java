
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Base class for all drawable element on the canvas.
 */
abstract public class SB_Drawable implements Externalizable
{
    private static final long serialVersionUID = 2302585093L + 1000;

    public final static BasicStroke stroke = new BasicStroke(1.0f);
    public final static BasicStroke wideStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    public final static float dash1[] = { 10.0f };
    public final static BasicStroke dashed = new BasicStroke(1.0f,
                                                      BasicStroke.CAP_BUTT,
                                                      BasicStroke.JOIN_MITER,
                                                      10.0f, dash1, 0.0f);
    
    public final static int font_point = 11;
    public final static int border_x = font_point + 1;
    public final static int border_y = font_point - 1;
    
    //public final static Font font = new Font("Arial Unicode MS", Font.PLAIN, font_point);
    //public final static Font font = new Font("Monaco", Font.PLAIN, font_point);
    public final static Font font = new Font("Consolas", Font.PLAIN, font_point);
    

    protected boolean _isHighlighted = false;
    protected boolean _isPrehighlighted = false;
    protected static boolean _highlightOnly = false;

    abstract public void draw(Graphics2D g2);
    abstract public void highlight(Graphics2D g2);
    abstract public SB_Drawable containsPoint(Point point);
    abstract public boolean intersectsRect(Rectangle rect);
    abstract public void offset(int delta_x, int delta_y, boolean highlightOnly);
    abstract public Rectangle unionRect(Rectangle ur, boolean highlightOnly);

    public boolean isHighlighted() { return _isHighlighted; }
    public void setHighlighted(boolean isHighlighted) { _isHighlighted = isHighlighted; }
    public boolean isPrehighlighted() { return _isPrehighlighted; }
    public void setPrehighlighted(boolean isPrehighlighted) { _isPrehighlighted = isPrehighlighted; }
    public void updatePrehighlight() { _isPrehighlighted = _isHighlighted; }
    public void bringHighlightToFront() { }
    public void makeSelection(Rectangle rect) { _isHighlighted = _isPrehighlighted ^ intersectsRect(rect); }
    
    public int count(Class c) 
    { 
    	if(c != null && c.equals(SB_MultiRectangle.class))
    		c = SB_Rectangle.class;
    	
    	return c == null || c.isInstance(this) ? 1 : 0; 
    }
    
    public int countHighlight() { return _isHighlighted ? 1 : 0; }
    public SB_Drawable firstHighlight() { return _isHighlighted ? this : null; }
    public SB_Drawable singleHighlight() { return firstHighlight(); }
    public int getId() { return -1; }
    public SB_Drawable findDrawable(int id, Class c) { return getId() == id && (c == null || c.isInstance(this)) ? this : null; }
    public void mapById(TreeMap map, Class c)
    {
    	if(c != null && c.equals(SB_MultiRectangle.class))
    		c = SB_Rectangle.class;
    	
    	if (c == null || c.isInstance(this))
    		map.put(new Integer(getId()), this);
    }
    public boolean exists(SB_Drawable drawable) { return drawable == this; }

    public boolean isComposite() { return false; }
    public boolean add(SB_Drawable drawable) { return false; }
    public List<SB_Drawable> removeHighlight() { return new ArrayList<SB_Drawable>(); }
    public void removeHighlightDependencies() { }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      out.writeBoolean(_isHighlighted);
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      _isHighlighted = in.readBoolean();
    }
}
