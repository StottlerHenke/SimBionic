
package com.stottlerhenke.simbionic.editor.gui;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.script.ScriptException;

import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;

/**
 * This class represents an action node
 *
 */
public class SB_Rectangle extends SB_Element
{
    private static final long serialVersionUID = 2302585093L + 1006;

    static final Color _icolor = new Color(96, 255, 96);
    static final Color _fcolor = new Color(255, 96, 96);
    static final Color _acolor = new Color(254,255,79);
    static final Color _ccolor = new Color(78,65,254);

    private boolean _initial = false;
    
    public SB_Rectangle() {}
    
    public SB_Rectangle(ActionNode dataModel) {
       super(dataModel);
    }
    
    public ActionNode getActionNodeModel() {
       return (ActionNode)getDataModel();
    }
    
    public String getExpr()
    {
       String expr = getActionNodeModel().getExpr();
       if (expr == null) {
          return "";
       }
       return expr;
    }

    public void setExpr(String expr)
    {
      getActionNodeModel().setExpr(expr);
      updateComplex();
      _needToResize = true;
    }
    
    public boolean isInitial() {
       return _initial;
    }
    
    /**
     * Set the initial field for this node.
     * 
     * @param initial true if initial, false otherwise.
     */
    public void setInitial(boolean initial) {
    	// Note: when this method is called, make sure that the NodeGroup's setInitial
        // is also updated.
       _initial = initial;
    }
    
    public void setFinal(boolean isFinal) {
       getActionNodeModel().setIsFinal(isFinal);
    }
    
    public boolean isFinal() {
       return getActionNodeModel().isFinal();
    }
    
    public void setAlways(boolean isAlways) {
    	getActionNodeModel().setAlways(isAlways);
    }
    
    public boolean isAlways() {
    	return getActionNodeModel().isAlways();
    }
    
    public void setCatch(boolean isCatch) {
    	getActionNodeModel().setCatch(isCatch);
    }
    
    public boolean isCatch() {
    	return getActionNodeModel().isCatch();
    }

    public void draw(Graphics2D g2)
    {
    	super.draw(g2);
    	if (_runningState == RUNNING)
    	{
    		g2.setPaint(_rcolor);
    		if (isBehavior())
    			g2.fillRect(_hrect.x - 1, _hrect.y - 1, _hrect.width + 1, _hrect.height + 1);
    		else
    			g2.fill(_hrect);
    	}
    	
        if (isBehavior())
            g2.setStroke(SB_Drawable.wideStroke);
        else
            g2.setStroke(SB_Drawable.stroke);
        if (isInitial())
            g2.setPaint(_icolor);
        else if (isFinal())
            g2.setPaint(_fcolor);
        else if (isAlways())
        	g2.setPaint(_acolor);
        else if (isCatch())
        	g2.setPaint(_ccolor);
        else
            g2.setPaint(Color.white);
        
        
        paintRectangle(g2);

        if (getLabelMode() == COMMENT_LABEL || _bindingsString == null)
          g2.drawString(_label, _labelOffsetX + _rect.x + SB_Drawable.border_x,
                        _rect.y + SB_Drawable.font_point + SB_Drawable.border_y);
        else
        {
        	if (_label.length() > 0)
        	{
        		g2.drawString(_bindingsString, _bindingsOffsetX + _rect.x + SB_Drawable.border_x,
        					  _rect.y + SB_Drawable.font_point + SB_Drawable.border_y - 1);
        		g2.drawString(_label, _labelOffsetX + _rect.x + SB_Drawable.border_x,
        					  _rect.y + SB_Drawable.font_point + SB_Drawable.border_y + 14);
        	}
        	else //Draw a possible multiline binding
        	{
        		String lines[] = _bindingsString.split(endline);
        		int availableHeight = _rect.height - SB_Drawable.border_y - SB_Drawable.font_point;
        		int lineHeight = availableHeight / lines.length;
        		
        		for(int x = 0; x < lines.length; x++)
        		{
	        		g2.drawString(lines[x], 
	        				_bindingsOffsetX + _rect.x + SB_Drawable.border_x,
	  					  	_rect.y + SB_Drawable.font_point + lineHeight * x + SB_Drawable.border_y);     
        		}
        	}
        }
        if(_isBreakpoint){
    		g2.setPaint(_breakcolor);
    		if(_breakpointEnabled){
    			g2.fillOval(_hrect.x+5, _hrect.y+18, 7, 7);
    		}
    		else{
    			g2.drawOval(_hrect.x+6, _hrect.y+19, 5, 5);
    		}
    	}
        g2.setPaint(Color.BLACK);
    }
    
