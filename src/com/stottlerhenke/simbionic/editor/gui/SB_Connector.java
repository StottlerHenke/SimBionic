
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.QuadCurve2D;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Vector;
import java.util.regex.Pattern;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Connector;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Start;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;

/**
 * Represents a connector in the editor.
 */
public class SB_Connector extends SB_Drawable implements SB_BindingsHolder, SB_CommentHolder
{
    private static final long serialVersionUID = 2302585093L + 1008;

    protected SB_Element _startElement = null;
    private SB_Element _endElement = null;
    protected Point p3 = new Point();
    protected Point p4 = new Point();
    protected Point p5 = new Point();
    protected Point p6 = new Point();
    protected String _label = null;
    protected String _bindingsString = null;
    protected int _labelWidth = 0;
    protected boolean _needToUpdate = true;
    protected boolean _twoWay = false;
    
    private Connector _dataModel;
    private Start _startModel;
    private Vector _bindings;

    transient protected static int kLoopRadius = 15;
    transient protected static double kArrowAngle1 = Math.PI/6;
    transient protected static double kArrowAngle2 = kArrowAngle1/1.5;
    transient protected static double kArrowLen = 12;
    
    public SB_Connector() {}

    public SB_Connector(Connector dataModel, Start startModel)
    {
       _dataModel = dataModel;
       _startModel = startModel;
       _bindings = new Vector();	// bindings
       
       // bindings
       for (Binding bindingModel : getDataModel().getBindings()) {
          _bindings.add(new SB_Binding(bindingModel));
       }
       updateBindings();
       
       
    }
    
    public Start getStartModel() {
       return _startModel;
    }
    
    public Connector getDataModel() {
       return _dataModel;
    }
    
    public boolean isInterrupt() {
       return getDataModel().isInterrupt();
    }
    
    public void setInterrupt(boolean interrupt) {
       getDataModel().setInterrupt(interrupt);
    }
    

    public void draw(Graphics2D g2)
    {
        if (isInterrupt())
            g2.setStroke(SB_Drawable.dashed);
        else
            g2.setStroke(SB_Drawable.stroke);
        if (isLoop())
        {
          Rectangle rect = _startElement.getRect();
          g2.drawArc(getStartPoint().x - kLoopRadius, getEndPoint().y - kLoopRadius + 1,
                     2*kLoopRadius, 2*kLoopRadius, 90, 270);
        }
        else if (_twoWay)
        {
          g2.draw(new QuadCurve2D.Double(getStartPoint().x, getStartPoint().y, p6.x, p6.y, getEndPoint().x, getEndPoint().y));
        }
        else
          g2.drawLine(getStartPoint().x, getStartPoint().y, getEndPoint().x, getEndPoint().y);
        g2.drawLine(getEndPoint().x, getEndPoint().y, p3.x, p3.y);
        g2.drawLine(getEndPoint().x, getEndPoint().y, p4.x, p4.y);
        if (isInterrupt())
            g2.setStroke(SB_Drawable.stroke);

        if (_needToUpdate)
        {
          if (_label != null)
          {
            FontMetrics metrics = g2.getFontMetrics();
            _labelWidth = metrics.stringWidth(_label) - 2;
              if (_twoWay || isLoop())
                updatePoints();
              else
                updateArrowPoints();
          }

          _needToUpdate = false;
        }

        if (_label != null)
          g2.drawString(_label, p5.x, p5.y);

        if (isHighlighted()) highlight(g2);
    }

    public void highlight(Graphics2D g2)
    {
	if (_startElement != null)
            g2.setPaint(Color.green);
	else
            g2.setPaint(Color.yellow);
        Rectangle rect = getStartRect();
	g2.fillOval(rect.x, rect.y, rect.width + 1, rect.height + 1);
        g2.setPaint(Color.black);
        g2.drawOval(rect.x, rect.y, rect.width, rect.height);

	if (_endElement != null)
           g2.setPaint(Color.red);
	else
           g2.setPaint(Color.yellow);
        rect = getEndRect();
	g2.fillOval(rect.x, rect.y, rect.width + 1, rect.height + 1);
        g2.setPaint(Color.black);
        g2.drawOval(rect.x, rect.y, rect.width, rect.height);
    }

