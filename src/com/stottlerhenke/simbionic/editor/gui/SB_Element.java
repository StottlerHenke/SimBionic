
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Node;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * base class for simbionic canvas element.
 *
 */
abstract public class SB_Element extends SB_Drawable implements SB_BindingsHolder, SB_CommentHolder
{
    private static final long serialVersionUID = 2302585093L + 1005;

    protected static final Color _hcolor = new Color(0x99, 0x99, 0xFF);		// highlight
    protected static final Color _rcolor = Color.yellow;					// rectangle running
    protected static final Color _rccolor = Color.cyan;						// condition checked
    protected static final Color _rfcolor = new Color(0, 232, 0);			// condition followed
    protected static final Color _breakcolor = new Color(140, 10, 10);


    protected static final int NOT_RUNNING = 0;
    protected static final int RUNNING = 1;				// rectangle running
    protected static final int RUNNING_CHECKED = 2;		// condition checked
    protected static final int RUNNING_FOLLOWED = 3;	// condition followed

    protected static final String endline = "\r\n";


    protected Rectangle _rect = new Rectangle(0, 0, 100, 75);
    protected Rectangle _hrect = new Rectangle(_rect.x - 3, _rect.y - 3, _rect.width + 7, _rect.height + 7);
    public Vector _connectors = new Vector();
    protected String _label = null;
    protected String _bindingsString = null;
    protected int _labelOffsetX = 0;
    protected int _bindingsOffsetX = 0;
    protected boolean _needToResize = true;
    protected int _runningState = NOT_RUNNING;
    public boolean _isBreakpoint = false;
    public boolean _breakpointEnabled = true;

    private Node _dataModel;

    private Vector _bindings;

    public SB_Element() {
       _bindings = new Vector();
    }

    public SB_Element(Node dataModel)
    {
        _dataModel = dataModel;
        _bindings = new Vector();
        updateWithDataModel();
    }

    /**
     * Get the type of the specified element.
     * @param element SB_Element to get its type.
     * @return The type of the specified element.
     */
    public static int getType(SB_Element element) {
       if (element == null) {
    	   // null element
    	   return -1;
       }
       if (element instanceof SB_Rectangle) {
    	   // rectangle is for action and compound action
          return 0;
       }
       // 1 is for condition.
       return 1;
    }

    private void updateWithDataModel() {
       int cx = getDataModel().getCx();
       int cy = getDataModel().getCy();
       if (cx != Node.INVALID_CX && cy != Node.INVALID_CY) {
          setCenter(cx, cy);
       }

       // bindings
       for (Binding bindingModel : getDataModel().getBindings()) {
          _bindings.add(new SB_Binding(bindingModel));
       }
       updateBindings();
    }

    public Node getDataModel() {
       return _dataModel;
    }