    protected void paintRectangle(Graphics2D g2)
    {
    	g2.fill(_rect);
    	g2.setPaint(Color.black);
    	g2.draw(_rect);
    	
    }

    public void highlight(Graphics2D g2)
    {
    	g2.setPaint(_hcolor);
    	if (_runningState == NOT_RUNNING)
    	{
    		if (isBehavior())
    			g2.fillRect(_hrect.x - 1, _hrect.y - 1, _hrect.width + 1, _hrect.height + 1);
    		else
    			g2.fill(_hrect);
    	}
    	else
    	{
    		if (isBehavior())
    			g2.fillRect(_hrect.x - 3, _hrect.y - 3, _hrect.width + 6, _hrect.height + 6);
    		else
    			g2.fillRect(_hrect.x - 2, _hrect.y - 2, _hrect.width + 4, _hrect.height + 4);
    	}
    	
        g2.setPaint(Color.black);
    }

    public SB_Drawable containsPoint(Point point)
    {
        return _hrect.contains(point) ? this : null;
    }

    public boolean intersectsRect(Rectangle rect)
    {
        return _hrect.intersects(rect);
    }

    protected Point intersectionFromPoint(Point point)
    {
	Point center = new Point((int) _rect.getCenterX(), (int) _rect.getCenterY());
	double top, left, right, bottom;
	double t = 0;

	if (_rect.contains(point))
            return center;

	// equation for line: v = t(center - point) + center
	if (center.x != point.x)
	{
		left = _rect.getMinX();
		right = _rect.getMaxX();
		if (point.x < left)
			// find intersection along left
			t = (left - point.x)/(center.x - point.x);
		else if (point.x > right)
			// find intersection along right
			t = (right - point.x)/(center.x - point.x);
	}
	if (center.y != point.y)
	{
		bottom = _rect.getMaxY();
		top = _rect.getMinY();
		if (point.y > bottom)
			// find intersection along bottom, taking the point nearer to center
			t = Math.max(t, (bottom - point.y)/(center.y - point.y));
		else if (point.y < top)
			// find intersection along top, taking the point nearer to center
			t = Math.max(t, (top - point.y)/(center.y - point.y));
	}

	return new Point((int) (t*(center.x - point.x) + point.x),
		(int) (t*(center.y - point.y) + point.y));
    }

    
    protected void updateComplex()
    {
      String name = SB_Catalog.extractFuncName(getExpr());
      SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
      boolean isBehavior = projectBar._catalog.findBehavior(name) != null;
      setIsBehavior(isBehavior);
      
    }
    
    protected boolean isBehavior() {
       return getActionNodeModel().isBehavior();
    }
    
    protected void setIsBehavior(boolean value) {
       getActionNodeModel().setIsBehavior(value);
    }

