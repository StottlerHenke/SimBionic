
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
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
    
    protected static final int MAX_TRUNCATED_LINE_LENGTH = 20; //Maximum # characters in a single line
    
    protected static final int CONDITION_PADDING = 4;
    
    protected static final String endline = "\n";


    protected Rectangle _rect = new Rectangle(0, 0, 100, 75);

    /**
     * XXX: This rectangle is slightly larger and off to the side; this appears
     * to be the highlight rectangle?
     * */
    protected Rectangle _hrect = new Rectangle(_rect.x - 3, _rect.y - 3, _rect.width + 7, _rect.height + 7);

    /**
     * 2017-11
     * <br>
     * Assuming that the vector is only modified through methods of SB_Element
     * (a dangerous assumption as long as this field is public), null elements
     * will not be added (all current callers attempt to add non-null
     * SB_Element instances) However,
     * {@link SB_Polymorphism#updateWithDataModel()} will attempt
     * to modify this Vector in ways that insert nulls immediately before
     * calling methods that call {@link #updateTwoWay(SB_Element, boolean)
     * updateTwoWay}.
     * <br>
     * In general, it appears that this will not contain nulls after
     * "initialization" (Modification in SB_Polymorphism, the only direct
     * modification of _connectors outside of SB_Element and its subclasses),
     * but methods accessing this may be called during this step, so methods
     * accessing _connectors need to handle nulls anyways.
     * */
    Vector<SB_Connector> _connectors = new Vector<>();
    protected String _label = null;
    protected String _bindingsString = null;
    protected int _labelOffsetX = 0;
    protected int _bindingsOffsetX = 0;
    protected boolean _needToResize = true;
    protected int _runningState = NOT_RUNNING;
    public boolean _isBreakpoint = false;
    public boolean _breakpointEnabled = true;

    private Node _dataModel;

    private List<SB_Binding> _bindings;

    public SB_Element() {
       _bindings = new Vector<>();
    }

    public SB_Element(Node dataModel)
    {
        _dataModel = dataModel;
        _bindings = new Vector<>();
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


    /**
     * Setup the label
     */
    public void setupLabel() {
    	//Get the label string (expression)
    	int length = getExpr().length();
    	if (getLabelMode() == TRUNCATED_LABEL && length > MAX_TRUNCATED_LINE_LENGTH)
    		_label = getExpr().substring(0, MAX_TRUNCATED_LINE_LENGTH - 3) + "...";
    	else
    		if (getLabelMode() == COMMENT_LABEL)
    		{
    			if (getComment() != null && getComment().length() > 0) {
    				_label = new String(getComment());
    			}
    			else
    				_label = "(no user comment)";
    		}
    		else
    		{
    			if (length > 0) {
    				_label = new String(getExpr());
    			}
    			else
    				_label = "";
    		}
    }
    
    /**
     * @return an array of strings from a label, where each item in the array represents a single line on screen.
     */
    protected static String[] splitMultiLineLabel(String text) {
    	
    	text = text.replace("&&\n", "&&"); //Remove newline in case the user manually added them
    	text = text.replace("||\n", "||");
    	text = text.replace("&& \n", "&&"); //Remove space+newline in case the user manually added them
    	text = text.replace("|| \n", "||");
    	text = text.replace("&&", "&&\n"); //Add newline after each && and ||
    	text = text.replace("||", "||\n");
    	
    	String[] strings = text.split(endline);
    	
    	return strings;
    }
    
    /**
     * @return the width of the widest label
     */
    protected int getMultilineLabelWidth(Graphics2D g2, String text) {

    	int width = 0;

    	String[] strings = splitMultiLineLabel(text);
    	for(String s : strings) {
    		int temp = g2.getFontMetrics().stringWidth(s);
    		if(temp > width)
    			width = temp;
    	}
    	
    	return width;
    }
    
    /**
     * @return the combined height of all label
     */
    protected int getMultilineLabelHeight(Graphics2D g2, String text) {

    	String[] strings = splitMultiLineLabel(text);
    	
    	int height = g2.getFontMetrics().getHeight();
    	
    	int followingLinesHeight = g2.getFontMetrics().getAscent() + 2;
    	if(strings.length > 1)
    		height += followingLinesHeight * (strings.length - 1);
   
    	return height;
    }
    
    /**
     * Breakup the given string and draw each line of text. Drawing will start at the x,y given.
     */
    public void drawMultiLineLabel(Graphics2D g2, String text, double x, double y) {
    	
    	String[] strings = splitMultiLineLabel(text);
    	
    	float drawPosX = (float) x;
    	float drawPosY = (float) y;
    	float followingLinesHeight = g2.getFontMetrics().getAscent() + 2;
    	
    	for(String s : strings) {
    		g2.drawString(s, drawPosX, drawPosY);
    		drawPosY += followingLinesHeight;
    	}
    }

    /**
     * XXX: This method body does not actually draw anything itself, but
     * instead does size bookkeeping common to all child classes.
     * */
    @Override
	public void draw(Graphics2D g2)
    {
        if (_needToResize)
        {
            int center_x = (int) _rect.getCenterX();
            int center_y = (int) _rect.getCenterY();

            setupLabel();

            setRect(regenRectAndCalculateOffsets(g2));
            setCenter(center_x, center_y);

            _needToResize = false;
        }

        if (isHighlighted()) highlight(g2);
        drawAfterRectManagement(g2);
    }

    /**
     * 2018-05-30 jmm
     * <br>
     * XXX: This method is an attempt to formalize the fact that all subclasses
     * depend on the setup done by {@link #draw(Graphics2D)} at the start of
     * their overriden {@code draw} methods, hence subclasses shouuld not
     * override {@code draw} directly.
     * */
    protected abstract void drawAfterRectManagement(Graphics2D g2);

    /**
     * Calculates the size of the minimum bounding rectangle for rendering
     * {@link #_label} in {@code g2}.
     * */
    Dimension calcLabelSize(Graphics2D g2) {
        int labelWidth = getMultilineLabelWidth(g2, _label);
        int labelHeight = getMultilineLabelHeight(g2, _label);
        return new Dimension(labelWidth, labelHeight);
    }

    /**
     * Calculates the size of the bounding rectangle that should be used by
     * this SB_Element given a minimum bounding rectangle size for
     * {@link #_label} given by {@code labelSize}. Subclasses of
     * {@code SB_Element} may override this to influence rendering.
     * */
    protected Dimension rectBoundsFromLabel(Dimension labelSize) {
        int width = Math.max(labelSize.width, 12) + SB_Drawable.border_x;
        return new Dimension(width, labelSize.height);
    }

    /**
     * An attempt to factor out the calculation of the bounds rectangle used in
     * calculating the size that should be used for this element.
     * */
    Rectangle regenRectAndCalculateOffsets(Graphics2D g2) {

        //Get the binding string
        if (getLabelMode() != COMMENT_LABEL && _bindingsString != null)
        {
            return genRectWithBorderMargins(
                    calcOffsetAndBoundsWithBindings(g2));
        }
        else {
            return genRectWithBorderMargins(
                    calcOffsetAndBoundsWithoutBindings(g2));
        }

    }

    /**
     * Determines the necessary bounds of this rectangle and calculates offsets
     * necessary to display both bindings and the contained expression.
     * XXX: This method may destructively modify {@code newBounds}
     * @return newBounds after necessary modifications.
     * */
    Dimension calcOffsetAndBoundsWithBindings(Graphics2D g2) {
        Dimension labelBounds = calcLabelSize(g2);
        Dimension newBounds = rectBoundsFromLabel(labelBounds);
  
        int bindingWidth = this.getMultilineLabelWidth(g2, _bindingsString);
        int bindingHeight = this.getMultilineLabelHeight(g2, _bindingsString);

        if (bindingWidth > newBounds.width) {
            _labelOffsetX = (bindingWidth - labelBounds.width) / 2;
            _bindingsOffsetX = 0;
            newBounds.width = bindingWidth;
        } else {
            _labelOffsetX = (newBounds.width - labelBounds.width) / 2;
            _bindingsOffsetX = (newBounds.width - bindingWidth) / 2;
        }

        int newHeight = (_label.length() > 0)
                ? newBounds.height + bindingHeight
                : bindingHeight;

        newBounds.height = newHeight;
        return newBounds;
    }

    /**
     * Given the bounds of the label to be displayed in this element,
     * calculates the new bounds (used in {@link #setRect(Rectangle) setRect})
     * and label offset needed to display this element. Subclasses of
     * {@code SB_Element} may override this to influence rendering.
     * <br>
     * This method sets {@link #_labelOffsetX} as a side effect.
     * @return newBounds after necessary modifications.
     * */
    protected Dimension calcOffsetAndBoundsWithoutBindings(Graphics2D g2) {
        Dimension labelBounds = calcLabelSize(g2);
        Dimension newBounds = rectBoundsFromLabel(labelBounds);
        _labelOffsetX = (newBounds.width - labelBounds.width)/2;
        return newBounds;
    }

    private static Rectangle genRectWithBorderMargins(Dimension rect) {
        return new Rectangle(rect.width + 2*border_x - 1,
                rect.height + 2*border_y);
    }

    @Override
	public void offset(int delta_x, int delta_y, boolean highlightOnly)
    {
        if (!highlightOnly || isHighlighted())
        {
            _rect.translate(delta_x, delta_y);
            _hrect.translate(delta_x, delta_y);

            updateCenterXY();
            updateWidhtHeigth();
        }
    }

    private void updateCenterXY() {
       getDataModel().setCx((int)_rect.getCenterX());
       getDataModel().setCy((int)_rect.getCenterY());
    }
    
    private void updateWidhtHeigth() {
        getDataModel().setWidth((int)_rect.getWidth());
        getDataModel().setHeight((int)_rect.getHeight());
    }

    /**
     * Returns a string that uniquely identifies this node within its
     * containing SB_Polymorphism. Unfortunately, this string may change
     * whenever a node in the polymorphism is deleted, as deletion causes a
     * renumbering of nodes.
     * */
    protected String getPolyUniqueId() {
        return getDataModel().getClass().getSimpleName()
                + " " + getDataModel().getId();
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
    
    public Rectangle getHRect() {
    	return _hrect;
    }

    protected void setRect(Rectangle rect)
    {
        _rect.setBounds(rect);
        _hrect.setBounds(rect.x - 3, rect.y - 3, rect.width + 7, rect.height + 7);
        // update the data model
        updateCenterXY();
        updateWidhtHeigth();
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
    public List<SB_Binding> getBindings() {
       return _bindings;
    }
    @Override
    public void setBindings(List<SB_Binding> bindings) {
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
            if (_connectors.get(i) != null
                    && _connectors.get(i).isHighlighted()) {
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

    /**
     * As of 2017-11, no call provides a null value for parameter connector
     * (as <code>this</code> of an existing object is never null)
     * */
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

        for (SB_Connector connector : _connectors) {
            if (connector != null &&
                    connector.getEndElement() == element) {
                result = true;
                connector.setTwoWay(twoWay);
            } 
        }
        return result;
    }

    protected boolean hasConnectorTo(SB_Element element)
    {
      for (SB_Connector connector : _connectors) {
        if (connector != null
                && connector.getEndElement() == element)
          return true;
      }
      return false;
    }

    protected void prioritize() {
        int size = _connectors.size();
        for (int i = 0; i < size; ++i)
            _connectors.get(i).setPriority(i + 1);
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


    /**
     * Connectors are copied from an old version of _connectors, so no nulls
     * as long as _connectors does not include nulls
     * */
    @Override
	public void writeExternal(ObjectOutput out) throws IOException
    {
      Vector<SB_Connector> oldConnectors = _connectors;
      if (_highlightOnly)
      {
        SB_Connector connector;
        _connectors = new Vector<>();
        int size = oldConnectors.size();
        for (int i = 0; i < size; ++i)
        {
          connector = oldConnectors.get(i);
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

      _bindings = (List<SB_Binding>) in.readObject();  // bindings
      _rect = (Rectangle) in.readObject();
      _hrect = (Rectangle) in.readObject();
      _connectors = (Vector<SB_Connector>) in.readObject();
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

      return total;
    }
}