    @Override
	public void draw(Graphics2D g2)
    {
        if (_needToResize)
        {
            int center_x = (int) _rect.getCenterX();
            int center_y = (int) _rect.getCenterY();

            //Get the label string (expression)
            int length = getExpr().length();
            if (getLabelMode() == TRUNCATED_LABEL && length > 20)
              _label = getExpr().substring(0, 17) + "...";
            else
            if (getLabelMode() == COMMENT_LABEL)
            {
              if (getComment() != null && getComment().length() > 0) {
            	//user comments might have new lines characters and be very long
            	//show only the first 70 characters of the first line in the comment
                _label = new String(getComment());
                String[] lines = _label.split("\n");
                _label = lines[0];
                if (_label.length() > 70) {
                	_label = _label.substring(0,70) + "...";
                }
              }
              else
                _label = "(no user comment)";
            }
            else
            {
              if (length > 0)
                _label = new String(getExpr());
              else
              	_label = "";
            }

            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(_label);
            int width = Math.max(labelWidth, 24);
            if (this instanceof SB_Condition)
            	width = Math.max(width, 26);
            int height = metrics.getHeight();

            //Get the binding string
            if (getLabelMode() != COMMENT_LABEL && _bindingsString != null)
            {
            	Dimension bd = getStringDimensions(metrics, _bindingsString);

            	if(bd.width > width)
            	{
            		_labelOffsetX = (bd.width - labelWidth)/2;
            		_bindingsOffsetX = 0;
            		width = bd.width;
            	}
            	else
            	{
            		_labelOffsetX = (width - labelWidth)/2;
            		_bindingsOffsetX = (width - bd.width)/2;
            	}

            	if (_label.length() > 0)
            	{
            		height += bd.height;
            		if (this instanceof SB_Condition)
            			height += 4;
            	}
            	else
            		height = bd.height;
           }
            else
            {
               //_labelOffsetX = 0;
               _labelOffsetX = (width - labelWidth)/2;
               if (this instanceof SB_Rectangle && width > 40)
               {
                 width -= 4;
                 _labelOffsetX = -2;
                 if (!((SB_Rectangle) this).isBehavior())
                   --width;
               }
            }
            //if (this instanceof SB_Rectangle && !((SB_Rectangle) this)._complex)
            //  --width;

            setRect(new Rectangle(width + 2*border_x - 1, height + 2*border_y));
            setCenter(center_x, center_y);

            _needToResize = false;
        }

        if (isHighlighted()) highlight(g2);
    }

    @Override
	public void offset(int delta_x, int delta_y, boolean highlightOnly)
    {
        if (!highlightOnly || isHighlighted())
        {
            _rect.translate(delta_x, delta_y);
            _hrect.translate(delta_x, delta_y);

            updateCenterXY();
        }
    }

    private void updateCenterXY() {
       getDataModel().setCx((int)_rect.getCenterX());
       getDataModel().setCy((int)_rect.getCenterY());

    }
    @Override
	public String toString(){
        if (this._bindingsString==null) return "<"+this._label+">";
        return "<"+this._label+" / "+this._bindingsString+">";
    }

    @Override
	public Rectangle unionRect(Rectangle ur, boolean highlightOnly)
    {
	if (!highlightOnly || isHighlighted())
        {
            if (ur.isEmpty())
                ur.setBounds(_rect);
            else
                ur = ur.union(_rect);
        }
	return ur;
    }

    protected abstract Point intersectionFromPoint(Point point);

    public Rectangle getRect()
    {
        return _rect;
    }

    protected void setRect(Rectangle rect)
    {
        _rect.setBounds(rect);
        _hrect.setBounds(rect.x - 3, rect.y - 3, rect.width + 7, rect.height + 7);
        // update the data model
        updateCenterXY();
    }

    protected Rectangle getHighlightRect()
    {
	return _hrect;
    }

    public void setCenter(int point_x, int point_y)
    {
       int center_x = (int) _rect.getCenterX();
       int center_y = (int) _rect.getCenterY();

       offset(point_x - center_x, point_y - center_y, false);
    }

    @Override
	public Vector getBindings() {
       return _bindings;
    }
    @Override
	public void setBindings(Vector bindings) {
       _bindings = bindings;
       getDataModel().clearBindings();
       for (int i = 0; i < _bindings.size(); i++) {
          SB_Binding sbBinding = (SB_Binding)_bindings.get(i);
          getDataModel().addBinding(sbBinding.getDataModel());
       }
       updateBindings();
    }
    @Override
	public int getBindingCount() {
       return getBindings().size();
    }
    @Override
	public SB_Binding getBinding(int i) {
       return (SB_Binding) getBindings().get(i);
    }

    @Override
	public void addBinding(SB_Binding binding) {
       getBindings().add(binding);
       getDataModel().addBinding(binding.getDataModel());
       updateBindings();
    }
    @Override
	public void addBinding(int i, SB_Binding binding) {
       getBindings().add(i, binding);
       getDataModel().addBinding(i, binding.getDataModel());
       updateBindings();
    }
    @Override
	public void removeBinding(int i) {
       getBindings().remove(i);
       getDataModel().removeBinding(i);
       updateBindings();
    }
    @Override
	public void updateBindings()
    {
      int size = getBindingCount();
      if (size == 0)
        _bindingsString = null;
      else
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
      if (getLabelMode() == TRUNCATED_LABEL && _bindingsString != null && _bindingsString.length() > 20)
          _bindingsString = _bindingsString.substring(0, 16) + "...]";
      _needToResize = true;
    }