    protected void checkError(SB_Polymorphism poly, SB_ErrorInfo errorInfo, SB_MultiRectangle parentNode)
    {
       SB_Output build = SB_OutputBar._build;


       int nodeType = (isBehavior() ? 1 : 0);

       if(this instanceof SB_MultiRectangle)
          nodeType = SB_MultiRectangle.COMPOUND_ACTION;

       //Setup the component to assign errors to
       SB_Rectangle errorNode;
       if (parentNode != null)
          errorNode = parentNode;
       else
          errorNode = this;


       if (isFinal())
       {

          if( nodeType == SB_MultiRectangle.COMPOUND_ACTION)
          {
             errorInfo._ne++;
             build.addLine(new SB_Line("ERROR: Final node cannot be a compound action node.",
                   Color.red, poly, errorNode, null, SB_Line.ERROR));
             return;

          }
          if (getExpr().length() > 0)
          {
             errorInfo._ne++;
             build.addLine(new SB_Line("ERROR: Final can only be specified on 'None' action.",
                   Color.red, poly, errorNode, null, SB_Line.ERROR));
          }
          if (getBindingCount() > 0)
          {
             errorInfo._ne++;
             build.addLine(new SB_Line("ERROR: No bindings allowed on final.", Color.red,
                   poly, errorNode, null, SB_Line.ERROR));
             return;
          }

          nodeType = 2;
       }

       SB_Catalog catalog = ((SB_ProjectBar) ComponentRegistry.getProjectBar())._catalog;
       String name = SB_Catalog.extractFuncName(getExpr());

       if (nodeType == 5)
       {
          name += "...";
       }


       if (getExpr().length() > 0)
       {
          String expr = catalog.constantReplace(getExpr());

             try {
                SB_JavaScriptEngine.compile( expr);
             } catch (Exception ex) {
                String errMsg = ex.getMessage();
                errorInfo._ne++;
                if (ex instanceof ScriptException) {
                   errMsg = SB_JavaScriptEngine.getMessage((ScriptException)ex, false);
                } 

                build.addLine(new SB_Line("ERROR: [" + expr + "] " +errMsg, Color.red, poly, errorNode, null, SB_Line.ERROR));
                return;
             } 
       }
       else {
          name = "None";
       }


       String ab_text = (isBehavior() ? "behavior" : "action");
       if( nodeType == SB_MultiRectangle.COMPOUND_ACTION)
          ab_text = "multi-action";

       if(parentNode == null )
       {	
          if (!isInitial() && poly.getConnectors().isUnreachable(this))
          {
             errorInfo._nw++;
             build.addLine(new SB_Line("WARNING: '" + name + "' " + ab_text + " is unreachable.",
                   Color.red, poly, errorNode, null, SB_Line.WARNING));
          }
          else if (_connectors.size() == 0)
          {
             if (!isFinal())
             {
                if (poly.getParent().getExec() == SB_Behavior.kExecOneTick)
                {
                   errorInfo._ne++;
                   build.addLine(new SB_Line("ERROR: '" + name + "' " + ab_text + " is a dead-end node in a one-tick behavior.",
                         Color.red, poly, errorNode, null, SB_Line.ERROR));                                    
                }
                else
                {
                   errorInfo._nw++;
                   build.addLine(new SB_Line("WARNING: '" + name + "' " + ab_text + " is terminal, non-final.",
                         Color.red, poly, errorNode, null, SB_Line.WARNING));                  
                }
             }
          }
          else
          {
             if (isFinal())
             {
                errorInfo._nw++;
                build.addLine(new SB_Line("WARNING: '" + name  + "' " + ab_text + " is non-terminal, final.",
                      Color.red, poly, errorNode, null, SB_Line.WARNING));
             }
          }
       }


       int size_var_bindings = getBindingCount();

       if( nodeType == SB_MultiRectangle.COMPOUND_ACTION)
       {
          size_var_bindings = 0;
       }


       for (int i = 0; i < size_var_bindings; ++i)
       {
          SB_Binding binding = getBinding(i);
          binding.checkError(poly, this, errorInfo);
       }
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      super.writeExternal(out);

      out.writeBoolean(isInitial());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);

      setInitial(in.readBoolean());
    }

    public boolean isSpecial() {

		return _initial || isFinal() || isAlways() || isCatch();
	}

}
