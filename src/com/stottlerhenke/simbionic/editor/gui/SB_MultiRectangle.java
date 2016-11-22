package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.IOException;

import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * This node supports compound actions
 *
 */
public class SB_MultiRectangle extends SB_Rectangle
{
	public final static int COMPOUND_ACTION = 5;
	
	 public SB_MultiRectangle() {}
	
	public SB_MultiRectangle(CompoundActionNode dataModel) {
	   super(dataModel);
	}
	
	/**
	 * Paint a shadow rectangle to the upper left.
	 * 
	 * @see com.stottlerhenke.simbionic.editor.gui.SB_Rectangle#paintRectangle(java.awt.Graphics2D)
	 */
	protected void paintRectangle(Graphics2D g2)
    {
    	Rectangle shadow = new Rectangle(_rect);
    	shadow.setLocation( _rect.getLocation().x - 3, _rect.getLocation().y - 3);

    	Paint fill = g2.getPaint();
    	
    	g2.fill(shadow);
    	g2.setPaint(Color.black);
    	g2.draw(shadow);
    	
    	g2.setPaint(fill);
    	g2.fill(_rect);
    	g2.setPaint(Color.black);
    	g2.draw(_rect);
    }
	
	/**
	 * Surround both the main and shadow rectangles
	 * 
	 * @see com.stottlerhenke.simbionic.editor.gui.SB_Drawable#highlight(java.awt.Graphics2D)
	 */
    public void highlight(Graphics2D g2)
    {
    	g2.setPaint(_hcolor);
    	if (_runningState == NOT_RUNNING)
    	{
    		g2.fillRect(_hrect.x - 3, _hrect.y - 3, _hrect.width + 3, _hrect.height + 3);
    	}
    	else
    	{
    		g2.fillRect(_hrect.x - 5, _hrect.y - 5, _hrect.width + 4, _hrect.height + 4);
    	}
    	
        g2.setPaint(Color.black);
    }
    
    /**
     * Create a custom string to be displayed as the Multi-Rectangle label
     * @see com.stottlerhenke.simbionic.editor.gui.SB_BindingsHolder#updateBindings()
     */
    public void updateBindings()
    {
      int size = getBindingCount();
      if (size == 0)
        _bindingsString = null;
      else
      {
        _bindingsString = new String();
        for (int i = 0; i < size; ++i)
        {
        	SB_Binding entry = getBinding(i);
        	if(entry.isAction())
        		_bindingsString += entry.getExpr() + "\r\n";
        	else
        		_bindingsString += "[" + entry.getVar() + "]\r\n";
        }
      }
      if (getLabelMode() == TRUNCATED_LABEL && _bindingsString != null && _bindingsString.length() > 20)
          _bindingsString = _bindingsString.substring(0, 16) + "...";
      
      _needToResize = true;
    }
    
    
    public CompoundActionNode getCompoundActionNodeModel() {
       return (CompoundActionNode)getDataModel();
    }
}