    public SB_Drawable containsPoint(Point p)
    {
	if (containsPoint_Label(p) != null)
            return this;
        if (isLoop())
        {
          double dist2 = p.distanceSq(getStartPoint().x, getEndPoint().y);
          if ((kLoopRadius - 3)*(kLoopRadius - 3) <= dist2
              && dist2 <= (kLoopRadius + 4)*(kLoopRadius + 4))
            return this;
        }
        else if (_twoWay)
        {
          if (containsPoint(p, getStartPoint(), p6) || containsPoint(p, p6, getEndPoint()))
            return this;
        }
        else if (containsPoint(p, getStartPoint(), getEndPoint()))
            return this;
        return null;
    }

    protected boolean containsPoint(Point p, Point pt1, Point pt2)
    {
	Point v = new Point(pt2.x - pt1.x, pt2.y - pt1.y);
	Point w = new Point(p.x - pt1.x, p.y - pt1.y);
	double dp_vw = (double)v.x*(double)w.x + (double)v.y*(double)w.y;	// dot product vw
	double dp_vv = (double)v.x*(double)v.x + (double)v.y*(double)v.y;	// distance v squared
	double dp_ww = (double)w.x*(double)w.x + (double)w.y*(double)w.y;	// distance w squared

	if (getStartRect().contains(p) || getEndRect().contains(p))
            return true;

	if (dp_vv == 0)   // no line to project onto
            return false;

	return dp_vw >= 0   // angle from -pi to pi
            && dp_vw*dp_vw/dp_vv <= dp_vv   // projected distance <= distance v
            && dp_ww - dp_vw*dp_vw/dp_vv <= 16.0;   // perpendicular distance <= 4.0
    }

    protected SB_Drawable containsPoint_Label(Point p)
    {
      if (_label != null)
      {
        Rectangle labelRect = new Rectangle(p5.x - 1, p5.y - 10, _labelWidth + 2, 11);
        if (labelRect.contains(p))
          return this;
      }
      return null;
    }

    public boolean intersectsRect(Rectangle rect)
    {
        if (isLoop())
        {
          Rectangle loopRect = new Rectangle(getStartPoint().x - kLoopRadius - 2, getEndPoint().y - kLoopRadius - 1,
                                             2*kLoopRadius + 4, 2*kLoopRadius + 4);
          return rect.intersects(loopRect);
        }
        else if (_twoWay)
        {
          return intersectsRect(rect, getStartPoint(), p6) || intersectsRect(rect, p6, getEndPoint());
        }
        else
          return intersectsRect(rect, getStartPoint(), getEndPoint());
    }

    protected boolean intersectsRect(Rectangle rect, Point pt1, Point pt2)
    {
	if (rect.contains(pt1))
            return true;

	double left = rect.getMinX();
	double right = rect.getMaxX();
	double bottom = rect.getMaxY();
	double top = rect.getMinY();
	double t, intersection;

	// equation for line: v = t(pt2 - pt1) + pt1
	if (pt2.x != pt1.x)
	{
            t = -1;
            if (pt1.x < left)
                // find intersection along left
                t = (left - (double)pt1.x)/((double)pt2.x - (double)pt1.x);
            else if (pt1.x > right)
                // find intersection along right
                t = (right - (double)pt1.x)/((double)pt2.x - (double)pt1.x);
            if (0 <= t && t <= 1)  // between pt1 and pt2
            {
                intersection = t*(pt2.y - pt1.y) + pt1.y;
                if (top <= intersection && intersection <= bottom)   // intersects side
                    return true;
            }
	}
	if (pt2.y != pt1.y)
	{
            t = -1;
            if (pt1.y > bottom)
                // find intersection along bottom
                t = (bottom - (double)pt1.y)/((double)pt2.y - (double)pt1.y);
            else if (pt1.y < top)
                // find intersection along top
                t = (top - (double)pt1.y)/((double)pt2.y - (double)pt1.y);
            if (0 <= t && t <= 1)   // between pt1 and pt2
            {
                intersection = t*((double)pt2.x - (float)pt1.x) + (double)pt1.x;
                if (left <= intersection && intersection <= right)   // intersects side
                    return true;
            }
	}
	return false;
    }