    @Override
	public String getComment() {
       return getDataModel().getComment();
    }
    @Override
	public void setComment(String comment) {
       getDataModel().setComment(comment);
    }
    @Override
	public void updateComment() { updateBindings(); }

    @Override
	public int getLabelMode() {
       return getDataModel().getLabelMode();
    }
    @Override
	public void setLabelMode(int labelMode) {
       getDataModel().setLabelMode(labelMode);
    }

    @Override
	public void removeHighlightDependencies()
    {
       int n = 0;
       int size = _connectors.size();
       for (int i = 0; i < size - n; ++i)
       {
          if (_connectors.get(i) != null && ((SB_Connector)(_connectors.get(i))).isHighlighted())
          {
             ++n;
             _connectors.removeElementAt(i);
             --i;
          }
       }
       prioritize();
    }

    protected int numPriorities()
    {
        return _connectors.size();
    }

    protected boolean addConnector(SB_Connector connector)
    {
       int size = _connectors.size();
       for (int i = 0; i < size; ++i)
          if (_connectors.get(i) == connector) return false;
       _connectors.add(connector);
       prioritize();
       return true;
    }

    protected boolean removeConnector(SB_Connector connector)
    {
       int size = _connectors.size();
       for (int i = 0; i < size; ++i)
       {
          if (_connectors.get(i) == connector)
          {
             _connectors.removeElementAt(i);
             prioritize();
             return true;
          }
       }
       return false;
    }

    protected boolean updateTwoWay(SB_Element element, boolean twoWay)
    {
      boolean result = false;
      SB_Connector connector;
      int size = _connectors.size();
      for (int i = 0; i < size; ++i)
      {
        connector = (SB_Connector) _connectors.get(i);
        if (connector.getEndElement() == element)
        {
          result = true;
          connector.setTwoWay(twoWay);
        }
      }
      return result;
    }

    protected boolean hasConnectorTo(SB_Element element)
    {
      SB_Connector connector;
      int size = _connectors.size();
      for (int i = 0; i < size; ++i)
      {
        connector = (SB_Connector) _connectors.get(i);
        if (connector.getEndElement() == element)
          return true;
      }
      return false;
    }

    protected void prioritize()
    {
	int size = _connectors.size();
	for (int i = 0; i < size; ++i)
            ((SB_Connector)(_connectors.get(i))).setPriority(i + 1);
    }

    protected void reprioritize(SB_Connector connector, int priority)
    {
	removeConnector(connector);
        _connectors.insertElementAt(connector, priority - 1);
        prioritize();
    }

    @Override
	public int getId() {
       return getDataModel().getId();
    }
    public void setId(int id) {
       getDataModel().setId(id);
    }

    public abstract String getExpr();
    public abstract void setExpr(String expr);
/*
    private void writeObject(ObjectOutputStream s) throws IOException
    {
      if (!_highlightOnly)
        s.defaultWriteObject();
      else
      {
        SB_Connector connector;
        Vector oldConnectors = _connectors;
        _connectors = new Vector();
        int size = oldConnectors.size();
        for (int i = 0; i < size; ++i)
        {
          connector = (SB_Connector) oldConnectors.get(i);
          if (connector.isHighlighted())
            _connectors.add(connector);
        }
        prioritize();
        s.defaultWriteObject();
        _connectors = oldConnectors;
        prioritize();
      }
    }
*/


