
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.script.ScriptException;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.engine.SB_JavaScriptEngine;

/**
 * Represents a condition in the editor
 *
 */
public class SB_Condition extends SB_Element
{
    private static final long serialVersionUID = 2302585093L + 1007;
   
    public SB_Condition() {}
    
    public SB_Condition(Condition dataModel) {
       super(dataModel);
    }
    
    public Condition getConditionModel() {
       return (Condition)getDataModel();
    }
    
    public String getExpr()
    {
        String expr = getConditionModel().getExpr();
        if (expr == null) {
           return "";
        }
        return expr;
    }

    public void setExpr(String expr)
    {
       getConditionModel().setExpr(expr);
      _needToResize = true;
    }

    public void draw(Graphics2D g2)
    {
    	super.draw(g2);
    	
    	if (_runningState != NOT_RUNNING)
    	{
    		if (_runningState == RUNNING_CHECKED)
    			g2.setPaint(_rccolor);
    		else if (_runningState == RUNNING_FOLLOWED)
    			g2.setPaint(_rfcolor);
    		g2.fillOval(_hrect.x, _hrect.y, _hrect.width, _hrect.height);
    	}
    	
        g2.setStroke(SB_Drawable.stroke);
        g2.setPaint(Color.white);
        g2.fillOval(_rect.x + 1, _rect.y + 1, _rect.width - 1, _rect.height - 1);
        g2.setPaint(Color.black);
        g2.drawOval(_rect.x, _rect.y, _rect.width, _rect.height);
        if (getLabelMode() == COMMENT_LABEL || _bindingsString == null)
          g2.drawString(_label, _labelOffsetX + _rect.x + SB_Drawable.border_x,
                        _rect.y + SB_Drawable.font_point + SB_Drawable.border_y);
        else
        {
          if (_label.length() > 0)
          {
          	g2.drawString(_bindingsString, _bindingsOffsetX + _rect.x + SB_Drawable.border_x,
          				  _rect.y + SB_Drawable.font_point + SB_Drawable.border_y + 2);
          	g2.drawString(_label, _labelOffsetX + _rect.x + SB_Drawable.border_x,
                      	  _rect.y + SB_Drawable.font_point + SB_Drawable.border_y + 15);
          }
          else
          {
          	g2.drawString(_bindingsString, _bindingsOffsetX + _rect.x + SB_Drawable.border_x,
    				  _rect.y + SB_Drawable.font_point + SB_Drawable.border_y);
          }
        }
        
        if(_isBreakpoint){
    		g2.setPaint(_breakcolor);
    		if(_breakpointEnabled){
    			g2.fillOval(_rect.x+3, _rect.y+14, 7, 7);
    		}
    		else{
    			g2.drawOval(_rect.x+4, _rect.y+15, 5, 5);
    		}
    	}
        g2.setPaint(Color.BLACK);
    }

    public void highlight(Graphics2D g2)
    {
    	g2.setPaint(_hcolor);
    	if (_runningState == NOT_RUNNING)
    		g2.fillOval(_hrect.x, _hrect.y, _hrect.width, _hrect.height);
    	else
    		g2.fillOval(_hrect.x - 2, _hrect.y - 2, _hrect.width + 4, _hrect.height + 4);
        g2.setPaint(Color.black);
    }

    public SB_Drawable containsPoint(Point point)
    {
	// check if oval contains point
	// equation for oval (and interior): [(x - cx)/a]^2 + [(y - cy)/b]^2 <= 1
	double a = _hrect.getWidth()/2;
	double b = _hrect.getHeight()/2;
	double cx = _hrect.getMinX() + a;
	double cy = _hrect.getMinY() + b;
	double s, t;

	if (a == 0 || b == 0)
            return null;
	else
	{
            s = (point.x - cx)/a;
            t = (point.y - cy)/b;
            if (s*s + t*t <= 1)
                return this;
            else
		return null;
	}
    }