    public void offset(int delta_x, int delta_y, boolean highlightOnly)
    {
       if (!highlightOnly || isHighlighted())
       {
          if (_startElement == null) {
             Point startPoint = getStartPoint();
             startPoint.translate(delta_x, delta_y);
             setStartPoint(startPoint.x, startPoint.y);
          }
          if (_endElement == null) {
             Point endPoint = getEndPoint();
             endPoint.translate(delta_x, delta_y);
             setEndPoint(endPoint.x, endPoint.y);
          }
       }
    }

    public Rectangle unionRect(Rectangle ur, boolean highlightOnly)
    {
	if (!highlightOnly || isHighlighted())
	{
            if (_startElement == null)
            {
                if (ur.isEmpty())
                    ur.setBounds(getStartRect());
                else
                    ur = ur.union(getStartRect());
            }
            if (_endElement == null)
            {
                if (ur.isEmpty())
                    ur.setBounds(getEndRect());
                else
                    ur = ur.union(getEndRect());
            }
	}
	return ur;
    }

    protected SB_Element getStartElement() { return _startElement; }
    protected SB_Element getEndElement() { return _endElement; }

    protected void setStartElement(SB_Element element, Start startModel)
    {
       if (_startElement == element) return;
       
       SB_Element oldStartElement = _startElement;
       
       if (_startElement != null) {
          _startElement.removeConnector(this);
       }
       if (_startModel != null) {
          _startModel.removeConnector(getDataModel());
       }
       
       
       _startElement = element;
       _startModel = startModel;
       
       if (_startElement != null && startModel != null) {
          _startElement.addConnector(this);
          _startModel.addConnector(getDataModel());
       }
       else {
          setPriority(-1);
       }
       
       if (_twoWay)
       {
          if (!oldStartElement.hasConnectorTo(_endElement))
             _endElement.updateTwoWay(oldStartElement, false);
       }
       updateTwoWay();
    }
    

    protected void setEndElement(SB_Element element)
    {
       if (_endElement == element) return;
       
       SB_Element oldEndElement = _endElement;
       _endElement = element;
       if (_twoWay)
       {
          if (!_startElement.hasConnectorTo(oldEndElement))
             oldEndElement.updateTwoWay(_startElement, false);
       }
       updateTwoWay();
       updateDataModel();
       
    }
    
    /**
     * Set the end id and type in the data model.
     */
    protected void updateDataModel() {
       int endId = -1;
       if (_endElement != null) {
          endId = _endElement.getId();
        } 
       
        getDataModel().setEndId(endId);
        int endType = SB_Element.getType(_endElement);
        getDataModel().setEndType(endType);
    }

    protected boolean isTwoWay() { return _twoWay; }
    protected void setTwoWay(boolean twoWay) { _twoWay = twoWay; updatePoints(); }

    protected void updateTwoWay()
    {
      if (_endElement != null && _startElement != null && _endElement != _startElement)
        _twoWay = _endElement.updateTwoWay(_startElement, true);
      else
        _twoWay = false;
    }

    public Point getStartPoint() { 
       return new Point(getDataModel().getStartX(), getDataModel().getStartY()); 
    }
    
    public void setStartPoint(int x, int y) {
       getDataModel().setStartX(x);
       getDataModel().setStartY(y);
    }
    
    public Point getEndPoint() {
       return new Point(getDataModel().getEndX(), getDataModel().getEndY()); 
    }
    protected void setEndPoint (int x, int y) {
       getDataModel().setEndX(x);
       getDataModel().setEndY(y);
    }

    protected void offsetStartPoint(int delta_x, int delta_y)
    {
       Point startPoint = getStartPoint();
       startPoint.translate(delta_x, delta_y);
       setStartPoint(startPoint.x, startPoint.y);
	    updatePoints(null, _endElement);
    }

    protected void offsetEndPoint(int delta_x, int delta_y)
    {
       Point endPoint = getEndPoint();
       endPoint.translate(delta_x, delta_y);
       setEndPoint(endPoint.x, endPoint.y);
       updatePoints(_startElement, null);
    }

    protected Rectangle getStartRect()
    {
	return new Rectangle(getStartPoint().x - 4, getStartPoint().y - 4, 9, 9);
    }