    @Override
	public void writeExternal(ObjectOutput out) throws IOException
    {
      Vector oldConnectors = _connectors;
      if (_highlightOnly)
      {
        SB_Connector connector;
        _connectors = new Vector();
        int size = oldConnectors.size();
        for (int i = 0; i < size; ++i)
        {
          connector = (SB_Connector) oldConnectors.get(i);
          if (connector.isHighlighted())
            _connectors.add(connector);
        }
        prioritize();
      }

      super.writeExternal(out);

      out.writeObject(_bindings);  // bindings
      out.writeObject(_rect);
      out.writeObject(_hrect);
      out.writeObject(_connectors);
      out.writeObject(_dataModel);
      out.writeObject(_label);
      out.writeObject(_bindingsString);
      out.writeInt(_labelOffsetX);
      out.writeInt(_bindingsOffsetX);
      out.writeBoolean(_needToResize);

      if (_highlightOnly)
      {
        _connectors = oldConnectors;
        prioritize();
      }
    }

    @Override
	public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);

      _bindings = (Vector) in.readObject();  // bindings
      _rect = (Rectangle) in.readObject();
      _hrect = (Rectangle) in.readObject();
      _connectors = (Vector) in.readObject();
      _dataModel = (Node) in.readObject();
      _label = (String) in.readObject();
      _bindingsString = (String) in.readObject();
      _labelOffsetX = in.readInt();
      _bindingsOffsetX = in.readInt();
      _needToResize = in.readBoolean();
    }




    protected int findOccurrences(Pattern pattern, String strReplace, SB_Polymorphism poly) throws SB_CancelException
    {
      int total = 0;
      StringBuffer sb = null;
      StringBuffer sb_next = null;
      int delta = 0;
      SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
      SB_Catalog catalog = projectBar._catalog;
      String originalExpr = getExpr();
      setExpr(catalog.constantReplace(getExpr()));   // match against an expression without constants
      Matcher matcher = pattern.matcher(getExpr());
      while (matcher.find())
      {
        ++total;
        SimBionicEditor editor = poly._parent.getEditor();
        SB_OutputBar outputBar = SB_OutputBar.getInstance();
        SB_Output find = SB_OutputBar._find;
        find.addFindLine(poly, this, null, matcher.start() + delta, matcher.end() + delta);

        if (strReplace != null)
        {
          int sel = find._lines.size() - 1;
          find.setSel(sel);

          int n = JOptionPane.showConfirmDialog(projectBar, "Replace this occurrence?",
                                                "Replace", JOptionPane.YES_NO_CANCEL_OPTION);
          if (n == JOptionPane.YES_OPTION)
          {
            if (sb == null)
            {
              sb = new StringBuffer();
              sb_next = new StringBuffer();
            }
            matcher.appendReplacement(sb, strReplace);
            delta = sb.length() - matcher.end();
            sb_next.setLength(0);
            sb_next.append(sb);
            matcher.appendTail(sb_next);
            poly.addToUndoStack();
            setExpr(new String(sb_next));
            poly.setHighlighted(false);
            SB_TabbedCanvas tabbedCanvas = ComponentRegistry.getContent();
            tabbedCanvas.getActiveCanvas().clearSingle();
            find.setSel(sel);
            poly.setModified(true);
          }
          else if (n == JOptionPane.CANCEL_OPTION)
          {
            setExpr(originalExpr);
            throw new SB_CancelException();
          }
        }
      }
      int size = getBindingCount();
      for (int i = 0; i < size; ++i)
        total += getBinding(i).findOccurrences(pattern, strReplace, poly, this);

      setExpr(originalExpr); // restore original expression with constants
      return total;
    }

    /**
     *
     * @param str
     * @return [width in pixesl, height in pixels]
     */
    protected Dimension getStringDimensions(FontMetrics metrics, String str)
    {
    	String[] lines = str.split(endline);

    	int width = 0;
    	for( int x = 0; x < lines.length; x++)
    	{
    		width = Math.max(width, metrics.stringWidth(lines[x]));
    	}

    	return new Dimension(width, metrics.getHeight() * lines.length);
    }
}