    public boolean intersectsRect(Rectangle rect)
    {
	// check if oval intersects rectangle
	double a = _hrect.getWidth()/2;
	double b = _hrect.getHeight()/2;
	double cx = _hrect.getMinX() + a;
	double cy = _hrect.getMinY() + b;
	Rectangle ir = rect.intersection(_hrect);
	Point point = new Point();

	if (ir.isEmpty())   // doesn't even intersect rectangle
		return false;
	else   // does intersect rectangle, further checking needed
	{
            int ir_right = (int) ir.getMaxX();
            int ir_bottom = (int) ir.getMaxY();
            // need only check cases when ir is contained in one quadrant of hr
            if (ir_right < cx)   // ir is contained in left half of hr
                point.x = ir_right;
            else if (ir.x > cx)   // ir is contained in right half of hr
                point.x = ir.x;
            else   // ir is contained in at least two quadrants of hr
                return true;

            if (ir.y > cy)   // ir is contained in bottom half of hr
                point.y = ir.y;
            else if (ir_bottom < cy)   // ir is contained in top half of hr
                point.y = ir_bottom;
            else   // ir is contained in at least two quadrants of hr
                return true;

            // check if oval contains the corner of ir interior to hr
            return containsPoint(point) != null;
	}
    }

    protected Point intersectionFromPoint(Point point)
    {
	Point center = new Point((int) _rect.getCenterX(), (int) _rect.getCenterY());
	double a = _rect.getWidth()/2;
	double b = _rect.getHeight()/2;
	double m, s;
	Point ip = new Point();

	if (containsPoint(point) != null || a == 0 || b == 0)
            return center;

	// equation for oval: [(x - cx)/a]^2 + [(y - cy)/b]^2 = 1
	// equation for line: (y - cy)/(x - cx) = (py - cy)/(px - cx) = m
	if (point.x != center.x)
	{
		m = ((double)point.y - (double)center.y)/((double)point.x - (double)center.x);
		s = (double)Math.sqrt(1/(1/(a*a) + m*m/(b*b)));
		if (point.x > center.x)   // line intersects right half
			ip.x = (int) (center.x + s);
		else   // line intersects left half
			ip.x = (int) (center.x - s);
		s = (double)Math.sqrt(1/(1/(m*m*a*a) + 1/(b*b)));
		if (point.y > center.y)   // line intersects top half
			ip.y = (int) (center.y + s);
		else   // line intersects bottom half
			ip.y = (int) (center.y - s);
	}
	else
	{
		ip.x = center.x;
		if (point.y > center.y)   // line intersects top half
			ip.y = (int) (center.y + b);
		else   // line intersect bottom half
		ip.y = (int) (center.y - b);
	}

     return ip;
    }

    protected void checkError( SB_Polymorphism poly, SB_ErrorInfo errorInfo)
    {
      SB_Output build = SB_OutputBar._build;


      if (poly.getConnectors().isUnreachable(this))
      {
         errorInfo._nw++;
         String expr = getExpr();
         if (expr.length() == 0) expr = "None";
         build.addLine(new SB_Line("WARNING: '" + expr + "' condition is unreachable.",
               Color.red, poly, this, null, SB_Line.WARNING));
      }

      int size =  getBindingCount();
      for (int i = 0; i < size; ++i)
      {
         SB_Binding binding = getBinding(i);
         binding.checkError(poly, this, errorInfo);
      }

      // parse expression here
      SB_Catalog catalog = ((SB_ProjectBar) ComponentRegistry.getProjectBar())._catalog;
      String expr = catalog.constantReplace(getExpr());
      // check syntax for the expression
      try {
         SB_JavaScriptEngine.compile( expr);
      } catch (Exception ex) {
         String errMsg = ex.getMessage();
         errorInfo._ne++;
         if (ex instanceof ScriptException) {
            errMsg = SB_JavaScriptEngine.getMessage((ScriptException)ex, false);
         } 

         build.addLine(new SB_Line("ERROR: [" + expr + "] " +errMsg, Color.red, poly, this, null, SB_Line.ERROR));
         return;
      } 
    }

}