    protected Rectangle getEndRect()
    {
	return new Rectangle(getEndPoint().x - 4, getEndPoint().y - 4, 9, 9);
    }

    protected void updatePoints()
    {
	updatePoints(_startElement, _endElement);
    }

    protected void updatePoints(SB_Element startElement, SB_Element endElement)
    {
        if (isLoop())
        {
          Rectangle rect = _startElement.getRect();
          int startPointX = rect.x;
          int startPointY = rect.y + rect.height - kLoopRadius;
          setStartPoint(startPointX, startPointY);
          int endPointX = rect.x + kLoopRadius + 1;
          int endPointY = rect.y + rect.height;
          setEndPoint(endPointX, endPointY);
          p3.x = getEndPoint().x - 8;
          p3.y = getEndPoint().y + 7;
          p4.x = getEndPoint().x;
          p4.y = getEndPoint().y + 11;
          p5.x = rect.x - kLoopRadius - 2 - _labelWidth;
          p5.y = rect.y + rect.height + kLoopRadius + 6;
        }
        else
        {
          if (startElement != null)
          {
            Rectangle rect = _startElement.getRect();
            int startPointX = (int) rect.getCenterX();
            int startPointY = (int) rect.getCenterY();
            setStartPoint(startPointX, startPointY);
          }
          if (endElement != null)
          {
            Rectangle rect = _endElement.getRect();
            setEndPoint((int) rect.getCenterX(), (int) rect.getCenterY());
          }
          if (startElement != null) {
            Point startPointLocation = _startElement.intersectionFromPoint(getEndPoint());
            setStartPoint(startPointLocation.x, startPointLocation.y);
          }
          
          if (endElement != null) {
            Point endPointLocation = _endElement.intersectionFromPoint(getStartPoint());
            setEndPoint(endPointLocation.x, endPointLocation.y); 
          }

          updateArrowPoints();

          if (_twoWay)
          {
            if (startElement != null) {
               Point startPointLocation = _startElement.intersectionFromPoint(p6);
               setStartPoint(startPointLocation.x, startPointLocation.y);
            }
            if (endElement != null) {
               Point endPointLocation = _endElement.intersectionFromPoint(p6);
               setEndPoint(endPointLocation.x, endPointLocation.y); 
            }

            updateCurveArrowPoints();
          }
        }
    }

    protected void updateArrowPoints()
    {
	double theta = Math.atan2(getEndPoint().y - getStartPoint().y, getEndPoint().x - getStartPoint().x);
        p3.x = (int) (getEndPoint().x + 12.0 * Math.cos(theta - Math.PI + Math.PI/8.0));
        p3.y = (int) (getEndPoint().y + 12.0 * Math.sin(theta - Math.PI + Math.PI/8.0));
        p4.x = (int) (getEndPoint().x + 12.0 * Math.cos(theta - Math.PI - Math.PI/8.0));
        p4.y = (int) (getEndPoint().y + 12.0 * Math.sin(theta - Math.PI - Math.PI/8.0));
        Point midpoint = new Point((getStartPoint().x + getEndPoint().x)/2, (getStartPoint().y + getEndPoint().y)/2);
	double c = Math.cos(theta - Math.PI + Math.PI/2.0);
	double s = Math.sin(theta - Math.PI + Math.PI/2.0);
        if (_twoWay)
        {
          p5.x = (int) (midpoint.x + 19.0 * c) - 2;
          p5.y = (int) (midpoint.y + 19.0 * s) + 5;
          p6.x = (int) (midpoint.x + 15.0 * c);
          p6.y = (int) (midpoint.y + 15.0 * s) + 1;
        }
        else
        {
          p5.x = (int) (midpoint.x + 10.0 * c) - 2;
          p5.y = (int) (midpoint.y + 10.0 * s) + 5;
        }
        c = Math.cos(theta);
        s = Math.sin(theta);
        if (theta < 0)
        {
          p5.x += (0.5 + theta/Math.PI) * _labelWidth * c - _labelWidth + 2;
          p5.y += (0.5 + theta/Math.PI) * _labelWidth * s;
        }
        else
        {
          p5.x -= (0.5 - theta/Math.PI) * _labelWidth * c;
          p5.y -= (0.5 - theta/Math.PI) * _labelWidth * s;
        }
    }

    protected void updateCurveArrowPoints()
    {
       double theta = Math.atan2(getEndPoint().y - getStartPoint().y, getEndPoint().x - getStartPoint().x);
       p3.x = getEndPoint().x + (int) (kArrowLen*Math.cos(Math.PI + theta + kArrowAngle1));
       p3.y = getEndPoint().y + (int) (kArrowLen*Math.sin(Math.PI + theta + kArrowAngle1));
       p4.x = getEndPoint().x + (int) (kArrowLen*Math.cos(Math.PI + theta - kArrowAngle2));
       p4.y = getEndPoint().y + (int) (kArrowLen*Math.sin(Math.PI + theta - kArrowAngle2));
    }

    public void removeHighlightDependencies()
    {
       if (_startElement != null && _startElement.isHighlighted())
       {
          _startElement = null;
          setPriority(-1);
       }
       if (_endElement != null && _endElement.isHighlighted()) {
          _endElement = null;
          updateDataModel();
       }
    }

    protected int getPriority() { 
       return getDataModel().getPriority(); 
    }
    public void setPriority(int priority)
    {
      if (getPriority() == priority) return;
      
      getDataModel().setPriority(priority);
      updateLabel();
    }

    public Vector getBindings() { 
       return _bindings; 
    }
    public void setBindings(Vector bindings) { 
       _bindings = bindings; 
       getDataModel().clearBindings();
       for (int i = 0; i < _bindings.size(); i++) {
          SB_Binding sbBinding = (SB_Binding)_bindings.get(i);
          getDataModel().addBinding(sbBinding.getDataModel());
       }
       updateBindings(); 
    }
    public int getBindingCount() { 
       return getBindings().size(); 
    }

    public SB_Binding getBinding(int i) { 
       return (SB_Binding) getBindings().get(i); 
    }
    
    public void addBinding(SB_Binding binding) {
       getBindings().add(binding); 
       getDataModel().addBinding(binding.getDataModel());
       updateBindings(); 
    }
    public void addBinding(int i, SB_Binding binding) { 
       getBindings().add(i, binding); 
       getDataModel().addBinding(i, binding.getDataModel());
       updateBindings(); 
    }
    public void removeBinding(int i) { 
       getBindings().remove(i); 
       getDataModel().removeBinding(i);
       updateBindings(); 
    }
    
    public void updateBindings() { 
       updateLabel(); 
    }
    
    /**
     * 
     * @return true is this is the only connector out of the start node; false otherwise
     */
    protected boolean isOnlyConnector() {
    	if(getPriority() > 1)
    		return false;
    	
    	if(_startModel != null && _startModel.getConnectors().size() > 1)
    		return false;
    	
    	return true;
    }
    
    public void updateLabel()
    {
      _bindingsString = null;
      int size = getBindingCount();
      if (size > 0)
      {
        _bindingsString = "[";
        for (int i = 0; i < size; ++i)
        {
          if (i < size - 1)
            _bindingsString += getBinding(i).getVar() + ",";
          else
            _bindingsString += getBinding(i).getVar() + "]";
        }
      }

      if (getPriority() != -1) {
    	  if(isOnlyConnector())
    		  _label = null;
    	  else
    		  _label = new Integer(getPriority()).toString();
      }
      else
        _label = null;
      
      if (getLabelMode() != COMMENT_LABEL)
      {
        if (_bindingsString != null)
        {
          if (_label != null)
            _label += "  " + _bindingsString;
          else
            _label = _bindingsString;
          _bindingsString = null;  // only needed for comment label mode
        }
      }
      else
      {
        if (_label != null)
          _label += "  ";
        else
          _label = "";
        if (getComment()!=null && getComment().length() > 0)
          _label += getComment();
        else
          _label += "(no user comment)";
      }
      if (getLabelMode() == TRUNCATED_LABEL && _label != null && _label.length() > 20)
        _label = _label.substring(0, 16) + "...]";
      _needToUpdate = true;
    }
    public String getComment() { 
       return getDataModel().getComment(); 
    }
    public void setComment(String comment) { 
       getDataModel().setComment(comment); 
    }
    public void updateComment() { updateLabel(); }

    public int getLabelMode() { 
       return getDataModel().getLabelMode(); 
    }
    public void setLabelMode(int labelMode) { 
       getDataModel().setLabelMode(labelMode); 
    }

    public int getId() { 
       return getDataModel().getId();
    }
    public void setId(int id) { 
       getDataModel().setId(id); 
    }

    protected boolean isLoop()
    {
      return _startElement != null && _startElement == _endElement
          && _startElement instanceof SB_Rectangle
          && SB_Canvas.getDragConnector() != this;
    }
/*
    private void writeObject(ObjectOutputStream s) throws IOException
    {
      if (!_highlightOnly)
        s.defaultWriteObject();
      else
      {
        SB_Element oldStartElement = _startElement;
        SB_Element oldEndElement = _endElement;
        int oldPriority = _priority;
        if (_startElement != null && !_startElement.isHighlighted())
          _startElement = null;
        if (_endElement != null && !_endElement.isHighlighted())
          _endElement = null;
        if (_startElement == null)
          setPriority(-1);
        s.defaultWriteObject();
        _startElement = oldStartElement;
        _endElement = oldEndElement;
        setPriority(oldPriority);
      }
    }
*/
    protected void checkError(SB_Polymorphism poly, SB_ErrorInfo errorInfo)
    {
      SB_Output build = SB_OutputBar._build;

        // end node/condition
        if (_startElement == null)
        {
          // report error
          errorInfo._ne++;
          build.addLine(new SB_Line("ERROR: Connector has a disconnected start point.", Color.red,
                                    poly, this, null, SB_Line.ERROR));
        }
        if (_endElement == null)
        {
          // report error
          errorInfo._ne++;
          build.addLine(new SB_Line("ERROR: Connector has a disconnected end point.", Color.red,
                                    poly, this, null, SB_Line.ERROR));
        }

        int size =  getBindingCount();
        for (int i = 0; i < size; ++i)
        {
          SB_Binding binding = getBinding(i);
          binding.checkError(poly, this, errorInfo);
        }
     
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      SB_Element oldStartElement = _startElement;
      SB_Element oldEndElement = _endElement;
      int oldPriority = getPriority();
      if (_highlightOnly)
      {
        if (_startElement != null && !_startElement.isHighlighted())
          _startElement = null;
        if (_endElement != null && !_endElement.isHighlighted())
          _endElement = null;
        if (_startElement == null)
          setPriority(-1);
      }

      super.writeExternal(out);

      out.writeObject(_bindings);  // bindings
      out.writeObject(_startElement);
      out.writeObject(_endElement);
      out.writeObject(_dataModel);
      out.writeObject(_startModel);
      out.writeObject(p3);
      out.writeObject(p4);
      out.writeObject(p5);
      out.writeObject(p6);
      out.writeObject(_bindingsString);
      out.writeObject(_label);
      out.writeInt(_labelWidth);
      out.writeBoolean(_needToUpdate);
      out.writeBoolean(_twoWay);

      if (_highlightOnly)
      {
        _startElement = oldStartElement;
        _endElement = oldEndElement;
        setPriority(oldPriority);
      }
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);

      _bindings = (Vector) in.readObject();  // bindings
      _startElement = (SB_Element) in.readObject();
      _endElement = (SB_Element) in.readObject();
      _dataModel = (Connector)in.readObject();
      _startModel = (Start)in.readObject();
      p3 = (Point) in.readObject();
      p4 = (Point) in.readObject();
      p5 = (Point) in.readObject();
      p6 = (Point) in.readObject();
      _bindingsString = (String) in.readObject();
      _label = (String) in.readObject();
      _labelWidth = in.readInt();
      _needToUpdate = in.readBoolean();
      _twoWay = in.readBoolean();
    }


    protected int findOccurrences(Pattern pattern, String strReplace, SB_Polymorphism poly) throws SB_CancelException
    {
      int total = 0;
      int size = getBindingCount();
      for (int i = 0; i < size; ++i)
        total += getBinding(i).findOccurrences(pattern, strReplace, poly, this);
      return total;
    }
}
